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

package examples;

// tag::imports[]

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.esmf.aspectmodel.generator.asyncapi.AspectModelAsyncApiGenerator;
import org.eclipse.esmf.aspectmodel.generator.asyncapi.AsyncApiSchemaGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.asyncapi.AsyncApiSchemaGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class GenerateAsyncApi {
   @Test
   public void generateYaml() throws JsonProcessingException {
      final File modelFile = new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" );

      // tag::generateYaml[]
      // Aspect as created by the AspectModelLoader
      final Aspect aspect = // ...
            // end::generateYaml[]
            // exclude the actual loading from the example to reduce noise
            AspectModelResolver.loadAndResolveModel( modelFile ).flatMap( AspectModelLoader::getSingleAspect ).get();
      // tag::generateYaml[]

      ObjectMapper YAML_MAPPER = new YAMLMapper().enable( YAMLGenerator.Feature.MINIMIZE_QUOTES );
      //language=yaml
      final AsyncApiSchemaGenerationConfig config = AsyncApiSchemaGenerationConfigBuilder.builder()
            // i.e., true = v1.2.3, false = v1
            .useSemanticVersion( false )
            // Set Application id
            .applicationId( "test:serve" )
            .channelAddress( "/123-456/789-012/movement/0.0.1/Movement" )
            .build();

      // Generate pretty-printed YAML
      final AspectModelAsyncApiGenerator generator = new AspectModelAsyncApiGenerator();
      final JsonNode json = generator.apply( aspect, config ).getContent();
      final String yaml = YAML_MAPPER.writeValueAsString( json );

      final ByteArrayOutputStream out = new ByteArrayOutputStream();

      try {
         out.write( yaml.getBytes( StandardCharsets.UTF_8 ) );
      } catch ( IOException e ) {
         throw new RuntimeException( e );
      }

      final String result = out.toString();
      // end::generateYaml[]
   }

   @Test
   public void generateJson() throws IOException {
      final File modelFile = new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" );
      // tag::generateJson[]
      // Aspect as created by the AspectModelLoader
      final Aspect aspect = // ...
            // end::generateJson[]
            // exclude the actual loading from the example to reduce noise
            AspectModelResolver.loadAndResolveModel( modelFile ).flatMap( AspectModelLoader::getSingleAspect ).get();
      // tag::generateJson[]

      final ObjectMapper objectMapper = new ObjectMapper();
      final AsyncApiSchemaGenerationConfig config = AsyncApiSchemaGenerationConfigBuilder.builder()
            // i.e., true = v1.2.3, false = v1
            .useSemanticVersion( false )
            // Set Application id
            .applicationId( "test:serve" )
            .channelAddress( "/123-456/789-012/movement/0.0.1/Movement" )
            .build();

      // Generate the JSON
      final AspectModelAsyncApiGenerator generator = new AspectModelAsyncApiGenerator();
      final JsonNode json = generator.apply( aspect, config ).getContent();

      // If needed, print or pretty print it into a string
      final ByteArrayOutputStream out = new ByteArrayOutputStream();

      objectMapper.writerWithDefaultPrettyPrinter().writeValue( out, json );
      final String result = out.toString();
      // end::generateJson[]
   }
}
