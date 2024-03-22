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
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.DocumentGenerationException;
import org.eclipse.esmf.aspectmodel.resolver.services.SammDataType;
import org.eclipse.esmf.aspectmodel.vocabulary.SAMM;
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
import org.eclipse.esmf.samm.KnownVersion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import io.vavr.control.Try;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

public class AspectModelJsonSchemaVisitor implements AspectVisitor<JsonNode, ObjectNode> {

   public static final String SAMM_EXTENSION = "x-samm-aspect-model-urn";
   private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;
   private final List<Property> processedProperties = new LinkedList<>();
   private final List<String> reservedSchemaNames;
   private final BiMap<NamedElement, String> schemaNameForElement = HashBiMap.create();

   /**
    * Defines JSON Schema restrictions for Aspect/XSD types, e.g. formats and patterns
    * according to OpenAPI Schema definition.
    *
    * @see AspectModelJsonSchemaVisitor#getSchemaTypeForAspectType(Resource)
    */
   public static final Map<Resource, Map<String, JsonNode>> OPEN_API_TYPE_DATA =
         ImmutableMap.<Resource, Map<String, JsonNode>> builder()
               .put( XSD.date, Map.of( "format", FACTORY.textNode( "date" ) ) )
               .put( XSD.time, Map.of( "format", FACTORY.textNode( "time" ) ) )
               // Time offset (time zone) is optional in XSD dateTime.
               // Source: https://www.w3.org/TR/xmlschema11-2/#dateTime
               .put( XSD.dateTime, Map.of( "pattern", FACTORY.textNode(
                     "-?([1-9][0-9]{3,}|0[0-9]{3})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])"
                           + "T(([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9](\\.[0-9]+)?|(24:00:00(\\.0+)?))"
                           + "(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?" ) ) )
               // JSON Schema format "date-time" corresponds to RFC 3339 production "date-time", which
               // includes mandatory time offset (time zone)
               .put( XSD.dateTimeStamp, Map.of( "format", FACTORY.textNode( "date-time" ) ) )
               // Source: https://www.w3.org/TR/xmlschema11-2/#gYear
               .put( XSD.gYear,
                     Map.of( "pattern", FACTORY.textNode( "-?([1-9][0-9]{3,}|0[0-9]{3})(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?" ) ) )
               // Source: https://www.w3.org/TR/xmlschema11-2/#gMonth
               .put( XSD.gMonth,
                     Map.of( "pattern", FACTORY.textNode( "--(0[1-9]|1[0-2])(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?" ) ) )
               // Source https://www.w3.org/TR/xmlschema11-2/#gDay
               .put( XSD.gDay,
                     Map.of( "pattern", FACTORY.textNode( "---(0[1-9]|[12][0-9]|3[01])(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?" ) ) )
               // Source: https://www.w3.org/TR/xmlschema11-2/#gYearMonth
               .put( XSD.gYearMonth, Map.of( "pattern", FACTORY.textNode(
                     "-?([1-9][0-9]{3,}|0[0-9]{3})-(0[1-9]|1[0-2])(Z|(\\+|-)((0[0-9]|1[0-3]):[0-5][0-9]|14:00))?" ) ) )
               .put( XSD.duration, Map.of( "format", FACTORY.textNode( "duration" ) ) )
               // Source: https://www.w3.org/TR/xmlschema11-2/#yearMonthDuration
               .put( XSD.yearMonthDuration, Map.of( "pattern", FACTORY.textNode( "-?P[0-9]+(Y([0-9]+M)?|M)" ) ) )
               // Source: https://www.w3.org/TR/xmlschema11-2/#dayTimeDuration
               .put( XSD.dayTimeDuration, Map.of( "pattern", FACTORY.textNode( "[^YM]*[DT].*" ) ) )
               .put( XSD.xbyte, Map.of( "minimum", FACTORY.numberNode( Byte.MIN_VALUE ), "maximum", FACTORY.numberNode( Byte.MAX_VALUE ) ) )
               .put( XSD.xshort,
                     Map.of( "minimum", FACTORY.numberNode( Short.MIN_VALUE ), "maximum", FACTORY.numberNode( Short.MAX_VALUE ) ) )
               .put( XSD.xint,
                     Map.of( "minimum", FACTORY.numberNode( Integer.MIN_VALUE ), "maximum", FACTORY.numberNode( Integer.MAX_VALUE ) ) )
               .put( XSD.xlong, Map.of( "minimum", FACTORY.numberNode( Long.MIN_VALUE ), "maximum", FACTORY.numberNode( Long.MAX_VALUE ) ) )
               .put( XSD.unsignedByte, Map.of( "minimum", FACTORY.numberNode( 0 ), "maximum", FACTORY.numberNode( 255 ) ) )
               .put( XSD.unsignedShort, Map.of( "minimum", FACTORY.numberNode( 0 ), "maximum", FACTORY.numberNode( 65535 ) ) )
               .put( XSD.unsignedInt, Map.of( "minimum", FACTORY.numberNode( 0 ), "maximum", FACTORY.numberNode( 4294967295L ) ) )
               .put( XSD.unsignedLong, Map.of( "minimum", FACTORY.numberNode( 0 ), "maximum",
                     FACTORY.numberNode( new BigDecimal( "18446744073709551615" ) ) ) )
               .put( XSD.positiveInteger, Map.of( "minimum", FACTORY.numberNode( 1 ) ) )
               .put( XSD.nonNegativeInteger, Map.of( "minimum", FACTORY.numberNode( 0 ) ) )
               .put( XSD.negativeInteger, Map.of( "maximum", FACTORY.numberNode( -1 ) ) )
               .put( XSD.nonPositiveInteger, Map.of( "maximum", FACTORY.numberNode( 0 ) ) )
               .put( XSD.hexBinary, Map.of( "pattern", FACTORY.textNode( "([0-9a-fA-F])([0-9a-fA-F])*" ) ) )
               .put( XSD.anyURI, Map.of( "format", FACTORY.textNode( "uri" ) ) )
               .build();

   /**
    * Defines JSON Schema restrictions for Aspect/XSD types, e.g. formats and patterns
    *
    * @see AspectModelJsonSchemaVisitor#getSchemaTypeForAspectType(Resource)
    */
   private final Map<Resource, Map<String, JsonNode>> extendedTypeData = ImmutableMap.<Resource, Map<String, JsonNode>> builder()
         .putAll( OPEN_API_TYPE_DATA )
         .put( RDF.langString,
               Map.of( "patternProperties",
                     FACTORY.objectNode().set( "^.*$", FACTORY.objectNode().set( "type", JsonType.STRING.toJsonNode() ) ) ) )
         .put( XSD.base64Binary, Map.of( "contentEncoding", FACTORY.textNode( "base64" ) ) )
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

   private final ObjectNode rootNode = FACTORY.objectNode();
   private final Map<ModelElement, JsonNode> hasVisited = new HashMap<>();

   private final boolean generateCommentForSeeAttributes;

   public AspectModelJsonSchemaVisitor( final boolean useExtendedTypes, final Locale locale ) {
      this( useExtendedTypes, locale, false, List.of() );
   }

   public AspectModelJsonSchemaVisitor( final boolean useExtendedTypes ) {
      this( useExtendedTypes, Locale.ENGLISH );
   }

   public AspectModelJsonSchemaVisitor( final boolean useExtendedTypes, final Locale locale,
         final boolean generateCommentForSeeAttributes, final List<String> reservedSchemaNames ) {
      if ( useExtendedTypes ) {
         typeData = extendedTypeData;
      } else {
         typeData = OPEN_API_TYPE_DATA;
      }

      this.locale = locale;
      this.generateCommentForSeeAttributes = generateCommentForSeeAttributes;
      this.reservedSchemaNames = reservedSchemaNames;
   }

   public ObjectNode getRootNode() {
      return rootNode;
   }

   private String getSchemaNameForModelElement( final NamedElement element ) {
      final String existingSchemaName = schemaNameForElement.get( element );
      if ( existingSchemaName != null ) {
         return existingSchemaName;
      }
      // Check if the schema name is already used by another element
      final BiMap<String, NamedElement> elementBySchemaName = schemaNameForElement.inverse();
      final String elementName = element instanceof final Property property ? property.getPayloadName() : element.getName();
      final String designatedSchemaName =
            Stream.concat( Stream.of( elementName ), IntStream.iterate( 0, i -> i + 1 ).mapToObj( i -> element.getName() + i ) )
                  .filter( schemaName -> elementBySchemaName.get( schemaName ) == null )
                  .filter( schemaName -> !reservedSchemaNames.contains( schemaName ) )
                  .findFirst()
                  .orElseThrow( () -> new DocumentGenerationException( "Could not determine schema name for " + element ) );
      schemaNameForElement.put( element, designatedSchemaName );
      return designatedSchemaName;
   }

   @Override
   public JsonNode visitBase( final ModelElement modelElement, final ObjectNode context ) {
      final ObjectNode result = FACTORY.objectNode();
      if ( modelElement instanceof final NamedElement namedElement ) {
         addSammExtensionAttribute( result, namedElement );
      }
      return result;
   }

   public JsonNode visitAspectForOpenApi( final Aspect aspect ) {
      addDescription( rootNode, aspect, locale );
      addSammExtensionAttribute( rootNode, aspect );
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
      if ( element instanceof final NamedElement namedElement ) {
         addSammExtensionAttribute( context, namedElement );
      }

      final ObjectNode properties =
            io.vavr.collection.Stream.ofAll( element.getProperties() )
                  .filter( property -> !property.isNotInPayload() )
                  .filter( property -> !property.isAbstract() )
                  .foldLeft( FACTORY.objectNode(), ( propertyContext, property ) -> {
                     final JsonNode jsonNode = visitProperty( property, propertyContext );
                     return propertyContext.set( property.getPayloadName(), jsonNode );
                  } );
      context.set( "properties", properties );
      final List<TextNode> requiredProperties =
            io.vavr.collection.Stream.ofAll( element.getProperties() )
                  .filter( property -> !property.isNotInPayload() )
                  .filter( property -> !property.isOptional() )
                  .filter( property -> !property.isAbstract() ).toList()
                  .map( property -> FACTORY.textNode( property.getPayloadName() ) )
                  .toJavaList();
      if ( !requiredProperties.isEmpty() ) {
         final ArrayNode required = FACTORY.arrayNode();
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

      throw new DocumentGenerationException( "Can not determine characteristic for Property " + property );
   }

   @Override
   @SuppressWarnings( "squid:S2250" )
   public JsonNode visitProperty( final Property property, final ObjectNode context ) {
      final ObjectNode propertyNode = addDescription( FACTORY.objectNode(), property, locale );
      addSammExtensionAttribute( propertyNode, property );
      final Characteristic characteristic = determineCharacteristic( property );
      final String referenceNodeName = getSchemaNameForModelElement( characteristic );
      if ( processedProperties.contains( property ) ) {
         return propertyNode.put( "$ref", "#/components/schemas/" + referenceNodeName );
      }
      processedProperties.add( property );
      final JsonNode schemaNode = characteristic.accept( this, context );
      setNodeInRootSchema( schemaNode, referenceNodeName );
      return propertyNode.put( "$ref", "#/components/schemas/" + referenceNodeName );
   }

   @Override
   public JsonNode visitSingleEntity( final SingleEntity singleEntity, final ObjectNode context ) {
      final ObjectNode characteristicNode = FACTORY.objectNode();
      addDescription( characteristicNode, singleEntity, locale );
      addSammExtensionAttribute( characteristicNode, singleEntity );
      characteristicNode.put( "type", "object" );
      final ComplexType entityType = singleEntity.getDataType().map( type -> type.as( ComplexType.class ) )
            .orElseThrow( () -> new DocumentGenerationException( "Characteristic " + singleEntity + " is missing a dataType" ) );
      final String referenceNodeName = getSchemaNameForModelElement( entityType );
      characteristicNode.set( "allOf", FACTORY.arrayNode().add( FACTORY.objectNode().put( "$ref",
            "#/components/schemas/" + referenceNodeName ) ) );
      final JsonNode entitySchema = entityType.accept( this, context );
      setNodeInRootSchema( entitySchema, referenceNodeName );
      return characteristicNode;
   }

   private AspectModelJsonSchemaVisitor.JsonType getSchemaTypeForAspectType( final Resource type ) {
      return TYPE_MAP.getOrDefault( type, AspectModelJsonSchemaVisitor.JsonType.STRING );
   }

   private Map<String, JsonNode> getAdditionalFieldsForType( final Resource type, final KnownVersion metaModelVersion ) {
      final SAMM samm = new SAMM( metaModelVersion );
      final Map<Resource, Map<String, JsonNode>> typeDates = ImmutableMap.<Resource, Map<String, JsonNode>> builder()
            .putAll( typeData )
            .put( samm.curie(), Map.of( "pattern", FACTORY.textNode( SammDataType.CURIE_REGEX ) ) )
            .build();
      return typeDates.getOrDefault( type, Map.of() );
   }

   @Override
   public JsonNode visitSet( final Set set, final ObjectNode context ) {
      final ObjectNode collectionNode = (ObjectNode) visitCollection( set, context );
      addSammExtensionAttribute( collectionNode, set );
      return collectionNode.put( "uniqueItems", true );
   }

   @Override
   public JsonNode visitSortedSet( final SortedSet sortedSet, final ObjectNode context ) {
      final ObjectNode collectionNode = (ObjectNode) visitCollection( sortedSet, context );
      addSammExtensionAttribute( collectionNode, sortedSet );
      return collectionNode.put( "uniqueItems", true );
   }

   @Override
   public JsonNode visitCollection( final Collection collection, final ObjectNode context ) {
      final ObjectNode collectionNode = FACTORY.objectNode();
      addDescription( collectionNode, collection, locale );
      addSammExtensionAttribute( collectionNode, collection );
      collectionNode.put( "type", "array" );
      final Optional<Characteristic> characteristic = collection.getElementCharacteristic();
      if ( characteristic.isPresent() ) {
         collectionNode.set( "items", characteristic.get().accept( this, collectionNode ) );
         return collectionNode;
      }

      collection.getDataType().ifPresent( type -> {
         final JsonNode typeNode = type.accept( this, collectionNode );
         if ( type instanceof final ComplexType complexType ) {
            final String referenceNodeName = getSchemaNameForModelElement( complexType );
            final ObjectNode referenceNode = FACTORY.objectNode().put( "$ref", "#/components/schemas/" + referenceNodeName );
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
      final ObjectNode characteristicNode = (ObjectNode) trait.getBaseCharacteristic().accept( this, context );
      addDescription( characteristicNode, trait, locale );
      addSammExtensionAttribute( characteristicNode, trait );
      return io.vavr.collection.Stream.ofAll( trait.getConstraints() )
            .foldLeft( characteristicNode, ( node, constraint ) -> ( (ObjectNode) ( constraint.accept( this, node ) ) ) );
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
         return Try.success( FACTORY.numberNode( new BigDecimal( value.toString() ) ) );
      } catch ( final NumberFormatException exception ) {
         return Try.failure( exception );
      }
   }

   @Override
   public JsonNode visitRegularExpressionConstraint( final RegularExpressionConstraint regularExpressionConstraint,
         final ObjectNode context ) {
      addDescription( context, regularExpressionConstraint, locale );
      context.set( "pattern", FACTORY.textNode( regularExpressionConstraint.getValue() ) );
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
      final ObjectNode propertyNode = FACTORY.objectNode();
      final AspectModelJsonSchemaVisitor.JsonType value = getSchemaTypeForAspectType( ResourceFactory.createResource( scalar.getUrn() ) );
      propertyNode.set( "type", value.toJsonNode() );
      getAdditionalFieldsForType( ResourceFactory.createResource( scalar.getUrn() ), scalar.getMetaModelVersion() ).forEach(
            propertyNode::set );
      return propertyNode;
   }

   @Override
   public JsonNode visitEither( final Either either, final ObjectNode context ) {
      final ObjectNode properties = FACTORY.objectNode();
      properties.set( "left", either.getLeft().accept( this, properties ) );
      properties.set( "right", either.getRight().accept( this, properties ) );

      final ObjectNode result = addDescription( FACTORY.objectNode(), either, locale )
            .put( "additionalProperties", false )
            .<ObjectNode> set( "properties", properties )
            .set( "oneOf",
                  FACTORY.arrayNode()
                        .add( FACTORY.objectNode().set( "required", FACTORY.arrayNode().add( "left" ) ) )
                        .add( FACTORY.objectNode().set( "required", FACTORY.arrayNode().add( "right" ) ) ) );
      addSammExtensionAttribute( result, either );
      return result;
   }

   @Override
   public JsonNode visitCharacteristic( final Characteristic characteristic, final ObjectNode context ) {
      return characteristic.getDataType().map( type -> {
         final JsonNode typeNode = type.accept( this, context );
         if ( typeNode instanceof final ObjectNode objectNode ) {
            addSammExtensionAttribute( objectNode, characteristic );
            return addDescription( objectNode, characteristic, locale );
         }
         throw new DocumentGenerationException( "Could not generate description for characteristic " + characteristic );
      } ).orElseThrow( () -> new DocumentGenerationException( "Characteristic " + characteristic + " is missing a dataType" ) );
   }

   private void setNodeInRootSchema( final JsonNode node, final String name ) {
      final ObjectNode schemaNode;
      if ( rootNode.has( "components" ) ) {
         schemaNode = (ObjectNode) rootNode.get( "components" ).get( "schemas" );
      } else {
         schemaNode = FACTORY.objectNode();
         rootNode.set( "components", FACTORY.objectNode().set( "schemas", schemaNode ) );
      }
      schemaNode.set( name, node );
   }

   @Override
   public JsonNode visitComplexType( final ComplexType complexType, final ObjectNode context ) {
      if ( hasVisited.containsKey( complexType ) ) {
         return hasVisited.get( complexType );
      }
      final ObjectNode complexTypeNode = FACTORY.objectNode();
      hasVisited.put( complexType, complexTypeNode );

      addDescription( complexTypeNode, complexType, locale );
      addSammExtensionAttribute( complexTypeNode, complexType );

      // visitHasProperties needs to be called before accept() on the supertype:
      // This Entity's Properties could be extending an AbstractProperty on the supertype,
      // in order to know the corresponding Characteristic this must be visited first
      final JsonNode result = visitHasProperties( complexType, complexTypeNode );
      if ( complexType.getExtends().isPresent() ) {
         final ComplexType extendedComplexType = complexType.getExtends().get();
         final JsonNode extendedComplexTypeNode = extendedComplexType.accept( this, context );
         final String referenceNodeName = getSchemaNameForModelElement( extendedComplexType );
         setNodeInRootSchema( extendedComplexTypeNode, referenceNodeName );
         complexTypeNode.set( "allOf", FACTORY.arrayNode().add( FACTORY.objectNode().put( "$ref",
               "#/components/schemas/" + referenceNodeName ) ) );
      }
      return result;
   }

   @Override
   public JsonNode visitAbstractEntity( final AbstractEntity abstractEntity, final ObjectNode context ) {
      final JsonNode abstractEntityNode = visitComplexType( abstractEntity, FACTORY.objectNode() );
      abstractEntity.getExtendingElements().forEach( extendingComplexType -> {
         final JsonNode extendedComplexTypeNode = extendingComplexType.accept( this, context );
         final String referenceNodeName = getSchemaNameForModelElement( extendingComplexType );
         setNodeInRootSchema( extendedComplexTypeNode, referenceNodeName );
      } );
      return abstractEntityNode;
   }

   @Override
   public JsonNode visitScalarValue( final ScalarValue value, final ObjectNode context ) {
      final Type type = value.getType();
      final Resource typeResource = ResourceFactory.createResource( type.getUrn() );
      if ( typeResource.equals( RDF.langString ) ) {
         final ObjectNode result = FACTORY.objectNode();
         final LangString langString = (LangString) value.getValue();
         result.set( langString.getLanguageTag().toLanguageTag(), FACTORY.textNode( langString.getValue() ) );
         return result;
      }

      return switch ( getSchemaTypeForAspectType( typeResource ) ) {
         case NUMBER -> getNumberNode( value.getValue() ).getOrElse( FACTORY.numberNode( 0 ) );
         case STRING -> FACTORY.textNode( value.getValue().toString() );
         case BOOLEAN -> FACTORY.booleanNode( (boolean) value.getValue() );
         default -> throw new DocumentGenerationException( "Could not convert value " + value + " to JSON" );
      };
   }

   @Override
   public JsonNode visitCollectionValue( final CollectionValue value, final ObjectNode context ) {
      final ArrayNode array = FACTORY.arrayNode();
      value.getValues().forEach( collectionValue -> array.add( collectionValue.accept( this, context ) ) );
      return array;
   }

   @Override
   public JsonNode visitEntityInstance( final EntityInstance instance, final ObjectNode context ) {
      final ObjectNode result = FACTORY.objectNode();
      for ( final Map.Entry<Property, Value> assertion : instance.getAssertions().entrySet() ) {
         final Property property = assertion.getKey();
         if ( property.isNotInPayload() ) {
            continue;
         }
         result.set( getSchemaNameForModelElement( property ), assertion.getValue().accept( this, context ) );
      }
      return result;
   }

   @Override
   public JsonNode visitEnumeration( final Enumeration enumeration, final ObjectNode context ) {
      final SAMM samm = new SAMM( enumeration.getMetaModelVersion() );
      final Type type = enumeration.getDataType().orElseThrow( () ->
            new DocumentGenerationException( "Characteristic " + enumeration + " is missing a dataType" ) );
      if ( type.is( Scalar.class ) ) {
         return createEnumNodeWithScalarValues( enumeration, type, context );
      }
      return createEnumNodeWithComplexValues( enumeration, samm );
   }

   private JsonNode createEnumNodeWithScalarValues( final Enumeration enumeration, final Type type, final ObjectNode context ) {
      final ArrayNode valuesNode = FACTORY.arrayNode();
      final ObjectNode enumNode = type.getUrn().equals( RDF.langString.getURI() )
            ? FACTORY.objectNode().put( "type", "object" )
            : (ObjectNode) type.accept( this, context );
      addSammExtensionAttribute( enumNode, enumeration );
      addDescription( enumNode, enumeration, locale );
      enumeration.getValues().stream()
            .map( value -> value.accept( this, enumNode ) )
            .forEach( valuesNode::add );
      enumNode.set( "enum", valuesNode );
      return enumNode;
   }

   private JsonNode createEnumNodeWithComplexValues( final Enumeration enumeration, final SAMM samm ) {
      final ObjectNode enumNode = FACTORY.objectNode();
      addDescription( enumNode, enumeration, locale );
      addSammExtensionAttribute( enumNode, enumeration );
      enumNode.put( "type", "object" );
      final ArrayNode enumValueReferences = FACTORY.arrayNode();
      enumeration.getValues().stream()
            .map( value -> value.as( EntityInstance.class ) )
            //Should the enum value entity be declared with only optional properties, enum values may be declared
            //without any properties. In such a case the value is ignored in the JSON schema since there is nothing
            //to validate
            .filter( value -> !value.getAssertions().isEmpty() )
            .forEach( value -> {
               final String schemaName = getSchemaNameForModelElement( value );
               enumValueReferences.add( FACTORY.objectNode().put( "$ref", "#/components/schemas/" + schemaName ) );
               final ObjectNode enumValueNode = createNodeForEnumEntityInstance( value, samm );
               setNodeInRootSchema( enumValueNode, schemaName );
            } );
      if ( !enumValueReferences.isEmpty() ) {
         enumNode.set( "oneOf", enumValueReferences );
      }
      return enumNode;
   }

   private ObjectNode createNodeForEnumEntityInstance( final EntityInstance entityInstance, final SAMM samm ) {
      final ObjectNode enumEntityInstanceNode = FACTORY.objectNode();
      final ArrayNode required = FACTORY.arrayNode();
      final ObjectNode properties = FACTORY.objectNode();
      addDescription( enumEntityInstanceNode, entityInstance, locale );
      enumEntityInstanceNode.put( "type", "object" );

      final Entity entity = entityInstance.getEntityType();
      entity.getProperties().stream()
            .filter( property -> !property.isNotInPayload() )
            .filter( property -> entityInstance.getAssertions().containsKey( property ) )
            .forEach( property -> {
               required.add( property.getPayloadName() );
               properties.set( property.getPayloadName(), createNodeForEnumEntityPropertyInstance( property, entityInstance, samm ) );
            } );

      enumEntityInstanceNode.set( "properties", properties );
      enumEntityInstanceNode.set( "required", required );
      return enumEntityInstanceNode;
   }

   @SuppressWarnings( { "squid:S3655" } )
   //squid S3655 - Properties with an Enumeration Characteristic always have a data type
   private JsonNode createNodeForEnumEntityPropertyInstance( final Property property, final EntityInstance entityInstance,
         final SAMM samm ) {
      final Characteristic characteristic = property.getCharacteristic().orElseThrow( () ->
            new DocumentGenerationException( "Property " + property + " has no Characteristic" ) );
      final Type type = property.getDataType().orElseThrow( () ->
            new DocumentGenerationException( "Characteristic " + characteristic + " has no data type" ) );
      final Value valueForProperty = entityInstance.getAssertions().get( property );
      final AspectModelJsonSchemaVisitor.JsonType schemaType = type.is( Scalar.class )
            ? getSchemaTypeForAspectType( ResourceFactory.createResource( type.getUrn() ) )
            : JsonType.OBJECT;

      if ( characteristic.is( SingleEntity.class ) ) {
         return createNodeForEnumEntityInstance( valueForProperty.as( EntityInstance.class ), samm );
      }
      if ( characteristic.is( Collection.class ) ) {
         final ObjectNode propertyInstanceNode = FACTORY.objectNode();
         addDescription( propertyInstanceNode, property, locale );
         propertyInstanceNode.put( "type", "array" );
         final ObjectNode arrayPropertyInstanceNode = FACTORY.objectNode();
         arrayPropertyInstanceNode.set( "type", schemaType.toJsonNode() );
         final ArrayNode arrayValues = (ArrayNode) valueForProperty.accept( this, propertyInstanceNode );
         propertyInstanceNode.put( "minItems", arrayValues.size() );
         propertyInstanceNode.put( "maxItems", arrayValues.size() );
         arrayPropertyInstanceNode.set( "enum", arrayValues );
         propertyInstanceNode.set( "items", arrayPropertyInstanceNode );
         return propertyInstanceNode;
      }
      final ObjectNode propertyInstanceNode = FACTORY.objectNode();
      addDescription( propertyInstanceNode, property, locale );
      propertyInstanceNode.set( "type", schemaType.toJsonNode() );
      propertyInstanceNode.set( "enum", FACTORY.arrayNode().add( valueForProperty.accept( this, propertyInstanceNode ) ) );
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

   private void addSammExtensionAttribute( final ObjectNode node, final NamedElement describedElement ) {
      describedElement.getAspectModelUrn().ifPresent( urn -> node.put( SAMM_EXTENSION, urn.toString() ) );
   }
}
