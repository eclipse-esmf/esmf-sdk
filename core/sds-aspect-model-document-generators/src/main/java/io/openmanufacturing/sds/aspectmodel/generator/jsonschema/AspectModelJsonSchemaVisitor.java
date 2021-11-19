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

package io.openmanufacturing.sds.aspectmodel.generator.jsonschema;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableMap;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.generator.DocumentGenerationException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.BammDataType;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.metamodel.AbstractEntity;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Base;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Collection;
import io.openmanufacturing.sds.metamodel.ComplexType;
import io.openmanufacturing.sds.metamodel.Constraint;
import io.openmanufacturing.sds.metamodel.Either;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Enumeration;
import io.openmanufacturing.sds.metamodel.HasProperties;
import io.openmanufacturing.sds.metamodel.LengthConstraint;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.RangeConstraint;
import io.openmanufacturing.sds.metamodel.RegularExpressionConstraint;
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.Set;
import io.openmanufacturing.sds.metamodel.SingleEntity;
import io.openmanufacturing.sds.metamodel.SortedSet;
import io.openmanufacturing.sds.metamodel.Trait;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.impl.BoundDefinition;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import io.vavr.control.Try;

public class AspectModelJsonSchemaVisitor implements AspectVisitor<JsonNode, ObjectNode> {

   private static final JsonNodeFactory factory = JsonNodeFactory.instance;
   private final List<Property> processedProperties = new LinkedList<>();

   /**
    * Defines JSON Schema restrictions for Aspect/XSD types, e.g. formats and patterns
    * according to OpenAPI Schema definition.
    *
    * @see AspectModelJsonSchemaVisitor#getSchemaTypeForAspectType(Resource)
    */
   public static final Map<Resource, Map<String, JsonNode>> openApiTypeData =
         ImmutableMap.<Resource, Map<String, JsonNode>> builder()
               .put( XSD.date, Map.of( "format", factory.textNode( "date" ) ) )
               .put( XSD.time, Map.of( "format", factory.textNode( "time" ) ) )
               // Time offset (time zone) is optional in XSD dateTime.
               // Source: https://www.w3.org/TR/xmlschema11-2/#dateTime
               .put( XSD.dateTime, Map.of( "pattern", factory.textNode(
                     "-?([1-9][0-9]{3,}|0[0-9]{3})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])"
                           + "T(([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](\\.[0-9]+)?|(24:00:00(\\.0+)?))"
                           + "(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?" ) ) )
               // JSON Schema format "date-time" corresponds to RFC 3339 production "date-time", which
               // includes mandatory time offset (time zone)
               .put( XSD.dateTimeStamp, Map.of( "format", factory.textNode( "date-time" ) ) )
               // Source: https://www.w3.org/TR/xmlschema11-2/#gYear
               .put( XSD.gYear, Map.of( "pattern", factory.textNode( "-?([1-9][0-9]{3,}|0[0-9]{3})(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?" ) ) )
               // Source: https://www.w3.org/TR/xmlschema11-2/#gMonth
               .put( XSD.gMonth, Map.of( "pattern", factory.textNode( "--(0[1-9]|1[0-2])(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?" ) ) )
               // Source https://www.w3.org/TR/xmlschema11-2/#gDay
               .put( XSD.gDay, Map.of( "pattern", factory.textNode( "---(0[1-9]|[12][0-9]|3[01])(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?" ) ) )
               // Source: https://www.w3.org/TR/xmlschema11-2/#gYearMonth
               .put( XSD.gYearMonth, Map.of( "pattern", factory.textNode(
                     "-?([1-9][0-9]{3,}|0[0-9]{3})-(0[1-9]|1[0-2])(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?" ) ) )
               .put( XSD.duration, Map.of( "format", factory.textNode( "duration" ) ) )
               // Source: https://www.w3.org/TR/xmlschema11-2/#yearMonthDuration
               .put( XSD.yearMonthDuration, Map.of( "pattern", factory.textNode( "-?P[0-9]+(Y([0-9]+M)?|M)" ) ) )
               // Source: https://www.w3.org/TR/xmlschema11-2/#dayTimeDuration
               .put( XSD.dayTimeDuration, Map.of( "pattern", factory.textNode( "[^YM]*[DT].*" ) ) )
               .put( XSD.xbyte, Map.of( "minimum", factory.numberNode( Byte.MIN_VALUE ), "maximum", factory.numberNode( Byte.MAX_VALUE ) ) )
               .put( XSD.xshort, Map.of( "minimum", factory.numberNode( Short.MIN_VALUE ), "maximum", factory.numberNode( Short.MAX_VALUE ) ) )
               .put( XSD.xint, Map.of( "minimum", factory.numberNode( Integer.MIN_VALUE ), "maximum", factory.numberNode( Integer.MAX_VALUE ) ) )
               .put( XSD.xlong, Map.of( "minimum", factory.numberNode( Long.MIN_VALUE ), "maximum", factory.numberNode( Long.MAX_VALUE ) ) )
               .put( XSD.unsignedByte, Map.of( "minimum", factory.numberNode( 0 ), "maximum", factory.numberNode( 255 ) ) )
               .put( XSD.unsignedShort, Map.of( "minimum", factory.numberNode( 0 ), "maximum", factory.numberNode( 65535 ) ) )
               .put( XSD.unsignedInt, Map.of( "minimum", factory.numberNode( 0 ), "maximum", factory.numberNode( 4294967295L ) ) )
               .put( XSD.unsignedLong, Map.of( "minimum", factory.numberNode( 0 ), "maximum", factory.numberNode( new BigDecimal( "18446744073709551615" ) ) ) )
               .put( XSD.positiveInteger, Map.of( "minimum", factory.numberNode( 1 ) ) )
               .put( XSD.nonNegativeInteger, Map.of( "minimum", factory.numberNode( 0 ) ) )
               .put( XSD.negativeInteger, Map.of( "maximum", factory.numberNode( -1 ) ) )
               .put( XSD.nonPositiveInteger, Map.of( "maximum", factory.numberNode( 0 ) ) )
               .put( XSD.hexBinary, Map.of( "pattern", factory.textNode( "([0-9a-fA-F])([0-9a-fA-F])*" ) ) )
               .put( XSD.anyURI, Map.of( "format", factory.textNode( "uri" ) ) )
               .build();

   /**
    * Defines JSON Schema restrictions for Aspect/XSD types, e.g. formats and patterns
    *
    * @see AspectModelJsonSchemaVisitor#getSchemaTypeForAspectType(Resource)
    */
   private final Map<Resource, Map<String, JsonNode>> extendedTypeData = ImmutableMap
         .<Resource, Map<String, JsonNode>> builder()
         .putAll( openApiTypeData )
         .put( RDF.langString,
               Map.of( "patternProperties", factory.objectNode().set( "^.*$", factory.objectNode().set( "type", JsonType.STRING.toJsonNode() ) ) ) )
         .put( XSD.base64Binary, Map.of( "contentEncoding", factory.textNode( "base64" ) ) )
         .build();

   private final Map<Resource, Map<String, JsonNode>> typeData;

   static final String JSON_SCHEMA_VERSION = "http://json-schema.org/draft-04/schema";

   private enum JsonType {
      NUMBER,
      BOOLEAN,
      STRING,
      OBJECT;

      private JsonNode toJsonNode() {
         switch ( this ) {
         case NUMBER:
            return JsonNodeFactory.instance.textNode( "number" );
         case BOOLEAN:
            return JsonNodeFactory.instance.textNode( "boolean" );
         case OBJECT:
            return JsonNodeFactory.instance.textNode( "object" );
         default:
            return JsonNodeFactory.instance.textNode( "string" );
         }
      }
   }

   /**
    * Maps Aspect types to JSON Schema types, with no explicit mapping defaulting to string
    *
    * @see AspectModelJsonSchemaVisitor#getSchemaTypeForAspectType(Resource)
    */
   private static final Map<Resource, JsonType> TYPE_MAP = ImmutableMap.<Resource, JsonType> builder()
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

   private final ObjectNode rootNode = factory.objectNode();
   private final Map<Base, JsonNode> hasVisited = new HashMap<>();

   public AspectModelJsonSchemaVisitor( final boolean useExtendedTypes ) {
      if ( useExtendedTypes ) {
         typeData = extendedTypeData;
      } else {
         typeData = openApiTypeData;
      }
   }

   public ObjectNode getRootNode() {
      return rootNode;
   }

   @Override
   public JsonNode visitBase( final Base base, final ObjectNode context ) {
      return factory.objectNode();
   }

   public JsonNode visitAspectForOpenApi( final Aspect aspect ) {
      return visitHasProperties( aspect, rootNode );
   }

   @Override
   public JsonNode visitAspect( final Aspect aspect, final ObjectNode context ) {
      rootNode.put( "$schema", JSON_SCHEMA_VERSION );
      return visitHasProperties( aspect, rootNode );
   }

   @Override
   public JsonNode visitHasProperties( final HasProperties element, final ObjectNode context ) {
      context.put( "type", "object" );
      final ObjectNode properties =
            Stream.ofAll( element.getProperties() )
                  .filter( property -> !property.isNotInPayload() )
                  .foldLeft( factory.objectNode(), ( propertyContext, property ) -> {
                     final JsonNode jsonNode = visitProperty( property, propertyContext );
                     return propertyContext.set( property.getPayloadName(), jsonNode );
                  } );
      context.set( "properties", properties );
      final List<TextNode> requiredProperties =
            Stream.ofAll( element.getProperties() )
                  .filter( property -> !property.isNotInPayload() )
                  .filter( property -> !property.isOptional() ).toList()
                  .map( property -> factory.textNode( property.getPayloadName() ) )
                  .toJavaList();
      if ( !requiredProperties.isEmpty() ) {
         final ArrayNode required = factory.arrayNode();
         required.addAll( requiredProperties );
         context.set( "required", required );
      }
      return context;
   }

   @Override
   @SuppressWarnings( "squid:S2250" )
   //Amount of elements in list is in regard to amount of properties in aspect model. Even in bigger aspects this should not lead to performance issues
   public JsonNode visitProperty( final Property property, final ObjectNode context ) {
      final Characteristic characteristic = property.getCharacteristic();
      String referenceNodeName;
      if ( characteristic instanceof SingleEntity ) {
         referenceNodeName = characteristic.getDataType().get().getUrn().replace( "#", "_" ).replace( ":", "_" );
      } else {
         referenceNodeName = characteristic.getAspectModelUrn()
               .map( aspectModelUrn -> aspectModelUrn.toString().replace( "#", "_" ) )
               .map( aspectModelUrn -> aspectModelUrn.replace( ":", "_" ) )
               .orElseGet( characteristic::getName );
      }

      if ( processedProperties.contains( property ) ) {
         return factory.objectNode().put( "$ref", "#/components/schemas/" + referenceNodeName );
      }
      processedProperties.add( property );
      final JsonNode characteristicSchema = characteristic.accept( this, context );
      setNodeInRootSchema( characteristicSchema, referenceNodeName );
      return factory.objectNode().put( "$ref", "#/components/schemas/" + referenceNodeName );
   }

   private AspectModelJsonSchemaVisitor.JsonType getSchemaTypeForAspectType( final Resource type ) {
      return TYPE_MAP.getOrDefault( type, AspectModelJsonSchemaVisitor.JsonType.STRING );
   }

   private Map<String, JsonNode> getAdditionalFieldsForType( final Resource type,
         final KnownVersion metaModelVersion ) {
      final BAMM bamm = new BAMM( metaModelVersion );
      final Map<Resource, Map<String, JsonNode>> typeDates = ImmutableMap.<Resource, Map<String, JsonNode>> builder()
            .putAll( typeData )
            .put( bamm.curie(), Map.of( "pattern", factory.textNode( BammDataType.CURIE_REGEX ) ) )
            .build();
      return typeDates.getOrDefault( type, Map.of() );
   }

   @Override
   public JsonNode visitSet( final Set set, final ObjectNode context ) {
      final ObjectNode collectionNode = (ObjectNode) visitCollection( set, context );
      return collectionNode.put( "uniqueItems", true );
   }

   @Override
   public JsonNode visitSortedSet( final SortedSet sortedSet, final ObjectNode context ) {
      final ObjectNode collectionNode = (ObjectNode) visitCollection( sortedSet, context );
      return collectionNode.put( "uniqueItems", true );
   }

   @Override
   public JsonNode visitCollection( final Collection collection, final ObjectNode context ) {
      final ObjectNode collectionNode = factory.objectNode();
      collectionNode.put( "type", "array" );
      final Optional<Characteristic> characteristic = collection.getElementCharacteristic();
      if ( characteristic.isPresent() ) {
         collectionNode.set( "items", characteristic.get().accept( this, collectionNode ) );
         return collectionNode;
      }

      collection.getDataType().ifPresent( type -> {
         final JsonNode typeNode = type.accept( this, collectionNode );
         if ( type.isComplex() ) {
            final String referenceNodeName = type.getUrn().replace( "#", "_" ).replace( ":", "_" );
            final ObjectNode referenceNode = factory.objectNode().put( "$ref", "#/components/schemas/" + referenceNodeName );
            setNodeInRootSchema( typeNode, referenceNodeName );
            collectionNode.set( "items", referenceNode );
            return;
         }
         collectionNode.set( "items", typeNode );
      } );
      return collectionNode;
   }

   @Override
   public JsonNode visitTrait( final Trait trait, final ObjectNode context ) {
      final ObjectNode propertyNode = (ObjectNode) trait.getBaseCharacteristic().accept( this, context );
      return Stream.ofAll( trait.getConstraints() ).foldLeft( propertyNode, ( node, constraint ) -> ((ObjectNode) (constraint.accept( this, node ))) );
   }

   @Override
   public JsonNode visitConstraint( final Constraint constraint, final ObjectNode context ) {
      return context;
   }

   @Override
   public JsonNode visitLengthConstraint( final LengthConstraint lengthConstraint, final ObjectNode context ) {
      final String itemsOrLength = "array".equals( context.get( "type" ).asText() ) ? "Items" : "Length";
      lengthConstraint.getMaxValue().ifPresent( maxValue -> context.put( "max" + itemsOrLength, maxValue ) );
      lengthConstraint.getMinValue().ifPresent( minValue -> context.put( "min" + itemsOrLength, minValue ) );
      return context;
   }

   private Try<JsonNode> getNumberNode( final Object value ) {
      try {
         return Try.success( factory.numberNode( new BigDecimal( value.toString() ) ) );
      } catch ( final NumberFormatException exception ) {
         return Try.failure( exception );
      }
   }

   @Override
   public JsonNode visitRegularExpressionConstraint( final RegularExpressionConstraint regularExpressionConstraint,
         final ObjectNode context ) {
      context.set( "pattern", factory.textNode( regularExpressionConstraint.getValue() ) );
      return context;
   }

   @Override
   public JsonNode visitRangeConstraint( final RangeConstraint rangeConstraint, final ObjectNode context ) {
      rangeConstraint.getMaxValue().ifPresent( maxValue -> getNumberNode( maxValue ).forEach( value -> {
               context.set( "maximum", value );
               context.put( "exclusiveMaximum", rangeConstraint.getUpperBoundDefinition().equals( BoundDefinition.LESS_THAN ) );
            }
      ) );

      rangeConstraint.getMinValue().ifPresent( minValue ->
            getNumberNode( minValue ).forEach( value -> {
               context.set( "minimum", value );
               context.put( "exclusiveMinimum", rangeConstraint.getLowerBoundDefinition().equals( BoundDefinition.GREATER_THAN ) );
            } ) );

      return context;
   }

   public JsonNode visitScalar( final Scalar scalar, final ObjectNode context ) {
      final ObjectNode propertyNode = factory.objectNode();
      final AspectModelJsonSchemaVisitor.JsonType value = getSchemaTypeForAspectType( ResourceFactory.createResource( scalar.getUrn() ) );
      propertyNode.set( "type", value.toJsonNode() );
      getAdditionalFieldsForType( ResourceFactory.createResource( scalar.getUrn() ), scalar.getMetaModelVersion() ).forEach( propertyNode::set );
      return propertyNode;
   }

   @Override
   public JsonNode visitEither( final Either either, final ObjectNode context ) {
      final ObjectNode properties = factory.objectNode();
      properties.set( "left", either.getLeft().accept( this, properties ) );
      properties.set( "right", either.getRight().accept( this, properties ) );

      return factory.objectNode()
            .put( "additionalProperties", false )
            .<ObjectNode> set( "properties", properties )
            .set( "oneOf",
                  factory.arrayNode()
                        .add( factory.objectNode().set( "required", factory.arrayNode().add( "left" ) ) )
                        .add( factory.objectNode().set( "required", factory.arrayNode().add( "right" ) ) ) );
   }

   @Override
   public JsonNode visitCharacteristic( final Characteristic characteristic, final ObjectNode context ) {
      return characteristic.getDataType().map( type -> type.accept( this, context ) ).orElseThrow( () ->
            new DocumentGenerationException( "Characteristic " + characteristic + " is missing a dataType" ) );
   }

   private void setNodeInRootSchema( final JsonNode node, final String name ) {
      final ObjectNode schemaNode;
      if ( rootNode.has( "components" ) ) {
         schemaNode = (ObjectNode) rootNode.get( "components" ).get( "schemas" );
      } else {
         schemaNode = factory.objectNode();
         rootNode.set( "components", factory.objectNode().set( "schemas", schemaNode ) );
      }
      schemaNode.set( name, node );
   }

   @Override
   public JsonNode visitComplexType( final ComplexType complexType, final ObjectNode context ) {
      if ( hasVisited.containsKey( complexType ) ) {
         return hasVisited.get( complexType );
      }
      final ObjectNode complexTypeNode = factory.objectNode();
      hasVisited.put( complexType, complexTypeNode );
      if ( complexType.getExtends().isPresent() ) {
         final ComplexType extendedComplexType = complexType.getExtends().get();
         final JsonNode extendedComplexTypeNode = extendedComplexType.accept( this, context );
         final String referenceNodeName = extendedComplexType.getUrn().replace( "#", "_" ).replace( ":", "_" );
         setNodeInRootSchema( extendedComplexTypeNode, referenceNodeName );
         complexTypeNode.set( "allOf", factory.arrayNode().add( factory.objectNode().put( "$ref",
               "#/components/schemas/" + referenceNodeName ) ) );
      }
      return visitHasProperties( complexType, complexTypeNode );
   }

   @Override
   public JsonNode visitAbstractEntity( final AbstractEntity abstractEntity, final ObjectNode context ) {
      final JsonNode abstractEntityNode = visitComplexType( abstractEntity, factory.objectNode() );
      abstractEntity.getExtendingElements().forEach( extendingComplexType -> {
         final JsonNode extendedComplexTypeNode = extendingComplexType.accept( this, context );
         final String referenceNodeName = extendingComplexType.getUrn().replace( "#", "_" ).replace( ":", "_" );
         setNodeInRootSchema( extendedComplexTypeNode, referenceNodeName );
      } );
      return abstractEntityNode;
   }

   @SuppressWarnings( "unchecked" )
   private JsonNode valueToJsonNode( final Object value, final Type type, final BAMM bamm ) {
      final Resource typeResource = ResourceFactory.createResource( type.getUrn() );
      if ( typeResource.equals( RDF.langString ) ) {
         final ObjectNode result = factory.objectNode();
         @SuppressWarnings( "unchecked" ) final List<Tuple2<String, String>> valueEntries = (List<Tuple2<String, String>>) value;
         valueEntries.forEach( entry -> result.set( entry._1(), factory.textNode( entry._2() ) ) );
         return result;
      }

      if ( value instanceof Map ) {
         return getNodeForEntityInstance( (Map<String, Object>) value, (Entity) type, bamm );
      }

      if ( value instanceof Iterable ) {
         final Iterable<?> iterable = (Iterable<?>) value;
         final ArrayNode array = factory.arrayNode();
         iterable.forEach( element -> array.add( valueToJsonNode( element, type, bamm ) ) );
         return array;
      }

      final AspectModelJsonSchemaVisitor.JsonType typeNode = getSchemaTypeForAspectType( typeResource );
      switch ( typeNode ) {
      case NUMBER:
         return getNumberNode( value ).getOrElse( factory.numberNode( 0 ) );
      case STRING:
         return factory.textNode( value.toString() );
      case BOOLEAN:
         return factory.booleanNode( (boolean) value );
      default:
         throw new DocumentGenerationException( "Could not convert value " + value + " to JSON" );
      }
   }

   @Override
   public JsonNode visitEnumeration( final Enumeration enumeration, final ObjectNode context ) {
      final BAMM bamm = new BAMM( enumeration.getMetaModelVersion() );
      final Type type = enumeration.getDataType().orElseThrow( () ->
            new DocumentGenerationException( "Characteristic " + enumeration + " is missing a dataType" ) );
      if ( type.isScalar() ) {
         return createEnumNodeWithScalarValues( enumeration, type, context, bamm );
      }
      return createEnumNodeWithComplexValues( enumeration, type, bamm );
   }

   private JsonNode createEnumNodeWithScalarValues( final Enumeration enumeration, final Type type, final ObjectNode context, final BAMM bamm ) {
      final ArrayNode valuesNode = factory.arrayNode();
      final ObjectNode enumNode = type.getUrn().equals( RDF.langString.getURI() ) ?
            factory.objectNode().put( "type", "object" ) :
            (ObjectNode) type.accept( this, context );
      enumeration.getValues().stream()
            .map( value -> valueToJsonNode( value, type, bamm ) )
            .forEach( valuesNode::add );
      enumNode.set( "enum", valuesNode );
      return enumNode;
   }

   @SuppressWarnings( "unchecked" )
   private JsonNode createEnumNodeWithComplexValues( final Enumeration enumeration, final Type type, final BAMM bamm ) {
      final ObjectNode enumNode = factory.objectNode();
      enumNode.put( "type", "object" );
      final ArrayNode enumValueReferences = factory.arrayNode();
      enumeration.getValues().stream()
            .map( value -> (Map<String, Object>) value )
            //Should the enum value entity be declared with only optional properties, enum values may be declared
            //without any properties. In such a case the value is ignored in the JSON schema since there is nothing
            //to validate
            .filter( value -> value.size() > 1 )
            .forEach( values -> {
               final Object instanceName = values.get( bamm.name().toString() );
               enumValueReferences.add( factory.objectNode().put( "$ref", "#/components/schemas/" + instanceName ) );
               final JsonNode enumValueNode = createNodeForEnumEntityInstance( values, (Entity) type, bamm );
               setNodeInRootSchema( enumValueNode, instanceName.toString() );
            } );
      if ( enumValueReferences.size() > 0 ) {
         enumNode.set( "oneOf", enumValueReferences );
      }
      return enumNode;
   }

   private JsonNode createNodeForEnumEntityInstance( final Map<String, Object> objectMap, final Entity entity, final BAMM bamm ) {
      final ObjectNode enumEntityInstanceNode = factory.objectNode();
      final ArrayNode required = factory.arrayNode();
      final ObjectNode properties = factory.objectNode();
      enumEntityInstanceNode.put( "type", "object" );

      entity.getProperties().stream()
            .filter( property -> !property.isNotInPayload() )
            .filter( property -> objectMap.containsKey( property.getPayloadName() ) )
            .forEach( property -> {
               required.add( property.getPayloadName() );
               properties.set( property.getPayloadName(),
                     createNodeForEnumEntityPropertyInstance( property, objectMap, bamm ) );
            } );

      enumEntityInstanceNode.set( "properties", properties );
      enumEntityInstanceNode.set( "required", required );
      return enumEntityInstanceNode;
   }

   @SuppressWarnings( { "unchecked", "squid:S3655" } )
   //squid S3655 - Properties with an Enumeration Characteristic always have a data type
   private JsonNode createNodeForEnumEntityPropertyInstance( final Property property, final Map<String, Object> objectMap, final BAMM bamm ) {
      final Characteristic characteristic = property.getCharacteristic();
      final Type type = property.getDataType().get();
      final Object valueForProperty = objectMap.get( property.getPayloadName() );
      final AspectModelJsonSchemaVisitor.JsonType schemaType = type.isScalar() ? getSchemaTypeForAspectType(
            ResourceFactory.createResource( type.getUrn() ) ) : JsonType.OBJECT;

      if ( characteristic instanceof SingleEntity ) {
         return createNodeForEnumEntityInstance( (Map<String, Object>) valueForProperty, (Entity) type, bamm );
      }
      if ( characteristic instanceof Collection ) {
         final ObjectNode propertyInstanceNode = factory.objectNode();
         propertyInstanceNode.put( "type", "array" );
         final ObjectNode arrayPropertyInstanceNode = factory.objectNode();
         arrayPropertyInstanceNode.set( "type", schemaType.toJsonNode() );
         final ArrayNode arrayValues = (ArrayNode) valueToJsonNode( valueForProperty, type, bamm );
         propertyInstanceNode.put( "minItems", arrayValues.size() );
         propertyInstanceNode.put( "maxItems", arrayValues.size() );
         arrayPropertyInstanceNode.set( "enum", arrayValues );
         propertyInstanceNode.set( "items", arrayPropertyInstanceNode );
         return propertyInstanceNode;
      }
      final ObjectNode propertyInstanceNode = factory.objectNode();
      propertyInstanceNode.set( "type", schemaType.toJsonNode() );
      propertyInstanceNode.set( "enum", factory.arrayNode().add( valueToJsonNode( valueForProperty, type, bamm ) ) );
      return propertyInstanceNode;
   }

   private JsonNode getNodeForEntityInstance( final Map<String, Object> objectMap, final Entity entity, final BAMM bamm ) {
      final ObjectNode result = factory.objectNode();
      objectMap.entrySet().stream()
            .filter( entry -> !entry.getKey().equals( bamm.name().toString() ) )
            .forEach( entry ->
                  entity.getProperties().stream()
                        .filter( property -> property.getPayloadName().equals( entry.getKey() ) )
                        .filter( property -> !property.isNotInPayload() )
                        .forEach( property -> {
                           final Tuple2<String, JsonNode> propertyEntry = getEntryForProperty( bamm, entry,
                                 property );
                           result.set( propertyEntry._1(), propertyEntry._2() );
                        } ) );
      return result;
   }

   private Tuple2<String, JsonNode> getEntryForProperty( final BAMM bamm, final Map.Entry<String, Object> entry, final Property property ) {
      final Type propertyType = property.getDataType().orElseThrow( () -> new DocumentGenerationException( "Either not supported in Entity instances" ) );

      return Tuple.of( entry.getKey(), valueToJsonNode( entry.getValue(), propertyType, bamm ) );
   }
}
