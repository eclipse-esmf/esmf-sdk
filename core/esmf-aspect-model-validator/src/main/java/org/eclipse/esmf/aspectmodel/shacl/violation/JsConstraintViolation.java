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

import java.util.Map;

import org.eclipse.esmf.aspectmodel.shacl.JsLibrary;
import org.eclipse.esmf.aspectmodel.shacl.constraint.JsConstraint;

import org.apache.jena.graph.Node_Blank;
import org.apache.jena.graph.Node_Literal;
import org.apache.jena.graph.Node_URI;
import org.apache.jena.rdf.model.RDFNode;

/**
 * Violation of a {@link JsConstraint}. Represents the violation of a SHACL JavaScript constraint. It provides references to the used
 * <a href="https://www.w3.org/TR/shacl-js/#JSLibrary">SHACL JavaScript library</a> and the JavaScript function name.
 * The bindings map returned by the constraint validation will contains those object returned by the JavaScript function, with its values
 * mapped to the corresponding Java types: Scalars (string, numbers) will be native Java types, Named nodes (RDF resources, RDF properties)
 * will be of type {@link Node_URI}, blank nodes will be of type {@link Node_Blank}, RDF literals will be of type {@link Node_Literal}.
 *
 * @param context the evaluation context
 * @param constraintMessage the message as defined by the constraint
 * @param library the JS library that defined the constraint
 * @param functionName the JS function called in the library
 * @param bindings the variable bindings as provided by the validation function
 */
public record JsConstraintViolation( EvaluationContext context, String constraintMessage, JsLibrary library, String functionName,
      Map<String, Object> bindings ) implements Violation {
   public static final String ERROR_CODE = "ERR_JAVASCRIPT";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      if ( constraintMessage().isEmpty() ) {
         return context.property().isPresent()
               ? String.format( "Property %s on %s is invalid.", context.propertyName(), context.elementName() )
               : String.format( "%s is invalid.", context.elementName() );
      }
      String interpolatedMessage = bindings.getOrDefault( "message", constraintMessage() ).toString();
      for ( final Map.Entry<String, Object> entry : bindings.entrySet() ) {
         final String value;
         if ( entry.getValue() instanceof final Node_Literal literal ) {
            value = literal.getLiteralLexicalForm();
         } else if ( entry.getValue() instanceof final Node_URI namedNode ) {
            value = context.shortUri( namedNode.getURI() );
         } else if ( entry.getValue() instanceof Node_Blank ) {
            value = "anonymous element";
         } else {
            value = entry.getValue().toString();
         }
         interpolatedMessage = interpolatedMessage.replaceAll( "\\{[$?]" + entry.getKey() + "\\}", value );
      }
      return interpolatedMessage;
   }

   @Override
   public RDFNode highlight() {
      return context().property().isPresent() ? context().property().get() : context().element();
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitJsViolation( this );
   }
}
