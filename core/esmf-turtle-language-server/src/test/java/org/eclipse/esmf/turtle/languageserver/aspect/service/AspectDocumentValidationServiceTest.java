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
import static org.eclipse.esmf.turtle.languageserver.aspect.TestUtil.emptyParsedDocument;
import static org.eclipse.esmf.turtle.languageserver.aspect.TestUtil.parsedDocument;

import java.net.URI;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.InvalidLexicalValueViolation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.treesitterturtle.TurtleDiagnosticCode;
import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectDocumentDiagnostic;
import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectViolationDiagnosticMapper;
import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.TestViolation;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticReport;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

class AspectDocumentValidationServiceTest {
   @Test
   void unexpectedExceptionIsLoggedAndMappedToSafeProcessingDiagnostic() {
      final Logger logger = (Logger) LoggerFactory.getLogger( AspectModelValidationService.class );
      final ListAppender<ILoggingEvent> appender = new ListAppender<>();
      appender.start();
      logger.addAppender( appender );

      final RuntimeException failure = new RuntimeException( "secret internal details" );
      final AspectModelValidationService service = new AspectModelValidationService( new AspectModelValidator() {
         @Override
         public List<Violation> validateModel( final Supplier<AspectModel> aspectModelSupplier ) {
            throw failure;
         }
      } );

      try {
         final DiagnosticReport report = service.validate( parsedDocument( TestAspect.ASPECT ) );
         assertThat( report.diagnostics() ).singleElement()
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
   void parserExceptionIsMappedToSyntaxFallbackDiagnostic() {
      final AspectModelValidationService service = new AspectModelValidationService( new AspectModelValidator() {
         @Override
         public List<Violation> validateModel( final Supplier<AspectModel> aspectModelSupplier ) {
            throw new ParserException( 3, 5, "Triples not terminated by DOT", "source", URI.create( "test.ttl" ) );
         }
      } );

      final DiagnosticReport report = service.validate( parsedDocument( TestAspect.ASPECT ) );
      assertThat( report.diagnostics() ).singleElement()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.code().code() ).isEqualTo( TurtleDiagnosticCode.E0003.code() );
               assertThat( diagnostic.message() ).isEqualTo( "Triples not terminated by DOT" );
            } );
   }

   @Test
   void ordinaryValidationViolationsAreMapped() {
      final AspectModelValidationService service = new AspectModelValidationService( new AspectModelValidator() {
         @Override
         public List<Violation> validateModel( final Supplier<AspectModel> aspectModelSupplier ) {
            return List.of( new TestViolation( "ERR_TEST", "semantic problem" ) );
         }
      } );

      final DiagnosticReport report = service.validate( parsedDocument( TestAspect.ASPECT ) );
      assertThat( report.diagnostics() ).singleElement()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.code().code() ).isEqualTo( "ERR_TEST" );
               assertThat( diagnostic.message() ).isEqualTo( "semantic problem" );
            } );
   }

   @Test
   void processingViolationIsLoggedAndMappedToUserFacingValidationDiagnostic() {
      final RuntimeException cause = new RuntimeException( "secret internal details" );

      final AspectModelValidationService service = new AspectModelValidationService( new AspectModelValidator() {
         @Override
         public List<Violation> validateModel( final Supplier<AspectModel> aspectModelSupplier ) {
            return List.of( new ProcessingViolation( "processing violation", cause ) );
         }
      } );
      final Logger logger = (Logger) LoggerFactory.getLogger( AspectModelValidationService.class );
      final ListAppender<ILoggingEvent> appender = new ListAppender<>();
      appender.start();
      logger.addAppender( appender );

      try {
         final DiagnosticReport report = service.validate( parsedDocument( TestAspect.ASPECT ) );

         assertThat( report.diagnostics() ).singleElement()
               .satisfies( diagnostic -> {
                  assertThat( diagnostic.code().code() ).isEqualTo( ProcessingViolation.ERROR_CODE );
                  assertThat( diagnostic.message() ).isEqualTo( "processing violation" );
                  assertThat( diagnostic.message() ).doesNotContain( "secret internal details" );
               } );
         assertThat( appender.list ).anySatisfy( event -> {
            assertThat( event.getLevel() ).isEqualTo( Level.WARN );
            assertThat( event.getFormattedMessage() ).contains(
                  "aspect model processing failed: processing violation" );
            assertThat( event.getThrowableProxy() ).isNotNull();
         } );
      } finally {
         logger.detachAppender( appender );
      }
   }

   @Test
   void riotExceptionReturnsEmptyReport() {
      final AspectModelValidationService service = new AspectModelValidationService();
      final DiagnosticReport report = service.validate( emptyParsedDocument() );
      assertThat( report.diagnostics() ).isEmpty();
   }

   @Test
   void valueParsingExceptionFromRealLoadingIsMappedToLexicalDiagnostic() {
      final AspectModelValidationService service = new AspectModelValidationService();

      final DiagnosticReport report = service.validate( parsedDocument( "Aspect.ttl", """
         @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

         :Aspect a samm:Aspect ;
            samm:properties ( :property ) .

         :property a samm:Property ;
            samm:characteristic :Characteristic ;
            samm:exampleValue "999"^^xsd:byte .

         :Characteristic a samm:Characteristic ;
            samm:dataType xsd:byte .
         """ ) );

      assertThat( report.diagnostics() ).singleElement().isInstanceOfSatisfying( AspectDocumentDiagnostic.class, diagnostic -> {
         assertThat( diagnostic.code().code() ).isEqualTo( InvalidLexicalValueViolation.ERROR_CODE );
         assertThat( diagnostic.message() ).isEqualTo( "Invalid value" );
      } );
   }

   @Test
   void missingReferencedPropertyFromRealLoadingIsMappedToProcessingDiagnosticWithActionableMessage() {
      final AspectModelValidationService service = new AspectModelValidationService();

      final DiagnosticReport report = service.validate( parsedDocument( "Aspect.ttl", """
         @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

         :Aspect a samm:Aspect ;
            samm:properties ( :notExistingProperty ) .
         """ ) );

      assertThat( report.diagnostics() ).singleElement()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.code().code() ).isEqualTo( ProcessingViolation.ERROR_CODE );
               assertThat( diagnostic.message() )
                     .contains( "notExistingProperty.ttl" );
               assertThat( diagnostic.message() )
                     .doesNotContain( "AspectLoadingException" )
                     .doesNotContain( "\tat" );
            } );
   }
}
