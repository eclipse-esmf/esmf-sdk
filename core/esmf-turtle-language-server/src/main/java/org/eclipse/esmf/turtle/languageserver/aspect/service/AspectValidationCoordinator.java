/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 *
 * See the AUTHORS file(s) distributed with this work for additional
 * information regarding authorship.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.eclipse.esmf.turtle.languageserver.aspect.service;

import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectViolationDiagnosticMapper;
import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspectValidationCoordinator implements AutoCloseable {
   private static final Logger LOG = LoggerFactory.getLogger( AspectValidationCoordinator.class );
   private static final long IDLE_VALIDATION_DELAY_SECONDS = 4L;

   private final AspectDocumentValidationService aspectDocumentValidationService;
   private final BiConsumer<Document, DiagnosticReport> onValidationComplete;
   private final ExecutorService executorService;
   private final ScheduledExecutorService scheduler;

   private final Map<Document, CompletableFuture<?>> runningValidations = new ConcurrentHashMap<>();
   private final Map<Document, ScheduledFuture<?>> scheduledValidations = new ConcurrentHashMap<>();
   private final Map<Document, AtomicLong> generations = new ConcurrentHashMap<>();
   private final Map<Document, DiagnosticReport> cache = new ConcurrentHashMap<>();

   public AspectValidationCoordinator(
         final AspectModelValidationService aspectModelValidationService,
         final BiConsumer<Document, DiagnosticReport> onValidationComplete ) {
      this( new AspectDocumentValidationService(
            new ParsedAspectModelFileLoader(),
            aspectModelValidationService,
            new AspectViolationDiagnosticMapper() ), onValidationComplete );
   }

   AspectValidationCoordinator(
         final AspectModelValidationService aspectModelValidationService,
         final ParsedAspectModelFileLoader aspectModelFileLoader,
         final AspectViolationDiagnosticMapper diagnosticMapper,
         final BiConsumer<Document, DiagnosticReport> onValidationComplete ) {
      this( new AspectDocumentValidationService( aspectModelFileLoader, aspectModelValidationService, diagnosticMapper ),
            onValidationComplete );
   }

   public AspectValidationCoordinator(
         final AspectDocumentValidationService aspectDocumentValidationService,
         final BiConsumer<Document, DiagnosticReport> onValidationComplete ) {
      this.aspectDocumentValidationService = aspectDocumentValidationService;
      this.onValidationComplete = onValidationComplete;
      this.executorService = Executors.newSingleThreadExecutor( Thread.ofPlatform().name( "aspect-validation-", 0 ).factory() );
      this.scheduler =
            Executors.newSingleThreadScheduledExecutor( Thread.ofPlatform().name( "aspect-validation-debounce-", 0 ).factory() );
   }

   public DiagnosticReport getCachedDiagnostics( final Document document ) {
      return cache.getOrDefault( document, DiagnosticReport.EMPTY );
   }

   public void seedCache( final Document document, final DiagnosticReport report ) {
      cache.put( document, report );
   }

   public void onDocumentOpened( final ParsedDocument parsedDocument ) {
      cancelRunningValidation( parsedDocument.sourceDocument() );
      submit( parsedDocument );
   }

   public void onDocumentChanged( final ParsedDocument parsedDocument ) {
      cancelRunningValidation( parsedDocument.sourceDocument() );
      rescheduleValidation( parsedDocument );
   }

   public void onDocumentSaved( final ParsedDocument parsedDocument ) {
      cancelRunningValidation( parsedDocument.sourceDocument() );
      rescheduleValidation( parsedDocument );
   }

   public void onDocumentClosed( final Document document ) {
      if ( document == null ) {
         return;
      }
      cancelScheduledValidation( document );
      cancelRunningValidation( document );
      cache.remove( document );
      generations.remove( document );
   }

   @Override
   public void close() {
      scheduledValidations.values().forEach( f -> f.cancel( false ) );
      scheduledValidations.clear();
      runningValidations.values().forEach( f -> f.cancel( true ) );
      runningValidations.clear();
      scheduler.shutdownNow();
      executorService.shutdownNow();
   }

   private void rescheduleValidation( final ParsedDocument parsedDocument ) {
      final Document document = parsedDocument.sourceDocument();
      cancelScheduledValidation( document );
      final ScheduledFuture<?> task = scheduler.schedule(
            () -> {
               scheduledValidations.remove( document );
               LOG.debug( "[debounce] idle timeout reached, submitting aspect validation for uri={}", document.getUri() );
               submit( parsedDocument );
            },
            IDLE_VALIDATION_DELAY_SECONDS,
            TimeUnit.SECONDS
      );
      scheduledValidations.put( document, task );
   }

   private void cancelScheduledValidation( final Document document ) {
      final ScheduledFuture<?> pending = scheduledValidations.remove( document );
      if ( pending != null ) {
         pending.cancel( false );
      }
   }

   private void cancelRunningValidation( final Document document ) {
      final CompletableFuture<?> previous = runningValidations.remove( document );
      if ( previous != null ) {
         LOG.debug( "[cancel] cancelling in-flight aspect validation for uri={}", document.getUri() );
         previous.cancel( true );
      }
   }

   private void submit( final ParsedDocument parsedDocument ) {
      final Document document = parsedDocument.sourceDocument();
      cancelRunningValidation( document );
      final long generation = generations.computeIfAbsent( document, ignored -> new AtomicLong() ).incrementAndGet();
      final CompletableFuture<DiagnosticReport> future = CompletableFuture.supplyAsync(
            () -> validate( parsedDocument ),
            executorService
      );
      runningValidations.put( document, future );
      future.whenComplete( ( result, throwable ) -> {
         runningValidations.remove( document, future );
         final Throwable failure = unwrap( throwable );
         if ( failure instanceof CancellationException || future.isCancelled() ) {
            LOG.debug( "[cancel] aspect validation cancelled for uri={}", document.getUri() );
            return;
         }
         final long currentGeneration = generations.getOrDefault( document, new AtomicLong() ).get();
         if ( generation != currentGeneration ) {
            LOG.debug( "[stale] ignoring stale result for uri={}, generation={}, current={}", document.getUri(), generation,
                  currentGeneration );
            return;
         }
         if ( failure != null ) {
            LOG.error( "[error] aspect validation failed for uri={}", document.getUri(), failure );
            final DiagnosticReport errorReport = aspectDocumentValidationService.processingFailureReport();
            cache.put( document, errorReport );
            onValidationComplete.accept( document, errorReport );
            return;
         }
         cache.put( document, result );
         onValidationComplete.accept( document, result );
      } );
   }

   private DiagnosticReport validate( final ParsedDocument parsedDocument ) {
      return aspectDocumentValidationService.validate( parsedDocument );
   }

   private Throwable unwrap( final Throwable throwable ) {
      if ( throwable instanceof CompletionException && throwable.getCause() != null ) {
         return throwable.getCause();
      }
      return throwable;
   }
}
