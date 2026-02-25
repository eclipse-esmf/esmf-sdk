/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.parquet;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.SimpleGroupFactory;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.example.ExampleParquetWriter;
import org.apache.parquet.io.LocalOutputFile;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Types;
import org.eclipse.esmf.aspectmodel.generator.Artifact;
import org.eclipse.esmf.aspectmodel.generator.ArtifactGenerator;
import org.eclipse.esmf.aspectmodel.generator.AspectGenerator;
import org.eclipse.esmf.aspectmodel.generator.DocumentGenerationException;
import org.eclipse.esmf.aspectmodel.generator.NumericTypeTraits;
import org.eclipse.esmf.aspectmodel.generator.ParquetArtifact;
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
import org.eclipse.esmf.metamodel.datatype.LangString;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.impl.DefaultScalarValue;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.google.common.collect.ImmutableMap;

import io.vavr.Tuple2;

/**
 * Generator for Apache Parquet payload files from Aspect Models.
 * This generator creates sample Parquet files with data that conforms to the Aspect Model structure.
 */
public class AspectModelParquetPayloadGenerator extends AspectGenerator<String, byte[], ParquetGenerationConfig, ParquetArtifact> {

   MessageType messageTypeSchema = null;

   Group group = null;

   Map<String, String> typeKeyColumnNameMap = new HashMap<>();

   List<String> columnNames = new ArrayList<>();

   public static final ParquetGenerationConfig DEFAULT_CONFIG = ParquetGenerationConfigBuilder.builder().build();

   public AspectModelParquetPayloadGenerator( final Aspect aspect ) {
      this( aspect, DEFAULT_CONFIG );
   }

   public AspectModelParquetPayloadGenerator( final Aspect aspect, final ParquetGenerationConfig config ) {
      super( aspect, config );
   }

   @Override
   public Stream<ParquetArtifact> generate() {
      return Stream.of( new PayloadGenerator().apply( aspect(), config ) );
   }

   @Override
   protected void write( final Artifact<String, byte[]> artifact, final Function<String, OutputStream> nameMapper ) {
      try ( final OutputStream output = nameMapper.apply( aspect().getName() ) ) {
         output.write( artifact.serialize() );
         output.flush();
      } catch ( final IOException exception ) {
         throw new DocumentGenerationException( exception );
      }
   }

   private class PayloadGenerator implements ArtifactGenerator<String, byte[], Aspect, ParquetGenerationConfig, ParquetArtifact> {

      /**
       * Constant for the either left property.
       * For example Parquet payloads the left type will be used.
       */
      private static final String EITHER_LEFT = "left";

      private final List<Transformer> transformers;
      private final List<Property> recursiveProperty;
      private ExampleValueGenerator exampleValueGenerator = null;
      private Aspect aspect;

      private PayloadGenerator() {
         transformers = Arrays.asList( this::transformCollectionProperty, this::transformEnumeration, this::transformEntityProperty,
               this::transformAbstractEntityProperty, this::transformEitherProperty, this::transformSimpleProperty );
         recursiveProperty = new LinkedList<>();
      }

      @Override
      public ParquetArtifact apply( final Aspect aspect, final ParquetGenerationConfig config ) {
         this.aspect = aspect;
         exampleValueGenerator = new ExampleValueGenerator( config.randomStrategy() );

         try {
            // Generate Parquet file in memory
            final String tempFilePath = System.getProperty( "java.io.tmpdir" ) + java.io.File.separator + aspect.getName() + "_temp.parquet";
            final java.nio.file.Path parquetPath = Paths.get( tempFilePath );
            // Clean up temporary file
            java.nio.file.Files.deleteIfExists( parquetPath );

            generateParquetFile( tempFilePath, aspect );

            // Read the generated file into byte array
            final byte[] parquetData = java.nio.file.Files.readAllBytes( parquetPath );

            // Clean up temporary file
            java.nio.file.Files.deleteIfExists( parquetPath );

            return new ParquetArtifact( aspect.getName() + ".parquet", parquetData );
         } catch ( final IOException e ) {
            throw new DocumentGenerationException( e );
         }
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
         return trait.getBaseCharacteristic().is( Collection.class ) && (!trait.getConstraints().isEmpty());
      }

      private Map<String, Object> transformAbstractEntityProperty( final BasicProperty property, final boolean useModelExampleValue ) {
         final Optional<AbstractEntity> dataType = getForCharacteristic( property.getCharacteristic(), AbstractEntity.class );
         if ( dataType.isPresent() ) {
            final AbstractEntity abstractEntity = dataType.get();
            final ComplexType extendingComplexType = abstractEntity.getExtendingElements().get( 0 );
            final Map<String, Object> generatedProperties = transformProperties( extendingComplexType.getAllProperties(),
                  useModelExampleValue );
            if ( config.addTypeAttributeForEntityInheritance() ) {
               generatedProperties.put( TYPE, extendingComplexType.getName() );
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
               generatedProperties.put( TYPE, entity.getName() );
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
         final Value firstValue = enumeration.getValues().getFirst();
         // Extract the value directly - for ScalarValue get the underlying value, otherwise use toString
         final Object value = firstValue instanceof final ScalarValue scalarValue
               ? scalarValue.getValue()
               : firstValue.toString();
         return toMap( property.getName(), value );
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
            elementCharacteristics = ((Collection) characteristic).getElementCharacteristic();
         }
         final Characteristic effectiveCharacteristics = elementCharacteristics.orElse( characteristic );

         if ( !useModelExampleValue ) {
            return generateExampleValue( effectiveCharacteristics );
         }

         return property.getExampleValue()
               .map( exampleValue -> exampleValue.as( ScalarValue.class ).getValue() )
               .map( value -> value instanceof final Curie curie ? curie.value() : value )
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
            return payload instanceof final List list ? list : List.of( payload );
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
               propertyValueMap.put( TYPE, extendingComplexType.getName() );
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

         private static Optional<ScalarValue> processExampleValue( final Optional<ScalarValue> exampleValue ) {
            return exampleValue.map( value -> {
               if ( value instanceof final ScalarValue scalarValue ) {
                  return scalarValue;
               } else if ( value instanceof final Value valueInstance ) {
                  return convertToScalarValue( valueInstance );
               } else {
                  throw new IllegalArgumentException( "Unexpected exampleValue type: " + value.getClass() );
               }
            } );
         }

         private static ScalarValue convertToScalarValue( final Value value ) {
            if ( value instanceof final ScalarValue scalarValue ) {
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
                  ((ScalarValue) value).getValue(),
                  new DefaultScalar( value.getType().toString() )
            );
         }
      }
   }

   private static class ExampleValueGenerator {
      private static final double THRESHOLD = .0001;
      private final EasyRandom defaultEasyRandom = new EasyRandom();
      private final Random random;

      private static final List<String> CURIE_VALUES = List.of( "unit:hectopascal" );

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

         if ( !(dataType.is( Scalar.class )) ) {
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

      private Number calculateLowerBound( final RangeConstraint rangeConstraint, final java.lang.reflect.Type valueType,
            final Resource dataTypeResource ) {
         return rangeConstraint.getMinValue()
               .map( ScalarValue::getValue )
               .map( Number.class::cast )
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
               .map( Number.class::cast )
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

   private static final Map<Resource, PrimitiveType.PrimitiveTypeName> XSD_TO_PARQUET_TYPE_MAP =
         Map.of(
               XSD.xboolean, PrimitiveType.PrimitiveTypeName.BOOLEAN,
               XSD.xfloat, PrimitiveType.PrimitiveTypeName.FLOAT,
               XSD.xdouble, PrimitiveType.PrimitiveTypeName.DOUBLE,
               XSD.decimal, PrimitiveType.PrimitiveTypeName.DOUBLE,
               XSD.xint, PrimitiveType.PrimitiveTypeName.INT32,
               XSD.integer, PrimitiveType.PrimitiveTypeName.INT32,
               XSD.xshort, PrimitiveType.PrimitiveTypeName.INT32,
               XSD.xbyte, PrimitiveType.PrimitiveTypeName.INT32,
               XSD.xlong, PrimitiveType.PrimitiveTypeName.INT64
               // Add other mappings as needed
         );

   private static final Set<Resource> INT32_TYPES = Set.of(
         XSD.xbyte, XSD.unsignedShort, XSD.unsignedByte, XSD.nonNegativeInteger,
         XSD.positiveInteger, XSD.nonPositiveInteger, XSD.negativeInteger
   );

   private static final Set<Resource> INT64_TYPES = Set.of(
         XSD.unsignedInt, XSD.unsignedLong
   );

   private Object generateDefaultScalarValue( final Scalar scalar ) {
      final String scalarUri = scalar.getUrn();
      final Resource dataTypeResource = ResourceFactory.createResource( scalarUri );

      if ( scalarUri.equals( XSD.xstring.getURI() ) || scalarUri.equals( XSD.anyURI.getURI() )
            || scalarUri.equals( XSD.normalizedString.getURI() ) || scalarUri.equals( XSD.token.getURI() ) ) {
         return new EasyRandom().nextObject( String.class );
      } else if ( scalarUri.equals( XSD.xboolean.getURI() ) ) {
         return true;
      } else if ( scalarUri.equals( XSD.duration.getURI() ) || scalarUri.equals( XSD.yearMonthDuration.getURI() )
            || scalarUri.equals( XSD.dayTimeDuration.getURI() ) ) {
         return DatatypeFactory.newDefaultInstance().newDuration( new Random().nextLong( 86400000L ) ).toString();
      } else if ( scalarUri.equals( XSD.dateTime.getURI() ) || scalarUri.equals( XSD.dateTimeStamp.getURI() ) ) {
         return DatatypeFactory.newDefaultInstance().newXMLGregorianCalendar( new GregorianCalendar() );
      } else if ( scalarUri.equals( XSD.date.getURI() ) ) {
         return LocalDate.now().toString();
      } else if ( scalarUri.equals( XSD.hexBinary.getURI() ) ) {
         return HexFormat.of().formatHex( new EasyRandom().nextObject( String.class ).getBytes( StandardCharsets.UTF_8 ) );
      } else if ( scalarUri.equals( XSD.base64Binary.getURI() ) ) {
         return Base64.getEncoder().encodeToString( new EasyRandom().nextObject( String.class ).getBytes( StandardCharsets.UTF_8 ) );
      } else if ( scalarUri.equals( RDF.langString.getURI() ) ) {
         return new EasyRandom().nextObject( String.class );
      }

      // Handle numeric types using SammXsdType
      final Class<?> javaType = SammXsdType.getJavaTypeForMetaModelType( dataTypeResource );
      if ( javaType != null && Number.class.isAssignableFrom( javaType ) ) {
         final Number min = NumericTypeTraits.getModelMinValue( dataTypeResource, javaType );
         final Number max = NumericTypeTraits.getModelMaxValue( dataTypeResource, javaType );
         // Generate a value in the valid range using the overflow-safe method
         return generateNumericValueInRange( javaType, min, max );
      }

      // Handle time/date related types as string
      if ( scalarUri.equals( XSD.time.getURI() ) || scalarUri.equals( XSD.gDay.getURI() )
            || scalarUri.equals( XSD.gMonth.getURI() ) || scalarUri.equals( XSD.gYear.getURI() )
            || scalarUri.equals( XSD.gYearMonth.getURI() ) || scalarUri.equals( XSD.gMonthDay.getURI() ) ) {
         return DatatypeFactory.newDefaultInstance().newXMLGregorianCalendar( new GregorianCalendar() ).toString();
      }

      // Handle curie type
      if ( Curie.class.equals( javaType ) ) {
         return "unit:hectopascal";
      }

      return new EasyRandom().nextObject( String.class );
   }

   private PrimitiveType.PrimitiveTypeName mapXsdTypeToParquetType( final String xsdTypeUri ) {
      final Resource xsdResource = ResourceFactory.createResource( xsdTypeUri );

      // Direct mapping
      if ( XSD_TO_PARQUET_TYPE_MAP.containsKey( xsdResource ) ) {
         return XSD_TO_PARQUET_TYPE_MAP.get( xsdResource );
      }

      // Group mappings
      if ( INT32_TYPES.contains( xsdResource ) ) {
         return PrimitiveType.PrimitiveTypeName.INT32;
      }

      if ( INT64_TYPES.contains( xsdResource ) ) {
         return PrimitiveType.PrimitiveTypeName.INT64;
      }

      // Default fallback
      return PrimitiveType.PrimitiveTypeName.BINARY;
   }

   private PrimitiveType mapXsdTypeToParquetType( final String xsdTypeUri, final String fieldName, final String language, final boolean isTimezoneAvailable,
         final BigInteger maxLength ) {
      final Resource xsdResource = ResourceFactory.createResource( xsdTypeUri );

      // Boolean type
      if ( XSD.xboolean.equals( xsdResource ) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.BOOLEAN, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .named( fieldName );
      } else if ( (XSD.xstring.equals( xsdResource ) ||
            XSD.time.equals( xsdResource ) ||
            XSD.gYear.equals( xsdResource ) ||
            XSD.gMonth.equals( xsdResource ) ||
            XSD.gDay.equals( xsdResource ) ||
            XSD.gYearMonth.equals( xsdResource ) ||
            XSD.gMonthDay.equals( xsdResource ) ||
            XSD.duration.equals( xsdResource ) ||
            XSD.yearMonthDuration.equals( xsdResource ) ||
            XSD.dayTimeDuration.equals( xsdResource ) ||
            XSD.hexBinary.equals( xsdResource ) ||
            XSD.base64Binary.equals( xsdResource ) ||
            XSD.anyURI.equals( xsdResource ) ||
            RDF.langString.getURI().equals( xsdTypeUri )) && (maxLength != null && maxLength.intValue() > 0) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .length( maxLength.intValue() )
               .named( fieldName );
      } else if ( RDF.langString.getURI().equals( xsdTypeUri ) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.BINARY, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.stringType() )
               .named( fieldName + "-" + language );
      }

      // Float type
      else if ( XSD.xfloat.equals( xsdResource ) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.FLOAT, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .named( fieldName );
      }

      // Double and decimal types
      else if ( XSD.xdouble.equals( xsdResource ) || XSD.decimal.equals( xsdResource ) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.DOUBLE, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .named( fieldName );
      }

      // Integer types that map to INT32
      else if ( XSD.xint.equals( xsdResource ) || XSD.integer.equals( xsdResource ) ||
            XSD.xshort.equals( xsdResource ) || XSD.xbyte.equals( xsdResource ) ||
            XSD.unsignedShort.equals( xsdResource ) || XSD.unsignedByte.equals( xsdResource ) ||
            XSD.nonNegativeInteger.equals( xsdResource ) || XSD.positiveInteger.equals( xsdResource ) ||
            XSD.nonPositiveInteger.equals( xsdResource ) || XSD.negativeInteger.equals( xsdResource ) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.INT32, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .named( fieldName );
      }

      // Long types that map to INT64
      else if ( XSD.xlong.equals( xsdResource ) || XSD.unsignedInt.equals( xsdResource ) ||
            XSD.unsignedLong.equals( xsdResource ) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.INT64, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .named( fieldName );
      }

      // Date type with logical annotation
      else if ( XSD.date.equals( xsdResource ) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.INT32, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.dateType() )
               .named( fieldName );
      }

      // Time types as string
      else if ( XSD.time.equals( xsdResource ) || XSD.gDay.equals( xsdResource ) ||
            XSD.gMonth.equals( xsdResource ) || XSD.gYear.equals( xsdResource ) ||
            XSD.gYearMonth.equals( xsdResource ) || XSD.gMonthDay.equals( xsdResource ) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.BINARY, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.stringType() )
               .named( fieldName );
      }

      // DateTime types with timestamp annotation
      else if ( XSD.dateTime.equals( xsdResource ) || XSD.dateTimeStamp.equals( xsdResource ) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.INT64, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.timestampType( isTimezoneAvailable, LogicalTypeAnnotation.TimeUnit.MICROS ) )
               .named( fieldName );
      }

      // Duration types as string
      else if ( XSD.duration.equals( xsdResource ) || XSD.yearMonthDuration.equals( xsdResource ) ||
            XSD.dayTimeDuration.equals( xsdResource ) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.BINARY, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.stringType() )
               .named( fieldName );
      }

      // Binary types
      else if ( XSD.hexBinary.equals( xsdResource ) || XSD.base64Binary.equals( xsdResource ) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.BINARY, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .named( fieldName );
      }

      // String types (including all string-derived types)
      else if ( XSD.xstring.equals( xsdResource ) || XSD.anyURI.equals( xsdResource ) ||
            XSD.normalizedString.equals( xsdResource ) || XSD.token.equals( xsdResource ) ||
            XSD.Name.equals( xsdResource ) || XSD.QName.equals( xsdResource ) ||
            XSD.language.equals( xsdResource ) || XSD.NMTOKEN.equals( xsdResource ) ||
            XSD.NCName.equals( xsdResource ) || XSD.ID.equals( xsdResource ) ||
            XSD.IDREF.equals( xsdResource ) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.BINARY, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.stringType() )
               .named( fieldName );
      }

      // Default fallback for unknown types
      else {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.BINARY, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.stringType() )
               .named( fieldName );
      }
   }

   private void extractPropertyData( final Property property, final String prefix,
         final Map<String, Tuple2<Object, PrimitiveType.PrimitiveTypeName>> flattenedData ) {
      final String columnName = prefix.isEmpty() ? property.getPayloadName() : prefix + "_" + property.getPayloadName();

      if ( property.getCharacteristic().isEmpty() ) {
         return;
      }

      final Characteristic characteristic = property.getCharacteristic().orElse( null );
      BigInteger maxLength = null;
      if ( characteristic instanceof final Trait trait ) {

         final LengthConstraint lengthConstraint = trait.getConstraints().stream()
               .filter( LengthConstraint.class::isInstance )
               .map( LengthConstraint.class::cast )
               .findFirst().orElse( null );

         if ( lengthConstraint != null ) {
            maxLength = lengthConstraint.getMaxValue().orElse( null );
         }
      }

      extractCharacteristicData( characteristic, property, columnName, flattenedData, maxLength );
   }

   private void extractCharacteristicData( final Characteristic characteristic, final Property property,
         final String columnName, final Map<String, Tuple2<Object, PrimitiveType.PrimitiveTypeName>> flattenedData, final BigInteger maxLength ) {

      switch ( characteristic ) {
      case final Collection collection -> extractCollectionData( collection, property, columnName, flattenedData, maxLength );
      case final Trait trait -> {
         final Characteristic baseCharacteristic = trait.getBaseCharacteristic();

         // Check if the base characteristic is a Collection
         if ( baseCharacteristic instanceof final Collection collection ) {
            extractCollectionData( collection, property, columnName, flattenedData, maxLength );
         } else {
            extractCharacteristicData( baseCharacteristic, property, columnName, flattenedData, maxLength );
         }
      }
      case final Either either -> extractEitherData( either, property, columnName, flattenedData, maxLength );

      default -> extractScalarOrEntityData( characteristic, property, columnName, flattenedData, maxLength );
      }

   }

   private void extractCollectionData( final Collection collection, final Property property,
         final String columnName, final Map<String, Tuple2<Object, PrimitiveType.PrimitiveTypeName>> flattenedData, final BigInteger maxLength ) {
      final Type dataType = collection.getDataType().orElse( null );
      if ( dataType == null ) {
         return;
      }

      if ( dataType instanceof final ComplexType complexType ) {
         // For collections of complex types, create indexed columns for each element
         final int collectionSize = getCollectionExampleSize( collection, property );

         for ( int i = 0; i < collectionSize; i++ ) {
            final String indexedPrefix = columnName + "_" + i;
            extractComplexTypeProperties( complexType, indexedPrefix, flattenedData );
         }
      } else {
         // For collections of scalar types, create indexed columns for each element
         final int collectionSize = getCollectionExampleSize( collection, property );

         for ( int i = 0; i < collectionSize; i++ ) {
            final String indexedColumnName = columnName + "_" + i;
            final Object exampleValue = extractExampleValueFromProperty( property, collection );
            flattenedData.put( indexedColumnName, new Tuple2<>( exampleValue, mapXsdTypeToParquetType( dataType.getUrn() ) ) );
         }
      }
   }

   private void extractComplexTypeProperties( final ComplexType complexType, final String prefix,
         final Map<String, Tuple2<Object, PrimitiveType.PrimitiveTypeName>> flattenedData ) {
      for ( final Property property : complexType.getAllProperties() ) {
         if ( property.isNotInPayload() ) {
            continue;
         }
         extractPropertyData( property, prefix, flattenedData );
      }
   }

   private void extractEitherData( final Either either, final Property property,
         final String columnName, final Map<String, Tuple2<Object, PrimitiveType.PrimitiveTypeName>> flattenedData, final BigInteger maxLength ) {
      // For Either, extract both left and right possibilities
      extractCharacteristicData( either.getLeft(), property, columnName + "_left", flattenedData, maxLength );
      extractCharacteristicData( either.getRight(), property, columnName + "_right", flattenedData, maxLength );
   }

   private void extractScalarOrEntityData( final Characteristic characteristic, final Property property,
         final String columnName, final Map<String, Tuple2<Object, PrimitiveType.PrimitiveTypeName>> flattenedData, final BigInteger maxLength ) {

      final Type dataType = characteristic.getDataType().orElse( null );
      if ( dataType == null ) {
         return;
      }

      if ( dataType instanceof final ComplexType complexType ) {
         extractComplexTypeProperties( complexType, columnName, flattenedData );
      } else {
         final Object exampleValue = extractExampleValueFromProperty( property, characteristic );
         flattenedData.put( columnName, new Tuple2<>( exampleValue, mapXsdTypeToParquetType( dataType.getUrn() ) ) );

      }
   }

   private Object extractExampleValueFromProperty( final Property property, final Characteristic characteristic ) {
      // First check if property has example value
      if ( property.getExampleValue().isPresent() ) {
         final Value exampleValue = property.getExampleValue().orElse( null );
         return extractActualValue( exampleValue );
      }

      // Then check characteristic for example values
      if ( characteristic instanceof final Enumeration enumeration
            && !enumeration.getValues().isEmpty() ) {
         final Value firstValue = enumeration.getValues().getFirst();
         return extractActualValue( firstValue );
      }

      // Handle Trait with constraints (e.g., RangeConstraint)
      if ( characteristic instanceof final Trait trait ) {
         final Characteristic baseCharacteristic = trait.getBaseCharacteristic();
         final Type dataType = baseCharacteristic.getDataType().orElse( null );
         if ( dataType instanceof final Scalar scalar ) {
            final Resource dataTypeResource = ResourceFactory.createResource( scalar.getUrn() );
            final Class<?> javaType = SammXsdType.getJavaTypeForMetaModelType( dataTypeResource );

            for ( final Constraint constraint : trait.getConstraints() ) {
               if ( constraint instanceof final RangeConstraint rangeConstraint && javaType != null
                     && Number.class.isAssignableFrom( javaType ) ) {
                  final Number min = rangeConstraint.getMinValue()
                        .map( ScalarValue::getValue )
                        .map( Number.class::cast )
                        .orElse( NumericTypeTraits.getModelMinValue( dataTypeResource, javaType ) );
                  final Number max = rangeConstraint.getMaxValue()
                        .map( ScalarValue::getValue )
                        .map( Number.class::cast )
                        .orElse( NumericTypeTraits.getModelMaxValue( dataTypeResource, javaType ) );

                  // Generate a random value within the constrained range
                  return generateNumericValueInRange( javaType, min, max );
               }
            }
         }
         // Fall through to generate based on base characteristic's data type
         return extractExampleValueFromProperty( property, baseCharacteristic );
      }

      // Generate default value based on data type
      final Type dataType = characteristic.getDataType().orElse( null );
      if ( dataType instanceof final Scalar scalarDataType ) {
         return generateDefaultScalarValue( scalarDataType );
      }

      return new EasyRandom().nextObject( String.class );
   }

   private Number generateNumericValueInRange( final Class<?> javaType, final Number min, final Number max ) {
      final Random rand = new Random();
      if ( Integer.class.isAssignableFrom( javaType ) || Short.class.isAssignableFrom( javaType )
            || Byte.class.isAssignableFrom( javaType ) ) {
         final int lo = min.intValue();
         final int hi = max.intValue();
         if ( lo >= hi ) {
            return lo;
         }
         // Use long arithmetic to avoid overflow in (hi - lo)
         final long range = (long) hi - (long) lo;
         return (int) (lo + (long) (rand.nextDouble() * range));
      } else if ( Long.class.isAssignableFrom( javaType ) ) {
         // Clamp to Long range to handle unsignedLong whose model max exceeds Long.MAX_VALUE
         long lo = min.longValue();
         long hi = max.longValue();
         // If the original Number values suggest overflow (e.g., unsignedLong max), clamp
         final BigDecimal bdMin = NumericTypeTraits.convertToBigDecimal( min );
         final BigDecimal bdMax = NumericTypeTraits.convertToBigDecimal( max );
         if ( bdMin.compareTo( BigDecimal.valueOf( Long.MIN_VALUE ) ) < 0 ) {
            lo = Long.MIN_VALUE;
         }
         if ( bdMax.compareTo( BigDecimal.valueOf( Long.MAX_VALUE ) ) > 0 ) {
            hi = Long.MAX_VALUE;
         }
         if ( lo >= hi ) {
            return lo;
         }
         // Use BigDecimal to avoid overflow
         final BigDecimal bdLo = BigDecimal.valueOf( lo );
         final BigDecimal bdHi = BigDecimal.valueOf( hi );
         final BigDecimal range = bdHi.subtract( bdLo );
         return bdLo.add( range.multiply( BigDecimal.valueOf( rand.nextDouble() ) ) ).longValue();
      } else if ( Float.class.isAssignableFrom( javaType ) ) {
         final float lo = min.floatValue();
         final float hi = max.floatValue();
         if ( Float.isInfinite( lo ) || Float.isInfinite( hi ) || Float.isNaN( lo ) || Float.isNaN( hi ) || lo >= hi ) {
            return lo >= hi && !Float.isInfinite( lo ) && !Float.isNaN( lo ) ? lo : 1.0f;
         }
         final float range = hi - lo;
         if ( Float.isInfinite( range ) ) {
            // Range overflows, use half range
            final float halfHi = hi / 2.0f;
            final float halfLo = lo / 2.0f;
            return halfLo + rand.nextFloat() * (halfHi - halfLo);
         }
         return lo + rand.nextFloat() * range;
      } else if ( Double.class.isAssignableFrom( javaType ) ) {
         final double lo = min.doubleValue();
         final double hi = max.doubleValue();
         if ( Double.isInfinite( lo ) || Double.isInfinite( hi ) || Double.isNaN( lo ) || Double.isNaN( hi ) || lo >= hi ) {
            return lo >= hi && !Double.isInfinite( lo ) && !Double.isNaN( lo ) ? lo : 1.0;
         }
         final double range = hi - lo;
         if ( Double.isInfinite( range ) ) {
            // Range overflows (e.g., -Double.MAX_VALUE to Double.MAX_VALUE), use half range
            final double halfHi = hi / 2.0;
            final double halfLo = lo / 2.0;
            return halfLo + rand.nextDouble() * (halfHi - halfLo);
         }
         return lo + rand.nextDouble() * range;
      } else if ( BigInteger.class.isAssignableFrom( javaType ) ) {
         // BigInteger XSD types (nonNegativeInteger, positiveInteger, etc.) are mapped to INT32 in Parquet,
         // so clamp the range to INT32 to avoid overflow when writing.
         BigDecimal bdMin = NumericTypeTraits.convertToBigDecimal( min );
         BigDecimal bdMax = NumericTypeTraits.convertToBigDecimal( max );
         final BigDecimal int32Min = BigDecimal.valueOf( Integer.MIN_VALUE );
         final BigDecimal int32Max = BigDecimal.valueOf( Integer.MAX_VALUE );
         if ( bdMin.compareTo( int32Min ) < 0 ) {
            bdMin = int32Min;
         }
         if ( bdMax.compareTo( int32Max ) > 0 ) {
            bdMax = int32Max;
         }
         if ( bdMin.compareTo( bdMax ) >= 0 ) {
            return bdMin.toBigInteger();
         }
         final BigDecimal range = bdMax.subtract( bdMin );
         return bdMin.add( range.multiply( BigDecimal.valueOf( rand.nextDouble() ) ) ).toBigInteger();
      } else if ( BigDecimal.class.isAssignableFrom( javaType ) ) {
         // BigDecimal / xsd:decimal is mapped to DOUBLE in Parquet, so clamp to a range
         // where double arithmetic won't overflow (half of Double.MAX_VALUE on each side).
         BigDecimal bdMin = NumericTypeTraits.convertToBigDecimal( min );
         BigDecimal bdMax = NumericTypeTraits.convertToBigDecimal( max );
         final BigDecimal dblHalfMin = BigDecimal.valueOf( -Double.MAX_VALUE / 2 );
         final BigDecimal dblHalfMax = BigDecimal.valueOf( Double.MAX_VALUE / 2 );
         if ( bdMin.compareTo( dblHalfMin ) < 0 ) {
            bdMin = dblHalfMin;
         }
         if ( bdMax.compareTo( dblHalfMax ) > 0 ) {
            bdMax = dblHalfMax;
         }
         if ( bdMin.compareTo( bdMax ) >= 0 ) {
            return bdMin;
         }
         final BigDecimal range = bdMax.subtract( bdMin );
         return bdMin.add( range.multiply( BigDecimal.valueOf( rand.nextDouble() ) ) );
      }
      return min;
   }

   private Object extractActualValue( final Value value ) {
      if ( value instanceof final ScalarValue scalarValue ) {
         return scalarValue.getValue();
      }
      return value == null ? null : value.toString();
   }

   private int getCollectionExampleSize( final Collection collection, final Property property ) {
      // Check if property has example value and try to determine size from it
      if ( property.getExampleValue().isPresent() ) {
         final Value exampleValue = property.getExampleValue().orElse( null );
         final Object actualValue = extractActualValue( exampleValue );

         // If the example value is already a collection, use its size
         if ( actualValue instanceof java.util.Collection ) {
            final int size = ((java.util.Collection<?>) actualValue).size();
            return Math.max( 1, size ); // Ensure at least 1 element
         }
      }

      // Check for length constraints on the collection
      if ( collection instanceof final Trait trait ) {
         for ( final Constraint constraint : trait.getConstraints() ) {
            if ( constraint instanceof final LengthConstraint lengthConstraint ) {
               final BigInteger minValue = lengthConstraint.getMinValue().orElse( BigInteger.ONE );
               return Math.max( 1, minValue.intValue() );
            }
         }
      }

      return 1;
   }

   private void createMessageTypeSchemaFromFlattenedData( final Map<String, Tuple2<Object, PrimitiveType>> data ) {

      final Types.MessageTypeBuilder messageTypeBuilder = Types.buildMessage();

      for ( final Map.Entry<String, Tuple2<Object, PrimitiveType>> entry : data.entrySet() ) {
         final PrimitiveType parquetType = entry.getValue()._2;
         messageTypeBuilder.addField( parquetType );

      }

      messageTypeSchema = messageTypeBuilder.named( "AspectModel" );

   }

   private void generateParquetFile( final String outputPath, final Aspect aspect ) throws IOException {

      final Map<String, Tuple2<Object, PrimitiveType>> flattenedExampleData = new LinkedHashMap<>();

      final Set<String> visitedTypes = new HashSet<>();

      // Extract all properties and create flattened structure
      extractAspectProperties( aspect, "", flattenedExampleData, visitedTypes );

      if ( flattenedExampleData.isEmpty() ) {
         // Create a minimal placeholder entry for aspects with no extractable data
         // (e.g., aspects with only recursive properties or only operations)
         final PrimitiveType placeholderType = Types.primitive( PrimitiveType.PrimitiveTypeName.BINARY,
                     org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.stringType() )
               .named( "_placeholder" );
         flattenedExampleData.put( "_placeholder", new Tuple2<>( aspect.getName(), placeholderType ) );
      }

      // Create MessageType from flattened data
      createMessageTypeSchemaFromFlattenedData( flattenedExampleData );

      // Generate denormalized rows based on collections
      final List<Map<String, Object>> denormalizedRows = createDenormalizedRows( aspect, flattenedExampleData );
      final Map<String, List<Map.Entry<String, Object>>> propertyNameKeyValueMap = new HashMap<>();

      denormalizedRows.stream().flatMap( map -> map.entrySet().stream() )
            .forEach( entrySet -> {
               final String mapKey = getFirstPartBeforeDoubleUnderscore( entrySet.getKey() );

               if ( propertyNameKeyValueMap.containsKey( mapKey ) ) {
                  propertyNameKeyValueMap.get( mapKey ).add( entrySet );
               } else {
                  final List<Map.Entry<String, Object>> entrySetList = new ArrayList<>();
                  entrySetList.add( entrySet );
                  propertyNameKeyValueMap.put( mapKey, entrySetList );
               }

            } );

      try ( final ParquetWriter<Group> writer = ExampleParquetWriter.builder( new LocalOutputFile( Paths.get( outputPath ) ) )
            .withType( messageTypeSchema )
            .build() ) {

         final SimpleGroupFactory simpleGroupFactory = new SimpleGroupFactory( messageTypeSchema );

         propertyNameKeyValueMap.entrySet().forEach( stringListEntry -> {

            final List<Map.Entry<String, Object>> entryList = stringListEntry.getValue();

            final Group groupNew = simpleGroupFactory.newGroup();

            entryList.forEach( entry -> {
               final String fieldName = entry.getKey();
               final Object value = entry.getValue();

               if ( value != null && flattenedExampleData.containsKey( fieldName ) ) {
                  final PrimitiveType parquetType = flattenedExampleData.get( fieldName )._2;
                  addGroup( value, parquetType, fieldName, groupNew );
               }

            } );

            try {
               writer.write( groupNew );
            } catch ( final IOException ioException ) {
               throw new RuntimeException( ioException );
            }

         } );

      }
   }

   public String getFirstPartBeforeDoubleUnderscore( final String input ) {
      if ( input != null && input.contains( "__" ) ) {
         return input.split( "__" )[0];
      }
      return input; // Return original string if no double underscore found
   }

   private void extractAspectProperties( final Aspect aspect, final String prefix,
         final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final Set<String> visitedTypes ) {
      for ( final Property property : aspect.getProperties() ) {
         if ( property.isNotInPayload() ) {
            continue;
         }
         extractPropertyData( property, prefix, flattenedData, visitedTypes );
      }
   }

   private void extractPropertyData( final Property property, final String prefix,
         final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final Set<String> visitedTypes ) {
      final String columnName = prefix.isEmpty() ? property.getPayloadName() : prefix + "__" + property.getPayloadName();

      if ( property.getCharacteristic().isEmpty() ) {
         return;
      }

      final Characteristic characteristic = property.getCharacteristic().orElse( null );

      BigInteger maxLength = null;
      if ( characteristic instanceof final Trait trait ) {

         final LengthConstraint lengthConstraint = trait.getConstraints().stream()
               .filter( LengthConstraint.class::isInstance )
               .map( LengthConstraint.class::cast )
               .findFirst().orElse( null );

         if ( lengthConstraint != null ) {
            maxLength = lengthConstraint.getMaxValue().orElse( null );
         }
      }

      extractCharacteristicData( characteristic, property, columnName, flattenedData, visitedTypes, maxLength );
   }

   private void extractCharacteristicData( final Characteristic characteristic, final Property property,
         final String columnName, final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final Set<String> visitedTypes, final BigInteger maxLength ) {

      switch ( characteristic ) {
      case final Collection collection -> extractCollectionData( collection, property, columnName, flattenedData, visitedTypes, maxLength );
      case final Trait trait ->
         // Unwrap trait and process base characteristic
            extractCharacteristicData( trait.getBaseCharacteristic(), property, columnName, flattenedData, visitedTypes, maxLength );
      case final Either either -> extractEitherData( either, property, columnName, flattenedData, visitedTypes, maxLength );
      default -> extractScalarOrEntityData( characteristic, property, columnName, flattenedData, visitedTypes, maxLength );
      }
   }

   private void extractCollectionData( final Collection collection, final Property property,
         final String columnName, final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final Set<String> visitedTypes, final BigInteger maxLength ) {
      final Type dataType = collection.getDataType().orElse( null );
      if ( dataType == null ) {
         return;
      }

      if ( dataType instanceof final ComplexType complexType ) {
         // For collections of complex types, flatten the complex type properties
         extractComplexTypeProperties( complexType, columnName, flattenedData, visitedTypes );
      } else if ( dataType instanceof final Scalar scalar ) {
         // For collections of scalars, create a single column
         Object exampleValue = extractExampleValueFromProperty( property, collection );
         String language = null;
         if ( RDF.langString.getURI().equals( scalar.getUrn() ) ) {
            switch ( exampleValue ) {
            case final LangString langString -> {
               language = Optional.ofNullable( langString.getLanguageTag() ).map( Locale::getLanguage ).orElse( null );
               exampleValue = langString.getValue();
            }
            case final Map<?, ?> map when !map.isEmpty() -> {
               final Map.Entry<?, ?> firstEntry = map.entrySet().iterator().next();
               language = firstEntry.getKey().toString();
               exampleValue = firstEntry.getValue();
            }
            default -> {
               language = Locale.ENGLISH.getLanguage();
               // exampleValue remains as-is (String)
            }
            }
         }

         final Resource xsdResource = ResourceFactory.createResource( scalar.getUrn() );
         boolean isTimezoneAvailable = false;
         if ( (XSD.dateTime.equals( xsdResource ) || XSD.dateTimeStamp.equals( xsdResource ))
               && exampleValue instanceof XMLGregorianCalendar ) {
            final XMLGregorianCalendar xmlCal = (XMLGregorianCalendar) exampleValue;
            // Check if timezone is present
            if ( xmlCal.getTimezone() != DatatypeConstants.FIELD_UNDEFINED ) {
               isTimezoneAvailable = true;
            }
         }

         final PrimitiveType parquetType = mapXsdTypeToParquetType( scalar.getUrn(), columnName, language, isTimezoneAvailable, maxLength );
         flattenedData.put( language == null ? columnName : columnName + "-" + language, new Tuple2<>( exampleValue, parquetType ) );
      }
   }

   private void extractComplexTypeProperties( final ComplexType complexType, final String prefix,
         final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final Set<String> visitedTypes ) {

      final String typeKey = complexType.urn().toString();
      if ( visitedTypes.contains( typeKey ) ) {
         return; // Prevent infinite recursion
      }
      visitedTypes.add( typeKey );

      try {
         for ( final Property property : complexType.getAllProperties() ) {
            if ( property.isNotInPayload() ) {
               continue;
            }
            extractPropertyData( property, prefix, flattenedData, new HashSet<>( visitedTypes ) );
         }
      } finally {
         visitedTypes.remove( typeKey );
      }
   }

   private void extractEitherData( final Either either, final Property property,
         final String columnName, final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final Set<String> visitedTypes, final BigInteger maxLength ) {
      // For Either, extract both left and right possibilities
      extractCharacteristicData( either.getLeft(), property, columnName + "__left", flattenedData, visitedTypes, maxLength );
   }

   private void extractScalarOrEntityData( final Characteristic characteristic, final Property property,
         final String columnName, final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final Set<String> visitedTypes, final BigInteger maxLength ) {

      final Type dataType = characteristic.getDataType().orElse( null );
      if ( dataType == null ) {
         return;
      }

      if ( dataType instanceof final ComplexType complexType ) {
         extractComplexTypeProperties( complexType, columnName, flattenedData, visitedTypes );
      } else if ( dataType instanceof final Scalar scalar ) {
         columnNames.add( columnName );

         // Use the property's original characteristic (which may include Trait/constraints) for value generation
         final Characteristic originalCharacteristic = property.getCharacteristic().orElse( characteristic );
         Object exampleValue = extractExampleValueFromProperty( property, originalCharacteristic );
         String language = null;
         if ( RDF.langString.getURI().equals( scalar.getUrn() ) ) {
            switch ( exampleValue ) {
            case final LangString langString -> {
               language = Optional.ofNullable( langString.getLanguageTag() ).map( Locale::getLanguage ).orElse( null );
               exampleValue = langString.getValue();
            }
            case final Map<?, ?> map when !map.isEmpty() -> {
               final Map.Entry<?, ?> firstEntry = map.entrySet().iterator().next();
               language = firstEntry.getKey().toString();
               exampleValue = firstEntry.getValue();
            }
            default -> {
               language = Locale.ENGLISH.getLanguage();
               // exampleValue remains as-is (String)
            }
            }
         }
         final Resource xsdResource = ResourceFactory.createResource( scalar.getUrn() );
         boolean isTimezoneAvailable = false;
         if ( (XSD.dateTime.equals( xsdResource ) || XSD.dateTimeStamp.equals( xsdResource ))
               && exampleValue instanceof final XMLGregorianCalendar xmlCal
               && xmlCal.getTimezone() != DatatypeConstants.FIELD_UNDEFINED ) {
            isTimezoneAvailable = true;
         }

         final PrimitiveType parquetType = mapXsdTypeToParquetType( scalar.getUrn(), columnName, language, isTimezoneAvailable, maxLength );
         flattenedData.put( language == null ? columnName : columnName + "-" + language, new Tuple2<>( exampleValue, parquetType ) );
      }
   }

   private List<Map<String, Object>> createDenormalizedRows( final Aspect aspect,
         final Map<String, Tuple2<Object, PrimitiveType>> flattenedData ) {
      final List<Map<String, Object>> rows = new ArrayList<>();
      final Map<String, List<Property>> collectionsMap = new HashMap<>();

      // Identify collection properties
      identifyCollections( aspect.getProperties(), "", collectionsMap );

      if ( collectionsMap.isEmpty() ) {
         // No collections, create single row with all data
         final Map<String, Object> singleRow = new HashMap<>();
         flattenedData.forEach( ( key, value ) -> singleRow.put( key, value._1 ) );
         rows.add( singleRow );
      } else {
         // Create denormalized rows based on collections
         createRowsForCollections( aspect, flattenedData, rows );
      }

      return rows;
   }

   private void identifyCollections( final List<Property> properties, final String prefix,
         final Map<String, List<Property>> collectionsMap ) {
      for ( final Property property : properties ) {
         if ( property.isNotInPayload() || property.getCharacteristic().isEmpty() ) {
            continue;
         }

         final String propertyPath = prefix.isEmpty() ? property.getPayloadName() : prefix + "__" + property.getPayloadName();
         final Characteristic characteristic = property.getCharacteristic().orElse( null );

         if ( characteristic instanceof java.util.Collection ) {
            collectionsMap.computeIfAbsent( propertyPath, k -> new ArrayList<>() ).add( property );
         } else if ( characteristic != null && characteristic.getDataType().isPresent() &&
               characteristic.getDataType().orElse( null ) instanceof final ComplexType complexType
               && columnNames.contains( propertyPath ) ) {
            identifyCollections( complexType.getAllProperties(), propertyPath, collectionsMap );
         }

      }
   }

   private void createRowsForCollections( final Aspect aspect,
         final Map<String, Tuple2<Object, PrimitiveType>> flattenedData,
         final List<Map<String, Object>> rows ) {

      // Create separate rows for each top-level property group
      for ( final Property property : aspect.getProperties() ) {
         if ( property.isNotInPayload() ) {
            continue;
         }

         final String propertyName = property.getPayloadName();
         final Map<String, Object> row = new HashMap<>();

         // Initialize all columns as null
         flattenedData.keySet().forEach( key -> row.put( key, null ) );

         // Fill only the columns related to this property
         fillRowForProperty( property, propertyName, row, flattenedData );

         if ( row.values().stream().anyMatch( java.util.Objects::nonNull ) ) {
            rows.add( row );
         }
      }
   }

   private void fillRowForProperty( final Property property, final String prefix,
         final Map<String, Object> row, final Map<String, Tuple2<Object, PrimitiveType>> flattenedData ) {

      if ( property.getCharacteristic().isEmpty() ) {
         return;
      }

      final Characteristic characteristic = property.getCharacteristic().orElse( null );

      if ( characteristic instanceof final Collection collection ) {
         fillRowForCollection( collection, property, prefix, row );
      } else if ( characteristic != null && characteristic.getDataType().isPresent() &&
            characteristic.getDataType().orElse( null ) instanceof final ComplexType complexType ) {
         fillRowForComplexType( complexType, prefix, row, flattenedData );
      } else if ( characteristic != null ) {
         // Simple property
         final Object value = extractExampleValueFromProperty( property, characteristic );
         row.put( prefix, value );
      }
   }

   private void fillRowForCollection( final Collection collection, final Property property, final String prefix,
         final Map<String, Object> row ) {

      final Type dataType = collection.getDataType().orElse( null );
      if ( dataType instanceof final ComplexType complexType ) {
         // Fill complex type properties for this collection
         for ( final Property complexProperty : complexType.getAllProperties() ) {
            if ( complexProperty.isNotInPayload() ) {
               continue;
            }
            final String columnName = prefix + "__" + complexProperty.getPayloadName();
            final Object value = extractExampleValueFromProperty( complexProperty,
                  complexProperty.getCharacteristic().orElse( null ) );
            row.put( columnName, value );
         }
      } else {
         // Simple collection
         final Object value = extractExampleValueFromProperty( property, collection );
         row.put( prefix, value );
      }
   }

   private void fillRowForComplexType( final ComplexType complexType, final String prefix,
         final Map<String, Object> row, final Map<String, Tuple2<Object, PrimitiveType>> flattenedData ) {

      for ( final Property complexProperty : complexType.getAllProperties() ) {
         if ( complexProperty.isNotInPayload() ) {
            continue;
         }
         final String columnName = prefix + "__" + complexProperty.getPayloadName();
         fillRowForProperty( complexProperty, columnName, row, flattenedData );
      }
   }

   private void addGroup( final Object value, final PrimitiveType primitiveType, final String fieldName, final Group group ) {
      if ( value == null ) {
         return;
      }

      final LogicalTypeAnnotation logicalType = primitiveType.getLogicalTypeAnnotation();
      final int typeLength = primitiveType.getTypeLength();

      if ( logicalType != null ) {
         // Handle logical types
         if ( logicalType instanceof LogicalTypeAnnotation.StringLogicalTypeAnnotation ) {
            group.add( fieldName, value.toString() );
         } else if ( logicalType instanceof LogicalTypeAnnotation.DateLogicalTypeAnnotation ) {
            // Handle date values - convert to days since epoch (1970-01-01)
            if ( value instanceof final XMLGregorianCalendar xmlCal ) {
               // Convert XMLGregorianCalendar to LocalDate then to days since epoch (1970-01-01)
               final LocalDate localDate = LocalDate.of( xmlCal.getYear(), xmlCal.getMonth(), xmlCal.getDay() );
               final long daysSinceEpoch = localDate.toEpochDay();
               group.add( fieldName, (int) daysSinceEpoch );
            } else if ( value instanceof final Number number ) {
               group.add( fieldName, number.intValue() );
            } else if ( value instanceof String ) {
               try {
                  // Parse ISO date string (YYYY-MM-DD) and convert to days since epoch
                  final LocalDate date = LocalDate.parse( value.toString() );
                  final long daysSinceEpoch = date.toEpochDay();
                  group.add( fieldName, (int) daysSinceEpoch );
               } catch ( final Exception _ ) {
                  // Fallback to 0 (epoch) if parsing fails
                  group.add( fieldName, 0 );
               }
            } else {
               group.add( fieldName, 0 );
            }
         } else if ( logicalType instanceof LogicalTypeAnnotation.TimestampLogicalTypeAnnotation ) {
            if ( value instanceof XMLGregorianCalendar ) {
               XMLGregorianCalendar xmlCal = (XMLGregorianCalendar) value;
               // Check if timezone is present
               if ( xmlCal.getTimezone() != DatatypeConstants.FIELD_UNDEFINED ) {
                  // Normalize this instance to UTC.
                  xmlCal = xmlCal.normalize();
               }
               final long timestampMicros = xmlCal.toGregorianCalendar().getTimeInMillis() * 1000; // Convert to microseconds
               group.add( fieldName, timestampMicros );
            } else if ( value instanceof final Number number ) {
               group.add( fieldName, number.longValue() );
            }
         } else {
            // Default handling for other logical types
            addValueByPrimitiveType( value, primitiveType.getPrimitiveTypeName(), fieldName, group, typeLength );
         }
      } else {
         // No logical type annotation, use primitive type
         addValueByPrimitiveType( value, primitiveType.getPrimitiveTypeName(), fieldName, group, typeLength );
      }

   }

   private void addValueByPrimitiveType( final Object value, final PrimitiveType.PrimitiveTypeName primitiveTypeName,
         final String fieldName, final Group group, final int typeLength ) {
      switch ( primitiveTypeName ) {
      case INT32:
         if ( value instanceof final Number number ) {
            group.add( fieldName, number.intValue() );
         } else if ( value instanceof final String stringValue ) {
            try {
               group.add( fieldName, Integer.parseInt( stringValue ) );
            } catch ( final NumberFormatException _ ) {
               group.add( fieldName, 0 );
            }
         }
         break;
      case INT64:
         if ( value instanceof final Number number ) {
            group.add( fieldName, number.longValue() );
         } else if ( value instanceof final String stringValue ) {
            try {
               group.add( fieldName, Long.parseLong( stringValue ) );
            } catch ( final NumberFormatException _ ) {
               group.add( fieldName, 0L );
            }
         }
         break;
      case FLOAT:
         if ( value instanceof final Number number ) {
            group.add( fieldName, number.floatValue() );
         } else if ( value instanceof final String stringValue ) {
            try {
               group.add( fieldName, Float.parseFloat( stringValue ) );
            } catch ( final NumberFormatException _ ) {
               group.add( fieldName, 0.0f ); // Use default instead of writing string to float column
            }
         }
         break;
      case DOUBLE:
         if ( value instanceof final Number number ) {
            group.add( fieldName, number.doubleValue() );
         } else if ( value instanceof final String stringValue ) {
            try {
               group.add( fieldName, Double.parseDouble( stringValue ) );
            } catch ( final NumberFormatException _ ) {
               group.add( fieldName, 0.0d );
            }
         }
         break;
      case BOOLEAN:
         if ( value instanceof final Boolean booleanValue ) {
            group.add( fieldName, booleanValue );
         } else if ( value instanceof final String stringValue ) {
            group.add( fieldName, Boolean.parseBoolean( stringValue ) );
         }
         break;
      case FIXED_LEN_BYTE_ARRAY:
         final String stringValue = value.toString();
         final byte[] bytes = stringValue.getBytes( StandardCharsets.UTF_8 );
         final byte[] paddedBytes = new byte[typeLength];
         System.arraycopy( bytes, 0, paddedBytes, 0, Math.min( bytes.length, typeLength ) );
         group.add( fieldName, Binary.fromConstantByteArray( paddedBytes ) );
         break;
      case BINARY:
      default:
         group.add( fieldName, value.toString() );
         break;
      }
   }

   public static final String TYPE = "@type";
}
