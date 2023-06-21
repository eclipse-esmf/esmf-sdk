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

package org.eclipse.esmf.aspectmodel.shacl.constraint;

import java.util.List;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;

import org.eclipse.esmf.aspectmodel.shacl.LiteralComparator;
import org.eclipse.esmf.aspectmodel.shacl.Shape;
import org.eclipse.esmf.aspectmodel.shacl.violation.DatatypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.MaxExclusiveViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#MaxExclusiveConstraintComponent">sh:maxExclusive</a>
 *
 * @param maxValue the max value
 */
public record MaxExclusiveConstraint( Literal maxValue ) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      final List<Violation> nodeKindViolations = new NodeKindConstraint( Shape.NodeKind.Literal ).apply( rdfNode, context );
      if ( !nodeKindViolations.isEmpty() ) {
         return nodeKindViolations;
      }

      final Literal actual = rdfNode.asLiteral();
      try {
         return new LiteralComparator().compare( maxValue, actual ) > 0 ?
               List.of() :
               List.of( new MaxExclusiveViolation( context, maxValue, actual ) );
      } catch ( final ClassCastException exception ) {
         return List.of( new DatatypeViolation( context, maxValue.getDatatypeURI(), actual.getDatatypeURI() ) );
      }
   }

   @Override
   public String name() {
      return "sh:maxExclusive";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitMaxExclusiveConstraint( this );
   }
}
