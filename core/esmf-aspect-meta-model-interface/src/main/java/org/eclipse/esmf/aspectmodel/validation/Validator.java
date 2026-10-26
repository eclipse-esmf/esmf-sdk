/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.validation;

import java.util.function.Supplier;

import org.eclipse.esmf.aspectmodel.ViolationReport;
import org.eclipse.esmf.metamodel.AspectModel;

import io.vavr.control.Either;
import io.vavr.control.Try;

/**
 * Generic validator for Aspect Models, either on the raw RDF input or on the already loaded Aspect
 * Model
 */
public interface Validator extends RdfBasedValidator, AspectModelBasedValidator {
   /**
    * Convenience function that takes an Aspect Model loading function as an input and returns the
    * resulting Aspect Model on success, or a validation report describing the loading failures on
    * failure
    *
    * @param aspectModelLoader the Aspect Model loading function
    * @return the validation report on failure ({@link Try.Failure}) or the Aspect Model on success
    *         ({@link Try.Success})
    */
   Either<ViolationReport, AspectModel> loadModel( Supplier<AspectModel> aspectModelLoader );

   /**
    * If {@link #loadModel(Supplier)} is called with a loading function that itself makes use of the
    * Validator, the loading function can short-circuit the loading-and-validation process and directly
    * return validation results by throwing the exception returned by this method.
    *
    * @param violations the results to return from the loading-and-validation process
    * @param <E> the type of exception that is thrown
    * @return the exception
    */
   <E extends RuntimeException> E cancelValidation( ViolationReport violations );

   /**
    * Validates an Aspect Model provided by a Supplier. This can be used to make the validator also
    * catch and handle loading and resolution errors, such as RDF/Turtle syntax errors or missing
    * references. In those cases, corresponding violations are created.
    *
    * @param aspectModelSupplier the Aspect Model supplier
    * @return a collection of problems. An empty collection indicates that the model is valid.
    */
   ViolationReport validateModel( Supplier<AspectModel> aspectModelSupplier );
}
