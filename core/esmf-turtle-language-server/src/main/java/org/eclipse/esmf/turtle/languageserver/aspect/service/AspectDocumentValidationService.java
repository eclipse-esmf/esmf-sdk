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

import java.util.List;

import org.eclipse.esmf.aspectmodel.ValueParsingException;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectViolationDiagnosticMapper;
import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

import org.apache.jena.riot.RiotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspectDocumentValidationService {
   private static final Logger LOG = LoggerFactory.getLogger( AspectDocumentValidationService.class );

   private final ParsedAspectModelFileLoader aspectModelFileLoader;
   private final AspectModelValidationService aspectModelValidationService;
   private final AspectViolationDiagnosticMapper diagnosticMapper;

   public AspectDocumentValidationService() {
      this( new ParsedAspectModelFileLoader(), new AspectModelValidationService(), new AspectViolationDiagnosticMapper() );
   }

   AspectDocumentValidationService(
         final ParsedAspectModelFileLoader aspectModelFileLoader,
         final AspectModelValidationService aspectModelValidationService,
         final AspectViolationDiagnosticMapper diagnosticMapper ) {
      this.aspectModelFileLoader = aspectModelFileLoader;
      this.aspectModelValidationService = aspectModelValidationService;
      this.diagnosticMapper = diagnosticMapper;
   }

   public DiagnosticReport validate( final ParsedDocument parsedDocument ) {
      try {
         LOG.debug( "[load] loading aspect model from {}", parsedDocument.getUri() );
         final RawAspectModelFile file = aspectModelFileLoader.load( parsedDocument );
         final List<Violation> violations = aspectModelValidationService.validate( file );
         logProcessingViolations( violations );
         return diagnosticMapper.mapValidationViolations( violations );
      } catch ( final RiotException _ ) {
         // Tree-sitter owns ordinary syntax diagnostics. ParserException below keeps Jena-only syntax fallback visible.
         return DiagnosticReport.EMPTY;
      } catch ( final ParserException exception ) {
         return diagnosticMapper.mapParserException( exception, parsedDocument.getUri() );
      } catch ( final ValueParsingException exception ) {
         return diagnosticMapper.mapValueParsingException( exception );
      } catch ( final Exception exception ) {
         LOG.error( "[validate] unexpected runtime failure for {}", parsedDocument.getUri(), exception );
         return diagnosticMapper.processingFailureReport();
      }
   }

   public DiagnosticReport processingFailureReport() {
      return diagnosticMapper.processingFailureReport();
   }

   private void logProcessingViolations( final List<Violation> violations ) {
      violations.stream()
            .filter( ProcessingViolation.class::isInstance )
            .map( ProcessingViolation.class::cast )
            .forEach( violation -> LOG.warn( "[validation] aspect model processing failed: {}", violation.message(), violation.cause() ) );
   }
}
