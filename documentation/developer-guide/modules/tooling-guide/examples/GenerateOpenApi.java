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
import java.io.ByteArrayOutputStream;
import java.util.Optional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.esmf.aspectmodel.generator.openapi.AspectModelOpenApiGenerator;
import org.eclipse.esmf.aspectmodel.generator.openapi.PagingOption;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
// end::imports[]
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class GenerateOpenApi {
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

      // Server URL
      final String baseUrl = "http://www.example.com";

      // The resource path which shall be added
      final Optional<String> resourcePath = Optional.of( "/testPath/{parameter}" );

      // A String containing all the information for dynamic properties mentioned in the resource path.
      // The string must be syntactically valid YAML.
      final Optional<String> yamlProperties = Optional.of( """
            parameter:
              name: parameter
              in: path
              description: "A parameter."
              required: true
              schema:
                type: string
            """ );

      // Should the query API be added to the generated specification?
      final boolean includeQueryApi = true;

      // The paging Option to be chosen. In case paging is possible:
      // The default for a time related collection is time-based paging.
      // Otherwise the default is offset-based paging.
      final Optional<PagingOption> pagingOption = Optional.of( PagingOption.OFFSET_BASED_PAGING );

      // Determines whether semantic versioning should be used for the API
      // i.e., true = v1.2.3, false = v1
      final boolean useSemanticVersion = false;

      // Generate pretty-printed YAML
      final AspectModelOpenApiGenerator generator = new AspectModelOpenApiGenerator();
      final String yaml = generator.applyForYaml( aspect, useSemanticVersion, baseUrl,
            resourcePath, yamlProperties, includeQueryApi, pagingOption );
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

      // Server URL
      final String baseUrl = "http://www.example.com";

      // The resource path which shall be added
      final Optional<String> resourcePath = Optional.of( "/testPath/{parameter}" );

      // Determines whether semantic versioning should be used for the API
      // i.e., true = v1.2.3, false = v1
      final boolean useSemanticVersion = false;

      // A JsonNode containing all the information for variable properties mentioned
      // in the resource path .
      final ObjectMapper objectMapper = new ObjectMapper();
      final Optional<JsonNode> jsonProperties = Optional.of( objectMapper.readTree( """
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
            """ ) );

      // Should the query API be added to the generated specification?
      final boolean includeQueryApi = true;

      // The paging Option to be chosen. In case paging is possible:
      // The default for a time related collection is time-based paging.
      // Otherwise the default is offset-based paging.
      final Optional<PagingOption> pagingOption = Optional.of( PagingOption.OFFSET_BASED_PAGING );

      // Generate the JSON
      final AspectModelOpenApiGenerator generator = new AspectModelOpenApiGenerator();
      final JsonNode json = generator.applyForJson( aspect, useSemanticVersion, baseUrl,
            resourcePath, jsonProperties, includeQueryApi, pagingOption );

      // If needed, print or pretty print it into a string
      final ByteArrayOutputStream out = new ByteArrayOutputStream();

      objectMapper.writerWithDefaultPrettyPrinter().writeValue( out, json );
      final String result = out.toString();
      // end::generateJson[]
   }
}
