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

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.InvalidLexicalValueViolation;
import org.eclipse.esmf.aspectmodel.validation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleBaseDiagnostic;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDiagnostic;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDiagnosticsService;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDocumentDiagnostic;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;

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
   public DiagnosticReport check( final Document document ) {
      try ( final InputStream inputStream = document.getInputStream() ) {
         LOG.debug( "[load] loading aspect model from {}", document.getUri() );
         final List<Violation> violations =
               validator.validateModel( () -> loader.load( inputStream, URI.create( document.getUri() ) ) );
         LOG.debug( "[validate] validation finished for {} with {} violation(s)", document.getUri(), violations.size() );
         return new DiagnosticReport( violations.stream().map( this::toViolationInfo ).toList() );
      } catch ( final Exception exception ) {
         LOG.error( "[validate] unexpected runtime failure for {}", document.getUri(), exception );
         return new DiagnosticReport( exception.getMessage(), TurtleDiagnostic.TurtleCode.E0000 );
      }
   }

   private TurtleDiagnostic.Code classifyViolation( final Violation violation ) {
      return TurtleDiagnostic.TurtleCode.E0000;
   }

   private TurtleDiagnostic toViolationInfo( final Violation violation ) {
      return switch ( violation ) {
         case final InvalidSyntaxViolation syntaxViolation ->
            new TurtleDocumentDiagnostic(
                  syntaxViolation.message(),
                  classifyViolation( violation ),
                  syntaxViolation.sourceLocation().map( URI::toString ).orElseThrow(),
                  (int) syntaxViolation.line(),
                  (int) syntaxViolation.column(),
                  (int) syntaxViolation.line(),
                  (int) syntaxViolation.column() );
         case final InvalidLexicalValueViolation lexicalValueViolation ->
            new TurtleDocumentDiagnostic(
                  lexicalValueViolation.message(),
                  classifyViolation( violation ),
                  lexicalValueViolation.sourceLocation().map( URI::toString ).orElseThrow(),
                  lexicalValueViolation.line(),
                  lexicalValueViolation.column(),
                  lexicalValueViolation.line(),
                  lexicalValueViolation.column() );
         default -> new TurtleBaseDiagnostic(
               violation.message(),
               classifyViolation( violation ) );
      };
   }
}
