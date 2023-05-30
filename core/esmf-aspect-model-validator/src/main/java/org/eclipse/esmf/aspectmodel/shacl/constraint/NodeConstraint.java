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
import java.util.Optional;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.eclipse.esmf.aspectmodel.shacl.Shape;
import org.eclipse.esmf.aspectmodel.shacl.path.Path;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#NodeConstraintComponent">sh:node</a>
 * @param targetShape the node shape this sh:node refers to
 */
public record NodeConstraint(Optional<Path> path, Shape.Node targetShape) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      // An empty path means that the node constraint is used inside a node shape, i.e., we just apply the target shape to the context element
      if ( path.isEmpty() ) {
         return context.validator().validateShapeForElement( context.element(), targetShape, context.resolvedModel() );
      }

      // Having a path means that the node constraint is used inside a property shape, i.e., it applies to the element the
      // shape's path points to
      return path.get().accept( context.element(), context.validator().getPathNodeRetriever() ).stream()
            .filter( statement -> statement.getObject().isResource() )
            .map( Statement::getResource )
            .flatMap( element -> context.validator().validateShapeForElement( element, targetShape, context.resolvedModel() ).stream() )
            .toList();
   }

   @Override
   public String name() {
      return "sh:node";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitNodeConstraint( this );
   }
}
