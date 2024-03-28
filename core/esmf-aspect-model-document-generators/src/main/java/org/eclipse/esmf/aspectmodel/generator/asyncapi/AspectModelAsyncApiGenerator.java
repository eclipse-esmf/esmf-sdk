package org.eclipse.esmf.aspectmodel.generator.asyncapi;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AspectModelAsyncApiGenerator {

   private static final String APPLICATION_JSON = "application/json";
   private static final String OPERATIONS = "#/operations/";

   private static final String CHANNELS = "#/channels";
   private static final String COMPONENTS_SCHEMAS = "#/components/schemas";
   private static final String COMPONENTS_MESSAGES = "#/components/messages/";

   private static final String V30 = "3.0.0";

   private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;
   private static final Logger LOG = LoggerFactory.getLogger( AspectModelAsyncApiGenerator.class );
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

   public JsonNode applyForJson( final Aspect aspect, final boolean useSemanticVersion, final String baseUrl,
         final String tenantId, final String twinId, final Locale locale ) {
      try {
         final ObjectNode rootNode = getRootJsonNode();
         final String apiVersion = getApiVersion( aspect, useSemanticVersion );

         ( (ObjectNode) rootNode.get( "info" ) ).put( "title", aspect.getPreferredName( locale ) + " MQTT API" );
         ( (ObjectNode) rootNode.get( "info" ) ).put( "version", apiVersion );
         ( (ObjectNode) rootNode.get( "info" ) ).put( "description", aspect.getDescription( locale ) );

         rootNode.set( "channels", getChannelNode( aspect, tenantId, twinId, locale ));
         if ( !aspect.getEvents().isEmpty() || !aspect.getOperations().isEmpty() ) {
            setOperations( aspect, rootNode );
            setComponents( aspect, rootNode, locale );
         }
         return rootNode;
      } catch ( final Exception e ) {
         LOG.error( "There was an exception during the read of the root or the validation.", e );
      }
      return FACTORY.objectNode();
   }

   private void setComponents( final Aspect aspect, final ObjectNode rootNode, final Locale locale ) {
      final ObjectNode componentsNode = FACTORY.objectNode();
      final ObjectNode messagesNode = FACTORY.objectNode();
      final ObjectNode schemasNode = FACTORY.objectNode();

      aspect.getEvents().forEach( event -> generateComponentsMessageAndSchemaEvent( messagesNode, schemasNode, event, locale ) );
      aspect.getOperations().forEach( operation -> {

         operation.getInput().forEach( input -> generateComponentsMessageAndSchemaOperation( messagesNode, schemasNode, input, locale ) );

         if ( operation.getOutput().isPresent() ) {
            generateComponentsMessageAndSchemaOperation( messagesNode, schemasNode, operation.getOutput().get(), locale );
         }
      } );

      componentsNode.set( "messsages", messagesNode );
      componentsNode.set( "schemas", schemasNode );

      rootNode.set( "components", componentsNode );
   }

   private void generateComponentsMessageAndSchemaEvent( final ObjectNode messagesNode, final ObjectNode schemasNode, final Event event, final Locale locale ) {
      final ObjectNode messageNode = FACTORY.objectNode();
      messageNode.put( "name", event.getName() );
      messageNode.put( "title", event.getPreferredName( locale ) );
      messageNode.put( "summary", event.getDescription( locale ) );
      messageNode.put( "content-type", APPLICATION_JSON );

      final ObjectNode payloadNode = FACTORY.objectNode();
      payloadNode.put( "$ref", String.format( "%s/%s", COMPONENTS_SCHEMAS, event.getName() ) );

      messageNode.set( "payload", payloadNode );

      messagesNode.set( event.getName(), messageNode );

      final ObjectNode schemaNode = FACTORY.objectNode();
      if ( !event.getProperties().isEmpty() ) {
         schemaNode.put( "type", "object" );

         final ObjectNode propertiesNode = FACTORY.objectNode();
         event.getProperties().forEach( property -> {
            final ObjectNode propertyNode = FACTORY.objectNode();
            propertyNode.put( "title", property.getName() );
            propertyNode.put( "type", getType( property.getDataType().get() ) );
            propertyNode.put( "description",property.getDescription( locale ) );

            propertiesNode.set( property.getName(), propertyNode );
         } );

         schemaNode.set( "properties", propertiesNode );
      }

      schemasNode.set( event.getName(), schemaNode );
   }

   private void generateComponentsMessageAndSchemaOperation( final ObjectNode messagesNode, final ObjectNode schemasNode, final Property property, final Locale locale ) {
      final ObjectNode messageNode = FACTORY.objectNode();
      messageNode.put( "name", property.getName() );
      messageNode.put( "title", property.getPreferredName( locale ) );
      messageNode.put( "summary", property.getDescription( locale ) );
      messageNode.put( "content-type", APPLICATION_JSON );

      final ObjectNode payloadNode = FACTORY.objectNode();
      payloadNode.put( "$ref", String.format( "%s/%s", COMPONENTS_SCHEMAS, property.getName() ) );

      messageNode.set( "payload", payloadNode );

      messagesNode.set( property.getName(), messageNode );

      final ObjectNode schemaNode = FACTORY.objectNode();
      schemaNode.put( "type", getType( property.getDataType().get() ) );
      schemaNode.put( "description", property.getDescription( locale ) );

      schemasNode.set( property.getName(), schemaNode );
   }

   private void setOperations( final Aspect aspect, final ObjectNode rootNode ) {
      final ObjectNode operationsNode = FACTORY.objectNode();
      final String aspectName = aspect.getName();
      aspect.getEvents().forEach( event -> generateOperation( operationsNode, aspectName, event.getName(), "receive" ) );
      aspect.getOperations().forEach( operation -> {

         operation.getInput().forEach( input -> generateOperation( operationsNode, aspectName, input.getName(), "receive" ) );

         if ( operation.getOutput().isPresent() ) {
            generateOperation( operationsNode, aspectName, operation.getOutput().get().getName(), "send" );
         }
      } );

      rootNode.set( "operations", operationsNode );
   }

   private void generateOperation( final ObjectNode operationsNode, final String aspectName, final String operationName, final String action ) {
      final ObjectNode operationNode = FACTORY.objectNode();

      operationNode.put( "action", action);

      final ObjectNode channelNode = FACTORY.objectNode();
      channelNode.put( "$ref", String.format( "%s/%s", CHANNELS, aspectName ) );

      operationNode.set( "channel",  channelNode);

      final ArrayNode messagesNode = operationNode.putArray( "messages" );
      final ObjectNode objectNode = FACTORY.objectNode();
      objectNode.put( "$ref", String.format( "%s/%s/messages/%s", CHANNELS, aspectName, operationName ) );
      messagesNode.add( objectNode );

      operationsNode.set( operationName, operationNode );
   }

   private ObjectNode getChannelNode( final Aspect aspect, final String tenatId, final String twinId, final Locale locale ) {
      final ObjectNode endpointPathsNode = FACTORY.objectNode();
      final ObjectNode pathNode = FACTORY.objectNode();

      endpointPathsNode.set( aspect.getName(), pathNode );

      setChannelNodeMeta( pathNode, aspect, tenatId, twinId );
      setNodeMessages( pathNode, aspect);

      return endpointPathsNode;
   }

   private void setChannelNodeMeta( final ObjectNode channelNode, final Aspect aspect, final String tenatId, final String twinId ) {
      final AspectModelUrn aspectModelUrn = aspect.getAspectModelUrn().get();

      String channelAddress = String.format( "/%s/%s/%s",
            aspectModelUrn.getNamespace(),
            aspectModelUrn.getVersion(),
            aspect.getName());

      if (!tenatId.isEmpty() && !twinId.isEmpty()) {
         channelAddress = String.format( "/%s/%s%s", tenatId, twinId, channelAddress );
      }

      channelNode.put( "address", channelAddress );
      channelNode.put( "description", "This channel for updating " + aspect.getName() + " Aspect." );

      final ObjectNode parametersNode = FACTORY.objectNode();
      parametersNode.put( "namespace", aspectModelUrn.getNamespace() );
      parametersNode.put( "version", aspectModelUrn.getVersion() );
      parametersNode.put( "aspect-name", aspect.getName() );

      channelNode.set( "parameters", parametersNode);
   }

   private void setNodeMessages( final ObjectNode rootNode, final Aspect aspect ) {
      final ObjectNode messagesNode = FACTORY.objectNode();
      final String componentsPath = "components";
      if ( !aspect.getEvents().isEmpty() || !aspect.getOperations().isEmpty() ) {
         aspect.getEvents().forEach( event -> generateRef( messagesNode, componentsPath, event.getName() ) );
         aspect.getOperations().forEach( operation -> {

            operation.getInput().forEach( input -> generateRef( messagesNode, componentsPath, input.getName() ) );

            if ( operation.getOutput().isPresent() ) {
               generateRef( messagesNode, componentsPath, operation.getOutput().get().getName() );
            }
         } );
      }

      rootNode.set( "massages", messagesNode );
   }

   private void generateRef( final ObjectNode parentNode, final String path, final String messageName ) {
      final ObjectNode refNode = FACTORY.objectNode();
      refNode.put( "$ref", String.format( "#/%s/massages/%s", path, messageName ) );
      parentNode.set( messageName, refNode );
   }

   private String getApiVersion( final Aspect aspect, final boolean useSemanticVersion ) {
      final String aspectVersion = aspect.getAspectModelUrn().get().getVersion();
      if ( useSemanticVersion ) {
         return String.format( "v%s", aspectVersion );
      }
      final int endIndexOfMajorVersion = aspectVersion.indexOf( '.' );
      final String majorAspectVersion = aspectVersion.substring( 0, endIndexOfMajorVersion );
      return String.format( "v%s", majorAspectVersion );
   }

   private ObjectNode getRootJsonNode() throws IOException {
      final InputStream inputStream = getClass().getResourceAsStream( "/openapi/AsyncApiRootJson.json" );
      final String string = IOUtils.toString( inputStream, StandardCharsets.UTF_8 )
            .replace( "${AsyncApiVer}", V30 );
      return (ObjectNode) OBJECT_MAPPER.readTree( string );
   }

   private String getType( final Type type ) {
      String currentTypeString = type.getUrn().split( "#" )[1];
      return currentTypeString.contains( "Entity" ) ? "object" : currentTypeString;
   }
}
