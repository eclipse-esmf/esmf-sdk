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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.generator.JsonGenerator;
import org.eclipse.esmf.aspectmodel.generator.jsonschema.AspectModelJsonSchemaGenerator;
import org.eclipse.esmf.aspectmodel.generator.jsonschema.AspectModelJsonSchemaVisitor;
import org.eclipse.esmf.aspectmodel.generator.jsonschema.JsonSchemaGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.jsonschema.JsonSchemaGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.Property;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspectModelAsyncApiGenerator extends JsonGenerator<Aspect, AsyncApiSchemaGenerationConfig, JsonNode, AsyncApiSchemaArtifact> {
   public static final AsyncApiSchemaGenerationConfig DEFAULT_CONFIG = AsyncApiSchemaGenerationConfigBuilder.builder().build();

   private static final String APPLICATION_JSON = "application/json";
   private static final String CHANNELS_REF = "#/channels";
   private static final String COMPONENTS_SCHEMAS_REF = "#/components/schemas";
   private static final String COMPONENTS_MESSAGES_REF = "#/components/messages";
   public static final String SCHEMAS = "schemas";
   public static final String COMPONENTS = "components";
   private static final String MESSAGES_FIELD = "messages";
   private static final String ACTION_RECEIVE = "receive";
   private static final String ACTION_SEND = "send";
   private static final String V30 = "3.0.0";

   private static final String TITLE_FIELD = "title";
   private static final String DESCRIPTION_FIELD = "description";

   private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;
   private static final Logger LOG = LoggerFactory.getLogger( AspectModelAsyncApiGenerator.class );

   public AspectModelAsyncApiGenerator( final Aspect aspect ) {
      this( aspect, DEFAULT_CONFIG );
   }

   public AspectModelAsyncApiGenerator( final Aspect aspect, final AsyncApiSchemaGenerationConfig config ) {
      super( aspect, config );
   }

   private Aspect aspect() {
      return structureElement();
   }

   @Override
   public Stream<AsyncApiSchemaArtifact> generate() {
      AsyncApiSchemaArtifact result;
      try {
         final ObjectNode rootNode = getRootJsonNode();
         final String apiVersion = getApiVersion( aspect(), config.useSemanticVersion() );

         if ( StringUtils.isNotBlank( config.applicationId() ) ) {
            rootNode.put( "id", config.applicationId() );
         }

         final ObjectNode info = (ObjectNode) rootNode.get( "info" );
         info.put( TITLE_FIELD, aspect().getPreferredName( config.locale() ) + " MQTT API" );
         info.put( "version", apiVersion );
         info.put( AspectModelJsonSchemaGenerator.SAMM_EXTENSION, aspect().urn().toString() );
         Optional.ofNullable( aspect().getDescription( config.locale() ) )
               .ifPresent( description -> info.put( DESCRIPTION_FIELD, description ) );

         rootNode.set( "channels", buildChannelNode( aspect() ) );

         if ( !aspect().getEvents().isEmpty() || !aspect().getOperations().isEmpty() ) {
            setOperations( aspect(), rootNode );
            setComponents( aspect(), rootNode );
         }
         result = new AsyncApiSchemaArtifact( aspect().getName(), rootNode );
      } catch ( final Exception exception ) {
         LOG.error( "There was an exception during the read of the root or the validation.", exception );
         result = new AsyncApiSchemaArtifact( aspect().getName(), FACTORY.objectNode() );
      }
      return Stream.of( result );
   }

   private void setOperations( final Aspect aspect, final ObjectNode rootNode ) {
      final ObjectNode operationsNode = FACTORY.objectNode();

      aspect.getEvents().forEach( event ->
            addOperation( operationsNode, aspect.getName(), event.getName(), ACTION_RECEIVE ) );

      aspect.getOperations().forEach( operation -> {
         operation.getInput().forEach( input ->
               addOperation( operationsNode, aspect.getName(), input.getName(), ACTION_RECEIVE ) );
         operation.getOutput().ifPresent( output ->
               addOperation( operationsNode, aspect.getName(), output.getName(), ACTION_SEND ) );
      } );

      rootNode.set( "operations", operationsNode );
   }

   private void addOperation( final ObjectNode operationsNode, final String aspectName,
         final String operationName, final String action ) {
      final ObjectNode operationNode = FACTORY.objectNode();
      operationNode.put( "action", action );
      operationNode.set( "channel", FACTORY.objectNode().put( "$ref", ref( CHANNELS_REF, aspectName ) ) );

      final ArrayNode messagesArray = operationNode.putArray( MESSAGES_FIELD );
      messagesArray.add( FACTORY.objectNode().put( "$ref",
            String.format( "%s/%s/messages/%s", CHANNELS_REF, aspectName, operationName ) ) );

      operationsNode.set( operationName, operationNode );
   }

   private ObjectNode buildChannelNode( final Aspect aspect ) {
      final ObjectNode channelsNode = FACTORY.objectNode();
      final ObjectNode pathNode = FACTORY.objectNode();

      final AspectModelUrn urn = aspect.urn();
      pathNode.put( "address", config.channelAddress() != null && !config.channelAddress().isBlank()
            ? config.channelAddress()
            : String.format( "/%s/%s/%s", urn.getNamespaceMainPart(), urn.getVersion(), aspect.getName() ) );
      pathNode.put( DESCRIPTION_FIELD, "Channel for updating " + aspect.getName() + " Aspect." );

      final ObjectNode parametersNode = FACTORY.objectNode();
      parametersNode.put( "namespace", urn.getNamespaceMainPart() );
      parametersNode.put( "version", urn.getVersion() );
      parametersNode.put( "aspect-name", aspect.getName() );
      pathNode.set( "parameters", parametersNode );

      pathNode.set( MESSAGES_FIELD, buildChannelMessages( aspect ) );

      channelsNode.set( aspect.getName(), pathNode );
      return channelsNode;
   }

   private ObjectNode buildChannelMessages( final Aspect aspect ) {
      final ObjectNode messagesNode = FACTORY.objectNode();
      aspect.getEvents().forEach( event ->
            messagesNode.set( event.getName(),
                  FACTORY.objectNode().put( "$ref", ref( COMPONENTS_MESSAGES_REF, event.getName() ) ) ) );
      aspect.getOperations().forEach( operation -> {
         operation.getInput().forEach( input ->
               messagesNode.set( input.getName(),
                     FACTORY.objectNode().put( "$ref", ref( COMPONENTS_MESSAGES_REF, input.getName() ) ) ) );
         operation.getOutput().ifPresent( output ->
               messagesNode.set( output.getName(),
                     FACTORY.objectNode().put( "$ref", ref( COMPONENTS_MESSAGES_REF, output.getName() ) ) ) );
      } );
      return messagesNode;
   }

   private JsonSchemaGenerationConfig createJsonSchemaConfig() {
      return JsonSchemaGenerationConfigBuilder.builder()
            .locale( config.locale() )
            .generateForOpenApi( true )
            .generateCommentForSeeAttributes( false )
            .useExtendedTypes( false )
            .build();
   }

   private void setComponents( final Aspect aspect, final ObjectNode rootNode ) {
      final ObjectNode componentsNode = FACTORY.objectNode();
      final ObjectNode messagesNode = FACTORY.objectNode();
      final ObjectNode schemasNode = FACTORY.objectNode();

      final AspectModelJsonSchemaVisitor schemaVisitor = new AspectModelJsonSchemaVisitor( createJsonSchemaConfig() );

      aspect.getEvents().forEach( event ->
            addEventComponent( messagesNode, schemasNode, event, schemaVisitor ) );

      aspect.getOperations().forEach( operation -> {
         operation.getInput().forEach( input ->
               addOperationPropertyComponent( messagesNode, schemasNode, input, schemaVisitor ) );
         operation.getOutput().ifPresent( output ->
               addOperationPropertyComponent( messagesNode, schemasNode, output, schemaVisitor ) );
      } );

      mergeVisitorSchemas( schemaVisitor, schemasNode );

      componentsNode.set( MESSAGES_FIELD, messagesNode );
      componentsNode.set( SCHEMAS, schemasNode );
      rootNode.set( COMPONENTS, componentsNode );
   }

   private void addEventComponent( final ObjectNode messagesNode, final ObjectNode schemasNode,
         final Event event, final AspectModelJsonSchemaVisitor schemaVisitor ) {
      final Locale locale = config.locale();

      final ObjectNode messageNode = FACTORY.objectNode();
      messageNode.put( "name", event.getName() );
      messageNode.put( TITLE_FIELD, event.getPreferredName( locale ) );
      messageNode.put( "contentType", APPLICATION_JSON );
      messageNode.set( "payload", FACTORY.objectNode().put( "$ref", ref( COMPONENTS_SCHEMAS_REF, event.getName() ) ) );
      messagesNode.set( event.getName(), messageNode );

      Optional.ofNullable( event.getDescription( locale ) )
            .ifPresent( description -> messageNode.put( "summary", description ) );

      final ObjectNode eventSchemaNode = FACTORY.objectNode();
      if ( !event.getProperties().isEmpty() ) {
         schemaVisitor.visitHasProperties( event, eventSchemaNode );
      }
      schemasNode.set( event.getName(), eventSchemaNode );
   }

   private void addOperationPropertyComponent( final ObjectNode messagesNode, final ObjectNode schemasNode,
         final Property property, final AspectModelJsonSchemaVisitor schemaVisitor ) {
      final Locale locale = config.locale();

      final ObjectNode messageNode = FACTORY.objectNode();
      messageNode.put( "name", property.getName() );
      messageNode.put( TITLE_FIELD, property.getPreferredName( locale ) );
      messageNode.put( "contentType", APPLICATION_JSON );
      messageNode.set( "payload", FACTORY.objectNode().put( "$ref", ref( COMPONENTS_SCHEMAS_REF, property.getName() ) ) );
      messagesNode.set( property.getName(), messageNode );

      Optional.ofNullable( property.getDescription( locale ) )
            .ifPresent( description -> messageNode.put( "summary", description ) );

      final JsonNode schema = property.accept( schemaVisitor, FACTORY.objectNode() );
      schemasNode.set( property.getName(), schema );
   }

   private void mergeVisitorSchemas( final AspectModelJsonSchemaVisitor schemaVisitor, final ObjectNode schemasNode ) {
      final ObjectNode visitorRoot = schemaVisitor.getRootNode();
      if ( visitorRoot.has( COMPONENTS ) && visitorRoot.get( COMPONENTS ).has( SCHEMAS ) ) {
         visitorRoot.get( COMPONENTS ).get( SCHEMAS ).properties()
               .forEach( field -> schemasNode.set( field.getKey(), field.getValue() ) );
      }
   }

   private static String ref( final String basePath, final String name ) {
      return String.format( "%s/%s", basePath, name );
   }

   private String getApiVersion( final Aspect aspect, final boolean useSemanticVersion ) {
      final String aspectVersion = aspect.urn().getVersion();
      return "v" + ( useSemanticVersion ? aspectVersion : VersionNumber.parse( aspectVersion ).getMajor() );
   }

   private ObjectNode getRootJsonNode() throws IOException {
      final InputStream inputStream = getClass().getResourceAsStream( "/asyncapi/AsyncApiRootJson.json" );
      final String string = IOUtils.toString( inputStream, StandardCharsets.UTF_8 )
            .replace( "${AsyncApiVer}", V30 );
      return (ObjectNode) objectMapper.readTree( string );
   }
}
