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

import org.eclipse.esmf.aspectmodel.shacl.Shape;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.MinLengthViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#MinLengthConstraintComponent">sh:minLength</a>
 *
 * @param minLength the min length
 */
public record MinLengthConstraint( int minLength ) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      // sh:minLength is applicable to literals and IRIs, but not blank nodes
      final List<Violation> nodeKindViolations = new NodeKindConstraint( Shape.NodeKind.IRIOrLiteral ).apply( rdfNode, context );
      if ( !nodeKindViolations.isEmpty() ) {
         return nodeKindViolations;
      }

      final String value = rdfNode.asLiteral().getLexicalForm();
      return value.length() >= minLength
            ? List.of()
            : List.of( new MinLengthViolation( context, minLength, value.length() ) );
   }

   @Override
   public String name() {
      return "sh:minLength";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitMinLengthConstraint( this );
   }
}
