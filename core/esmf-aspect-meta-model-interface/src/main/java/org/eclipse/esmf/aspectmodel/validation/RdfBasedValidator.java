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

import org.apache.jena.rdf.model.Model;

import org.eclipse.esmf.annotations.InterfaceVersion;

import org.eclipse.esmf.aspectmodel.ViolationReport;

/**
 * Generic validator for Aspect Models on the raw RDF input
 */
@InterfaceVersion( version = 1 )
public interface RdfBasedValidator {
   /**
    * Validates a complete RDF input (i.e., this model is expected to contain all necessary
    * definitions, including meta model definitions)
    *
    * @param model the input model
    * @return the validation report
    */
   ViolationReport validateModel( Model model );
}
