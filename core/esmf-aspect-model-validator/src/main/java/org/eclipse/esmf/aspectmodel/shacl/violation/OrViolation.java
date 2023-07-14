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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.esmf.aspectmodel.shacl.constraint.OrConstraint;

/**
 * Violation of a {@link OrConstraint}
 *
 * @param context the evaluation context
 * @param violations the list of violations that were encountered
 */
public record OrViolation( EvaluationContext context, List<Violation> violations ) implements Violation {
   public static final String ERROR_CODE = "ERR_OR";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      return "At least one of the following violations" +
            (context.property().isPresent() ? " for " + context.propertyName() : "")
            + " must be fixed: " +
            IntStream.range( 0, violations.size() )
                  .mapToObj( i -> String.format( "(%d) %s", i + 1, violations().get( i ).message().replaceAll( "\\.$", "" ) ) )
                  .collect( Collectors.joining( ", " ) ) + ".";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitOrViolation( this );
   }
}
