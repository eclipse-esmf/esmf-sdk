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

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;

import io.openmanufacturing.sds.aspectmodel.shacl.LiteralComparator;
import io.openmanufacturing.sds.aspectmodel.shacl.Shape;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.DatatypeViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MaxInclusiveViolation;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#MaxInclusiveConstraintComponent">sh:maxInclusive</a>
 * @param maxValue the max value
 */
public record MaxInclusiveConstraint(Literal maxValue) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      final List<Violation> nodeKindViolations = new NodeKindConstraint( Shape.NodeKind.Literal ).apply( rdfNode, context );
      if ( !nodeKindViolations.isEmpty() ) {
         return nodeKindViolations;
      }

      final Literal actual = rdfNode.asLiteral();
      try {
         return new LiteralComparator().compare( maxValue, actual ) >= 0 ?
               List.of() :
               List.of( new MaxInclusiveViolation( context, maxValue, actual ) );
      } catch ( final ClassCastException exception ) {
         return List.of( new DatatypeViolation( context, maxValue.getDatatypeURI(), actual.getDatatypeURI() ) );
      }
   }

   @Override
   public String name() {
      return "sh:maxInclusive";
   }
}