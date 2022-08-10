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

import org.apache.jena.rdf.model.Literal;

public record MinExclusiveViolation(EvaluationContext context, Literal min, Literal actual) implements Violation {
   @Override
   public String errorCode() {
      return "ERR_MIN_EXCLUSIVE";
   }

   @Override
   public String message() {
      return String.format( "Property %s on %s has value %s, but it must be greater than %s",
            propertyName(), elementName(), actual, min );
   }

   @Override
   public <T> T accept( Visitor<T> visitor ) {
      return visitor.visitMinExclusiveViolation( this );
   }
}
