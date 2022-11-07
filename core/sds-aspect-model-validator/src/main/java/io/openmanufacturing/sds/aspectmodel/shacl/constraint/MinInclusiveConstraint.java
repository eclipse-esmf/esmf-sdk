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
import io.openmanufacturing.sds.aspectmodel.shacl.violation.DatatypeViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MinInclusiveViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#MinInclusiveConstraintComponent">sh:minInclusive</a>
 * @param minValue the min value
 */
public record MinInclusiveConstraint(Literal minValue) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      final List<Violation> nodeKindViolations = new NodeKindConstraint( Shape.NodeKind.Literal ).apply( rdfNode, context );
      if ( !nodeKindViolations.isEmpty() ) {
         return nodeKindViolations;
      }

      final Literal actual = rdfNode.asLiteral();
      try {
         return new LiteralComparator().compare( minValue, actual ) <= 0 ?
               List.of() :
               List.of( new MinInclusiveViolation( context, minValue, actual ) );
      } catch ( final ClassCastException exception ) {
         return List.of( new DatatypeViolation( context, minValue.getDatatypeURI(), actual.getDatatypeURI() ) );
      }
   }

   @Override
   public String name() {
      return "sh:minInclusive";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitMinInclusiveConstraint( this );
   }
}
