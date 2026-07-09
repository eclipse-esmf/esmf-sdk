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

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.InvalidLexicalValueViolation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.treesitterturtle.TurtleDiagnostic;
import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectViolationDiagnosticMapper;
import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDocumentDiagnostic;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RiotException;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class AspectDocumentValidationServiceTest {
   @Test
   void unexpectedExceptionIsLoggedAndMappedToSafeProcessingDiagnostic() {
      final RuntimeException failure = new RuntimeException( "secret internal details" );
      final AspectDocumentValidationService service = new AspectDocumentValidationService(
            new ThrowingParsedAspectModelFileLoader( failure ),
            new StubAspectModelValidationService( List.of() ),
            new AspectViolationDiagnosticMapper() );
      final Logger logger = (Logger) LoggerFactory.getLogger( AspectDocumentValidationService.class );
      final ListAppender<ILoggingEvent> appender = new ListAppender<>();
      appender.start();
      logger.addAppender( appender );

      try {
         final DiagnosticReport report = service.validate( parsedDocument() );

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
      final AspectDocumentValidationService service = new AspectDocumentValidationService(
            new ThrowingParsedAspectModelFileLoader(
                  new ParserException( 3, 5, "Triples not terminated by DOT", "source", URI.create( "test.ttl" ) ) ),
            new StubAspectModelValidationService( List.of() ),
            new AspectViolationDiagnosticMapper() );

      final DiagnosticReport report = service.validate( parsedDocument() );

      assertThat( report.diagnostics() ).singleElement()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.code().code() ).isEqualTo( TurtleDiagnostic.TurtleCode.E0003.code() );
               assertThat( diagnostic.message() ).isEqualTo( "Triples not terminated by DOT" );
            } );
   }

   @Test
   void ordinaryValidationViolationsAreMapped() {
      final AspectDocumentValidationService service = new AspectDocumentValidationService(
            new StubParsedAspectModelFileLoader(),
            new StubAspectModelValidationService( List.of( new TestViolation( "ERR_TEST", "semantic problem" ) ) ),
            new AspectViolationDiagnosticMapper() );

      final DiagnosticReport report = service.validate( parsedDocument() );

      assertThat( report.diagnostics() ).singleElement()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.code().code() ).isEqualTo( "ERR_TEST" );
               assertThat( diagnostic.message() ).isEqualTo( "semantic problem" );
            } );
   }

   @Test
   void processingViolationIsLoggedAndMappedToUserFacingValidationDiagnostic() {
      final RuntimeException cause = new RuntimeException( "secret internal details" );
      final AspectDocumentValidationService service = new AspectDocumentValidationService(
            new StubParsedAspectModelFileLoader(),
            new StubAspectModelValidationService( List.of( new ProcessingViolation( "processing violation", cause ) ) ),
            new AspectViolationDiagnosticMapper() );
      final Logger logger = (Logger) LoggerFactory.getLogger( AspectDocumentValidationService.class );
      final ListAppender<ILoggingEvent> appender = new ListAppender<>();
      appender.start();
      logger.addAppender( appender );

      try {
         final DiagnosticReport report = service.validate( parsedDocument() );

         assertThat( report.diagnostics() ).singleElement()
               .satisfies( diagnostic -> {
                  assertThat( diagnostic.code().code() ).isEqualTo( ProcessingViolation.ERROR_CODE );
                  assertThat( diagnostic.message() ).isEqualTo( "processing violation" );
                  assertThat( diagnostic.message() ).doesNotContain( "secret internal details" );
               } );
         assertThat( appender.list ).anySatisfy( event -> {
            assertThat( event.getLevel() ).isEqualTo( Level.WARN );
            assertThat( event.getFormattedMessage() ).contains( "aspect model processing failed: processing violation" );
            assertThat( event.getThrowableProxy() ).isNotNull();
         } );
      } finally {
         logger.detachAppender( appender );
      }
   }

   @Test
   void riotExceptionReturnsEmptyReport() {
      final AspectDocumentValidationService service = new AspectDocumentValidationService(
            new ThrowingParsedAspectModelFileLoader( new RiotException( "ordinary syntax error" ) ),
            new StubAspectModelValidationService( List.of() ),
            new AspectViolationDiagnosticMapper() );

      final DiagnosticReport report = service.validate( parsedDocument() );

      assertThat( report.diagnostics() ).isEmpty();
   }

   @Test
   void valueParsingExceptionFromRealLoadingIsMappedToLexicalDiagnostic() {
      final AspectDocumentValidationService service = new AspectDocumentValidationService();

      final DiagnosticReport report = service.validate( parsedDocument( """
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

      assertThat( report.diagnostics() ).singleElement()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.code().code() ).isEqualTo( InvalidLexicalValueViolation.ERROR_CODE );
               assertThat( diagnostic.message() ).isEqualTo( "Invalid value" );
               assertThat( diagnostic ).isInstanceOf( TurtleDocumentDiagnostic.class );
            } );
   }

   @Test
   void missingReferencedPropertyFromRealLoadingIsMappedToProcessingDiagnosticWithActionableMessage() {
      final AspectDocumentValidationService service = new AspectDocumentValidationService();

      final DiagnosticReport report = service.validate( parsedDocument( """
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

   private ParsedDocument parsedDocument() {
      return new TreeSitterTurtleParserService().apply( new Document( "test.ttl", "" ) );
   }

   private ParsedDocument parsedDocument( final String content ) {
      final TreeSitterTurtleParserService parserService = new TreeSitterTurtleParserService();
      final Document document = new Document( "test.ttl", content );
      parserService.onOpen( document );
      return parserService.apply( document );
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

      @Override
      public List<Violation> validate( final RawAspectModelFile file, final ParsedDocument parsedDocument ) {
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

   private record TestViolation(
         String errorCode, String message
   ) implements Violation {
      @Override
      public EvaluationContext context() {
         return null;
      }

      @Override
      public String violationSpecificMessage() {
         return message;
      }

      @Override
      public <T> T accept( final Visitor<T> visitor ) {
         return visitor.visit( this );
      }
   }
}
