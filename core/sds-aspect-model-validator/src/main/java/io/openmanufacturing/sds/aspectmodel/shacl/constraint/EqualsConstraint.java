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

package io.openmanufacturing.sds.aspectmodel.shacl.constraint;

import java.util.List;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;

import io.openmanufacturing.sds.aspectmodel.shacl.violation.EvaluationContext;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.EqualsViolation;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#EqualsConstraintComponent">sh:equals</a>
 * @param otherProperty this value nodes' value must be less than otherProperty's value
 */
public record EqualsConstraint(Property otherProperty) implements Constraint {
   @Override
   public boolean canBeUsedOnNodeShapes() {
      return false;
   }

   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      final RDFNode otherValue = context.element().getProperty( otherProperty ).getObject();
      return rdfNode.equals( otherValue ) ?
            List.of() :
            List.of( new EqualsViolation( context, otherProperty, otherValue, rdfNode ) );
   }
}
