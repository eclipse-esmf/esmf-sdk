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
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.eclipse.esmf.aspectmodel.shacl.LiteralComparator;
import org.eclipse.esmf.aspectmodel.shacl.Shape;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import org.eclipse.esmf.aspectmodel.shacl.violation.LessThanOrEqualsViolation;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#LessThanOrEqualsConstraintComponent">sh:lessThanOrEquals</a>
 * @param otherProperty the other property to compare with
 */
public record LessThanOrEqualsConstraint(Property otherProperty) implements Constraint {
   @Override
   public boolean canBeUsedOnNodeShapes() {
      return false;
   }

   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      final List<Violation> nodeKindViolations = new NodeKindConstraint( Shape.NodeKind.Literal ).apply( rdfNode, context );
      if ( !nodeKindViolations.isEmpty() ) {
         return nodeKindViolations;
      }

      final Literal actual = rdfNode.asLiteral();
      final RDFNode otherValueNode = context.element().getProperty( otherProperty ).getObject();
      final List<Violation> otherValueNodeKindViolations = new NodeKindConstraint( Shape.NodeKind.Literal ).apply( rdfNode,
            context.withProperty( otherProperty ) );
      if ( !otherValueNodeKindViolations.isEmpty() ) {
         return otherValueNodeKindViolations;
      }

      final Literal otherValue = otherValueNode.asLiteral();
      return new LiteralComparator().compare( actual, otherValue ) <= 0 ?
            List.of() :
            List.of( new LessThanOrEqualsViolation( context, otherProperty, otherValue, actual ) );
   }

   @Override
   public String name() {
      return "sh:lessThanOrEquals";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitLessThanOrEqualsConstraint( this );
   }
}
