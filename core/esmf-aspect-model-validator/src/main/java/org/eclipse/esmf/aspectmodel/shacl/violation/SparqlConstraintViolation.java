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
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.shacl.constraint.SparqlConstraint;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;

/**
 * Violation of a {@link SparqlConstraint}
 *
 * @param context the evaluation context
 * @param constraintMessage the message as given by the SPARQL constraint
 * @param bindings the variable bindings produced by the SPARQL query
 */
public record SparqlConstraintViolation( EvaluationContext context, String constraintMessage, Map<String, RDFNode> bindings )
      implements Violation {
   public static final String ERROR_CODE = "ERR_UNSPECIFIED_SPARQL_CONSTRAINT_VIOLATION";

   @Override
   public String errorCode() {
      return Optional.ofNullable( bindings.get( "code" ) )
            .map( RDFNode::asLiteral )
            .map( Literal::getString )
            .orElse( ERROR_CODE );
   }

   @Override
   public String violationSpecificMessage() {
      if ( constraintMessage().isEmpty() ) {
         if ( context.property().isPresent() ) {
            return String.format( "Property %s on %s is invalid.", context.propertyName(), context.elementName() );
         }
         return String.format( "%s is invalid.", context.elementName() );
      }

      String interpolatedMessage = constraintMessage();
      for ( final Map.Entry<String, RDFNode> entry : bindings.entrySet() ) {
         final String value = entry.getValue().isURIResource()
               ? context.shortUri( entry.getValue().asResource().getURI() )
               : entry.getValue().toString();
         interpolatedMessage = interpolatedMessage.replaceAll( "\\{[$?]" + entry.getKey() + "\\}", value );
      }
      return interpolatedMessage;
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitSparqlConstraintViolation( this );
   }
}
