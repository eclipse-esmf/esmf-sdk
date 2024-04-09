package org.eclipse.esmf.aspectmodel.generator;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.common.collect.ImmutableMap;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

public class XsdToJsonTypeMapping {
   public enum JsonType {
      NUMBER,
      BOOLEAN,
      STRING,
      OBJECT;

      public JsonNode toJsonNode() {
         return switch ( this ) {
            case NUMBER -> JsonNodeFactory.instance.textNode( "number" );
            case BOOLEAN -> JsonNodeFactory.instance.textNode( "boolean" );
            case OBJECT -> JsonNodeFactory.instance.textNode( "object" );
            default -> JsonNodeFactory.instance.textNode( "string" );
         };
      }
   }

   /**
    * Maps Aspect types to JSON Schema types, with no explicit mapping defaulting to string
    */
   public static final Map<Resource, JsonType> TYPE_MAP = ImmutableMap.<Resource, JsonType> builder()
         .put( XSD.xboolean, JsonType.BOOLEAN )
         .put( XSD.decimal, JsonType.NUMBER )
         .put( XSD.integer, JsonType.NUMBER )
         .put( XSD.xfloat, JsonType.NUMBER )
         .put( XSD.xdouble, JsonType.NUMBER )
         .put( XSD.xbyte, JsonType.NUMBER )
         .put( XSD.xshort, JsonType.NUMBER )
         .put( XSD.xint, JsonType.NUMBER )
         .put( XSD.xlong, JsonType.NUMBER )
         .put( XSD.unsignedByte, JsonType.NUMBER )
         .put( XSD.unsignedShort, JsonType.NUMBER )
         .put( XSD.unsignedInt, JsonType.NUMBER )
         .put( XSD.unsignedLong, JsonType.NUMBER )
         .put( XSD.positiveInteger, JsonType.NUMBER )
         .put( XSD.nonPositiveInteger, JsonType.NUMBER )
         .put( XSD.negativeInteger, JsonType.NUMBER )
         .put( XSD.nonNegativeInteger, JsonType.NUMBER )
         .put( RDF.langString, JsonType.OBJECT )
         .build();
}
