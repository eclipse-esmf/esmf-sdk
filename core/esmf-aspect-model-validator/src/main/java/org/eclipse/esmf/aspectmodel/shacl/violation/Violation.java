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

import org.eclipse.esmf.aspectmodel.shacl.fix.Fix;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

/**
 * Represents a single violation raised by one or more SHACL shapes against an RDF model.
 * A human-readable representation of the violation is available via {@link #message()} while details about the context
 * in which the violation occurred (such as offending model element, RDF statements and the SHACL shape that raised the
 * violation) are available via {@link #context()}. To handle information specific to each type of violation,
 * implement {@link Visitor} and call {@link #accept(Visitor)} on the violation(s).
 */
public interface Violation {
   String errorCode();

   EvaluationContext context();

   String violationSpecificMessage();

   <T> T accept( Visitor<T> visitor );

   enum AppliesTo {
      WHOLE_ELEMENT, ONLY_PROPERTY
   }

   interface Visitor<T> {
      T visit( final Violation violation );

      default T visitProcessingViolation( final ProcessingViolation violation ) {
         return visit( violation );
      }

      default T visitInvalidSyntaxViolation( final InvalidSyntaxViolation violation ) {
         return visit( violation );
      }

      default T visitClassTypeViolation( final ClassTypeViolation violation ) {
         return visit( violation );
      }

      default T visitDatatypeViolation( final DatatypeViolation violation ) {
         return visit( violation );
      }

      default T visitInvalidValueViolation( final InvalidValueViolation violation ) {
         return visit( violation );
      }

      default T visitLanguageFromListViolation( final LanguageFromListViolation violation ) {
         return visit( violation );
      }

      default T visitMaxCountViolation( final MaxCountViolation violation ) {
         return visit( violation );
      }

      default T visitMaxExclusiveViolation( final MaxExclusiveViolation violation ) {
         return visit( violation );
      }

      default T visitMaxInclusiveViolation( final MaxInclusiveViolation violation ) {
         return visit( violation );
      }

      default T visitMaxLengthViolation( final MaxLengthViolation violation ) {
         return visit( violation );
      }

      default T visitMinCountViolation( final MinCountViolation violation ) {
         return visit( violation );
      }

      default T visitMinExclusiveViolation( final MinExclusiveViolation violation ) {
         return visit( violation );
      }

      default T visitMinInclusiveViolation( final MinInclusiveViolation violation ) {
         return visit( violation );
      }

      default T visitMinLengthViolation( final MinLengthViolation violation ) {
         return visit( violation );
      }

      default T visitMissingTypeViolation( final MissingTypeViolation violation ) {
         return visit( violation );
      }

      default T visitNodeKindViolation( final NodeKindViolation violation ) {
         return visit( violation );
      }

      default T visitPatternViolation( final PatternViolation violation ) {
         return visit( violation );
      }

      default T visitSparqlConstraintViolation( final SparqlConstraintViolation violation ) {
         return visit( violation );
      }

      default T visitUniqueLanguageViolation( final UniqueLanguageViolation violation ) {
         return visit( violation );
      }

      default T visitEqualsViolation( final EqualsViolation violation ) {
         return visit( violation );
      }

      default T visitDisjointViolation( final DisjointViolation violation ) {
         return visit( violation );
      }

      default T visitLessThanViolation( final LessThanViolation violation ) {
         return visit( violation );
      }

      default T visitLessThanOrEqualsViolation( final LessThanOrEqualsViolation violation ) {
         return visit( violation );
      }

      default T visitValueFromListViolation( final ValueFromListViolation violation ) {
         return visit( violation );
      }

      default T visitClosedViolation( final ClosedViolation violation ) {
         return visit( violation );
      }

      default T visitNotViolation( final NotViolation violation ) {
         return visit( violation );
      }

      default T visitXoneViolation( final XoneViolation violation ) {
         return visit( violation );
      }

      default T visitJsViolation( final JsConstraintViolation violation ) {
         return visit( violation );
      }
   }

   default AppliesTo appliesTo() {
      return AppliesTo.WHOLE_ELEMENT;
   }

   default String shortUri( final String uri ) {
      final String shortened = context().element().getModel().shortForm( uri );
      return shortened.equals( uri ) ?
            context().validator().getShapesModel().shortForm( uri ) : shortened;
   }

   default String elementName() {
      return Optional.ofNullable( context().element().getURI() )
            .map( this::shortUri )
            .orElse( "anonymous element" );
   }

   default String propertyName() {
      return context().property().map( Resource::getURI ).map( this::shortUri ).orElse( elementName() );
   }

   default String value( final Property property ) {
      return shortUri( property.getURI() );
   }

   default String value( final RDFNode node ) {
      if ( node.isLiteral() ) {
         return value( node.asLiteral() );
      }
      if ( node.isURIResource() ) {
         return shortUri( node.asResource().getURI() );
      }
      return "anonymous element";
   }

   default String value( final Literal literal ) {
      return literal.getLexicalForm();
   }

   default String message() {
      final String nodeShapeMessage = context().shape().attributes().message().map( message -> message.replaceAll( "\\.$", "" )
            + ", more specifically: " ).orElse( "" );
      final String propertyShapeMessage = context().propertyShape().flatMap( propertyShape -> propertyShape.attributes().message() )
            .orElseGet( this::violationSpecificMessage );
      return nodeShapeMessage + propertyShapeMessage;
   }

   default List<Fix> fixes() {
      return List.of();
   }
}

