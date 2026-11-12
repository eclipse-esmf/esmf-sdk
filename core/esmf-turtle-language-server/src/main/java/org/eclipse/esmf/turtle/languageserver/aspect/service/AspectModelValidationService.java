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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.Location;
import org.eclipse.esmf.aspectmodel.Violation;
import org.eclipse.esmf.aspectmodel.ViolationReport;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.resolver.ModelResolutionViolation;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.parser.SmartToken;
import org.eclipse.esmf.aspectmodel.resolver.parser.TokenRegistry;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.validation.DefaultDocumentLocationViolationBuilder;
import org.eclipse.esmf.aspectmodel.validation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.validation.Validator;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.ResolutionStrategyAwareViolationProvider;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
      final ViolationReport violationReport = validator.validateModel( () -> {
         final AspectModelLoader aspectModelLoader = parsedDocument.getUri().getScheme() == null
               ? new AspectModelLoader()
               : new AspectModelLoader( resolutionStrategyService.buildResolutionStrategyForDocument( parsedDocument ) );
         final AspectModelFile file = AspectModelFileLoader.load( parsedDocument.turtleSyntaxTree(), parsedDocument.getUri() );
         try {
            return aspectModelLoader.load( file );
         } catch ( final ModelResolutionException exception ) {
            if ( exception.getCheckedLocations().isEmpty() ) {
               throw exception;
            }
            throw validator.cancelValidation( resolutionFailureReport( exception, file ) );
         }
      } );
      logProcessingViolations( violationReport );
      return violationReport;
   }

   private ViolationReport resolutionFailureReport( final ModelResolutionException exception, final AspectModelFile file ) {
      final Set<AspectModelUrn> reportedElements = new HashSet<>();
      final List<Violation> violations = new ArrayList<>();
      for ( final ModelResolutionViolation failure : exception.getCheckedLocations() ) {
         if ( failure.element().isEmpty() ) {
            violations.add( failure );
         } else if ( reportedElements.add( failure.element().get() ) ) {
            violations.addAll( unresolvedReferenceViolations( failure, file ) );
         }
      }
      return new ViolationReport( violations );
   }

   private List<Violation> unresolvedReferenceViolations( final ModelResolutionViolation violation, final AspectModelFile file ) {
      final AspectModelUrn urn = violation.element().get();
      final String message = "Reference '%s' could not be resolved.".formatted( urn.getName() );
      final List<Violation> violations = referenceLocations( urn, file )
            .<Violation>map( location -> DefaultDocumentLocationViolationBuilder.builder()
                  .message( message )
                  .code( violation.code() )
                  .severity( violation.severity() )
                  .sourceDocument( file.sourceUri() )
                  .documentContent( file::sourceRepresentation )
                  .location( location )
                  .build() )
            .toList();
      return violations.isEmpty()
            ? List.of( new ModelResolutionViolation( violation.element(), violation.location(), message, violation.cause() ) )
            : violations;
   }

   private Stream<Location> referenceLocations( final AspectModelUrn urn, final AspectModelFile file ) {
      return file.sourceModel().getGraph().stream()
            .flatMap( triple -> Stream.of( triple.getSubject(), triple.getPredicate(), triple.getObject() ) )
            .filter( node -> node.isURI() && node.getURI().equals( urn.toString() ) )
            .flatMap( node -> TokenRegistry.getToken( node ).stream() )
            .filter( token -> file.sourceUri().equals( token.getSourceDocument() ) )
            .map( SmartToken::location )
            .distinct();
   }

   private void logProcessingViolations( final ViolationReport violations ) {
      violations.violations().stream()
            .filter( ProcessingViolation.class::isInstance )
            .map( ProcessingViolation.class::cast )
            .forEach( violation -> LOG.warn( "[validation] aspect model processing failed: {}, {}",
                  violation.message(), violation.cause() ) );
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
