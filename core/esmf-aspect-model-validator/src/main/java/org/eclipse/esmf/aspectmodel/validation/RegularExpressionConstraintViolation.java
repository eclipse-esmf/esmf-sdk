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
import org.apache.jena.rdf.model.Resource;

/**
 * Violation for regular expressions that are too complex to automatically generate example values.
 *
 * @param path the property where the problematic regular expression is defined
 * @param regexp the problematic regular expression
 */
public record RegularExpressionConstraintViolation( Resource path, String regexp ) implements Violation {
   public static final String ERROR_CODE = "ERR_EMPTY_EXAMPLE_VALUE";

   @Override
   public EvaluationContext context() {
      return null;
   }

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public String violationSpecificMessage() {
      return String.format(
            "Cannot automatically generate an example value for property '%s' due to a complex or unsupported regular expression:%n    %s%n"
                  + "To resolve this, please do one of the following:%n"
                  + "  1. Add an example value manually for this property.%n"
                  + "  2. Simplify the regular expression so that an example can be generated automatically.",
            path.getModel().shortForm( path.getURI() ), regexp
      );
   }

   @Override
   public String message() {
      return "Cannot automatically generate an example value due to a complex or unsupported regular expression";
   }

   @Override
   public RDFNode highlight() {
      return Optional.ofNullable( path )
            .filter( property -> TokenRegistry.getToken( property.asNode() ).isPresent() )
            .map( resource -> resource.as( RDFNode.class ) )
            .orElse( path );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitRegularExpressionConstraint( this );
   }
}