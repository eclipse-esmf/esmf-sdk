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

package org.eclipse.esmf.aspectmodel.generator.jsonschema;

import java.util.Locale;
import java.util.function.BiFunction;

import org.eclipse.esmf.metamodel.Aspect;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Generator that generates a JSON Schema for payloads corresponding to a given Aspect model.
 */
public class AspectModelJsonSchemaGenerator implements BiFunction<Aspect, Locale, JsonNode> {
   @Override
   public JsonNode apply( final Aspect aspect, final Locale locale ) {
      final AspectModelJsonSchemaVisitor visitor = new AspectModelJsonSchemaVisitor( true, locale );
      return visitor.visitAspect( aspect, null );
   }

   public JsonNode applyForOpenApi( final Aspect aspect, final Locale locale, final boolean generateCommentForSeeAttributes ) {
      final AspectModelJsonSchemaVisitor visitor = new AspectModelJsonSchemaVisitor( false, locale, generateCommentForSeeAttributes );
      return visitor.visitAspectForOpenApi( aspect );
   }
}
