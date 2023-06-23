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

import java.util.function.Function;

import org.eclipse.esmf.aspectmodel.shacl.Shape;
import org.eclipse.esmf.aspectmodel.shacl.constraint.NodeKindConstraint;

/**
 * Violation of a {@link NodeKindConstraint}
 *
 * @param context the evaluation context
 * @param allowedNodeKind the allowed kind of node
 * @param actualNodeKind the encountered kind of node
 */
public record NodeKindViolation( EvaluationContext context, Shape.NodeKind allowedNodeKind, Shape.NodeKind actualNodeKind )
      implements Violation {
   public static final String ERROR_CODE = "ERR_NODEKIND";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      final Function<Shape.NodeKind, String> nodeKindString = nodeKind -> switch ( nodeKind ) {
         case BlankNode -> "an anonymous node";
         case BlankNodeOrIRI -> "an anonymous node or a named element";
         case IRI -> "a named element";
         case BlankNodeOrLiteral -> "an anonymous node or a value";
         case Literal -> "a value";
         case IRIOrLiteral -> "a named element or a value";
      };
      if ( context.property().isPresent() ) {
         return String.format( "Property %s on %s is %s, but it must be %s.",
               propertyName(), context.element().isAnon() ? "the element" : elementName(),
               nodeKindString.apply( actualNodeKind ), nodeKindString.apply( allowedNodeKind ) );
      } else if ( context.parentProperty().isPresent() ) {
         return String.format( "Property %s on %s is %s, but it must be %s.",
               parentPropertyName(), parentElementName(), nodeKindString.apply( actualNodeKind ), nodeKindString.apply( allowedNodeKind ) );
      }
      return context.element().isAnon() ?
            String.format( "The element is %s, but it must be %s.",
                  nodeKindString.apply( actualNodeKind ), nodeKindString.apply( allowedNodeKind ) ) :
            String.format( "Element %s is %s, but it must be %s.",
                  elementName(), nodeKindString.apply( actualNodeKind ), nodeKindString.apply( allowedNodeKind ) );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitNodeKindViolation( this );
   }

   @Override
   public AppliesTo appliesTo() {
      return AppliesTo.ONLY_PROPERTY;
   }
}
