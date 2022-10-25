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

import org.apache.jena.rdf.model.RDFNode;

import io.openmanufacturing.sds.aspectmodel.shacl.Shape;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.MaxLengthViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#MaxLengthConstraintComponent">sh:maxLength</a>
 * @param maxLength the max length
 */
public record MaxLengthConstraint(int maxLength) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      // sh:maxLength is applicable to literals and IRIs, but not blank nodes
      final List<Violation> nodeKindViolations = new NodeKindConstraint( Shape.NodeKind.IRIOrLiteral ).apply( rdfNode, context );
      if ( !nodeKindViolations.isEmpty() ) {
         return nodeKindViolations;
      }

      final String value = rdfNode.asLiteral().getLexicalForm();
      return value.length() <= maxLength ?
            List.of() :
            List.of( new MaxLengthViolation( context, maxLength, value.length() ) );
   }

   @Override
   public String name() {
      return "sh:maxLength";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitMaxLengthConstraint( this );
   }
}
