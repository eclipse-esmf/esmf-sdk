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

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Property;

public record ClosedViolation( EvaluationContext context, Set<Property> allowedProperties, Set<Property> ignoredProperties,
      Property actual ) implements Violation {
   public static final String ERROR_CODE = "ERR_CLOSED";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String message() {
      final Set<String> allowed = Stream.concat( allowedProperties().stream(), ignoredProperties().stream() )
            .map( Property::getURI )
            .map( this::shortUri )
            .collect( Collectors.toSet() );
      return String.format( "%s is used on %s. It is not allowed there; allowed are only %s.",
            shortUri( actual.getURI() ), elementName(), allowed );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitClosedViolation( this );
   }
}
