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

import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.InvalidValueViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#HasValueConstraintComponent">sh:hasValue</a>
 *
 * @param allowedValue the allowed value
 */
public record HasValueConstraint( RDFNode allowedValue ) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      return rdfNode.equals( allowedValue )
            ? List.of()
            : List.of( new InvalidValueViolation( context, allowedValue, rdfNode ) );
   }

   @Override
   public String name() {
      return "sh:hasValue";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitHasValueConstraint( this );
   }
}
