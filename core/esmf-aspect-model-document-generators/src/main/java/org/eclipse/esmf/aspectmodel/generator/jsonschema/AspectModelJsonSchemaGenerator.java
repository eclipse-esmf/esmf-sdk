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

import org.eclipse.esmf.aspectmodel.generator.ArtifactGenerator;
import org.eclipse.esmf.metamodel.Aspect;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Generator that generates a JSON Schema for payloads corresponding to a given Aspect model.
 */
public class AspectModelJsonSchemaGenerator implements
      ArtifactGenerator<String, JsonNode, Aspect, JsonSchemaGenerationConfig, JsonSchemaArtifact> {
   public static final AspectModelJsonSchemaGenerator INSTANCE = new AspectModelJsonSchemaGenerator();

   @Override
   public JsonSchemaArtifact apply( final Aspect aspect, final JsonSchemaGenerationConfig config ) {
      final AspectModelJsonSchemaVisitor visitor = new AspectModelJsonSchemaVisitor( config );
      final JsonNode result = aspect.accept( visitor, null );
      return new JsonSchemaArtifact( aspect.getName(), result );
   }
}
