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

package io.openmanufacturing.sds.aspectmodel.shacl.fix;

import java.util.Optional;

import org.apache.jena.rdf.model.Literal;

import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;

public record ReplaceValue(EvaluationContext context, Literal oldValue, Literal newValue, Optional<String> customDescription) implements Fix {
   @Override
   public String description() {
      return customDescription.orElseGet( () -> {
         return String.format( "Change %s's value from %s to %s", context.property(), oldValue, newValue );
      } );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitReplaceValue( this );
   }
}
