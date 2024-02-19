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
      if ( context.property().isPresent() ) {
         return String.format( "Property %s on %s is %s, but it must be %s.",
               context.propertyName(), context.element().isAnon() ? "the element" : context.elementName(),
               actualNodeKind.humanReadable(), allowedNodeKind.humanReadable() );
      } else if ( context.parentContext().flatMap( EvaluationContext::property ).isPresent() ) {
         return String.format( "Property %s on %s is %s, but it must be %s.",
               context.parentPropertyName(), context.parentElementName(), actualNodeKind.humanReadable(), allowedNodeKind.humanReadable() );
      }
      return context.element().isAnon()
            ? String.format( "The element is %s, but it must be %s.",
            actualNodeKind.humanReadable(), allowedNodeKind.humanReadable() )
            : String.format( "Element %s is %s, but it must be %s.",
            context.elementName(), actualNodeKind.humanReadable(), allowedNodeKind.humanReadable() );
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
