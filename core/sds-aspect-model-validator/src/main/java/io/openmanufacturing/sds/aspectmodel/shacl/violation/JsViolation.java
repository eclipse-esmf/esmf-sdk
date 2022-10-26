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

import io.openmanufacturing.sds.aspectmodel.shacl.JsLibrary;

public record JsViolation(EvaluationContext context, String constraintMessage, JsLibrary library, String functionName) implements Violation {
   public static final String ERROR_CODE = "ERR_JAVASCRIPT";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String message() {
      if ( constraintMessage().isEmpty() ) {
         return context.property().isPresent() ?
               String.format( "Property %s on %s is invalid", propertyName(), elementName() ) :
               String.format( "%s is invalid", elementName() );
      }
      return constraintMessage();
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitJsViolation( this );
   }
}
