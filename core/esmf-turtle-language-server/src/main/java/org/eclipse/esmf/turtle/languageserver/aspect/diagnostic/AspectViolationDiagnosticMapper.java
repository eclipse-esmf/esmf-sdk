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
import java.util.function.Function;

import org.apache.jena.rdf.model.RDFNode;

import org.eclipse.esmf.Diagnostic;
import org.eclipse.esmf.Location;
import org.eclipse.esmf.aspectmodel.ValueParsingException;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.aspectmodel.resolver.parser.TokenRegistry;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.InvalidLexicalValueViolation;
import org.eclipse.esmf.aspectmodel.validation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.treesitterturtle.TurtleDiagnosticCode;
import org.eclipse.esmf.treesitterturtle.TurtleDocumentDiagnostic;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticReport;

public class AspectViolationDiagnosticMapper implements Function<List<Violation>, DiagnosticReport> {
   public static final String PROCESSING_ERROR_MESSAGE = "Model validation failed. See language server logs for details.";

   @Override
   public DiagnosticReport apply( final List<Violation> violations ) {
      return mapValidationViolations( violations );
   }

   public DiagnosticReport mapValidationViolations( final List<Violation> violations ) {
      return new DiagnosticReport( violations.stream()
            .<Diagnostic<?>>flatMap( violation -> mapViolation( violation ).stream() )
            .toList() );
   }

   public DiagnosticReport mapParserException( final ParserException exception, final String sourceLocation ) {
      return new DiagnosticReport( new TurtleDocumentDiagnostic( exception.getMessage(), TurtleDiagnosticCode.E0003,
            sourceLocation, new Location( line( exception ), column( exception ), line( exception ), column( exception ) + 1 ) ) );
   }

   public DiagnosticReport mapValueParsingException( final ValueParsingException exception ) {
      return new DiagnosticReport( mapLexicalViolation( lexicalViolation( exception ) ) );
   }

   public DiagnosticReport processingFailureReport() {
      return new DiagnosticReport(
            new AspectDiagnostic( PROCESSING_ERROR_MESSAGE, new AspectDiagnosticCode( ProcessingViolation.ERROR_CODE ) ) );
   }

   private Optional<Diagnostic<AspectDiagnosticCode>> mapViolation( final Violation violation ) {
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

   private Diagnostic<AspectDiagnosticCode> mapLexicalViolation( final InvalidLexicalValueViolation violation ) {
      final AspectDiagnosticCode code = new AspectDiagnosticCode( InvalidLexicalValueViolation.ERROR_CODE );
      final Location diagnosticsLocation = new Location( Math.max( 0, violation.line() - 1 ),
            Math.max( 0, violation.column() - 1 ),
            Math.max( 0, violation.line() - 1 ),
            Math.max( 0, violation.column() ) );
      return Optional.ofNullable( violation.location() )
            .<Diagnostic<AspectDiagnosticCode>>map( location -> new AspectDocumentDiagnostic(
                  violation.message(),
                  code,
                  location.toString(),
                  diagnosticsLocation
            ) )
            .orElseGet( () -> new AspectDiagnostic( violation.message(), code ) );
   }

   private Diagnostic<AspectDiagnosticCode> mapProcessingViolation( final ProcessingViolation violation ) {
      return mapViolationWithOptionalLocation( violation, new AspectDiagnosticCode( ProcessingViolation.ERROR_CODE ) );
   }

   private Diagnostic<AspectDiagnosticCode> mapSemanticViolation( final Violation violation ) {
      return mapViolationWithOptionalLocation( violation, new AspectDiagnosticCode( violation.errorCode() ) );
   }

   private Diagnostic<AspectDiagnosticCode> mapViolationWithOptionalLocation( final Violation violation, final AspectDiagnosticCode code ) {
      final Location location = Optional.ofNullable( violation.highlight() )
            .map( RDFNode::asNode )
            .flatMap( TokenRegistry::getToken )
            .flatMap( smartToken -> Optional.ofNullable( smartToken.getTreesitterToken() ) )
            .map( TurtleSyntaxTree.Token::location )
            .orElse( null );
      if ( location == null ) {
         return new AspectDiagnostic( violation.message(), code );
      }
      return violation.sourceLocation()
            .map( URI::toString )
            .<Diagnostic<AspectDiagnosticCode>>map( sourceLocation -> new AspectDocumentDiagnostic(
                  violation.message(),
                  code,
                  sourceLocation,
                  location
            ) )
            .orElseGet( () -> new AspectDiagnostic( violation.message(), code ) );
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
