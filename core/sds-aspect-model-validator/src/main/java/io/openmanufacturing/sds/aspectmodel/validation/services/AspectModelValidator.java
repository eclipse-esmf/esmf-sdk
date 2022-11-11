/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.validation.services;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.query.ARQ;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RiotException;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.validation.ValidationEngineFactory;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.shacl.vocabulary.SH;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.UnsupportedVersionException;
import io.openmanufacturing.sds.aspectmodel.resolver.ModelResolutionException;
import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.InvalidNamespaceException;
import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.InvalidRootElementCountException;
import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.InvalidVersionException;
import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.ParserException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.shacl.ShaclValidator;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.InvalidSyntaxViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.ProcessingViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationError;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationReport;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationReportBuilder;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import io.vavr.control.Try;

/**
 * Uses SHACL to validate an Aspect Model against the defined semantics of the Aspect Meta Model.
 */
public class AspectModelValidator {

   private static final Logger LOG = LoggerFactory.getLogger( AspectModelValidator.class );
   private final SdsAspectMetaModelResourceResolver aspectMetaModelResourceResolver;
   private final ShaclValidator shaclValidator;

   private final Match.Case<RiotException, ValidationReport.InvalidReport> riotExceptionHandler =
         Case( $( instanceOf( RiotException.class ) ), riotException -> {
            LOG.warn( ValidationError.MESSAGE_SYNTAX_ERROR, riotException );
            return new ValidationReportBuilder()
                  .withValidationErrors( ImmutableList.of( new ValidationError.Syntactic( riotException ) ) )
                  .buildInvalidReport();
         } );

   private final Match.Case<InvalidVersionException, ValidationReport.InvalidReport> invalidVersionExceptionHandler =
         Case( $( instanceOf( InvalidVersionException.class ) ), invalidVersionException -> {
            LOG.warn( ValidationError.MESSAGE_COULD_NOT_RETRIEVE_BAMM_VERSION, invalidVersionException );
            return new ValidationReportBuilder()
                  .withValidationErrors( ImmutableList.of( new ValidationError.Processing(
                        ValidationError.MESSAGE_COULD_NOT_RETRIEVE_BAMM_VERSION ) ) )
                  .buildInvalidReport();
         } );

   private final Match.Case<UnsupportedVersionException, ValidationReport.InvalidReport> unsupportedVersionExceptionHandler =
         Case( $( instanceOf( UnsupportedVersionException.class ) ), unsupportedVersionException -> {
            LOG.warn( ValidationError.MESSAGE_BAMM_VERSION_NOT_SUPPORTED, unsupportedVersionException );
            return new ValidationReportBuilder()
                  .withValidationErrors( ImmutableList.of( new ValidationError.Processing(
                        ValidationError.MESSAGE_BAMM_VERSION_NOT_SUPPORTED ) ) )
                  .buildInvalidReport();
         } );

   private final Match.Case<ModelResolutionException, ValidationReport.InvalidReport> modelResolutionExceptionHandler =
         Case( $( instanceOf( ModelResolutionException.class ) ), modelResolutionException -> {
            String message = ValidationError.MESSAGE_MODEL_RESOLUTION_ERROR +
                  (modelResolutionException.getCause() instanceof FileNotFoundException ?
                        ": " + modelResolutionException.getCause().getMessage() : "");
            LOG.warn( message, modelResolutionException );
            return new ValidationReportBuilder()
                  .withValidationErrors( ImmutableList.of( new ValidationError.Processing( message ) ) )
                  .buildInvalidReport();
         } );

   public AspectModelValidator() {
      this( KnownVersion.getLatest() );
   }

   public AspectModelValidator( final KnownVersion metaModelVersion ) {
      ARQ.init();
      aspectMetaModelResourceResolver = new SdsAspectMetaModelResourceResolver();
      ValidationEngineFactory.set( new BammValidationEngineFactory() );
      shaclValidator = new ShaclValidator( aspectMetaModelResourceResolver.loadShapesModel( metaModelVersion )
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
    * if an error during parsing or resolution occurred.
    * @param versionedModel the Aspect Model or the corresponding error
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
      final List<Violation> result = Streams.stream( model.getRawModel().listStatements( null, RDF.type, (RDFNode) null ) )
            .map( Statement::getSubject )
            .filter( Resource::isURIResource )
            .map( Resource::getURI )
            .map( uri -> model.getModel().createResource( uri ) )
            .flatMap( element -> shaclValidator.validateElement( element ).stream() )
            .toList();

      if ( result.isEmpty() ) {
         // The SHACL validation succeeded, check for cycles in the model.
         final List<Violation> cycleDetectionReport = new ModelCycleDetector().validateModel( model );
         if ( !cycleDetectionReport.isEmpty() ) {
            return cycleDetectionReport;
         }

         // To catch false positives, also try to load the model
         final Try<Aspect> aspects = AspectModelLoader.fromVersionedModel( model );
         if ( aspects.isFailure() && !(aspects.getCause() instanceof InvalidRootElementCountException) ) {
            return List.of( new ProcessingViolation(
                  "Validation succeeded, but an error was found while processing the model. "
                        + "This indicates an error in the model validation; please consider reporting this issue including the model "
                        + "at https://github.com/OpenManufacturingPlatform/sds-bamm-aspect-meta-model/issues -- "
                        + buildCauseMessage( aspects.getCause() ), aspects.getCause() ) );
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

   /**
    * Validates an Aspect Model that is provided as a {@link Try} of a {@link VersionedModel} that can
    * contain either a syntactically valid (but semantically invalid) Aspect model, or a
    * {@link RiotException} if a parser error occurred.
    *
    * @param versionedModel The Aspect Model or the corresponding parser error
    * @return Either a {@link ValidationReport.ValidReport} if the model is syntactically correct and conforms to the
    *       Aspect Meta Model semantics or a {@link ValidationReport.InvalidReport} that provides a number of
    *       {@link ValidationError}s that describe all validation violations.
    * @deprecated use {@link #validateModel(VersionedModel)} or {@link #validateModel(Try)} instead
    */
   @Deprecated( forRemoval = true )
   public ValidationReport validate( final Try<VersionedModel> versionedModel ) {
      return versionedModel.flatMap( model -> {
         final Model dataModel = model.getModel();

         final Try<KnownVersion> metaModelVersion = KnownVersion
               .fromVersionString( model.getVersion().toString() )
               .map( Try::success )
               .orElse( Try.failure( new UnsupportedVersionException( model.getVersion() ) ) );

         return metaModelVersion
               .flatMap( aspectMetaModelResourceResolver::loadShapesModel ).map( shapesModel -> {
                  final Resource report = ValidationUtil.validateModel( dataModel, shapesModel, false );

                  if ( report.getProperty( SH.conforms ).getObject().asLiteral().getBoolean() ) {
                     // The SHACL validation succeeded. But to catch false positives, also try to load the model
                     final Try<Aspect> aspects = AspectModelLoader.fromVersionedModel( model );
                     if ( aspects.isFailure() && !(aspects.getCause() instanceof InvalidRootElementCountException) ) {
                        return getInvalidReport( aspects.getCause() );
                     }
                     return new ValidationReportBuilder().buildValidReport();
                  }

                  return new ValidationReportBuilder()
                        .withValidationErrors( buildSemanticValidationErrors( report ) )
                        .buildInvalidReport();
               } );
      } ).recover( failure -> Match( failure ).of(
            riotExceptionHandler, invalidVersionExceptionHandler, unsupportedVersionExceptionHandler,
            modelResolutionExceptionHandler )
      ).get();
   }

   private static ValidationReport getInvalidReport( final Throwable cause ) {
      final String errorMessage;

      if ( cause instanceof InvalidNamespaceException ) {
         errorMessage = buildCauseMessage( cause );
      } else {
         errorMessage = "Validation succeeded, but an error was found while processing the model. "
               + "This indicates an error in the model validation; please consider reporting this issue including the model "
               + "at https://github.com/OpenManufacturingPlatform/sds-bamm-aspect-meta-model/issues -- "
               + buildCauseMessage( cause );
      }

      return new ValidationReportBuilder()
            .withValidationErrors( List.of( new ValidationError.Processing( errorMessage ) ) )
            .buildInvalidReport();
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

   private Collection<ValidationError.Semantic> buildSemanticValidationErrors( final Resource report ) {
      final Collection<ValidationError.Semantic> semanticValidationErrors = new ArrayList<>();
      for ( final NodeIterator it = report.getModel().listObjectsOfProperty( report, SH.result ); it.hasNext(); ) {
         final Resource validationResultResource = it.next().asResource();
         final String resultMessage = getValidationResultField( validationResultResource, SH.resultMessage );
         final String focusNode = getValidationResultField( validationResultResource, SH.focusNode );
         final String resultPath = getValidationResultField( validationResultResource, SH.resultPath );
         final String resultSeverity = getValidationResultField( validationResultResource, SH.resultSeverity );
         final String value = getValidationResultField( validationResultResource, SH.value );
         semanticValidationErrors.add( new ValidationError.Semantic( resultMessage, focusNode,
               resultPath, resultSeverity, value ) );
      }
      return semanticValidationErrors;
   }

   private Collection<ValidationError.Processing> buildLoadingFailureError( final String errorMessage ) {
      return List.of( new ValidationError.Processing( errorMessage ) );
   }
}
