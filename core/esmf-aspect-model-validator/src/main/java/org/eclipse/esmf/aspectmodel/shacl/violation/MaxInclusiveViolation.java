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

import org.eclipse.esmf.aspectmodel.shacl.constraint.MaxInclusiveConstraint;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;

/**
 * Violation of a {@link MaxInclusiveConstraint}
 *
 * @param context the evaluation context
 * @param max the given maximum value
 * @param actual the encountered value
 */
public record MaxInclusiveViolation( EvaluationContext context, Literal max, Literal actual ) implements Violation {
   public static final String ERROR_CODE = "ERR_MAX_INCLUSIVE";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      return String.format( "Property %s on %s has value %s, but it must be less than or equal to %s.",
            context.propertyName(), context.elementName(), context.value( actual ), context.value( max ) );
   }

   @Override
   public RDFNode highlight() {
      return actual();
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitMaxInclusiveViolation( this );
   }
}
