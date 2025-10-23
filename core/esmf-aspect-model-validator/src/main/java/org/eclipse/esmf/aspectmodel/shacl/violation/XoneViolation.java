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

import org.eclipse.esmf.aspectmodel.shacl.ShapeSummarizer;
import org.eclipse.esmf.aspectmodel.shacl.constraint.XoneConstraint;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Violation of a {@link XoneConstraint}
 *
 * @param context the evaluation context
 * @param violations the list of violations that were encountered
 */
public record XoneViolation( EvaluationContext context, List<Violation> violations, XoneConstraint constraint ) implements Violation {
   public static final String ERROR_CODE = "ERR_XONE";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      if ( violations().size() == 1 ) {
         return violations.get( 0 ).message();
      }

      if ( !violations().isEmpty() ) {
         return "Exactly one of the following violations"
               + ( context.property().isPresent() ? " for " + context.propertyName() : "" )
               + " must be fixed: "
               + IntStream.range( 0, violations.size() )
               .mapToObj( i -> String.format( "(%d) %s", i + 1, violations().get( i ).message().replaceAll( "\\.$", "" ) ) )
               .collect( Collectors.joining( ", " ) ) + ".";
      }
      // A Xone violation can be triggered when there are zero violations: Since xone states that exactly one of the linked shapes
      // must cause a violation, but when none do, this is itself a violation of the xone. In this case, we can not rely on the details
      // of the violations for the linked shapes to construct an error message, since there are none.
      // Instead, we build a string representation of the shapes referenced by the xone.
      final ShapeSummarizer summarizer = new ShapeSummarizer( context );
      return "Exactly one of the following conditions should lead to a violation, but all of them passed successfully: "
            + IntStream.range( 0, constraint.shapes().size() )
            .mapToObj( i -> String.format( "(%d) %s", i + 1, constraint().shapes().get( i ).accept( summarizer ) ) )
            .collect( Collectors.joining( ", " ) ) + ".";
   }

   @Override
   public RDFNode highlight() {
      return context().property().isPresent() ? context().property().get() : context().element();
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitXoneViolation( this );
   }
}
