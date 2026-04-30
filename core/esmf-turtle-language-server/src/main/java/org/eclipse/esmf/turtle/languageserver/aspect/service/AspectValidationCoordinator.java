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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDiagnostic;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDiagnosticsService;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspectValidationCoordinator implements AutoCloseable {
   private static final Logger LOG = LoggerFactory.getLogger( AspectValidationCoordinator.class );
   private final TurtleDiagnosticsService validationService;
   private final ExecutorService executorService;
   private final Map<Document, CompletableFuture<?>> inFlight = new ConcurrentHashMap<>();
   private final Map<Document, AtomicLong> generations = new ConcurrentHashMap<>();

   public AspectValidationCoordinator( final TurtleDiagnosticsService validationService ) {
      this( validationService, Executors.newSingleThreadExecutor( Thread.ofPlatform().name( "aspect-validation-", 0 ).factory() ) );
   }

   AspectValidationCoordinator( final TurtleDiagnosticsService validationService, final ExecutorService executorService ) {
      this.validationService = validationService;
      this.executorService = executorService;
   }

   public long nextGeneration( final Document document ) {
      return generations.computeIfAbsent( document, ignored -> new AtomicLong() ).incrementAndGet();
   }

   public long currentGeneration( final Document document ) {
      final AtomicLong generation = generations.get( document );
      return generation != null ? generation.get() : 0L;
   }

   public void cancel( final Document document ) {
      final CompletableFuture<?> previous = inFlight.remove( document );
      if ( previous != null ) {
         LOG.debug( "[cancel] cancelling previous aspect validation for {}", document.getUri() );
         previous.cancel( true );
      }
   }

   public void submit( final Document document, final long generation, final BiConsumer<Long, DiagnosticReport> callback ) {
      cancel( document );
      final CompletableFuture<DiagnosticReport> future = CompletableFuture.supplyAsync(
            () -> validationService.check( document ),
            executorService
      );
      inFlight.put( document, future );
      future.whenComplete( ( result, throwable ) -> {
         inFlight.remove( document, future );
         if ( throwable instanceof CancellationException || future.isCancelled() ) {
            LOG.debug( "[cancel] aspect validation cancelled for {}", document.getUri() );
            return;
         }
         if ( throwable != null ) {
            LOG.error( "[publish diagnostics] aspect validation failed for {}", document.getUri(), throwable );
            callback.accept( generation, new DiagnosticReport( throwable.getMessage(), TurtleDiagnostic.TurtleCode.E0002 ) );
            return;
         }
         callback.accept( generation, result );
      } );
   }

   public DiagnosticReport validateSync( final Document document ) {
      return validationService.check( document );
   }

   @Override
   public void close() {
      inFlight.values().forEach( future -> future.cancel( true ) );
      executorService.shutdownNow();
   }
}
