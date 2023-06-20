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

import org.eclipse.esmf.aspectmodel.shacl.constraint.PatternConstraint;

/**
 * Violation of a {@link PatternConstraint}
 *
 * @param context the evaluation context
 * @param actual the encountered string
 * @param pattern the regular expression pattern the string should adhere to
 */
public record PatternViolation( EvaluationContext context, String actual, String pattern ) implements Violation {
   public static final String ERROR_CODE = "ERR_PATTERN";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      return String.format( "Property %s on %s has value %s, which does not match the required pattern %s.",
            propertyName(), elementName(), actual, pattern );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitPatternViolation( this );
   }
}
