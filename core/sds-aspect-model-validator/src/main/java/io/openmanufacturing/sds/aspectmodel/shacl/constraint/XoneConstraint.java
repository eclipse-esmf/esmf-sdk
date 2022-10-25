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

import java.util.Collection;
import java.util.List;

import org.apache.jena.rdf.model.RDFNode;

import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#XoneConstraintComponent">sh:xone</a>
 */
public record XoneConstraint(List<Constraint> constraints) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      final List<List<Violation>> violationsPerConstraint = constraints.stream().map( constraint -> constraint.apply( rdfNode, context ) ).toList();
      final long numberOfEmptyViolationLists = violationsPerConstraint.stream().filter( List::isEmpty ).count();
      // The xone constraint is evaluated successfully if exactly one of the provided constraints evaluates successfully
      if ( numberOfEmptyViolationLists == 1 ) {
         return List.of();
      }
      return violationsPerConstraint.stream().flatMap( Collection::stream ).toList();
   }

   @Override
   public String name() {
      return "sh:xone";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitXoneConstraint( this );
   }
}
