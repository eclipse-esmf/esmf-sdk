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
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import io.openmanufacturing.sds.aspectmodel.shacl.Shape;
import io.openmanufacturing.sds.aspectmodel.shacl.path.FirstEffectiveProperty;
import io.openmanufacturing.sds.aspectmodel.shacl.path.PathNodeRetriever;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.ClosedViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.NodeKindViolation;

public record ClosedConstraint(Set<Property> ignoredProperties) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      if ( !rdfNode.isResource() ) {
         return List.of( new NodeKindViolation( context, Shape.NodeKind.BlankNodeOrIRI, Shape.NodeKind.forNode( rdfNode ) ) );
      }
      if ( !(context.shape() instanceof final Shape.Node shapeNode) ) {
         return List.of();
      }

      final Resource resource = rdfNode.asResource();
      final PathNodeRetriever retriever = new PathNodeRetriever();
      // All statements that lead to (i.e. have as their object) nodes that are reachable via the paths of the property shapes
      final List<Statement> allowedNodes = shapeNode.properties().stream()
            .flatMap( property -> property.path().accept( resource, retriever ).stream() )
            .toList();

      final FirstEffectiveProperty allowedFirstProperties = new FirstEffectiveProperty();
      final Set<Property> allowedProperties = shapeNode.properties().stream()
            .flatMap( property -> property.path().accept( resource, allowedFirstProperties ).stream() ).collect( Collectors.toSet() );

      return resource.getModel().listStatements( resource, null, (RDFNode) null )
            .mapWith( Statement::getPredicate )
            .filterKeep( predicate -> !allowedProperties.contains( predicate ) && !ignoredProperties.contains( predicate ) )
            .<Violation> mapWith( predicate -> new ClosedViolation( context, allowedProperties, ignoredProperties(), predicate ) )
            .toList();
   }

}
