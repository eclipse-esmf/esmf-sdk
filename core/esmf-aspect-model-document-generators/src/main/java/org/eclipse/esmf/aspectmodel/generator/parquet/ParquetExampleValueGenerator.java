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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.esmf.aspectmodel.generator.Range;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.characteristic.Collection;
import org.eclipse.esmf.metamodel.characteristic.Either;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.characteristic.State;
import org.eclipse.esmf.metamodel.characteristic.Trait;
import org.eclipse.esmf.metamodel.constraint.FixedPointConstraint;
import org.eclipse.esmf.metamodel.constraint.RegularExpressionConstraint;
import org.eclipse.esmf.metamodel.datatype.CurieType;
import org.eclipse.esmf.metamodel.datatype.SammType;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;

import com.github.curiousoddman.rgxgen.RgxGen;

/**
 * Generates random example values suitable for Parquet payload generation.
 *
 * <p>
 * This class follows the visitor pattern (like {@code JsonPayloadGenerator}) and dispatches
 * on the Aspect Model element type to produce raw Java objects (Number, String,
 * XMLGregorianCalendar, etc.) that can be written directly into Parquet groups.
 * </p>
 *
 * @see org.eclipse.esmf.aspectmodel.generator.json.JsonPayloadGenerator
 */
public class ParquetExampleValueGenerator implements AspectVisitor<Object, ParquetExampleValueGenerator.Context> {

   private final Random random;

   /**
    * Traversal context carrying constraints that apply to the current position.
    *
    * @param constraints the constraints that apply at the current position in the model
    */
   public record Context(
         List<Constraint> constraints
   ) {
      public Context() {
         this( List.of() );
      }

      public Context withConstraints( final List<Constraint> constraints ) {
         return new Context( constraints );
      }
   }

   public ParquetExampleValueGenerator( final Random random ) {
      this.random = random;
   }

   @Override
   public Object visitBase( final ModelElement modelElement, final Context context ) {
      throw new UnsupportedOperationException( "Cannot generate example value for " + modelElement );
   }

   @Override
   public Object visitProperty( final Property property, final Context context ) {
      return property.getCharacteristic()
            .map( c -> c.accept( this, context ) )
            .orElse( null );
   }

   @Override
   public Object visitCharacteristic( final Characteristic characteristic, final Context context ) {
      return characteristic.getDataType()
            .map( t -> t.accept( this, context ) )
            .orElse( null );
   }

   @Override
   public Object visitTrait( final Trait trait, final Context context ) {
      return trait.getBaseCharacteristic().accept( this, context.withConstraints( trait.getConstraints() ) );
   }

   @Override
   public Object visitEnumeration( final Enumeration enumeration, final Context context ) {
      return enumeration.getValues().getFirst().accept( this, context );
   }

   @Override
   public Object visitState( final State state, final Context context ) {
      return state.getDefaultValue().accept( this, context );
   }

   @Override
   public Object visitEither( final Either either, final Context context ) {
      // For Parquet payloads the left type is used by convention
      return either.getLeft().accept( this, context );
   }

   @Override
   public Object visitCollection( final Collection collection, final Context context ) {
      final ModelElement elementType = collection.getElementCharacteristic()
            .map( ModelElement.class::cast )
            .or( collection::getDataType )
            .orElseThrow( () -> new IllegalArgumentException(
                  "Collection " + collection.getName() + " has neither dataType nor elementCharacteristic" ) );
      return elementType.accept( this, context.withConstraints( List.of() ) );
   }

   @Override
   public Object visitScalarValue( final ScalarValue scalarValue, final Context context ) {
      return scalarValue.getValue();
   }

   @Override
   public Object visitScalar( final Scalar scalar, final Context context ) {
      final SammType<?> sammType = SammXsdType.ALL_TYPES.stream()
            .filter( type -> type.getUrn().equals( scalar.getUrn() ) )
            .findFirst()
            .orElseThrow( () -> new IllegalArgumentException( "Encountered unknown type: " + scalar.getUrn() ) );
      return sammType.accept( this, context );
   }

   @Override
   public Object visitXsdBoolean( final SammType.XsdBoolean booleanType, final Context context ) {
      return randomValueOf( true, false );
   }

   @Override
   public Object visitXsdDecimal( final SammType.XsdDecimal decimal, final Context context ) {
      return visitXsdDouble( null, context );
   }

   @Override
   public Object visitXsdInteger( final SammType.XsdInteger integerType, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( integerType.lowerBound(), integerType.upperBound() );
      return randomNumber( range.min(), range.max() ).intValue();
   }

   @Override
   public Object visitXsdDouble( final SammType.XsdDouble doubleType, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), true )
            .clamp( -100000, 100000 );
      final Optional<FixedPointConstraint> fixedPoint = context.constraints().stream()
            .filter( c -> c.is( FixedPointConstraint.class ) )
            .map( c -> c.as( FixedPointConstraint.class ) )
            .findFirst();
      return randomFloatingPointNumber( range.min(), range.max(), fixedPoint ).doubleValue();
   }

   @Override
   public Object visitXsdFloat( final SammType.XsdFloat floatType, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), true )
            .clamp( -100000, 100000 );
      final Optional<FixedPointConstraint> fixedPoint = context.constraints().stream()
            .filter( c -> c.is( FixedPointConstraint.class ) )
            .map( c -> c.as( FixedPointConstraint.class ) )
            .findFirst();
      return randomFloatingPointNumber( range.min(), range.max(), fixedPoint ).floatValue();
   }

   @Override
   public Object visitXsdDate( final SammType.XsdDate date, final Context context ) {
      final LocalDateTime now = LocalDateTime.now();
      final XMLGregorianCalendar calendar = DatatypeFactory.newDefaultInstance()
            .newXMLGregorianCalendar( now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                  DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
                  DatatypeConstants.FIELD_UNDEFINED, 0 );
      return calendar.toXMLFormat();
   }

   @Override
   public Object visitXsdTime( final SammType.XsdTime time, final Context context ) {
      final LocalDateTime now = LocalDateTime.now();
      final XMLGregorianCalendar calendar = DatatypeFactory.newDefaultInstance()
            .newXMLGregorianCalendar( DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
                  DatatypeConstants.FIELD_UNDEFINED,
                  now.getHour(), now.getMinute(), now.getSecond(), 0, 0 );
      return calendar.toXMLFormat();
   }

   @Override
   public Object visitXsdDateTime( final SammType.XsdDateTime dateTime, final Context context ) {
      final LocalDateTime now = LocalDateTime.now();
      return DatatypeFactory.newDefaultInstance()
            .newXMLGregorianCalendar( now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
                  now.getHour(), now.getMinute(), now.getSecond(), 0, 0 );
   }

   @Override
   public Object visitXsdDateTimeStamp( final SammType.XsdDateTimeStamp dateTimeStamp, final Context context ) {
      return visitXsdDateTime( null, context );
   }

   @Override
   public Object visitXsdGYear( final SammType.XsdGYear gYear, final Context context ) {
      return "" + LocalDateTime.now().getYear();
   }

   @Override
   public Object visitXsdGMonth( final SammType.XsdGMonth gMonth, final Context context ) {
      return "--%02d".formatted( LocalDateTime.now().getMonthValue() );
   }

   @Override
   public Object visitXsdGDay( final SammType.XsdGDay gDay, final Context context ) {
      return "---%02d".formatted( LocalDateTime.now().getDayOfMonth() );
   }

   @Override
   public Object visitXsdGYearMonth( final SammType.XsdGYearMonth gYearMonth, final Context context ) {
      final LocalDateTime now = LocalDateTime.now();
      return "%04d-%02d".formatted( now.getYear(), now.getMonthValue() );
   }

   @Override
   public Object visitXsdGMonthDay( final SammType.XsdMonthDay monthDay, final Context context ) {
      final LocalDateTime now = LocalDateTime.now();
      return "--%02d-%02d".formatted( now.getMonthValue(), now.getDayOfMonth() );
   }

   @Override
   public Object visitXsdDuration( final SammType.XsdDuration duration, final Context context ) {
      return DatatypeFactory.newDefaultInstance().newDuration( randomLong( 1, 86400000L ) ).toString();
   }

   @Override
   public Object visitXsdYearMonthDuration( final SammType.XsdYearMonthDuration yearMonthDuration, final Context context ) {
      return randomValueOf( "P10M", "P5Y2M" );
   }

   @Override
   public Object visitXsdDayTimeDuration( final SammType.XsdDayTimeDuration dayTimeDuration, final Context context ) {
      return randomValueOf( "P30D", "P1DT5H", "PT1H5M0S" );
   }

   @Override
   public Object visitXsdByte( final SammType.XsdByte byteType, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( byteType.lowerBound(), byteType.upperBound() );
      return randomNumber( range.min(), range.max() ).byteValue();
   }

   @Override
   public Object visitXsdShort( final SammType.XsdShort shortType, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( shortType.lowerBound(), shortType.upperBound() );
      return randomNumber( range.min(), range.max() ).shortValue();
   }

   @Override
   public Object visitXsdInt( final SammType.XsdInt intType, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( intType.lowerBound(), intType.upperBound() );
      return randomNumber( range.min(), range.max() ).intValue();
   }

   @Override
   public Object visitXsdLong( final SammType.XsdLong longType, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( longType.lowerBound(), longType.upperBound() );
      return randomNumber( range.min(), range.max() ).longValue();
   }

   @Override
   public Object visitXsdUnsignedByte( final SammType.XsdUnsignedByte unsignedByte, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( unsignedByte.lowerBound(), unsignedByte.upperBound() );
      return randomNumber( range.min(), range.max() ).shortValue();
   }

   @Override
   public Object visitXsdUnsignedShort( final SammType.XsdUnsignedShort unsignedShort, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( unsignedShort.lowerBound(), unsignedShort.upperBound() );
      return randomNumber( range.min(), range.max() ).intValue();
   }

   @Override
   public Object visitXsdUnsignedInt( final SammType.XsdUnsignedInt unsignedInt, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( unsignedInt.lowerBound(), unsignedInt.upperBound() );
      return randomNumber( range.min(), range.max() ).longValue();
   }

   @Override
   public Object visitXsdUnsignedLong( final SammType.XsdUnsignedLong unsignedLong, final Context context ) {
      // Parquet stores unsignedLong as INT64, so clamp to Long.MAX_VALUE to prevent overflow
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( unsignedLong.lowerBound(), unsignedLong.upperBound() )
            .clamp( 0L, Long.MAX_VALUE );
      return randomNumber( range.min(), range.max() ).longValue();
   }

   @Override
   public Object visitXsdPositiveInteger( final SammType.XsdPositiveInteger positiveInteger, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( positiveInteger.lowerBound(), positiveInteger.upperBound() )
            .clamp( null, 100000L );
      return randomNumber( range.min(), range.max() ).intValue();
   }

   @Override
   public Object visitXsdNonNegativeInteger( final SammType.XsdNonNegativeInteger nonNegativeInteger, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( nonNegativeInteger.lowerBound(), nonNegativeInteger.upperBound() )
            .clamp( null, 100000L );
      return randomNumber( range.min(), range.max() ).intValue();
   }

   @Override
   public Object visitXsdNegativeInteger( final SammType.XsdNegativeInteger negativeInteger, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( negativeInteger.lowerBound(), negativeInteger.upperBound() )
            .clamp( -100000L, null );
      return randomNumber( range.min(), range.max() ).intValue();
   }

   @Override
   public Object visitXsdNonPositiveInteger( final SammType.XsdNonPositiveInteger nonPositiveInteger, final Context context ) {
      final Range range = Range.fromRangeConstraints( context.constraints(), false )
            .clamp( nonPositiveInteger.lowerBound(), nonPositiveInteger.upperBound() )
            .clamp( -100000L, null );
      return randomNumber( range.min(), range.max() ).intValue();
   }

   @Override
   public Object visitXsdHexBinary( final SammType.XsdHexBinary hexBinary, final Context context ) {
      final Range range = Range.fromLengthConstraints( context.constraints() ).clamp( 1, 10 );
      final byte[] bytes = randomString( range.min().intValue(), range.max().intValue() ).getBytes( StandardCharsets.UTF_8 );
      return HexFormat.of().formatHex( bytes );
   }

   @Override
   public Object visitXsdBase64Binary( final SammType.XsdBase64Binary base64Binary, final Context context ) {
      final Range range = Range.fromLengthConstraints( context.constraints() ).clamp( 1, 10 );
      final byte[] bytes = randomString( range.min().intValue(), range.max().intValue() ).getBytes( StandardCharsets.UTF_8 );
      return Base64.getEncoder().encodeToString( bytes );
   }

   @Override
   public Object visitXsdAnyUri( final SammType.XsdAnyUri anyUri, final Context context ) {
      return "https://example.com/" + randomString( 5, 10 );
   }

   @Override
   public Object visitCurieType( final CurieType curieType, final Context context ) {
      return randomValueOf( "unit:kilometre", "unit:hectopascal", "unit:newton" );
   }

   @Override
   public Object visitXsdString( final SammType.XsdString string, final Context context ) {
      return context.constraints().stream()
            .flatMap( constraint -> {
               if ( constraint instanceof final RegularExpressionConstraint regex ) {
                  return Stream.of( RgxGen.parse( regex.getValue() ).generate( random ) );
               }
               return Stream.empty();
            } )
            .findFirst()
            .orElseGet( () -> {
               final Range range = Range.fromLengthConstraints( context.constraints() ).clamp( 1, 10 );
               return randomString( range.min().intValue(), range.max().intValue() );
            } );
   }

   @Override
   public Object visitRdfLangString( final SammType.RdfLangString langString, final Context context ) {
      final String languageTag = randomValueOf( "en", "de" );
      return java.util.Map.of( languageTag, randomString( 5, 20 ) );
   }

   BigDecimal randomFloatingPointNumber( final BigDecimal start, final BigDecimal end,
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

   BigDecimal randomNumber( final BigDecimal minInclusive, final BigDecimal maxInclusive ) {
      final BigDecimal min = minInclusive == null ? BigDecimal.valueOf( Long.MIN_VALUE ) : minInclusive;
      final BigDecimal max = maxInclusive == null ? BigDecimal.valueOf( Long.MAX_VALUE ) : maxInclusive;
      if ( min.equals( max ) ) {
         return min;
      }
      if ( min.compareTo( max ) >= 0 ) {
         return min;
      }
      final int numDigits = Math.max( min.precision(), max.precision() );
      final int numBits = (int) ( numDigits / Math.log10( 2.0 ) );
      final BigDecimal factor = new BigDecimal( new BigInteger( numBits, random ) ).movePointLeft( numDigits );
      return min.add( max.subtract( min ).multiply( factor, new MathContext( numDigits ) ) );
   }

   int randomInt( final int startInclusive, final int endInclusive ) {
      if ( startInclusive == endInclusive ) {
         return startInclusive;
      }
      if ( startInclusive > endInclusive ) {
         return startInclusive;
      }
      return random.nextInt( startInclusive, endInclusive == Integer.MAX_VALUE ? Integer.MAX_VALUE : endInclusive + 1 );
   }

   private long randomLong( final long min, final long max ) {
      if ( min == max ) {
         return min;
      }
      return random.nextLong( min, max );
   }

   private String randomString( final int minLength, final int maxLength ) {
      final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
      final int length = randomInt( minLength, maxLength );
      final StringBuilder builder = new StringBuilder( length );
      for ( int i = 0; i < length; i++ ) {
         builder.append( characters.charAt( random.nextInt( characters.length() ) ) );
      }
      return builder.toString();
   }

   @SuppressWarnings( "FinalMethod" )
   @SafeVarargs
   private final <T> T randomValueOf( final T... values ) {
      if ( values.length == 0 ) {
         throw new IndexOutOfBoundsException();
      }
      return values[randomInt( 0, values.length - 1 )];
   }
}
