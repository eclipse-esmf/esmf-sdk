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

import org.apache.jena.rdf.model.Model;

/**
 * Generic validator for Aspect Models on the raw RDF input
 *
 * @param <P> the "problem" type that describes loading or validation failures
 * @param <C> the "collection of problem" type that constitutes a validation report
 */
public interface RdfBasedValidator<P, C extends Collection<? super P>> {
   /**
    * Validates a complete RDF input (i.e., this model is expected to contain all necessary definitions, including meta model definitions)
    *
    * @param model the input model
    * @return the validation report
    */
   C validateModel( Model model );
}
