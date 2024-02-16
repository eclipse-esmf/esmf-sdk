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

import org.eclipse.esmf.aspectmodel.shacl.violation.DisjointViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#DisjointConstraintComponent">sh:disjoint</a>
 *
 * @param otherProperty the property of which the value must not match the given property value
 */
public record DisjointConstraint( Property otherProperty ) implements Constraint {
   @Override
   public boolean canBeUsedOnNodeShapes() {
      return false;
   }

   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      final RDFNode otherValue = context.element().getProperty( otherProperty ).getObject();
      return !rdfNode.equals( otherValue )
            ? List.of()
            : List.of( new DisjointViolation( context, otherProperty, otherValue ) );
   }

   @Override
   public String name() {
      return "sh:disjoint";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitDisjointConstraint( this );
   }
}
