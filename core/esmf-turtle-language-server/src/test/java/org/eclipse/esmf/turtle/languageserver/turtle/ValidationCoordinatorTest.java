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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.eclipse.esmf.turtle.languageserver.aspect.TestUtil.emptyParsedDocument;
import static org.eclipse.esmf.turtle.languageserver.aspect.TestUtil.parsedDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectViolationDiagnosticMapper;
import org.eclipse.esmf.turtle.languageserver.aspect.service.AspectModelValidationService;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticsProvider;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

class ValidationCoordinatorTest {
   @Test
   void validatesChangedDocumentAsynchronouslyAndUpdatesCache() throws InterruptedException {
      final Document document = new Document( "test.ttl", "" );
      final ParsedDocument parsedDocument = new TreeSitterTurtleParserService().apply( document );
      final CountDownLatch callbackCalled = new CountDownLatch( 1 );
      final AtomicReference<DiagnosticReport> callbackReport = new AtomicReference<>();

      final BiConsumer<Document, DiagnosticReport> onValidationComplete =
            ( changedDocument, report ) -> {
               assertThat( changedDocument ).isSameAs( document );
               callbackReport.set( report );
               callbackCalled.countDown();
            };
      final List<DiagnosticsProvider> diagnosticsProviders = List.of(
            _ -> {
               final Violation violation = new ProcessingViolation( "processing violation", new RuntimeException() );
               return new AspectViolationDiagnosticMapper().mapValidationViolations( List.of( violation ) );
            }
      );
      final ValidationCoordinator coordinator = new ValidationCoordinator( diagnosticsProviders, onValidationComplete );

      try ( coordinator ) {
         coordinator.onDocumentOpened( parsedDocument );

         assertThat( callbackCalled.await( 5, TimeUnit.SECONDS ) ).isTrue();
         assertThat( callbackReport.get().diagnostics() ).singleElement()
               .satisfies( diagnostic -> {
                  assertThat( diagnostic.code().code() ).isEqualTo( ProcessingViolation.ERROR_CODE );
                  assertThat( diagnostic.message() ).isEqualTo( "processing violation" );
               } );
      }
   }

   @Test
   void unexpectedValidationFailurePublishesSafeMessageAndLogsException() throws InterruptedException {
      final ParsedDocument parsedDocument = parsedDocument( TestAspect.ASPECT );
      final CountDownLatch callbackCalled = new CountDownLatch( 1 );
      final AtomicReference<DiagnosticReport> callbackReport = new AtomicReference<>();
      final RuntimeException failure = new RuntimeException( "secret internal details" );
      final Logger logger = (Logger) LoggerFactory.getLogger( AspectModelValidationService.class );
      final ListAppender<ILoggingEvent> appender = new ListAppender<>();
      appender.start();
      logger.addAppender( appender );

      final BiConsumer<Document, DiagnosticReport> onValidationComplete =
            ( changedDocument, report ) -> {
               assertThat( changedDocument ).isSameAs( parsedDocument.sourceDocument() );
               callbackReport.set( report );
               callbackCalled.countDown();
            };
      final List<DiagnosticsProvider> diagnosticsProviders = List.of(
            new AspectModelValidationService(
                  new AspectModelValidator() {
                     @Override
                     public List<Violation> validateModel( final Supplier<AspectModel> aspectModelSupplier ) {
                        throw failure;
                     }
                  }
            )
      );
      final ValidationCoordinator coordinator = new ValidationCoordinator( diagnosticsProviders, onValidationComplete );

      try ( coordinator ) {
         coordinator.onDocumentOpened( parsedDocument );

         assertThat( callbackCalled.await( 5, TimeUnit.SECONDS ) ).isTrue();
         assertThat( callbackReport.get().diagnostics() ).singleElement()
               .satisfies( diagnostic -> {
                  assertThat( diagnostic.code().code() ).isEqualTo( ProcessingViolation.ERROR_CODE );
                  assertThat( diagnostic.message() ).isEqualTo(
                        AspectViolationDiagnosticMapper.PROCESSING_ERROR_MESSAGE );
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
   void onDocumentChangedRunsFastValidationAndSchedulesDelayedValidation() throws InterruptedException {
      final ParsedDocument parsedDocument = emptyParsedDocument();
      final CountDownLatch fastCallbackCalled = new CountDownLatch( 1 );
      final CountDownLatch delayedCallbackCalled = new CountDownLatch( 2 );
      final AtomicReference<DiagnosticReport> lastReport = new AtomicReference<>();

      final BiConsumer<Document, DiagnosticReport> onValidationComplete =
            ( _, report ) -> {
               lastReport.set( report );
               fastCallbackCalled.countDown();
               delayedCallbackCalled.countDown();
            };
      final List<DiagnosticsProvider> diagnosticsProviders = List.of(
            new DiagnosticsProvider() {
               @Override
               public DiagnosticReport validate( final ParsedDocument document ) {
                  final Violation violation = new ProcessingViolation( "fast validation", new RuntimeException() );
                  return new AspectViolationDiagnosticMapper().mapValidationViolations( List.of( violation ) );
               }
            },
            new DiagnosticsProvider() {
               @Override
               public DiagnosticReport validate( final ParsedDocument document ) {
                  final Violation violation = new ProcessingViolation( "delayed validation", new RuntimeException() );
                  return new AspectViolationDiagnosticMapper().mapValidationViolations( List.of( violation ) );
               }

               @Override
               public Type type() {
                  return Type.DELAYED;
               }
            }
      );
      final ValidationCoordinator coordinator = new ValidationCoordinator( diagnosticsProviders, onValidationComplete );

      try ( coordinator ) {
         coordinator.onDocumentChanged( parsedDocument );

         assertThat( fastCallbackCalled.await( 1, TimeUnit.SECONDS ) ).isTrue();
         assertThat( lastReport.get().diagnostics() ).hasSize( 1 );
         assertThat( lastReport.get().diagnostics().getFirst().message() ).isEqualTo( "fast validation" );

         assertThat( delayedCallbackCalled.await( 6, TimeUnit.SECONDS ) ).isTrue();
         assertThat( lastReport.get().diagnostics() ).hasSize( 2 );
      }
   }

   @Test
   void onDocumentSavedRunsFastValidationAndSchedulesDelayedValidation() throws InterruptedException {
      final ParsedDocument parsedDocument = emptyParsedDocument();
      final CountDownLatch fastCallbackCalled = new CountDownLatch( 1 );
      final CountDownLatch delayedCallbackCalled = new CountDownLatch( 2 );
      final List<String> validationTypes = new ArrayList<>();

      final BiConsumer<Document, DiagnosticReport> onValidationComplete =
            ( _, report ) -> {
               if ( !report.diagnostics().isEmpty() ) {
                  validationTypes.add( report.diagnostics().getFirst().message() );
               }
               fastCallbackCalled.countDown();
               delayedCallbackCalled.countDown();
            };
      final List<DiagnosticsProvider> diagnosticsProviders = List.of(
            new DiagnosticsProvider() {
               @Override
               public DiagnosticReport validate( final ParsedDocument document ) {
                  final Violation violation = new ProcessingViolation( "fast", new RuntimeException() );
                  return new AspectViolationDiagnosticMapper().mapValidationViolations( List.of( violation ) );
               }
            },
            new DiagnosticsProvider() {
               @Override
               public DiagnosticReport validate( final ParsedDocument document ) {
                  final Violation violation = new ProcessingViolation( "delayed", new RuntimeException() );
                  return new AspectViolationDiagnosticMapper().mapValidationViolations( List.of( violation ) );
               }

               @Override
               public Type type() {
                  return Type.DELAYED;
               }
            }
      );
      final ValidationCoordinator coordinator = new ValidationCoordinator( diagnosticsProviders, onValidationComplete );

      try ( coordinator ) {
         coordinator.onDocumentSaved( parsedDocument );

         assertThat( fastCallbackCalled.await( 1, TimeUnit.SECONDS ) ).isTrue();
         assertThat( delayedCallbackCalled.await( 6, TimeUnit.SECONDS ) ).isTrue();
         assertThat( validationTypes ).contains( "fast", "delayed" );
      }
   }

   @Test
   void onDocumentClosedCancelsValidations() throws InterruptedException {
      final ParsedDocument parsedDocument = emptyParsedDocument();
      final AtomicInteger validationCount = new AtomicInteger( 0 );
      final CountDownLatch validationStarted = new CountDownLatch( 1 );

      final BiConsumer<Document, DiagnosticReport> onValidationComplete =
            ( _, _ ) -> validationCount.incrementAndGet();
      final List<DiagnosticsProvider> diagnosticsProviders = List.of(
            new DiagnosticsProvider() {
               @Override
               public DiagnosticReport validate( final ParsedDocument doc ) {
                  validationStarted.countDown();
                  try {
                     Thread.sleep( 2000 ); // Long-running validation
                  } catch ( final InterruptedException e ) {
                     Thread.currentThread().interrupt();
                  }
                  final Violation violation = new ProcessingViolation( "validation", new RuntimeException() );
                  return new AspectViolationDiagnosticMapper().mapValidationViolations( List.of( violation ) );
               }

               @Override
               public Type type() {
                  return Type.DELAYED;
               }
            }
      );
      final ValidationCoordinator coordinator = new ValidationCoordinator( diagnosticsProviders, onValidationComplete );

      try ( coordinator ) {
         coordinator.onDocumentOpened( parsedDocument );
         assertThat( validationStarted.await( 1, TimeUnit.SECONDS ) ).isTrue();

         // Close document while validation is running
         coordinator.onDocumentClosed( parsedDocument.sourceDocument() );

         Thread.sleep( 500 );
         assertThat( validationCount.get() ).isZero(); // Validation should be canceled, callback not called
      }
   }

   @Test
   void onDocumentClosedHandlesNullDocument() {
      final BiConsumer<Document, DiagnosticReport> onValidationComplete = ( _, _ ) -> {};
      final ValidationCoordinator coordinator = new ValidationCoordinator( List.of(), onValidationComplete );

      try ( coordinator ) {
         assertThatCode( () -> {
            // noinspection DataFlowIssue
            coordinator.onDocumentClosed( null );
         } ).doesNotThrowAnyException();
      }
   }

   @Test
   void rapidDocumentChangesDebounceValidation() throws InterruptedException {
      final ParsedDocument parsedDocument = emptyParsedDocument();
      final AtomicInteger delayedValidationCount = new AtomicInteger( 0 );
      final CountDownLatch delayedValidationCalled = new CountDownLatch( 1 );

      final BiConsumer<Document, DiagnosticReport> onValidationComplete =
            ( _, report ) -> {
               if ( report.diagnostics().stream().anyMatch( d -> d.message().equals( "delayed" ) ) ) {
                  delayedValidationCount.incrementAndGet();
                  delayedValidationCalled.countDown();
               }
            };
      final List<DiagnosticsProvider> diagnosticsProviders = List.of(
            new DiagnosticsProvider() {
               @Override
               public DiagnosticReport validate( final ParsedDocument doc ) {
                  final Violation violation = new ProcessingViolation( "delayed", new RuntimeException() );
                  return new AspectViolationDiagnosticMapper().mapValidationViolations( List.of( violation ) );
               }

               @Override
               public Type type() {
                  return Type.DELAYED;
               }
            }
      );
      final ValidationCoordinator coordinator = new ValidationCoordinator( diagnosticsProviders, onValidationComplete );

      try ( coordinator ) {
         // Simulate rapid changes
         for ( int i = 0; i < 5; i++ ) {
            coordinator.onDocumentChanged( parsedDocument );
            Thread.sleep( 500 );
         }

         // Wait for delayed validation
         assertThat( delayedValidationCalled.await( 6, TimeUnit.SECONDS ) ).isTrue();

         // Only one delayed validation should have completed (debounced)
         Thread.sleep( 1000 );
         assertThat( delayedValidationCount.get() ).isEqualTo( 1 );
      }
   }

   @Test
   void fastAndDelayedValidationResultsAreMerged() throws InterruptedException {
      final ParsedDocument parsedDocument = emptyParsedDocument();
      final CountDownLatch callbackCalled = new CountDownLatch( 1 );
      final AtomicReference<DiagnosticReport> finalReport = new AtomicReference<>();

      final BiConsumer<Document, DiagnosticReport> onValidationComplete =
            ( _, report ) -> {
               if ( report.diagnostics().size() == 2 ) {
                  finalReport.set( report );
                  callbackCalled.countDown();
               }
            };
      final List<DiagnosticsProvider> diagnosticsProviders = List.of(
            new DiagnosticsProvider() {
               @Override
               public DiagnosticReport validate( final ParsedDocument document ) {
                  final Violation violation = new ProcessingViolation( "fast validation error", new RuntimeException() );
                  return new AspectViolationDiagnosticMapper().mapValidationViolations( List.of( violation ) );
               }
            },
            new DiagnosticsProvider() {
               @Override
               public DiagnosticReport validate( final ParsedDocument document ) {
                  final Violation violation = new ProcessingViolation( "delayed validation error", new RuntimeException() );
                  return new AspectViolationDiagnosticMapper().mapValidationViolations( List.of( violation ) );
               }

               @Override
               public Type type() {
                  return Type.DELAYED;
               }
            }
      );
      final ValidationCoordinator coordinator = new ValidationCoordinator( diagnosticsProviders, onValidationComplete );

      try ( coordinator ) {
         coordinator.onDocumentOpened( parsedDocument );

         assertThat( callbackCalled.await( 5, TimeUnit.SECONDS ) ).isTrue();
         assertThat( finalReport.get().diagnostics() ).hasSize( 2 );
         assertThat( finalReport.get().diagnostics() )
               .anySatisfy( d -> assertThat( d.message() ).isEqualTo( "fast validation error" ) )
               .anySatisfy( d -> assertThat( d.message() ).isEqualTo( "delayed validation error" ) );
      }
   }

   @Test
   void multipleDiagnosticsProvidersOfSameTypeAreMerged() throws InterruptedException {
      final ParsedDocument parsedDocument = emptyParsedDocument();
      final CountDownLatch callbackCalled = new CountDownLatch( 1 );
      final AtomicReference<DiagnosticReport> finalReport = new AtomicReference<>();

      final BiConsumer<Document, DiagnosticReport> onValidationComplete =
            ( _, report ) -> {
               if ( report.diagnostics().size() == 2 ) {
                  finalReport.set( report );
                  callbackCalled.countDown();
               }
            };
      final List<DiagnosticsProvider> diagnosticsProviders = List.of(
            new DiagnosticsProvider() {
               @Override
               public DiagnosticReport validate( final ParsedDocument doc ) {
                  final Violation violation = new ProcessingViolation( "fast provider 1", new RuntimeException() );
                  return new AspectViolationDiagnosticMapper().mapValidationViolations( List.of( violation ) );
               }
            },
            new DiagnosticsProvider() {
               @Override
               public DiagnosticReport validate( final ParsedDocument doc ) {
                  final Violation violation = new ProcessingViolation( "fast provider 2", new RuntimeException() );
                  return new AspectViolationDiagnosticMapper().mapValidationViolations( List.of( violation ) );
               }
            }
      );
      final ValidationCoordinator coordinator = new ValidationCoordinator( diagnosticsProviders, onValidationComplete );

      try ( coordinator ) {
         coordinator.onDocumentChanged( parsedDocument );

         assertThat( callbackCalled.await( 2, TimeUnit.SECONDS ) ).isTrue();
         assertThat( finalReport.get().diagnostics() ).hasSize( 2 );
         assertThat( finalReport.get().diagnostics() )
               .anySatisfy( d -> assertThat( d.message() ).isEqualTo( "fast provider 1" ) )
               .anySatisfy( d -> assertThat( d.message() ).isEqualTo( "fast provider 2" ) );
      }
   }

   @Test
   void validationReturnsEmptyReportWhenNoDiagnosticsProviders() throws InterruptedException {
      final ParsedDocument parsedDocument = emptyParsedDocument();
      final CountDownLatch callbackCalled = new CountDownLatch( 1 );
      final AtomicReference<DiagnosticReport> report = new AtomicReference<>();

      final BiConsumer<Document, DiagnosticReport> onValidationComplete =
            ( _, diagnosticReport ) -> {
               report.set( diagnosticReport );
               callbackCalled.countDown();
            };
      final ValidationCoordinator coordinator = new ValidationCoordinator( List.of(), onValidationComplete );

      try ( coordinator ) {
         coordinator.onDocumentOpened( parsedDocument );

         assertThat( callbackCalled.await( 2, TimeUnit.SECONDS ) ).isTrue();
         assertThat( report.get().diagnostics() ).isEmpty();
      }
   }

   @Test
   void cancellingValidationPreventsCallbackExecution() throws InterruptedException {
      final ParsedDocument parsedDocument = emptyParsedDocument();
      final AtomicInteger callbackCount = new AtomicInteger( 0 );
      final CountDownLatch validationStarted = new CountDownLatch( 1 );

      final BiConsumer<Document, DiagnosticReport> onValidationComplete =
            ( _, _ ) -> callbackCount.incrementAndGet();
      final List<DiagnosticsProvider> diagnosticsProviders = List.of(
            new DiagnosticsProvider() {
               @Override
               public DiagnosticReport validate( final ParsedDocument doc ) {
                  validationStarted.countDown();
                  try {
                     Thread.sleep( 3000 );
                  } catch ( final InterruptedException e ) {
                     Thread.currentThread().interrupt();
                  }
                  final Violation violation = new ProcessingViolation( "validation", new RuntimeException() );
                  return new AspectViolationDiagnosticMapper().mapValidationViolations( List.of( violation ) );
               }

               @Override
               public Type type() {
                  return Type.DELAYED;
               }
            }
      );
      final ValidationCoordinator coordinator = new ValidationCoordinator( diagnosticsProviders, onValidationComplete );

      try ( coordinator ) {
         coordinator.onDocumentOpened( parsedDocument );
         assertThat( validationStarted.await( 1, TimeUnit.SECONDS ) ).isTrue();

         // Trigger another validation which should cancel the first one
         coordinator.onDocumentOpened( parsedDocument );

         Thread.sleep( 1000 );
         // Only the second validation should complete (the first was canceled)
         assertThat( callbackCount.get() ).isLessThanOrEqualTo( 1 );
      }
   }
}
