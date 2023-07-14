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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.shacl.constraint.ClosedConstraint;

import org.apache.jena.rdf.model.Property;

/**
 * Violation of a {@link ClosedConstraint}
 *
 * @param context the evaluation context of this violation
 * @param allowedProperties the properties that are allowed on the value node
 * @param ignoredProperties the properties that were ignored in the validation
 * @param actual the actually encountered property
 */
public record ClosedViolation( EvaluationContext context, Set<Property> allowedProperties, Set<Property> ignoredProperties,
      Property actual ) implements Violation {
   /**
    * The error code for this violation
    */
   public static final String ERROR_CODE = "ERR_CLOSED";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      final List<String> allowed = allowedProperties().stream()
            .map( Property::getURI )
            .map( context::shortUri )
            .collect( Collectors.toSet() )
            .stream()
            .sorted()
            .toList();
      final String allowedText = switch ( allowed.size() ) {
         case 0 -> "no properties are allowed";
         case 1 -> "only " + allowed.iterator().next() + " is allowed";
         default -> "allowed are only " + allowed;
      };

      return String.format( "%s is used on %s. It is not allowed there; %s.",
            context.shortUri( actual.getURI() ), context.elementName(), allowedText );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitClosedViolation( this );
   }
}
