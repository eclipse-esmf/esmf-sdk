package org.eclipse.esmf.aspectmodel.generator.asyncapi;

import java.io.IOException;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class AspectModelAsyncApiGeneratorTest extends MetaModelVersions {

   private final AspectModelAsyncApiGenerator asyncApiGenerator = new AspectModelAsyncApiGenerator();

   private static final String APPLICATION_ID = "urn:samm:test:test:serve";
   private static final String CHANNEL_ADDRESS = "123/456/test/1.0.0/TestAspect";
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testAsyncApiGeneratorEmptyAspect( final KnownVersion metaModelVersion ) throws IOException {
      final Aspect aspect = loadAspect( TestAspect.ASPECT, metaModelVersion );
      final AsyncApiSchemaGenerationConfig config = AsyncApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( false )
            .applicationId( APPLICATION_ID )
            .channelAddress( CHANNEL_ADDRESS )
            .locale( Locale.ENGLISH )
            .build();
      final AsyncApiSchemaArtifact asyncSpec = asyncApiGenerator.apply( aspect, config );
      final JsonNode json = asyncSpec.getContent();

      final JsonNode expectedJson = OBJECT_MAPPER.readTree(
            """
                     {
                        "asyncapi": "3.0.0",
                        "id": "urn:samm:test:test:serve",
                        "info": {
                           "title": "Test Aspect MQTT API",
                           "version": "v1",
                           "description": "This is a test description"
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
                              "massages": {}
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

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   void testAsyncApiGeneratorAspectWithEvent( final KnownVersion metaModelVersion ) throws IOException {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_EVENT, metaModelVersion );
      final AsyncApiSchemaGenerationConfig config = AsyncApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( false )
            .applicationId( APPLICATION_ID )
            .channelAddress( CHANNEL_ADDRESS )
            .locale( Locale.ENGLISH )
            .build();
      final AsyncApiSchemaArtifact asyncSpec = asyncApiGenerator.apply( aspect, config );
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
                           "massages": {
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
                           "content-type": "application/json",
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
      assertThat( json.get( "components" ).get( "schemas" ).get( aspect.getEvents().get( 0 ).getName() ) ).isEqualTo( expectedComponentsSchemas );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   void testAsyncApiGeneratorAspectWithOperation( final KnownVersion metaModelVersion ) throws IOException {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_OPERATION, metaModelVersion );
      final AsyncApiSchemaGenerationConfig config = AsyncApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( false )
            .applicationId( APPLICATION_ID )
            .channelAddress( CHANNEL_ADDRESS )
            .locale( Locale.ENGLISH )
            .build();
      final AsyncApiSchemaArtifact asyncSpec = asyncApiGenerator.apply( aspect, config );
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
                           "massages": {
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
                     "content-type": "application/json",
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
                        "content-type": "application/json",
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

   private Aspect loadAspect( final TestAspect testAspect, final KnownVersion metaModelVersion ) {
      final VersionedModel versionedModel = TestResources.getModel( testAspect, metaModelVersion ).get();
      return AspectModelLoader.getSingleAspect( versionedModel ).get();
   }
}
