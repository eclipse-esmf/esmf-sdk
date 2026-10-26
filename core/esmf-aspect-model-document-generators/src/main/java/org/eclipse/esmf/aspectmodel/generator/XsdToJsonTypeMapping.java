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

import java.util.Map;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import com.google.common.collect.ImmutableMap;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeFactory;

public class XsdToJsonTypeMapping {
   public enum JsonType {
      NUMBER, INTEGER, BOOLEAN, STRING, OBJECT;

      public JsonNode toJsonNode() {
         return switch ( this ) {
            case NUMBER -> JsonNodeFactory.instance.stringNode( "number" );
            case INTEGER -> JsonNodeFactory.instance.stringNode( "integer" );
            case BOOLEAN -> JsonNodeFactory.instance.stringNode( "boolean" );
            case OBJECT -> JsonNodeFactory.instance.stringNode( "object" );
            default -> JsonNodeFactory.instance.stringNode( "string" );
         };
      }
   }

   /**
    * Maps Aspect types to JSON Schema types, with no explicit mapping defaulting to string
    */
   public static final Map<Resource, JsonType> TYPE_MAP = ImmutableMap.<Resource, JsonType>builder()
         .put( XSD.xboolean, JsonType.BOOLEAN )
         .put( XSD.decimal, JsonType.NUMBER )
         .put( XSD.integer, JsonType.INTEGER )
         .put( XSD.xfloat, JsonType.NUMBER )
         .put( XSD.xdouble, JsonType.NUMBER )
         .put( XSD.xbyte, JsonType.INTEGER )
         .put( XSD.xshort, JsonType.INTEGER )
         .put( XSD.xint, JsonType.INTEGER )
         .put( XSD.xlong, JsonType.INTEGER )
         .put( XSD.unsignedByte, JsonType.INTEGER )
         .put( XSD.unsignedShort, JsonType.INTEGER )
         .put( XSD.unsignedInt, JsonType.INTEGER )
         .put( XSD.unsignedLong, JsonType.INTEGER )
         .put( XSD.positiveInteger, JsonType.INTEGER )
         .put( XSD.nonPositiveInteger, JsonType.INTEGER )
         .put( XSD.negativeInteger, JsonType.INTEGER )
         .put( XSD.nonNegativeInteger, JsonType.INTEGER )
         .put( RDF.langString, JsonType.OBJECT )
         .build();
}
