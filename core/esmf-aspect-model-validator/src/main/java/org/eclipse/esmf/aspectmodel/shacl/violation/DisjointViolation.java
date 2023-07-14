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

import org.eclipse.esmf.aspectmodel.shacl.constraint.DisjointConstraint;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

/**
 * Violation of a {@link DisjointConstraint}
 *
 * @param context the evaluation context
 * @param otherProperty the property the context.property()'s value must be disjunct with
 * @param otherValue the corresponding other value
 */
public record DisjointViolation( EvaluationContext context, Property otherProperty, RDFNode otherValue ) implements Violation {
   public static final String ERROR_CODE = "ERR_DISJOINT";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      return String.format( "Property %s on %s may not have the same value as property %s (%s).",
            context.propertyName(), context.elementName(), context.shortUri( otherProperty.getURI() ), otherValue );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitDisjointViolation( this );
   }
}
