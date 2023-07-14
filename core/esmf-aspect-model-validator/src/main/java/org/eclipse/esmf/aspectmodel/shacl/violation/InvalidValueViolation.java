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

import org.eclipse.esmf.aspectmodel.shacl.constraint.HasValueConstraint;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Violation of a {@link HasValueConstraint}
 *
 * @param context the evaluation context
 * @param allowed the allowed value
 * @param actual the encountered value
 */
public record InvalidValueViolation( EvaluationContext context, RDFNode allowed, RDFNode actual ) implements Violation {
   public static final String ERROR_CODE = "ERR_VALUE";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      return String.format( "Property %s on %s has value %s, but only %s is allowed.",
            context.propertyName(), context.elementName(), context.value( actual ), context.value( allowed ) );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitInvalidValueViolation( this );
   }
}
