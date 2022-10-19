/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.shacl.constraint;

import java.util.List;
import java.util.function.BiFunction;

import org.apache.jena.rdf.model.RDFNode;

import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;

/**
 * Represents a SHACL constraint component as a function that takes the <a href="https://www.w3.org/TR/shacl/#value-nodes">value node</a> as input
 * and returns a (possibly empty) list of violations.
 */
public interface Constraint extends BiFunction<RDFNode, EvaluationContext, List<Violation>> {
   default boolean canBeUsedOnNodeShapes() {
      return true;
   }

   String name();
}

// not

// and

// or

// xone

// sh:qualifiedValueShape, sh:qualifiedMinCount, sh:qualifiedMaxCount

