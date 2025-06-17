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

import org.eclipse.esmf.aspectmodel.shacl.constraint.AllowedValuesConstraint;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Violation of a {@link AllowedValuesConstraint}
 *
 * @param context the evaluation context
 * @param allowed the list of allowed values
 * @param actual the encountered value
 */
public record ValueFromListViolation( EvaluationContext context, List<RDFNode> allowed, RDFNode actual ) implements Violation {
   public static final String ERROR_CODE = "ERR_VALUE_FROM_LIST";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      return String.format( "Property %s on %s has value %s which is not in the list of allowed values: %s.",
            context.propertyName(), context.elementName(), context.value( actual ),
            allowed.stream().map( context::value ).collect( Collectors.joining( ", ", "[", "]" ) ) );
   }

   @Override
   public RDFNode highlight() {
      return actual();
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
