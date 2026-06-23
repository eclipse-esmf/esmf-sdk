/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;

import org.eclipse.esmf.aspectmodel.jackson.AspectModelJacksonModule;
import org.eclipse.esmf.metamodel.StructureElement;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Base class for generators that create JSON
 *
 * @param <S> the input element type, e.g., Aspect or Entity
 * @param <C> the configuration type
 * @param <R> the result type, e.g., JsonNode or ObjectNode
 * @param <A> the corresponding artifact type
 */
public abstract class JsonGenerator<S extends StructureElement, C extends JsonGenerationConfig, R extends JsonNode,
      A extends JsonArtifact<R>>
      extends StructureElementGenerator<S, String, R, C, A> {
   protected final ObjectMapper objectMapper;

   public JsonGenerator( final S element, final C config ) {
      super( element, config );

      objectMapper = JsonMapper.builder()
            .addModule( new AspectModelJacksonModule() )
            .configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false )
            .build();
   }

   /**
    * Generate a pretty-printed JSON output
    *
    * @return the formatted JSON string
    */
   public String generateJson() {
      return getContent().toPrettyString();
   }

   /**
    * Generate a pretty-printed YAML output
    *
    * @return the formatted JSON string
    */
   public String generateYaml() {
      return singleResult().getContentAsYaml();
   }

   @Override
   protected void write( final Artifact<String, R> artifact, final Function<String, OutputStream> nameMapper ) {
      try ( final OutputStream output = nameMapper.apply( structureElement().getName() ) ) {
         output.write( artifact.serialize() );
         output.flush();
      } catch ( final IOException exception ) {
         throw new DocumentGenerationException( exception );
      }
   }
}
