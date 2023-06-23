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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.shacl.Shape;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.shacl.violation.XoneViolation;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#XoneConstraintComponent">sh:xone</a>
 */
public record XoneConstraint( List<Shape> shapes ) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      final List<List<Violation>> violationsPerConstraint = context.propertyShape().map( Shape.Property::path ).map( path ->
                  path.accept( context.element(), context.validator().getRetriever() ).stream()
                        .filter( statement -> statement.getObject().isResource() )
                        .map( Statement::getResource ) )
            .orElse( Stream.of( context.element() ) )
            .map( element -> shapes.stream().flatMap( shape -> {
               final Optional<Resource> parentElement = Optional.of( context.element() );
               final Optional<Property> parentProperty = context.property();
               return shape instanceof Shape.Node ?
                     context.validator().validateShapeForElement( element, (Shape.Node) shape, context.resolvedModel(),
                           parentElement, parentProperty ).stream() :
                     context.validator().validateShapeForElement( element, (Shape.Node) context.shape(), (Shape.Property) shape,
                           context.resolvedModel(), parentElement, parentProperty ).stream();
            } ).toList() )
            .toList();

      final long numberOfEmptyViolationLists = violationsPerConstraint.stream().filter( List::isEmpty ).count();
      // The xone constraint is evaluated successfully if exactly one of the provided constraints evaluates successfully
      if ( numberOfEmptyViolationLists == 1 ) {
         return List.of();
      }
      return List.of( new XoneViolation( context, violationsPerConstraint.stream().flatMap( Collection::stream ).toList() ) );
   }

   @Override
   public String name() {
      return "sh:xone";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitXoneConstraint( this );
   }
}
