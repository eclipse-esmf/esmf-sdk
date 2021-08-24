/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.generator.jsonschema;

import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import io.openmanufacturing.sds.metamodel.Aspect;

/**
 * Generator that generates a JSON Schema for payloads corresponding to a given Aspect model.
 */
public class AspectModelJsonSchemaGenerator implements Function<Aspect, JsonNode> {

   @Override
   public JsonNode apply( final Aspect aspect ) {
      final AspectModelJsonSchemaVisitor visitor = new AspectModelJsonSchemaVisitor(true);
      return visitor.visitAspect( aspect, null );
   }

   public JsonNode applyForOpenApi( final Aspect aspect ) {
      final AspectModelJsonSchemaVisitor visitor = new AspectModelJsonSchemaVisitor(false);
      return visitor.visitAspectForOpenApi( aspect );
   }
}
