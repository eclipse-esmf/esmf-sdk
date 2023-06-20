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

package org.eclipse.esmf.aspectmodel.shacl.constraint;

import java.util.List;

import org.apache.jena.rdf.model.RDFNode;

import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.ValueFromListViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#InConstraintComponent">sh:in</a> *
 *
 * @param allowedValues the list of allowed values
 */
public record AllowedValuesConstraint( List<RDFNode> allowedValues ) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      return allowedValues.contains( rdfNode ) ?
            List.of() :
            List.of( new ValueFromListViolation( context, allowedValues, rdfNode ) );
   }

   @Override
   public String name() {
      return "sh:in";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitAllowedValuesConstraint( this );
   }
}
