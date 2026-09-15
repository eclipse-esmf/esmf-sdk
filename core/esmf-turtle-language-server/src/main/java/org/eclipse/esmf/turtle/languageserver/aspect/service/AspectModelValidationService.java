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

import org.eclipse.esmf.aspectmodel.ViolationReport;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.validation.Validator;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.ResolutionStrategyAwareViolationProvider;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Either;

public class AspectModelValidationService extends TurtleService implements ResolutionStrategyAwareViolationProvider {
   private static final Logger LOG = LoggerFactory.getLogger( AspectModelValidationService.class );
   private final Validator validator;
   private ResolutionStrategyService resolutionStrategyService;

   public AspectModelValidationService() {
      this( new AspectModelValidator(), new ResolutionStrategyService() );
   }

   public AspectModelValidationService( final Validator validator ) {
      this( validator, new ResolutionStrategyService() );
   }

   public AspectModelValidationService( final Validator validator,
         final ResolutionStrategyService resolutionStrategyService ) {
      this.validator = validator;
      this.resolutionStrategyService = resolutionStrategyService;
   }

   @Override
   public ViolationReport validate( final ParsedDocument parsedDocument ) {
      if ( !documentIsAspectModel( parsedDocument ) || isExemptFromValidation( parsedDocument ) ) {
         return ViolationReport.EMPTY;
      }
      LOG.debug( "[load] loading aspect model from {}", parsedDocument.getUri() );
      final Either<ViolationReport, AspectModel> reportOrModel = validator.loadModel( () -> {
         final AspectModelLoader aspectModelLoader = parsedDocument.getUri().getScheme() == null
               ? new AspectModelLoader()
               : new AspectModelLoader( resolutionStrategyService.buildResolutionStrategyForDocument( parsedDocument ) );
         return aspectModelLoader.load( AspectModelFileLoader.load( parsedDocument.turtleSyntaxTree(), parsedDocument.getUri() ) );
      } );
      if ( reportOrModel.isLeft() ) {
         final ViolationReport report = reportOrModel.getLeft();
         logProcessingViolations( report );
         return report;
      }
      return ViolationReport.EMPTY;
   }

   private void logProcessingViolations( final ViolationReport violations ) {
      violations.violations().stream()
            .filter( ProcessingViolation.class::isInstance )
            .map( ProcessingViolation.class::cast )
            .forEach( violation -> LOG.warn( "[validation] aspect model processing failed: {}", violation.message(), violation.cause() ) );
   }

   @Override
   public Type type() {
      return Type.DELAYED;
   }

   @Override
   public void setResolutionStrategyService( final ResolutionStrategyService resolutionStrategyService ) {
      this.resolutionStrategyService = resolutionStrategyService;
   }
}
