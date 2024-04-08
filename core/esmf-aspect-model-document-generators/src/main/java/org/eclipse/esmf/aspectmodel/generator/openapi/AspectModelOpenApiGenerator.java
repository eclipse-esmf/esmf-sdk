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

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.generator.AbstractGenerator;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.stream.Streams;
import org.eclipse.esmf.aspectmodel.generator.ArtifactGenerator;
import org.eclipse.esmf.aspectmodel.generator.jsonschema.AspectModelJsonSchemaGenerator;
import org.eclipse.esmf.aspectmodel.generator.jsonschema.AspectModelJsonSchemaVisitor;
import org.eclipse.esmf.aspectmodel.generator.jsonschema.JsonSchemaGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.jsonschema.JsonSchemaGenerationConfigBuilder;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.CaseFormat;

import io.vavr.CheckedFunction1;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings( "OptionalUsedAsFieldOrParameterType" )
public class AspectModelOpenApiGenerator
      implements ArtifactGenerator<String, ObjectNode, Aspect, OpenApiSchemaGenerationConfig, OpenApiSchemaArtifact> {
   private static final String APPLICATION_JSON = "application/json";
   private static final String CLIENT_ERROR = "ClientError";
   private static final String COMPONENTS_RESPONSES = "#/components/responses/";
   private static final String COMPONENTS_REQUESTS = "#/components/requestBodies/";
   private static final String COMPONENTS_SCHEMAS = "#/components/schemas/";
   private static final String FIELD_COMPONENTS = "components";
   private static final String FIELD_CONTENT = "content";
   private static final String FIELD_DESCRIPTION = "description";
   private static final String FIELD_FILTER = "Filter";
   private static final String FIELD_GET = "get";
   private static final String FIELD_POST = "post";
   private static final String FIELD_PUT = "put";
   private static final String FIELD_PATCH = "patch";
   private static final String FIELD_OBJECT = "object";
   private static final String FIELD_OPERATION = "Operation";
   private static final String FIELD_OPERATION_ID = "operationId";
   private static final String FIELD_OPERATION_RESPONSE = "OperationResponse";
   protected static final String FIELD_PAGING_SCHEMA = "PagingSchema";
   private static final String FIELD_PARAMETERS = "parameters";
   private static final String FIELD_PROPERTIES = "properties";
   private static final String FIELD_RPC = "JsonRpc";
   private static final String FIELD_REQUEST_BODIES = "requestBodies";
   private static final String FIELD_REQUEST_BODY = "requestBody";
   private static final String FIELD_REQUIRED = "required";
   private static final String FIELD_RESPONSES = "responses";
   private static final String FIELD_SCHEMA = "schema";
   private static final String FIELD_SCHEMAS = "schemas";
   private static final String FIELD_STRING = "string";
   private static final String FIELD_TYPE = "type";
   private static final String FORBIDDEN = "Forbidden";
   private static final String NOT_FOUND_ERROR = "NotFoundError";
   private static final String OPERATIONS_SERVER_PATH = "/rpc-api/%s";
   private static final String TENANT_ID = "/{tenant-id}";
   private static final String OPERATIONS_ENDPOINT_PATH = "%s/operations";
   private static final String PARAMETER_CONVENTION = "^[a-zA-Z][a-zA-Z0-9-_]*";
   private static final String QUERY_SERVER_PATH = "/query-api/%s";
   private static final String READ_SERVER_PATH = "/api/%s";
   private static final String REF = "$ref";

   private static final String UNAUTHORIZED = "Unauthorized";

   private static final String V30 = "3.0.3";
   private static final String V31 = "3.1.0";

   private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;
   private static final AspectModelJsonSchemaVisitor SCHEMA_VISITOR = new AspectModelJsonSchemaVisitor(
         JsonSchemaGenerationConfigBuilder.builder()
               .useExtendedTypes( false )
               .build() );
   private static final AspectModelPagingGenerator PAGING_GENERATOR = new AspectModelPagingGenerator();
   private static final Logger LOG = LoggerFactory.getLogger( AspectModelOpenApiGenerator.class );

   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
   private static final ObjectMapper YAML_MAPPER = new ObjectMapper( new YAMLFactory() );

   /**
    * Generates an OpenAPI specification for the given Aspect Model.
    *
    * @param aspect the Aspect Model for which the OpenAPI specification will be generated.
    * @param config the configuration for the generation process
    * @return the OpenAPI specification
    */
   @Override
   public OpenApiSchemaArtifact apply( final Aspect aspect, final OpenApiSchemaGenerationConfig config ) {
      try {
         final ObjectNode rootNode = getRootJsonNode( config.generateCommentForSeeAttributes() );
         final String apiVersion = getApiVersion( aspect, config.useSemanticVersion() );

         ((ObjectNode) rootNode.get( "info" )).put( "title", aspect.getPreferredName( config.locale() ) );
         ((ObjectNode) rootNode.get( "info" )).put( "version", apiVersion );
         ((ObjectNode) rootNode.get( "info" )).put( AbstractGenerator.SAMM_EXTENSION,
               aspect.getAspectModelUrn().map( Object::toString ).orElse( "" ) );
         setServers( rootNode, config.baseUrl(), apiVersion, READ_SERVER_PATH );
         final boolean includePaging = includePaging( aspect, config.pagingOption() );
         setOptionalSchemas( aspect, config, includePaging, rootNode );
         setAspectSchemas( aspect, config, rootNode );
         setRequestBodies( aspect, config, rootNode );
         setResponseBodies( aspect, rootNode, includePaging );
         rootNode.set( "paths", getPathsNode( aspect, config, apiVersion, config.properties() ) );
         return new OpenApiSchemaArtifact( aspect.getName(), rootNode );
      } catch ( final Exception exception ) {
         LOG.error( "There was an exception during the read of the root or the validation.", exception );
      }
      return new OpenApiSchemaArtifact( aspect.getName(), FACTORY.objectNode() );
   }

   /**
    * Generates an OpenAPI specification for the given Aspect Model in JSON.
    *
    * @param aspect             the Aspect Model for which the OpenAPI specification will be generated.
    * @param useSemanticVersion if set to true, the complete semantic version of the Aspect Model will be used as the version of the API.
    *                           Otherwise only the major part of the Aspect Version is used as the version of the API.
    * @param baseUrl            the base URL for the Aspect API
    * @param resourcePath       the resource path for the Aspect API endpoints. If no resource path is given, the resource path will be derived
    *                           from the Aspect name
    * @param jsonProperties     A string containing the needed properties for the resource path, defined in JSON.
    * @param includeQueryApi    if set to true, a path section for the Query API Endpoint of the Aspect API will be included in the
    *                           specification
    * @param pagingOption       if defined, the chosen paging type will be in the JSON.
    * @return a JsonNode containing the JSON for the given Aspect.
    * @deprecated Use {@link #apply(Aspect, OpenApiSchemaGenerationConfig)} instead
    */
   @Deprecated( forRemoval = true )
   public JsonNode applyForJson( final Aspect aspect, final boolean useSemanticVersion, final String baseUrl,
         final Optional<String> resourcePath, final Optional<JsonNode> jsonProperties, final boolean includeQueryApi,
         final Optional<PagingOption> pagingOption ) {
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( useSemanticVersion )
            .baseUrl( baseUrl )
            .resourcePath( resourcePath.orElse( null ) )
            .properties( (ObjectNode) jsonProperties.orElse( null ) )
            .includeQueryApi( includeQueryApi )
            .pagingOption( pagingOption.orElse( null ) )
            .build();
      return apply( aspect, config ).getContent();
   }

   /**
    * Generates an OpenAPI specification for the given Aspect Model in JSON.
    *
    * @param aspect             the Aspect Model for which the OpenAPI specification will be generated.
    * @param useSemanticVersion if set to true, the complete semantic version of the Aspect Model will be used as the version of the API.
    *                           Otherwise only the major part of the Aspect Version is used as the version of the API.
    * @param baseUrl            the base URL for the Aspect API
    * @param resourcePath       the resource path for the Aspect API endpoints. If no resource path is given, the resource path will be derived
    *                           from the Aspect name.
    * @param jsonProperties     A string containing the needed properties for the resource path, defined in JSON.
    * @param includeQueryApi    if set to true, a path section for the Query API Endpoint of the Aspect API will be included in the
    *                           specification
    * @param pagingOption       if defined, the chosen paging type will be in the JSON.
    * @param locale             the locale for choosing the preferred language for description and preferred name.
    * @return a JsonNode containing the JSON for the given Aspect.
    * @deprecated Use {@link #apply(Aspect, OpenApiSchemaGenerationConfig)} instead
    */
   @Deprecated( forRemoval = true )
   public JsonNode applyForJson( final Aspect aspect, final boolean useSemanticVersion, final String baseUrl,
         final Optional<String> resourcePath, final Optional<JsonNode> jsonProperties, final boolean includeQueryApi,
         final Optional<PagingOption> pagingOption, final Locale locale ) {
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( useSemanticVersion )
            .baseUrl( baseUrl )
            .resourcePath( resourcePath.orElse( null ) )
            .properties( (ObjectNode) jsonProperties.orElse( null ) )
            .includeQueryApi( includeQueryApi )
            .pagingOption( pagingOption.orElse( null ) )
            .locale( locale )
            .build();
      return apply( aspect, config ).getContent();
   }

   /**
    * Generates an OpenAPI specification for the given Aspect Model in JSON.
    *
    * @param aspect                          the Aspect Model for which the OpenAPI specification will be generated.
    * @param useSemanticVersion              if set to true, the complete semantic version of the Aspect Model will be used as the version of the API.
    *                                        Otherwise only the major part of the Aspect Version is used as the version of the API.
    * @param baseUrl                         the base URL for the Aspect API
    * @param resourcePath                    the resource path for the Aspect API endpoints. If no resource path is given, the resource path will be derived
    *                                        from the Aspect name
    * @param jsonProperties                  A string containing the needed properties for the resource path, defined in JSON.
    * @param includeQueryApi                 if set to true, a path section for the Query API Endpoint of the Aspect API will be included in the
    *                                        specification
    * @param pagingOption                    if defined, the chosen paging type will be in the JSON.
    * @param locale                          the locale for choosing the preferred language for description and preferred name.
    * @param generateCommentForSeeAttributes generate $comment OpenAPI element for samm:see attributes in the model
    * @return a JsonNode containing the JSON for the given Aspect.
    * @deprecated Use {@link #apply(Aspect, OpenApiSchemaGenerationConfig)} instead
    */
   @Deprecated( forRemoval = true )
   public JsonNode applyForJson( final Aspect aspect, final boolean useSemanticVersion, final String baseUrl,
         final Optional<String> resourcePath, final Optional<JsonNode> jsonProperties, final boolean includeQueryApi,
         final Optional<PagingOption> pagingOption, final Locale locale, final boolean generateCommentForSeeAttributes ) {
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( useSemanticVersion )
            .baseUrl( baseUrl )
            .resourcePath( resourcePath.orElse( null ) )
            .properties( (ObjectNode) jsonProperties.orElse( null ) )
            .includeQueryApi( includeQueryApi )
            .pagingOption( pagingOption.orElse( null ) )
            .locale( locale )
            .generateCommentForSeeAttributes( generateCommentForSeeAttributes )
            .build();
      return apply( aspect, config ).getContent();
   }

   private void setServers( final ObjectNode objectNode, final String baseUrl, final String apiVersion, final String endPointPath ) {
      final ArrayNode arrayNode = objectNode.putArray( "servers" );
      final ObjectNode node = FACTORY.objectNode();
      final ObjectNode variables = FACTORY.objectNode();
      node.put( "url", baseUrl + String.format( endPointPath, apiVersion ) );
      node.set( "variables", variables );
      variables.set( "api-version", FACTORY.objectNode().put( "default", apiVersion ) );
      arrayNode.add( node );
   }

   private boolean includePaging( final Aspect aspect, final PagingOption pagingType ) {
      return pagingType != PagingOption.NO_PAGING
             && PAGING_GENERATOR.isPagingPossible( aspect );
   }

   private ObjectNode getPropertiesNode( final String resourcePath, final ObjectNode properties ) {
      if ( properties != null && resourcePath == null ) {
         LOG.warn( "There are parameter definitions but no resource path." );
         return FACTORY.objectNode();
      }

      if ( resourcePath != null ) {
         final List<String> dynamicParameters = Pattern.compile( "[{]\\S+?[}]" ).matcher( resourcePath ).results()
               .map( match -> match.group( 0 ) ).toList();
         if ( !dynamicParameters.isEmpty() && properties == null ) {
            final String errorString = String
                  .format( "Resource path contains properties %s, but has no properties map.", dynamicParameters );
            LOG.error( errorString );
            throw new IllegalArgumentException( errorString );
         }
         if ( properties != null ) {
            dynamicParameters.forEach( match -> {
               final String nodeName = match.replace( "{", "" ).replace( "}", "" );
               validateParameterName( nodeName );
               final JsonNode node = properties.get( nodeName );
               if ( node == null ) {
                  final String errorString = String
                        .format( "Resource path contains property %s, but can't be found in properties map.%s", nodeName, properties );
                  LOG.error( errorString );
                  throw new IllegalArgumentException( errorString );
               }
            } );
            return properties;
         }
      }
      return FACTORY.objectNode();
   }

   private void validateParameterName( final String nodeName ) {
      final Matcher a = Pattern.compile( PARAMETER_CONVENTION ).matcher( nodeName );
      if ( !a.matches() ) {
         final String errorString = String
               .format( "The parameter name %s is not in the correct form. A valid form is described as: %s", nodeName,
                     PARAMETER_CONVENTION );
         LOG.error( errorString );
         throw new IllegalArgumentException( errorString );
      }
   }

   private void setOptionalSchemas( final Aspect aspect, final OpenApiSchemaGenerationConfig config, final boolean includePaging,
         final ObjectNode rootNode ) throws IOException {
      final ObjectNode schemas = (ObjectNode) rootNode.get( FIELD_COMPONENTS ).get( FIELD_SCHEMAS );
      if ( config.includeQueryApi() ) {
         try ( final InputStream inputStream = getClass().getResourceAsStream( "/openapi/Filter.json" ) ) {
            Objects.requireNonNull( inputStream, "Filter.json not found" );
            final String string = IOUtils.toString( inputStream, StandardCharsets.UTF_8 );
            final ObjectNode filterNode = (ObjectNode) OBJECT_MAPPER.readTree( string );
            schemas.set( FIELD_FILTER, filterNode );
         }
      }
      if ( !aspect.getOperations().isEmpty() ) {
         try ( final InputStream inputStream = getClass().getResourceAsStream( "/openapi/JsonRPC.json" ) ) {
            Objects.requireNonNull( inputStream, "JsonRPC.json not found" );
            final String string = IOUtils.toString( inputStream, StandardCharsets.UTF_8 );
            final ObjectNode filterNode = (ObjectNode) OBJECT_MAPPER.readTree( string );
            schemas.set( FIELD_RPC, filterNode );
         }
      }
      if ( includePaging ) {
         PAGING_GENERATOR.setSchemaInformationForPaging( aspect, schemas, config.pagingOption() );
      }
   }

   @SuppressWarnings( "squid:S3655" ) // An Aspect always has an URN
   private String getApiVersion( final Aspect aspect, final boolean useSemanticVersion ) {
      @SuppressWarnings( "OptionalGetWithoutIsPresent" ) final String aspectVersion = aspect.getAspectModelUrn().get().getVersion();
      if ( useSemanticVersion ) {
         return String.format( "v%s", aspectVersion );
      }
      final int endIndexOfMajorVersion = aspectVersion.indexOf( '.' );
      final String majorAspectVersion = aspectVersion.substring( 0, endIndexOfMajorVersion );
      return String.format( "v%s", majorAspectVersion );
   }

   private void setResponseBodies( final Aspect aspect, final ObjectNode jsonNode, final boolean includePaging ) {
      final ObjectNode componentsResponseNode = (ObjectNode) jsonNode.get( FIELD_COMPONENTS ).get( FIELD_RESPONSES );
      final ObjectNode referenceNode = FACTORY.objectNode()
            .put( REF, COMPONENTS_SCHEMAS + (includePaging ? FIELD_PAGING_SCHEMA : aspect.getName()) );
      final ObjectNode contentNode = getApplicationNode( referenceNode );
      componentsResponseNode.set( aspect.getName(), contentNode );
      contentNode.put( FIELD_DESCRIPTION, "The request was successful." );
      if ( !aspect.getOperations().isEmpty() ) {
         final ObjectNode operationResponseNode = FACTORY.objectNode()
               .put( REF, COMPONENTS_SCHEMAS + FIELD_OPERATION_RESPONSE );
         final ObjectNode wrappedOperationNode = getApplicationNode( operationResponseNode );
         componentsResponseNode.set( FIELD_OPERATION_RESPONSE, wrappedOperationNode );
         wrappedOperationNode.put( FIELD_DESCRIPTION, "The request was successful." );
      }
   }

   private void setRequestBodies( final Aspect aspect, final OpenApiSchemaGenerationConfig config, final ObjectNode jsonNode ) {
      final ObjectNode componentNode = (ObjectNode) jsonNode.get( FIELD_COMPONENTS );
      final ObjectNode requestBodies = FACTORY.objectNode();
      componentNode.set( FIELD_REQUEST_BODIES, requestBodies );
      requestBodies.set( aspect.getName(),
            getApplicationNode( FACTORY.objectNode().put( REF, COMPONENTS_SCHEMAS + aspect.getName() ) ) );
      if ( config.includeQueryApi() ) {
         requestBodies.set( FIELD_FILTER,
               getApplicationNode( FACTORY.objectNode().put( REF, COMPONENTS_SCHEMAS + FIELD_FILTER ) ) );
      }
      if ( !aspect.getOperations().isEmpty() ) {
         requestBodies.set( FIELD_OPERATION,
               getApplicationNode( FACTORY.objectNode().put( REF, COMPONENTS_SCHEMAS + FIELD_OPERATION ) ) );
      }
   }

   private ObjectNode getApplicationNode( final ObjectNode contentNode ) {
      return FACTORY.objectNode().set( FIELD_CONTENT,
            FACTORY.objectNode().set( APPLICATION_JSON,
                  FACTORY.objectNode().set( FIELD_SCHEMA, contentNode ) ) );
   }

   private void setAspectSchemas( final Aspect aspect, final OpenApiSchemaGenerationConfig config, final ObjectNode jsonNode ) {
      final ObjectNode schemas = (ObjectNode) jsonNode.get( FIELD_COMPONENTS ).get( FIELD_SCHEMAS );
      setAspectSchemaNode( schemas, aspect, config.locale(), config.generateCommentForSeeAttributes() );
      final List<Operation> operations = aspect.getOperations();
      if ( !operations.isEmpty() ) {
         if ( operations.size() == 1 ) {
            aspect.getOperations().stream()
                  .collect( Collectors.toMap( NamedElement::getName, Operation::getInput ) )
                  .entrySet().stream()
                  .findFirst()
                  .ifPresent( entry -> schemas.set( FIELD_OPERATION, getRequestBodyForPropertyList( entry ) ) );
            aspect.getOperations().stream()
                  .collect( Collectors.toMap( NamedElement::getName, Operation::getOutput ) )
                  .entrySet().stream()
                  .findFirst().ifPresent(
                        entry -> schemas.set( FIELD_OPERATION_RESPONSE, getResponseSchemaForOperation( entry.getValue() ) ) );
         } else {
            final ArrayNode arrayNode = FACTORY.arrayNode();
            schemas.set( FIELD_OPERATION, FACTORY.objectNode().set( "oneOf", arrayNode ) );
            aspect.getOperations().stream()
                  .collect( Collectors.toMap( NamedElement::getName, Operation::getInput ) )
                  .entrySet().forEach( entry -> {
                     schemas.set( entry.getKey(), getRequestBodyForPropertyList( entry ) );
                     arrayNode.add( FACTORY.objectNode().put( REF, COMPONENTS_SCHEMAS + entry.getKey() ) );
                  } );
            final ArrayNode responseArrayNode = FACTORY.arrayNode();
            schemas.set( FIELD_OPERATION_RESPONSE, FACTORY.objectNode().set( "oneOf", responseArrayNode ) );
            aspect.getOperations().stream()
                  .collect( Collectors.toMap( NamedElement::getName, Operation::getOutput ) )
                  .forEach( ( key, value ) -> {
                     schemas.set( key + "Response", getResponseSchemaForOperation( value ) );
                     responseArrayNode.add( FACTORY.objectNode().put( REF, COMPONENTS_SCHEMAS + key + "Response" ) );
                  } );
         }
         SCHEMA_VISITOR.getRootNode().get( FIELD_COMPONENTS ).get( FIELD_SCHEMAS ).fields()
               .forEachRemaining( field -> schemas.set( field.getKey(), field.getValue() ) );
      }
   }

   private ObjectNode getRootJsonNode( final boolean generateCommentForSeeAttributes ) throws IOException {
      try ( final InputStream inputStream = getClass().getResourceAsStream( "/openapi/OpenApiRootJson.json" ) ) {
         // at least one important OpenAPI tool (swagger-ui) still does not support v3.1, so for the time being we stay with the version 3.0.3;
         // only when $comment is explicitly requested (a v3.1 feature) we switch to 3.1.0
         Objects.requireNonNull( inputStream, "OpenApiRootJson.json not found" );
         final String string = IOUtils.toString( inputStream, StandardCharsets.UTF_8 )
               .replace( "${OpenApiVer}", generateCommentForSeeAttributes ? V31 : V30 );
         return (ObjectNode) OBJECT_MAPPER.readTree( string );
      }
   }

   /**
    * Generates an OpenAPI specification for the given Aspect Model in YAML.
    *
    * @param aspect             the Aspect Model for which the OpenAPI specification will be generated.
    * @param useSemanticVersion if set to true, the complete semantic version of the Aspect Model will be used as the version of the API.
    *                           Otherwise only the major part of the Aspect Version is used as the version of the API.
    * @param baseUrl            the base URL for the Aspect API
    * @param resourcePath       the resource path for the Aspect API endpoints. If no resource path is given, the resource path will be derived
    *                           from the Aspect name
    * @param yamlProperties     the properties for the resource. Needs to be defined as YAML.
    * @param includeQueryApi    if set to true, a path section for the Query API Endpoint of the Aspect API will be included in the
    *                           specification
    * @param pagingOption       if defined, the chosen paging type will be in the YAML.
    * @return a string containing the YAML for the given aspect.
    * @deprecated Use {@link #apply(Aspect, OpenApiSchemaGenerationConfig)} instead
    */
   @Deprecated( forRemoval = true )
   public String applyForYaml( final Aspect aspect, final boolean useSemanticVersion, final String baseUrl,
         final Optional<String> resourcePath, final Optional<String> yamlProperties, final boolean includeQueryApi,
         final Optional<PagingOption> pagingOption ) {
      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( useSemanticVersion )
            .baseUrl( baseUrl )
            .resourcePath( resourcePath.orElse( null ) )
            .properties(
                  (ObjectNode) yamlProperties.map( CheckedFunction1.of( ( String content ) -> YAML_MAPPER.readTree( content ) ).unchecked() ).orElse( null ) )
            .includeQueryApi( includeQueryApi )
            .pagingOption( pagingOption.orElse( null ) )
            .build();
      return apply( aspect, config ).getContentAsYaml();
   }

   /**
    * Generates an OpenAPI specification for the given Aspect Model in YAML.
    *
    * @param aspect             the Aspect Model for which the OpenAPI specification will be generated.
    * @param useSemanticVersion if set to true, the complete semantic version of the Aspect Model will be used as the version of the API.
    *                           Otherwise only the major part of the Aspect Version is used as the version of the API.
    * @param baseUrl            the base URL for the Aspect API
    * @param resourcePath       the resource path for the Aspect API endpoints. If no resource path is given, the resource path will be derived
    *                           from the Aspect name
    * @param yamlProperties     the properties for the resource. Needs to be defined as YAML.
    * @param includeQueryApi    if set to true, a path section for the Query API Endpoint of the Aspect API will be included in the
    *                           specification
    * @param pagingOption       if defined, the chosen paging type will be in the YAML.
    * @param locale             the locale for choosing the preferred language for description and preferred name.
    * @return a string containing the YAML for the given aspect.
    * @deprecated Use {@link #apply(Aspect, OpenApiSchemaGenerationConfig)} instead
    */
   @Deprecated( forRemoval = true )
   public String applyForYaml( final Aspect aspect, final boolean useSemanticVersion, final String baseUrl,
         final Optional<String> resourcePath, final Optional<String> yamlProperties, final boolean includeQueryApi,
         final Optional<PagingOption> pagingOption, final Locale locale ) {

      final OpenApiSchemaGenerationConfig config = OpenApiSchemaGenerationConfigBuilder.builder()
            .useSemanticVersion( useSemanticVersion )
            .baseUrl( baseUrl )
            .resourcePath( resourcePath.orElse( null ) )
            .properties(
                  (ObjectNode) yamlProperties.map( CheckedFunction1.of( ( String content ) -> YAML_MAPPER.readTree( content ) ).unchecked() ).orElse( null ) )
            .includeQueryApi( includeQueryApi )
            .pagingOption( pagingOption.orElse( null ) )
            .locale( locale )
            .build();
      return apply( aspect, config ).getContentAsYaml();
   }

   private ObjectNode getPathsNode( final Aspect aspect, final OpenApiSchemaGenerationConfig config, final String apiVersion,
         final ObjectNode properties ) throws IOException {
      final ObjectNode endpointPathsNode = FACTORY.objectNode();
      final ObjectNode pathNode = FACTORY.objectNode();
      final ObjectNode propertiesNode = getPropertiesNode( config.resourcePath(), properties );
      // If resource path is provided then use it as the complete path and don't prefix tenant-id to it.
      final String path = config.resourcePath();
      final String finalResourcePath = path != null
            ? path.startsWith( "/" ) ? path : "/" + path
            : TENANT_ID + "/" + deriveResourcePathFromAspectName( aspect.getName() );

      endpointPathsNode.set( finalResourcePath, pathNode );

      if ( includePaging( aspect, config.pagingOption() ) ) {
         PAGING_GENERATOR.setPagingProperties( aspect, config.pagingOption(), propertiesNode );
      }

      pathNode.set( FIELD_GET, getRequestEndpointsRead( aspect, propertiesNode, config.resourcePath() ) );

      boolean includeCrud = config.includeCrud();

      if ( includeCrud || config.includePost() ) {
         pathNode.set( FIELD_POST, getRequestEndpointsCreate( aspect, propertiesNode, config.resourcePath() ) );
      }

      if ( includeCrud || config.includePut() ) {
         pathNode.set( FIELD_PUT, getRequestEndpointsUpdate( aspect, propertiesNode, config.resourcePath(), true ) );
      }

      if ( includeCrud || config.includePatch() ) {
         pathNode.set( FIELD_PATCH, getRequestEndpointsUpdate( aspect, propertiesNode, config.resourcePath(), false ) );
      }

      if ( config.includeQueryApi() ) {
         final ObjectNode includeQueryPathNode = FACTORY.objectNode();
         includeQueryPathNode.set( FIELD_POST,
               getRequestEndpointFilter( aspect, propertiesNode, config.baseUrl(), apiVersion, config.resourcePath() ) );
         endpointPathsNode.set( config.baseUrl() + String.format( QUERY_SERVER_PATH, apiVersion ) + finalResourcePath,
               includeQueryPathNode );
      }

      final Optional<ObjectNode> operationsNode = getRequestEndpointOperations( aspect, propertiesNode, config.baseUrl(), apiVersion,
            config.resourcePath() );
      operationsNode.ifPresent(
            jsonNodes -> endpointPathsNode
                  .set( String.format( OPERATIONS_ENDPOINT_PATH, finalResourcePath ), jsonNodes ) );
      return endpointPathsNode;
   }

   private String deriveResourcePathFromAspectName( final String aspectName ) {
      return CaseFormat.UPPER_CAMEL.to( CaseFormat.LOWER_HYPHEN, aspectName );
   }

   private Optional<ObjectNode> getRequestEndpointOperations( final Aspect aspect, final ObjectNode parameterNode, final String baseUrl,
         final String apiVersion, final String resourcePath ) {
      if ( !aspect.getOperations().isEmpty() ) {
         final ObjectNode postNode = FACTORY.objectNode();
         final ObjectNode objectNode = FACTORY.objectNode();
         setServers( objectNode, baseUrl, apiVersion, OPERATIONS_SERVER_PATH );
         objectNode.set( "tags", FACTORY.arrayNode().add( aspect.getName() ) );
         objectNode.put( FIELD_OPERATION_ID, FIELD_POST + FIELD_OPERATION + aspect.getName() );
         objectNode.set( FIELD_PARAMETERS, getRequiredParameters( parameterNode, isEmpty( resourcePath ) ) );
         objectNode.set( FIELD_REQUEST_BODY, FACTORY.objectNode().put( REF, COMPONENTS_REQUESTS + FIELD_OPERATION ) );
         final ObjectNode responseNode = FACTORY.objectNode();
         objectNode.set( FIELD_RESPONSES, responseNode );
         responseNode.set( "200", FACTORY.objectNode().put( REF, COMPONENTS_RESPONSES + FIELD_OPERATION_RESPONSE ) );
         setErrorResponses( responseNode );
         postNode.set( FIELD_POST, objectNode );
         return Optional.of( postNode );
      }
      return Optional.empty();
   }

   private JsonNode getRequestBodyForPropertyList( final Map.Entry<String, List<Property>> propertyEntry ) {
      final ObjectNode propertyNode = FACTORY.objectNode();
      final ArrayNode arrayNode = FACTORY.arrayNode();
      final ObjectNode objectNode = FACTORY.objectNode();

      propertyNode.set( "allOf", arrayNode );
      arrayNode.add( FACTORY.objectNode().put( REF, COMPONENTS_SCHEMAS + FIELD_RPC ) );
      arrayNode.add( objectNode );

      final ObjectNode propertiesNode = FACTORY.objectNode();
      objectNode.set( FIELD_PROPERTIES, propertiesNode );
      if ( !propertyEntry.getValue().isEmpty() ) {
         propertiesNode.set( "params", getParamsNode( propertyEntry.getValue() ) );
      }
      propertiesNode.set( "method", getMethodNode( propertyEntry.getKey() ) );
      return propertyNode;
   }

   private ObjectNode getParamsNode( final List<Property> property ) {
      final ObjectNode objectNode = FACTORY.objectNode();
      final ArrayNode requiredNode = FACTORY.arrayNode();
      final ObjectNode propertyNode = FACTORY.objectNode();
      objectNode.put( FIELD_TYPE, FIELD_OBJECT );
      objectNode.set( FIELD_REQUIRED, requiredNode );
      objectNode.set( FIELD_PROPERTIES, propertyNode );
      property.stream().map( NamedElement::getName ).distinct().forEach( requiredNode::add );
      property.forEach(
            prop -> propertyNode.set( prop.getName(), SCHEMA_VISITOR.visitProperty( prop, FACTORY.objectNode() ) ) );
      return objectNode;
   }

   private ObjectNode getMethodNode( final String methodName ) {
      final ObjectNode methodNode = FACTORY.objectNode();
      methodNode.put( FIELD_TYPE, FIELD_STRING );
      methodNode.put( FIELD_DESCRIPTION, "The method name" );
      methodNode.put( "example", methodName );
      return methodNode;
   }

   private ObjectNode getResponseSchemaForOperation( final Optional<Property> property ) {
      final ArrayNode arrayNode = FACTORY.arrayNode();
      final ObjectNode objectNode = FACTORY.objectNode();
      final ObjectNode schemaNode = FACTORY.objectNode();
      final ObjectNode propertiesNode = FACTORY.objectNode();
      schemaNode.set( "allOf", arrayNode );
      arrayNode.add( FACTORY.objectNode().put( REF, COMPONENTS_SCHEMAS + FIELD_RPC ) );
      arrayNode.add( objectNode );
      objectNode.set( FIELD_PROPERTIES, propertiesNode );
      property.ifPresent( prop -> propertiesNode.set( "result", getParamsNode( List.of( prop ) ) ) );
      return schemaNode;
   }

   private ObjectNode getRequestEndpointFilter( final Aspect aspect, final ObjectNode parameterNode, final String baseUrl,
         final String apiVersion,
         final String resourcePath ) {
      final ObjectNode objectNode = FACTORY.objectNode();
      setServers( objectNode, baseUrl, apiVersion, QUERY_SERVER_PATH );
      objectNode.set( "tags", FACTORY.arrayNode().add( aspect.getName() ) );
      objectNode.put( FIELD_OPERATION_ID, FIELD_POST + "Base" + aspect.getName() );
      objectNode.set( FIELD_PARAMETERS, getRequiredParameters( parameterNode, isEmpty( resourcePath ) ) );
      objectNode.set( FIELD_REQUEST_BODY, getRequestBodyForFilter() );
      objectNode.set( FIELD_RESPONSES, getResponsesForGet( aspect ) );
      return objectNode;
   }

   private ObjectNode getRequestBodyForFilter() {
      return FACTORY.objectNode().put( REF, COMPONENTS_REQUESTS + FIELD_FILTER );
   }

   private ObjectNode getRequestEndpointsRead( final Aspect aspect, final ObjectNode parameterNode, final String resourcePath ) {
      final ObjectNode objectNode = FACTORY.objectNode();
      objectNode.set( "tags", FACTORY.arrayNode().add( aspect.getName() ) );
      objectNode.put( FIELD_OPERATION_ID, FIELD_GET + aspect.getName() );
      objectNode.set( FIELD_PARAMETERS, getRequiredParameters( parameterNode, isEmpty( resourcePath ) ) );
      objectNode.set( FIELD_RESPONSES, getResponsesForGet( aspect ) );
      return objectNode;
   }

   private ObjectNode getRequestEndpointsCreate( final Aspect aspect, final ObjectNode parameterNode, final String resourcePath ) {
      final ObjectNode objectNode = FACTORY.objectNode();
      objectNode.set( "tags", FACTORY.arrayNode().add( aspect.getName() ) );
      objectNode.put( FIELD_OPERATION_ID, FIELD_POST + aspect.getName() );
      objectNode.set( FIELD_PARAMETERS, getRequiredParameters( parameterNode, isEmpty( resourcePath ) ) );
      objectNode.set( FIELD_REQUEST_BODY, FACTORY.objectNode().put( REF, COMPONENTS_REQUESTS + aspect.getName() ) );
      objectNode.set( FIELD_RESPONSES, getResponsesForGet( aspect ) );
      return objectNode;
   }

   private ObjectNode getRequestEndpointsUpdate( final Aspect aspect, final ObjectNode parameterNode, final String resourcePath,
         final boolean isPut ) {
      final ObjectNode objectNode = FACTORY.objectNode();
      objectNode.set( "tags", FACTORY.arrayNode().add( aspect.getName() ) );
      objectNode.put( FIELD_OPERATION_ID, (isPut ? FIELD_PUT : FIELD_PATCH) + aspect.getName() );
      objectNode.set( FIELD_PARAMETERS, getRequiredParameters( parameterNode, isEmpty( resourcePath ) ) );
      objectNode.set( FIELD_REQUEST_BODY, FACTORY.objectNode().put( REF, COMPONENTS_REQUESTS + aspect.getName() ) );
      objectNode.set( FIELD_RESPONSES, getResponsesForGet( aspect ) );
      return objectNode;
   }

   private ObjectNode getResponsesForGet( final Aspect aspect ) {
      final ObjectNode responses = FACTORY.objectNode();
      responses.set( "200", getSuccessfulNode( aspect ) );
      setErrorResponses( responses );
      return responses;
   }

   private void setErrorResponses( final ObjectNode responses ) {
      final ObjectNode clientError = FACTORY.objectNode().put( REF, COMPONENTS_RESPONSES + CLIENT_ERROR );
      final ObjectNode unauthorized = FACTORY.objectNode().put( REF, COMPONENTS_RESPONSES + UNAUTHORIZED );
      final ObjectNode forbidden = FACTORY.objectNode().put( REF, COMPONENTS_RESPONSES + FORBIDDEN );
      final ObjectNode notFoundError = FACTORY.objectNode().put( REF, COMPONENTS_RESPONSES + NOT_FOUND_ERROR );
      responses.set( "401", clientError );
      responses.set( "402", unauthorized );
      responses.set( "403", forbidden );
      responses.set( "404", notFoundError );
   }

   private ObjectNode getSuccessfulNode( final Aspect aspect ) {
      return FACTORY.objectNode().put( REF, COMPONENTS_RESPONSES + aspect.getName() );
   }

   private ArrayNode getRequiredParameters( final ObjectNode parameterNode, final boolean includeTenantIdNode ) {
      final ArrayNode parameters = FACTORY.arrayNode();
      if ( includeTenantIdNode ) {
         parameters.add( getTenantIdNode() );
      }
      parameterNode.forEach( parameters::add );
      return parameters;
   }

   private ObjectNode getTenantIdNode() {
      final ObjectNode objectNode = FACTORY.objectNode();
      final ObjectNode schema = FACTORY.objectNode();
      objectNode.put( "name", "tenant-id" );
      objectNode.put( "in", "path" );
      objectNode.put( FIELD_DESCRIPTION, "The ID of the tenant owning the requested Twin." );
      objectNode.put( FIELD_REQUIRED, true );
      objectNode.set( FIELD_SCHEMA, schema );
      schema.put( FIELD_TYPE, FIELD_STRING );
      schema.put( "format", "uuid" );
      return objectNode;
   }

   private JsonNode getAspectSchemaNode( final Aspect aspect, final Locale locale, final boolean generateCommentsForSeeAttribute ) {
      final JsonSchemaGenerationConfig config = JsonSchemaGenerationConfigBuilder.builder()
            .locale( locale )
            .generateCommentForSeeAttributes( generateCommentsForSeeAttribute )
            .useExtendedTypes( false )
            .generateForOpenApi( true )
            .build();
      return AspectModelJsonSchemaGenerator.INSTANCE.apply( aspect, config ).getContent();
   }

   private void setAspectSchemaNode( final ObjectNode schemas, final Aspect aspect, final Locale locale,
         final boolean generateCommentsForSeeAttribute ) {
      final ObjectNode aspectSchema = (ObjectNode) getAspectSchemaNode( aspect, locale, generateCommentsForSeeAttribute );
      splitSchemaRoot( aspectSchema, aspect, schemas );
   }

   private void splitSchemaRoot( final ObjectNode aspectSchema, final Aspect aspect, final ObjectNode rootSchemaNode ) {
      if ( aspectSchema.has( FIELD_PROPERTIES ) ) {
         handleRecursiveSchemas( aspectSchema, rootSchemaNode );
      }
      rootSchemaNode.set( aspect.getName(), aspectSchema );
   }

   private void handleRecursiveSchemas( final ObjectNode aspectSchema, final ObjectNode rootSchemaNode ) {
      if ( aspectSchema.has( FIELD_COMPONENTS ) ) {
         final JsonNode definitionsNode = aspectSchema.get( FIELD_COMPONENTS ).get( FIELD_SCHEMAS );
         aspectSchema.remove( FIELD_COMPONENTS );
         for ( final Iterator<String> it = definitionsNode.fieldNames(); it.hasNext(); ) {
            final String nodeName = it.next();
            final JsonNode node = definitionsNode.get( nodeName );
            rootSchemaNode.set( nodeName, node );
         }
      }
   }
}
