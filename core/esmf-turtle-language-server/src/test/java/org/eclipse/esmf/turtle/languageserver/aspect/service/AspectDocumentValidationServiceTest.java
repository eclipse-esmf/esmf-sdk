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
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.esmf.aspectmodel.ViolationReport;
import org.eclipse.esmf.aspectmodel.resolver.ModelResolutionViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.MinCountViolation;
import org.eclipse.esmf.aspectmodel.validation.InvalidLexicalValueViolation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.validation.RegularExpressionConstraintViolation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.InvalidTestAspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.treesitterturtle.TurtleViolationCode;
import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.TestViolation;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticMapper;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

import org.eclipse.lsp4j.Diagnostic;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.vavr.control.Either;

class AspectDocumentValidationServiceTest {
   @Test
   void parserExceptionIsMappedToSyntaxFallbackDiagnostic() {
      final AspectModelValidationService service = new AspectModelValidationService( new AspectModelValidator() );
      final ViolationReport report = service.validate( parsedDocument( InvalidTestAspect.INVALID_SYNTAX ) );
      assertThat( report.violations() ).singleElement()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.code().code() ).isEqualTo( TurtleViolationCode.ERR_SYNTAX.code() );
               assertThat( diagnostic.message() ).isEqualTo( "Triples not terminated by DOT" );
            } );
   }

   @Test
   void ordinaryValidationViolationsAreMapped() {
      final AspectModelValidationService service = new AspectModelValidationService( new AspectModelValidator() {
         @Override
         public Either<ViolationReport, AspectModel> loadModel( final Supplier<AspectModel> aspectModelLoader ) {
            return Either.left( new ViolationReport( new TestViolation( "ERR_TEST", "semantic problem" ) ) );
         }
      } );

      final ViolationReport report = service.validate( parsedDocument( TestAspect.ASPECT ) );
      assertThat( report.violations() ).singleElement()
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
         public Either<ViolationReport, AspectModel> loadModel( final Supplier<AspectModel> aspectModelLoader ) {
            return Either.left( new ViolationReport( new ProcessingViolation( "processing violation", cause ) ) );
         }
      } );
      final Logger logger = (Logger) LoggerFactory.getLogger( AspectModelValidationService.class );
      final ListAppender<ILoggingEvent> appender = new ListAppender<>();
      appender.start();
      logger.addAppender( appender );

      try {
         final ViolationReport report = service.validate( parsedDocument( TestAspect.ASPECT ) );

         assertThat( report.violations() ).singleElement()
               .satisfies( diagnostic -> {
                  assertThat( diagnostic.code().code() ).isEqualTo( ProcessingViolation.ERROR_CODE );
                  assertThat( diagnostic.message() ).isEqualTo( "processing violation" );
                  assertThat( diagnostic.message() ).doesNotContain( "secret internal details" );
               } );
         assertThat( appender.list ).anySatisfy( event -> {
            assertThat( event.getLevel() ).isEqualTo( Level.WARN );
            assertThat( event.getFormattedMessage() ).contains(
                  "aspect model processing failed: processing violation" );
         } );
      } finally {
         logger.detachAppender( appender );
      }
   }

   @Test
   void riotExceptionReturnsEmptyReport() {
      final AspectModelValidationService service = new AspectModelValidationService();
      final ViolationReport report = service.validate( emptyParsedDocument() );
      assertThat( report.violations() ).isEmpty();
   }

   @Test
   void valueParsingExceptionFromRealLoadingIsMappedToLexicalDiagnostic() {
      final AspectModelValidationService service = new AspectModelValidationService();

      final ViolationReport report = service.validate( parsedDocument( "Aspect.ttl", """
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

      assertThat( report.violations() ).singleElement().isInstanceOfSatisfying( InvalidLexicalValueViolation.class, diagnostic -> {
         assertThat( diagnostic.code().code() ).isEqualTo( InvalidLexicalValueViolation.ERROR_CODE );
         assertThat( diagnostic.message() ).contains( "no valid value for type" );
      } );
   }

   @Test
   void missingReferencedPropertyFromRealLoadingIsMappedToProcessingDiagnosticWithActionableMessage() {
      final AspectModelValidationService service = new AspectModelValidationService();

      final ViolationReport report = service.validate( parsedDocument( "Aspect.ttl", """
         @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .

         :Aspect a samm:Aspect ;
            samm:properties ( :notExistingProperty ) .
         """ ) );

      assertThat( report.violations() ).first()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.code().code() ).isEqualTo( ModelResolutionViolation.ERROR_CODE );
               assertThat( diagnostic ).isInstanceOfSatisfying( ModelResolutionViolation.class, violation -> {
                  assertThat( violation.location().toString() ).contains( "notExistingProperty" );
               } );
               assertThat( diagnostic.message() )
                     .doesNotContain( "AspectLoadingException" )
                     .doesNotContain( "\tat" );
            } );
   }

   @Test
   void measurementWithoutUnitIsMappedToDiagnosticAtTheMeasurementDefinition() {
      final AspectModelValidationService service = new AspectModelValidationService();
      final ParsedDocument document = parsedDocument( "AspectWithMeasurementWithoutUnit.ttl", """
         @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
         @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

         :InvalidMeasurement a samm-c:Measurement ;
            samm:dataType xsd:positiveInteger ;
            samm:description "Specifies the mileage of the vehicle in kilometers."@en .
         """ );

      final ViolationReport report = service.validate( document );
      assertThat( report.violations() ).singleElement()
            .isInstanceOfSatisfying( MinCountViolation.class,
                  violation -> assertThat( violation.message() ).contains( "unit" ) );

      final Map<URI, List<Diagnostic>> diagnostics = new DiagnosticMapper().apply( document.sourceDocument(), report );
      assertThat( diagnostics.get( document.getUri() ) ).singleElement().satisfies( diagnostic -> {
         assertThat( diagnostic.getRange().getStart().getLine() ).isEqualTo( 5 );
         assertThat( diagnostic.getRange().getStart().getCharacter() ).isZero();
      } );
   }

   @Test
   void violationsFoundByDifferentValidatorsAreProperlyReported() {
      final AspectModelValidationService service = new AspectModelValidationService();
      final ParsedDocument document = parsedDocument( "AspectWithMultipleViolations.ttl", """
         @prefix : <urn:samm:org.eclipse.esmf.test:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         @prefix samm-c: <urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#> .
         @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .

         :InvalidMeasurement a samm-c:Measurement ;
            samm:dataType xsd:positiveInteger .

         :InvalidRegexConstraint a samm-c:RegularExpressionConstraint ;
            samm:value "x(?<named>[a-z])y" .
         """ );

      final ViolationReport report = service.validate( document );

      assertThat( report.violations() ).hasSize( 2 );
      assertThat( report.violations() )
            .anySatisfy( violation -> assertThat( violation ).isInstanceOfSatisfying( MinCountViolation.class, minCountViolation -> {
               assertThat( minCountViolation.code().code() ).isEqualTo( MinCountViolation.ERROR_CODE );
               assertThat( minCountViolation.message() ).contains( "unit" );
            } ) );
      assertThat( report.violations() ).anySatisfy(
            violation -> assertThat( violation ).isInstanceOfSatisfying( RegularExpressionConstraintViolation.class, regexViolation -> {
               assertThat( regexViolation.code().code() ).isEqualTo( RegularExpressionConstraintViolation.ERROR_CODE );
               assertThat( regexViolation.message() ).contains( "Regular expression" );
            } ) );
   }
}
