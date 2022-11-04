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

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.RDFNode;

public record ValueFromListViolation(EvaluationContext context, List<RDFNode> allowed, RDFNode actual) implements Violation {
   public static final String ERROR_CODE = "ERR_VALUE_FROM_LIST";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String message() {
      return String.format( "Property %s on %s has value %s which is not in the list of allowed values: %s.",
            propertyName(), elementName(), value( actual ), allowed.stream().map( this::value ).collect( Collectors.joining( ", ", "[", "]" ) ) );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitValueFromListViolation( this );
   }

   @Override
   public AppliesTo appliesTo() {
      return AppliesTo.ONLY_PROPERTY;
   }
}
