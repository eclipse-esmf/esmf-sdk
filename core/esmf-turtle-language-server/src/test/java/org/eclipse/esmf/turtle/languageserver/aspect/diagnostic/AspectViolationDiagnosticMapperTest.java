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

package org.eclipse.esmf.turtle.languageserver.aspect.diagnostic;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;

import org.eclipse.esmf.Diagnostic;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.InvalidLexicalValueViolation;
import org.eclipse.esmf.aspectmodel.validation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.treesitterturtle.TurtleDiagnostic;
import org.eclipse.esmf.treesitterturtle.TurtleDiagnosticCode;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticMapper;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;

import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Test;

class AspectViolationDiagnosticMapperTest {
   @Test
   void ignoresInvalidSyntaxViolationFromSemanticDiagnostics() {
      final AspectViolationDiagnosticMapper mapper = new AspectViolationDiagnosticMapper();
      final InvalidSyntaxViolation violation = new InvalidSyntaxViolation(
            "broken syntax", "source", 3, 5, URI.create( "test.ttl" ) );

      final DiagnosticReport report = mapper.mapValidationViolations( List.of( violation ) );

      assertThat( report.diagnostics() ).isEmpty();
   }

   @Test
   void mapsInvalidLexicalValueViolationAsLexicalDiagnostic() {
      final AspectViolationDiagnosticMapper mapper = new AspectViolationDiagnosticMapper();
      final InvalidLexicalValueViolation violation = new InvalidLexicalValueViolation(
            null, "999", 3, 5, "", URI.create( "test.ttl" ) );

      final DiagnosticReport report = mapper.mapValidationViolations( List.of( violation ) );

      assertThat( report.diagnostics() ).singleElement()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.code().code() ).isEqualTo( InvalidLexicalValueViolation.ERROR_CODE );
               assertThat( diagnostic.message() ).isEqualTo( "Invalid value" );
               assertThat( diagnostic ).isInstanceOf( AspectDocumentDiagnostic.class );
            } );
   }

   @Test
   void mapsInvalidLexicalValueViolationWithoutSourceLocationAsBaseDiagnostic() {
      final AspectViolationDiagnosticMapper mapper = new AspectViolationDiagnosticMapper();
      final InvalidLexicalValueViolation violation = new InvalidLexicalValueViolation(
            null, "999", 3, 5, "", null );

      final DiagnosticReport report = mapper.mapValidationViolations( List.of( violation ) );

      assertThat( report.diagnostics() ).singleElement()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.code().code() ).isEqualTo( InvalidLexicalValueViolation.ERROR_CODE );
               assertThat( diagnostic.message() ).isEqualTo( "Invalid value" );
               assertThat( diagnostic ).isExactlyInstanceOf( AspectDiagnostic.class );
            } );
   }

   @Test
   void mapsProcessingViolationToProcessingDiagnosticWithViolationMessage() {
      final AspectViolationDiagnosticMapper mapper = new AspectViolationDiagnosticMapper();
      final ProcessingViolation violation = new ProcessingViolation(
            "Resource urn:samm:org.eclipse.esmf.test:1.0.0#notExistingProperty has no type",
            new RuntimeException( "secret" ) );

      final DiagnosticReport report = mapper.mapValidationViolations( List.of( violation ) );

      assertThat( report.diagnostics() ).singleElement()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.code().code() ).isEqualTo( ProcessingViolation.ERROR_CODE );
               assertThat( diagnostic.message() )
                     .isEqualTo( "Resource urn:samm:org.eclipse.esmf.test:1.0.0#notExistingProperty has no type" );
            } );
   }

   @Test
   void mapsProcessingViolationWithoutExposingCauseInDiagnosticMessage() {
      final AspectViolationDiagnosticMapper mapper = new AspectViolationDiagnosticMapper();
      final ProcessingViolation violation = new ProcessingViolation( "user facing validation message",
            new RuntimeException( "secret internal details" ) );

      final DiagnosticReport report = mapper.mapValidationViolations( List.of( violation ) );

      assertThat( report.diagnostics() ).singleElement()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.message() ).isEqualTo( "user facing validation message" );
               assertThat( diagnostic.message() )
                     .doesNotContain( "RuntimeException" )
                     .doesNotContain( "secret internal details" )
                     .doesNotContain( "\tat" );
            } );
   }

   @Test
   void mapsProcessingViolationWithoutLocationToFallbackRange() {
      final AspectViolationDiagnosticMapper violationMapper = new AspectViolationDiagnosticMapper();
      final DiagnosticMapper diagnosticMapper = new DiagnosticMapper();
      final Document document = new Document( "test.ttl", "" );
      final ProcessingViolation violation = new ProcessingViolation( "processing violation", new RuntimeException() );

      final List<org.eclipse.lsp4j.Diagnostic> diagnostics = diagnosticMapper.toDiagnostics( document,
            violationMapper.mapValidationViolations( List.of( violation ) ) );

      assertThat( diagnostics ).singleElement()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.getCode().getLeft() ).isEqualTo( ProcessingViolation.ERROR_CODE );
               assertThat( diagnostic.getMessage().getLeft() ).isEqualTo( "processing violation" );
               assertThat( diagnostic.getRange() ).isEqualTo( new Range( new Position( 0, 0 ), new Position( 0, 1 ) ) );
            } );
   }

   @Test
   void mapsUnexpectedProcessingFailureToSafeDiagnostic() {
      final AspectViolationDiagnosticMapper mapper = new AspectViolationDiagnosticMapper();

      final DiagnosticReport report = mapper.processingFailureReport();

      assertThat( report.diagnostics() ).singleElement()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.code().code() ).isEqualTo( ProcessingViolation.ERROR_CODE );
               assertThat( diagnostic.message() ).isEqualTo( AspectViolationDiagnosticMapper.PROCESSING_ERROR_MESSAGE );
            } );
   }

   @Test
   void mapsParserExceptionToSyntaxFallbackDiagnostic() {
      final AspectViolationDiagnosticMapper mapper = new AspectViolationDiagnosticMapper();
      final ParserException exception = new ParserException( 3, 5, "Triples not terminated by DOT", "source",
            URI.create( "test.ttl" ) );

      final DiagnosticReport report = mapper.mapParserException( exception, "test.ttl" );

      assertThat( report.diagnostics() ).singleElement()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.code().code() ).isEqualTo( TurtleDiagnosticCode.E0003.code() );
               assertThat( diagnostic.message() ).isEqualTo( "Triples not terminated by DOT" );
            } );
   }

   @Test
   void mapsSemanticViolationUsingViolationErrorCode() {
      final AspectViolationDiagnosticMapper mapper = new AspectViolationDiagnosticMapper();
      final Violation violation = new TestViolation( "ERR_TEST_SHACL", "semantic problem" );

      final DiagnosticReport report = mapper.mapValidationViolations( List.of( violation ) );

      assertThat( report.diagnostics() ).singleElement()
            .satisfies( diagnostic -> {
               assertThat( diagnostic.code().code() ).isEqualTo( "ERR_TEST_SHACL" );
               assertThat( diagnostic.message() ).isEqualTo( "semantic problem" );
            } );
   }

   @Test
   void mapsDiagnosticSeverityToLspSeverity() {
      final DiagnosticMapper mapper = new DiagnosticMapper();
      final Document document = new Document( "test.ttl", "" );
      final DiagnosticReport report = new DiagnosticReport( new TurtleDiagnostic(
            "warning", TurtleDiagnosticCode.E0000, Diagnostic.Severity.WARNING ) );

      final List<org.eclipse.lsp4j.Diagnostic> diagnostics = mapper.toDiagnostics( document, report );

      assertThat( diagnostics ).singleElement()
            .extracting( org.eclipse.lsp4j.Diagnostic::getSeverity )
            .isEqualTo( DiagnosticSeverity.Warning );
   }

   @Test
   void mapsDiagnosticWithoutLocationToFallbackRange() {
      final DiagnosticMapper mapper = new DiagnosticMapper();
      final Document document = new Document( "test.ttl", "" );
      final DiagnosticReport report = new DiagnosticReport( new TurtleDiagnostic(
            "warning", TurtleDiagnosticCode.E0000, Diagnostic.Severity.WARNING ) );

      final List<org.eclipse.lsp4j.Diagnostic> diagnostics = mapper.toDiagnostics( document, report );

      assertThat( diagnostics ).singleElement()
            .extracting( org.eclipse.lsp4j.Diagnostic::getRange )
            .isEqualTo( new Range( new Position( 0, 0 ), new Position( 0, 1 ) ) );
   }
}
