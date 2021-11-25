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

package io.openmanufacturing.sds.aspectmodel.generator.json;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.curiousoddman.rgxgen.RgxGen;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.openmanufacturing.sds.aspectmodel.generator.AbstractGenerator;
import io.openmanufacturing.sds.aspectmodel.generator.NumericTypeTraits;
import io.openmanufacturing.sds.aspectmodel.jackson.AspectModelJacksonModule;
import io.openmanufacturing.sds.aspectmodel.resolver.services.DataType;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.metamodel.AbstractEntity;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Collection;
import io.openmanufacturing.sds.metamodel.ComplexType;
import io.openmanufacturing.sds.metamodel.Constraint;
import io.openmanufacturing.sds.metamodel.Either;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Enumeration;
import io.openmanufacturing.sds.metamodel.LengthConstraint;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.RangeConstraint;
import io.openmanufacturing.sds.metamodel.RegularExpressionConstraint;
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.State;
import io.openmanufacturing.sds.metamodel.Trait;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.datatypes.Curie;
import io.openmanufacturing.sds.metamodel.impl.BoundDefinition;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import io.vavr.Tuple2;

public class AspectModelJsonPayloadGenerator extends AbstractGenerator {
   /**
    * Constant for the either left property.
    * For example JSON payloads the left type will be used.
    */
   private static final String EITHER_LEFT = "left";
   private static final double THRESHOLD = .0001;

   private final Aspect aspect;
   private final List<Transformer> transformers;
   private final ExampleValueGenerator exampleValueGenerator;
   private final ObjectMapper objectMapper;
   private final BAMM bamm;
   private final List<Property> recursiveProperty;

   public AspectModelJsonPayloadGenerator( final Aspect aspect ) {
      this( aspect, new BAMM( aspect.getMetaModelVersion() ), new Random() );
   }

   public AspectModelJsonPayloadGenerator( final VersionedModel versionedModel ) {
      this( AspectModelLoader.fromVersionedModelUnchecked( versionedModel ) );
   }

   public AspectModelJsonPayloadGenerator( final Aspect aspect, final Random randomStrategy ) {
      this( aspect, new BAMM( aspect.getMetaModelVersion() ), randomStrategy );
   }

   private AspectModelJsonPayloadGenerator( final Aspect aspect, final BAMM bamm, final Random randomStrategy ) {
      this.aspect = aspect;
      this.bamm = bamm;
      exampleValueGenerator = new ExampleValueGenerator( randomStrategy );
      objectMapper = AspectModelJsonPayloadGenerator.createObjectMapper();
      objectMapper.configure( SerializationFeature.FAIL_ON_EMPTY_BEANS, false );
      transformers = Arrays
            .asList( this::transformCollectionProperty, this::transformEnumeration, this::transformEntityProperty,
                  this::transformAbstractEntityProperty, this::transformEitherProperty, this::transformSimpleProperty );
      recursiveProperty = new LinkedList<>();
   }

   /**
    * Generates a sample JSON payload.
    * <p>
    * The example values in Aspect Models, which are passed in the constructor, are extracted and used to create the
    * JSON payload as a {@link String}.
    * In case an example value has not been given in the Model, a random value is generated and used instead.
    *
    * @param nameMapper The callback function that maps the Aspect name to an OutputStream
    */
   public void generateJson( final Function<String, OutputStream> nameMapper ) throws IOException {
      try ( final OutputStream output = nameMapper.apply( aspect.getName() ) ) {
         output.write( generateJson().getBytes() );
      }
   }

   /**
    * Generates a pretty formatted JSON payload.
    *
    * @see #generateJson(Function)
    */
   public void generateJsonPretty( final Function<String, OutputStream> nameMapper ) throws IOException {
      try ( final OutputStream output = nameMapper.apply( aspect.getName() ) ) {
         output.write( generateJsonPretty().getBytes() );
      }
   }

   public String generateJson() throws IOException {
      return objectMapper.writer().writeValueAsString( transformAspectProperties() );
   }

   private String generateJsonPretty() throws IOException {
      return objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString( transformAspectProperties() );
   }

   private static ObjectMapper createObjectMapper() {
      final ObjectMapper mapper = new ObjectMapper();
      mapper.registerModule( new JavaTimeModule() );
      mapper.registerModule( new AspectModelJacksonModule() );
      mapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
      return mapper;
   }

   private Map<String, Object> transformAspectProperties() {
      return transformProperties( aspect.getProperties() );
   }

   /**
    * @param properties the meta model properties to transform
    * @return a map representing the given properties
    */
   @SuppressWarnings( "squid:S2250" )
   //Amount of elements in list is in regard to amount of properties in aspect model. Even in bigger aspects this
   // should not lead to performance issues
   private Map<String, Object> transformProperties( final List<Property> properties ) {
      return Stream.concat(
            properties.stream().filter( recursiveProperty::contains ).map( this::recursiveProperty ),
            properties.stream().filter( property -> !recursiveProperty.contains( property ) )
                  .map( property -> {
                     recursiveProperty.add( property );
                     final Map<String, Object> result = transformProperty( new BasicProperty( property ) );
                     recursiveProperty.remove( property );
                     return result;
                  } )
      ).collect( HashMap::new, HashMap::putAll, HashMap::putAll );
   }

   private Map<String, Object> recursiveProperty( final Property property ) {
      if ( !property.isNotInPayload() && !property.isOptional() ) {
         throw new IllegalArgumentException(
               String.format( "Having a recursive Property: %s which is not Optional nor marked as NotInPayload is not valid.", property ) );
      }
      return Map.of();
   }

   /**
    * A meta model {@link Property#getCharacteristic()} can represent different types.
    * This method recursively transforms the types to corresponding data structures or primitives each having the {@link
    * Property#getName()}
    * as key.
    * <p>
    * A {@link Collection} will be represented as a {@link List}.
    * {@link Entity} and {@link Either} will be represented as {@link Map}.
    * As such the returned map can contain 1 to n entries.
    *
    * @param property the property to transform
    * @return a map representing the property names as key and the property values as value
    */
   private Map<String, Object> transformProperty( final BasicProperty property ) {
      return transformers.stream()
            .map( transformer -> transformer.apply( property ) )
            .filter( propertiesMap -> !propertiesMap.isEmpty() )
            .findFirst()
            .orElseThrow( () -> new IllegalArgumentException( "No transformer for " + property.getName() + " available." ) );
   }

   private Map<String, Object> transformCollectionProperty( final BasicProperty property ) {
      final Characteristic characteristic = property.getCharacteristic();
      if ( characteristic instanceof Collection ) {
         final List<Object> collectionValues = getCollectionValues( property, (Collection) characteristic );
         return toMap( property.getName(), collectionValues );
      }
      return ImmutableMap.of();
   }

   private Map<String, Object> transformAbstractEntityProperty( final BasicProperty property ) {
      final Optional<AbstractEntity> dataType = getForCharacteristic( property.getCharacteristic(), AbstractEntity.class );
      if ( dataType.isPresent() ) {
         final AbstractEntity abstractEntity = dataType.get();
         final ComplexType extendingComplexType = abstractEntity.getExtendingElements().get( 0 );
         final Map<String, Object> generatedProperties = transformProperties( extendingComplexType.getAllProperties() );
         generatedProperties.put( "@type", extendingComplexType.getName() );
         return toMap( property.getName(), generatedProperties );
      }
      return ImmutableMap.of();
   }

   private Map<String, Object> transformEntityProperty( final BasicProperty property ) {
      final Optional<Entity> dataType = getForCharacteristic( property.getCharacteristic(), Entity.class );
      if ( dataType.isPresent() ) {
         final Entity entity = dataType.get();
         final Map<String, Object> generatedProperties = transformProperties( entity.getAllProperties() );
         if ( entity.getExtends().isPresent() ) {
            generatedProperties.put( "@type", entity.getName() );
         }
         return toMap( property.getName(), generatedProperties );
      }
      return ImmutableMap.of();
   }

   private Map<String, Object> transformEnumeration( final BasicProperty property ) {
      return getForCharacteristic( property.getCharacteristic(), Enumeration.class )
            .map( enumeration -> extractEnumerationValues( property, enumeration ) )
            .orElseGet( ImmutableMap::of );
   }

   private Map<String, Object> extractEnumerationValues( final BasicProperty property, final Enumeration enumeration ) {
      final Object o = enumeration.getValues().get( 0 );
      if ( o instanceof Map ) {
         final Entity entity = (Entity) enumeration.getDataType().orElseThrow();
         @SuppressWarnings( "unchecked" ) final Map<String, Object> enumAttributes = removeNotInPayloadProperties(
               entity.getProperties(), (Map<String, Object>) o );
         return toMap( property.getName(), replaceNameFields( enumAttributes ) );
      }
      return toMap( property.getName(), o );
   }

   private Map<String, Object> removeNotInPayloadProperties( final List<Property> properties, final Map<String, Object> o ) {
      properties.stream().filter( Property::isNotInPayload ).forEach( property -> o.remove( property.getName() ) );
      return o;
   }

   /**
    * Removes the key {@link BAMM#name()} from the given {@link Map}.
    * This operation will be done recursively for all values that are entries in {@link Map}.
    *
    * @param fields the map to remove the {@link BAMM#name()} key if exists
    * @return a new map having all the left entries.
    */
   @SuppressWarnings( "unchecked" )
   private Map<String, Object> replaceNameFields( final Map<String, Object> fields ) {
      return fields.entrySet()
            .stream()
            .filter( entry -> !entry.getKey().equals( bamm.name().getURI() ) )
            .map( this::mapEntries )
            .collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) );
   }

   private AbstractMap.SimpleEntry<String, Object> mapEntries( final Map.Entry<String, Object> entry ) {
      final Object value = entry.getValue();
      // Entity
      if ( value instanceof Map ) {
         return new AbstractMap.SimpleEntry<>( entry.getKey(), replaceNameFields( (Map<String, Object>) value ) );
      }
      if ( value instanceof List ) {
         final List<?> list = (List<?>) value;
         // langString
         if ( !list.isEmpty() && list.get( 0 ) instanceof Tuple2 ) {
            final List<Tuple2<String, String>> languageStrings = (List<Tuple2<String, String>>) value;
            final Map<String, String> entries = languageStrings.stream().collect(
                  Collectors.toMap( Tuple2::_1, Tuple2::_2 ) );
            return new AbstractMap.SimpleEntry<>( entry.getKey(), entries );
         }
         // complex nested entity
         if ( list.get( 0 ) instanceof Map ) {
            final List<Map<String, Object>> entries = list.stream().map( e -> replaceNameFields( (Map<String, Object>) e ) ).collect( Collectors.toList() );
            return new AbstractMap.SimpleEntry<>( entry.getKey(), entries );
         }
      }
      return new AbstractMap.SimpleEntry<>( entry.getKey(), value );
   }

   private Map<String, Object> transformEitherProperty( final BasicProperty property ) {
      final Characteristic characteristic = property.getCharacteristic();
      return getForCharacteristic( characteristic, Either.class )
            .map( value -> transformProperty(
                  new BasicProperty( AspectModelJsonPayloadGenerator.EITHER_LEFT, value.getLeft(), Optional.empty() ) ) )
            .map( value -> toMap( property.getName(), value ) )
            .orElseGet( ImmutableMap::of );
   }

   private Map<String, Object> transformSimpleProperty( final BasicProperty basicProperty ) {
      return toMap( basicProperty.getName(), getExampleValueOrElseRandom( basicProperty ) );
   }

   /**
    * @param property the property to transform
    * @return the {@link Property#getExampleValue()} or if absent a random value.
    */
   private Object getExampleValueOrElseRandom( final BasicProperty property ) {
      final Characteristic characteristic = property.getCharacteristic();
      if ( characteristic instanceof State ) {
         return ((State) characteristic).getDefaultValue();
      }
      if ( characteristic instanceof Enumeration ) {
         return ((Enumeration) characteristic).getValues().get( 0 );
      }

      return property.getExampleValue().orElseGet( () -> generateExampleValue( characteristic ) );
   }

   private Map<String, Object> toMap( final String key, final Object value ) {
      return ImmutableMap.of( key, value );
   }

   private List<Object> getCollectionValues( final BasicProperty property, final Collection collection ) {
      final Type dataType = collection.getDataType().orElseThrow( () -> new IllegalArgumentException( "DataType for collection is required." ) );
      if ( dataType instanceof AbstractEntity ) {
         final AbstractEntity abstractEntity = (AbstractEntity) dataType;
         final ComplexType extendingComplexType = abstractEntity.getExtendingElements().get( 0 );
         final Map<String, Object> propertyValueMap = transformProperties( extendingComplexType.getAllProperties() );
         propertyValueMap.put( "@type", extendingComplexType.getName() );
         return ImmutableList.of( propertyValueMap );
      }
      if ( dataType instanceof Entity ) {
         final Entity entity = (Entity) dataType;
         return ImmutableList.of( transformProperties( entity.getAllProperties() ) );
      }
      if ( dataType instanceof Scalar ) {
         return ImmutableList.of( getExampleValueOrElseRandom( property ) );
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
   private interface Transformer extends Function<BasicProperty, Map<String, Object>> {
   }

   private static class BasicProperty {
      private final String name;
      private final Characteristic characteristic;
      private final Optional<Object> exampleValue;

      BasicProperty( final Property property ) {
         this( property.getPayloadName(), property.getCharacteristic(), property.getExampleValue() );
      }

      BasicProperty( final String name, final Characteristic characteristic, final Optional<Object> exampleValue ) {
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

      public Optional<Object> getExampleValue() {
         return exampleValue;
      }
   }

   private static class ExampleValueGenerator {
      private final EasyRandom defaultEasyRandom = new EasyRandom();
      private final Random random;

      private static final List<String> CURIE_VALUES = ImmutableList.of( "unit:hectopascal" );

      private final Map<Class<?>, BiFunction<Number, Number, Number>> generators = Map.of(
            Byte.class, ( min, max ) -> getRandomInteger( min.intValue(), max.intValue() ),
            Short.class, ( min, max ) -> getRandomInteger( min.intValue(), max.intValue() ),
            Integer.class, ( min, max ) -> getRandomInteger( min.intValue(), max.intValue() ),
            Long.class, ( min, max ) -> getRandomLong( min.longValue(), max.longValue() ),
            Float.class, ( min, max ) -> getRandomDouble( min.floatValue(), max.floatValue() ).floatValue(),
            BigInteger.class, ( min, max ) -> BigDecimal.valueOf( getRandomDouble( safelyNarrowDown( min ), safelyNarrowDown( max ) ) ).toBigInteger(),
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

         if ( !(dataType instanceof Scalar) ) {
            throw new IllegalArgumentException( "Example values can only be generated for scalar types." );
         }

         if ( RDF.langString.getURI().equals( dataType.getUrn() ) ) {
            return ImmutableMap.of( Locale.ENGLISH.toLanguageTag(), "Example multi language string" );
         }

         final Scalar scalar = (Scalar) dataType;
         final Resource dataTypeResource = ResourceFactory.createResource( scalar.getUrn() );
         final Class<?> exampleValueType = DataType.getJavaTypeForMetaModelType( dataTypeResource, characteristic.getMetaModelVersion() );
         if ( Curie.class.equals( exampleValueType ) ) {
            return getRandomEntry( ExampleValueGenerator.CURIE_VALUES );
         }
         if ( XMLGregorianCalendar.class.equals( exampleValueType ) ) {
            return getGregorianRandomValue( dataType );
         }
         if ( Duration.class.equals( exampleValueType ) ) {
            return DatatypeFactory.newDefaultInstance().newDuration( defaultEasyRandom.nextLong() );
         }

         if ( characteristic instanceof Trait ) {
            final Optional<Object> traitExampleValue = generateExampleValueForTrait( (Trait) characteristic, exampleValueType, dataTypeResource );
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
            if ( constraint instanceof LengthConstraint ) {
               return Optional.of( getRandomValue( (LengthConstraint) constraint, exampleValueType, trait.getBaseCharacteristic() ) );
            }
            if ( constraint instanceof RangeConstraint ) {
               return Optional.of( getRandomValue( (RangeConstraint) constraint, exampleValueType, dataTypeResource ) );
            }
            if ( constraint instanceof RegularExpressionConstraint ) {
               return Optional.of( getRandomValue( (RegularExpressionConstraint) constraint ) );
            }
         }
         return Optional.empty();
      }

      private Object getGregorianRandomValue( final Type dataType ) {
         final XMLGregorianCalendar randomCalendar = DatatypeFactory.newDefaultInstance().newXMLGregorianCalendar( new GregorianCalendar() );
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
         return Optional.empty();
      }

      @SuppressWarnings( "unchecked" )
      private Object getRandomValue( final LengthConstraint lengthConstraint,
            final java.lang.reflect.Type exampleValueType, final Characteristic traitBaseCharacteristic ) {
         final BigInteger maxLength = lengthConstraint.getMaxValue().orElse( BigInteger.valueOf( Integer.MAX_VALUE ) );
         final BigInteger minLength = lengthConstraint.getMinValue().orElse( BigInteger.ZERO );

         final EasyRandomParameters easyRandomParameters = new EasyRandomParameters()
               .stringLengthRange( minLength.intValue(), maxLength.intValue() );
         final EasyRandom easyRandom = new EasyRandom( easyRandomParameters );
         if ( traitBaseCharacteristic instanceof Collection ) {
            final int amount = getRandomInteger( minLength.intValue(), maxLength.intValue() );
            final List<Object> returnValues = new ArrayList<>();
            for ( int i = 0; i < amount; i++ ) {
               returnValues.add( easyRandom.nextObject( (Class<?>) exampleValueType ) );
            }
            return returnValues;
         }
         return easyRandom.nextObject( (Class<?>) exampleValueType );
      }

      private Number getRandomValue( final RangeConstraint rangeConstraint, final java.lang.reflect.Type valueType, final Resource dataTypeResource ) {
         final Number min = calculateLowerBound( rangeConstraint, valueType, dataTypeResource );
         final Number max = calculateUpperBound( rangeConstraint, valueType, dataTypeResource );
         return generateForNumericTypeInRange( valueType, min, max );
      }

      private Number generateForNumericTypeInRange( final java.lang.reflect.Type valueType, final Number min, final Number max ) {
         return generators
               .getOrDefault( valueType, ( low, high ) -> getRandomDouble( low.doubleValue(), high.doubleValue() ) )
               .apply( min, max );
      }

      // narrowing conversion from BigDecimal to double
      private double safelyNarrowDown( final Number bound ) {
         if ( !(BigDecimal.class.equals( bound.getClass() )) ) {
            return bound.doubleValue();
         }

         // We have to be extremely cautious with BigDecimal bounds. Because of the limited precision
         // of the "double" type (15-17 significant digits) we might jump over the bound while rounding.
         // Example: xsd:unsignedLong has a max. value of 18446744073709551615; when converting it to double
         // it will get represented as 1.8446744073709552E16, thereby exceeding the upper bound.
         // Therefore we need to take care of always rounding down when narrowing to double.
         final BigDecimal narrowed = ((BigDecimal) bound).round( new MathContext( 15, RoundingMode.DOWN ) );
         return narrowed.doubleValue();
      }

      private Number calculateLowerBound( final RangeConstraint rangeConstraint, final java.lang.reflect.Type valueType, final Resource dataTypeResource ) {
         return rangeConstraint.getMinValue()
               .map( value -> (Number) value )
               // exclusive lower bound?
               .map( value -> BoundDefinition.GREATER_THAN
                     .equals( rangeConstraint.getLowerBoundDefinition() )
                     ? NumericTypeTraits.polymorphicAdd( value, exclusiveBoundDelta( valueType ) )
                     : value )
               .orElse( NumericTypeTraits.getModelMinValue( dataTypeResource, valueType ) );
      }

      private Number calculateUpperBound( final RangeConstraint rangeConstraint, final java.lang.reflect.Type valueType, final Resource dataTypeResource ) {
         return rangeConstraint.getMaxValue()
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
         final RgxGen rgxGen = new RgxGen( rangeConstraint.getValue() );
         return rgxGen.generate();
      }

      private Double getRandomDouble( final double min, final double max ) {
         if ( Math.abs( min - max ) < THRESHOLD || Math.abs( min - max ) == Double.POSITIVE_INFINITY ) {
            return min;
         }
         //noinspection OptionalGetWithoutIsPresent
         return random.doubles( 1, min, max ).findFirst().getAsDouble();
      }

      private Integer getRandomInteger( final int min, final int max ) {
         if ( min == max ) {
            return min;
         }
         //noinspection OptionalGetWithoutIsPresent
         return random.ints( 1, min, max ).findFirst().getAsInt();
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
