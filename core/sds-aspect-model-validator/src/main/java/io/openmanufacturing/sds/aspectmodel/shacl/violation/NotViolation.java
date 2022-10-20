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

import io.openmanufacturing.sds.aspectmodel.shacl.constraint.Constraint;

public record NotViolation(EvaluationContext context, Constraint negatedConstraint) implements Violation {
   public static final String ERROR_CODE = "ERR_NOT";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String message() {
      return context.property().isPresent() ?
            String.format( "Expected violation of constraint %s on %s on %s, but did not occur", negatedConstraint.name(), propertyName(), elementName() ) :
            String.format( "Expected violation of constraint %s on element %s, but did not occur", negatedConstraint.name(), elementName() );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitNotViolation( this );
   }
}
