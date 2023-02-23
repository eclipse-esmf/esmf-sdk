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

package org.eclipse.esmf.aspectmodel.generator.jsonschema;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.eclipse.esmf.aspectmodel.generator.DocumentGenerationException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
import org.eclipse.esmf.samm.KnownVersion;

import org.eclipse.esmf.aspectmodel.resolver.services.BammDataType;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.characteristic.Collection;
import org.eclipse.esmf.characteristic.Either;
import org.eclipse.esmf.characteristic.Enumeration;
import org.eclipse.esmf.characteristic.Set;
import org.eclipse.esmf.characteristic.SingleEntity;
import org.eclipse.esmf.characteristic.SortedSet;
import org.eclipse.esmf.characteristic.Trait;
import org.eclipse.esmf.constraint.LengthConstraint;
import org.eclipse.esmf.constraint.RangeConstraint;
import org.eclipse.esmf.constraint.RegularExpressionConstraint;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.HasProperties;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.metamodel.impl.BoundDefinition;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;
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
   private final Map<Resource, Map<String, JsonNode>> extendedTypeData = ImmutableMap.<Resource, Map<String, JsonNode>> builder()
         .putAll( openApiTypeData )
         .put( RDF.langString,
               Map.of( "patternProperties", factory.objectNode().set( "^.*$", factory.objectNode().set( "type", JsonType.STRING.toJsonNode() ) ) ) )
         .put( XSD.base64Binary, Map.of( "contentEncoding", factory.textNode( "base64" ) ) )
         .build();

   private final Map<Resource, Map<String, JsonNode>> typeData;

   static final String JSON_SCHEMA_VERSION = "http://json-schema.org/draft-04/schema";
   private final Locale locale;

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
   private final Map<ModelElement, JsonNode> hasVisited = new HashMap<>();

   private final boolean generateCommentForSeeAttributes;

   public AspectModelJsonSchemaVisitor( final boolean useExtendedTypes, final Locale locale ) {
      this( useExtendedTypes, locale, false );
   }

   public AspectModelJsonSchemaVisitor( final boolean useExtendedTypes ) {
      this( useExtendedTypes, Locale.ENGLISH );
   }

   public AspectModelJsonSchemaVisitor( final boolean useExtendedTypes, final Locale locale, final boolean generateCommentForSeeAttributes ) {
      if ( useExtendedTypes ) {
         typeData = extendedTypeData;
      } else {
         typeData = openApiTypeData;
      }

      this.locale = locale;
      this.generateCommentForSeeAttributes = generateCommentForSeeAttributes;
   }

   public ObjectNode getRootNode() {
      return rootNode;
   }

   @Override
   public JsonNode visitBase( final ModelElement modelElement, final ObjectNode context ) {
      return factory.objectNode();
   }

   public JsonNode visitAspectForOpenApi( final Aspect aspect ) {
      addDescription( rootNode, aspect, locale );
      return visitHasProperties( aspect, rootNode );
   }

   @Override
   public JsonNode visitAspect( final Aspect aspect, final ObjectNode context ) {
      rootNode.put( "$schema", JSON_SCHEMA_VERSION );
      addDescription( rootNode, aspect, locale );
      return visitHasProperties( aspect, rootNode );
   }

   @Override
   public JsonNode visitHasProperties( final HasProperties element, final ObjectNode context ) {
      context.put( "type", "object" );
      final ObjectNode properties =
            Stream.ofAll( element.getProperties() )
                  .filter( property -> !property.isNotInPayload() )
                  .filter( property -> !property.isAbstract() )
                  .foldLeft( factory.objectNode(), ( propertyContext, property ) -> {
                     final JsonNode jsonNode = visitProperty( property, propertyContext );
                     return propertyContext.set( property.getPayloadName(), jsonNode );
                  } );
      context.set( "properties", properties );
      final List<TextNode> requiredProperties =
            Stream.ofAll( element.getProperties() )
                  .filter( property -> !property.isNotInPayload() )
                  .filter( property -> !property.isOptional() )
                  .filter( property -> !property.isAbstract() ).toList()
                  .map( property -> factory.textNode( property.getPayloadName() ) )
                  .toJavaList();
      if ( !requiredProperties.isEmpty() ) {
         final ArrayNode required = factory.arrayNode();
         required.addAll( requiredProperties );
         context.set( "required", required );
      }
      return context;
   }

   private Characteristic determineCharacteristic( final Property property ) {
      if ( property.getCharacteristic().isPresent() ) {
         return property.getCharacteristic().get();
      }

      if ( property.isAbstract() ) {
         for ( final Property processedProperty : processedProperties ) {
            if ( processedProperty.getExtends().isEmpty() ) {
               continue;
            }
            final Property superProperty = processedProperty.getExtends().get();
            if ( superProperty.equals( property ) ) {
               return determineCharacteristic( processedProperty );
            }
         }
      }

      return null;
   }

   @Override
   @SuppressWarnings( "squid:S2250" )
   //Amount of elements in list is in regard to amount of properties in aspect model. Even in bigger aspects this should not lead to performance issues
   public JsonNode visitProperty( final Property property, final ObjectNode context ) {
      final ObjectNode propertyNode = addDescription( factory.objectNode(), property, locale );
      final Characteristic characteristic = determineCharacteristic( property );
      final String referenceNodeName;
      if ( characteristic instanceof SingleEntity ) {
         referenceNodeName = escapeUrn( characteristic.getDataType().get().getUrn() );
      } else {
         referenceNodeName = characteristic.getAspectModelUrn()
               .map( AspectModelUrn::toString )
               .map( this::escapeUrn )
               .orElseGet( characteristic::getName );
      }

      if ( processedProperties.contains( property ) ) {
         return propertyNode.put( "$ref", "#/components/schemas/" + referenceNodeName );
      }
      processedProperties.add( property );
      final JsonNode characteristicSchema = characteristic.accept( this, context );
      setNodeInRootSchema( characteristicSchema, referenceNodeName );
      return propertyNode.put( "$ref", "#/components/schemas/" + referenceNodeName );
   }

   private AspectModelJsonSchemaVisitor.JsonType getSchemaTypeForAspectType( final Resource type ) {
      return TYPE_MAP.getOrDefault( type, AspectModelJsonSchemaVisitor.JsonType.STRING );
   }

   private Map<String, JsonNode> getAdditionalFieldsForType( final Resource type, final KnownVersion metaModelVersion ) {
      final SAMM samm = new SAMM( metaModelVersion );
      final Map<Resource, Map<String, JsonNode>> typeDates = ImmutableMap.<Resource, Map<String, JsonNode>> builder()
            .putAll( typeData )
            .put( samm.curie(), Map.of( "pattern", factory.textNode( BammDataType.CURIE_REGEX ) ) )
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
      addDescription( collectionNode, collection, locale );
      collectionNode.put( "type", "array" );
      final Optional<Characteristic> characteristic = collection.getElementCharacteristic();
      if ( characteristic.isPresent() ) {
         collectionNode.set( "items", characteristic.get().accept( this, collectionNode ) );
         return collectionNode;
      }

      collection.getDataType().ifPresent( type -> {
         final JsonNode typeNode = type.accept( this, collectionNode );
         if ( !type.is( Scalar.class ) ) {
            final String referenceNodeName = escapeUrn( type.getUrn() );
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
      addDescription( propertyNode, trait, locale );
      return Stream.ofAll( trait.getConstraints() ).foldLeft( propertyNode, ( node, constraint ) -> ((ObjectNode) (constraint.accept( this, node ))) );
   }

   @Override
   public JsonNode visitConstraint( final Constraint constraint, final ObjectNode context ) {
      addDescription( context, constraint, locale );
      return context;
   }

   @Override
   public JsonNode visitLengthConstraint( final LengthConstraint lengthConstraint, final ObjectNode context ) {
      addDescription( context, lengthConstraint, locale );
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
      addDescription( context, regularExpressionConstraint, locale );
      context.set( "pattern", factory.textNode( regularExpressionConstraint.getValue() ) );
      return context;
   }

   @Override
   public JsonNode visitRangeConstraint( final RangeConstraint rangeConstraint, final ObjectNode context ) {
      addDescription( context, rangeConstraint, locale );
      rangeConstraint.getMaxValue().map( maxValue -> maxValue.accept( this, context ) ).ifPresent( value -> {
         if ( value instanceof NumericNode ) {
            context.set( "maximum", value );
            context.put( "exclusiveMaximum", rangeConstraint.getUpperBoundDefinition().equals( BoundDefinition.LESS_THAN ) );
         }
      } );
      rangeConstraint.getMinValue().map( minValue -> minValue.accept( this, context ) ).ifPresent( value -> {
         if ( value instanceof NumericNode ) {
            context.set( "minimum", value );
            context.put( "exclusiveMinimum", rangeConstraint.getLowerBoundDefinition().equals( BoundDefinition.GREATER_THAN ) );
         }
      } );
      return context;
   }

   @Override
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

      return addDescription( factory.objectNode(), either, locale )
            .put( "additionalProperties", false )
            .<ObjectNode> set( "properties", properties )
            .set( "oneOf",
                  factory.arrayNode()
                        .add( factory.objectNode().set( "required", factory.arrayNode().add( "left" ) ) )
                        .add( factory.objectNode().set( "required", factory.arrayNode().add( "right" ) ) ) );
   }

   @Override
   public JsonNode visitCharacteristic( final Characteristic characteristic,
         final ObjectNode context ) {
      final JsonNode dataTypeNode = characteristic.getDataType().map( type -> type.accept( this, context ) )
            .orElseThrow( () -> new DocumentGenerationException( "Characteristic " + characteristic + " is missing a dataType" ) );

      return addDescription( (ObjectNode) dataTypeNode, characteristic, locale );
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

      addDescription( complexTypeNode, complexType, locale );
      // visitHasProperties needs to be called before accept() on the supertype:
      // This Entity's Properties could be extending an AbstractProperty on the supertype,
      // in order to know the corresponding Characteristic this must be visited first
      final JsonNode result = visitHasProperties( complexType, complexTypeNode );
      if ( complexType.getExtends().isPresent() ) {
         final ComplexType extendedComplexType = complexType.getExtends().get();
         final JsonNode extendedComplexTypeNode = extendedComplexType.accept( this, context );
         final String referenceNodeName = escapeUrn( extendedComplexType.getUrn() );
         setNodeInRootSchema( extendedComplexTypeNode, referenceNodeName );
         complexTypeNode.set( "allOf", factory.arrayNode().add( factory.objectNode().put( "$ref",
               "#/components/schemas/" + referenceNodeName ) ) );
      }
      return result;
   }

   @Override
   public JsonNode visitAbstractEntity( final AbstractEntity abstractEntity, final ObjectNode context ) {
      final JsonNode abstractEntityNode = visitComplexType( abstractEntity, factory.objectNode() );
      abstractEntity.getExtendingElements().forEach( extendingComplexType -> {
         final JsonNode extendedComplexTypeNode = extendingComplexType.accept( this, context );
         final String referenceNodeName = escapeUrn( extendingComplexType.getUrn() );
         setNodeInRootSchema( extendedComplexTypeNode, referenceNodeName );
      } );
      return abstractEntityNode;
   }

   @Override
   public JsonNode visitScalarValue( final ScalarValue value, final ObjectNode context ) {
      final Type type = value.getType();
      final Resource typeResource = ResourceFactory.createResource( type.getUrn() );
      if ( typeResource.equals( RDF.langString ) ) {
         final ObjectNode result = factory.objectNode();
         final LangString langString = (LangString) value.getValue();
         result.set( langString.getLanguageTag().toLanguageTag(), factory.textNode( langString.getValue() ) );
         return result;
      }

      final AspectModelJsonSchemaVisitor.JsonType typeNode = getSchemaTypeForAspectType( typeResource );
      switch ( typeNode ) {
      case NUMBER:
         return getNumberNode( value.getValue() ).getOrElse( factory.numberNode( 0 ) );
      case STRING:
         return factory.textNode( value.getValue().toString() );
      case BOOLEAN:
         return factory.booleanNode( (boolean) value.getValue() );
      default:
         throw new DocumentGenerationException( "Could not convert value " + value + " to JSON" );
      }
   }

   @Override
   public JsonNode visitCollectionValue( final CollectionValue value, final ObjectNode context ) {
      final ArrayNode array = factory.arrayNode();
      value.getValues().forEach( collectionValue -> array.add( collectionValue.accept( this, context ) ) );
      return array;
   }

   @Override
   public JsonNode visitEntityInstance( final EntityInstance instance, final ObjectNode context ) {
      final ObjectNode result = factory.objectNode();
      for ( final Map.Entry<Property, Value> assertion : instance.getAssertions().entrySet() ) {
         final Property property = assertion.getKey();
         if ( property.isNotInPayload() ) {
            continue;
         }
         result.set( property.getPayloadName(), assertion.getValue().accept( this, context ) );
      }
      return result;
   }

   @Override
   public JsonNode visitEnumeration( final Enumeration enumeration, final ObjectNode context ) {
      final SAMM samm = new SAMM( enumeration.getMetaModelVersion() );
      final Type type = enumeration.getDataType().orElseThrow( () ->
            new DocumentGenerationException( "Characteristic " + enumeration + " is missing a dataType" ) );
      if ( type.is( Scalar.class ) ) {
         return createEnumNodeWithScalarValues( enumeration, type, context, samm );
      }
      return createEnumNodeWithComplexValues( enumeration, samm );
   }

   private String escapeUrn( final String urn ) {
      return urn.replace( "#", "_" )
            .replace( ":", "_" );
   }

   private JsonNode createEnumNodeWithScalarValues( final Enumeration enumeration, final Type type, final ObjectNode context, final SAMM samm ) {
      final ArrayNode valuesNode = factory.arrayNode();
      final ObjectNode enumNode = type.getUrn().equals( RDF.langString.getURI() ) ?
            factory.objectNode().put( "type", "object" ) :
            (ObjectNode) type.accept( this, context );
      addDescription( enumNode, enumeration, locale );
      enumeration.getValues().stream()
            .map( value -> value.accept( this, enumNode ) )
            .forEach( valuesNode::add );
      enumNode.set( "enum", valuesNode );
      return enumNode;
   }

   private JsonNode createEnumNodeWithComplexValues( final Enumeration enumeration, final SAMM samm ) {
      final ObjectNode enumNode = factory.objectNode();
      addDescription( enumNode, enumeration, locale );
      enumNode.put( "type", "object" );
      final ArrayNode enumValueReferences = factory.arrayNode();
      enumeration.getValues().stream()
            .map( value -> value.as( EntityInstance.class ) )
            //Should the enum value entity be declared with only optional properties, enum values may be declared
            //without any properties. In such a case the value is ignored in the JSON schema since there is nothing
            //to validate
            .filter( value -> value.getAssertions().size() > 0 )
            .forEach( value -> {
               final String instanceName = value.getName();
               enumValueReferences.add( factory.objectNode().put( "$ref", "#/components/schemas/" + instanceName ) );
               final JsonNode enumValueNode = createNodeForEnumEntityInstance( value, samm );
               setNodeInRootSchema( enumValueNode, instanceName );
            } );
      if ( enumValueReferences.size() > 0 ) {
         enumNode.set( "oneOf", enumValueReferences );
      }
      return enumNode;
   }

   private JsonNode createNodeForEnumEntityInstance( final EntityInstance entityInstance, final SAMM samm ) {
      final ObjectNode enumEntityInstanceNode = factory.objectNode();
      final ArrayNode required = factory.arrayNode();
      final ObjectNode properties = factory.objectNode();
      addDescription( enumEntityInstanceNode, entityInstance, locale );
      enumEntityInstanceNode.put( "type", "object" );

      final Entity entity = entityInstance.getEntityType();
      entity.getProperties().stream()
            .filter( property -> !property.isNotInPayload() )
            .filter( property -> entityInstance.getAssertions().containsKey( property ) )
            .forEach( property -> {
               required.add( property.getPayloadName() );
               properties.set( property.getPayloadName(),
                     createNodeForEnumEntityPropertyInstance( property, entityInstance, samm ) );
            } );

      enumEntityInstanceNode.set( "properties", properties );
      enumEntityInstanceNode.set( "required", required );
      return enumEntityInstanceNode;
   }

   @SuppressWarnings( { "squid:S3655" } )
   //squid S3655 - Properties with an Enumeration Characteristic always have a data type
   private JsonNode createNodeForEnumEntityPropertyInstance( final Property property, final EntityInstance entityInstance, final SAMM samm ) {
      final Characteristic characteristic = property.getCharacteristic().orElseThrow( () ->
            new DocumentGenerationException( "Property " + property + " has no Characteristic" ) );
      final Type type = property.getDataType().get();
      final Value valueForProperty = entityInstance.getAssertions().get( property );
      final AspectModelJsonSchemaVisitor.JsonType schemaType = type.is( Scalar.class ) ?
            getSchemaTypeForAspectType( ResourceFactory.createResource( type.getUrn() ) ) :
            JsonType.OBJECT;

      if ( characteristic.is( SingleEntity.class ) ) {
         return createNodeForEnumEntityInstance( valueForProperty.as( EntityInstance.class ), samm );
      }
      if ( characteristic.is( Collection.class ) ) {
         final ObjectNode propertyInstanceNode = factory.objectNode();
         addDescription( propertyInstanceNode, property, locale );
         propertyInstanceNode.put( "type", "array" );
         final ObjectNode arrayPropertyInstanceNode = factory.objectNode();
         arrayPropertyInstanceNode.set( "type", schemaType.toJsonNode() );
         final ArrayNode arrayValues = (ArrayNode) valueForProperty.accept( this, propertyInstanceNode );
         propertyInstanceNode.put( "minItems", arrayValues.size() );
         propertyInstanceNode.put( "maxItems", arrayValues.size() );
         arrayPropertyInstanceNode.set( "enum", arrayValues );
         propertyInstanceNode.set( "items", arrayPropertyInstanceNode );
         return propertyInstanceNode;
      }
      final ObjectNode propertyInstanceNode = factory.objectNode();
      addDescription( propertyInstanceNode, property, locale );
      propertyInstanceNode.set( "type", schemaType.toJsonNode() );
      propertyInstanceNode.set( "enum", factory.arrayNode().add( valueForProperty.accept( this, propertyInstanceNode ) ) );
      return propertyInstanceNode;
   }

   private ObjectNode addDescription( final ObjectNode node, final NamedElement describedElement, final Locale locale ) {
      final String description = describedElement.getDescription( locale );

      if ( !Strings.isNullOrEmpty( description ) ) {
         node.put( "description", description );
      }

      if ( generateCommentForSeeAttributes ) {
         final String sees = String.join( ", ", describedElement.getSee() );
         if ( !Strings.isNullOrEmpty( sees ) ) {
            node.put( "$comment", "See: " + sees );
         }
      }

      return node;
   }
}
