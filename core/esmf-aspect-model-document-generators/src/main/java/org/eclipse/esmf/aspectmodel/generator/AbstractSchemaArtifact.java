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

package org.eclipse.esmf.aspectmodel.generator;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.util.StringQuotingChecker;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The result of a schema generation where the schema is represented as a JSON document
 */
public abstract class AbstractSchemaArtifact<T extends JsonNode> implements Artifact<String, T> {
   private static final Logger LOG = LoggerFactory.getLogger( AbstractSchemaArtifact.class );

   /**
    * Returns the OpenAPI schema with separate files for schemas. In the resulting map, the key is the path
    * that names a schema and the value is the corresponding JSON structure. The root schema will be called
    * like the originating Aspect with a ".oai.json" suffix.
    *
    * @return the OpenAPI schema definition as separate files
    */
   public abstract Map<Path, JsonNode> getContentWithSeparateSchemasAsJson();

   /**
    * Returns the OpenAPI schema with separate files for schemas. In the resulting map, the key is the path
    * that names a schema and the value is the corresponding YAML structure. The root schema will be called
    * like the originating Aspect with a ".oai.yaml" suffix.
    *
    * @return the OpenAPI schema definition as separate files
    */
   public abstract Map<Path, String> getContentWithSeparateSchemasAsYaml();

   protected Map<Path, JsonNode> getSeparateSchemas( final String aspectName, final String fileExtension,
         final Optional<String> mainSpec ) {
      final ObjectMapper objectMapper = new ObjectMapper();
      // Create a copy of the content, because we change the root node in-place
      final JsonNode json = Try.of( () ->
                  objectMapper.readTree( objectMapper.writer().writeValueAsString( getContent() ) ) )
            .getOrElseThrow( DocumentGenerationException::new );
      final ImmutableMap.Builder<Path, JsonNode> builder = ImmutableMap.builder();

      final JsonNode schemas = json.get( "components" ).get( "schemas" );
      final Function<String, String> newSchemaReference = schemaName -> schemaName + "." + fileExtension;

      // Add separate entry in result map for each schema
      for ( final Iterator<Map.Entry<String, JsonNode>> it = schemas.fields(); it.hasNext(); ) {
         final Map.Entry<String, JsonNode> schema = it.next();
         builder.put( Path.of( newSchemaReference.apply( schema.getKey() ) ), schema.getValue() );
      }

      // Update each $ref in root schema
      final Map<String, String> oldToNew = Streams.stream( schemas.fieldNames() ).collect(
            Collectors.toMap( schemaName -> "#/components/schemas/" + schemaName, newSchemaReference ) );
      final JsonNode updatedRoot = updateRefValues( json, oldToNew );
      builder.put( Path.of( aspectName + mainSpec.map( s -> "." + s ).orElse( "" ) + "." + fileExtension ), updatedRoot );

      // Remove schema definitions from root schema
      final ObjectNode components = (ObjectNode) json.get( "components" );
      components.remove( "schemas" );

      return builder.build();
   }

   /**
    * Recursively updates the values of $ref nodes
    *
    * @param node the node to start the substitution with
    * @param oldToNew the map that contains the mapping from old to new values
    * @return the original node with the updated values
    */
   private JsonNode updateRefValues( final JsonNode node, final Map<String, String> oldToNew ) {
      if ( node == null ) {
         return null;
      } else if ( node instanceof ArrayNode ) {
         for ( final JsonNode child : node ) {
            updateRefValues( child, oldToNew );
         }
      } else if ( node instanceof final ObjectNode objectNode ) {
         for ( final Iterator<String> it = node.fieldNames(); it.hasNext(); ) {
            final String fieldName = it.next();
            final JsonNode updatedChild = updateRefValues( node.get( fieldName ), oldToNew );
            if ( fieldName.equals( "$ref" ) && updatedChild instanceof final TextNode updatedTextNode ) {
               objectNode.replace( "$ref", updatedTextNode );
            }
         }
      } else if ( node instanceof final TextNode textNode ) {
         return JsonNodeFactory.instance.textNode(
               Optional.ofNullable( oldToNew.get( textNode.textValue() ) ).orElse( textNode.textValue() ) );
      }
      return node;
   }

   protected Map<Path, JsonNode> getContentWithSeparateSchemasAsJson( final Optional<String> mainSpec ) {
      final JsonNode jsonContent = getContent();
      final String aspectName = AspectModelUrn.fromUrn(
            jsonContent.get( "info" ).get( AbstractGenerator.SAMM_EXTENSION ).asText() ).getName();
      return getSeparateSchemas( aspectName, "json", mainSpec );
   }

   protected Map<Path, String> getContentWithSeparateSchemasAsYaml( final Optional<String> mainSpec ) {
      final JsonNode jsonContent = getContent();
      final String aspectName = AspectModelUrn.fromUrn(
            jsonContent.get( "info" ).get( AbstractGenerator.SAMM_EXTENSION ).asText() ).getName();
      return getSeparateSchemas( aspectName, "yaml", mainSpec ).entrySet().stream().collect( Collectors.toMap(
            Map.Entry::getKey, entry -> jsonToYaml( entry.getValue() ) ) );
   }

   protected String jsonToYaml( final JsonNode json ) {
      try {
         final YAMLFactory yamlFactory = YAMLFactory.builder()
               .stringQuotingChecker( new OpenApiStringQuotingChecker() ).build();
         return new YAMLMapper( yamlFactory ).enable( YAMLGenerator.Feature.MINIMIZE_QUOTES )
               .writeValueAsString( json );
      } catch ( final JsonProcessingException exception ) {
         LOG.error( "JSON could not be converted to YAML", exception );
         return json.toString();
      }
   }

   private static class OpenApiStringQuotingChecker extends StringQuotingChecker.Default {

      @Override
      protected boolean valueHasQuotableChar( final String inputStr ) {
         if ( inputStr.contains( "#" ) ) {
            return true;
         }
         return super.valueHasQuotableChar( inputStr );
      }
   }
}
