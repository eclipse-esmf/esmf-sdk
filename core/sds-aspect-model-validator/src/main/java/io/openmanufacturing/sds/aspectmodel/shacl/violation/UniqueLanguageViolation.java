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

import java.util.Set;

public record UniqueLanguageViolation(EvaluationContext context, Set<String> duplicates) implements Violation {
   @Override
   public String errorCode() {
      return "ERR_DUPLICATE_LANGUAGE";
   }

   @Override
   public String message() {
      return String.format( "Property %s on %s uses language tag that has has been used already: %s",
            propertyName(), elementName(), duplicates );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitUniqueLanguageViolation( this );
   }
}
