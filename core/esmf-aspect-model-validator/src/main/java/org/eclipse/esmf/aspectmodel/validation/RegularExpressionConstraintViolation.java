/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.validation;

import java.util.Optional;

import org.eclipse.esmf.aspectmodel.resolver.parser.TokenRegistry;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Violation for regular expressions that are too complex to automatically generate example values.
 *
 * @param context the evaluation context
 * @param regexp the problematic regular expression
 */
public record RegularExpressionConstraintViolation( EvaluationContext context, String regexp ) implements Violation {
   public static final String ERROR_CODE = "ERR_EMPTY_EXAMPLE_VALUE";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String message() {
      return violationSpecificMessage();
   }

   @Override
   public String violationSpecificMessage() {
      return "Regular expression on %s is invalid: '%s'.".formatted( context.value( context.element() ), regexp );
   }

   @Override
   public RDFNode highlight() {
      return Optional.ofNullable( context.element() )
            .filter( property -> TokenRegistry.getToken( property.asNode() ).isPresent() )
            .map( resource -> resource.as( RDFNode.class ) )
            .orElse( context.element() );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitRegularExpressionConstraint( this );
   }
}