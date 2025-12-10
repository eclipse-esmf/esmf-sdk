/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.esmf.aspectmodel.generator.JsonGenerator;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.BoundDefinition;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.StructureElement;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.characteristic.Either;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.characteristic.State;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.constraint.FixedPointConstraint;
import org.eclipse.esmf.metamodel.constraint.LengthConstraint;
import org.eclipse.esmf.metamodel.constraint.RangeConstraint;
import org.eclipse.esmf.metamodel.constraint.RegularExpressionConstraint;
import org.eclipse.esmf.metamodel.datatype.Curie;
import org.eclipse.esmf.metamodel.datatype.CurieType;
import org.eclipse.esmf.metamodel.datatype.LangString;
import org.eclipse.esmf.metamodel.datatype.SammType;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.curiousoddman.rgxgen.RgxGen;
import org.apache.jena.vocabulary.RDF;

/**
 * Generator for random JSON payloads corresponding to a given StructureElement (e.g., {@link Aspect} or {@link Entity}).
 *
 * @param <S> the element type
 */
public class JsonPayloadGenerator<S extends StructureElement>
      extends JsonGenerator<S, JsonPayloadGenerationConfig, JsonNode, JsonPayloadArtifact>
      implements AspectVisitor<JsonNode, JsonPayloadGenerator.Context> {
   public static final JsonPayloadGenerationConfig DEFAULT_CONFIG = JsonPayloadGenerationConfigBuilder.builder().build();
   private static final float EPSILON = .0001f;

   public JsonPayloadGenerator( final S element, final JsonPayloadGenerationConfig config ) {
      super( element, config );
   }

   public JsonPayloadGenerator( final S element ) {
      this( element, DEFAULT_CONFIG );
   }

   @Override
   public Stream<JsonPayloadArtifact> generate() {
      final StructureElement element = structureElement();
      final JsonNode json = element.accept( this, new Context() );
      return Stream.of( new JsonPayloadArtifact( element.urn().toString(), json ) );
   }

   public record Context( List<Constraint> constraints, Set<Property> visitedProperties ) {
      private Context() {
         this( List.of(), new HashSet<>() );
      }

      public Context withConstraints( final List<Constraint> constraints ) {
         return new Context( constraints, visitedProperties() );
      }
   }

   private record Range( BigDecimal min, BigDecimal max ) {
      static final Range OPEN = new Range( null, (BigDecimal) null );

      Range( final Double min, final Double max ) {
         this( min == null ? null : BigDecimal.valueOf( min ), max == null ? null : BigDecimal.valueOf( max ) );
      }

      Range( final Long min, final Long max ) {
         this( min == null ? null : BigDecimal.valueOf( min ), max == null ? null : BigDecimal.valueOf( max ) );
      }

      Range( final Integer min, final Integer max ) {
         this( min == null ? null : BigDecimal.valueOf( min ), max == null ? null : BigDecimal.valueOf( max ) );
      }

      Range( final BigInteger min, final BigInteger max ) {
         this( min == null ? null : new BigDecimal( min ), max == null ? null : new BigDecimal( max ) );
      }

      Range merge( final Range other ) {
         final BigDecimal newMin;
         final BigDecimal newMax;
         if ( min == null && other.min == null ) {
            newMin = null;
         } else if ( min == null ) {
            newMin = other.min;
         } else if ( other.min == null ) {
            newMin = min;
         } else {
            newMin = min.max( other.min );
         }

         if ( max == null && other.max == null ) {
            newMax = null;
         } else if ( max == null ) {
            newMax = other.max;
         } else if ( other.max == null ) {
            newMax = max;
         } else {
            newMax = max.min( other.max );
         }
         if ( newMin != null && newMax != null && newMin.compareTo( newMax ) > 0 ) {
            // This happens if the range to merge is disjunct to this range.
            // In this case, ignore the range to merge.
            return this;
         }
         return new Range( newMin, newMax );
      }

      Range clamp( final Long min, final Long max ) {
         return merge( new Range( min, max ) );
      }

      Range clamp( final Integer min, final Integer max ) {
         return merge( new Range( min, max ) );
      }

      Range clamp( final Optional<BigInteger> min, final Optional<BigInteger> max ) {
         return merge( new Range( min.orElse( null ), max.orElse( null ) ) );
      }

      private static BigDecimal getScalarValue( final ScalarValue value ) {
         return value.getValue() instanceof final BigDecimal bigDecimal
               ? bigDecimal
               : new BigDecimal( value.getValue().toString() );
      }

      static Range fromLengthConstraints( final List<Constraint> constraints ) {
         return constraints.stream()
               .filter( constraint -> constraint.is( LengthConstraint.class ) )
               .map( constraint -> constraint.as( LengthConstraint.class ) )
               .map( lengthConstraint -> new Range( lengthConstraint.getMinValue().orElse( null ),
                     lengthConstraint.getMaxValue().orElse( null ) ) )
               .reduce( Range.OPEN, Range::merge );
      }

      static Range fromRangeConstraints( final List<Constraint> constraints, final boolean floatingPoint ) {
         return constraints.stream()
               .<Optional<Range>> map( constraint -> {
                  if ( constraint instanceof final RangeConstraint rangeConstraint ) {
                     if ( floatingPoint ) {
                        final Optional<Double> min = rangeConstraint.getMinValue()
                              .map( value -> getScalarValue( value ).doubleValue() )
                              .map( value -> BoundDefinition.GREATER_THAN.equals( rangeConstraint.getLowerBoundDefinition() )
                                    ? value + EPSILON : value );
                        final Optional<Double> max = rangeConstraint.getMaxValue()
                              .map( value -> getScalarValue( value ).doubleValue() )
                              .map( value -> BoundDefinition.LESS_THAN.equals( rangeConstraint.getLowerBoundDefinition() )
                                    ? value - EPSILON : value );
                        return Optional.of( new Range( min.orElse( null ), max.orElse( null ) ) );
                     } // else
                     final Optional<BigDecimal> min = rangeConstraint.getMinValue()
                           .map( Range::getScalarValue )
                           .map( value -> BoundDefinition.GREATER_THAN.equals( rangeConstraint.getLowerBoundDefinition() )
                                 ? value.add( BigDecimal.ONE ) : value );
                     final Optional<BigDecimal> max = rangeConstraint.getMaxValue()
                           .map( Range::getScalarValue )
                           .map( value -> BoundDefinition.LESS_THAN.equals( rangeConstraint.getLowerBoundDefinition() )
                                 ? value.add( BigDecimal.valueOf( -1L ) ) : value );
                     return Optional.of( new Range( min.orElse( null ), max.orElse( null ) ) );
                  }
                  return Optional.empty();
               } )
               .flatMap( Optional::stream )
               .reduce( Range.OPEN, Range::merge );
      }
   }

   @Override
   public JsonNode visitBase( final ModelElement modelElement, final Context context ) {
      throw new UnsupportedOperationException();
   }

   @Override
   public JsonNode visitStructureElement( final StructureElement structureElement, final Context context ) {
      final ObjectNode result = JsonNodeFactory.instance.objectNode();
      structureElement.getProperties().stream()
            .filter( property -> property.getCharacteristic().isPresent() )
            .forEach( property -> {
               final JsonNode propertyResult = property.accept( this, context );
               if ( propertyResult != null ) {
                  result.set( property.getPayloadName(), propertyResult );
               }
            } );
      return result;
   }

   @Override
   public JsonNode visitAspect( final Aspect aspect, final Context context ) {
      return visitStructureElement( aspect, context );
   }

   @Override
   public JsonNode visitEntity( final Entity entity, final Context context ) {
      final ObjectNode result = JsonNodeFactory.instance.objectNode();
      entity.getAllProperties().stream()
            .filter( Objects::nonNull )
            .filter( property -> property.getCharacteristic() != null && property.getCharacteristic().isPresent() )
            .forEach( property -> {
               final JsonNode propertyResult = property.accept( this, context );
               if ( propertyResult != null ) {
                  result.set( property.getPayloadName(), propertyResult );
               }
            } );
      if ( entity.getExtends().isPresent() && config.addTypeAttributeForEntityInheritance() ) {
         result.put( "@type", entity.getName() );
      }
      return result;
   }

   @Override
   public JsonNode visitEvent( final Event event, final Context context ) {
      return visitStructureElement( event, context );
   }

   @Override
   public JsonNode visitAbstractEntity( final AbstractEntity abstractEntity, final Context context ) {
      throw new IllegalArgumentException( "Invalid model: AbstractEntity " + abstractEntity.getUrn() + " is used as a datatype" );
   }

   @Override
   public JsonNode visitProperty( final Property property, final Context context ) {
      if ( property.isNotInPayload() ) {
         return null;
      }
      // For optional Properties, always generate a payload, except when the Property's optionality breaks a cycle in the model.
      // If this specific Property was already processed, it means there's a cycle.
      if ( property.isOptional() && context.visitedProperties().contains( property ) ) {
         return null;
      }
      context.visitedProperties().add( property );
      return property.getExampleValue()
            .map( exampleValue -> exampleValue.accept( this, context ) )
            .orElseGet( () -> property.getCharacteristic()
                  .map( c -> c.accept( this, context ) )
                  .orElse( null ) );
   }

   @Override
   public JsonNode visitCharacteristic( final Characteristic characteristic, final Context context ) {
      return characteristic.getDataType()
            .map( t -> t.accept( this, context ) )
            .orElse( null );
   }

   @Override
   public JsonNode visitCollection( final Collection collection, final Context context ) {
      // "type" of elements of the collection, i.e., scalar type or Characteristic
      final ModelElement collectionElementType =
            collection.getDataType()
                  .map( ModelElement.class::cast )
                  .or( collection::getElementCharacteristic )
                  .orElseThrow( () -> new IllegalArgumentException(
                        "Collection " + collection.getName() + " has neither dataType nor elementCharacteristic" ) );

      // Generate at least 1 and at least minValue elements
      final Range range = Range.fromLengthConstraints( context.constraints() );
      final int numberOfElements = Optional.ofNullable( range.min() ).map( BigDecimal::intValue ).orElse( 1 );
      if ( numberOfElements == 0 ) {
         // degenerate case
         return JsonNodeFactory.instance.arrayNode();
      }
      final ArrayNode result = JsonNodeFactory.instance.arrayNode( numberOfElements );
      final Context newContext = new Context();
      for ( int i = 0; i < numberOfElements; i++ ) {
         result.add( collectionElementType.accept( this, newContext ) );
      }
      return result;
   }

   @Override
   public JsonNode visitEither( final Either either, final Context context ) {
      final ObjectNode result = JsonNodeFactory.instance.objectNode();
      final Consumer<ObjectNode> consumer = randomValueOf(
            node -> node.set( SammNs.SAMMC.left().getLocalName(), either.getLeft().accept( this, context ) ),
            node -> node.set( SammNs.SAMMC.right().getLocalName(), either.getRight().accept( this, context ) )
      );
      consumer.accept( result );
      return result;
   }

   @Override
   public JsonNode visitEnumeration( final Enumeration enumeration, final Context context ) {
      return enumeration.getValues().getFirst().accept( this, context );
   }

   @Override
   public JsonNode visitState( final State state, final Context context ) {
      return state.getDefaultValue().accept( this, context );
   }

   @Override
   public JsonNode visitTrait( final Trait trait, final Context context ) {
      return trait.getBaseCharacteristic().accept( this, context.withConstraints( trait.getConstraints() ) );
   }

   @Override
   public JsonNode visitScalarValue( final ScalarValue scalarValue, final Context context ) {
      if ( scalarValue.getType().getUrn().equals( RDF.langString.getURI() ) ) {
         final LangString langString = (LangString) scalarValue.getValue();
         return JsonNodeFactory.instance.objectNode()
               .put( langString.getLanguageTag().toLanguageTag(), langString.getValue() );
      }
      if ( scalarValue.getValue() instanceof Curie( final String value ) ) {
         return JsonNodeFactory.instance.textNode( value );
      }
      return objectMapper.valueToTree( scalarValue.getValue() );
   }

   @Override
   public JsonNode visitCollectionValue( final CollectionValue collectionValue, final Context context ) {
      return JsonNodeFactory.instance.arrayNode().addAll(
            collectionValue.getValues().stream().map( value -> value.accept( this, context ) ).toList() );
   }

   @Override
   public JsonNode visitEntityInstance( final EntityInstance instance, final Context context ) {
      final ObjectNode result = JsonNodeFactory.instance.objectNode();
      instance.getEntityType().getAllProperties().stream()
            .filter( property -> !property.isNotInPayload() )
            .forEach( property -> {
               final Value propertyValue = instance.getAssertions().get( property );
               if ( propertyValue != null ) {
                  result.set( property.getPayloadName(), propertyValue.accept( this, context ) );
               }
            } );
      return result;
   }

   @Override
   public JsonNode visitScalar( final Scalar scalar, final Context context ) {
      final SammType<?> sammType = SammXsdType.ALL_TYPES.stream()
            .filter( type -> type.getUrn().equals( scalar.getUrn() ) )
            .findFirst()
            .orElseThrow( () -> new IllegalArgumentException( "Encountered unknown type: " + scalar.getUrn() ) );
      return sammType.accept( this, context );
   }

   @Override
   public JsonNode visitXsdBoolean( final SammType.XsdBoolean booleanType, final Context context ) {
      return JsonNodeFactory.instance.booleanNode( randomValueOf( true, false ) );
   }

   @Override
   public JsonNode visitXsdDecimal( final SammType.XsdDecimal decimal, final Context context ) {
      return visitXsdDouble( null, context );
   }

   @Override
   public JsonNode visitXsdInteger( final SammType.XsdInteger integerType, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( integerType.lowerBound(), integerType.upperBound() );
      final BigDecimal value = randomNumber( range.min(), range.max() );
      return JsonNodeFactory.instance.numberNode( value );
   }

   @Override
   public JsonNode visitXsdDouble( final SammType.XsdDouble doubleType, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), true )
            .clamp( -100000, 100000 );
      final Optional<FixedPointConstraint> fixedPointConstraint = context.constraints().stream()
            .filter( c -> c.is( FixedPointConstraint.class ) )
            .map( c -> c.as( FixedPointConstraint.class ) )
            .findFirst();
      final double value = randomFloatingPointNumber( range.min(), range.max(), fixedPointConstraint ).doubleValue();
      return JsonNodeFactory.instance.numberNode( value );
   }

   @Override
   public JsonNode visitXsdFloat( final SammType.XsdFloat floatType, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), true )
            .clamp( -100000, 100000 );
      final Optional<FixedPointConstraint> fixedPointConstraint = context.constraints().stream()
            .filter( c -> c.is( FixedPointConstraint.class ) )
            .map( c -> c.as( FixedPointConstraint.class ) )
            .findFirst();
      final float value = randomFloatingPointNumber( range.min(), range.max(), fixedPointConstraint ).floatValue();
      return JsonNodeFactory.instance.numberNode( value );
   }

   @Override
   public JsonNode visitXsdDate( final SammType.XsdDate date, final Context context ) {
      final LocalDateTime now = LocalDateTime.now();
      final XMLGregorianCalendar calendar = DatatypeFactory.newDefaultInstance()
            .newXMLGregorianCalendar( now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                  DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
                  DatatypeConstants.FIELD_UNDEFINED, 0 );
      return JsonNodeFactory.instance.textNode( calendar.toXMLFormat() );
   }

   @Override
   public JsonNode visitXsdTime( final SammType.XsdTime time, final Context context ) {
      final LocalDateTime now = LocalDateTime.now();
      final XMLGregorianCalendar calendar = DatatypeFactory.newDefaultInstance()
            .newXMLGregorianCalendar( DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
                  DatatypeConstants.FIELD_UNDEFINED,
                  now.getHour(), now.getMinute(), now.getSecond(), 0, 0 );
      return JsonNodeFactory.instance.textNode( calendar.toXMLFormat() );
   }

   @Override
   public JsonNode visitXsdDateTime( final SammType.XsdDateTime dateTime, final Context context ) {
      final LocalDateTime now = LocalDateTime.now();
      final XMLGregorianCalendar calendar = DatatypeFactory.newDefaultInstance()
            .newXMLGregorianCalendar( now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                  now.getHour(), now.getMinute(), now.getSecond(), 0, 0 );
      return JsonNodeFactory.instance.textNode( calendar.toXMLFormat() );
   }

   @Override
   public JsonNode visitXsdDateTimeStamp( final SammType.XsdDateTimeStamp dateTimeStamp, final Context context ) {
      return visitXsdDateTime( null, context );
   }

   @Override
   public JsonNode visitXsdGYear( final SammType.XsdGYear gYear, final Context context ) {
      return JsonNodeFactory.instance.textNode( "" + LocalDateTime.now().getYear() );
   }

   @Override
   public JsonNode visitXsdGMonth( final SammType.XsdGMonth gMonth, final Context context ) {
      return JsonNodeFactory.instance.textNode( "--%02d".formatted( LocalDateTime.now().getMonthValue() ) );
   }

   @Override
   public JsonNode visitXsdGDay( final SammType.XsdGDay gDay, final Context context ) {
      return JsonNodeFactory.instance.textNode( "---%02d".formatted( LocalDateTime.now().getDayOfMonth() ) );
   }

   @Override
   public JsonNode visitXsdGYearMonth( final SammType.XsdGYearMonth gYearMonth, final Context context ) {
      final LocalDateTime now = LocalDateTime.now();
      return JsonNodeFactory.instance.textNode( "%04d-%02d".formatted( now.getYear(), now.getMonthValue() ) );
   }

   @Override
   public JsonNode visitXsdGMonthDay( final SammType.XsdMonthDay monthDay, final Context context ) {
      final LocalDateTime now = LocalDateTime.now();
      return JsonNodeFactory.instance.textNode( "--%02d-%02d".formatted( now.getMonthValue(), now.getDayOfMonth() ) );
   }

   @Override
   public JsonNode visitXsdDuration( final SammType.XsdDuration duration, final Context context ) {
      return JsonNodeFactory.instance.textNode( "P%02dD".formatted( randomInt( 1, 10 ) ) );
   }

   @Override
   public JsonNode visitXsdYearMonthDuration( final SammType.XsdYearMonthDuration yearMonthDuration, final Context context ) {
      return JsonNodeFactory.instance.textNode( randomValueOf( "P10M", "P5Y2M" ) );
   }

   @Override
   public JsonNode visitXsdDayTimeDuration( final SammType.XsdDayTimeDuration dayTimeDuration, final Context context ) {
      return JsonNodeFactory.instance.textNode( randomValueOf( "P30D", "P1DT5H", "PT1H5M0S" ) );
   }

   @Override
   public JsonNode visitXsdByte( final SammType.XsdByte byteType, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( byteType.lowerBound(), byteType.upperBound() );
      final byte value = randomNumber( range.min(), range.max() ).byteValue();
      return JsonNodeFactory.instance.numberNode( value );
   }

   @Override
   public JsonNode visitXsdShort( final SammType.XsdShort shortType, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( shortType.lowerBound(), shortType.upperBound() );
      final short value = randomNumber( range.min(), range.max() ).shortValue();
      return JsonNodeFactory.instance.numberNode( value );
   }

   @Override
   public JsonNode visitXsdInt( final SammType.XsdInt intType, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( intType.lowerBound(), intType.upperBound() );
      final int value = randomNumber( range.min(), range.max() ).intValue();
      return JsonNodeFactory.instance.numberNode( value );
   }

   @Override
   public JsonNode visitXsdLong( final SammType.XsdLong longType, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( longType.lowerBound(), longType.upperBound() );
      final long value = randomNumber( range.min(), range.max() ).longValue();
      return JsonNodeFactory.instance.numberNode( value );
   }

   @Override
   public JsonNode visitXsdUnsignedByte( final SammType.XsdUnsignedByte unsignedByte, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( unsignedByte.lowerBound(), unsignedByte.upperBound() );
      final short value = randomNumber( range.min(), range.max() ).shortValue();
      return JsonNodeFactory.instance.numberNode( value );
   }

   @Override
   public JsonNode visitXsdUnsignedShort( final SammType.XsdUnsignedShort unsignedShort, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( unsignedShort.lowerBound(), unsignedShort.upperBound() );
      final int value = randomNumber( range.min(), range.max() ).intValue();
      return JsonNodeFactory.instance.numberNode( value );
   }

   @Override
   public JsonNode visitXsdUnsignedInt( final SammType.XsdUnsignedInt unsignedInt, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( unsignedInt.lowerBound(), unsignedInt.upperBound() );
      final long value = randomNumber( range.min(), range.max() ).longValue();
      return JsonNodeFactory.instance.numberNode( value );
   }

   @Override
   public JsonNode visitXsdUnsignedLong( final SammType.XsdUnsignedLong unsignedLong, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( unsignedLong.lowerBound(), unsignedLong.upperBound() );
      final BigInteger value = randomNumber( range.min(), range.max() ).toBigInteger();
      return JsonNodeFactory.instance.numberNode( value );
   }

   @Override
   public JsonNode visitXsdPositiveInteger( final SammType.XsdPositiveInteger positiveInteger, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( positiveInteger.lowerBound(), positiveInteger.upperBound() )
            .clamp( null, 100000L );
      final BigInteger value = randomNumber( range.min(), range.max() ).toBigInteger();
      return JsonNodeFactory.instance.numberNode( value );
   }

   @Override
   public JsonNode visitXsdNonNegativeInteger( final SammType.XsdNonNegativeInteger nonNegativeInteger, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( nonNegativeInteger.lowerBound(), nonNegativeInteger.upperBound() )
            .clamp( null, 100000L );
      final BigInteger value = randomNumber( range.min(), range.max() ).toBigInteger();
      return JsonNodeFactory.instance.numberNode( value );
   }

   @Override
   public JsonNode visitXsdNegativeInteger( final SammType.XsdNegativeInteger negativeInteger, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( negativeInteger.lowerBound(), negativeInteger.upperBound() )
            .clamp( -100000L, null );
      final BigInteger value = randomNumber( range.min(), range.max() ).toBigInteger();
      return JsonNodeFactory.instance.numberNode( value );
   }

   @Override
   public JsonNode visitXsdNonPositiveInteger( final SammType.XsdNonPositiveInteger nonPositiveInteger, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( nonPositiveInteger.lowerBound(), nonPositiveInteger.upperBound() )
            .clamp( -100000L, null );
      final BigInteger value = randomNumber( range.min(), range.max() ).toBigInteger();
      return JsonNodeFactory.instance.numberNode( value );
   }

   @Override
   public JsonNode visitXsdHexBinary( final SammType.XsdHexBinary hexBinary, final Context context ) {
      final Range range = Range.fromLengthConstraints( context.constraints() )
            .clamp( 1, 10 );
      final byte[] bytes = randomBytes( range.min().intValue(), range.max().intValue() );
      final String value = hexBinary.serialize( bytes );
      return JsonNodeFactory.instance.textNode( value );
   }

   @Override
   public JsonNode visitXsdBase64Binary( final SammType.XsdBase64Binary base64Binary, final Context context ) {
      final Range range = Range.fromLengthConstraints( context.constraints() )
            .clamp( 1, 10 );
      final byte[] bytes = randomBytes( range.min().intValue(), range.max().intValue() );
      final String value = base64Binary.serialize( bytes );
      return JsonNodeFactory.instance.textNode( value );
   }

   @Override
   public JsonNode visitXsdAnyUri( final SammType.XsdAnyUri anyUri, final Context context ) {
      final String value = "https://example.com/" + randomString( 5, 10 );
      return JsonNodeFactory.instance.textNode( value );
   }

   @Override
   public JsonNode visitCurieType( final CurieType curieType, final Context context ) {
      final String value = randomValueOf( "unit:kilometre", "unit:hectopascal", "unit:newton" );
      return JsonNodeFactory.instance.textNode( value );
   }

   @Override
   public JsonNode visitXsdString( final SammType.XsdString string, final Context context ) {
      final String value = context.constraints().stream()
            .flatMap( constraint -> {
               if ( constraint instanceof final RegularExpressionConstraint regularExpressionConstraint ) {
                  final RgxGen rgxGen = RgxGen.parse( regularExpressionConstraint.getValue() );
                  return Stream.of( rgxGen.generate( config.randomStrategy() ) );
               }
               return Stream.empty();
            } )
            .findFirst()
            .orElseGet( () -> {
               final Range range = Range.fromLengthConstraints( context.constraints() ).clamp( 1, 10 );
               return randomString( range.min().intValue(), range.max().intValue() );
            } );
      return JsonNodeFactory.instance.textNode( value );
   }

   @Override
   public JsonNode visitRdfLangString( final SammType.RdfLangString langString, final Context context ) {
      final ObjectNode result = JsonNodeFactory.instance.objectNode();
      final String languageTag = randomValueOf( "en", "de" );
      final JsonNode value = visitXsdString( null, context );
      result.set( languageTag, value );
      return result;
   }

   protected BigDecimal randomFloatingPointNumber( final BigDecimal start, final BigDecimal end,
         final Optional<FixedPointConstraint> fixedPointConstraint ) {
      return fixedPointConstraint.map( constraint -> {
         final int integerDigits = constraint.getInteger();
         final int scale = constraint.getScale();
         final int intMin = (int) Math.pow( 10, integerDigits - 1 );
         final int intMax = (int) Math.pow( 10, integerDigits ) - 1;
         final int integerPart = randomInt( intMin, intMax );

         if ( scale <= 0 ) {
            return BigDecimal.valueOf( integerPart );
         }
         final int scaleMin = (int) Math.pow( 10, scale - 1 );
         final int scaleMax = (int) Math.pow( 10, scale ) - 1;
         final int fractionalValue = randomInt( scaleMin, scaleMax );
         final double fractionalPart = fractionalValue / Math.pow( 10, scale );
         final double result = integerPart + fractionalPart;
         return BigDecimal.valueOf( result ).setScale( scale, RoundingMode.DOWN );
      } ).orElseGet( () -> randomNumber( start, end ) );
   }

   protected byte[] randomBytes( final int minLength, final int maxLength ) {
      return randomString( minLength, maxLength ).getBytes( StandardCharsets.UTF_8 );
   }

   protected String randomString( final int minLength, final int maxLength ) {
      final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
      final int length = randomInt( minLength, maxLength );
      final StringBuilder builder = new StringBuilder( length );
      for ( int i = 0; i < length; i++ ) {
         builder.append( characters.charAt( config.randomStrategy().nextInt( characters.length() ) ) );
      }
      return builder.toString();
   }

   protected BigDecimal randomNumber( final BigDecimal minInclusive, final BigDecimal maxInclusive ) {
      final BigDecimal min = minInclusive == null ? BigDecimal.valueOf( Long.MIN_VALUE ) : minInclusive;
      final BigDecimal max = maxInclusive == null ? BigDecimal.valueOf( Long.MAX_VALUE ) : maxInclusive;
      if ( min.equals( max ) ) {
         return min;
      }
      if ( min.compareTo( max ) >= 0 ) {
         throw new IllegalArgumentException( "Random range is inverted" );
      }
      final int numDigits = Math.max( min.precision(), max.precision() );
      final int numBits = (int) ( numDigits / Math.log10( 2.0 ) );
      // Factor will be between 0..1
      final BigDecimal factor = new BigDecimal( new BigInteger( numBits, config.randomStrategy() ) ).movePointLeft( numDigits );
      return min.add( max.subtract( min ).multiply( factor, new MathContext( numDigits ) ) );
   }

   protected int randomInt( final int startInclusive, final int endInclusive ) {
      if ( startInclusive == endInclusive ) {
         return startInclusive;
      }
      if ( startInclusive > endInclusive ) {
         throw new IllegalArgumentException( "Random range is inverted" );
      }
      return config.randomStrategy().nextInt( startInclusive, endInclusive == Integer.MAX_VALUE ? Integer.MAX_VALUE : endInclusive + 1 );
   }

   @SuppressWarnings( "FinalMethod" ) // Needs to be final for @SafeVarargs
   @SafeVarargs
   protected final <T> T randomValueOf( final T... values ) {
      if ( values.length == 0 ) {
         throw new IndexOutOfBoundsException();
      }
      return values[randomInt( 0, values.length - 1 )];
   }
}
