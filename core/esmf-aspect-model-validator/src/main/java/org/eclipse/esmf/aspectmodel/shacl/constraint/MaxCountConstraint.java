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

import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.MaxCountViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#MaxCountConstraintComponent">sh:maxCount</a>
 *
 * @param maxCount the max count
 */
public record MaxCountConstraint( int maxCount ) implements Constraint {
   @Override
   public boolean canBeUsedOnNodeShapes() {
      return false;
   }

   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      if ( context.property().isEmpty() ) {
         return List.of();
      }
      final Property property = context.property().get();
      final int count = property.getModel().listStatements( context.element(), null, (RDFNode) null )
            .filterKeep( statement -> statement.getPredicate().equals( property ) ).toList().size();
      if ( count > maxCount ) {
         return List.of( new MaxCountViolation( context, maxCount, count ) );
      }
      return List.of();
   }

   @Override
   public String name() {
      return "sh:maxCount";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitMaxCountConstraint( this );
   }
}
