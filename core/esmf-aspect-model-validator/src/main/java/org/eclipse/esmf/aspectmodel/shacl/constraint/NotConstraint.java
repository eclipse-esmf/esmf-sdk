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
import org.eclipse.esmf.aspectmodel.shacl.violation.NotViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#NotConstraintComponent">sh:not</a>
 */
public record NotConstraint( Constraint constraint ) implements Constraint {
   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      final List<Violation> violations = constraint.apply( rdfNode, context );

      // If the wrapped constrained has _no_ violations, we return a violation
      if ( violations.isEmpty() ) {
         return List.of( new NotViolation( context, constraint ) );
      }
      // If the wrapped constraint has violations, ignore them
      return List.of();
   }

   @Override
   public String name() {
      return "sh:not";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitNotConstraint( this );
   }
}
