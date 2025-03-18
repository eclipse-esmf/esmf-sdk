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

package org.eclipse.esmf.aspectmodel.generator.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.esmf.aspectmodel.generator.ArtifactGenerator;
import org.eclipse.esmf.aspectmodel.generator.JsonGenerator;
import org.eclipse.esmf.aspectmodel.generator.NumericTypeTraits;
import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.BoundDefinition;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
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
import org.eclipse.esmf.metamodel.datatype.SammXsdType;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.impl.DefaultScalarValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.curiousoddman.rgxgen.RgxGen;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

public class AspectModelJsonPayloadGenerator extends JsonGenerator<JsonPayloadGenerationConfig, JsonNode, JsonPayloadArtifact> {
   public static final JsonPayloadGenerationConfig DEFAULT_CONFIG = JsonPayloadGenerationConfigBuilder.builder().build();

   public AspectModelJsonPayloadGenerator( final Aspect aspect ) {
      this( aspect, DEFAULT_CONFIG );
   }

   public AspectModelJsonPayloadGenerator( final Aspect aspect, final JsonPayloadGenerationConfig config ) {
      super( aspect, config );
   }

   @Override
   public Stream<JsonPayloadArtifact> generate() {
      return Stream.of( new PayloadGenerator( objectMapper ).apply( aspect(), config ) );
   }

   private class PayloadGenerator implements ArtifactGenerator<String, JsonNode, Aspect, JsonPayloadGenerationConfig, JsonPayloadArtifact> {
      /**
       * Constant for the either left property.
       * For example JSON payloads the left type will be used.
       */
      private static final String EITHER_LEFT = "left";

      private final ObjectMapper objectMapper;
      private final List<Transformer> transformers;
      private final List<Property> recursiveProperty;
      private final ValueToPayloadStructure valueToPayloadStructure = new ValueToPayloadStructure();
      private ExampleValueGenerator exampleValueGenerator = null;
      private Aspect aspect;

      private PayloadGenerator( final ObjectMapper objectMapper ) {
         this.objectMapper = objectMapper;
         transformers = Arrays.asList( this::transformCollectionProperty, this::transformEnumeration, this::transformEntityProperty,
               this::transformAbstractEntityProperty, this::transformEitherProperty, this::transformSimpleProperty );
         recursiveProperty = new LinkedList<>();
      }

      @Override
      public JsonPayloadArtifact apply( final Aspect aspect, final JsonPayloadGenerationConfig config ) {
         this.aspect = aspect;
         exampleValueGenerator = new ExampleValueGenerator( config.randomStrategy() );
         final JsonNode jsonNode = objectMapper.valueToTree( transformAspectProperties() );
         return new JsonPayloadArtifact( aspect.getName() + ".json", jsonNode );
      }

      private Map<String, Object> transformAspectProperties() {
         return transformProperties( aspect.getProperties(), true );
      }

      /**
       * @param properties the meta model properties to transform
       * @return a map representing the given properties
       */
      @SuppressWarnings( "squid:S2250" )
      // Amount of elements in list is in regard to amount of properties in Aspect Model. Even in bigger aspects this
      // should not lead to performance issues
      private Map<String, Object> transformProperties( final List<Property> properties, final boolean useModelExampleValue ) {
         return Stream.concat(
               properties.stream().filter( recursiveProperty::contains ).map( this::recursiveProperty ),
               properties.stream()
                     .filter( property -> !recursiveProperty.contains( property ) )
                     .filter( property -> !property.isAbstract() )
                     .map( property -> {
                        recursiveProperty.add( property );
                        final Map<String, Object> result = transformProperty( new BasicProperty( property ), useModelExampleValue );
                        recursiveProperty.remove( property );
                        return result;
                     } )
         ).collect( HashMap::new, HashMap::putAll, HashMap::putAll );
      }

      private Map<String, Object> recursiveProperty( final Property property ) {
         if ( !property.isNotInPayload() && !property.isOptional() ) {
            throw new IllegalArgumentException(
                  String.format( "Having a recursive Property: %s which is not Optional nor marked as NotInPayload is not valid.",
                        property ) );
         }
         return Map.of();
      }

      /**
       * A meta model {@link Property#getCharacteristic()} can represent different types.
       * This method recursively transforms the types to corresponding data structures or primitives each having the
       * {@link Property#getName()} as key. A {@link Collection} will be represented as a {@link List}. {@link Entity} and {@link Either}
       * will
       * be represented as {@link Map}. As such the returned map can contain 1 to n entries.
       *
       * @param property the property to transform
       * @return a map representing the property names as key and the property values as value
       */
      private Map<String, Object> transformProperty( final BasicProperty property, final boolean useModelExampleValue ) {
         return transformers.stream()
               .map( transformer -> transformer.apply( property, useModelExampleValue ) )
               .filter( propertiesMap -> !propertiesMap.isEmpty() )
               .findFirst()
               .orElseThrow( () -> new IllegalArgumentException( "No transformer for " + property.getName() + " available." ) );
      }

      private Map<String, Object> transformCollectionProperty( final BasicProperty property, final boolean useModelExampleValue ) {
         final Characteristic characteristic = property.getCharacteristic();
         if ( characteristic.is( Collection.class ) ) {
            final List<Object> collectionValues = getCollectionValues( property, (Collection) characteristic );
            return toMap( property.getName(), collectionValues );
         } else if ( isConstrainedCollection( characteristic ) ) {

            final Collection collection = characteristic.as( Trait.class ).getBaseCharacteristic().as( Collection.class );

            final List<Constraint> constraints = characteristic.as( Trait.class ).getConstraints().stream()
                  .filter( trait -> trait.is( LengthConstraint.class ) )
                  .toList();

            return !constraints.isEmpty()
                  ? toMap( property.getName(), getCollectionValues( property, collection,
                  (LengthConstraint) characteristic.as( Trait.class ).getConstraints().get( 0 ) ) )
                  : toMap( property.getName(), getCollectionValues( property, collection ) );
         }
         return ImmutableMap.of();
      }

      private boolean isConstrainedCollection( final Characteristic characteristic ) {
         if ( !characteristic.is( Trait.class ) ) {
            return false;
         }
         final Trait trait = characteristic.as( Trait.class );
         return trait.getBaseCharacteristic().is( Collection.class ) && ( !trait.getConstraints().isEmpty() );
      }

      private Map<String, Object> transformAbstractEntityProperty( final BasicProperty property, final boolean useModelExampleValue ) {
         final Optional<AbstractEntity> dataType = getForCharacteristic( property.getCharacteristic(), AbstractEntity.class );
         if ( dataType.isPresent() ) {
            final AbstractEntity abstractEntity = dataType.get();
            final ComplexType extendingComplexType = abstractEntity.getExtendingElements().get( 0 );
            final Map<String, Object> generatedProperties = transformProperties( extendingComplexType.getAllProperties(),
                  useModelExampleValue );
            if ( config.addTypeAttributeForEntityInheritance() ) {
               generatedProperties.put( "@type", extendingComplexType.getName() );
            }
            return toMap( property.getName(), generatedProperties );
         }
         return ImmutableMap.of();
      }

      private Map<String, Object> transformEntityProperty( final BasicProperty property, final boolean useModelExmplevalue ) {
         final Optional<Entity> dataType = getForCharacteristic( property.getCharacteristic(), Entity.class );
         if ( dataType.isPresent() ) {
            final Entity entity = dataType.get();
            final Map<String, Object> generatedProperties = transformProperties( entity.getAllProperties(), useModelExmplevalue );
            if ( entity.getExtends().isPresent() && config.addTypeAttributeForEntityInheritance() ) {
               generatedProperties.put( "@type", entity.getName() );
            }
            return toMap( property.getName(), generatedProperties );
         }
         return ImmutableMap.of();
      }

      private Map<String, Object> transformEnumeration( final BasicProperty property, final boolean useModelExampleValue ) {
         return getForCharacteristic( property.getCharacteristic(), Enumeration.class )
               .map( enumeration -> extractEnumerationValues( property, enumeration ) )
               .orElseGet( ImmutableMap::of );
      }

      private Map<String, Object> extractEnumerationValues( final BasicProperty property, final Enumeration enumeration ) {
         final Value firstValue = enumeration.getValues().get( 0 );
         return toMap( property.getName(), firstValue.accept( valueToPayloadStructure, null ) );
      }

      private Map<String, Object> transformEitherProperty( final BasicProperty property, final boolean useModelExampleValue ) {
         final Characteristic characteristic = property.getCharacteristic();
         return getForCharacteristic( characteristic, Either.class )
               .map( value -> transformProperty(
                     new BasicProperty( PayloadGenerator.EITHER_LEFT, value.getLeft(), Optional.empty() ),
                     useModelExampleValue ) )
               .map( value -> toMap( property.getName(), value ) )
               .orElseGet( ImmutableMap::of );
      }

      private Map<String, Object> transformSimpleProperty( final BasicProperty basicProperty, final boolean useModelExampleValue ) {
         return toMap( basicProperty.getName(), getExampleValueOrElseRandom( basicProperty, useModelExampleValue ) );
      }

      /**
       * @param property the property to transform
       * @return the {@link Property#getExampleValue()} or if absent a random value.
       */
      private Object getExampleValueOrElseRandom( final BasicProperty property, final boolean useModelExampleValue ) {
         final Characteristic characteristic = property.getCharacteristic();
         if ( characteristic.is( State.class ) ) {
            return characteristic.as( State.class ).getDefaultValue();
         }
         if ( characteristic.is( Enumeration.class ) ) {
            return characteristic.as( Enumeration.class ).getValues().get( 0 );
         }

         Optional<Characteristic> elementCharacteristics = Optional.empty();
         if ( characteristic.is( Collection.class ) ) {
            elementCharacteristics = ( (Collection) characteristic ).getElementCharacteristic();
         }
         final Characteristic effectiveCharacteristics = elementCharacteristics.orElse( characteristic );

         if ( !useModelExampleValue ) {
            return generateExampleValue( effectiveCharacteristics );
         }

         return property.getExampleValue()
               .map( exampleValue -> exampleValue.as( ScalarValue.class ).getValue() )
               .map( value -> value instanceof Curie ? ( (Curie) value ).value() : value )
               .orElseGet( () -> generateExampleValue( effectiveCharacteristics ) );
      }

      private Map<String, Object> toMap( final String key, final Object value ) {
         return ImmutableMap.of( key, value );
      }

      private List<Object> getCollectionValues( final BasicProperty property, final Collection collection ) {
         return getCollectionValues( property, collection, null );
      }

      private List<Object> getCollectionValues( final BasicProperty property, final Collection collection,
            final LengthConstraint lengthConstraint ) {
         final Type dataType = collection.getDataType()
               .orElseThrow( () -> new IllegalArgumentException( "DataType for collection is required." ) );

         if ( dataType.is( Scalar.class ) ) {
            final Object payload = getExampleValueOrElseRandom( property, lengthConstraint == null );
            return payload instanceof List ? (List) payload : ImmutableList.of( payload );
         }

         final BigInteger minLength = lengthConstraint == null ? BigInteger.ONE : lengthConstraint.getMinValue().orElse( BigInteger.ONE );
         final List<Object> returnValues = new ArrayList<>();
         // Fill in minLength elements
         for ( int i = 0; i < minLength.intValue(); i++ ) {
            returnValues.add( generateCollectionValue( dataType, minLength.intValue() ) );
         }
         return returnValues;
      }

      private Object generateCollectionValue( final Type dataType, final int minCount ) {
         if ( dataType.is( AbstractEntity.class ) ) {
            final AbstractEntity abstractEntity = dataType.as( AbstractEntity.class );
            final ComplexType extendingComplexType = abstractEntity.getExtendingElements().get( 0 );
            final Map<String, Object> propertyValueMap = transformProperties( extendingComplexType.getAllProperties(), minCount < 2 );
            if ( config.addTypeAttributeForEntityInheritance() ) {
               propertyValueMap.put( "@type", extendingComplexType.getName() );
            }
            return propertyValueMap;
         }
         if ( dataType.is( Entity.class ) ) {
            final Entity entity = dataType.as( Entity.class );
            return transformProperties( entity.getAllProperties(), minCount < 2 );
         }
         throw new IllegalArgumentException( String.format( "DataType %s is unknown", dataType ) );
      }

      private <T> Optional<T> getForCharacteristic( final Characteristic characteristic, final Class<T> type ) {
         if ( type.isAssignableFrom( characteristic.getClass() ) ) {
            return Optional.of( type.cast( characteristic ) );
         }
         return characteristic.getDataType()
               .filter( dataType -> type.isAssignableFrom( dataType.getClass() ) )
               .map( type::cast );
      }

      private Object generateExampleValue( final Characteristic characteristic ) {
         return exampleValueGenerator.generateExampleValue( characteristic );
      }

      /**
       * Transforms an {@link BasicProperty} to a map.
       * If no transformation can be applied, an empty map will returned.
       */
      private interface Transformer extends BiFunction<BasicProperty, Boolean, Map<String, Object>> {
      }

      private static class BasicProperty {
         private final String name;
         private final Characteristic characteristic;
         private final Optional<ScalarValue> exampleValue;

         BasicProperty( final Property property ) {
            this(
                  property.getPayloadName(),
                  property.getCharacteristic().orElseThrow( () ->
                        new IllegalArgumentException( "Could not process Property " + property )
                  ),
                  processExampleValue( property.getExampleValue() )
            );
         }

         BasicProperty( final String name, final Characteristic characteristic, final Optional<ScalarValue> exampleValue ) {
            this.name = name;
            this.characteristic = characteristic;
            this.exampleValue = exampleValue;
         }

         public String getName() {
            return name;
         }

         public Characteristic getCharacteristic() {
            return characteristic;
         }

         public Optional<ScalarValue> getExampleValue() {
            return exampleValue;
         }

         private static Optional<ScalarValue> processExampleValue( Optional<ScalarValue> exampleValue ) {
            return exampleValue.map( value -> {
               if ( value instanceof ScalarValue scalarValue ) {
                  return scalarValue;
               } else if ( value instanceof Value valueInstance ) {
                  return convertToScalarValue( valueInstance );
               } else {
                  throw new IllegalArgumentException( "Unexpected exampleValue type: " + value.getClass() );
               }
            } );
         }

         private static ScalarValue convertToScalarValue( Value value ) {
            if ( value instanceof ScalarValue scalarValue ) {
               return scalarValue;
            }

            return new DefaultScalarValue(
                  MetaModelBaseAttributes.builder()
                        .withUrn( value.urn() )
                        .withPreferredNames( value.getPreferredNames() )
                        .withDescriptions( value.getDescriptions() )
                        .withSee( value.getSee() )
                        .isAnonymous( value.isAnonymous() )
                        .withSourceFile( value.getSourceFile() ).build(),
                  ( ( ScalarValue ) value).getValue(),
                  new DefaultScalar( value.getType().toString() )
            );
         }
      }
   }

   private static class ExampleValueGenerator {
      private static final double THRESHOLD = .0001;
      private final EasyRandom defaultEasyRandom = new EasyRandom();
      private final Random random;

      private static final List<String> CURIE_VALUES = ImmutableList.of( "unit:hectopascal" );

      private final Map<Class<?>, BiFunction<Number, Number, Number>> generators = Map.of(
            Byte.class, ( min, max ) -> getRandomInteger( min.intValue(), max.intValue() ),
            Short.class, ( min, max ) -> getRandomInteger( min.intValue(), max.intValue() ),
            Integer.class, ( min, max ) -> getRandomInteger( min.intValue(), max.intValue() ),
            Long.class, ( min, max ) -> getRandomLong( min.longValue(), max.longValue() ),
            Float.class, ( min, max ) -> getRandomDouble( min.floatValue(), max.floatValue() ).floatValue(),
            BigInteger.class,
            ( min, max ) -> BigDecimal.valueOf( getRandomDouble( safelyNarrowDown( min ), safelyNarrowDown( max ) ) ).toBigInteger(),
            BigDecimal.class, ( min, max ) -> BigDecimal.valueOf( getRandomDouble( safelyNarrowDown( min ), safelyNarrowDown( max ) ) )
      );

      private ExampleValueGenerator( final Random random ) {
         this.random = random;
      }

      private Type getDataType( final Characteristic characteristic ) {
         return characteristic.getDataType()
               .orElseThrow( () -> new IllegalArgumentException(
                     "DataType for characteristic " + characteristic.getName() + " is required." ) );
      }

      private Object generateExampleValue( final Characteristic characteristic ) {
         final Type dataType = getDataType( characteristic );

         if ( !( dataType.is( Scalar.class ) ) ) {
            throw new IllegalArgumentException( "Example values can only be generated for scalar types." );
         }

         if ( RDF.langString.getURI().equals( dataType.getUrn() ) ) {
            return ImmutableMap.of( Locale.ENGLISH.toLanguageTag(), "Example multi language string" );
         }

         final Scalar scalar = dataType.as( Scalar.class );
         final Resource dataTypeResource = ResourceFactory.createResource( scalar.getUrn() );
         final Class<?> exampleValueType = SammXsdType.getJavaTypeForMetaModelType( dataTypeResource );
         if ( Curie.class.equals( exampleValueType ) ) {
            return getRandomEntry( ExampleValueGenerator.CURIE_VALUES );
         }
         if ( XMLGregorianCalendar.class.equals( exampleValueType ) ) {
            return getGregorianRandomValue( dataType );
         }
         if ( Duration.class.equals( exampleValueType ) ) {
            return DatatypeFactory.newDefaultInstance().newDuration( defaultEasyRandom.nextLong() );
         }
         if ( byte[].class.equals( exampleValueType ) ) {
            return getBinaryRandomValue( dataType );
         }

         if ( characteristic.is( Trait.class ) ) {
            final Optional<Object> traitExampleValue = generateExampleValueForTrait( characteristic.as( Trait.class ), exampleValueType,
                  dataTypeResource );
            if ( traitExampleValue.isPresent() ) {
               return traitExampleValue.get();
            }
         }

         if ( Number.class.isAssignableFrom( exampleValueType ) ) {
            final Number min = NumericTypeTraits.getModelMinValue( dataTypeResource, exampleValueType );
            final Number max = NumericTypeTraits.getModelMaxValue( dataTypeResource, exampleValueType );
            return generateForNumericTypeInRange( exampleValueType, min, max );
         }

         return defaultEasyRandom.nextObject( exampleValueType );
      }

      private Optional<Object> generateExampleValueForTrait( final Trait trait, final Class<?> exampleValueType,
            final Resource dataTypeResource ) {
         for ( final Constraint constraint : trait.getConstraints() ) {
            if ( constraint.is( LengthConstraint.class ) ) {
               return Optional.of(
                     getRandomValue( constraint.as( LengthConstraint.class ), exampleValueType, trait.getBaseCharacteristic() ) );
            }
            if ( constraint.is( RangeConstraint.class ) ) {
               return Optional.of( getRandomValue( constraint.as( RangeConstraint.class ), exampleValueType, dataTypeResource ) );
            }
            if ( constraint.is( RegularExpressionConstraint.class ) ) {
               return Optional.of( getRandomValue( constraint.as( RegularExpressionConstraint.class ) ) );
            }
            if ( constraint.is( FixedPointConstraint.class ) ) {
               return Optional.of( getRandomValue( constraint.as( FixedPointConstraint.class ) ) );
            }
         }
         return Optional.empty();
      }

      private Object getBinaryRandomValue( final Type dataType ) {
         final byte[] value = defaultEasyRandom.nextObject( String.class ).getBytes( StandardCharsets.UTF_8 );
         return dataType.getUrn().equals( XSD.base64Binary.getURI() )
               ? Base64.getEncoder().encodeToString( value )
               : HexFormat.of().formatHex( value );
      }

      private Object getGregorianRandomValue( final Type dataType ) {
         final XMLGregorianCalendar randomCalendar = DatatypeFactory.newDefaultInstance()
               .newXMLGregorianCalendar( new GregorianCalendar() );
         final Optional<DateFormat> dateFormat = getDateFormat( dataType.getUrn() );
         if ( dateFormat.isPresent() ) {
            final Date date = randomCalendar.toGregorianCalendar().getTime();
            return dateFormat.get().format( date );
         }
         return randomCalendar;
      }

      private Optional<DateFormat> getDateFormat( final String urn ) {
         if ( XSD.gDay.getURI().equals( urn ) ) {
            return Optional.of( new SimpleDateFormat( "---dd" ) );
         }
         if ( XSD.gMonth.getURI().equals( urn ) ) {
            return Optional.of( new SimpleDateFormat( "--MM" ) );
         }
         if ( XSD.gYear.getURI().equals( urn ) ) {
            return Optional.of( new SimpleDateFormat( "yyyy" ) );
         }
         if ( XSD.gYearMonth.getURI().equals( urn ) ) {
            return Optional.of( new SimpleDateFormat( "yyyy-MM" ) );
         }
         if ( XSD.gMonthDay.getURI().equals( urn ) ) {
            return Optional.of( new SimpleDateFormat( "--MM-dd" ) );
         }
         if ( XSD.duration.getURI().equals( urn ) ) {
            return Optional.of( new SimpleDateFormat( "PddD" ) );
         }
         if ( XSD.date.getURI().equals( urn ) ) {
            return Optional.of( new SimpleDateFormat( "yyyy-MM-dd" ) );
         }
         return Optional.empty();
      }

      @SuppressWarnings( "unchecked" )
      private Object getRandomValue( final LengthConstraint lengthConstraint, final java.lang.reflect.Type exampleValueType,
            final Characteristic traitBaseCharacteristic ) {
         if ( traitBaseCharacteristic.is( Collection.class ) ) {
            return getRandomValues( lengthConstraint, () -> defaultEasyRandom.nextObject( (Class<?>) exampleValueType ) );
         }

         final BigInteger maxLength = lengthConstraint.getMaxValue().orElse( BigInteger.valueOf( 30 ) );
         final BigInteger minLength = lengthConstraint.getMinValue().orElse( BigInteger.ZERO );
         final EasyRandomParameters easyRandomParameters = new EasyRandomParameters().stringLengthRange( minLength.intValue(),
               maxLength.intValue() );
         final EasyRandom easyRandom = new EasyRandom( easyRandomParameters );
         return easyRandom.nextObject( (Class<?>) exampleValueType );
      }

      public List<Object> getRandomValues( final LengthConstraint lengthConstraint, final Supplier<Object> valueGenerator ) {
         final BigInteger maxLength =
               lengthConstraint == null
                     ? BigInteger.ONE
                     : lengthConstraint.getMaxValue().orElse( BigInteger.valueOf( Integer.MAX_VALUE ) );
         final BigInteger minLength =
               lengthConstraint == null ? BigInteger.ONE : lengthConstraint.getMinValue().orElse( BigInteger.ZERO );

         final List<Object> returnValues = new ArrayList<>();
         // Fill in minLength elements
         for ( int i = 0; i < minLength.intValue(); i++ ) {
            returnValues.add( valueGenerator.get() );
         }
         if ( minLength.intValue() == maxLength.intValue() ) {
            return returnValues;
         }
         // Add between minLength and maxLength-minLength elements, but not more than 5
         final int amount = getRandomInteger( minLength.intValue(), Math.min( maxLength.intValue() - minLength.intValue(), 5 ) );
         for ( int i = 0; i < amount; i++ ) {
            returnValues.add( valueGenerator.get() );
         }
         return returnValues;
      }

      private Number getRandomValue( final RangeConstraint rangeConstraint, final java.lang.reflect.Type valueType,
            final Resource dataTypeResource ) {
         final Number min = calculateLowerBound( rangeConstraint, valueType, dataTypeResource );
         final Number max = calculateUpperBound( rangeConstraint, valueType, dataTypeResource );
         return generateForNumericTypeInRange( valueType, min, max );
      }

      public Number getRandomValue( final FixedPointConstraint fixedPointConstraint ) {

         final int integerDigits = fixedPointConstraint.getInteger();
         final int scale = fixedPointConstraint.getScale();

         final int integerPart = getRandomInteger( integerDigits );

         if ( scale > 0 ) {
            final double fractionalPart = getRandomDouble( scale );

            final double result = integerPart + fractionalPart;

            final BigDecimal roundedResult = BigDecimal.valueOf( result )
                  .setScale( scale, RoundingMode.DOWN );
            return roundedResult.doubleValue();
         } else {
            return integerPart;
         }
      }

      private Number generateForNumericTypeInRange( final java.lang.reflect.Type valueType, final Number min, final Number max ) {
         return generators
               .getOrDefault( valueType, ( low, high ) -> getRandomDouble( low.doubleValue(), high.doubleValue() ) )
               .apply( min, max );
      }

      // narrowing conversion from BigDecimal to double
      private double safelyNarrowDown( final Number bound ) {
         if ( !( BigDecimal.class.equals( bound.getClass() ) ) ) {
            return bound.doubleValue();
         }

         // We have to be extremely cautious with BigDecimal bounds. Because of the limited precision
         // of the "double" type (15-17 significant digits) we might jump over the bound while rounding.
         // Example: xsd:unsignedLong has a max. value of 18446744073709551615; when converting it to double
         // it will get represented as 1.8446744073709552E16, thereby exceeding the upper bound.
         // Therefore we need to take care of always rounding down when narrowing to double.
         final BigDecimal narrowed = ( (BigDecimal) bound ).round( new MathContext( 15, RoundingMode.DOWN ) );
         return narrowed.doubleValue();
      }

      private Number calculateLowerBound( final RangeConstraint rangeConstraint, final java.lang.reflect.Type valueType,
            final Resource dataTypeResource ) {
         return rangeConstraint.getMinValue()
               .map( ScalarValue::getValue )
               .map( value -> (Number) value )
               // exclusive lower bound?
               .map( value -> BoundDefinition.GREATER_THAN
                     .equals( rangeConstraint.getLowerBoundDefinition() )
                     ? NumericTypeTraits.polymorphicAdd( value, exclusiveBoundDelta( valueType ) )
                     : value )
               .orElse( NumericTypeTraits.getModelMinValue( dataTypeResource, valueType ) );
      }

      private Number calculateUpperBound( final RangeConstraint rangeConstraint, final java.lang.reflect.Type valueType,
            final Resource dataTypeResource ) {
         return rangeConstraint.getMaxValue()
               .map( ScalarValue::getValue )
               .map( value -> (Number) value )
               // exclusive upper bound?
               .map( value -> BoundDefinition.LESS_THAN
                     .equals( rangeConstraint.getUpperBoundDefinition() )
                     ? NumericTypeTraits.polymorphicAdd( value, -exclusiveBoundDelta( valueType ) )
                     : value )
               .orElse( NumericTypeTraits.getModelMaxValue( dataTypeResource, valueType ) );
      }

      // with floating point values we cannot simply use 1 (the range could be defined as 2.4 - 2.9, both exclusive)
      private float exclusiveBoundDelta( final java.lang.reflect.Type valueType ) {
         return NumericTypeTraits.isFloatingPointNumberType( valueType ) ? (float) THRESHOLD : 1.0f;
      }

      private Object getRandomValue( final RegularExpressionConstraint rangeConstraint ) {
         final RgxGen rgxGen = RgxGen.parse( rangeConstraint.getValue() );
         return rgxGen.generate();
      }

      private Double getRandomDouble( final double min, final double max ) {
         if ( Math.abs( min - max ) < THRESHOLD || Math.abs( min - max ) == Double.POSITIVE_INFINITY ) {
            return min;
         }
         //noinspection OptionalGetWithoutIsPresent
         return random.doubles( 1, min, max ).findFirst().getAsDouble();
      }

      private Double getRandomDouble( final int scale ) {
         final int min = (int) Math.pow( 10, scale - 1 );
         final int max = (int) Math.pow( 10, scale ) - 1;

         final int fractionalValue = getRandomInteger( min, max );

         return fractionalValue / Math.pow( 10, scale );
      }

      private Integer getRandomInteger( final int min, final int max ) {
         if ( min == max ) {
            return min;
         }
         //noinspection OptionalGetWithoutIsPresent
         return random.ints( 1, min, max ).findFirst().getAsInt();
      }

      /**
       * Generates a random integer with the exact specified number of digits.
       *
       * @param countOfDigits The number of digits the generated integer should have. Must be greater than 0.
       * @return A random integer with exactly {@code countOfDigits} digits.
       * For example, if {@code countOfDigits} is 3, the result will be
       * a number between 100 and 999 inclusive.
       * @throws IllegalArgumentException If {@code countOfDigits} is less than 1.
       */
      private int getRandomInteger( final int countOfDigits ) {
         final int min = (int) Math.pow( 10, countOfDigits - 1 );
         final int max = (int) Math.pow( 10, countOfDigits ) - 1;
         return getRandomInteger( min, max );
      }

      // We need a Long generator too, because with a RangeConstraint set to Long bounds
      // we might not be able to generate legal values with just the Integer generator.
      private Long getRandomLong( final long min, final long max ) {
         if ( min == max ) {
            return min;
         }
         //noinspection OptionalGetWithoutIsPresent
         return random.longs( 1, min, max ).findFirst().getAsLong();
      }

      private <T> T getRandomEntry( final List<T> entries ) {
         return entries.get( random.nextInt( entries.size() ) );
      }
   }
}
