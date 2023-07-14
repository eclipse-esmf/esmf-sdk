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

import org.eclipse.esmf.aspectmodel.shacl.constraint.Constraint;
import org.eclipse.esmf.aspectmodel.shacl.constraint.NotConstraint;

/**
 * Violation of a {@link NotConstraint}
 *
 * @param context the evaluation context
 * @param negatedConstraint the constraint that was given as negated
 */
public record NotViolation( EvaluationContext context, Constraint negatedConstraint ) implements Violation {
   public static final String ERROR_CODE = "ERR_NOT";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      return context.property().isPresent() ?
            String.format( "Expected violation of constraint %s on %s on %s, but it did not occur.", negatedConstraint.name(),
                  context.propertyName(), context.elementName() ) :
            String.format( "Expected violation of constraint %s on element %s, but it did not occur.", negatedConstraint.name(),
                  context.elementName() );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitNotViolation( this );
   }
}
