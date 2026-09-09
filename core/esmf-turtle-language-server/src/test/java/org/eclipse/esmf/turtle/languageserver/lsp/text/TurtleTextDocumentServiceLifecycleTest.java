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

package org.eclipse.esmf.turtle.languageserver.lsp.text;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.esmf.turtle.languageserver.turtle.ValidationCoordinator;

import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.junit.jupiter.api.Test;

class TurtleTextDocumentServiceLifecycleTest {
   private static final int OPEN_CLOSE_CYCLES = 25;
   private static final String URI = "file:///tmp/lifecycle-test.ttl";
   private static final String CONTENT = """
      @prefix ex: <http://example.org/> .

      ex:subject ex:predicate ex:object .
      """;

   @Test
   void repeatedOpenCloseReturnsAllDocumentOwnedStateToBaseline() throws ReflectiveOperationException {
      final TurtleTextDocumentService service = new TurtleTextDocumentService();
      try {
         for ( int cycle = 1; cycle <= OPEN_CLOSE_CYCLES; cycle++ ) {
            service.didOpen( new DidOpenTextDocumentParams( new TextDocumentItem( URI, "turtle", cycle, CONTENT ) ) );
            service.didClose( new DidCloseTextDocumentParams( new TextDocumentIdentifier( URI ) ) );
         }

         final TreeSitterTurtleParserService parserService = field( service, "turtleParserService" );
         final ValidationCoordinator validationCoordinator = field( service, "validationCoordinator" );
         final List<Integer> retainedStateSizes = List.of(
               mapField( service, "documents" ).size(),
               mapField( parserService, "syntaxTrees" ).size(),
               mapField( parserService, "previousDocumentStates" ).size(),
               mapField( validationCoordinator, "fastValidationResults" ).size(),
               mapField( validationCoordinator, "generations" ).size(),
               mapField( validationCoordinator, "scheduledValidations" ).size(),
               mapField( validationCoordinator, "runningValidations" ).size() );

         assertThat( retainedStateSizes )
               .as( "retained [documents, syntax trees, ropes, fast reports, generations, scheduled, running] after %s cycles",
                     OPEN_CLOSE_CYCLES )
               .containsExactly( 0, 0, 0, 0, 0, 0, 0 );
      } finally {
         service.shutdown();
      }
   }

   @Test
   void concurrentParserChangesPreserveAValidTree() throws Exception {
      final TreeSitterTurtleParserService parserService = new TreeSitterTurtleParserService();
      final Document document = new Document( URI, CONTENT );
      parserService.onOpen( document );
      final ExecutorService workers = Executors.newFixedThreadPool( 2 );
      try {
         for ( int iteration = 0; iteration < 100; iteration++ ) {
            final CyclicBarrier barrier = new CyclicBarrier( 2 );
            final Future<?> first = workers.submit( () -> changeAtBarrier( parserService, document, barrier ) );
            final Future<?> second = workers.submit( () -> changeAtBarrier( parserService, document, barrier ) );
            first.get( 5, TimeUnit.SECONDS );
            second.get( 5, TimeUnit.SECONDS );
            assertThat( parserService.apply( document ).concreteSyntaxTree().getRootNode().hasError() ).isFalse();
         }
      } finally {
         workers.shutdownNow();
      }
   }

   @Test
   void ownedServiceShutdownReleasesAllDocumentResources() throws ReflectiveOperationException {
      final TurtleTextDocumentService service = new TurtleTextDocumentService();
      service.didOpen( new DidOpenTextDocumentParams( new TextDocumentItem( URI, "turtle", 1, CONTENT ) ) );
      service.shutdown();

      final TreeSitterTurtleParserService parserService = field( service, "turtleParserService" );
      final ValidationCoordinator validationCoordinator = field( service, "validationCoordinator" );
      assertThat( List.of(
            mapField( service, "documents" ).size(),
            mapField( parserService, "syntaxTrees" ).size(),
            mapField( parserService, "previousDocumentStates" ).size(),
            mapField( validationCoordinator, "fastValidationResults" ).size() ) )
                  .as( "retained [documents, syntax trees, ropes, fast reports] after owned service shutdown" )
                  .containsExactly( 0, 0, 0, 0 );
   }

   private static void changeAtBarrier( final TreeSitterTurtleParserService parserService, final Document document,
         final CyclicBarrier barrier ) {
      try {
         barrier.await( 5, TimeUnit.SECONDS );
         parserService.onChange( document, new TextDocumentContentChangeEvent( CONTENT ) );
      } catch ( final Exception exception ) {
         throw new IllegalStateException( exception );
      }
   }

   @SuppressWarnings( "unchecked" )
   private static Map<Object, Object> mapField( final Object target, final String name ) throws ReflectiveOperationException {
      return (Map<Object, Object>) field( target, name );
   }

   @SuppressWarnings( "unchecked" )
   private static <T> T field( final Object target, final String name ) throws ReflectiveOperationException {
      final Field field = target.getClass().getDeclaredField( name );
      field.setAccessible( true );
      return (T) field.get( target );
   }
}
