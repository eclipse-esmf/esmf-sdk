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

import java.util.Collection;
import java.util.function.Supplier;

import org.eclipse.esmf.metamodel.AspectModel;

import io.vavr.control.Either;
import io.vavr.control.Try;

/**
 * Generic validator for Aspect Models, either on the raw RDF input or on the already loaded Aspect Model
 *
 * @param <P> the "problem" type that describes loading or validation failures
 * @param <C> the "collection of problem" type that constitutes a validation report
 */
public interface Validator<P, C extends Collection<? super P>> extends RdfBasedValidator<P, C>, AspectModelBasedValidator<P, C> {
   /**
    * Convenience function that takes an Aspect Model loading function as an input and returns the resulting Aspect Model on success,
    * or a validation report describing the loading failures on failure
    *
    * @param aspectModelLoader the Aspect Model loading function
    * @return the validation report on failure ({@link Try.Failure}) or the Aspect Model on success ({@link Try.Success})
    */
   Either<C, AspectModel> loadModel( Supplier<AspectModel> aspectModelLoader );

   /**
    * If {@link #loadModel(Supplier)} is called with a loading function that itself makes use of the Validator, the loading function can
    * short-circuit the loading-and-validation process and directly return validation results by throwing the exception returned by this
    * method.
    *
    * @param violations the results to return from the loading-and-validation process
    * @param <E> the type of exception that is thrown
    * @return the exception
    */
   <E extends RuntimeException> E cancelValidation( C violations );
}
