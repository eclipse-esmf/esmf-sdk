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
import org.eclipse.esmf.metamodel.Operation;
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
   private static final String CHANNEL = "channel";
   private static final String CHANNELS_REF = "#/channels";
   private static final String COMPONENTS_SCHEMAS_REF = "#/components/schemas";
   private static final String COMPONENTS_MESSAGES_REF = "#/components/messages";
   private static final String SCHEMAS = "schemas";
   private static final String COMPONENTS = "components";
   private static final String MESSAGES_FIELD = "messages";
   private static final String ACTION = "action";
   private static final String ACTION_RECEIVE = "receive";
   private static final String ACTION_SEND = "send";
   private static final String REQUEST = "Request";
   private static final String RESPONSE = "Response";
   private static final String V30 = "3.0.0";

   private static final String TITLE_FIELD = "title";
   private static final String NAME_FIELD = "name";
   private static final String SUMMARY_FIELD = "summary";
   private static final String DESCRIPTION_FIELD = "description";
   private static final String CONTENT_TYPE_FIELD = "contentType";
   private static final String PAYLOAD_FIELD = "payload";

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
            setOperationsNode( aspect(), rootNode );
            setComponentsNode( aspect(), rootNode );
         }
         result = new AsyncApiSchemaArtifact( aspect().getName(), rootNode );
      } catch ( final Exception exception ) {
         LOG.error( "There was an exception during the read of the root or the validation.", exception );
         result = new AsyncApiSchemaArtifact( aspect().getName(), FACTORY.objectNode() );
      }
      return Stream.of( result );
   }

   private ObjectNode getRootJsonNode() throws IOException {
      final InputStream inputStream = getClass().getResourceAsStream( "/asyncapi/AsyncApiRootJson.json" );
      final String string = IOUtils.toString( inputStream, StandardCharsets.UTF_8 )
            .replace( "${AsyncApiVer}", V30 );
      return (ObjectNode) objectMapper.readTree( string );
   }

   private String getApiVersion( final Aspect aspect, final boolean useSemanticVersion ) {
      final String aspectVersion = aspect.urn().getVersion();
      return "v" + ( useSemanticVersion ? aspectVersion : VersionNumber.parse( aspectVersion ).getMajor() );
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
         if ( !operation.getInput().isEmpty() ) {
            final String requestMessageName = operation.getName() + REQUEST;
            messagesNode.set( requestMessageName,
                  FACTORY.objectNode().put( "$ref", ref( COMPONENTS_MESSAGES_REF, requestMessageName ) ) );
         }

         operation.getOutput().ifPresent( output -> {
            final String responseMessageName = operation.getName() + RESPONSE;
            messagesNode.set( responseMessageName,
                  FACTORY.objectNode().put( "$ref", ref( COMPONENTS_MESSAGES_REF, responseMessageName ) ) );
         } );
      } );
      return messagesNode;
   }

   private static String ref( final String basePath, final String name ) {
      return String.format( "%s/%s", basePath, name );
   }

   private void setOperationsNode( final Aspect aspect, final ObjectNode rootNode ) {
      final ObjectNode operationsNode = rootNode.putObject( "operations" );

      aspect.getEvents().forEach( event -> addEventToOperationsNode( operationsNode, aspect.getName(), event ) );

      aspect.getOperations().forEach( operation -> addOperationToOperationsNode( operationsNode, operation, aspect.getName() ) );
   }

   private void addOperationToOperationsNode( final ObjectNode operationsNode, final Operation aspectOperation, final String aspectName ) {
      if ( !aspectOperation.getInput().isEmpty() ) {
         final String requestOperationName = aspectOperation.getName() + REQUEST;
         final ObjectNode requestNode = operationsNode.putObject( requestOperationName );

         requestNode.put( ACTION, ACTION_RECEIVE );
         requestNode.set( CHANNEL, FACTORY.objectNode().put( "$ref", ref( CHANNELS_REF, aspectName ) ) );
         final ArrayNode messagesArray = requestNode.putArray( MESSAGES_FIELD );

         messagesArray.add( FACTORY.objectNode().put( "$ref",
               String.format( "%s/%s/messages/%s", CHANNELS_REF, aspectName, requestOperationName ) ) );
      }

      if ( aspectOperation.getOutput().isPresent() ) {
         final String responseOperationName = aspectOperation.getName() + RESPONSE;
         final ObjectNode responseNode = operationsNode.putObject( responseOperationName );

         responseNode.put( ACTION, ACTION_SEND );
         responseNode.set( CHANNEL, FACTORY.objectNode().put( "$ref", ref( CHANNELS_REF, aspectName ) ) );

         final ArrayNode messagesArray = responseNode.putArray( MESSAGES_FIELD );
         messagesArray.add( FACTORY.objectNode().put( "$ref",
               String.format( "%s/%s/messages/%s", CHANNELS_REF, aspectName, responseOperationName ) ) );
      }
   }

   private void addEventToOperationsNode( final ObjectNode operationsNode, final String aspectName, final Event event ) {
      final ObjectNode eventNode = operationsNode.putObject( event.getName() );
      eventNode.put( ACTION, ACTION_SEND );
      eventNode.set( CHANNEL, FACTORY.objectNode().put( "$ref", ref( CHANNELS_REF, aspectName ) ) );

      Optional.ofNullable( event.getDescription( config.locale() ) )
            .ifPresent( description -> eventNode.put( SUMMARY_FIELD, description ) );

      final ArrayNode messagesArray = eventNode.putArray( MESSAGES_FIELD );
      messagesArray.add( FACTORY.objectNode().put( "$ref",
            String.format( "%s/%s/messages/%s", CHANNELS_REF, aspectName, event.getName() ) ) );
   }

   private void setComponentsNode( final Aspect aspect, final ObjectNode rootNode ) {
      final ObjectNode componentsNode = rootNode.putObject( COMPONENTS );
      final ObjectNode messagesNode = componentsNode.putObject( MESSAGES_FIELD );
      final ObjectNode schemasNode = componentsNode.putObject( SCHEMAS );

      final AspectModelJsonSchemaVisitor schemaVisitor = new AspectModelJsonSchemaVisitor( createJsonSchemaConfig() );

      aspect.getEvents().forEach( event ->
            addEventMessage( messagesNode, schemasNode, event, schemaVisitor ) );

      aspect.getOperations().forEach( operation -> {
         if ( !operation.getInput().isEmpty() ) {
            addOperationRequestComponent( messagesNode, schemasNode, operation, schemaVisitor );
         }

         if ( operation.getOutput().isPresent() ) {
            addOperationResponseComponent( messagesNode, schemasNode, operation, schemaVisitor );
         }
      } );

      mergeVisitorSchemas( schemaVisitor, schemasNode );
   }

   private JsonSchemaGenerationConfig createJsonSchemaConfig() {
      return JsonSchemaGenerationConfigBuilder.builder()
            .locale( config.locale() )
            .generateForOpenApi( true )
            .generateCommentForSeeAttributes( false )
            .useExtendedTypes( false )
            .build();
   }

   private void mergeVisitorSchemas( final AspectModelJsonSchemaVisitor schemaVisitor, final ObjectNode schemasNode ) {
      final ObjectNode visitorRoot = schemaVisitor.getRootNode();
      if ( visitorRoot.has( COMPONENTS ) && visitorRoot.get( COMPONENTS ).has( SCHEMAS ) ) {
         visitorRoot.get( COMPONENTS ).get( SCHEMAS ).properties()
               .forEach( field -> schemasNode.set( field.getKey(), field.getValue() ) );
      }
   }

   private void addEventMessage( final ObjectNode messagesNode, final ObjectNode schemasNode,
         final Event event, final AspectModelJsonSchemaVisitor schemaVisitor ) {
      final Locale locale = config.locale();

      final ObjectNode messageNode = FACTORY.objectNode();
      messageNode.put( NAME_FIELD, event.getName() );
      messageNode.put( TITLE_FIELD, event.getPreferredName( locale ) );
      messageNode.put( CONTENT_TYPE_FIELD, APPLICATION_JSON );
      messageNode.set( PAYLOAD_FIELD, FACTORY.objectNode().put( "$ref", ref( COMPONENTS_SCHEMAS_REF, event.getName() ) ) );
      messagesNode.set( event.getName(), messageNode );

      Optional.ofNullable( event.getDescription( locale ) )
            .ifPresent( description -> messageNode.put( SUMMARY_FIELD, description ) );

      final ObjectNode eventSchemaNode = FACTORY.objectNode();
      if ( !event.getProperties().isEmpty() ) {
         schemaVisitor.visitHasProperties( event, eventSchemaNode );
      }
      schemasNode.set( event.getName(), eventSchemaNode );
   }

   private void addOperationRequestComponent( final ObjectNode messagesNode, final ObjectNode schemasNode,
         final Operation operation, final AspectModelJsonSchemaVisitor schemaVisitor ) {
      final Locale locale = config.locale();

      final String requestComponentName = operation.getName() + REQUEST;

      final ObjectNode messageNode = FACTORY.objectNode();
      messageNode.put( NAME_FIELD, requestComponentName );
      messageNode.put( TITLE_FIELD, operation.getPreferredName( locale ) );
      messageNode.put( CONTENT_TYPE_FIELD, APPLICATION_JSON );
      messageNode.set( PAYLOAD_FIELD, FACTORY.objectNode().put( "$ref", ref( COMPONENTS_SCHEMAS_REF, requestComponentName ) ) );
      messagesNode.set( requestComponentName, messageNode );

      Optional.ofNullable( operation.getDescription( locale ) )
            .ifPresent( description -> messageNode.put( SUMMARY_FIELD, description ) );

      final ObjectNode requestComponentSchema = schemasNode.putObject( requestComponentName );
      requestComponentSchema.put( TITLE_FIELD, operation.getPreferredName( locale ) );
      final ArrayNode messageArrayNode = requestComponentSchema.putArray( "allOf" );
      for ( Property input : operation.getInput() ) {
         messageArrayNode.add( input.accept( schemaVisitor, null ) );
      }
   }

   private void addOperationResponseComponent( final ObjectNode messagesNode, final ObjectNode schemasNode,
         final Operation operation, final AspectModelJsonSchemaVisitor schemaVisitor ) {
      final Locale locale = config.locale();

      final String responseComponentName = operation.getName() + RESPONSE;

      final ObjectNode messageNode = FACTORY.objectNode();
      messageNode.put( NAME_FIELD, responseComponentName );
      messageNode.put( TITLE_FIELD, operation.getPreferredName( locale ) );
      messageNode.put( CONTENT_TYPE_FIELD, APPLICATION_JSON );
      messageNode.set( PAYLOAD_FIELD, FACTORY.objectNode().put( "$ref", ref( COMPONENTS_SCHEMAS_REF, responseComponentName ) ) );
      messagesNode.set( responseComponentName, messageNode );

      Optional.ofNullable( operation.getDescription( locale ) )
            .ifPresent( description -> messageNode.put( SUMMARY_FIELD, description ) );

      operation.getOutput().ifPresent( output -> {
         final JsonNode schema = output.accept( schemaVisitor, null );
         schemasNode.set( responseComponentName, schema );
      } );
   }
}
