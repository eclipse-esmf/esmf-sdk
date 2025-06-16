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

import org.eclipse.esmf.aspectmodel.shacl.constraint.MinLengthConstraint;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Violation of a {@link MinLengthConstraint}
 *
 * @param context the evaluation context
 * @param min the given minimum value
 * @param actual the encountered value
 */
public record MinLengthViolation( EvaluationContext context, int min, int actual ) implements Violation {
   public static final String ERROR_CODE = "ERR_MIN_LENGTH";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      return String.format( "Property %s on %s has length %s, but its length must be greater than or equal to %s.",
            context.propertyName(), context.elementName(), actual, min );
   }

   @Override
   public RDFNode highlight() {
      return context().property().get();
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitMinLengthViolation( this );
   }
}
