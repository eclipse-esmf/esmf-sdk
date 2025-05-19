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

package org.eclipse.esmf.aspectmodel.generator.openapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.generator.AbstractSchemaArtifact;
import org.eclipse.esmf.aspectmodel.generator.jsonschema.AspectModelJsonSchemaGenerator;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.common.collect.Streams;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.LoggerFactory;

public class AspectModelOpenApiGeneratorTest {
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
   private static final String TEST_BASE_URL = "https://test-aspect.example.com";
   private static final String TEST_RESOURCE_PATH = "my-test-aspect";
   private static final String TEST_RESOURCE_PATH_WITH_PARAMETER = "my-test-aspect/{test-Id}";
   private static final String TEST_RESOURCE_PATH_WITH_INVALID_PARAMETER = "my-test-aspect/{test-\\Id}";
   private static final ObjectNode TEST_INVALID_PARAMETER =
         JsonNodeFactory.instance.objectNode().put( "unitId", "unitId" );
   private static final List<String> UNSUPPORTED_KEYWORDS = List.of( "$schema", "additionalItems", "cost", "contains", "dependencies",
         "$id", "patternProperties", "propertyNames" );
   private final Configuration config = Configuration.defaultConfiguration().addOptions( Option.SUPPRESS_EXCEPTIONS );

   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   void testGeneration( final TestAspect testAspect ) throws IOException {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( false )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .build();
      final OpenApiSchemaArtifact result = new AspectModelOpenApiGenerator( aspect, config ).singleResult();
      final JsonNode json = result.getContent();
      assertSpecificationIsValid( json, json.toString(), aspect );
      assertThat( json.get( "info" ).get( AspectModelJsonSchemaGenerator.SAMM_EXTENSION ) ).isNotNull();
      assertThat( json.get( "info" ).get( AspectModelJsonSchemaGenerator.SAMM_EXTENSION ).asText() ).isEqualTo( aspect.urn().toString() );

      // Check that the map containing separate schema files contains the same information as the
      // all-in-one JSON document
      final Map<Path, JsonNode> jsonMap = result.getContentWithSeparateSchemasAsJson();
      assertThat( jsonMap ).containsKey( Path.of( aspect.getName() + ".oai.json" ) );
      for ( final Iterator<Map.Entry<String, JsonNode>> it = json.get( "components" ).get( "schemas" ).fields(); it.hasNext(); ) {
         final Map.Entry<String, JsonNode> schema = it.next();
         final Path keyForSchemaName = Path.of( schema.getKey() + ".json" );
         assertThat( jsonMap ).containsKey( keyForSchemaName );
      }
      final JsonNode rootDocument = jsonMap.get( Path.of( aspect.getName() + ".oai.json" ) );
      assertThat( Streams.stream( rootDocument.get( "components" ).fieldNames() ).toList() ).doesNotContain( "schemas" );

      // And the same thing for YAML format
      final Map<Path, String> yamlMap = result.getContentWithSeparateSchemasAsYaml();
      assertThat( yamlMap ).containsKey( Path.of( aspect.getName() + ".oai.yaml" ) );
      for ( final Iterator<Map.Entry<String, JsonNode>> it = json.get( "components" ).get( "schemas" ).fields(); it.hasNext(); ) {
         final Map.Entry<String, JsonNode> schema = it.next();
         final Path keyForSchemaName = Path.of( schema.getKey() + ".yaml" );
         assertThat( yamlMap ).containsKey( keyForSchemaName );
      }
   }

   private void showJson( final JsonNode node ) {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      try {
         new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue( out, node );
      } catch ( final IOException e ) {
         e.printStackTrace();
         Assertions.fail();
      }
      System.out.println( out );
   }

   @Test
   void testUseSemanticVersion() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_PROPERTY ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      assertThat( openApi.getInfo().getVersion() ).isEqualTo( "v1.0.0" );
      assertThat( json.get( "info" ).get( AspectModelJsonSchemaGenerator.SAMM_EXTENSION ) ).isNotNull();
      assertThat( json.get( "info" ).get( AspectModelJsonSchemaGenerator.SAMM_EXTENSION ).asText() ).isEqualTo( aspect.urn().toString() );

      openApi.getServers().forEach( server -> assertThat( server.getUrl() ).contains( "v1.0.0" ) );
   }

   @Test
   void testIncludeQueryApiWithSemanticVersion() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .includeQueryApi( true )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getPaths().get( "/query-api/v1.0.0/" + TEST_RESOURCE_PATH ).getPost().getServers()
            .get( 0 ).getUrl() )
            .isEqualTo( "https://test-aspect.example.com/query-api/v1.0.0" );
   }

   @Test
   void testDefaultResourcePath() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .includeQueryApi( true )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      final String apiEndpoint = "/{tenant-id}/aspect-without-see-attribute";

      assertThat( openApi.getPaths().get( apiEndpoint ).getPost() ).isNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPut() ).isNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPatch() ).isNull();
      assertThat( openApi.getPaths().keySet() ).anyMatch( path -> path.equals( apiEndpoint ) );
      assertThat( openApi.getPaths().keySet() ).anyMatch(
            path -> path.equals( "/query-api/v1.0.0" + apiEndpoint ) );
   }

   @Test
   void testInvalidResourcePath() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH_WITH_PARAMETER )
            .includeQueryApi( true )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      assertThat( json ).isEmpty();
   }

   @Test
   void testWithValidResourcePath() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .includeQueryApi( true )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      assertThat( openApi.getPaths().keySet() ).anyMatch( path -> path.equals( "/" + TEST_RESOURCE_PATH ) );
      assertThat( openApi.getPaths().keySet() ).anyMatch(
            path -> path.equals( "/query-api/v1.0.0/" + TEST_RESOURCE_PATH ) );
   }

   @Test
   void testInvalidJsonParameter() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH_WITH_PARAMETER )
            .properties( TEST_INVALID_PARAMETER )
            .includeQueryApi( true )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      assertThat( json ).isEmpty();
   }

   @Test
   void testValidParameter() throws IOException {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH_WITH_PARAMETER )
            .properties( getTestParameter() )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      assertThat( result.getMessages() ).isEmpty();

      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getPaths() ).hasSize( 1 );
      assertThat( openApi.getPaths() ).containsKey( "/my-test-aspect/{test-Id}" );
      openApi.getPaths().forEach( ( key, value ) -> {
         final List<String> params = value.getGet().getParameters().stream().map( Parameter::getName )
               .collect( Collectors.toList() );
         assertThat( params ).doesNotContain( "tenant-id" );
         assertThat( params ).contains( "test-Id" );
      } );
   }

   @Test
   void testValidTemplate() throws IOException {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .template( getTemplateParameter() )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      assertThat( result.getMessages() ).isEmpty();

      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getResponses().get( "400" ).get$ref() )
            .isEqualTo( "./core-api.yaml#/components/responses/ClientError" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getResponses().get( "401" ).get$ref() )
            .isEqualTo( "./core-api.yaml#/components/responses/Unauthorized" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getResponses().get( "403" ).get$ref() )
            .isEqualTo( "./core-api.yaml#/components/responses/Forbidden" );
   }

   @Test
   void testInValidParameterName() throws IOException {
      final ListAppender<ILoggingEvent> logAppender = new ListAppender<>();
      final Logger logger = (Logger) LoggerFactory.getLogger( AspectModelOpenApiGenerator.class );
      logger.addAppender( logAppender );
      logAppender.start();
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH_WITH_INVALID_PARAMETER )
            .properties( getTestParameter() )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      logAppender.stop();
      assertThat( result.getMessages().size() ).isNotZero();
      final List<String> logResults = logAppender.list.stream().map( ILoggingEvent::getFormattedMessage )
            .collect( Collectors.toList() );
      assertThat( logResults ).contains(
            "The parameter name test-\\Id is not in the correct form. A valid form is described as: ^[a-zA-Z][a-zA-Z0-9-_]*" );
      assertThat( logResults ).contains( "There was an exception during the read of the root or the validation." );
   }

   @Test
   void testYamlGenerator() throws IOException {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE ).aspect();
      final YAMLFactory yamlFactory = YAMLFactory.builder()
            .stringQuotingChecker( new AbstractSchemaArtifact.OpenApiStringQuotingChecker() ).build();
      final YAMLMapper yamlMapper = new YAMLMapper( yamlFactory ).enable( YAMLGenerator.Feature.MINIMIZE_QUOTES );
      final OpenApiSchemaGenerationConfig yamlConfig = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH_WITH_PARAMETER )
            .properties( getTestParameter() )
            .build();
      final String yaml = new AspectModelOpenApiGenerator( aspect, yamlConfig ).generateYaml();
      final OpenApiSchemaGenerationConfig jsonConfig = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH_WITH_PARAMETER )
            .properties( getTestParameter() )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, jsonConfig ).getContent();
      assertThat( yaml ).isEqualTo( yamlMapper.writeValueAsString( json ) );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      assertThat( result.getMessages().size() ).isZero();
   }

   @Test
   void testHasQuerySchema() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .includeQueryApi( true )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      assertThat( openApi.getComponents().getSchemas() ).containsKey( "Filter" );
      assertThat( openApi.getComponents().getRequestBodies() ).containsKey( "Filter" );
   }

   @Test
   void testHasNoQuerySchema() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      assertThat( openApi.getComponents().getSchemas().keySet() ).doesNotContain( "Filter" );
      assertThat( openApi.getComponents().getRequestBodies().keySet() ).doesNotContain( "Filter" );
   }

   @Test
   void testHasPagingWithChosenPaging() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_COLLECTION ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .pagingOption( PagingOption.OFFSET_BASED_PAGING )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getPaths() ).containsKey( "/{tenant-id}/aspect-with-collection" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 0 ).getName() ).isEqualTo(
            "tenant-id" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 1 ).getName() ).isEqualTo(
            "start" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 2 ).getName() ).isEqualTo(
            "count" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 3 ).getName() ).isEqualTo(
            "totalItemCount" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getResponses().get( "200" ).get$ref() )
            .isEqualTo( "#/components/responses/AspectWithCollection" );
      assertThat(
            openApi.getComponents().getResponses().get( "AspectWithCollection" ).getContent().get( "application/json" ).getSchema()
                  .get$ref() )
            .isEqualTo( "#/components/schemas/PagingSchema" );
   }

   @Test
   void testHasPagingWithoutChosenPaging() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_TIME_SERIES ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getPaths() ).containsKey( "/{tenant-id}/aspect-with-time-series" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 0 ).getName() ).isEqualTo(
            "tenant-id" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 1 ).getName() ).isEqualTo(
            "since" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 2 ).getName() ).isEqualTo(
            "until" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 3 ).getName() ).isEqualTo(
            "limit" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getResponses().get( "200" ).get$ref() )
            .isEqualTo( "#/components/responses/AspectWithTimeSeries" );
      assertThat( openApi.getComponents().getResponses().get( "AspectWithTimeSeries" ).getContent()
            .get( "application/json" ).getSchema().get$ref() ).isEqualTo( "#/components/schemas/PagingSchema" );
   }

   @Test
   void testHasPagingWitChosenCursorBasedPaging() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_COLLECTION ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .pagingOption( PagingOption.CURSOR_BASED_PAGING )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getPaths() ).containsKey( "/{tenant-id}/aspect-with-collection" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 0 ).getName() ).isEqualTo(
            "tenant-id" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 1 ).getName() ).isEqualTo(
            "before" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 2 ).getName() ).isEqualTo(
            "after" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getResponses().get( "200" ).get$ref() )
            .isEqualTo( "#/components/responses/AspectWithCollection" );
      assertThat(
            openApi.getComponents().getResponses().get( "AspectWithCollection" ).getContent().get( "application/json" ).getSchema()
                  .get$ref() )
            .isEqualTo( "#/components/schemas/PagingSchema" );
      assertThat(
            openApi.getComponents().getResponses().get( "AspectWithCollection" ).getContent().get( "application/json" ).getSchema()
                  .get$ref() )
            .isEqualTo( "#/components/schemas/PagingSchema" );
      assertThat( openApi.getComponents().getSchemas().get( "PagingSchema" ).getProperties() ).containsKey( "items" );
      assertThat( openApi.getComponents().getSchemas().get( "PagingSchema" ).getProperties() ).containsKey( "cursor" );
      assertThat( openApi.getComponents().getSchemas().get( "PagingSchema" ).getProperties() ).containsKey( "_links" );
   }

   @Test
   void testHasPagingWithWithDefaultChosenPaging() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_COLLECTION ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getPaths() ).containsKey( "/{tenant-id}/aspect-with-collection" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 0 ).getName() ).isEqualTo(
            "tenant-id" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 1 ).getName() ).isEqualTo(
            "start" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 2 ).getName() ).isEqualTo(
            "count" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 3 ).getName() ).isEqualTo(
            "totalItemCount" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getResponses()
            .get( "200" ).get$ref() ).isEqualTo( "#/components/responses/AspectWithCollection" );
      assertThat( openApi.getComponents().getResponses().get( "AspectWithCollection" ).getContent()
            .get( "application/json" ).getSchema().get$ref() ).isEqualTo( "#/components/schemas/PagingSchema" );
   }

   @Test
   void testHasNoPagination() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_COLLECTION ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .pagingOption( PagingOption.NO_PAGING )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getPaths() ).containsKey( "/{tenant-id}/aspect-with-collection" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getResponses()
            .get( "200" ).get$ref() ).isEqualTo( "#/components/responses/AspectWithCollection" );
      assertThat( openApi.getComponents().getResponses().get( "AspectWithCollection" ).getContent()
            .get( "application/json" ).getSchema().get$ref() ).isEqualTo( "#/components/schemas/AspectWithCollection" );
   }

   @Test
   void testAspectWithOperation() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_OPERATION ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "AspectWithOperation" );
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "Operation" );
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "OperationResponse" );
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "testOperation" );
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "testOperationResponse" );
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "testOperationTwo" );
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "testOperationTwoResponse" );
      assertThat( openApi.getComponents().getSchemas().get( "Operation" ).getOneOf() ).hasSize( 2 );
      assertThat( openApi.getComponents().getSchemas().get( "OperationResponse" ).getOneOf() ).hasSize( 2 );
   }

   @Test
   void testAspectWithOperationWithSeeAttribute() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_OPERATION_WITH_SEE_ATTRIBUTE ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat(
            ( (Schema) openApi.getComponents().getSchemas().get( "testOperation" ).getAllOf()
                  .get( 1 ) ).getProperties() ).doesNotContainKey(
            "params" );
      assertThat( ( (Schema) openApi.getComponents().getSchemas().get( "testOperationTwo" ).getAllOf()
            .get( 1 ) ).getProperties() ).doesNotContainKey( "params" );
   }

   @Test
   void testAspectWithAllCrud() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .includeQueryApi( true )
            .includeCrud( true )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      final String apiEndpoint = "/{tenant-id}/aspect-without-see-attribute";

      assertThat( openApi.getPaths().get( apiEndpoint ).getGet() ).isNotNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPost() ).isNotNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPut() ).isNotNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPatch() ).isNotNull();
      assertThat( openApi.getPaths().keySet() ).anyMatch(
            path -> path.equals( "/query-api/v1.0.0" + apiEndpoint ) );
   }

   @Test
   void testAspectWithPostOperation() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .includeQueryApi( true )
            .includePost( true )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      final String apiEndpoint = "/{tenant-id}/aspect-without-see-attribute";

      assertThat( openApi.getPaths().get( apiEndpoint ).getGet() ).isNotNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPost() ).isNotNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPut() ).isNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPatch() ).isNull();
      assertThat( openApi.getPaths().keySet() ).anyMatch(
            path -> path.equals( "/query-api/v1.0.0" + apiEndpoint ) );
   }

   @Test
   void testAspectWithPutOperation() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .includeQueryApi( true )
            .includePut( true )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      assertThat( openApi.getComponents().getRequestBodies().get( "AspectWithoutSeeAttribute" ).getRequired() ).isTrue();

      final String apiEndpoint = "/{tenant-id}/aspect-without-see-attribute";

      assertThat( openApi.getPaths().get( apiEndpoint ).getGet() ).isNotNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPost() ).isNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPut() ).isNotNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPatch() ).isNull();
      assertThat( openApi.getPaths().keySet() ).anyMatch(
            path -> path.equals( "/query-api/v1.0.0" + apiEndpoint ) );
   }

   @Test
   void testAspectWithPatchOperation() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .includeQueryApi( true )
            .includePatch( true )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      final String apiEndpoint = "/{tenant-id}/aspect-without-see-attribute";

      assertThat( openApi.getPaths().get( apiEndpoint ).getGet() ).isNotNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPost() ).isNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPut() ).isNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPatch() ).isNotNull();
      assertThat( openApi.getPaths().keySet() ).anyMatch(
            path -> path.equals( "/query-api/v1.0.0" + apiEndpoint ) );
   }

   @Test
   void testAspectWithPatchAndPostOperation() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .includeQueryApi( true )
            .includePatch( true )
            .includePost( true )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      final String apiEndpoint = "/{tenant-id}/aspect-without-see-attribute";

      assertThat( openApi.getPaths().get( apiEndpoint ).getGet() ).isNotNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPost() ).isNotNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPut() ).isNull();
      assertThat( openApi.getPaths().get( apiEndpoint ).getPatch() ).isNotNull();
      assertThat( openApi.getPaths().keySet() ).anyMatch(
            path -> path.equals( "/query-api/v1.0.0" + apiEndpoint ) );
   }

   @Test
   void testAspectWithCommentForSeeAttributes() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_COLLECTION ).aspect();
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .locale( Locale.ENGLISH )
            .generateCommentForSeeAttributes( true )
            .build();
      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      // $comment keyword is an OpenAPI 3.1 feature
      assertThat( openApi.getSpecVersion() ).isEqualTo( SpecVersion.V31 );
      assertThat( openApi.getComponents().getSchemas().get( "AspectWithCollection" ).get$comment() ).isEqualTo(
            "See: http://example.com/" );
      assertThat( ( (Schema) openApi.getComponents().getSchemas().get( "AspectWithCollection" ).getProperties()
            .get( "testProperty" ) ).get$comment() )
            .isEqualTo( "See: http://example.com/, http://example.com/me" );
      assertThat( openApi.getComponents().getSchemas().get( "TestCollection" ).get$comment() )
            .isEqualTo( "See: http://example.com/" );
   }

   @Test
   void testPagingSchemaStructure() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_COLLECTION ).aspect();

      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .locale( Locale.ENGLISH )
            .build();

      final JsonNode json = new AspectModelOpenApiGenerator( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      Schema pagingSchema = openApi.getComponents().getSchemas().get( "PagingSchema" );
      assertThat( pagingSchema ).isNotNull();

      Schema itemsProperty = (Schema) pagingSchema.getProperties().get( "items" );
      assertThat( itemsProperty.get$ref() )
            .isEqualTo( "#/components/schemas/" + aspect.getName() );

      assertThat( itemsProperty.getType() ).isNull();
      assertThat( itemsProperty.getItems() ).isNull();

      assertThat( pagingSchema.getProperties() ).containsKeys(
            "totalItems",
            "totalPages",
            "pageSize",
            "currentPage"
      );

      assertThat( openApi.getComponents().getSchemas().get( aspect.getName() ) )
            .isNotNull();
   }

   private void assertSpecificationIsValid( final JsonNode jsonNode, final String json, final Aspect aspect ) throws IOException {
      validateUnsupportedKeywords( jsonNode );

      final SwaggerParseResult result = new OpenAPIParser().readContents( json, null, null );
      assertThat( result.getMessages() ).isEmpty();

      final OpenAPI openApi = result.getOpenAPI();
      validateOpenApiSpec( jsonNode, openApi, aspect );

      final DocumentContext context = JsonPath.parse( json );
      assertThat( context.<Object> read( "$['components']['schemas']['" + aspect.getName() + "']" ) ).isNotNull();
      assertThat( context.<String> read(
            "$['components']['schemas']['" + aspect.getName() + "']['" + AspectModelJsonSchemaGenerator.SAMM_EXTENSION + "']" ) ).isEqualTo(
            aspect.urn().toString() );

      for ( final Property property : aspect.getProperties() ) {
         assertThat( context.<String> read( "$['components']['schemas']"
               + "['" + aspect.getName() + "']['properties']['" + property.getPayloadName() + "']['"
               + AspectModelJsonSchemaGenerator.SAMM_EXTENSION
               + "']" ) ).isEqualTo( property.urn().toString() );
      }

      // $comment keywords should only be generated on demand, not by default
      assertThat( context.<Object> read( "$..$comment" ) ).asInstanceOf( InstanceOfAssertFactories.LIST ).isEmpty();
      if ( !aspect.getOperations().isEmpty() ) {
         validateOperation( openApi );
      } else {
         assertThat( openApi.getComponents().getSchemas().keySet() ).doesNotContain( "JsonRpc" );
      }
      validateYaml( aspect );
   }

   private void validateOpenApiSpec( final JsonNode node, final OpenAPI openApi, final Aspect aspect ) {
      assertThat( openApi.getSpecVersion() ).isEqualTo( SpecVersion.V30 );
      assertThat( openApi.getInfo().getTitle() ).isEqualTo( aspect.getPreferredName( Locale.ENGLISH ) );

      final String expectedApiVersion = getExpectedApiVersion( aspect );
      assertThat( openApi.getInfo().getVersion() ).isEqualTo( expectedApiVersion );

      assertThat( node.get( "info" ).get( AspectModelJsonSchemaGenerator.SAMM_EXTENSION ) ).isNotNull();
      assertThat( node.get( "info" ).get( AspectModelJsonSchemaGenerator.SAMM_EXTENSION ).asText() ).isEqualTo( aspect.urn().toString() );

      assertThat( openApi.getServers() ).hasSize( 1 );
      assertThat( openApi.getServers().get( 0 ).getUrl() ).isEqualTo( TEST_BASE_URL + "/api/" + expectedApiVersion );

      assertThat( openApi.getPaths().keySet() ).noneMatch( path -> path.contains( "/query-api" ) );
      openApi.getPaths().entrySet().stream().filter( item -> !item.getKey().contains( "/operations" ) )
            .forEach( path -> {
               assertThat( path.getKey() ).startsWith( "/" + TEST_RESOURCE_PATH );
               path.getValue().readOperations()
                     .forEach( operation -> validateSuccessfulResponse( aspect.getName(), operation ) );
            } );

      assertThat( openApi.getComponents().getSchemas() ).containsKey( aspect.getName() );
      assertThat( openApi.getComponents().getResponses() ).containsKey( aspect.getName() );
      assertThat( openApi.getComponents().getRequestBodies() ).containsKey( aspect.getName() );
      assertThat( openApi.getComponents().getSchemas().get( aspect.getName() ).getExtensions()
            .get( AspectModelJsonSchemaGenerator.SAMM_EXTENSION ) ).isNotNull();
      assertThat(
            openApi.getComponents().getSchemas().get( aspect.getName() ).getExtensions()
                  .get( AspectModelJsonSchemaGenerator.SAMM_EXTENSION )
                  .toString() ).contains( aspect.urn().toString() );

      validateReferences( node );
   }

   private void validateReferences( final JsonNode rootNode ) {
      final List<JsonNode> nodes = rootNode.findValues( "$ref" ).stream().distinct().toList();
      nodes.forEach( node -> {
         final String[] text = node.asText().split( "/" );
         final DocumentContext context = JsonPath.using( config ).parse( rootNode.toString() );
         final LinkedHashMap<String, Object> read = context
               .<LinkedHashMap<String, Object>> read( String.format( "$['%s']['%s']['%s']", text[1], text[2], text[3] ) );
         assertThat( read ).isNotNull();
      } );
   }

   private String getExpectedApiVersion( final Aspect aspect ) {
      final String aspectVersion = aspect.urn().getVersion();
      final int endIndexOfMajorVersion = aspectVersion.indexOf( "." );
      final String majorAspectVersion = aspectVersion.substring( 0, endIndexOfMajorVersion );
      return String.format( "v%s", majorAspectVersion );
   }

   private void validateSuccessfulResponse( final String name, final Operation operation ) {
      final ApiResponse apiResponse = operation.getResponses().get( "200" );
      assertThat( apiResponse ).isNotNull();
      assertThat( apiResponse.get$ref() )
            .matches( "-?(#/components/responses/" + name + ")" );
   }

   private void validateUnsupportedKeywords( final JsonNode jsonNode ) throws IOException {
      final JsonParser jsonParser = jsonNode.traverse();
      while ( !jsonParser.isClosed() ) {
         if ( jsonParser.nextToken() == JsonToken.FIELD_NAME ) {
            assertThat( jsonParser.currentName() ).isNotIn( UNSUPPORTED_KEYWORDS );
         }
      }
   }

   private void validateOperation( final OpenAPI openApi ) {
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "JsonRpc" );
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "Operation" );
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "OperationResponse" );
      assertThat( openApi.getComponents().getResponses() ).containsKey( "OperationResponse" );
      assertThat( openApi.getComponents().getRequestBodies() ).containsKey( "Operation" );
   }

   private void validateYaml( final Aspect aspect ) {
      assertThatCode( () -> {
         final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
               .baseUrl( TEST_BASE_URL )
               .resourcePath( TEST_RESOURCE_PATH )
               .build();
         new AspectModelOpenApiGenerator( aspect, config ).generateYaml();
      } ).doesNotThrowAnyException();
   }

   private ObjectNode getTestParameter() throws IOException {
      final InputStream inputStream = getClass().getResourceAsStream( "/openapi/parameter.json" );
      final String inputString = IOUtils.toString( inputStream, StandardCharsets.UTF_8 );
      return (ObjectNode) OBJECT_MAPPER.readTree( inputString );
   }

   private ObjectNode getTemplateParameter() throws IOException {
      final InputStream inputStream = getClass().getResourceAsStream( "/openapi/open-api-error-template.yaml" );
      final String inputString = IOUtils.toString( inputStream, StandardCharsets.UTF_8 );
      return (ObjectNode) new YAMLMapper().readTree( inputString );
   }
}
