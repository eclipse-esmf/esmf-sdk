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

package io.openmanufacturing.sds.aspectmodel.shacl.violation;

import java.util.Map;
import java.util.Optional;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;

public record SparqlConstraintViolation(EvaluationContext context, String constraintMessage, Map<String, RDFNode> bindings) implements Violation {
   @Override
   public String errorCode() {
      return Optional.ofNullable( bindings.get( "code" ) )
            .map( RDFNode::asLiteral )
            .map( Literal::getString )
            .orElse( "ERR_UNSPECIFIED_SPARQL_CONSTRAINT_VIOLATION" );
   }

   @Override
   public String message() {
      String interpolatedMessage = constraintMessage();
      for ( final Map.Entry<String, RDFNode> entry : bindings.entrySet() ) {
         final String value = entry.getValue().isURIResource() ? shortUri( entry.getValue().asResource().getURI() ) : entry.getValue().toString();
         interpolatedMessage = interpolatedMessage.replaceAll( "\\{[$?]" + entry.getKey() + "\\}", value );
      }
      return interpolatedMessage;
   }

   @Override
   public <T> T accept( Visitor<T> visitor ) {
      return visitor.visitSparqlConstraintViolation( this );
   }
}
