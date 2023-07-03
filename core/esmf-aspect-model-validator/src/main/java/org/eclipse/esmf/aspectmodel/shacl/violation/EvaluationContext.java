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

package org.eclipse.esmf.aspectmodel.shacl.violation;

import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.shacl.ShaclValidator;
import org.eclipse.esmf.aspectmodel.shacl.Shape;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

/**
 * The state during the evaluation of SHACL constraints
 *
 * @param element the resource a constraint is evaluated on
 * @param shape the shape that is being evaluated
 * @param propertyShape if a property shape for a given node shape is evaluated, it is given here
 * @param property if a property shape for a given node shape is evaluated, this is the corresponding property
 * @param offendingStatements one ore more statements that are the cause of the violation if one occurs
 * @param validator the validator
 * @param resolvedModel the model being evaluated
 */
public record EvaluationContext( Resource element, Shape shape, Optional<Shape.Property> propertyShape, Optional<Property> property,
      Optional<EvaluationContext> parentContext, List<Statement> offendingStatements, ShaclValidator validator, Model resolvedModel ) {
   public EvaluationContext withProperty( final Property newProperty ) {
      return new EvaluationContext( element(), shape(), propertyShape(), Optional.of( newProperty ),
            parentContext(), offendingStatements(), validator(), resolvedModel() );
   }

   public EvaluationContext withShape( final Shape shape ) {
      return new EvaluationContext( element(), shape, propertyShape(), property(),
            parentContext(), offendingStatements(), validator(), resolvedModel() );
   }

   public EvaluationContext withPropertyShape( final Shape.Property propertyShape ) {
      return new EvaluationContext( element(), shape(), Optional.of( propertyShape ), property(),
            parentContext(), offendingStatements(), validator(), resolvedModel() );
   }

   public EvaluationContext withElement( final Resource newElement ) {
      return new EvaluationContext( newElement, shape(), propertyShape(), property(),
            parentContext(), offendingStatements(), validator(), resolvedModel() );
   }

   public EvaluationContext withOffendingStatements( final List<Statement> newOffendingStatements ) {
      return new EvaluationContext( element(), shape(), propertyShape(), property(),
            parentContext(), newOffendingStatements, validator(), resolvedModel() );
   }

   public String shortUri( final String uri ) {
      final String shortened = element().getModel().shortForm( uri );
      return shortened.equals( uri ) ?
            validator().getShapesModel().shortForm( uri ) : shortened;
   }

   public String elementName() {
      return Optional.ofNullable( element().getURI() )
            .map( this::shortUri )
            .orElse( "anonymous element" );
   }

   public String parentElementName() {
      return parentContext().map( parentContext -> parentContext.element().getURI() )
            .map( this::shortUri )
            .orElse( "anonymous element" );
   }

   public String propertyName() {
      return property().map( Resource::getURI ).map( this::shortUri ).orElse( elementName() );
   }

   public String parentPropertyName() {
      return parentContext()
            .flatMap( EvaluationContext::property )
            .map( Property::getURI )
            .map( this::shortUri )
            .orElse( propertyName() );
   }

   public String value( final Property property ) {
      return shortUri( property.getURI() );
   }

   public String value( final RDFNode node ) {
      if ( node.isLiteral() ) {
         return value( node.asLiteral() );
      }
      if ( node.isURIResource() ) {
         return shortUri( node.asResource().getURI() );
      }
      return "anonymous element";
   }

   public String value( final Literal literal ) {
      return literal.getLexicalForm();
   }
}
