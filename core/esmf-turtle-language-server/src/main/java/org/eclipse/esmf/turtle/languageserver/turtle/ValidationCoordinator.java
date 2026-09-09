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

package org.eclipse.esmf.turtle.languageserver.turtle;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import org.eclipse.esmf.aspectmodel.ViolationReport;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolationBuilder;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.ViolationProvider;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationCoordinator implements AutoCloseable {
   private static final Logger LOG = LoggerFactory.getLogger( ValidationCoordinator.class );
   private static final long IDLE_VALIDATION_DELAY_SECONDS = 4L;

   private final List<ViolationProvider> violationProviders;
   private final BiConsumer<Document, ViolationReport> onValidationComplete;
   private final ExecutorService executorService;
   private final ScheduledExecutorService scheduler;
   private final Object parserLock;
   private final TreeSitterTurtleParserService parserService;
   private final AtomicBoolean closed = new AtomicBoolean();

   private final Map<Document, CompletableFuture<?>> runningValidations = new ConcurrentHashMap<>();
   private final Map<Document, ScheduledFuture<?>> scheduledValidations = new ConcurrentHashMap<>();
   private final Map<Document, AtomicLong> generations = new ConcurrentHashMap<>();
   private final Map<Document, ViolationReport> fastValidationResults = new ConcurrentHashMap<>();

   public ValidationCoordinator(
         final List<ViolationProvider> violationProviders,
         final BiConsumer<Document, ViolationReport> onValidationComplete ) {
      this( violationProviders, onValidationComplete, null );
   }

   public ValidationCoordinator(
         final List<ViolationProvider> violationProviders,
         final BiConsumer<Document, ViolationReport> onValidationComplete,
         final TreeSitterTurtleParserService parserService ) {
      this.violationProviders = violationProviders;
      this.onValidationComplete = onValidationComplete;
      this.parserService = parserService;
      parserLock = parserService == null ? new Object() : parserService;
      executorService = Executors.newSingleThreadExecutor( Thread.ofPlatform().name( "semantic-models-validation-", 0 ).factory() );
      scheduler = Executors.newSingleThreadScheduledExecutor(
            Thread.ofPlatform().name( "semantic-models-validation-debounce-", 0 ).factory() );
   }

   private ViolationReport validateFast( final ParsedDocument parsedDocument ) {
      if ( parserService != null ) {
         return parserService.withExistingParsedDocument( parsedDocument.sourceDocument(), this::validateFastForCurrentDocument )
               .orElse( ViolationReport.EMPTY );
      }
      synchronized ( parserLock ) {
         return validateFastForCurrentDocument( parsedDocument );
      }
   }

   private ViolationReport validateFastForCurrentDocument( final ParsedDocument parsedDocument ) {
      final ViolationReport result = violationProviders.stream()
            .filter( provider -> provider.type().equals( ViolationProvider.Type.FAST ) )
            .map( provider -> executeViolationProvider( provider, parsedDocument ) )
            .reduce( ViolationReport::merge )
            .orElse( ViolationReport.EMPTY );
      fastValidationResults.put( parsedDocument.sourceDocument(), result );
      return result;
   }

   public void onDocumentOpened( final ParsedDocument parsedDocument ) {
      cancelRunningValidation( parsedDocument.sourceDocument() );
      validateFast( parsedDocument );
      validateAsync( parsedDocument );
   }

   public void onDocumentChanged( final ParsedDocument parsedDocument ) {
      cancelRunningValidation( parsedDocument.sourceDocument() );
      onValidationComplete.accept( parsedDocument.sourceDocument(), validateFast( parsedDocument ) );
      rescheduleValidation( parsedDocument );
   }

   public void onDocumentSaved( final ParsedDocument parsedDocument ) {
      cancelRunningValidation( parsedDocument.sourceDocument() );
      onValidationComplete.accept( parsedDocument.sourceDocument(), validateFast( parsedDocument ) );
      rescheduleValidation( parsedDocument );
   }

   public void onDocumentClosed( final Document document ) {
      if ( document == null ) {
         return;
      }
      cancelScheduledValidation( document );
      cancelRunningValidation( document );
      generations.remove( document );
      fastValidationResults.remove( document );
   }

   @Override
   public void close() {
      if ( !closed.compareAndSet( false, true ) ) {
         return;
      }
      scheduledValidations.values().forEach( f -> f.cancel( false ) );
      scheduledValidations.clear();
      runningValidations.values().forEach( f -> f.cancel( true ) );
      runningValidations.clear();
      generations.clear();
      fastValidationResults.clear();
      scheduler.shutdownNow();
      executorService.shutdownNow();
   }

   private void rescheduleValidation( final ParsedDocument parsedDocument ) {
      final Document document = parsedDocument.sourceDocument();
      cancelScheduledValidation( document );
      final ScheduledFuture<?> task = scheduler.schedule(
            () -> {
               scheduledValidations.remove( document );
               LOG.debug( "[debounce] idle timeout reached, submitting document validation for URI={}", document.uri() );
               validateAsync( parsedDocument );
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
         LOG.debug( "[cancel] cancelling in-flight document validation for URI={}", document.uri() );
         previous.cancel( true );
      }
   }

   public CompletableFuture<ViolationReport> validateAsync( final ParsedDocument parsedDocument ) {
      final Document document = parsedDocument.sourceDocument();
      cancelRunningValidation( document );
      final long generation = generations.computeIfAbsent( document, ignored -> new AtomicLong() ).incrementAndGet();
      final CompletableFuture<ViolationReport> future = CompletableFuture.supplyAsync(
            () -> validateDelayed( parsedDocument ),
            executorService
      );
      runningValidations.put( document, future );
      future.whenComplete( ( result, throwable ) -> {
         runningValidations.remove( document, future );
         final Throwable failure = unwrap( throwable );
         if ( failure instanceof CancellationException || future.isCancelled() ) {
            LOG.debug( "[cancel] document validation cancelled for URI={}", document.uri() );
            return;
         }
         final long currentGeneration = generations.getOrDefault( document, new AtomicLong() ).get();
         if ( generation != currentGeneration ) {
            LOG.debug( "[stale] ignoring stale result for URI={}, generation={}, current={}", document.uri(), generation,
                  currentGeneration );
            return;
         }
         if ( failure != null ) {
            LOG.error( "[error] document validation failed for URI={}", document.uri(), failure );
            return;
         }
         onValidationComplete.accept( document, result );
      } );
      return future;
   }

   private ViolationReport executeViolationProvider( final ViolationProvider provider, final ParsedDocument parsedDocument ) {
      try {
         return provider.validate( parsedDocument );
      } catch ( final Throwable throwable ) {
         LOG.error( "[error] unexpected runtime failure during validation for URI={}",
               parsedDocument.sourceDocument().uri(), throwable );
         return new ViolationReport( ProcessingViolationBuilder.builder()
               .message( throwable.getMessage() )
               .cause( Optional.of( throwable ) )
               .build() );
      }
   }

   private ViolationReport validateDelayed( final ParsedDocument parsedDocument ) {
      if ( parserService != null ) {
         return parserService.withExistingParsedDocument( parsedDocument.sourceDocument(), this::validateDelayedForCurrentDocument )
               .orElse( ViolationReport.EMPTY );
      }
      synchronized ( parserLock ) {
         return validateDelayedForCurrentDocument( parsedDocument );
      }
   }

   private ViolationReport validateDelayedForCurrentDocument( final ParsedDocument parsedDocument ) {
      return violationProviders.stream()
            .filter( provider -> provider.type().equals( ViolationProvider.Type.DELAYED ) )
            .map( provider -> executeViolationProvider( provider, parsedDocument ) )
            .reduce( ViolationReport::merge )
            .orElse( ViolationReport.EMPTY )
            .merge( fastValidationResults.getOrDefault( parsedDocument.sourceDocument(), ViolationReport.EMPTY ) );
   }

   private Throwable unwrap( final Throwable throwable ) {
      if ( throwable instanceof CompletionException && throwable.getCause() != null ) {
         return throwable.getCause();
      }
      return throwable;
   }
}
