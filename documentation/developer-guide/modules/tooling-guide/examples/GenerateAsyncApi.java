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
import java.nio.charset.StandardCharsets;

import org.eclipse.esmf.aspectmodel.generator.asyncapi.AspectModelAsyncApiGenerator;
import org.eclipse.esmf.aspectmodel.generator.asyncapi.AsyncApiSchemaGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.asyncapi.AsyncApiSchemaGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.metamodel.AspectModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
// end::imports[]

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class GenerateAsyncApi {
   @Test
   public void generateYaml() throws IOException {
      // tag::generateYaml[]
      // AspectModel as returned by the AspectModelLoader
      final AspectModel aspectModel = // ...
            // end::generate[]
            new AspectModelLoader().load(
                  new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" ) );
      // tag::generateYaml[]

      final ObjectMapper yamlMapper = new YAMLMapper().enable( YAMLGenerator.Feature.MINIMIZE_QUOTES );

      final AsyncApiSchemaGenerationConfig config = AsyncApiSchemaGenerationConfigBuilder.builder()
            // i.e., true = v1.2.3, false = v1
            .useSemanticVersion( false )
            // Set Application id
            .applicationId( "test:serve" )
            .channelAddress( "/123-456/789-012/movement/0.0.1/Movement" )
            .build();

      // Generate pretty-printed YAML
      final AspectModelAsyncApiGenerator generator = new AspectModelAsyncApiGenerator( aspectModel.aspect(), config );
      final JsonNode json = generator.getContent();
      final String yaml = yamlMapper.writeValueAsString( json );

      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      out.write( yaml.getBytes( StandardCharsets.UTF_8 ) );

      final String result = out.toString();
      // end::generateYaml[]
   }

   @Test
   public void generateJson() throws IOException {
      // tag::generateJson[]
      // AspectModel as returned by the AspectModelLoader
      final AspectModel aspectModel = // ...
            // end::generate[]
            new AspectModelLoader().load(
                  new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" ) );
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
      final AspectModelAsyncApiGenerator generator = new AspectModelAsyncApiGenerator( aspectModel.aspect(), config );
      final JsonNode json = generator.getContent();

      // If needed, print or pretty print it into a string
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      objectMapper.writerWithDefaultPrettyPrinter().writeValue( out, json );
      final String result = out.toString();
      // end::generateJson[]
   }
}
