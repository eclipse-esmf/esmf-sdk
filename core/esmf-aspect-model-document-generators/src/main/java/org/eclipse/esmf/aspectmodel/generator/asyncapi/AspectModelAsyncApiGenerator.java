package org.eclipse.esmf.aspectmodel.generator.asyncapi;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.generator.ArtifactGenerator;
import org.eclipse.esmf.aspectmodel.generator.XsdToJsonTypeMapping;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.jsonld.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AspectModelAsyncApiGenerator
      implements ArtifactGenerator<String, JsonNode, Aspect, AsyncApiSchemaGenerationConfig, AsyncApiSchemaArtifact> {

   private static final String APPLICATION_JSON = "application/json";
   private static final String CHANNELS = "#/channels";
   private static final String COMPONENTS_SCHEMAS_PATH = "#/components/schemas";
   private static final String COMPONENTS_MESSAGES = "#/components/messages";
   private static final String ACTION_RECEIVE = "receive";
   private static final String ACTION_SEND = "send";
   private static final String V30 = "3.0.0";

   private static final String TITLE_FIELD = "title";
   private static final String DESCRIPTION_FIELD = "description";
   private static final String TYPE_FIELD = "type";

   private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;
   private static final Logger LOG = LoggerFactory.getLogger( AspectModelAsyncApiGenerator.class );
   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

   @Override
   public AsyncApiSchemaArtifact apply( Aspect aspect, AsyncApiSchemaGenerationConfig config ) {
      try {
         final ObjectNode rootNode = getRootJsonNode();
         final String apiVersion = getApiVersion( aspect, config.useSemanticVersion() );

         if ( StringUtils.isNotBlank( config.applicationId() ) ) {
            rootNode.put( "id", config.applicationId() );
         }

         ( (ObjectNode) rootNode.get( "info" ) ).put( TITLE_FIELD, aspect.getPreferredName( config.locale() ) + " MQTT API" );
         ( (ObjectNode) rootNode.get( "info" ) ).put( "version", apiVersion );
         ( (ObjectNode) rootNode.get( "info" ) ).put( DESCRIPTION_FIELD, getDescription( aspect.getDescription( config.locale() ) ) );

         rootNode.set( "channels", getChannelNode( aspect, config ));
         if ( !aspect.getEvents().isEmpty() || !aspect.getOperations().isEmpty() ) {
            setOperations( aspect, rootNode );
            setComponents( aspect, rootNode, config.locale() );
         }
         return new AsyncApiSchemaArtifact( aspect.getName(), rootNode );
      } catch ( final Exception e ) {
         LOG.error( "There was an exception during the read of the root or the validation.", e );
      }
      return new AsyncApiSchemaArtifact( aspect.getName(), FACTORY.objectNode() );
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

      componentsNode.set( "messages", messagesNode );
      componentsNode.set( "schemas", schemasNode );

      rootNode.set( "components", componentsNode );
   }

   private void generateComponentsMessageAndSchemaEvent( final ObjectNode messagesNode, final ObjectNode schemasNode, final Event event, final Locale locale ) {
      final ObjectNode messageNode = FACTORY.objectNode();
      messageNode.put( "name", event.getName() );
      messageNode.put( TITLE_FIELD, event.getPreferredName( locale ) );
      messageNode.put( "summary", event.getDescription( locale ) );
      messageNode.put( "content-type", APPLICATION_JSON );

      final ObjectNode payloadNode = FACTORY.objectNode();
      payloadNode.put( "$ref", generateRef( COMPONENTS_SCHEMAS_PATH, event.getName() ) );

      messageNode.set( "payload", payloadNode );

      messagesNode.set( event.getName(), messageNode );

      final ObjectNode schemaNode = FACTORY.objectNode();
      if ( !event.getProperties().isEmpty() ) {
         schemaNode.put( TYPE_FIELD, "object" );

         final ObjectNode propertiesNode = FACTORY.objectNode();
         event.getProperties().forEach( property -> {
            final ObjectNode propertyNode = FACTORY.objectNode();
            propertyNode.put( TITLE_FIELD, property.getName() );
            propertyNode.set( TYPE_FIELD, getType( ResourceFactory.createResource( property.getDataType().get().getUrn() ) ).toJsonNode() );
            propertyNode.put( DESCRIPTION_FIELD,property.getDescription( locale ) );

            propertiesNode.set( property.getName(), propertyNode );
         } );

         schemaNode.set( "properties", propertiesNode );
      }

      schemasNode.set( event.getName(), schemaNode );
   }

   private void generateComponentsMessageAndSchemaOperation( final ObjectNode messagesNode, final ObjectNode schemasNode, final Property property, final Locale locale ) {
      final ObjectNode messageNode = FACTORY.objectNode();
      messageNode.put( "name", property.getName() );
      messageNode.put( TITLE_FIELD, property.getPreferredName( locale ) );
      messageNode.put( "summary", property.getDescription( locale ) );
      messageNode.put( "content-type", APPLICATION_JSON );

      final ObjectNode payloadNode = FACTORY.objectNode();
      payloadNode.put( "$ref", generateRef( COMPONENTS_SCHEMAS_PATH, property.getName() ) );

      messageNode.set( "payload", payloadNode );

      messagesNode.set( property.getName(), messageNode );

      final ObjectNode schemaNode = FACTORY.objectNode();
      schemaNode.set( TYPE_FIELD, getType( ResourceFactory.createResource( property.getDataType().get().getUrn() ) ).toJsonNode() );
      schemaNode.put( DESCRIPTION_FIELD, getDescription( property.getDescription( locale ) ) );

      schemasNode.set( property.getName(), schemaNode );
   }

   private String getDescription( final String description ) {
      return description == null ? "" : description;
   }

   private void setOperations( final Aspect aspect, final ObjectNode rootNode ) {
      final ObjectNode operationsNode = FACTORY.objectNode();
      final String aspectName = aspect.getName();
      aspect.getEvents().forEach( event -> generateOperation( operationsNode, aspectName, event.getName(), ACTION_RECEIVE ) );
      aspect.getOperations().forEach( operation -> {

         operation.getInput().forEach( input -> generateOperation( operationsNode, aspectName, input.getName(), ACTION_RECEIVE ) );

         if ( operation.getOutput().isPresent() ) {
            generateOperation( operationsNode, aspectName, operation.getOutput().get().getName(), ACTION_SEND );
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

   private ObjectNode getChannelNode( final Aspect aspect, final AsyncApiSchemaGenerationConfig config ) {
      final ObjectNode endpointPathsNode = FACTORY.objectNode();
      final ObjectNode pathNode = FACTORY.objectNode();

      endpointPathsNode.set( aspect.getName(), pathNode );

      setChannelNodeMeta( pathNode, aspect, config );
      setNodeMessages( pathNode, aspect);

      return endpointPathsNode;
   }

   private void setChannelNodeMeta( final ObjectNode channelNode, final Aspect aspect, final AsyncApiSchemaGenerationConfig config ) {
      final AspectModelUrn aspectModelUrn = aspect.getAspectModelUrn().get();

      channelNode.put( "address", StringUtils.isNotBlank( config.channelAddress() ) ? config.channelAddress() : String.format( "/%s/%s/%s", aspectModelUrn.getNamespace(), aspectModelUrn.getVersion(), aspect.getName() ) );
      channelNode.put( DESCRIPTION_FIELD, "This channel for updating " + aspect.getName() + " Aspect." );

      final ObjectNode parametersNode = FACTORY.objectNode();
      parametersNode.put( "namespace", aspectModelUrn.getNamespace() );
      parametersNode.put( "version", aspectModelUrn.getVersion() );
      parametersNode.put( "aspect-name", aspect.getName() );

      channelNode.set( "parameters", parametersNode);
   }

   private void setNodeMessages( final ObjectNode rootNode, final Aspect aspect ) {
      final ObjectNode messagesNode = FACTORY.objectNode();
      if ( !aspect.getEvents().isEmpty() || !aspect.getOperations().isEmpty() ) {
         aspect.getEvents().forEach( event -> generateNodeMessageRef( messagesNode, event.getName() ) );
         aspect.getOperations().forEach( operation -> {

            operation.getInput().forEach( input -> generateNodeMessageRef( messagesNode, input.getName() ) );

            if ( operation.getOutput().isPresent() ) {
               generateNodeMessageRef( messagesNode, operation.getOutput().get().getName() );
            }
         } );
      }

      rootNode.set( "massages", messagesNode );
   }

   private void generateNodeMessageRef( final ObjectNode parentNode, final String messageName ) {
      final ObjectNode refNode = FACTORY.objectNode();
      refNode.put( "$ref", generateRef( COMPONENTS_MESSAGES, messageName ) );
      parentNode.set( messageName, refNode );
   }

   private String generateRef( final String path, final String name ) {
      return String.format( "%s/%s", path, name);
   }

   private String getApiVersion( final Aspect aspect, final boolean useSemanticVersion ) {
      final String aspectVersion = aspect.getAspectModelUrn().get().getVersion();
      return "v" + ( useSemanticVersion ? aspectVersion : VersionNumber.parse( aspectVersion ).getMajor() );
   }

   private ObjectNode getRootJsonNode() throws IOException {
      final InputStream inputStream = getClass().getResourceAsStream( "/openapi/AsyncApiRootJson.json" );
      final String string = IOUtils.toString( inputStream, StandardCharsets.UTF_8 )
            .replace( "${AsyncApiVer}", V30 );
      return (ObjectNode) OBJECT_MAPPER.readTree( string );
   }

   private XsdToJsonTypeMapping.JsonType getType( final Resource type ) {
      return XsdToJsonTypeMapping.TYPE_MAP.getOrDefault( type, XsdToJsonTypeMapping.JsonType.STRING );
   }
}
