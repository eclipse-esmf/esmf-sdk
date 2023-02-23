/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.validation.services;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.query.ARQ;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.InvalidRootElementCountException;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.aspectmodel.resolver.services.SammAspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.shacl.ShaclValidator;
import org.eclipse.esmf.aspectmodel.shacl.violation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
import org.eclipse.esmf.samm.KnownVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Try;

/**
 * Uses SHACL to validate an Aspect Model against the defined semantics of the Aspect Meta Model.
 */
public class AspectModelValidator {

   private static final Logger LOG = LoggerFactory.getLogger( AspectModelValidator.class );
   private final ShaclValidator shaclValidator;

   public AspectModelValidator() {
      this( KnownVersion.getLatest() );
   }

   public AspectModelValidator( final KnownVersion metaModelVersion ) {
      ARQ.init();
      shaclValidator = new ShaclValidator( new SammAspectMetaModelResourceResolver().loadShapesModel( metaModelVersion )
            .getOrElseThrow( () -> new RuntimeException( "Could not load meta model shapes" ) ) );
   }

   /**
    * Validates an Aspect Model that is provided as a {@link VersionedModel}.
    * @param versionedModel the Aspect Model
    * @return a list of {@link Violation}s. An empty list indicates that the model is valid.
    */
   public List<Violation> validateModel( final VersionedModel versionedModel ) {
      return validateModel( Try.success( versionedModel ) );
   }

   /**
    * Validates an Aspect Model that is provided as a {@link Try} of a {@link VersionedModel} that can
    * contain either a syntactically valid (but semantically invalid) Aspect model, or a Throwable
    * if an error during parsing or resolution occured.
    * @param versionedModel the Aspect Model or the corresonding error
    * @return a list of {@link Violation}s. An empty list indicates that the model is valid.
    */
   public List<Violation> validateModel( final Try<VersionedModel> versionedModel ) {
      if ( versionedModel.isFailure() ) {
         final Throwable cause = versionedModel.getCause();
         if ( cause instanceof ParserException exception ) {
            // RiotExeception's message looks like this:
            // [line: 17, col: 2 ] Triples not terminated by DOT
            final Pattern pattern = Pattern.compile( "\\[\s*line:\s*(\\d+),\s*col:\s*(\\d+)\s*]\s*(.*)" );
            final Matcher matcher = pattern.matcher( cause.getMessage() );
            if ( matcher.find() ) {
               final long line = Long.parseLong( matcher.group( 1 ) );
               final long column = Long.parseLong( matcher.group( 2 ) );
               final String message = matcher.group( 3 );
               return List.of( new InvalidSyntaxViolation( message, exception.getSourceDocument(), line, column ) );
            }
         }

         return List.of( new ProcessingViolation( versionedModel.getCause().getMessage(), versionedModel.getCause() ) );
      }
      // Determine violations for all model elements
      final VersionedModel model = versionedModel.get();
      final List<Violation> result = shaclValidator.validateModel( model );

      if ( result.isEmpty() ) {
         // The SHACL validation succeeded, check for cycles in the model.
         final List<Violation> cycleDetectionReport = new ModelCycleDetector().validateModel( model );
         if ( !cycleDetectionReport.isEmpty() ) {
            return cycleDetectionReport;
         }

         // To catch false positives, also try to load the model
         final Try<List<ModelElement>> modelElements = AspectModelLoader.getElements( model );
         if ( modelElements.isFailure() && !(modelElements.getCause() instanceof InvalidRootElementCountException) ) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            model.getModel().write( out, "ttl" );
            LOG.debug( out.toString( StandardCharsets.UTF_8 ) );
            return List.of( new ProcessingViolation(
                  "Validation succeeded, but an error was found while processing the model. "
                        + "This indicates an error in the model validation; please consider reporting this issue including the model "
                        + "at https://github.com/eclipse-esmf/esmf-semantic-aspect-meta-model/issues -- "
                        + buildCauseMessage( modelElements.getCause() ), modelElements.getCause() ) );
         }
      }

      return result;
   }

   /**
    * Validates a single model element.
    * @param element the aspect model element to validate. The element MUST be part of the fully resolved model (i.e., element.getModel() should return
    *                the same value as versionedModel.getModel())
    * @return the list of violations
    */
   public List<Violation> validateElement( final Resource element ) {
      return shaclValidator.validateElement( element );
   }

   private static String buildCauseMessage( final Throwable throwable ) {
      final StringBuilder builder = new StringBuilder();
      Throwable t = throwable;
      while ( t != null ) {
         builder.append( t.getMessage() );
         t = t.getCause();
         if ( t != null ) {
            builder.append( ": " );
         }
      }
      return builder.toString();
   }

   private String getValidationResultField( final Resource validationResultResource, final Property property ) {
      return Optional.ofNullable( validationResultResource.getProperty( property ) )
            .map( Statement::getObject )
            .map( RDFNode::toString )
            .orElse( "" );
   }
}
