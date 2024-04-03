package org.eclipse.esmf.aspectmodel.generator;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;

import io.vavr.control.Try;

public class GeneratorHelper {

   private GeneratorHelper() {}

   public static Map<Path, JsonNode> getSeparateSchemas( final JsonNode content, final String fileExtension, final String aspectName, final String mainSpecName ) {
      final ObjectMapper objectMapper = new ObjectMapper();
      // Create a copy of the content, because we change the root node in-place
      final JsonNode json = Try.of( () ->
                  objectMapper.readTree( objectMapper.writer().writeValueAsString( content ) ) )
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
      builder.put( Path.of( String.format( "%s.%s.%s", aspectName, mainSpecName, fileExtension ) ), updatedRoot );

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
   private static JsonNode updateRefValues( final JsonNode node, final Map<String, String> oldToNew ) {
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
}
