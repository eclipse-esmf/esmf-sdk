/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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
import java.util.function.Supplier;

import org.apache.jena.rdf.model.Resource;

import org.eclipse.esmf.aspectmodel.DocumentLocationViolation;
import org.eclipse.esmf.aspectmodel.Location;
import org.eclipse.esmf.aspectmodel.RdfUtil;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * A value (literal) value was given with an invalid value, e.g., "9999"^^xsd:byte
 *
 * @param type the URI of the type
 * @param value the invalid value
 * @param sourceLine the line in the source document containing the problem
 * @param sourceDocument the source location of the violation
 * @param location the source location of the violation
 * @param documentContent a supplier for the content of the source document
 */
@RecordBuilder
public record InvalidLexicalValueViolation(
      Resource type,
      Object value,
      String sourceLine,
      URI sourceDocument,
      Location location,
      Supplier<String> documentContent
) implements DocumentLocationViolation {
   public static final String ERROR_CODE = "ERR_INVALID_LEXICAL_VALUE";

   @Override
   public Code code() {
      return () -> ERROR_CODE;
   }

   @Override
   public String message() {
      return "'%s' is no valid value for type %s".formatted( value, RdfUtil.curie( type.getURI() ) );
   }
}
