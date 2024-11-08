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

import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.JsonGenerator;
import org.eclipse.esmf.metamodel.Aspect;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Generator that generates a JSON Schema for payloads corresponding to a given Aspect model.
 */
public class AspectModelJsonSchemaGenerator extends JsonGenerator<JsonSchemaGenerationConfig, JsonNode, JsonSchemaArtifact> {
   public static final String SAMM_EXTENSION = "x-samm-aspect-model-urn";

   public static final JsonSchemaGenerationConfig DEFAULT_CONFIG = JsonSchemaGenerationConfigBuilder.builder().build();

   /**
    * Kept for backwards compatibility
    */
   @Deprecated( forRemoval = true )
   public static final AspectModelJsonSchemaGenerator INSTANCE = new AspectModelJsonSchemaGenerator( null );

   public AspectModelJsonSchemaGenerator( final Aspect aspect ) {
      this( aspect, DEFAULT_CONFIG );
   }

   public AspectModelJsonSchemaGenerator( final Aspect aspect, final JsonSchemaGenerationConfig config ) {
      super( aspect, config );
   }

   /**
    * @deprecated Use {@link #AspectModelJsonSchemaGenerator(Aspect, JsonSchemaGenerationConfig)} and {@link #singleResult()} instead
    */
   @Deprecated( forRemoval = true )
   public JsonSchemaArtifact apply( final Aspect aspect, final JsonSchemaGenerationConfig config ) {
      return new AspectModelJsonSchemaGenerator( aspect, config ).singleResult();
   }

   @Override
   public Stream<JsonSchemaArtifact> generate() {
      final AspectModelJsonSchemaVisitor visitor = new AspectModelJsonSchemaVisitor( config );
      final JsonNode result = aspect().accept( visitor, null );
      return Stream.of( new JsonSchemaArtifact( aspect().getName(), result ) );
   }
}
