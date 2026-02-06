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

package org.eclipse.esmf.metamodel.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.ValueParsingException;
import org.eclipse.esmf.aspectmodel.visitor.AspectVisitor;
import org.eclipse.esmf.metamodel.Scalar;

import io.vavr.control.Try;
import jakarta.xml.bind.DatatypeConverter;
import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.vocabulary.RDF;

/**
 * Represents a scalar datatype, such as xsd:integer, and provides parser and serializer
 *
 * @param <T> the Java type corresponding to values of this type
 */
public sealed interface SammType<T> extends RDFDatatype, Scalar
      permits CurieType, SammType.IntegerType, SammType.RdfLangString, SammType.XsdAnyUri, SammType.XsdBase64Binary, SammType.XsdBoolean,
      SammType.XsdByte, SammType.XsdDate, SammType.XsdDateTime, SammType.XsdDateTimeStamp, SammType.XsdDayTimeDuration, SammType.XsdDecimal,
      SammType.XsdDouble, SammType.XsdDuration, SammType.XsdFloat, SammType.XsdGDay, SammType.XsdGMonth, SammType.XsdGYear,
      SammType.XsdGYearMonth, SammType.XsdHexBinary, SammType.XsdInt, SammType.XsdInteger, SammType.XsdLong, SammType.XsdMonthDay,
      SammType.XsdNegativeInteger, SammType.XsdNonNegativeInteger, SammType.XsdNonPositiveInteger, SammType.XsdPositiveInteger,
      SammType.XsdShort, SammType.XsdString, SammType.XsdTime, SammType.XsdUnsignedByte, SammType.XsdUnsignedInt, SammType.XsdUnsignedLong,
      SammType.XsdUnsignedShort, SammType.XsdYearMonthDuration, SammXsdType, SammXsdType.SammExtendedXsdType {
   /**
    * Parses a lexical representation of a value of the type
    *
    * @param lexicalForm the lexical representation
    * @return if the lexical representation is valid for the type, Optional.of(x) where x is an object of the corresponding Java type (@see
    * {@link #getJavaClass()}), otherwise Optional.empty.
    */
   Optional<T> parseTyped( String lexicalForm );

   /**
    * Serializes a value of this type to a String
    *
    * @param value the value
    * @return the corresponding lexical representation
    */
   String serialize( T value );

   @Override
   Class<T> getJavaClass();

   @Override
   default String getUrn() {
      return getURI();
   }

   /**
    * Type definitions are "built-in" into SAMM and are therefore not defined in specific Aspect Model Files
    */
   @Override
   default AspectModelFile getSourceFile() {
      return null;
   }

   SammType<String> STRING = new XsdString();
   SammType<Boolean> BOOLEAN = new XsdBoolean();
   SammType<BigDecimal> DECIMAL = new XsdDecimal();
   SammType<BigInteger> INTEGER = new XsdInteger();
   SammType<Double> DOUBLE = new XsdDouble();
   SammType<Float> FLOAT = new XsdFloat();
   SammType<XMLGregorianCalendar> DATE = new XsdDate();
   SammType<XMLGregorianCalendar> TIME = new XsdTime();
   SammType<XMLGregorianCalendar> DATE_TIME = new XsdDateTime();
   SammType<XMLGregorianCalendar> DATE_TIME_STAMP = new XsdDateTimeStamp();
   SammType<XMLGregorianCalendar> G_YEAR = new XsdGYear();
   SammType<XMLGregorianCalendar> G_MONTH = new XsdGMonth();
   SammType<XMLGregorianCalendar> G_DAY = new XsdGDay();
   SammType<XMLGregorianCalendar> G_YEAR_MONTH = new XsdGYearMonth();
   SammType<XMLGregorianCalendar> G_MONTH_DAY = new XsdMonthDay();
   SammType<Duration> DURATION = new XsdDuration();
   SammType<Duration> YEAR_MONTH_DURATION = new XsdYearMonthDuration();
   SammType<Duration> DAY_TIME_DURATION = new XsdDayTimeDuration();
   SammType<Byte> BYTE = new XsdByte();
   SammType<Short> SHORT = new XsdShort();
   SammType<Integer> INT = new XsdInt();
   SammType<Long> LONG = new XsdLong();
   SammType<Short> UNSIGNED_BYTE = new XsdUnsignedByte();
   SammType<Integer> UNSIGNED_SHORT = new XsdUnsignedShort();
   SammType<Long> UNSIGNED_INT = new XsdUnsignedInt();
   SammType<BigInteger> UNSIGNED_LONG = new XsdUnsignedLong();
   SammType<BigInteger> POSITIVE_INTEGER = new XsdPositiveInteger();
   SammType<BigInteger> NON_NEGATIVE_INTEGER = new XsdNonNegativeInteger();
   SammType<BigInteger> NEGATIVE_INTEGER = new XsdNegativeInteger();
   SammType<BigInteger> NON_POSITIVE_INTEGER = new XsdNonPositiveInteger();
   SammType<byte[]> HEX_BINARY = new XsdHexBinary();
   SammType<byte[]> BASE64_BINARY = new XsdBase64Binary();
   SammType<URI> ANY_URI = new XsdAnyUri();
   SammType<LangString> LANG_STRING = new RdfLangString();
   SammType<Curie> CURIE = CurieType.INSTANCE;

   /**
    * Numeric types with integer numbers
    *
    * @param <T> the Java type corresponding to values of this type
    */
   sealed interface IntegerType<T> extends SammType<T> permits XsdInteger, XsdByte, XsdShort, XsdInt, XsdLong,
         XsdUnsignedByte, XsdUnsignedShort, XsdUnsignedInt, XsdUnsignedLong, XsdPositiveInteger, XsdNonNegativeInteger,
         XsdNonPositiveInteger, XsdNegativeInteger {
      /**
       * The minimum value for this type
       *
       * @return the lower bound
       */
      Optional<BigInteger> lowerBound();

      /**
       * The maximum value for this type
       *
       * @return the upper bound
       */
      Optional<BigInteger> upperBound();
   }

   final class RdfLangString extends BaseDatatype implements SammType<LangString> {
      public RdfLangString() {
         super( RDF.langString.getURI() );
      }

      @Override
      public Class<LangString> getJavaClass() {
         return LangString.class;
      }

      @Override
      public Optional<LangString> parseTyped( final String lexicalForm ) {
         // This method should never be called, because rdf:langString values must be parsed together with their
         // language tag; this is done in the RDF/Turtle parser.
         throw new UnsupportedOperationException();
      }

      @Override
      public String serialize( final LangString value ) {
         return value.toString();
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitRdfLangString( this, context );
      }
   }

   final class XsdString extends SammXsdType<String> implements SammType<String> {
      public XsdString() {
         super( org.apache.jena.vocabulary.XSD.xstring, String.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return true;
      }

      @Override
      protected String parseTypedValue( final String lexicalForm ) {
         return lexicalForm;
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdString( this, context );
      }
   }

   final class XsdBoolean extends SammXsdType<Boolean> implements SammType<Boolean> {
      private XsdBoolean() {
         super( org.apache.jena.vocabulary.XSD.xboolean, Boolean.class );
      }

      @Override
      public Boolean parseTypedValue( final String lexicalForm ) {
         return switch ( lexicalForm.toLowerCase() ) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new ValueParsingException( org.apache.jena.vocabulary.XSD.xboolean, lexicalForm );
         };
      }

      @Override
      public String serialize( final Boolean value ) {
         return value.toString();
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDboolean.isValid( lexicalForm );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdBoolean( this, context );
      }
   }

   final class XsdDecimal extends SammXsdType<BigDecimal> implements SammType<BigDecimal> {
      private XsdDecimal() {
         super( org.apache.jena.vocabulary.XSD.decimal, BigDecimal.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDdecimal.isValid( lexicalForm );
      }

      @Override
      public BigDecimal parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> new BigDecimal( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.decimal, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdDecimal( this, context );
      }
   }

   final class XsdInteger extends SammXsdType<BigInteger> implements SammType<BigInteger>, IntegerType<BigInteger> {
      private XsdInteger() {
         super( org.apache.jena.vocabulary.XSD.integer, BigInteger.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDinteger.isValid( lexicalForm );
      }

      @Override
      public BigInteger parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> new BigInteger( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.decimal, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdInteger( this, context );
      }

      @Override
      public Optional<BigInteger> lowerBound() {
         return Optional.empty();
      }

      @Override
      public Optional<BigInteger> upperBound() {
         return Optional.empty();
      }
   }

   final class XsdDouble extends SammXsdType<Double> implements SammType<Double> {
      private XsdDouble() {
         super( org.apache.jena.vocabulary.XSD.xdouble, Double.class );
      }

      @Override
      public String serialize( final Double value ) {
         if ( value.isInfinite() ) {
            return value > 0.0d ? "INF" : "-INF";
         }
         return value.toString();
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDdouble.isValid( lexicalForm );
      }

      @Override
      public Double parseTypedValue( final String lexicalForm ) {
         return switch ( lexicalForm.toUpperCase() ) {
            case "INF" -> Double.POSITIVE_INFINITY;
            case "-INF" -> Double.NEGATIVE_INFINITY;
            default -> Try.of( () -> Double.parseDouble( lexicalForm ) )
                  .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.xdouble, lexicalForm, cause ) );
         };
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdDouble( this, context );
      }
   }

   final class XsdFloat extends SammXsdType<Float> implements SammType<Float> {
      private XsdFloat() {
         super( org.apache.jena.vocabulary.XSD.xfloat, Float.class );
      }

      @Override
      public String serialize( final Float value ) {
         if ( value.isInfinite() ) {
            return value > 0.0f ? "INF" : "-INF";
         }
         return value.toString();
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDfloat.isValid( lexicalForm );
      }

      @Override
      public Float parseTypedValue( final String lexicalForm ) {
         return switch ( lexicalForm.toUpperCase() ) {
            case "INF" -> Float.POSITIVE_INFINITY;
            case "-INF" -> Float.NEGATIVE_INFINITY;
            default -> Try.of( () -> Float.parseFloat( lexicalForm ) )
                  .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.xfloat, lexicalForm, cause ) );
         };
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdFloat( this, context );
      }
   }

   final class XsdDate extends SammXsdType<XMLGregorianCalendar> implements SammType<XMLGregorianCalendar> {
      private XsdDate() {
         super( org.apache.jena.vocabulary.XSD.date, XMLGregorianCalendar.class );
      }

      @Override
      public String serialize( final XMLGregorianCalendar value ) {
         return value.toXMLFormat();
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDdate.isValid( lexicalForm );
      }

      @Override
      public XMLGregorianCalendar parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> datatypeFactory.newXMLGregorianCalendar( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.date, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdDate( this, context );
      }
   }

   final class XsdTime extends SammXsdType<XMLGregorianCalendar> implements SammType<XMLGregorianCalendar> {
      private XsdTime() {
         super( org.apache.jena.vocabulary.XSD.time, XMLGregorianCalendar.class );
      }

      @Override
      public String serialize( final XMLGregorianCalendar value ) {
         return value.toXMLFormat();
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDtime.isValid( lexicalForm );
      }

      @Override
      public XMLGregorianCalendar parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> datatypeFactory.newXMLGregorianCalendar( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.time, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdTime( this, context );
      }
   }

   final class XsdDateTime extends SammXsdType<XMLGregorianCalendar> implements SammType<XMLGregorianCalendar> {
      private XsdDateTime() {
         super( org.apache.jena.vocabulary.XSD.dateTime, XMLGregorianCalendar.class );
      }

      @Override
      public String serialize( final XMLGregorianCalendar value ) {
         return value.toXMLFormat();
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDdateTimeStamp.isValid( lexicalForm );
      }

      @Override
      public XMLGregorianCalendar parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> datatypeFactory.newXMLGregorianCalendar( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.dateTime, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdDateTime( this, context );
      }
   }

   final class XsdDateTimeStamp extends SammXsdType.SammExtendedXsdType<XMLGregorianCalendar> implements SammType<XMLGregorianCalendar> {
      private XsdDateTimeStamp() {
         super( org.apache.jena.vocabulary.XSD.dateTimeStamp, XMLGregorianCalendar.class );
      }

      @Override
      public String serialize( final XMLGregorianCalendar value ) {
         return value.toXMLFormat();
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDdateTimeStamp.isValid( lexicalForm );
      }

      @Override
      public XMLGregorianCalendar parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> SammXsdType.datatypeFactory.newXMLGregorianCalendar( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.dateTimeStamp, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdDateTimeStamp( this, context );
      }
   }

   @SuppressWarnings( "checkstyle:AbbreviationAsWordInName" )
   final class XsdGYear extends SammXsdType<XMLGregorianCalendar> implements SammType<XMLGregorianCalendar> {
      private XsdGYear() {
         super( org.apache.jena.vocabulary.XSD.gYear, XMLGregorianCalendar.class );
      }

      @Override
      public String serialize( final XMLGregorianCalendar value ) {
         return value.toXMLFormat();
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDgYear.isValid( lexicalForm );
      }

      @Override
      public XMLGregorianCalendar parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> datatypeFactory.newXMLGregorianCalendar( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.gYear, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdGYear( this, context );
      }
   }

   @SuppressWarnings( "checkstyle:AbbreviationAsWordInName" )
   final class XsdGMonth extends SammXsdType<XMLGregorianCalendar> implements SammType<XMLGregorianCalendar> {
      private XsdGMonth() {
         super( org.apache.jena.vocabulary.XSD.gMonth, XMLGregorianCalendar.class );
      }

      @Override
      public String serialize( final XMLGregorianCalendar value ) {
         return value.toXMLFormat();
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDgMonth.isValid( lexicalForm );
      }

      @Override
      public XMLGregorianCalendar parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> datatypeFactory.newXMLGregorianCalendar( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.gMonth, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdGMonth( this, context );
      }
   }

   @SuppressWarnings( "checkstyle:AbbreviationAsWordInName" )
   final class XsdGDay extends SammXsdType<XMLGregorianCalendar> implements SammType<XMLGregorianCalendar> {
      private XsdGDay() {
         super( org.apache.jena.vocabulary.XSD.gDay, XMLGregorianCalendar.class );
      }

      @Override
      public String serialize( final XMLGregorianCalendar value ) {
         return value.toXMLFormat();
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDgDay.isValid( lexicalForm );
      }

      @Override
      public XMLGregorianCalendar parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> datatypeFactory.newXMLGregorianCalendar( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.gDay, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdGDay( this, context );
      }
   }

   @SuppressWarnings( "checkstyle:AbbreviationAsWordInName" )
   final class XsdGYearMonth extends SammXsdType<XMLGregorianCalendar> implements SammType<XMLGregorianCalendar> {
      private XsdGYearMonth() {
         super( org.apache.jena.vocabulary.XSD.gYearMonth, XMLGregorianCalendar.class );
      }

      @Override
      public String serialize( final XMLGregorianCalendar value ) {
         return value.toXMLFormat();
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDgYearMonth.isValid( lexicalForm );
      }

      @Override
      public XMLGregorianCalendar parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> datatypeFactory.newXMLGregorianCalendar( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.gYearMonth, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdGYearMonth( this, context );
      }
   }

   final class XsdMonthDay extends SammXsdType<XMLGregorianCalendar> implements SammType<XMLGregorianCalendar> {
      private XsdMonthDay() {
         super( org.apache.jena.vocabulary.XSD.gMonthDay, XMLGregorianCalendar.class );
      }

      @Override
      public String serialize( final XMLGregorianCalendar value ) {
         return value.toXMLFormat();
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDgMonthDay.isValid( lexicalForm );
      }

      @Override
      public XMLGregorianCalendar parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> datatypeFactory.newXMLGregorianCalendar( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.gMonthDay, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdGMonthDay( this, context );
      }
   }

   final class XsdDuration extends SammXsdType<javax.xml.datatype.Duration> implements SammType<javax.xml.datatype.Duration> {
      private XsdDuration() {
         super( org.apache.jena.vocabulary.XSD.duration, javax.xml.datatype.Duration.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDduration.isValid( lexicalForm );
      }

      @Override
      public javax.xml.datatype.Duration parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> datatypeFactory.newDuration( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.duration, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdDuration( this, context );
      }
   }

   final class XsdYearMonthDuration extends SammXsdType.SammExtendedXsdType<javax.xml.datatype.Duration>
         implements SammType<javax.xml.datatype.Duration> {
      private XsdYearMonthDuration() {
         super( org.apache.jena.vocabulary.XSD.yearMonthDuration, javax.xml.datatype.Duration.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDyearMonthDuration.isValid( lexicalForm );
      }

      @Override
      public javax.xml.datatype.Duration parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> SammXsdType.datatypeFactory.newDurationYearMonth( lexicalForm ) )
               .getOrElseThrow(
                     cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.yearMonthDuration, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdYearMonthDuration( this, context );
      }
   }

   final class XsdDayTimeDuration extends SammXsdType.SammExtendedXsdType<javax.xml.datatype.Duration>
         implements SammType<javax.xml.datatype.Duration> {
      private XsdDayTimeDuration() {
         super( org.apache.jena.vocabulary.XSD.dayTimeDuration, javax.xml.datatype.Duration.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDdayTimeDuration.isValid( lexicalForm );
      }

      @Override
      public javax.xml.datatype.Duration parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> SammXsdType.datatypeFactory.newDurationDayTime( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.dayTimeDuration, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdDayTimeDuration( this, context );
      }
   }

   final class XsdByte extends SammXsdType<Byte> implements SammType<Byte>, IntegerType<Byte> {
      private XsdByte() {
         super( org.apache.jena.vocabulary.XSD.xbyte, Byte.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDbyte.isValid( lexicalForm );
      }

      @Override
      public Byte parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> Byte.parseByte( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.xbyte, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdByte( this, context );
      }

      @Override
      public Optional<BigInteger> lowerBound() {
         return Optional.of( BigInteger.valueOf( Byte.MIN_VALUE ) );
      }

      @Override
      public Optional<BigInteger> upperBound() {
         return Optional.of( BigInteger.valueOf( Byte.MAX_VALUE ) );
      }
   }

   final class XsdShort extends SammXsdType<Short> implements SammType<Short>, IntegerType<Short> {
      private XsdShort() {
         super( org.apache.jena.vocabulary.XSD.xshort, Short.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDshort.isValid( lexicalForm );
      }

      @Override
      public Short parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> Short.parseShort( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.xshort, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdShort( this, context );
      }

      @Override
      public Optional<BigInteger> lowerBound() {
         return Optional.of( BigInteger.valueOf( Short.MIN_VALUE ) );
      }

      @Override
      public Optional<BigInteger> upperBound() {
         return Optional.of( BigInteger.valueOf( Short.MAX_VALUE ) );
      }
   }

   final class XsdInt extends SammXsdType<Integer> implements SammType<Integer>, IntegerType<Integer> {
      private XsdInt() {
         super( org.apache.jena.vocabulary.XSD.xint, Integer.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDint.isValid( lexicalForm );
      }

      @Override
      public Integer parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> Integer.parseInt( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.xint, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdInt( this, context );
      }

      @Override
      public Optional<BigInteger> lowerBound() {
         return Optional.of( BigInteger.valueOf( Integer.MIN_VALUE ) );
      }

      @Override
      public Optional<BigInteger> upperBound() {
         return Optional.of( BigInteger.valueOf( Integer.MAX_VALUE ) );
      }
   }

   final class XsdLong extends SammXsdType<Long> implements SammType<Long>, IntegerType<Long> {
      private XsdLong() {
         super( org.apache.jena.vocabulary.XSD.xlong, Long.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDlong.isValid( lexicalForm );
      }

      @Override
      public Long parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> Long.parseLong( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.xlong, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdLong( this, context );
      }

      @Override
      public Optional<BigInteger> lowerBound() {
         return Optional.of( BigInteger.valueOf( Long.MIN_VALUE ) );
      }

      @Override
      public Optional<BigInteger> upperBound() {
         return Optional.of( BigInteger.valueOf( Long.MAX_VALUE ) );
      }
   }

   final class XsdUnsignedByte extends SammXsdType<Short> implements SammType<Short>, IntegerType<Short> {
      private XsdUnsignedByte() {
         super( org.apache.jena.vocabulary.XSD.unsignedByte, Short.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDunsignedByte.isValid( lexicalForm );
      }

      @Override
      public Short parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> Short.parseShort( lexicalForm ) )
               .flatMap( unsignedByteValue -> unsignedByteValue < 0 || unsignedByteValue > 255
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( unsignedByteValue ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.unsignedByte, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdUnsignedByte( this, context );
      }

      @Override
      public Optional<BigInteger> lowerBound() {
         return Optional.of( BigInteger.ZERO );
      }

      @Override
      public Optional<BigInteger> upperBound() {
         return Optional.of( BigInteger.valueOf( 255 ) );
      }
   }

   final class XsdUnsignedShort extends SammXsdType<Integer> implements SammType<Integer>, IntegerType<Integer> {
      private XsdUnsignedShort() {
         super( org.apache.jena.vocabulary.XSD.unsignedShort, Integer.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDunsignedShort.isValid( lexicalForm );
      }

      @Override
      public Integer parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> Integer.parseInt( lexicalForm ) )
               .flatMap( unsignedShortValue -> unsignedShortValue < 0 || unsignedShortValue > 65535
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( unsignedShortValue ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.unsignedShort, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdUnsignedShort( this, context );
      }

      @Override
      public Optional<BigInteger> lowerBound() {
         return Optional.of( BigInteger.ZERO );
      }

      @Override
      public Optional<BigInteger> upperBound() {
         return Optional.of( BigInteger.valueOf( 65535 ) );
      }
   }

   final class XsdUnsignedInt extends SammXsdType<Long> implements SammType<Long>, IntegerType<Long> {
      private XsdUnsignedInt() {
         super( org.apache.jena.vocabulary.XSD.unsignedInt, Long.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDunsignedInt.isValid( lexicalForm );
      }

      @Override
      public Long parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> Long.parseLong( lexicalForm ) )
               .flatMap( unsignedIntValue -> unsignedIntValue < 0 || unsignedIntValue > 4294967295L
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( unsignedIntValue ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.unsignedInt, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdUnsignedInt( this, context );
      }

      @Override
      public Optional<BigInteger> lowerBound() {
         return Optional.of( BigInteger.ZERO );
      }

      @Override
      public Optional<BigInteger> upperBound() {
         return Optional.of( BigInteger.valueOf( 4294967295L ) );
      }
   }

   final class XsdUnsignedLong extends SammXsdType<BigInteger> implements SammType<BigInteger>, IntegerType<BigInteger> {
      private XsdUnsignedLong() {
         super( org.apache.jena.vocabulary.XSD.unsignedLong, BigInteger.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDunsignedLong.isValid( lexicalForm );
      }

      @Override
      public BigInteger parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> new BigInteger( lexicalForm ) )
               .flatMap( unsignedLongValue -> unsignedLongValue.compareTo( BigInteger.ZERO ) < 0
                     || unsignedLongValue.compareTo( new BigInteger( "18446744073709551615" ) ) > 0
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( unsignedLongValue ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.unsignedLong, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdUnsignedLong( this, context );
      }

      @Override
      public Optional<BigInteger> lowerBound() {
         return Optional.of( BigInteger.ZERO );
      }

      @Override
      public Optional<BigInteger> upperBound() {
         return Optional.of( new BigInteger( "18446744073709551615" ) );
      }
   }

   final class XsdPositiveInteger extends SammXsdType<BigInteger> implements SammType<BigInteger>, IntegerType<BigInteger> {
      private XsdPositiveInteger() {
         super( org.apache.jena.vocabulary.XSD.positiveInteger, BigInteger.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDpositiveInteger.isValid( lexicalForm );
      }

      @Override
      public BigInteger parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> new BigInteger( lexicalForm ) )
               .flatMap( positiveIntegerValue -> positiveIntegerValue.compareTo( BigInteger.ONE ) < 0
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( positiveIntegerValue ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.positiveInteger, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdPositiveInteger( this, context );
      }

      @Override
      public Optional<BigInteger> lowerBound() {
         return Optional.of( BigInteger.ONE );
      }

      @Override
      public Optional<BigInteger> upperBound() {
         return Optional.empty();
      }
   }

   final class XsdNonNegativeInteger extends SammXsdType<BigInteger> implements SammType<BigInteger>, IntegerType<BigInteger> {
      private XsdNonNegativeInteger() {
         super( org.apache.jena.vocabulary.XSD.nonNegativeInteger, BigInteger.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDnonNegativeInteger.isValid( lexicalForm );
      }

      @Override
      public BigInteger parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> new BigInteger( lexicalForm ) )
               .flatMap( nonNegativeIntegerValue -> nonNegativeIntegerValue.compareTo( BigInteger.ZERO ) < 0
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( nonNegativeIntegerValue ) )
               .getOrElseThrow( cause ->
                     new ValueParsingException( org.apache.jena.vocabulary.XSD.nonNegativeInteger, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdNonNegativeInteger( this, context );
      }

      @Override
      public Optional<BigInteger> lowerBound() {
         return Optional.of( BigInteger.ZERO );
      }

      @Override
      public Optional<BigInteger> upperBound() {
         return Optional.empty();
      }
   }

   final class XsdNegativeInteger extends SammXsdType<BigInteger> implements SammType<BigInteger>, IntegerType<BigInteger> {
      private XsdNegativeInteger() {
         super( org.apache.jena.vocabulary.XSD.negativeInteger, BigInteger.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDnegativeInteger.isValid( lexicalForm );
      }

      @Override
      public BigInteger parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> new BigInteger( lexicalForm ) )
               .flatMap( negativeIntegegerValue -> negativeIntegegerValue.compareTo( BigInteger.ZERO ) >= 0
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( negativeIntegegerValue ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.negativeInteger, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdNegativeInteger( this, context );
      }

      @Override
      public Optional<BigInteger> lowerBound() {
         return Optional.empty();
      }

      @Override
      public Optional<BigInteger> upperBound() {
         return Optional.of( BigInteger.valueOf( -1 ) );
      }
   }

   final class XsdNonPositiveInteger extends SammXsdType<BigInteger> implements SammType<BigInteger>, IntegerType<BigInteger> {
      private XsdNonPositiveInteger() {
         super( org.apache.jena.vocabulary.XSD.nonPositiveInteger, BigInteger.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDnonPositiveInteger.isValid( lexicalForm );
      }

      @Override
      protected BigInteger parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> new BigInteger( lexicalForm ) )
               .flatMap( nonPositiveIntegerValue -> nonPositiveIntegerValue.compareTo( BigInteger.ZERO ) > 0
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( nonPositiveIntegerValue ) )
               .getOrElseThrow(
                     cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.nonPositiveInteger, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdNonPositiveInteger( this, context );
      }

      @Override
      public Optional<BigInteger> lowerBound() {
         return Optional.empty();
      }

      @Override
      public Optional<BigInteger> upperBound() {
         return Optional.of( BigInteger.ZERO );
      }
   }

   final class XsdHexBinary extends SammXsdType<byte[]> implements SammType<byte[]> {
      private XsdHexBinary() {
         super( org.apache.jena.vocabulary.XSD.hexBinary, byte[].class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDhexBinary.isValid( lexicalForm );
      }

      @Override
      protected byte[] parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> DatatypeConverter.parseHexBinary( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.hexBinary, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdHexBinary( this, context );
      }

      @Override
      public String serialize( final byte[] value ) {
         return HexFormat.of().withUpperCase().formatHex( value );
      }
   }

   final class XsdBase64Binary extends SammXsdType<byte[]> implements SammType<byte[]> {
      private XsdBase64Binary() {
         super( org.apache.jena.vocabulary.XSD.base64Binary, byte[].class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDbase64Binary.isValid( lexicalForm );
      }

      @Override
      protected byte[] parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> Base64.getDecoder().decode( lexicalForm ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.base64Binary, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdBase64Binary( this, context );
      }

      @Override
      public String serialize( final byte[] value ) {
         return Base64.getEncoder().encodeToString( value );
      }
   }

   final class XsdAnyUri extends SammXsdType<URI> implements SammType<URI> {
      private XsdAnyUri() {
         super( org.apache.jena.vocabulary.XSD.anyURI, URI.class );
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return XSDDatatype.XSDanyURI.isValid( lexicalForm );
      }

      @Override
      protected URI parseTypedValue( final String lexicalForm ) {
         return Try.of( () -> URI.create( (String) XSDDatatype.XSDanyURI.parse( lexicalForm ) ) )
               .getOrElseThrow( cause -> new ValueParsingException( org.apache.jena.vocabulary.XSD.anyURI, lexicalForm, cause ) );
      }

      @Override
      public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
         return visitor.visitXsdAnyUri( this, context );
      }
   }
}
