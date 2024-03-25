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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.generator.jsonschema.AspectModelJsonSchemaVisitor;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;

public class AspectModelOpenApiGeneratorTest extends MetaModelVersions {
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
   private static final String TEST_BASE_URL = "https://test-aspect.example.com";
   private static final Optional<String> TEST_RESOURCE_PATH = Optional.of( "my-test-aspect" );
   private static final Optional<String> TEST_RESOURCE_PATH_WITH_PARAMETER = Optional.of( "my-test-aspect/{test-Id}" );
   private static final Optional<String> TEST_RESOURCE_PATH_WITH_INVALID_PARAMETER = Optional.of( "my-test-aspect/{test-\\Id}" );
   private static final Optional<JsonNode> TEST_INVALID_PARAMETER = Optional.of(
         JsonNodeFactory.instance.objectNode().put( "unitId", "unitId" ) );
   private static final List<String> UNSUPPORTED_KEYWORDS = List.of( "$schema", "additionalItems", "cost", "contains", "dependencies",
         "$id", "patternProperties", "propertyNames" );
   private final AspectModelOpenApiGenerator apiJsonGenerator = new AspectModelOpenApiGenerator();
   private final Configuration config = Configuration.defaultConfiguration().addOptions( Option.SUPPRESS_EXCEPTIONS );

   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   public void testGeneration( final TestAspect testAspect ) throws IOException {
      final Aspect aspect = loadAspect( testAspect, KnownVersion.getLatest() );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( false )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      showJson( json );
      assertSpecificationIsValid( json, json.toString(), aspect );
      assertThat( json.get( "info" ).get( AspectModelJsonSchemaVisitor.SAMM_EXTENSION ) ).isNotNull();
      assertThat( json.get( "info" ).get( AspectModelJsonSchemaVisitor.SAMM_EXTENSION ).asText() ).isEqualTo(
            aspect.getAspectModelUrn().map( Object::toString ).orElse( "" ) );
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

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testUseSemanticVersion( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_PROPERTY, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      assertThat( openApi.getInfo().getVersion() ).isEqualTo( "v1.0.0" );
      assertThat( json.get( "info" ).get( AspectModelJsonSchemaVisitor.SAMM_EXTENSION ) ).isNotNull();
      assertThat( json.get( "info" ).get( AspectModelJsonSchemaVisitor.SAMM_EXTENSION ).asText() ).isEqualTo(
            aspect.getAspectModelUrn().map( Object::toString ).orElse( "" ) );

      openApi.getServers().forEach( server -> assertThat( server.getUrl() ).contains( "v1.0.0" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testIncludeQueryApiWithSemanticVersion( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .includeQueryApi( true )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getPaths().get( "/" + TEST_RESOURCE_PATH.get() ).getPost().getServers().get( 0 ).getUrl() )
            .isEqualTo( "https://test-aspect.example.com/query-api/v1.0.0" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testDefaultResourcePath( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .includeQueryApi( true )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      assertThat( openApi.getPaths().keySet() ).allMatch( path -> path.equals( "/{tenant-id}/aspect-without-see-attribute" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testInvalidResourcePath( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH_WITH_PARAMETER )
            .includeQueryApi( true )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      assertThat( json ).isEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testWithValidResourcePath( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .includeQueryApi( true )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      assertThat( openApi.getPaths().keySet() ).allMatch( path -> path.equals( "/" + TEST_RESOURCE_PATH.get() ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testInvalidJsonParameter( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH_WITH_PARAMETER )
            .jsonProperties( TEST_INVALID_PARAMETER )
            .includeQueryApi( true )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      assertThat( json ).isEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testValidParameter( final KnownVersion metaModelVersion ) throws IOException {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH_WITH_PARAMETER )
            .jsonProperties( Optional.of( getTestParameter() ) )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      assertThat( result.getMessages().size() ).isZero();

      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getPaths() ).hasSize( 1 );
      assertThat( openApi.getPaths().keySet() ).contains( "/my-test-aspect/{test-Id}" );
      openApi.getPaths().forEach( ( key, value ) -> {
         final List<String> params = value.getGet().getParameters().stream().map( Parameter::getName )
               .collect( Collectors.toList() );
         assertThat( params ).doesNotContain( "tenant-id" );
         assertThat( params ).contains( "test-Id" );
      } );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testInValidParameterName( final KnownVersion metaModelVersion ) throws IOException {
      final ListAppender<ILoggingEvent> logAppender = new ListAppender<>();
      final Logger logger = (Logger) LoggerFactory.getLogger( AspectModelOpenApiGenerator.class );
      logger.addAppender( logAppender );
      logAppender.start();
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH_WITH_INVALID_PARAMETER )
            .jsonProperties( Optional.of( getTestParameter() ) )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      logAppender.stop();
      assertThat( result.getMessages().size() ).isNotZero();
      final List<String> logResults = logAppender.list.stream().map( ILoggingEvent::getFormattedMessage )
            .collect( Collectors.toList() );
      assertThat( logResults ).contains(
            "The parameter name test-\\Id is not in the correct form. A valid form is described as: ^[a-zA-Z][a-zA-Z0-9-_]*" );
      assertThat( logResults ).contains( "There was an exception during the read of the root or the validation." );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testYamlGenerator( final KnownVersion metaModelVersion ) throws IOException {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final YAMLMapper yamlMapper = new YAMLMapper().enable( YAMLGenerator.Feature.MINIMIZE_QUOTES );
      final String yamlProperties = yamlMapper.writeValueAsString( getTestParameter() );
      final OpenApiSchemaGenerationConfig yamlConfig = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH_WITH_PARAMETER )
            .yamlProperties( Optional.of( yamlProperties ) )
            .build();
      final String yaml = apiJsonGenerator.apply( aspect, yamlConfig ).getContentAsYaml();
      final OpenApiSchemaGenerationConfig jsonConfig = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH_WITH_PARAMETER )
            .jsonProperties( Optional.of( getTestParameter() ) )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, jsonConfig ).getContent();
      assertThat( yaml ).isEqualTo( yamlMapper.writeValueAsString( json ) );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      assertThat( result.getMessages().size() ).isZero();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHasQuerySchema( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .includeQueryApi( true )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      assertThat( openApi.getComponents().getSchemas().keySet() ).contains( "Filter" );
      assertThat( openApi.getComponents().getRequestBodies().keySet() ).contains( "Filter" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHasNoQuerySchema( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();

      assertThat( openApi.getComponents().getSchemas().keySet() ).doesNotContain( "Filter" );
      assertThat( openApi.getComponents().getRequestBodies().keySet() ).doesNotContain( "Filter" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHasPagingWithChosenPaging( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .pagingOption( Optional.of( PagingOption.OFFSET_BASED_PAGING ) )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getPaths().keySet() ).contains( "/{tenant-id}/aspect-with-collection" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 0 ).getName() ).isEqualTo(
            "tenant-id" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 1 ).getName() ).isEqualTo( "start" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 2 ).getName() ).isEqualTo( "count" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 3 ).getName() ).isEqualTo(
            "totalItemCount" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getResponses().get( "200" ).get$ref() )
            .isEqualTo( "#/components/responses/AspectWithCollection" );
      assertThat( openApi.getComponents().getResponses().get( "AspectWithCollection" ).getContent().get( "application/json" ).getSchema()
            .get$ref() )
            .isEqualTo( "#/components/schemas/PagingSchema" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHasPagingWithoutChosenPaging( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_TIME_SERIES, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getPaths().keySet() ).contains( "/{tenant-id}/aspect-with-time-series" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 0 ).getName() ).isEqualTo(
            "tenant-id" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 1 ).getName() ).isEqualTo( "since" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 2 ).getName() ).isEqualTo( "until" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 3 ).getName() ).isEqualTo( "limit" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getResponses().get( "200" ).get$ref() )
            .isEqualTo( "#/components/responses/AspectWithTimeSeries" );
      assertThat( openApi.getComponents().getResponses().get( "AspectWithTimeSeries" ).getContent()
            .get( "application/json" ).getSchema().get$ref() ).isEqualTo( "#/components/schemas/PagingSchema" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHasPagingWitChosenCursorBasedPaging( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .pagingOption( Optional.of( PagingOption.CURSOR_BASED_PAGING ) )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getPaths().keySet() ).contains( "/{tenant-id}/aspect-with-collection" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 0 ).getName() ).isEqualTo(
            "tenant-id" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 1 ).getName() ).isEqualTo(
            "previous" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 2 ).getName() ).isEqualTo( "next" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 3 ).getName() ).isEqualTo(
            "before" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 4 ).getName() ).isEqualTo( "after" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 5 ).getName() ).isEqualTo( "count" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 6 ).getName() ).isEqualTo(
            "totalItemCount" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getResponses().get( "200" ).get$ref() )
            .isEqualTo( "#/components/responses/AspectWithCollection" );
      assertThat( openApi.getComponents().getResponses().get( "AspectWithCollection" ).getContent().get( "application/json" ).getSchema()
            .get$ref() )
            .isEqualTo( "#/components/schemas/PagingSchema" );
      assertThat( openApi.getComponents().getResponses().get( "AspectWithCollection" ).getContent().get( "application/json" ).getSchema()
            .get$ref() )
            .isEqualTo( "#/components/schemas/PagingSchema" );
      assertThat( openApi.getComponents().getSchemas().get( "PagingSchema" ).getProperties() ).containsKey( "items" );
      assertThat( openApi.getComponents().getSchemas().get( "PagingSchema" ).getProperties() ).containsKey( "totalItems" );
      assertThat( openApi.getComponents().getSchemas().get( "PagingSchema" ).getProperties() ).containsKey( "cursor" );
      assertThat( openApi.getComponents().getSchemas().get( "PagingSchema" ).getProperties() ).containsKey( "_links" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHasPagingWithWithDefaultChosenPaging( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getPaths().keySet() ).contains( "/{tenant-id}/aspect-with-collection" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 0 ).getName() ).isEqualTo(
            "tenant-id" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 1 ).getName() ).isEqualTo( "start" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 2 ).getName() ).isEqualTo( "count" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 3 ).getName() ).isEqualTo(
            "totalItemCount" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getResponses()
            .get( "200" ).get$ref() ).isEqualTo( "#/components/responses/AspectWithCollection" );
      assertThat( openApi.getComponents().getResponses().get( "AspectWithCollection" ).getContent()
            .get( "application/json" ).getSchema().get$ref() ).isEqualTo( "#/components/schemas/PagingSchema" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHasNoPagination( final KnownVersion metaModelVersion ) throws ProcessingException {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .pagingOption( Optional.of( PagingOption.NO_PAGING ) )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getPaths().keySet() ).contains( "/{tenant-id}/aspect-with-collection" );
      assertThat( openApi.getPaths().values().stream().findFirst().get().getGet().getResponses()
            .get( "200" ).get$ref() ).isEqualTo( "#/components/responses/AspectWithCollection" );
      assertThat( openApi.getComponents().getResponses().get( "AspectWithCollection" ).getContent()
            .get( "application/json" ).getSchema().get$ref() ).isEqualTo( "#/components/schemas/AspectWithCollection" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithOperation( final KnownVersion metaModelVersion ) throws ProcessingException {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_OPERATION, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "AspectWithOperation" );
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "Operation" );
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "OperationResponse" );
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "testOperation" );
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "testOperationResponse" );
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "testOperationTwo" );
      assertThat( openApi.getComponents().getSchemas() ).containsKey( "testOperationTwoResponse" );
      assertThat( openApi.getComponents().getSchemas().get( "Operation" ).getOneOf().size() ).isEqualTo( 2 );
      assertThat( openApi.getComponents().getSchemas().get( "OperationResponse" ).getOneOf().size() ).isEqualTo( 2 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithOperationWithSeeAttribute( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_OPERATION_WITH_SEE_ATTRIBUTE, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openApi = result.getOpenAPI();
      assertThat(
            ( (Schema) openApi.getComponents().getSchemas().get( "testOperation" ).getAllOf()
                  .get( 1 ) ).getProperties() ).doesNotContainKey(
            "params" );
      assertThat( ( (Schema) openApi.getComponents().getSchemas().get( "testOperationTwo" ).getAllOf()
            .get( 1 ) ).getProperties() ).doesNotContainKey( "params" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithCommentForSeeAttributes( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION, metaModelVersion );
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( true )
            .baseUrl( TEST_BASE_URL )
            .resourcePath( TEST_RESOURCE_PATH )
            .locale( Locale.ENGLISH )
            .generateCommentForSeeAttributes( true )
            .build();
      final JsonNode json = apiJsonGenerator.apply( aspect, config ).getContent();
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

   private void assertSpecificationIsValid( final JsonNode jsonNode, final String json, final Aspect aspect ) throws IOException {
      validateUnsupportedKeywords( jsonNode );

      final SwaggerParseResult result = new OpenAPIParser().readContents( json, null, null );
      assertThat( result.getMessages() ).isEmpty();

      final OpenAPI openApi = result.getOpenAPI();
      validateOpenApiSpec( jsonNode, openApi, aspect );

      System.out.println( prettyPrintJson( json ) );

      final DocumentContext context = JsonPath.parse( json );
      assertThat( context.<Object> read( "$['components']['schemas']['" + aspect.getName() + "']" ) ).isNotNull();
      assertThat( context.<String> read(
            "$['components']['schemas']['" + aspect.getName() + "']['" + AspectModelJsonSchemaVisitor.SAMM_EXTENSION + "']" ) ).isEqualTo(
            aspect.getAspectModelUrn().get().toString() );

      for ( final Property property : aspect.getProperties() ) {
         assertThat( context.<String> read( "$['components']['schemas']"
               + "['" + aspect.getName() + "']['properties']['" + property.getPayloadName() + "']['"
               + AspectModelJsonSchemaVisitor.SAMM_EXTENSION
               + "']" ) ).isEqualTo( property.getAspectModelUrn().get().toString() );
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

   public static String prettyPrintJson( final String json ) throws JsonProcessingException {
      final ObjectMapper m = new ObjectMapper();
      return m.writerWithDefaultPrettyPrinter().writeValueAsString( m.readTree( json ) );
   }

   private void validateOpenApiSpec( final JsonNode node, final OpenAPI openApi, final Aspect aspect ) {
      assertThat( openApi.getSpecVersion() ).isEqualTo( SpecVersion.V30 );
      assertThat( openApi.getInfo().getTitle() ).isEqualTo( aspect.getPreferredName( Locale.ENGLISH ) );

      final String expectedApiVersion = getExpectedApiVersion( aspect );
      assertThat( openApi.getInfo().getVersion() ).isEqualTo( expectedApiVersion );

      assertThat( node.get( "info" ).get( AspectModelJsonSchemaVisitor.SAMM_EXTENSION ) ).isNotNull();
      assertThat( node.get( "info" ).get( AspectModelJsonSchemaVisitor.SAMM_EXTENSION ).asText() ).isEqualTo(
            aspect.getAspectModelUrn().map( Object::toString ).orElse( "" ) );

      assertThat( openApi.getServers() ).hasSize( 1 );
      assertThat( openApi.getServers().get( 0 ).getUrl() ).isEqualTo( TEST_BASE_URL + "/api/" + expectedApiVersion );

      assertThat( openApi.getPaths().keySet() ).noneMatch( path -> path.contains( "/query-api" ) );
      openApi.getPaths().entrySet().stream().filter( item -> !item.getKey().contains( "/operations" ) )
            .forEach( path -> {
               assertThat( path.getKey() ).startsWith( "/" + TEST_RESOURCE_PATH.get() );
               path.getValue().readOperations()
                     .forEach( operation -> validateSuccessfulResponse( aspect.getName(), operation ) );
            } );

      assertThat( openApi.getComponents().getSchemas().keySet() ).contains( aspect.getName() );
      assertThat( openApi.getComponents().getResponses().keySet() ).contains( aspect.getName() );
      assertThat( openApi.getComponents().getRequestBodies().keySet() ).contains( aspect.getName() );
      assertThat( openApi.getComponents().getSchemas().get( aspect.getName() ).getExtensions()
            .get( AspectModelJsonSchemaVisitor.SAMM_EXTENSION ) ).isNotNull();
      assertThat(
            openApi.getComponents().getSchemas().get( aspect.getName() ).getExtensions().get( AspectModelJsonSchemaVisitor.SAMM_EXTENSION )
                  .toString() ).contains(
            aspect.getAspectModelUrn().map( Object::toString ).orElse( "" ) );

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
      final String aspectVersion = aspect.getAspectModelUrn().get().getVersion();
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
      assertThat( openApi.getComponents().getSchemas().keySet() ).contains( "JsonRpc" );
      assertThat( openApi.getComponents().getSchemas().keySet() ).contains( "Operation" );
      assertThat( openApi.getComponents().getSchemas().keySet() ).contains( "OperationResponse" );
      assertThat( openApi.getComponents().getResponses().keySet() ).contains( "OperationResponse" );
      assertThat( openApi.getComponents().getRequestBodies().keySet() ).contains( "Operation" );
   }

   private void validateYaml( final Aspect aspect ) {
      final AspectModelOpenApiGenerator apiJsonGenerator = new AspectModelOpenApiGenerator();
      assertThatCode( () -> {
         final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
               .baseUrl( TEST_BASE_URL )
               .resourcePath( TEST_RESOURCE_PATH )
               .build();
         apiJsonGenerator.apply( aspect, config ).getContentAsYaml();
      } ).doesNotThrowAnyException();
   }

   private Aspect loadAspect( final TestAspect testAspect, final KnownVersion metaModelVersion ) {
      final VersionedModel versionedModel = TestResources.getModel( testAspect, metaModelVersion ).get();
      return AspectModelLoader.getSingleAspect( versionedModel ).get();
   }

   private JsonNode getTestParameter() throws IOException {
      final InputStream inputStream = getClass().getResourceAsStream( "/openapi/parameter.json" );
      final String inputString = IOUtils.toString( inputStream, StandardCharsets.UTF_8 );
      return OBJECT_MAPPER.readTree( inputString );
   }
}
