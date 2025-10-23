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

import java.net.URI;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.aspectmodel.shacl.violation.EvaluationContext;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;

import org.apache.jena.rdf.model.Resource;

/**
 * A value (literal) value was given with an invalid value, e.g., "9999"^^xsd:byte
 *
 * @param type the URI of the type
 * @param value the invalid value
 * @param location the source location of the violation
 */
public record InvalidLexicalValueViolation( Resource type, Object value, int line, int column, String sourceLine, URI location )
      implements Violation {
   public static final String ERROR_CODE = "ERR_INVALID_LEXICAL_VALUE";

   @Override
   public String errorCode() {
      return ERROR_CODE;
   }

   @Override
   public EvaluationContext context() {
      return null;
   }

   @Override
   public String violationSpecificMessage() {
      return "'%s' is no valid value for type %s".formatted( value, RdfUtil.curie( type.getURI() ) );
   }

   @Override
   public String message() {
      return "Invalid value";
   }

   @Override
   public Optional<URI> sourceLocation() {
      return Optional.of( location() );
   }

   @Override
   public <T> T accept( final Visitor<T> visitor ) {
      return visitor.visitInvalidLexicalValueViolation( this );
   }
}
