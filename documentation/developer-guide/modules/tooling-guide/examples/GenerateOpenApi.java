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

package examples;

// tag::imports[]
import org.eclipse.esmf.aspectmodel.generator.openapi.AspectModelOpenApiGenerator;
import org.eclipse.esmf.aspectmodel.generator.openapi.OpenApiSchemaGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.openapi.OpenApiSchemaGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.generator.openapi.PagingOption;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.metamodel.AspectModel;

import com.fasterxml.jackson.databind.JsonNode;
// end::imports[]
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class GenerateOpenApi {
   @Test
   public void generateYaml() throws JsonProcessingException {
      // tag::generateYaml[]
      // AspectModel as returned by the AspectModelLoader
      final AspectModel aspectModel = // ...
            // end::generateYaml[]
            new AspectModelLoader().load(
                  new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" ) );
      // tag::generateYaml[]

      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            // Server URL
            .baseUrl( "http://www.example.com" )
            // The resource path which shall be added
            .resourcePath( "/testPath/{parameter}" )
            // Determines whether semantic versioning should be used for the API
            // i.e., true = v1.2.3, false = v1
            .useSemanticVersion( false )
            // A String containing all the information for dynamic properties mentioned in the resource path.
            // The string must be syntactically valid YAML.
            .properties( readYaml( """
                  parameter:
                    name: parameter
                    in: path
                    description: "A parameter."
                    required: true
                    schema:
                      type: string
                  """ ) )
            // Should the query API be added to the generated specification?
            .includeQueryApi( true )
            // The paging Option to be chosen. In case paging is possible:
            // The default for a time related collection is time-based paging.
            // Otherwise the default is offset-based paging.
            .pagingOption( PagingOption.OFFSET_BASED_PAGING )
            .build();

      // Generate pretty-printed YAML
      final AspectModelOpenApiGenerator generator = new AspectModelOpenApiGenerator( aspectModel.aspect(), config );
      final String yaml = generator.generateYaml();
      // end::generateYaml[]
   }

   @Test
   public void generateJson() throws IOException {
      // tag::generateJson[]
      // AspectModel as returned by the AspectModelLoader
      final AspectModel aspectModel = // ...
            // end::generateJson[]
            new AspectModelLoader().load(
                  new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" ) );
      // tag::generateJson[]

      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            // Server URL
            .baseUrl( "http://www.example.com" )
            // The resource path which shall be added
            .resourcePath( "/testPath/{parameter}" )
            // Determines whether semantic versioning should be used for the API
            // i.e., true = v1.2.3, false = v1
            .useSemanticVersion( false )
            // A JsonNode containing all the information for variable properties mentioned
            // in the resource path .
            .properties( readYaml( """
                     {
                       "test-Id": {
                         "name": "test-Id",
                         "in": "path",
                         "description": "The ID of the unit.",
                         "required": true,
                         "schema": {
                           "type": "string"
                         }
                       }
                     }
                  """ ) )
            // Should the query API be added to the generated specification?
            .includeQueryApi( true )
            // The paging Option to be chosen. In case paging is possible:
            // The default for a time related collection is time-based paging.
            // Otherwise the default is offset-based paging.
            .pagingOption( PagingOption.OFFSET_BASED_PAGING )
            .build();

      // Generate the JSON
      final AspectModelOpenApiGenerator generator = new AspectModelOpenApiGenerator( aspectModel.aspect(), config );
      // Get result as type-safe JSON object
      final JsonNode jsonNode = generator.getContent();
      // Or as pretty-printed JSON String:
      final String json = generator.generateJson();
      // end::generateJson[]
   }

   private static ObjectNode readYaml( String content ) throws JsonProcessingException {
      return (ObjectNode) new YAMLMapper().readTree( content );
   }
}
