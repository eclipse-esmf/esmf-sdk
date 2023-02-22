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

import org.apache.jena.rdf.model.Literal;

public record MaxExclusiveViolation(EvaluationContext context, Literal max, Literal actual) implements Violation {
   public static final String ERROR_CODE = "ERR_MAX_EXCLUSIVE";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String message() {
      return String.format( "Property %s on %s has value %s, but it must be less than %s.",
            propertyName(), elementName(), value( actual ), value( max ) );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitMaxExclusiveViolation( this );
   }
}
