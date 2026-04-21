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

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationError;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationErrorType;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationResult;

public class AspectValidationCoordinator implements AutoCloseable {
   private static final Logger LOG = LoggerFactory.getLogger( AspectValidationCoordinator.class );
   private final AspectModelValidationService validationService;
   private final ExecutorService executorService;
   private final Map<String, CompletableFuture<?>> inFlight = new ConcurrentHashMap<>();
   private final Map<String, AtomicLong> generations = new ConcurrentHashMap<>();

   public AspectValidationCoordinator( final AspectModelValidationService validationService ) {
      this( validationService, Executors.newSingleThreadExecutor( Thread.ofPlatform().name( "aspect-validation-", 0 ).factory() ) );
   }

   AspectValidationCoordinator( final AspectModelValidationService validationService, final ExecutorService executorService ) {
      this.validationService = validationService;
      this.executorService = executorService;
   }

   public long nextGeneration( final String uri ) {
      return generations.computeIfAbsent( uri, ignored -> new AtomicLong() ).incrementAndGet();
   }

   public long currentGeneration( final String uri ) {
      final AtomicLong generation = generations.get( uri );
      return generation != null ? generation.get() : 0L;
   }

   public void cancel( final String uri ) {
      final CompletableFuture<?> previous = inFlight.remove( uri );
      if ( previous != null ) {
         LOG.debug( "[cancel] cancelling previous aspect validation for {}", uri );
         previous.cancel( true );
      }
   }

   public void submit( final String uri, final Path path, final long generation, final BiConsumer<Long, AspectValidationResult> callback ) {
      cancel( uri );
      final CompletableFuture<AspectValidationResult> future = CompletableFuture.supplyAsync(
            () -> validationService.validate( path ),
            executorService
      );
      inFlight.put( uri, future );
      future.whenComplete( ( result, throwable ) -> {
         inFlight.remove( uri, future );
         if ( throwable instanceof CancellationException || future.isCancelled() ) {
            LOG.debug( "[cancel] aspect validation cancelled for {}", uri );
            return;
         }
         if ( throwable != null ) {
            LOG.error( "[publish diagnostics] aspect validation failed for {}", uri, throwable );
            callback.accept( generation, new AspectValidationResult(
                  false,
                  throwable.getMessage(),
                  java.util.List.of(),
                  new AspectValidationError( AspectValidationErrorType.PROCESSING, throwable.getMessage() )
            ) );
            return;
         }
         callback.accept( generation, result );
      } );
   }

   public AspectValidationResult validateSync( final Path path ) {
      return validationService.validate( path );
   }

   @Override
   public void close() {
      inFlight.values().forEach( future -> future.cancel( true ) );
      executorService.shutdownNow();
   }
}
