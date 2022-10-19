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
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.RDFNode;

import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#AndConstraintComponent">sh:and</a>
 */
public record AndConstraint(List<Constraint> constraints) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      return constraints().stream().flatMap( constraint -> constraint.apply( rdfNode, context ).stream() ).collect( Collectors.toList() );
   }

   @Override
   public String name() {
      return "sh:and";
   }
}
