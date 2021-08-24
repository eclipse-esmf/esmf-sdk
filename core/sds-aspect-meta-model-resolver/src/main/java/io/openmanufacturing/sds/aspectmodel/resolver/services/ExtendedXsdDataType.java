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

package io.openmanufacturing.sds.aspectmodel.resolver.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.impl.RDFLangString;
import org.apache.jena.ext.xerces.impl.dv.XSSimpleType;
import org.apache.jena.ext.xerces.impl.dv.xs.ExtendedSchemaDVFactoryImpl;
import org.apache.jena.rdf.model.Resource;

@SuppressWarnings( "squid:S1213" )
// the order of the variables is required because of the way they reference each other
public class ExtendedXsdDataType<T> extends XSDDatatype implements TypedRdfDatatype<T> {
   static DatatypeFactory datatypeFactory;

   private final Class<T> correspondingJavaClass;
   private final Function<String, T> parser;
   private final Function<T, String> unparser;
   private final Predicate<String> lexicalValidator;
   private static boolean checking = true;
   private static final ExtendedSchemaDVFactoryImpl extendedSchemaDVFactory = new ExtendedSchemaDVFactoryImpl();

   private ExtendedXsdDataType( final Resource dataTypeResource, final Class<T> correspondingJavaClass,
         final Function<String, T> parser,
         final Function<T, String> unparser,
         final Predicate<String> lexicalValidator ) {
      super( dataTypeResource.getLocalName() );
      this.correspondingJavaClass = correspondingJavaClass;
      this.parser = parser;
      this.unparser = unparser;
      this.lexicalValidator = lexicalValidator;
   }

   private ExtendedXsdDataType( final Resource dataTypeResource, final XSSimpleType xstype,
         final Class<T> correspondingJavaClass,
         final Function<String, T> parser,
         final Function<T, String> unparser,
         final Predicate<String> lexicalValidator ) {
      //In the namespace the hash symbol should be removed, because jena defines the namespace differently.
      super( xstype, dataTypeResource.getNameSpace().replace( "#", "" ) );
      this.correspondingJavaClass = correspondingJavaClass;
      this.parser = parser;
      this.unparser = unparser;
      this.lexicalValidator = lexicalValidator;
   }

   public static final ExtendedXsdDataType<Boolean> BOOLEAN = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.xboolean, Boolean.class, Boolean::valueOf, Object::toString,
         XSDDatatype.XSDboolean::isValid );

   public static final ExtendedXsdDataType<BigDecimal> DECIMAL = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.decimal, BigDecimal.class, BigDecimal::new, BigDecimal::toString,
         XSDDatatype.XSDdecimal::isValid );

   public static final ExtendedXsdDataType<BigInteger> INTEGER = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.integer, BigInteger.class, BigInteger::new, BigInteger::toString,
         XSDDatatype.XSDinteger::isValid );

   public static final ExtendedXsdDataType<Double> DOUBLE = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.xdouble, Double.class, value -> {
      if ( "INF".equalsIgnoreCase( value ) ) {
         return Double.POSITIVE_INFINITY;
      }
      if ( "-INF".equalsIgnoreCase( value ) ) {
         return Double.NEGATIVE_INFINITY;
      }
      return Double.parseDouble( value );
   }, value -> {
      if ( value.isInfinite() ) {
         if ( value > 0.0d ) {
            return "INF";
         } else {
            return "-INF";
         }
      }
      return value.toString();
   }, XSDDatatype.XSDdouble::isValid );

   public static final ExtendedXsdDataType<Float> FLOAT = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.xfloat, Float.class, value -> {
      if ( "INF".equalsIgnoreCase( value ) ) {
         return Float.POSITIVE_INFINITY;
      }
      if ( "-INF".equalsIgnoreCase( value ) ) {
         return Float.NEGATIVE_INFINITY;
      }
      return Float.parseFloat( value );
   }, value -> {
      if ( value.isInfinite() ) {
         if ( value > 0.0f ) {
            return "INF";
         } else {
            return "-INF";
         }
      }
      return value.toString();
   }, XSDDatatype.XSDfloat::isValid );

   public static final ExtendedXsdDataType<XMLGregorianCalendar> DATE = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.date, XMLGregorianCalendar.class,
         value -> datatypeFactory.newXMLGregorianCalendar( value ), XMLGregorianCalendar::toXMLFormat,
         XSDDatatype.XSDdate::isValid );

   public static final ExtendedXsdDataType<XMLGregorianCalendar> TIME = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.time, XMLGregorianCalendar.class,
         value -> datatypeFactory.newXMLGregorianCalendar( value ), XMLGregorianCalendar::toXMLFormat,
         XSDDatatype.XSDtime::isValid );

   public static final ExtendedXsdDataType<XMLGregorianCalendar> DATE_TIME = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.dateTime, XMLGregorianCalendar.class,
         value -> datatypeFactory.newXMLGregorianCalendar( value ), XMLGregorianCalendar::toXMLFormat,
         XSDDatatype.XSDdateTime::isValid );

   public static final ExtendedXsdDataType<XMLGregorianCalendar> DATE_TIME_STAMP = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.dateTimeStamp,
         extendedSchemaDVFactory.getBuiltInType( org.apache.jena.vocabulary.XSD.dateTimeStamp.getLocalName() ),
         XMLGregorianCalendar.class, value -> datatypeFactory.newXMLGregorianCalendar( value ),
         XMLGregorianCalendar::toXMLFormat, XSDDatatype.XSDdateTimeStamp::isValid );

   public static final ExtendedXsdDataType<XMLGregorianCalendar> G_YEAR = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.gYear, XMLGregorianCalendar.class,
         value -> datatypeFactory.newXMLGregorianCalendar( value ), XMLGregorianCalendar::toXMLFormat,
         XSDDatatype.XSDgYear::isValid );

   public static final ExtendedXsdDataType<XMLGregorianCalendar> G_MONTH = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.gMonth, XMLGregorianCalendar.class,
         value -> datatypeFactory.newXMLGregorianCalendar( value ),
         XMLGregorianCalendar::toXMLFormat, XSDDatatype.XSDgMonth::isValid );

   public static final ExtendedXsdDataType<XMLGregorianCalendar> G_DAY = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.gDay, XMLGregorianCalendar.class,
         value -> datatypeFactory.newXMLGregorianCalendar( value ), XMLGregorianCalendar::toXMLFormat,
         XSDDatatype.XSDgDay::isValid );

   public static final ExtendedXsdDataType<XMLGregorianCalendar> G_YEAR_MONTH = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.gYearMonth, XMLGregorianCalendar.class,
         value -> datatypeFactory.newXMLGregorianCalendar( value ), XMLGregorianCalendar::toXMLFormat,
         XSDDatatype.XSDgYearMonth::isValid );

   public static final ExtendedXsdDataType<XMLGregorianCalendar> G_MONTH_DAY = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.gMonthDay, XMLGregorianCalendar.class,
         value -> datatypeFactory.newXMLGregorianCalendar( value ), XMLGregorianCalendar::toXMLFormat,
         XSDDatatype.XSDgMonthDay::isValid );

   public static final ExtendedXsdDataType<Duration> DURATION = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.duration, Duration.class, value -> datatypeFactory.newDuration( value ),
         Duration::toString, XSDDatatype.XSDduration::isValid );

   public static final ExtendedXsdDataType<Duration> YEAR_MONTH_DURATION = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.yearMonthDuration,
         extendedSchemaDVFactory.getBuiltInType( org.apache.jena.vocabulary.XSD.yearMonthDuration.getLocalName() ),
         Duration.class, value -> datatypeFactory.newDurationYearMonth( value ), Duration::toString,
         XSDDatatype.XSDyearMonthDuration::isValid );

   public static final ExtendedXsdDataType<Duration> DAY_TIME_DURATION = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.dayTimeDuration,
         extendedSchemaDVFactory.getBuiltInType( org.apache.jena.vocabulary.XSD.dayTimeDuration.getLocalName() ),
         Duration.class, value -> datatypeFactory.newDurationDayTime( value ), Duration::toString,
         XSDDatatype.XSDdayTimeDuration::isValid );

   public static final ExtendedXsdDataType<Byte> BYTE = new ExtendedXsdDataType<>( org.apache.jena.vocabulary.XSD.xbyte,
         Byte.class, Byte::parseByte, Object::toString, XSDDatatype.XSDbyte::isValid );

   public static final ExtendedXsdDataType<Short> SHORT = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.xshort, Short.class, Short::parseShort, Object::toString,
         XSDDatatype.XSDshort::isValid );

   public static final ExtendedXsdDataType<Integer> INT = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.xint, Integer.class, Integer::parseInt, Object::toString,
         XSDDatatype.XSDint::isValid );

   public static final ExtendedXsdDataType<Long> LONG = new ExtendedXsdDataType<>( org.apache.jena.vocabulary.XSD.xlong,
         Long.class, Long::parseLong, Object::toString, XSDDatatype.XSDlong::isValid );

   public static final ExtendedXsdDataType<Short> UNSIGNED_BYTE = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.unsignedByte, Short.class, Short::parseShort, Object::toString,
         XSDDatatype.XSDunsignedByte::isValid );

   public static final ExtendedXsdDataType<Integer> UNSIGNED_SHORT = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.unsignedShort, Integer.class, Integer::parseInt, Object::toString,
         XSDDatatype.XSDunsignedShort::isValid );

   public static final ExtendedXsdDataType<Long> UNSIGNED_INT = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.unsignedInt, Long.class, Long::parseLong, Object::toString,
         XSDDatatype.XSDunsignedInt::isValid );

   public static final ExtendedXsdDataType<BigInteger> UNSIGNED_LONG = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.unsignedLong, BigInteger.class, BigInteger::new, BigInteger::toString,
         XSDDatatype.XSDunsignedLong::isValid );

   public static final ExtendedXsdDataType<BigInteger> POSITIVE_INTEGER = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.positiveInteger, BigInteger.class,
         BigInteger::new, BigInteger::toString, XSDDatatype.XSDpositiveInteger::isValid );

   public static final ExtendedXsdDataType<BigInteger> NON_NEGATIVE_INTEGER = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.nonNegativeInteger, BigInteger.class,
         BigInteger::new, BigInteger::toString, XSDDatatype.XSDnonNegativeInteger::isValid );

   public static final ExtendedXsdDataType<BigInteger> NEGATIVE_INTEGER = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.negativeInteger, BigInteger.class,
         BigInteger::new, BigInteger::toString, XSDDatatype.XSDnegativeInteger::isValid );

   public static final ExtendedXsdDataType<BigInteger> NON_POSITIVE_INTEGER = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.nonPositiveInteger, BigInteger.class,
         BigInteger::new, BigInteger::toString, XSDDatatype.XSDnonPositiveInteger::isValid );

   public static final ExtendedXsdDataType<byte[]> HEX_BINARY = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.hexBinary, byte[].class, DatatypeConverter::parseHexBinary,
         DatatypeConverter::printHexBinary, XSDDatatype.XSDhexBinary::isValid );

   public static final ExtendedXsdDataType<byte[]> BASE64_BINARY = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.base64Binary, byte[].class, DatatypeConverter::parseBase64Binary,
         DatatypeConverter::printBase64Binary, XSDDatatype.XSDbase64Binary::isValid );

   public static final ExtendedXsdDataType<URI> ANY_URI = new ExtendedXsdDataType<>(
         org.apache.jena.vocabulary.XSD.anyURI, URI.class,
         value -> URI.create( (String) XSDDatatype.XSDanyURI.parse( value ) ),
         URI::toString, XSDDatatype.XSDanyURI::isValid );

   public static final List<RDFDatatype> supportedXsdTypes = List
         .of( XSDDatatype.XSDstring, BOOLEAN, DECIMAL, INTEGER, DOUBLE, FLOAT, DATE, TIME, DATE_TIME, DATE_TIME_STAMP,
               G_YEAR, G_MONTH, G_YEAR_MONTH, G_DAY, G_MONTH_DAY, DURATION, YEAR_MONTH_DURATION, DAY_TIME_DURATION,
               BYTE, SHORT, INT, LONG, UNSIGNED_BYTE, UNSIGNED_SHORT, UNSIGNED_INT, UNSIGNED_LONG,
               POSITIVE_INTEGER, NON_NEGATIVE_INTEGER, NEGATIVE_INTEGER, NON_POSITIVE_INTEGER, HEX_BINARY,
               BASE64_BINARY, ANY_URI, RDFLangString.rdfLangString );

   public static void setChecking( final boolean checking ) {
      ExtendedXsdDataType.checking = checking;
   }

   public static boolean isCheckingEnabled() {
      return checking;
   }

   /**
    * Parses a lexical representation of a value of the type
    *
    * @param lexicalForm the lexical representation
    * @return if the lexical representation is valid for the type, an object of the corresponding Java
    *       type (@see {@link #getJavaClass()}), otherwise the original lexical value.
    */
   @Override
   public Object parse( final String lexicalForm ) {
      try {
         return parser.apply( lexicalForm );
      } catch ( final Exception exception ) {
         if ( checking ) {
            throw exception;
         }
      }
      return lexicalForm;
   }

   /**
    * Parses a lexical representaion of a value of the type
    *
    * @param lexicalForm the lexical representation
    * @return if the lexical representation is valid for the type, Optional.of(x) where x is an object of the
    *       corresponding Java type (@see {@link #getJavaClass()}), otherwise Optional.empty.
    */
   @Override
   public Optional<T> parseTyped( final String lexicalForm ) {
      try {
         return Optional.of( parser.apply( lexicalForm ) );
      } catch ( final RuntimeException exception ) {
         if ( checking ) {
            throw exception;
         }
      }
      return Optional.empty();
   }

   @Override
   @SuppressWarnings( "unchecked" )
   public String unparse( final Object value ) {
      return unparseTyped( (T) value );
   }

   @Override
   public String unparseTyped( final T value ) {
      return unparser.apply( value );
   }

   @Override
   public boolean isValid( final String lexicalForm ) {
      return lexicalValidator.test( lexicalForm );
   }

   @Override
   public Class<T> getJavaClass() {
      return correspondingJavaClass;
   }
}
