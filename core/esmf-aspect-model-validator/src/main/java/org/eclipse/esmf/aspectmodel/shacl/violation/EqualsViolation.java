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

import org.eclipse.esmf.aspectmodel.shacl.constraint.EqualsConstraint;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

/**
 * Violation of a {@link EqualsConstraint}
 *
 * @param context the evaluation context
 * @param otherProperty the property the context.property()'s value must be equal to
 * @param allowedValue the allowed value
 * @param actualValue the encountered value
 */
public record EqualsViolation( EvaluationContext context, Property otherProperty, RDFNode allowedValue, RDFNode actualValue )
      implements Violation {
   public static final String ERROR_CODE = "ERR_EQUALS";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      return String.format( "Property %s on %s must have the same value as property %s (%s), but has value %s.",
            context.propertyName(), context.elementName(), context.shortUri( otherProperty.getURI() ), allowedValue, actualValue );
   }

   @Override
   public RDFNode highlight() {
      return actualValue();
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitEqualsViolation( this );
   }
}
