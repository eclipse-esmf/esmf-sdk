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

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;

import io.openmanufacturing.sds.aspectmodel.shacl.Shape;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.DatatypeViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.NodeKindViolation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#DatatypeConstraintComponent">sh:datatype</a>
 * @param allowedTypeUri the allowed data type URI
 */
public record DatatypeConstraint(String allowedTypeUri) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      if ( !rdfNode.isLiteral() ) {
         return List.of( new NodeKindViolation( context, Shape.NodeKind.Literal, Shape.NodeKind.forNode( rdfNode ) ) );
      }
      final Literal value = rdfNode.asLiteral();
      final String actualTypeUri = value.getDatatypeURI();
      return actualTypeUri.endsWith( allowedTypeUri ) ?
            List.of() :
            List.of( new DatatypeViolation( context, allowedTypeUri, actualTypeUri ) );
   }

   @Override
   public String name() {
      return "sh:datatype";
   }
}
