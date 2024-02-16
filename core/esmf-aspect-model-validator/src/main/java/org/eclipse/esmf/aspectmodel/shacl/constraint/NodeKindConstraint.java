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
import org.eclipse.esmf.aspectmodel.shacl.violation.NodeKindViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#NodeKindConstraintComponent">sh:nodeKind</a>
 *
 * @param allowedNodeKind the allowed node kind
 */
public record NodeKindConstraint( Shape.NodeKind allowedNodeKind ) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      final Shape.NodeKind actualNodeKind = Shape.NodeKind.forNode( rdfNode );
      if ( actualNodeKind == allowedNodeKind ) {
         // No violation
         return List.of();
      }
      if ( actualNodeKind == Shape.NodeKind.Literal
            && (allowedNodeKind == Shape.NodeKind.BlankNodeOrLiteral || allowedNodeKind == Shape.NodeKind.IRIOrLiteral) ) {
         // No violation
         return List.of();
      }
      if ( actualNodeKind == Shape.NodeKind.IRI
            && (allowedNodeKind == Shape.NodeKind.BlankNodeOrIRI || allowedNodeKind == Shape.NodeKind.IRIOrLiteral) ) {
         // No violation
         return List.of();
      }
      return List.of( new NodeKindViolation( context, allowedNodeKind, actualNodeKind ) );
   }

   @Override
   public String name() {
      return "sh:nodeKind";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitNodeKindConstraint( this );
   }
}
