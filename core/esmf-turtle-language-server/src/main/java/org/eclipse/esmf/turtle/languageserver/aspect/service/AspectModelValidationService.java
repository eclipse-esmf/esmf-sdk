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

import java.net.URI;
import java.util.List;

import org.eclipse.esmf.aspectmodel.ValueParsingException;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.validation.Validator;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectViolationDiagnosticMapper;
import org.eclipse.esmf.turtle.languageserver.aspect.navigation.ExternalModelFileCache;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.ResolutionStrategyAwareDiagnosticsProvider;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleService;

import org.apache.jena.riot.RiotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspectModelValidationService extends TurtleService implements ResolutionStrategyAwareDiagnosticsProvider {
   private static final Logger LOG = LoggerFactory.getLogger( AspectModelValidationService.class );
   private final Validator<Violation, List<Violation>> validator;
   private final AspectViolationDiagnosticMapper diagnosticMapper = new AspectViolationDiagnosticMapper();
   private ResolutionStrategyService resolutionStrategyService;

   public AspectModelValidationService() {
      this( new AspectModelValidator(), new ResolutionStrategyService() );
   }

   public AspectModelValidationService( final Validator<Violation, List<Violation>> validator ) {
      this( validator, new ResolutionStrategyService() );
   }

   public AspectModelValidationService( final Validator<Violation, List<Violation>> validator,
         final ResolutionStrategyService resolutionStrategyService ) {
      this.validator = validator;
      this.resolutionStrategyService = resolutionStrategyService;
   }

   private boolean shouldValidateDocument( final ParsedDocument parsedDocument ) {
      return documentIsAspectModel( parsedDocument ) && !ExternalModelFileCache.isCachedModelUri( parsedDocument.getUri() );
   }

   @Override
   public DiagnosticReport validate( final ParsedDocument parsedDocument ) {
      if ( !shouldValidateDocument( parsedDocument ) ) {
         return DiagnosticReport.EMPTY;
      }
      try {
         LOG.debug( "[load] loading aspect model from {}", parsedDocument.getUri() );
         final RawAspectModelFile file =
               AspectModelFileLoader.load( parsedDocument.turtleSyntaxTree(), URI.create( parsedDocument.getUri() ) );
         final List<Violation> violations = validate( file, parsedDocument );
         logProcessingViolations( violations );
         return diagnosticMapper.mapValidationViolations( violations );
      } catch ( final RiotException _ ) {
         // Tree-sitter owns ordinary syntax diagnostics. ParserException below keeps Jena-only syntax
         // fallback visible.
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

   private void logProcessingViolations( final List<Violation> violations ) {
      violations.stream()
            .filter( ProcessingViolation.class::isInstance )
            .map( ProcessingViolation.class::cast )
            .forEach( violation -> LOG.warn( "[validation] aspect model processing failed: {}", violation.message(), violation.cause() ) );
   }

   private List<Violation> validate( final RawAspectModelFile file, final ParsedDocument parsedDocument ) {
      final AspectModelLoader documentLoader = loaderFor( parsedDocument );
      final List<Violation> violations = validate( file, documentLoader );
      LOG.debug( "[validate] validation finished for {} with {} violation(s)", parsedDocument.getUri(), violations.size() );
      return violations;
   }

   @Override
   public Type type() {
      return Type.DELAYED;
   }

   private AspectModelLoader loaderFor( final ParsedDocument parsedDocument ) {
      final URI documentUri = URI.create( parsedDocument.getUri() );
      return documentUri.getScheme() == null
            ? new AspectModelLoader()
            : new AspectModelLoader( resolutionStrategyService.buildResolutionStrategyForDocument( parsedDocument ) );
   }

   private List<Violation> validate( final RawAspectModelFile file, final AspectModelLoader modelLoader ) {
      return validator.validateModel( () -> modelLoader.loadRawAspectModelFile( file ) );
   }

   @Override
   public void setResolutionStrategyService( final ResolutionStrategyService resolutionStrategyService ) {
      this.resolutionStrategyService = resolutionStrategyService;
   }
}
