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

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.Diagnostic;
import org.eclipse.esmf.aspectmodel.ValueParsingException;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.aspectmodel.resolver.parser.TokenRegistry;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.InvalidLexicalValueViolation;
import org.eclipse.esmf.aspectmodel.validation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.treesitterturtle.TurtleDiagnostic;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleBaseDiagnostic;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDocumentDiagnostic;

import org.apache.jena.rdf.model.RDFNode;

public class AspectViolationDiagnosticMapper {
   public static final String PROCESSING_ERROR_MESSAGE = "Model validation failed. See language server logs for details.";

   public DiagnosticReport mapValidationViolations( final List<Violation> violations ) {
      return new DiagnosticReport( violations.stream()
            .map( this::mapViolation )
            .flatMap( Optional::stream )
            .toList() );
   }

   public DiagnosticReport mapParserException( final ParserException exception, final String sourceLocation ) {
      return new DiagnosticReport( new TurtleDocumentDiagnostic( exception.getMessage(), TurtleDiagnostic.TurtleCode.E0003,
            sourceLocation, line( exception ), column( exception ), line( exception ), column( exception ) + 1 ) );
   }

   public DiagnosticReport mapValueParsingException( final ValueParsingException exception ) {
      return new DiagnosticReport( mapLexicalViolation( lexicalViolation( exception ) ) );
   }

   public DiagnosticReport processingFailureReport() {
      return new DiagnosticReport( new TurtleBaseDiagnostic(
            PROCESSING_ERROR_MESSAGE,
            new AspectViolationCode( ProcessingViolation.ERROR_CODE ) ) );
   }

   private Optional<TurtleDiagnostic> mapViolation( final Violation violation ) {
      if ( violation instanceof InvalidSyntaxViolation ) {
         return Optional.empty();
      }
      if ( violation instanceof final InvalidLexicalValueViolation lexicalViolation ) {
         return Optional.of( mapLexicalViolation( lexicalViolation ) );
      }
      if ( violation instanceof final ProcessingViolation processingViolation ) {
         return Optional.of( mapProcessingViolation( processingViolation ) );
      }
      return Optional.of( mapSemanticViolation( violation ) );
   }

   private TurtleDiagnostic mapLexicalViolation( final InvalidLexicalValueViolation violation ) {
      final Diagnostic.Code code = new AspectViolationCode( InvalidLexicalValueViolation.ERROR_CODE );
      return Optional.ofNullable( violation.location() )
            .<TurtleDiagnostic>map( location -> new TurtleDocumentDiagnostic(
                  violation.message(),
                  code,
                  location.toString(),
                  Math.max( 0, violation.line() - 1 ),
                  Math.max( 0, violation.column() - 1 ),
                  Math.max( 0, violation.line() - 1 ),
                  Math.max( 0, violation.column() ) ) )
            .orElseGet( () -> new TurtleBaseDiagnostic( violation.message(), code ) );
   }

   private TurtleDiagnostic mapProcessingViolation( final ProcessingViolation violation ) {
      return mapViolationWithOptionalLocation( violation, new AspectViolationCode( ProcessingViolation.ERROR_CODE ) );
   }

   private TurtleDiagnostic mapSemanticViolation( final Violation violation ) {
      return mapViolationWithOptionalLocation( violation, violationCode( violation ) );
   }

   private TurtleDiagnostic mapViolationWithOptionalLocation( final Violation violation, final Diagnostic.Code code ) {
      final TurtleSyntaxTree.Location location = Optional.ofNullable( violation.highlight() )
            .map( RDFNode::asNode )
            .flatMap( TokenRegistry::getToken )
            .flatMap( smartToken -> Optional.ofNullable( smartToken.getTreesitterToken() ) )
            .map( TurtleSyntaxTree.Token::location )
            .orElse( null );
      if ( location == null ) {
         return new TurtleBaseDiagnostic( violation.message(), code );
      }
      return violation.sourceLocation()
            .map( URI::toString )
            .<TurtleDiagnostic>map( sourceLocation -> new TurtleDocumentDiagnostic(
                  violation.message(),
                  code,
                  sourceLocation,
                  location.fromLine(),
                  location.fromColumn(),
                  location.toLine(),
                  location.toColumn() ) )
            .orElseGet( () -> new TurtleBaseDiagnostic( violation.message(), code ) );
   }

   private Diagnostic.Code violationCode( final Violation violation ) {
      return new AspectViolationCode( violation.errorCode() );
   }

   private InvalidLexicalValueViolation lexicalViolation( final ValueParsingException exception ) {
      return new InvalidLexicalValueViolation(
            exception.getType(),
            exception.getValue(),
            (int) exception.getLine(),
            (int) exception.getColumn(),
            sourceLine( exception ),
            exception.getSourceLocation() );
   }

   private String sourceLine( final ValueParsingException exception ) {
      if ( exception.getSourceDocument() == null || exception.getLine() < 1 ) {
         return "";
      }
      return exception.getSourceDocument().lines()
            .skip( exception.getLine() - 1 )
            .findFirst()
            .orElse( "" );
   }

   private int line( final ParserException exception ) {
      return Math.max( 0, (int) exception.getLine() - 1 );
   }

   private int column( final ParserException exception ) {
      return Math.max( 0, (int) exception.getColumn() - 1 );
   }
}
