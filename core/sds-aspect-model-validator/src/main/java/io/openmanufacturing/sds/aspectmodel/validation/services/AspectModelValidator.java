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

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.jena.query.ARQ;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.RiotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.validation.ValidationEngineFactory;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.shacl.vocabulary.SH;

import com.google.common.collect.ImmutableList;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.UnsupportedVersionException;
import io.openmanufacturing.sds.aspectmodel.resolver.ModelResolutionException;
import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.InvalidVersionException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.shacl.ShaclValidator;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationError;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationReport;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationReportBuilder;
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
      ARQ.init();
      aspectMetaModelResourceResolver = new SdsAspectMetaModelResourceResolver();
      ValidationEngineFactory.set( new BammValidationEngineFactory() );
      shaclValidator = new ShaclValidator( aspectMetaModelResourceResolver.loadShapesModel( KnownVersion.getLatest() )
            .getOrElseThrow( () -> new RuntimeException( "Could not load meta model shapes" ) ) );
   }

   /**
    * Validates an Aspect Model that is provided as a {@link Try} of a {@link VersionedModel} that can
    * contain either a syntactically valid (but semantically invalid) Aspect model, or a
    * {@link RiotException} if a parser error occured.
    *
    * @param versionedModel The Aspect Model or the corresonding parser error
    * @return Either a {@link ValidationReport.ValidReport} if the model is syntactically correct and conforms to the
    *       Aspect Meta Model semantics or a {@link ValidationReport.InvalidReport} that provides a number of
    *       {@link ValidationError}s that describe all validation violations.
    */
   public ValidationReport validate( final Try<VersionedModel> versionedModel ) {
      return versionedModel.flatMap( model -> {
         final Model dataModel = model.getModel();

         final Try<KnownVersion> metaModelVersion = KnownVersion
               .fromVersionString( model.getVersion().toString() ).map( Try::success )
               .orElse( Try.failure( new UnsupportedVersionException( model.getVersion() ) ) );

         return metaModelVersion
               .flatMap( aspectMetaModelResourceResolver::loadShapesModel ).map( shapesModel -> {
                  final Resource report = ValidationUtil.validateModel( dataModel, shapesModel, false );

                  if ( report.getProperty( SH.conforms ).getObject().asLiteral().getBoolean() ) {
                     return new ValidationReportBuilder().buildValidReport();
                  }

                  return new ValidationReportBuilder().withValidationErrors( buildSemanticValidationErrors(
                        report ) ).buildInvalidReport();
               } );
      } ).recover( failure -> Match( failure ).of(
            riotExceptionHandler, invalidVersionExceptionHandler, unsupportedVersionExceptionHandler,
            modelResolutionExceptionHandler )
      ).get();
   }

   /**
    * Validates a single model element
    * @param element the aspect model element to validate
    * @return the list of violations
    */
   public List<Violation> validateElement( final Resource element ) {
      return shaclValidator.validateElement( element );
   }

   public List<Violation> validateElements( final List<Resource> elements ) {
      return shaclValidator.validateElements( elements );
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
}
