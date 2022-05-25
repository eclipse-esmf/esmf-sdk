/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.generator.openapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestResources;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

public class OpenApiTest extends MetaModelVersions {
   private static final ObjectMapper objectMapper = new ObjectMapper();
   private final static String testBaseUrl = "https://test-aspect.example.com";
   private final static Optional<String> testResourcePath = Optional.of( "my-test-aspect" );
   private final static Optional<String> testResourcePathWithParameter = Optional.of( "my-test-aspect/{test-Id}" );
   private final static Optional<String> testResourcePathWithInvalidParameter = Optional.of( "my-test-aspect/{test-\\Id}" );
   private final static Optional<JsonNode> testInvalidParameter =
         Optional.of( JsonNodeFactory.instance.objectNode().put( "unitId", "unitId" ) );
   private final AspectModelOpenApiGenerator apiJsonGenerator = new AspectModelOpenApiGenerator();
   private final Configuration config = Configuration.defaultConfiguration().addOptions( Option.SUPPRESS_EXCEPTIONS );

   @ParameterizedTest
   @EnumSource( value = TestAspect.class)
   public void testGeneration( final TestAspect testAspect ) {
      final Aspect aspect = loadAspect( testAspect, KnownVersion.getLatest() );
      final JsonNode json = apiJsonGenerator.applyForJson( aspect, false, testBaseUrl, testResourcePath,
            Optional.empty(), false, Optional.empty() );
      assertSpecificationIsValid( json, json.toString(), aspect );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testUseSemanticVersion( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT, metaModelVersion );
      final JsonNode json = apiJsonGenerator.applyForJson( aspect, true, testBaseUrl, testResourcePath,
            Optional.empty(), false, Optional.empty() );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openAPI = result.getOpenAPI();

      assertThat( openAPI.getInfo().getVersion() ).isEqualTo( "v1.0.0" );

      openAPI.getServers().forEach( server -> assertThat( server.getUrl() ).contains( "v1.0.0" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testIncludeQueryApiWithSemanticVersion( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT, metaModelVersion );
      final JsonNode json = apiJsonGenerator.applyForJson( aspect, true, testBaseUrl,
            testResourcePath, Optional.empty(), true, Optional.empty() );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openAPI = result.getOpenAPI();

      assertThat( openAPI.getPaths().get( "/{tenant-id}/" + testResourcePath.get() ).getPost().getServers().get( 0 ).getUrl() )
            .isEqualTo( "https://test-aspect.example.com/query-api/v1.0.0" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testDefaultResourcePath( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final JsonNode json = apiJsonGenerator.applyForJson( aspect, true, testBaseUrl,
            Optional.empty(), Optional.empty(), true, Optional.empty() );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openAPI = result.getOpenAPI();

      assertThat( openAPI.getPaths().keySet() ).allMatch( path -> path.endsWith( "/aspect-without-see-attribute" ) );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testInvalidResourcePath( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final JsonNode json = apiJsonGenerator
            .applyForJson( aspect, true, testBaseUrl, testResourcePathWithParameter, Optional.empty(),
                  true, Optional.empty() );
      assertThat( json ).isEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testInvalidJsonParameter( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final JsonNode json = apiJsonGenerator
            .applyForJson( aspect, true, testBaseUrl, testResourcePathWithParameter, testInvalidParameter,
                  true, Optional.empty() );
      assertThat( json ).isEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testValidParameter( final KnownVersion metaModelVersion ) throws IOException {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final JsonNode json = apiJsonGenerator
            .applyForJson( aspect, true, testBaseUrl, testResourcePathWithParameter, Optional.of( getTestParameter() ),
                  false, Optional.empty() );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      assertThat( result.getMessages().size() ).isZero();

      final OpenAPI openAPI = result.getOpenAPI();
      assertThat( openAPI.getPaths() ).hasSize( 1 );
      assertThat( openAPI.getPaths().keySet() ).contains( "/{tenant-id}/my-test-aspect/{test-Id}" );
      openAPI.getPaths().forEach( ( key, value ) -> {
         final List<String> params = value.getGet().getParameters().stream().map( Parameter::getName )
               .collect( Collectors.toList() );
         assertThat( params ).contains( "tenant-id" );
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
      final JsonNode json = apiJsonGenerator
            .applyForJson( aspect, true, testBaseUrl, testResourcePathWithInvalidParameter,
                  Optional.of( getTestParameter() ), false, Optional.empty() );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      logAppender.stop();
      assertThat( result.getMessages().size() ).isNotZero();
      final List<String> logResults = logAppender.list.stream().map( ILoggingEvent::getFormattedMessage )
            .collect( Collectors.toList() );
      assertThat( logResults ).contains( "The parameter name test-\\Id is not in the correct form. A valid form is described as: ^[a-zA-Z][a-zA-Z0-9-_]*" );
      assertThat( logResults ).contains( "There was an exception during the read of the root or the validation." );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testYamlGenerator( final KnownVersion metaModelVersion ) throws IOException {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final YAMLMapper yamlMapper = new YAMLMapper().enable( YAMLGenerator.Feature.MINIMIZE_QUOTES );
      final String yamlProperties = yamlMapper.writeValueAsString( getTestParameter() );
      final String yaml = apiJsonGenerator
            .applyForYaml( aspect, true, testBaseUrl, testResourcePathWithParameter,
                  Optional.of( yamlProperties ), false, Optional.empty() );
      final JsonNode json = apiJsonGenerator
            .applyForJson( aspect, true, testBaseUrl, testResourcePathWithParameter,
                  Optional.of( getTestParameter() ), false, Optional.empty() );
      assertThat( yaml ).isEqualTo( yamlMapper.writeValueAsString( json ) );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      assertThat( result.getMessages().size() ).isZero();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHasQuerySchema( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final JsonNode json = apiJsonGenerator.applyForJson( aspect, true, testBaseUrl,
            Optional.empty(), Optional.empty(), true, Optional.empty() );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openAPI = result.getOpenAPI();

      assertThat( openAPI.getComponents().getSchemas().keySet() ).contains( "Filter" );
      assertThat( openAPI.getComponents().getRequestBodies().keySet() ).contains( "Filter" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHasNoQuerySchema( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITHOUT_SEE_ATTRIBUTE, metaModelVersion );
      final JsonNode json = apiJsonGenerator.applyForJson( aspect, true, testBaseUrl,
            Optional.empty(), Optional.empty(), false, Optional.empty() );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openAPI = result.getOpenAPI();

      assertThat( openAPI.getComponents().getSchemas().keySet() ).doesNotContain( "Filter" );
      assertThat( openAPI.getComponents().getRequestBodies().keySet() ).doesNotContain( "Filter" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHasPagingWithChosenPaging( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION, metaModelVersion );
      final JsonNode json = apiJsonGenerator.applyForJson( aspect, true, testBaseUrl,
            Optional.empty(), Optional.empty(), false,
            Optional.of( PagingOption.OFFSET_BASED_PAGING ) );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openAPI = result.getOpenAPI();
      assertThat( openAPI.getPaths().keySet() ).contains( "/{tenant-id}/aspect-with-collection" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 0 ).getName() ).isEqualTo( "tenant-id" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 1 ).getName() ).isEqualTo( "start" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 2 ).getName() ).isEqualTo( "count" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 3 ).getName() ).isEqualTo( "totalItemCount" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getResponses().get( "200" ).get$ref() )
            .isEqualTo( "#/components/responses/AspectWithCollection" );
      assertThat( openAPI.getComponents().getResponses().get( "AspectWithCollection" ).getContent().get( "application/json" ).getSchema().get$ref() )
            .isEqualTo( "#/components/schemas/PagingSchema" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHasPagingWithoutChosenPaging( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_TIME_SERIES, metaModelVersion );
      final JsonNode json = apiJsonGenerator.applyForJson( aspect, true, testBaseUrl, Optional.empty(),
            Optional.empty(), false,
            Optional.empty() );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openAPI = result.getOpenAPI();
      assertThat( openAPI.getPaths().keySet() ).contains( "/{tenant-id}/aspect-with-time-series" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 0 ).getName() ).isEqualTo( "tenant-id" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 1 ).getName() ).isEqualTo( "since" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 2 ).getName() ).isEqualTo( "until" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 3 ).getName() ).isEqualTo( "limit" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getResponses().get( "200" ).get$ref() )
            .isEqualTo( "#/components/responses/AspectWithTimeSeries" );
      assertThat( openAPI.getComponents().getResponses().get( "AspectWithTimeSeries" ).getContent()
            .get( "application/json" ).getSchema().get$ref() ).isEqualTo( "#/components/schemas/PagingSchema" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHasPagingWitChosenCursorBasedPaging( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION, metaModelVersion );
      final JsonNode json = apiJsonGenerator.applyForJson( aspect, true, testBaseUrl, Optional.empty(), Optional.empty(), false,
            Optional.of( PagingOption.CURSOR_BASED_PAGING ) );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openAPI = result.getOpenAPI();
      assertThat( openAPI.getPaths().keySet() ).contains( "/{tenant-id}/aspect-with-collection" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 0 ).getName() ).isEqualTo( "tenant-id" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 1 ).getName() ).isEqualTo( "previous" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 2 ).getName() ).isEqualTo( "next" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 3 ).getName() ).isEqualTo( "before" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 4 ).getName() ).isEqualTo( "after" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 5 ).getName() ).isEqualTo( "count" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 6 ).getName() ).isEqualTo( "totalItemCount" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getResponses().get( "200" ).get$ref() )
            .isEqualTo( "#/components/responses/AspectWithCollection" );
      assertThat( openAPI.getComponents().getResponses().get( "AspectWithCollection" ).getContent().get( "application/json" ).getSchema().get$ref() )
            .isEqualTo( "#/components/schemas/PagingSchema" );
      assertThat( openAPI.getComponents().getResponses().get( "AspectWithCollection" ).getContent().get( "application/json" ).getSchema().get$ref() )
            .isEqualTo( "#/components/schemas/PagingSchema" );
      assertThat( openAPI.getComponents().getSchemas().get( "PagingSchema" ).getProperties() ).containsKey( "items" );
      assertThat( openAPI.getComponents().getSchemas().get( "PagingSchema" ).getProperties() ).containsKey( "totalItems" );
      assertThat( openAPI.getComponents().getSchemas().get( "PagingSchema" ).getProperties() ).containsKey( "cursor" );
      assertThat( openAPI.getComponents().getSchemas().get( "PagingSchema" ).getProperties() ).containsKey( "_links" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHasPagingWithWithDefaultChosenPaging( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION, metaModelVersion );
      final JsonNode json = apiJsonGenerator.applyForJson( aspect, true, testBaseUrl, Optional.empty(),
            Optional.empty(), false, Optional.empty() );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openAPI = result.getOpenAPI();
      assertThat( openAPI.getPaths().keySet() ).contains( "/{tenant-id}/aspect-with-collection" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 0 ).getName() ).isEqualTo( "tenant-id" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 1 ).getName() ).isEqualTo( "start" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 2 ).getName() ).isEqualTo( "count" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getParameters().get( 3 ).getName() ).isEqualTo( "totalItemCount" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getResponses()
            .get( "200" ).get$ref() ).isEqualTo( "#/components/responses/AspectWithCollection" );
      assertThat( openAPI.getComponents().getResponses().get( "AspectWithCollection" ).getContent()
            .get( "application/json" ).getSchema().get$ref() ).isEqualTo( "#/components/schemas/PagingSchema" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testHasNoPagination( final KnownVersion metaModelVersion ) throws ProcessingException {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION, metaModelVersion );
      final JsonNode json = apiJsonGenerator.applyForJson( aspect, true, testBaseUrl,
            Optional.empty(), Optional.empty(), false, Optional.of( PagingOption.NO_PAGING ) );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openAPI = result.getOpenAPI();
      assertThat( openAPI.getPaths().keySet() ).contains( "/{tenant-id}/aspect-with-collection" );
      assertThat( openAPI.getPaths().values().stream().findFirst().get().getGet().getResponses()
            .get( "200" ).get$ref() ).isEqualTo( "#/components/responses/AspectWithCollection" );
      assertThat( openAPI.getComponents().getResponses().get( "AspectWithCollection" ).getContent()
            .get( "application/json" ).getSchema().get$ref() ).isEqualTo( "#/components/schemas/AspectWithCollection" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithOperation( final KnownVersion metaModelVersion ) throws ProcessingException {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_OPERATION, metaModelVersion );
      final JsonNode json = apiJsonGenerator.applyForJson( aspect, false, testBaseUrl, testResourcePath,
            Optional.empty(), false, Optional.empty() );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openAPI = result.getOpenAPI();
      assertThat( openAPI.getComponents().getSchemas() ).containsKey( "AspectWithOperation" );
      assertThat( openAPI.getComponents().getSchemas() ).containsKey( "Operation" );
      assertThat( openAPI.getComponents().getSchemas() ).containsKey( "OperationResponse" );
      assertThat( openAPI.getComponents().getSchemas() ).containsKey( "testOperation" );
      assertThat( openAPI.getComponents().getSchemas() ).containsKey( "testOperationResponse" );
      assertThat( openAPI.getComponents().getSchemas() ).containsKey( "testOperationTwo" );
      assertThat( openAPI.getComponents().getSchemas() ).containsKey( "testOperationTwoResponse" );
      assertThat( ((ComposedSchema) openAPI.getComponents().getSchemas().get( "Operation" )).getOneOf().size() )
            .isEqualTo( 2 );
      assertThat( ((ComposedSchema) openAPI.getComponents().getSchemas().get( "OperationResponse" )).getOneOf().size() )
            .isEqualTo( 2 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithOperationWithSeeAttribute( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_OPERATION_WITH_SEE_ATTRIBUTE, metaModelVersion );
      final JsonNode json = apiJsonGenerator.applyForJson( aspect, false, testBaseUrl, testResourcePath,
            Optional.empty(), false, Optional.empty() );
      final SwaggerParseResult result = new OpenAPIParser().readContents( json.toString(), null, null );
      final OpenAPI openAPI = result.getOpenAPI();
      assertThat(
            ((ComposedSchema) openAPI.getComponents().getSchemas().get( "testOperation" )).getAllOf().get( 1 )
                  .getProperties() )
            .doesNotContainKey( "params" );
      assertThat(
            ((ComposedSchema) openAPI.getComponents().getSchemas().get( "testOperationTwo" )).getAllOf().get( 1 )
                  .getProperties() )
            .doesNotContainKey( "params" );
   }

   private void assertSpecificationIsValid( final JsonNode jsonNode, final String json, final Aspect aspect ) {
      validateUnsupportedKeywords( json );

      final SwaggerParseResult result = new OpenAPIParser().readContents( json, null, null );
      assertThat( result.getMessages().size() ).isZero();

      final OpenAPI openAPI = result.getOpenAPI();
      validateOpenApiSpec( jsonNode, openAPI, aspect );

      final DocumentContext context = JsonPath.parse( json );
      assertThat( context.<Object> read( "$['components']['schemas']['" + aspect.getName() + "']" ) ).isNotNull();
      if ( !aspect.getOperations().isEmpty() ) {
         validateOperation( openAPI );
      } else {
         assertThat( openAPI.getComponents().getSchemas().keySet() ).doesNotContain( "JsonRpc" );
      }
      validateYaml( aspect );
   }

   private void validateOpenApiSpec( final JsonNode node, final OpenAPI openAPI, final Aspect aspect ) {
      assertThat( openAPI.getInfo().getTitle() ).isEqualTo( aspect.getPreferredName( Locale.ENGLISH ) );

      final String expectedApiVersion = getExpectedApiVersion( aspect );
      assertThat( openAPI.getInfo().getVersion() ).isEqualTo( expectedApiVersion );

      assertThat( openAPI.getServers() ).hasSize( 1 );
      assertThat( openAPI.getServers().get( 0 ).getUrl() ).isEqualTo( testBaseUrl + "/api/" + expectedApiVersion );

      assertThat( openAPI.getPaths().keySet() ).noneMatch( path -> path.contains( "/query-api" ) );
      openAPI.getPaths().entrySet().stream().filter( item -> !item.getKey().contains( "/operations" ) )
            .forEach( path -> {
               assertThat( path.getKey() ).startsWith( "/{tenant-id}/" + testResourcePath.get() );
               path.getValue().readOperations()
                     .forEach( operation -> validateSuccessfulResponse( aspect.getName(), operation ) );
            } );

      assertThat( openAPI.getComponents().getSchemas().keySet() ).contains( aspect.getName() );
      assertThat( openAPI.getComponents().getResponses().keySet() ).contains( aspect.getName() );
      assertThat( openAPI.getComponents().getRequestBodies().keySet() ).contains( aspect.getName() );

      validateReferences( node );
   }

   private void validateReferences( final JsonNode rootNode ) {
      final List<JsonNode> nodes = rootNode.findValues( "$ref" ).stream().distinct().collect( Collectors.toList() );
      nodes.forEach( node ->
      {
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

   private void validateUnsupportedKeywords( final String json ) {
      assertThat( json ).doesNotContain( "$schema" );
      assertThat( json ).doesNotContain( "additionalItems" );
      assertThat( json ).doesNotContain( "cost" );
      assertThat( json ).doesNotContain( "contains" );
      assertThat( json ).doesNotContain( "dependencies" );
      assertThat( json ).doesNotContain( "$id" );
      assertThat( json ).doesNotContain( "patternProperties" );
      assertThat( json ).doesNotContain( "propertyNames" );
   }

   private void validateOperation( final OpenAPI openAPI ) {
      assertThat( openAPI.getComponents().getSchemas().keySet() ).contains( "JsonRpc" );
      assertThat( openAPI.getComponents().getSchemas().keySet() ).contains( "Operation" );
      assertThat( openAPI.getComponents().getSchemas().keySet() ).contains( "OperationResponse" );
      assertThat( openAPI.getComponents().getResponses().keySet() ).contains( "OperationResponse" );
      assertThat( openAPI.getComponents().getRequestBodies().keySet() ).contains( "Operation" );
   }

   private void validateYaml( final Aspect aspect ) {
      final AspectModelOpenApiGenerator apiJsonGenerator = new AspectModelOpenApiGenerator();
      try {
         apiJsonGenerator.applyForYaml( aspect, false, testBaseUrl, testResourcePath, Optional.empty(), false, Optional.empty() );
      } catch ( final JsonProcessingException e ) {
         fail( "Exception occurred during OpenAPI Yaml creation.", e );
      }
   }

   private Aspect loadAspect( final TestAspect testAspect, final KnownVersion metaModelVersion ) {
      final VersionedModel versionedModel = TestResources.getModel( testAspect, metaModelVersion ).get();
      return AspectModelLoader.fromVersionedModel( versionedModel ).get();
   }

   private JsonNode getTestParameter() throws IOException {
      final InputStream inputStream = getClass().getResourceAsStream( "/openapi/parameter.json" );
      final String inputString = IOUtils.toString( inputStream, StandardCharsets.UTF_8.name() );
      return objectMapper.readTree( inputString );
   }
}
