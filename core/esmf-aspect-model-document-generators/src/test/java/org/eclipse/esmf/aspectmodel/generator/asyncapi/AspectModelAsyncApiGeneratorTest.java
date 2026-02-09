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
package org.eclipse.esmf.aspectmodel.generator.asyncapi;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Locale;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class AspectModelAsyncApiGeneratorTest {
   private static final String APPLICATION_ID = "urn:samm:test:test:serve";
   private static final String CHANNEL_ADDRESS = "123/456/test/1.0.0/TestAspect";
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

   @Test
   void testAsyncApiGeneratorEmptyAspect() throws IOException {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT ).aspect();
      final AsyncApiSchemaGenerationConfig config = AsyncApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( false )
            .applicationId( APPLICATION_ID )
            .channelAddress( CHANNEL_ADDRESS )
            .locale( Locale.ENGLISH )
            .build();
      final AsyncApiSchemaArtifact asyncSpec = new AspectModelAsyncApiGenerator( aspect, config ).singleResult();
      final JsonNode json = asyncSpec.getContent();

      final JsonNode expectedJson = OBJECT_MAPPER.readTree(
            """
                     {
                        "asyncapi": "3.0.0",
                        "id": "urn:samm:test:test:serve",
                        "info": {
                           "title": "Test Aspect MQTT API",
                           "version": "v1",
                           "description": "This is a test description",
                           "x-samm-aspect-model-urn":"urn:samm:org.eclipse.esmf.test:1.0.0#Aspect"
                        },
                        "defaultContentType": "application/json",
                        "channels": {
                           "Aspect": {
                              "address": "123/456/test/1.0.0/TestAspect",
                              "description": "This channel for updating Aspect Aspect.",
                              "parameters": {
                                 "namespace": "org.eclipse.esmf.test",
                                 "version": "1.0.0",
                                 "aspect-name": "Aspect"
                              },
                              "messages": {}
                           }
                        },
                        "operations": {},
                        "components": {
                          "messages": {},
                          "schemas": {}
                        }
                     }
                  """
      );

      assertThat( json ).isEqualTo( expectedJson );
   }

   @Test
   void testAsyncApiGeneratorWithoutApplicationIdDoesNotAddEmptyId() throws JsonProcessingException {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_EVENT ).aspect();
      final AsyncApiSchemaGenerationConfig config = AsyncApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( false )
            .channelAddress( CHANNEL_ADDRESS )
            .locale( Locale.ENGLISH )
            .build();

      final AsyncApiSchemaArtifact asyncSpec = new AspectModelAsyncApiGenerator( aspect, config ).singleResult();
      final JsonNode json = asyncSpec.getContent();
      final String result = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString( json );
      assertThat( result ).doesNotContain( "\"id\" : \"\"" );
      assertThat( asyncSpec.getContentAsYaml() ).doesNotContain( "id: \"\"" );
   }

   @Test
   void testAsyncApiGeneratorAspectWithEvent() throws IOException {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_EVENT ).aspect();
      final AsyncApiSchemaGenerationConfig config = AsyncApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( false )
            .applicationId( APPLICATION_ID )
            .channelAddress( CHANNEL_ADDRESS )
            .locale( Locale.ENGLISH )
            .build();
      final AsyncApiSchemaArtifact asyncSpec = new AspectModelAsyncApiGenerator( aspect, config ).singleResult();
      final JsonNode json = asyncSpec.getContent();

      final JsonNode expectedChannels = OBJECT_MAPPER.readTree(
            """
                     {
                        "AspectWithEvent": {
                           "address": "123/456/test/1.0.0/TestAspect",
                           "description": "This channel for updating AspectWithEvent Aspect.",
                           "parameters": {
                              "namespace": "org.eclipse.esmf.test",
                              "version": "1.0.0",
                              "aspect-name": "AspectWithEvent"
                           },
                           "messages": {
                              "SomeEvent": {
                                 "$ref": "#/components/messages/SomeEvent"
                              }
                           }
                        }
                     }
                  """ );
      final JsonNode expectedComponentsMessages = OBJECT_MAPPER.readTree(
            """
                     {
                        "SomeEvent": {
                           "name": "SomeEvent",
                           "title": "Some Event",
                           "summary": "This is some event",
                           "contentType": "application/json",
                           "payload": {
                              "$ref": "#/components/schemas/SomeEvent"
                           }
                        }
                     }
                  """ );
      final JsonNode expectedComponentsSchemas = OBJECT_MAPPER.readTree(
            """
                     {
                        "type": "object",
                        "properties": {
                           "testProperty": {
                              "title": "testProperty",
                              "type": "string",
                              "description": "This is a test property."
                           }
                        }
                     }
                  """ );

      assertThat( json.get( "info" ).get( "title" ).asText() ).isEqualTo( "Test Aspect MQTT API" );
      assertThat( json.get( "info" ).get( "description" ).asText() ).isEqualTo( "This is a test description" );
      assertThat( json.get( "channels" ) ).isEqualTo( expectedChannels );
      assertThat( json.get( "components" ).get( "messages" ) ).isEqualTo( expectedComponentsMessages );
      assertThat( json.get( "components" ).get( "schemas" ).get( aspect.getEvents().get( 0 ).getName() ) ).isEqualTo(
            expectedComponentsSchemas );
   }

   @Test
   void testAsyncApiGeneratorAspectWithOperation() throws IOException {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_OPERATION ).aspect();
      final AsyncApiSchemaGenerationConfig config = AsyncApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( false )
            .applicationId( APPLICATION_ID )
            .channelAddress( CHANNEL_ADDRESS )
            .locale( Locale.ENGLISH )
            .build();
      final AsyncApiSchemaArtifact asyncSpec = new AspectModelAsyncApiGenerator( aspect, config ).singleResult();
      final JsonNode json = asyncSpec.getContent();

      final JsonNode expectedChannels = OBJECT_MAPPER.readTree(
            """
                     {
                        "AspectWithOperation": {
                           "address": "123/456/test/1.0.0/TestAspect",
                           "description": "This channel for updating AspectWithOperation Aspect.",
                           "parameters": {
                              "namespace": "org.eclipse.esmf.test",
                              "version": "1.0.0",
                              "aspect-name": "AspectWithOperation"
                           },
                           "messages": {
                              "input": {
                                 "$ref": "#/components/messages/input"
                              },
                              "output": {
                                 "$ref": "#/components/messages/output"
                              }
                           }
                        }
                     }
                  """ );
      final JsonNode expectedComponentsMessageInput = OBJECT_MAPPER.readTree(
            """
                  {
                     "name": "input",
                     "title": "input",
                     "summary": null,
                     "contentType": "application/json",
                     "payload": {
                        "$ref": "#/components/schemas/input"
                     }
                  }
                  """
      );
      final JsonNode expectedComponentsMessageOutput = OBJECT_MAPPER.readTree(
            """
                     {
                        "name": "output",
                        "title": "output",
                        "summary": null,
                        "contentType": "application/json",
                        "payload": {
                           "$ref": "#/components/schemas/output"
                        }
                     }
                  """
      );
      final JsonNode expectedComponentsSchemaInput = OBJECT_MAPPER.readTree(
            """
                     {
                        "type": "string",
                        "description": ""
                     }
                  """
      );
      final JsonNode expectedComponentsSchemaOutput = OBJECT_MAPPER.readTree(
            """
                  {
                     "type": "string",
                     "description": ""
                  }
                  """
      );

      assertThat( json.get( "info" ).get( "title" ).asText() ).isEqualTo( "AspectWithOperation MQTT API" );
      assertThat( json.get( "info" ).get( "description" ).asText() ).isEmpty();
      assertThat( json.get( "channels" ) ).isEqualTo( expectedChannels );
      assertThat( json.get( "components" ).get( "messages" ).get( "input" ) ).isEqualTo( expectedComponentsMessageInput );
      assertThat( json.get( "components" ).get( "messages" ).get( "output" ) ).isEqualTo( expectedComponentsMessageOutput );
      assertThat( json.get( "components" ).get( "schemas" ).get( "input" ) ).isEqualTo( expectedComponentsSchemaInput );
      assertThat( json.get( "components" ).get( "schemas" ).get( "output" ) ).isEqualTo( expectedComponentsSchemaOutput );
   }
}
