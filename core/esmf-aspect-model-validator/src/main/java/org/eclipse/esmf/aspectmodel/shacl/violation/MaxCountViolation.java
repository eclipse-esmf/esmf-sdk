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

import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxCountConstraint;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Violation of a {@link MaxCountConstraint}
 *
 * @param context the evaluation context
 * @param allowed the allowed number repetitions of a property
 * @param actual the encountered number
 */
public record MaxCountViolation( EvaluationContext context, int allowed, int actual ) implements Violation {
   public static final String ERROR_CODE = "ERR_MAX_COUNT";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      return allowed == 0
            ? String.format( "Property %s may not be used on %s.", context.propertyName(), context.elementName() )
            : String.format( "Property %s is used %d time%s on %s, but may only be used %d time%s.",
            context.propertyName(), actual, actual > 1 ? "s" : "", context.elementName(), allowed, allowed > 1 ? "s" : "" );
   }

   @Override
   public RDFNode highlight() {
      return allowed() == 0 ? context().element() : context().property().get();
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitMaxCountViolation( this );
   }

   @Override
   public AppliesTo appliesTo() {
      return actual == 0 ? AppliesTo.ONLY_PROPERTY : AppliesTo.WHOLE_ELEMENT;
   }
}
