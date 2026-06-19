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

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RiotException;

import org.eclipse.esmf.Diagnostic;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.parser.TokenRegistry;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleBaseDiagnostic;
import org.eclipse.esmf.TurtleDiagnostic;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDiagnosticsService;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDocumentDiagnostic;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspectModelValidationService implements TurtleDiagnosticsService {
   private static final Logger LOG = LoggerFactory.getLogger( AspectModelValidationService.class );

   private final AspectModelLoader loader;
   private final AspectModelValidator validator;

   public AspectModelValidationService() {
      this( new AspectModelLoader(), new AspectModelValidator() );
   }

   AspectModelValidationService( final AspectModelLoader loader, final AspectModelValidator validator ) {
      this.loader = loader;
      this.validator = validator;
   }

   @Override
   public DiagnosticReport onChange( final ParsedDocument document ) {
      return DiagnosticReport.EMPTY;
   }

   @Override
   public DiagnosticReport defaultValidate( final ParsedDocument parsedDocument ) {
      final Document document = parsedDocument.sourceDocument();
      try ( final InputStream inputStream = document.getInputStream() ) {
         LOG.debug( "[load] loading aspect model from {}", document.getUri() );
         final TurtleSyntaxTree syntaxTree = TurtleSyntaxTree.fromConcreteSyntaxTree( parsedDocument.concreteSyntaxTree(),
               () -> parsedDocument.sourceDocument().getContent(),
               location -> parsedDocument.sourceDocument().subSequence( location.fromLine(), location.fromColumn(),
                     location.toLine(), location.toColumn() ) );
         final RawAspectModelFile file = AspectModelFileLoader.load( syntaxTree, URI.create( document.getUri() ) );
         final List<Violation> violations =
               validator.validateModel( () -> loader.loadAspectModelFiles( List.of( file ) ) );
         LOG.debug( "[validate] validation finished for {} with {} violation(s)", document.getUri(), violations.size() );
         return new DiagnosticReport( violations.stream().flatMap( violation -> toViolationInfo( violation ).stream() ).toList() );
      } catch ( final RiotException exception ) {
         // Ignore. Syntax errors are handled by the TurtleSyntaxDiagnosticsService
         return DiagnosticReport.EMPTY;
      } catch ( final ParserException exception ) {
         // Can happen for cases where Jena complains but TreeSitter doesn't
         return new DiagnosticReport( diagnosticFromParserException( exception, parsedDocument.getUri() ) );
      } catch ( final Exception exception ) {
         LOG.error( "[validate] unexpected runtime failure for {}", document.getUri(), exception );
         return new DiagnosticReport( exception.getMessage(), TurtleDiagnostic.TurtleCode.E0000 );
      }
   }

   private TurtleDiagnostic diagnosticFromParserException( final ParserException exception, final String sourceLocation ) {
      return new TurtleDocumentDiagnostic( exception.getMessage(), TurtleDiagnostic.TurtleCode.E0003, sourceLocation,
            (int) exception.getLine() - 1, (int) exception.getColumn() - 1,
            (int) exception.getLine() - 1, (int) exception.getColumn() );
   }

   private Diagnostic.Code classifyViolation( final Violation violation ) {
      // TODO
      return TurtleDiagnostic.TurtleCode.E0000;
   }

   private Optional<TurtleDiagnostic> toViolationInfo( final Violation violation ) {
      return switch ( violation ) {
         // Syntax violation diagnostics are provided by TurtleSyntaxDiagnosticsService
         case final InvalidSyntaxViolation _ -> Optional.empty();
         // TODO Add other specific violations here
         default -> {
            final TurtleSyntaxTree.Location location = Optional.ofNullable( violation.highlight() )
                  .map( RDFNode::asNode )
                  .flatMap( TokenRegistry::getToken )
                  .flatMap( smartToken -> Optional.ofNullable( smartToken.getTreesitterToken() ) )
                  .map( TurtleSyntaxTree.Token::location )
                  .orElse( null );
            yield location == null
                  ? Optional.of( new TurtleBaseDiagnostic(
                        violation.message(),
                        classifyViolation( violation ) ) )
                  : Optional.of( new TurtleDocumentDiagnostic(
                        violation.message(),
                        classifyViolation( violation ),
                        violation.sourceLocation().map( URI::toString ).orElseThrow(),
                        location.fromLine(),
                        location.fromColumn(),
                        location.toLine(),
                        location.toColumn() ) );
         }
      };
   }
}
