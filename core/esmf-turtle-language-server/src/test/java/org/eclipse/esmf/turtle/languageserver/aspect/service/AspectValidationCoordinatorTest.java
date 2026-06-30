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

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectViolationDiagnosticMapper;
import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class AspectValidationCoordinatorTest {
   @Test
   void validatesChangedDocumentAsynchronouslyAndUpdatesCache() throws InterruptedException {
      final Document document = new Document( "test.ttl", "" );
      final ParsedDocument parsedDocument = new TreeSitterTurtleParserService().apply( document );
      final CountDownLatch callbackCalled = new CountDownLatch( 1 );
      final AtomicReference<DiagnosticReport> callbackReport = new AtomicReference<>();
      final AspectValidationCoordinator coordinator = new AspectValidationCoordinator(
            new StubAspectModelValidationService( List.of( new ProcessingViolation( "processing violation", new RuntimeException() ) ) ),
            new StubParsedAspectModelFileLoader(),
            new AspectViolationDiagnosticMapper(),
            ( changedDocument, report ) -> {
               assertThat( changedDocument ).isSameAs( document );
               callbackReport.set( report );
               callbackCalled.countDown();
            } );

      try ( coordinator ) {
         coordinator.onDocumentOpened( parsedDocument );

         assertThat( callbackCalled.await( 5, TimeUnit.SECONDS ) ).isTrue();
         assertThat( callbackReport.get().diagnostics() ).singleElement()
               .satisfies( diagnostic -> {
                  assertThat( diagnostic.code().code() ).isEqualTo( ProcessingViolation.ERROR_CODE );
                  assertThat( diagnostic.message() ).isEqualTo( "processing violation" );
               } );
         assertThat( coordinator.getCachedDiagnostics( document ).diagnostics() ).hasSize( 1 );
      }
   }

   @Test
   void unexpectedValidationFailurePublishesSafeMessageAndLogsException() throws InterruptedException {
      final Document document = new Document( "test.ttl", "" );
      final ParsedDocument parsedDocument = new TreeSitterTurtleParserService().apply( document );
      final CountDownLatch callbackCalled = new CountDownLatch( 1 );
      final AtomicReference<DiagnosticReport> callbackReport = new AtomicReference<>();
      final RuntimeException failure = new RuntimeException( "secret internal details" );
      final Logger logger = (Logger) LoggerFactory.getLogger( AspectDocumentValidationService.class );
      final ListAppender<ILoggingEvent> appender = new ListAppender<>();
      appender.start();
      logger.addAppender( appender );
      final AspectValidationCoordinator coordinator = new AspectValidationCoordinator(
            new StubAspectModelValidationService( List.of() ),
            new ThrowingParsedAspectModelFileLoader( failure ),
            new AspectViolationDiagnosticMapper(),
            ( changedDocument, report ) -> {
               assertThat( changedDocument ).isSameAs( document );
               callbackReport.set( report );
               callbackCalled.countDown();
            } );

      try ( coordinator ) {
         coordinator.onDocumentOpened( parsedDocument );

         assertThat( callbackCalled.await( 5, TimeUnit.SECONDS ) ).isTrue();
         assertThat( callbackReport.get().diagnostics() ).singleElement()
               .satisfies( diagnostic -> {
                  assertThat( diagnostic.code().code() ).isEqualTo( ProcessingViolation.ERROR_CODE );
                  assertThat( diagnostic.message() ).isEqualTo( AspectViolationDiagnosticMapper.PROCESSING_ERROR_MESSAGE );
               } );
         assertThat( appender.list ).anySatisfy( event -> {
            assertThat( event.getLevel() ).isEqualTo( Level.ERROR );
            assertThat( event.getFormattedMessage() ).contains( "unexpected runtime failure" );
            assertThat( event.getThrowableProxy() ).isNotNull();
         } );
      } finally {
         logger.detachAppender( appender );
      }
   }

   @Test
   void coordinatorUsesSharedValidationService() throws InterruptedException {
      final Document document = new Document( "test.ttl", "" );
      final ParsedDocument parsedDocument = new TreeSitterTurtleParserService().apply( document );
      final CountDownLatch callbackCalled = new CountDownLatch( 1 );
      final AtomicReference<ParsedDocument> validatedDocument = new AtomicReference<>();
      final DiagnosticReport expectedReport =
            new DiagnosticReport( "validation result", org.eclipse.esmf.treesitterturtle.TurtleDiagnostic.TurtleCode.E0000 );
      final AspectValidationCoordinator coordinator = new AspectValidationCoordinator(
            new StubAspectDocumentValidationService( validatedDocument, expectedReport ),
            ( changedDocument, report ) -> {
               assertThat( changedDocument ).isSameAs( document );
               assertThat( report ).isSameAs( expectedReport );
               callbackCalled.countDown();
            } );

      try ( coordinator ) {
         coordinator.onDocumentOpened( parsedDocument );

         assertThat( callbackCalled.await( 5, TimeUnit.SECONDS ) ).isTrue();
         assertThat( validatedDocument.get() ).isSameAs( parsedDocument );
         assertThat( coordinator.getCachedDiagnostics( document ) ).isSameAs( expectedReport );
      }
   }

   @Test
   void asyncValidationFailureUsesSharedValidationServiceFailureReport() throws InterruptedException {
      final Document document = new Document( "test.ttl", "" );
      final ParsedDocument parsedDocument = new TreeSitterTurtleParserService().apply( document );
      final CountDownLatch callbackCalled = new CountDownLatch( 1 );
      final DiagnosticReport expectedReport = new DiagnosticReport( "shared failure report",
            org.eclipse.esmf.treesitterturtle.TurtleDiagnostic.TurtleCode.E0000 );
      final AspectValidationCoordinator coordinator = new AspectValidationCoordinator(
            new ThrowingAspectDocumentValidationService( expectedReport ),
            ( changedDocument, report ) -> {
               assertThat( changedDocument ).isSameAs( document );
               assertThat( report ).isSameAs( expectedReport );
               callbackCalled.countDown();
            } );

      try ( coordinator ) {
         coordinator.onDocumentOpened( parsedDocument );

         assertThat( callbackCalled.await( 5, TimeUnit.SECONDS ) ).isTrue();
         assertThat( coordinator.getCachedDiagnostics( document ) ).isSameAs( expectedReport );
      }
   }

   private static class StubAspectModelValidationService extends AspectModelValidationService {
      private final List<Violation> violations;

      StubAspectModelValidationService( final List<Violation> violations ) {
         this.violations = violations;
      }

      @Override
      public List<Violation> validate( final RawAspectModelFile file ) {
         return violations;
      }
   }

   private static class StubParsedAspectModelFileLoader extends ParsedAspectModelFileLoader {
      @Override
      public RawAspectModelFile load( final ParsedDocument parsedDocument ) {
         return new RawAspectModelFile( parsedDocument.sourceDocument().getContent(), ModelFactory.createDefaultModel(), List.of(),
               Optional.of( URI.create( parsedDocument.getUri() ) ) );
      }
   }

   private static class ThrowingParsedAspectModelFileLoader extends ParsedAspectModelFileLoader {
      private final RuntimeException failure;

      ThrowingParsedAspectModelFileLoader( final RuntimeException failure ) {
         this.failure = failure;
      }

      @Override
      public RawAspectModelFile load( final ParsedDocument parsedDocument ) {
         throw failure;
      }
   }

   private static class StubAspectDocumentValidationService extends AspectDocumentValidationService {
      private final AtomicReference<ParsedDocument> validatedDocument;
      private final DiagnosticReport report;

      StubAspectDocumentValidationService( final AtomicReference<ParsedDocument> validatedDocument, final DiagnosticReport report ) {
         this.validatedDocument = validatedDocument;
         this.report = report;
      }

      @Override
      public DiagnosticReport validate( final ParsedDocument parsedDocument ) {
         validatedDocument.set( parsedDocument );
         return report;
      }
   }

   private static class ThrowingAspectDocumentValidationService extends AspectDocumentValidationService {
      private final DiagnosticReport failureReport;

      ThrowingAspectDocumentValidationService( final DiagnosticReport failureReport ) {
         this.failureReport = failureReport;
      }

      @Override
      public DiagnosticReport validate( final ParsedDocument parsedDocument ) {
         throw new RuntimeException( "validation failed" );
      }

      @Override
      public DiagnosticReport processingFailureReport() {
         return failureReport;
      }
   }
}
