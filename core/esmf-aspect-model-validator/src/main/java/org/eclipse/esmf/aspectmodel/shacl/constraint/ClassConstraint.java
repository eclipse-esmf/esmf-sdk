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

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.eclipse.esmf.aspectmodel.shacl.RdfTypes;
import org.eclipse.esmf.aspectmodel.shacl.Shape;
import org.eclipse.esmf.aspectmodel.shacl.violation.ClassTypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.MissingTypeViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.NodeKindViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#ClassConstraintComponent">sh:class</a>
 * @param allowedClass the allowed class
 */
public record ClassConstraint(Resource allowedClass) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      if ( !rdfNode.isResource() ) {
         return List.of( new NodeKindViolation( context, Shape.NodeKind.BlankNodeOrIRI, Shape.NodeKind.forNode( rdfNode ) ) );
      }
      final Resource resource = rdfNode.asResource();
      final StmtIterator iterator = context.resolvedModel().listStatements( resource, RDF.type, (RDFNode) null );
      if ( !iterator.hasNext() ) {
         return List.of( new MissingTypeViolation( context.withElement( resource ).withProperty( RDF.type ).withOffendingStatements( List.of() ) ) );
      }
      final Statement typeAssertionStatement = iterator.next();
      final RDFNode assertedTypeNode = typeAssertionStatement.getObject();
      if ( !assertedTypeNode.isResource() ) {
         return List.of(
               new NodeKindViolation( context.withElement( resource ).withProperty( RDF.type ).withOffendingStatements( List.of( typeAssertionStatement ) ),
                     Shape.NodeKind.BlankNodeOrIRI,
                     Shape.NodeKind.forNode( rdfNode ) ) );
      }
      final Resource actualClass = assertedTypeNode.asResource();
      return RdfTypes.typesOfElement( typeAssertionStatement.getSubject(), context.resolvedModel() ).contains( allowedClass ) ?
            List.of() :
            List.of( new ClassTypeViolation( context, allowedClass, actualClass ) );
   }

   @Override
   public String name() {
      return "sh:class";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitClassConstraint( this );
   }
}
