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
                           "description": "Channel for updating Aspect Aspect.",
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
                        "description": "Channel for updating AspectWithEvent Aspect.",
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
                       "SomeEvent" : {
                         "type" : "object",
                         "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.test:1.0.0#SomeEvent",
                         "properties" : {
                           "testProperty" : {
                             "description" : "This is a test property.",
                             "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.test:1.0.0#testProperty",
                             "allOf" : [ {
                               "$ref" : "#/components/schemas/Text"
                             } ]
                           }
                         },
                         "required" : [ "testProperty" ]
                       },
                       "Text" : {
                         "type" : "string",
                         "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#Text",
                         "description" : "Describes a Property which contains plain text. \
This is intended exclusively for human readable strings, not for identifiers, measurement values, etc."
                       }
                  }
               """ );

      assertThat( json.get( "info" ).get( "title" ).asText() ).isEqualTo( "Test Aspect MQTT API" );
      assertThat( json.get( "info" ).get( "description" ).asText() ).isEqualTo( "This is a test description" );
      assertThat( json.get( "channels" ) ).isEqualTo( expectedChannels );
      assertThat( json.get( "components" ).get( "messages" ) ).isEqualTo( expectedComponentsMessages );
      assertThat( json.get( "components" ).get( "schemas" ) ).isEqualTo(
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
                       "AspectWithOperation" : {
                         "address" : "123/456/test/1.0.0/TestAspect",
                         "description" : "Channel for updating AspectWithOperation Aspect.",
                         "parameters" : {
                           "namespace" : "org.eclipse.esmf.test",
                           "version" : "1.0.0",
                           "aspect-name" : "AspectWithOperation"
                         },
                         "messages" : {
                           "testOperationRequest" : {
                             "$ref" : "#/components/messages/testOperationRequest"
                           },
                           "testOperationResponse" : {
                             "$ref" : "#/components/messages/testOperationResponse"
                           },
                           "testOperationTwoRequest" : {
                             "$ref" : "#/components/messages/testOperationTwoRequest"
                           },
                           "testOperationTwoResponse" : {
                             "$ref" : "#/components/messages/testOperationTwoResponse"
                           }
                         }
                       }
                     }
               """ );

      final JsonNode expectedComponentsMessages = OBJECT_MAPPER.readTree(
            """
                 {
                      "testOperationRequest" : {
                        "name" : "testOperationRequest",
                        "title" : "First Test Operation",
                        "contentType" : "application/json",
                        "payload" : {
                          "$ref" : "#/components/schemas/testOperationRequest"
                        },
                        "summary" : "Description of the first test operation"
                      },
                      "testOperationResponse" : {
                        "name" : "testOperationResponse",
                        "title" : "First Test Operation",
                        "contentType" : "application/json",
                        "payload" : {
                          "$ref" : "#/components/schemas/testOperationResponse"
                        },
                        "summary" : "Description of the first test operation"
                      },
                      "testOperationTwoRequest" : {
                        "name" : "testOperationTwoRequest",
                        "title" : "Second Test Operation",
                        "contentType" : "application/json",
                        "payload" : {
                          "$ref" : "#/components/schemas/testOperationTwoRequest"
                        },
                        "summary" : "Description of the second test operation. Has two inputs"
                      },
                      "testOperationTwoResponse" : {
                        "name" : "testOperationTwoResponse",
                        "title" : "Second Test Operation",
                        "contentType" : "application/json",
                        "payload" : {
                          "$ref" : "#/components/schemas/testOperationTwoResponse"
                        },
                        "summary" : "Description of the second test operation. Has two inputs"
                      }
                    }
               """
      );
      final JsonNode expectedComponentsSchemas = OBJECT_MAPPER.readTree(
            """
                  {
                       "testOperationRequest" : {
                         "title" : "First Test Operation",
                         "allOf" : [ {
                           "description" : "Description of a text property that is used for input",
                           "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.test:1.0.0#input",
                           "allOf" : [ {
                             "$ref" : "#/components/schemas/Text"
                           } ]
                         } ]
                       },
                       "testOperationResponse" : {
                         "description" : "Description of a text property that is used for output",
                         "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.test:1.0.0#output",
                         "allOf" : [ {
                           "$ref" : "#/components/schemas/Text"
                         } ]
                       },
                       "testOperationTwoRequest" : {
                         "title" : "Second Test Operation",
                         "allOf" : [ {
                           "description" : "Description of a text property that is used for input",
                           "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.test:1.0.0#input",
                           "allOf" : [ {
                             "$ref" : "#/components/schemas/Text"
                           } ]
                         }, {
                           "description" : "Description of a second text property that is used for input",
                           "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.test:1.0.0#input2",
                           "allOf" : [ {
                             "$ref" : "#/components/schemas/Text"
                           } ]
                         } ]
                       },
                       "testOperationTwoResponse" : {
                         "description" : "Description of a text property that is used for output",
                         "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.test:1.0.0#output",
                         "allOf" : [ {
                           "$ref" : "#/components/schemas/Text"
                         } ]
                       },
                       "Text" : {
                         "type" : "string",
                         "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#Text",
                         "description" : "Describes a Property which contains plain text. \
This is intended exclusively for human readable strings, not for identifiers, measurement values, etc."
                       }
                  }
               """
      );

      assertThat( json.get( "info" ).get( "title" ).asText() ).isEqualTo( "AspectWithOperation MQTT API" );
      assertThat( json.get( "info" ).get( "description" ).asText() ).isEmpty();
      assertThat( json.get( "channels" ) ).isEqualTo( expectedChannels );
      assertThat( json.get( "components" ).get( "messages" ) ).isEqualTo( expectedComponentsMessages );
      assertThat( json.get( "components" ).get( "schemas" ) ).isEqualTo( expectedComponentsSchemas );
   }

   @Test
   void testAsyncApiGeneratorAspectWithEventAndEntityProperty() throws IOException {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_EVENT_AND_ENTITY_PROPERTY ).aspect();
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
                    "AspectWithEventAndEntityProperty" : {
                      "address" : "123/456/test/1.0.0/TestAspect",
                      "description" : "Channel for updating AspectWithEventAndEntityProperty Aspect.",
                      "parameters" : {
                        "namespace" : "org.eclipse.esmf.test",
                        "version" : "1.0.0",
                        "aspect-name" : "AspectWithEventAndEntityProperty"
                      },
                      "messages" : {
                        "EntityEvent" : {
                          "$ref" : "#/components/messages/EntityEvent"
                        }
                      }
                    }
                  }
               """ );

      final JsonNode expectedComponentsMessages = OBJECT_MAPPER.readTree(
            """
                  {
                    "EntityEvent" : {
                      "name" : "EntityEvent",
                      "title" : "Entity Event",
                      "summary" : "This is an event with entity properties",
                      "contentType" : "application/json",
                      "payload" : {
                        "$ref" : "#/components/schemas/EntityEvent"
                      }
                    }
                  }
               """
      );
      final JsonNode expectedComponentsSchemas = OBJECT_MAPPER.readTree(
            """
                  {
                    "EntityEvent" : {
                      "type" : "object",
                      "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.test:1.0.0#EntityEvent",
                      "properties" : {
                        "entityProperty" : {
                          "description" : "This is a property with an entity type.",
                          "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.test:1.0.0#entityProperty",
                          "allOf" : [ {
                            "$ref" : "#/components/schemas/EntityCharacteristic"
                          } ]
                        },
                        "testString" : {
                          "description" : "A scalar test property.",
                          "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.test:1.0.0#testString",
                          "allOf" : [ {
                            "$ref" : "#/components/schemas/Text"
                          } ]
                        }
                      },
                      "required" : [ "entityProperty", "testString" ]
                    },
                    "Text" : {
                      "type" : "string",
                      "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.samm:characteristic:2.2.0#Text",
                      "description" : "Describes a Property which contains plain text. \
This is intended exclusively for human readable strings, not for identifiers, measurement values, etc."
                    },
                    "TestEntity" : {
                      "description" : "This is a test entity",
                      "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.test:1.0.0#TestEntity",
                      "type" : "object",
                      "properties" : {
                        "innerProperty" : {
                          "description" : "An inner property of the entity.",
                          "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.test:1.0.0#innerProperty",
                          "allOf" : [ {
                            "$ref" : "#/components/schemas/Text"
                          } ]
                        }
                      },
                      "required" : [ "innerProperty" ]
                    },
                    "EntityCharacteristic" : {
                      "description" : "A characteristic with entity data type",
                      "x-samm-aspect-model-urn" : "urn:samm:org.eclipse.esmf.test:1.0.0#EntityCharacteristic",
                      "type" : "object",
                      "allOf" : [ {
                        "$ref" : "#/components/schemas/TestEntity"
                      } ]
                    }
                  }
               """
      );

      assertThat( json.get( "info" ).get( "title" ).asText() ).isEqualTo( "Test Aspect MQTT API" );
      assertThat( json.get( "info" ).get( "description" ).asText() ).isEqualTo( "This is a test description" );
      assertThat( json.get( "channels" ) ).isEqualTo( expectedChannels );
      assertThat( json.get( "components" ).get( "messages" ) ).isEqualTo( expectedComponentsMessages );
      assertThat( json.get( "components" ).get( "schemas" ) ).isEqualTo( expectedComponentsSchemas );
   }
}
