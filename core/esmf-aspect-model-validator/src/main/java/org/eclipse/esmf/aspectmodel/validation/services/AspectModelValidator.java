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

import java.io.File;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.aspectmodel.shacl.ShaclValidator;
import org.eclipse.esmf.aspectmodel.shacl.violation.InvalidSyntaxViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.ProcessingViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.metamodel.AspectModel;

import io.vavr.control.Either;
import org.apache.jena.query.ARQ;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

/**
 * Uses SHACL to validate an Aspect Model against the defined semantics of the Aspect Meta Model.
 */
public class AspectModelValidator {
   private final ShaclValidator shaclValidator;

   /**
    * Default constructor that will use the latest meta model version
    */
   public AspectModelValidator() {
      ARQ.init();
      shaclValidator = new ShaclValidator( MetaModelFile.metaModelShapes() );
   }

   /**
    * Validates an Aspect Model provided by a Supplier. This can be used to make the validator also catch and handle
    * loading and resolution errors, such as RDF/Turtle syntax errors or missing references. In those cases, corresponding
    * violations such as {@link InvalidSyntaxViolation} and {@link ProcessingViolation} are created.
    *
    * @param aspectModelSupplier the Aspect Model supplier
    * @return a list of {@link Violation}s. An empty list indicates that the model is valid.
    */
   public List<Violation> validateModel( final Supplier<AspectModel> aspectModelSupplier ) {
      final Either<List<Violation>, AspectModel> result = loadModel( aspectModelSupplier );
      if ( result.isLeft() ) {
         return result.getLeft();
      }
      return validateModel( result.get() );
   }

   /**
    * Loads an Aspect Model provided by a Supplier and returns either the sucessfully loaded model or the list of
    * violations representing resolution failures, syntax errors etc.. This method does <i>not</i> perform semantic validation.
    * This method is an alternative to {@link AspectModelLoader#load(File)} and its siblings with the difference that on errors
    * (syntax errors, resolution errors), no exception is thrown and a {@link Violation} is created instead.
    *
    * @param aspectModelSupplier the Aspect Model supplier
    * @return An {@link Either.Right} with the model if there are no violations, or an {@link Either.Left} with a list of
    * {@link Violation}s.
    */
   public Either<List<Violation>, AspectModel> loadModel( final Supplier<AspectModel> aspectModelSupplier ) {
      final AspectModel model;
      try {
         model = aspectModelSupplier.get();
         return Either.right( model );
      } catch ( final ParserException exception ) { // Syntax error
         return Either.left( List.of(
               new InvalidSyntaxViolation( exception.getMessage(), exception.getSourceDocument(), exception.getLine(),
                     exception.getColumn() ) ) );
      } catch ( final Exception exception ) { // Any other exception, e.g., resolution exception
         return Either.left( List.of( new ProcessingViolation( exception.getMessage(), exception ) ) );
      }
   }

   /**
    * Validates an Aspect Model.
    *
    * @param aspectModel the Aspect Model
    * @return a list of {@link Violation}s. An empty list indicates that the model is valid.
    */
   public List<Violation> validateModel( final AspectModel aspectModel ) {
      final Model model = ModelFactory.createDefaultModel();
      model.add( aspectModel.mergedModel() );
      model.add( MetaModelFile.metaModelDefinitions() );
      final List<Violation> result = validateModel( model );

      if ( result.isEmpty() ) {
         // The SHACL validation succeeded, check for cycles in the model.
         final List<Violation> cycleDetectionReport = new ModelCycleDetector().validateModel( aspectModel.mergedModel() );
         if ( !cycleDetectionReport.isEmpty() ) {
            return cycleDetectionReport;
         }
      }
      return result;
   }

   /**
    * Validates an Aspect Model. Note that the model needs to include the SAMM meta model definitions to yield correct validation results.
    *
    * @param model the Aspect Model
    * @return a list of {@link Violation}s. An empty list indicates that the model is valid.
    */
   public List<Violation> validateModel( final Model model ) {
      return shaclValidator.validateModel( model );
   }

   /**
    * Validates a single model element.
    *
    * @param element the aspect model element to validate. The element MUST be part of the fully resolved model (i.e., element.getModel()
    * should return the same value as versionedModel.getModel())
    * @return the list of violations
    */
   public List<Violation> validateElement( final Resource element ) {
      return shaclValidator.validateElement( element );
   }
}
