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

import java.util.Collection;
import java.util.List;

import org.eclipse.esmf.aspectmodel.shacl.Shape;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Implements <a href="https://www.w3.org/TR/shacl/#AndConstraintComponent">sh:and</a>
 */
public class AndConstraint extends AbstractLogicalConstraint {
   public AndConstraint( final List<Shape> shapes ) {
      super( shapes );
   }

   @Override
   public List<Violation> apply( final RDFNode rdfNode, final EvaluationContext context ) {
      final List<List<Violation>> violationsPerConstraint = violationsPerShape( rdfNode, context );
      final long numberOfEmptyViolationLists = numberOfEmptyViolationLists( violationsPerConstraint );
      // The 'and' constraint is evaluated successfully if all of the provided constraints evaluate successfully
      if ( numberOfEmptyViolationLists == 0 ) {
         return List.of();
      }
      return violationsPerConstraint.stream().flatMap( Collection::stream ).toList();
   }

   @Override
   public String name() {
      return "sh:and";
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitAndConstraint( this );
   }
}
