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

package io.openmanufacturing.sds.aspectmodel.shacl;

import java.util.List;
import java.util.Optional;

import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import io.openmanufacturing.sds.aspectmodel.shacl.path.Path;
import io.openmanufacturing.sds.aspectmodel.shacl.constraint.Constraint;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#node-shapes">sh:NodeShape</a>
 */
public interface Shape {
   Attributes attributes();

   /**
    * Implements the valid values for <a href="https://www.w3.org/TR/shacl/#NodeKindConstraintComponent">sh:nodeKind</a>
    */
   enum NodeKind {
      BlankNode, IRI, Literal, BlankNodeOrIRI, BlankNodeOrLiteral, IRIOrLiteral;

      public static NodeKind forNode( final RDFNode node ) {
         if ( node.isLiteral() ) {
            return Shape.NodeKind.Literal;
         }
         if ( node.isURIResource() ) {
            return Shape.NodeKind.IRI;
         }
         if ( node.isAnon() ) {
            return Shape.NodeKind.BlankNode;
         }
         throw new RuntimeException( "Invalid nodekind: " + node );
      }
   }

   record Attributes(
         Optional<String> uri,
         Optional<Resource> targetNode,
         Optional<Resource> targetClass,
         Optional<org.apache.jena.rdf.model.Property> targetObjectsOf,
         Optional<org.apache.jena.rdf.model.Property> targetSubjectsOf,
         Optional<Query> targetSparql,
         Optional<String> name,
         Optional<String> description,
         Optional<Integer> order,
         Optional<String> group,
         Optional<RDFNode> defaultValue,
         boolean deactivated,
         Optional<String> message,
         Severity severity,
         List<Constraint> constraints
   ) {
   }

   enum Severity {
      INFO, WARNING, VIOLATION
   }

   record Node(
         Attributes attributes,
         List<Property> properties
   ) implements Shape {
   }

   /**
    * Implements <code>sh:property</code>
    */
   record Property(
         Attributes attributes,
         Path path
   ) implements Shape {
   }
}

