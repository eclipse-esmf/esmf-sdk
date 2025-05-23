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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.esmf.aspectmodel.ValueParsingException;

import io.vavr.control.Try;
import jakarta.xml.bind.DatatypeConverter;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.impl.RDFLangString;
import org.apache.jena.graph.impl.LiteralLabel;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The custom RDF type implementations that have deterministic and typed parsers and unparsers (i.e., serializers).
 *
 * @param <T> the Java class that represents values for the type
 */
// the order of the variables is required because of the way they reference each other
public class SammXsdType<T> extends XSDDatatype implements SammType<T> {
   private static final Logger LOG = LoggerFactory.getLogger( SammXsdType.class );
   public static DatatypeFactory datatypeFactory;

   private final Function<String, T> parser;
   private final Function<T, String> unparser;
   private final Predicate<String> lexicalValidator;
   private static boolean checking = true;

   protected SammXsdType( final Resource dataTypeResource,
         final Class<T> correspondingJavaClass,
         final Function<String, T> parser,
         final Function<T, String> unparser,
         final Predicate<String> lexicalValidator ) {
      super( dataTypeResource.getLocalName() );
      javaClass = correspondingJavaClass;
      this.parser = parser;
      this.unparser = unparser;
      this.lexicalValidator = lexicalValidator;
   }

   public static final SammXsdType<Boolean> BOOLEAN = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.xboolean, Boolean.class, value ->
         switch ( value.toLowerCase() ) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new ValueParsingException( "xsd:boolean", value );
         }, Object::toString, XSDDatatype.XSDboolean::isValid );

   public static final SammXsdType<BigDecimal> DECIMAL = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.decimal, BigDecimal.class,
         value -> Try.of( () -> new BigDecimal( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:decimal", value, cause ) ),
         BigDecimal::toString, XSDDatatype.XSDdecimal::isValid );

   public static final SammXsdType<BigInteger> INTEGER = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.integer, BigInteger.class,
         value -> Try.of( () -> new BigInteger( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:decimal", value, cause ) ),
         BigInteger::toString,
         XSDDatatype.XSDinteger::isValid );

   public static final SammXsdType<Double> DOUBLE = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.xdouble, Double.class,
         value -> switch ( value.toUpperCase() ) {
            case "INF" -> Double.POSITIVE_INFINITY;
            case "-INF" -> Double.NEGATIVE_INFINITY;
            default -> Try.of( () -> Double.parseDouble( value ) )
                  .getOrElseThrow( cause -> new ValueParsingException( "xsd:double", value, cause ) );
         },
         value -> {
            if ( value.isInfinite() ) {
               return value > 0.0d ? "INF" : "-INF";
            }
            return value.toString();
         }, XSDDatatype.XSDdouble::isValid );

   public static final SammXsdType<Float> FLOAT = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.xfloat, Float.class,
         value -> switch ( value.toUpperCase() ) {
            case "INF" -> Float.POSITIVE_INFINITY;
            case "-INF" -> Float.NEGATIVE_INFINITY;
            default -> Try.of( () -> Float.parseFloat( value ) )
                  .getOrElseThrow( cause -> new ValueParsingException( "xsd:float", value, cause ) );
         },
         value -> {
            if ( value.isInfinite() ) {
               return value > 0.0f ? "INF" : "-INF";
            }
            return value.toString();
         }, XSDDatatype.XSDfloat::isValid );

   public static final SammXsdType<XMLGregorianCalendar> DATE = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.date, XMLGregorianCalendar.class,
         value -> Try.of( () -> datatypeFactory.newXMLGregorianCalendar( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:date", value, cause ) ),
         XMLGregorianCalendar::toXMLFormat, XSDDatatype.XSDdate::isValid );

   public static final SammXsdType<XMLGregorianCalendar> TIME = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.time, XMLGregorianCalendar.class,
         value -> Try.of( () -> datatypeFactory.newXMLGregorianCalendar( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:time", value, cause ) ),
         XMLGregorianCalendar::toXMLFormat,
         XSDDatatype.XSDtime::isValid );

   public static final SammXsdType<XMLGregorianCalendar> DATE_TIME = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.dateTime, XMLGregorianCalendar.class,
         value -> Try.of( () -> datatypeFactory.newXMLGregorianCalendar( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:dateTime", value, cause ) ),
         XMLGregorianCalendar::toXMLFormat,
         XSDDatatype.XSDdateTime::isValid );

   public static final SammExtendedXsdType<XMLGregorianCalendar> DATE_TIME_STAMP = new SammExtendedXsdType<>(
         org.apache.jena.vocabulary.XSD.dateTimeStamp, XMLGregorianCalendar.class,
         value -> Try.of( () -> datatypeFactory.newXMLGregorianCalendar( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:dateTimeStamp", value, cause ) ),
         XMLGregorianCalendar::toXMLFormat, XSDDatatype.XSDdateTimeStamp::isValid );

   public static final SammXsdType<XMLGregorianCalendar> G_YEAR = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.gYear, XMLGregorianCalendar.class,
         value -> Try.of( () -> datatypeFactory.newXMLGregorianCalendar( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:gYear", value, cause ) ),
         XMLGregorianCalendar::toXMLFormat, XSDDatatype.XSDgYear::isValid );

   public static final SammXsdType<XMLGregorianCalendar> G_MONTH = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.gMonth, XMLGregorianCalendar.class,
         value -> Try.of( () -> datatypeFactory.newXMLGregorianCalendar( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:gMonth", value, cause ) ),
         XMLGregorianCalendar::toXMLFormat, XSDDatatype.XSDgMonth::isValid );

   public static final SammXsdType<XMLGregorianCalendar> G_DAY = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.gDay, XMLGregorianCalendar.class,
         value -> Try.of( () -> datatypeFactory.newXMLGregorianCalendar( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:gDay", value, cause ) ),
         XMLGregorianCalendar::toXMLFormat, XSDDatatype.XSDgDay::isValid );

   public static final SammXsdType<XMLGregorianCalendar> G_YEAR_MONTH = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.gYearMonth, XMLGregorianCalendar.class,
         value -> Try.of( () -> datatypeFactory.newXMLGregorianCalendar( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:gYearMonth", value, cause ) ),
         XMLGregorianCalendar::toXMLFormat, XSDDatatype.XSDgYearMonth::isValid );

   public static final SammXsdType<XMLGregorianCalendar> G_MONTH_DAY = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.gMonthDay, XMLGregorianCalendar.class,
         value -> Try.of( () -> datatypeFactory.newXMLGregorianCalendar( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:gMonthDay", value, cause ) ),
         XMLGregorianCalendar::toXMLFormat, XSDDatatype.XSDgMonthDay::isValid );

   public static final SammXsdType<Duration> DURATION = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.duration, Duration.class,
         value -> Try.of( () -> datatypeFactory.newDuration( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:duration", value, cause ) ),
         Duration::toString, XSDDatatype.XSDduration::isValid );

   public static final SammExtendedXsdType<Duration> YEAR_MONTH_DURATION = new SammExtendedXsdType<>(
         org.apache.jena.vocabulary.XSD.yearMonthDuration, Duration.class,
         value -> Try.of( () -> datatypeFactory.newDurationYearMonth( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:yearMonthDuration", value, cause ) ),
         Duration::toString, XSDDatatype.XSDyearMonthDuration::isValid );

   public static final SammExtendedXsdType<Duration> DAY_TIME_DURATION = new SammExtendedXsdType<>(
         org.apache.jena.vocabulary.XSD.dayTimeDuration, Duration.class,
         value -> Try.of( () -> datatypeFactory.newDurationDayTime( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:dayTimeDuration", value, cause ) ),
         Duration::toString,
         XSDDatatype.XSDdayTimeDuration::isValid );

   public static final SammXsdType<Byte> BYTE = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.xbyte, Byte.class,
         value -> Try.of( () -> Byte.parseByte( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:byte", value, cause ) ),
         Object::toString, XSDDatatype.XSDbyte::isValid );

   public static final SammXsdType<Short> SHORT = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.xshort, Short.class,
         value -> Try.of( () -> Short.parseShort( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:short", value, cause ) ),
         Object::toString, XSDDatatype.XSDshort::isValid );

   public static final SammXsdType<Integer> INT = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.xint, Integer.class,
         value -> Try.of( () -> Integer.parseInt( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:int", value, cause ) ),
         Object::toString, XSDDatatype.XSDint::isValid );

   public static final SammXsdType<Long> LONG = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.xlong, Long.class,
         value -> Try.of( () -> Long.parseLong( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:long", value, cause ) ),
         Object::toString, XSDDatatype.XSDlong::isValid );

   public static final SammXsdType<Short> UNSIGNED_BYTE = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.unsignedByte, Short.class,
         value -> Try.of( () -> Short.parseShort( value ) )
               .flatMap( unsignedByteValue -> unsignedByteValue < 0 || unsignedByteValue > 255
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( unsignedByteValue ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:unsignedByte", value, cause ) ),
         Object::toString, XSDDatatype.XSDunsignedByte::isValid );

   public static final SammXsdType<Integer> UNSIGNED_SHORT = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.unsignedShort, Integer.class,
         value -> Try.of( () -> Integer.parseInt( value ) )
               .flatMap( unsignedShortValue -> unsignedShortValue < 0 || unsignedShortValue > 65535
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( unsignedShortValue ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:unsignedShort", value, cause ) ),
         Object::toString, XSDDatatype.XSDunsignedShort::isValid );

   public static final SammXsdType<Long> UNSIGNED_INT = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.unsignedInt, Long.class,
         value -> Try.of( () -> Long.parseLong( value ) )
               .flatMap( unsignedIntValue -> unsignedIntValue < 0 || unsignedIntValue > 4294967295L
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( unsignedIntValue ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:unsignedInt", value, cause ) ),
         Object::toString, XSDDatatype.XSDunsignedInt::isValid );

   public static final SammXsdType<BigInteger> UNSIGNED_LONG = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.unsignedLong, BigInteger.class,
         value -> Try.of( () -> new BigInteger( value ) )
               .flatMap( unsignedLongValue -> unsignedLongValue.compareTo( BigInteger.ZERO ) < 0
                     || unsignedLongValue.compareTo( new BigInteger( "18446744073709551615" ) ) > 0
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( unsignedLongValue ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:unsignedLong", value, cause ) ),
         BigInteger::toString, XSDDatatype.XSDunsignedLong::isValid );

   public static final SammXsdType<BigInteger> POSITIVE_INTEGER = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.positiveInteger, BigInteger.class,
         value -> Try.of( () -> new BigInteger( value ) )
               .flatMap( positiveIntegerValue -> positiveIntegerValue.compareTo( BigInteger.ONE ) < 0
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( positiveIntegerValue ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:positiveInteger", value, cause ) ),
         BigInteger::toString, XSDDatatype.XSDpositiveInteger::isValid );

   public static final SammXsdType<BigInteger> NON_NEGATIVE_INTEGER = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.nonNegativeInteger, BigInteger.class,
         value -> Try.of( () -> new BigInteger( value ) )
               .flatMap( nonNegativeIntegerValue -> nonNegativeIntegerValue.compareTo( BigInteger.ZERO ) < 0
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( nonNegativeIntegerValue ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:nonNegativeInteger", value, cause ) ),
         BigInteger::toString, XSDDatatype.XSDnonNegativeInteger::isValid );

   public static final SammXsdType<BigInteger> NEGATIVE_INTEGER = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.negativeInteger, BigInteger.class,
         value -> Try.of( () -> new BigInteger( value ) )
               .flatMap( negativeIntegegerValue -> negativeIntegegerValue.compareTo( BigInteger.ZERO ) >= 0
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( negativeIntegegerValue ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:negativeInteger", value, cause ) ),
         BigInteger::toString, XSDDatatype.XSDnegativeInteger::isValid );

   public static final SammXsdType<BigInteger> NON_POSITIVE_INTEGER = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.nonPositiveInteger, BigInteger.class,
         value -> Try.of( () -> new BigInteger( value ) )
               .flatMap( nonPositiveIntegerValue -> nonPositiveIntegerValue.compareTo( BigInteger.ZERO ) > 0
                     ? Try.failure( new IllegalArgumentException() )
                     : Try.success( nonPositiveIntegerValue ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:nonPositiveInteger", value, cause ) ),
         BigInteger::toString, XSDDatatype.XSDnonPositiveInteger::isValid );

   public static final SammXsdType<byte[]> HEX_BINARY = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.hexBinary, byte[].class,
         value -> Try.of( () -> DatatypeConverter.parseHexBinary( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:hexBinary", value, cause ) ),
         DatatypeConverter::printHexBinary, XSDDatatype.XSDhexBinary::isValid );

   public static final SammXsdType<byte[]> BASE64_BINARY = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.base64Binary, byte[].class,
         value -> Try.of( () -> DatatypeConverter.parseBase64Binary( value ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:base64Binary", value, cause ) ),
         DatatypeConverter::printBase64Binary, XSDDatatype.XSDbase64Binary::isValid );

   public static final SammXsdType<URI> ANY_URI = new SammXsdType<>(
         org.apache.jena.vocabulary.XSD.anyURI, URI.class,
         value -> Try.of( () -> URI.create( (String) XSDDatatype.XSDanyURI.parse( value ) ) )
               .getOrElseThrow( cause -> new ValueParsingException( "xsd:anyURI", value, cause ) ),
         URI::toString, XSDDatatype.XSDanyURI::isValid );

   public static final List<RDFDatatype> ALL_TYPES = List
         .of( XSDDatatype.XSDstring, BOOLEAN, DECIMAL, INTEGER, DOUBLE, FLOAT, DATE, TIME, DATE_TIME, DATE_TIME_STAMP,
               G_YEAR, G_MONTH, G_YEAR_MONTH, G_DAY, G_MONTH_DAY, DURATION, YEAR_MONTH_DURATION, DAY_TIME_DURATION,
               BYTE, SHORT, INT, LONG, UNSIGNED_BYTE, UNSIGNED_SHORT, UNSIGNED_INT, UNSIGNED_LONG,
               POSITIVE_INTEGER, NON_NEGATIVE_INTEGER, NEGATIVE_INTEGER, NON_POSITIVE_INTEGER, HEX_BINARY,
               BASE64_BINARY, ANY_URI, RDFLangString.rdfLangString, CurieType.INSTANCE );

   public static void setChecking( final boolean checking ) {
      SammXsdType.checking = checking;
   }

   public static boolean isCheckingEnabled() {
      return checking;
   }

   /**
    * Parses a lexical representation of a value of the type
    *
    * @param lexicalForm the lexical representation
    * @return if the lexical representation is valid for the type, an object of the corresponding Java type (@see {@link #getJavaClass()}),
    * otherwise the original lexical value.
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

   @SuppressWarnings( "unchecked" )
   @Override
   public Class<T> getJavaClass() {
      return (Class<T>) javaClass;
   }

   private static boolean setupPerformed = false;

   /**
    * Idempotent method to register the SAMM type mapping in the Jena RDF parser.
    */
   public static synchronized void setupTypeMapping() {
      if ( !setupPerformed ) {
         try {
            datatypeFactory = DatatypeFactory.newInstance();
         } catch ( final DatatypeConfigurationException exception ) {
            LOG.error( "Could not instantiate DatatypeFactory", exception );
         }

         final TypeMapper typeMapper = TypeMapper.getInstance();
         ALL_TYPES.forEach( typeMapper::registerDatatype );
         setupPerformed = true;
      }
   }

   /**
    * Returns the Java class corresponding to a XSD type in a given meta model version.
    *
    * @param type the resource of the data type
    * @return the java class
    */
   public static Class<?> getJavaTypeForMetaModelType( final Resource type ) {
      return ALL_TYPES
            .stream()
            .filter( xsdType -> xsdType.getURI().equals( type.getURI() ) )
            .map( RDFDatatype::getJavaClass )
            .findAny()
            .orElseThrow( () -> new IllegalStateException( "Invalid data type " + type + " found in model." ) );
   }

   /**
    * Separate implementation for the "extended" RDF types that can not be based on {@link XSDDatatype}.
    *
    * @param <T> the Java class that represents values for the type
    */
   public static class SammExtendedXsdType<T> implements SammType<T> {
      private final String uri;
      private final Class<T> javaClass;
      private final Function<String, T> parser;
      private final Function<T, String> unparser;
      private final Predicate<String> lexicalValidator;

      public SammExtendedXsdType(
            final Resource dataTypeResource,
            final Class<T> javaClass,
            final Function<String, T> parser,
            final Function<T, String> unparser,
            final Predicate<String> lexicalValidator
      ) {
         uri = dataTypeResource.getURI();
         this.javaClass = javaClass;
         this.parser = parser;
         this.unparser = unparser;
         this.lexicalValidator = lexicalValidator;
      }

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
      public String unparseTyped( final T value ) {
         return unparser.apply( value );
      }

      @Override
      public String getURI() {
         return uri;
      }

      @Override
      @SuppressWarnings( "unchecked" )
      public String unparse( final Object value ) {
         return unparseTyped( (T) value );
      }

      @Override
      public Object parse( final String lexicalForm ) throws DatatypeFormatException {
         try {
            return parser.apply( lexicalForm );
         } catch ( final Exception exception ) {
            if ( checking ) {
               throw exception;
            }
         }
         return lexicalForm;
      }

      @Override
      public boolean isValid( final String lexicalForm ) {
         return lexicalValidator.test( lexicalForm );
      }

      @Override
      public boolean isValidValue( final Object valueForm ) {
         return isValid( unparse( valueForm ) );
      }

      @Override
      public boolean isValidLiteral( final LiteralLabel lit ) {
         return isValid( lit.getLexicalForm() );
      }

      @Override
      public boolean isEqual( final LiteralLabel value1, final LiteralLabel value2 ) {
         return value1.getLexicalForm().equals( value2.getLexicalForm() );
      }

      @Override
      public int getHashCode( final LiteralLabel lit ) {
         return lit.getValueHashCode();
      }

      @Override
      public Class<T> getJavaClass() {
         return javaClass;
      }

      @Override
      public Object cannonicalise( final Object value ) {
         return value;
      }

      @Override
      public Object extendedTypeDefinition() {
         throw new NotImplementedException();
      }

      @Override
      public RDFDatatype normalizeSubType( final Object value, final RDFDatatype dt ) {
         return this;
      }
   }
}
