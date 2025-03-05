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
import org.eclipse.esmf.metamodel.Aspect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Base class for generators that create JSON
 *
 * @param <C> the configuration type
 * @param <R> the result type, e.g., JsonNode or ObjectNode
 * @param <A> the corresponding artifact type
 */
public abstract class JsonGenerator<C extends JsonGenerationConfig, R extends JsonNode, A extends JsonArtifact<R>>
      extends AspectGenerator<String, R, C, A> {
   protected final ObjectMapper objectMapper;

   public JsonGenerator( final Aspect aspect, final C config ) {
      super( aspect, config );

      objectMapper = new ObjectMapper();
      objectMapper.registerModule( new JavaTimeModule() );
      objectMapper.registerModule( new AspectModelJacksonModule() );
      objectMapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
      objectMapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );
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
      try ( final OutputStream output = nameMapper.apply( aspect().getName() ) ) {
         output.write( artifact.serialize() );
         output.flush();
      } catch ( final IOException exception ) {
         throw new DocumentGenerationException( exception );
      }
   }
}
