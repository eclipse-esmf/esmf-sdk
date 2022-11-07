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

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MinCountViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#MinCountConstraintComponent">sh:minCount</a>
 * @param minCount the min count
 */
public record MinCountConstraint(int minCount) implements Constraint {
   @Override
   public boolean canBeUsedOnNodeShapes() {
      return false;
   }

   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      if ( context.property().isEmpty() ) {
         return List.of();
      }
      if ( minCount > 0 && rdfNode == null ) {
         return List.of( new MinCountViolation( context, minCount, 0 ) );
      }
      final Property property = context.property().get();
      final int count = property.getModel().listStatements( null, property, (RDFNode) null ).toList().size();
      if ( count < minCount ) {
         return List.of( new MinCountViolation( context, minCount, count ) );
      }
      return List.of();
   }

   @Override
   public String name() {
      return "sh:minCount";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitMinCountConstraint( this );
   }
}
