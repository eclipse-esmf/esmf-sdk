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

import static org.eclipse.esmf.aspectmodel.StreamUtil.asMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.datatypes.xsd.XSDDatatype;
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
public abstract non-sealed class SammXsdType<T> extends XSDDatatype implements SammType<T> {
   private static final Logger LOG = LoggerFactory.getLogger( SammXsdType.class );
   protected static DatatypeFactory datatypeFactory;
   private final String uri;

   private static boolean checking = true;

   /**
    * @deprecated Use {@link SammType#BOOLEAN} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<Boolean> BOOLEAN = SammType.BOOLEAN;
   /**
    * @deprecated Use {@link SammType#DECIMAL} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<BigDecimal> DECIMAL = SammType.DECIMAL;
   /**
    * @deprecated Use {@link SammType#INTEGER} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<BigInteger> INTEGER = SammType.INTEGER;
   /**
    * @deprecated Use {@link SammType#DOUBLE} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<Double> DOUBLE = SammType.DOUBLE;
   /**
    * @deprecated Use {@link SammType#FLOAT} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<Float> FLOAT = SammType.FLOAT;
   /**
    * @deprecated Use {@link SammType#DATE} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<XMLGregorianCalendar> DATE = SammType.DATE;
   /**
    * @deprecated Use {@link SammType#TIME} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<XMLGregorianCalendar> TIME = SammType.TIME;
   /**
    * @deprecated Use {@link SammType#DATE_TIME} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<XMLGregorianCalendar> DATE_TIME = SammType.DATE_TIME;
   /**
    * @deprecated Use {@link SammType#DATE_TIME_STAMP} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<XMLGregorianCalendar> DATE_TIME_STAMP = SammType.DATE_TIME_STAMP;
   /**
    * @deprecated Use {@link SammType#G_YEAR} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<XMLGregorianCalendar> G_YEAR = SammType.G_YEAR;
   /**
    * @deprecated Use {@link SammType#G_MONTH} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<XMLGregorianCalendar> G_MONTH = SammType.G_MONTH;
   /**
    * @deprecated Use {@link SammType#G_DAY} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<XMLGregorianCalendar> G_DAY = SammType.G_DAY;
   /**
    * @deprecated Use {@link SammType#G_YEAR_MONTH} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<XMLGregorianCalendar> G_YEAR_MONTH = SammType.G_YEAR_MONTH;
   /**
    * @deprecated Use {@link SammType#G_MONTH_DAY} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<XMLGregorianCalendar> G_MONTH_DAY = SammType.G_MONTH_DAY;
   /**
    * @deprecated Use {@link SammType#DURATION} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<javax.xml.datatype.Duration> DURATION = SammType.DURATION;
   /**
    * @deprecated Use {@link SammType#YEAR_MONTH_DURATION} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<javax.xml.datatype.Duration> YEAR_MONTH_DURATION = SammType.YEAR_MONTH_DURATION;
   /**
    * @deprecated Use {@link SammType#DAY_TIME_DURATION} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<javax.xml.datatype.Duration> DAY_TIME_DURATION = SammType.DAY_TIME_DURATION;
   /**
    * @deprecated Use {@link SammType#BYTE} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<Byte> BYTE = SammType.BYTE;
   /**
    * @deprecated Use {@link SammType#SHORT} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<Short> SHORT = SammType.SHORT;
   /**
    * @deprecated Use {@link SammType#INT} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<Integer> INT = SammType.INT;
   /**
    * @deprecated Use {@link SammType#LONG} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<Long> LONG = SammType.LONG;
   /**
    * @deprecated Use {@link SammType#UNSIGNED_BYTE} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<Short> UNSIGNED_BYTE = SammType.UNSIGNED_BYTE;
   /**
    * @deprecated Use {@link SammType#UNSIGNED_SHORT} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<Integer> UNSIGNED_SHORT = SammType.UNSIGNED_SHORT;
   /**
    * @deprecated Use {@link SammType#UNSIGNED_INT} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<Long> UNSIGNED_INT = SammType.UNSIGNED_INT;
   /**
    * @deprecated Use {@link SammType#UNSIGNED_LONG} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<BigInteger> UNSIGNED_LONG = SammType.UNSIGNED_LONG;
   /**
    * @deprecated Use {@link SammType#POSITIVE_INTEGER} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<BigInteger> POSITIVE_INTEGER = SammType.POSITIVE_INTEGER;
   /**
    * @deprecated Use {@link SammType#NON_NEGATIVE_INTEGER} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<BigInteger> NON_NEGATIVE_INTEGER = SammType.NON_NEGATIVE_INTEGER;
   /**
    * @deprecated Use {@link SammType#NEGATIVE_INTEGER} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<BigInteger> NEGATIVE_INTEGER = SammType.NEGATIVE_INTEGER;
   /**
    * @deprecated Use {@link SammType#NON_POSITIVE_INTEGER} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<BigInteger> NON_POSITIVE_INTEGER = SammType.NON_POSITIVE_INTEGER;
   /**
    * @deprecated Use {@link SammType#HEX_BINARY} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<byte[]> HEX_BINARY = SammType.HEX_BINARY;
   /**
    * @deprecated Use {@link SammType#BASE64_BINARY} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<byte[]> BASE64_BINARY = SammType.BASE64_BINARY;
   /**
    * @deprecated Use {@link SammType#ANY_URI} instead
    */
   @Deprecated( forRemoval = true )
   public static final SammType<URI> ANY_URI = SammType.ANY_URI;

   protected SammXsdType( final Resource dataTypeResource, final Class<T> correspondingJavaClass ) {
      super( dataTypeResource.getLocalName() );
      uri = dataTypeResource.getURI();
      javaClass = correspondingJavaClass;
   }

   @Override
   public String serialize( final T value ) {
      return value.toString();
   }

   @Override
   public abstract boolean isValid( final String lexicalForm );

   protected abstract T parseTypedValue( final String lexicalForm );

   @Override
   public String getURI() {
      return uri;
   }

   public static final List<SammType<?>> ALL_TYPES = List
         .of( SammType.STRING, SammType.BOOLEAN, SammType.DECIMAL, SammType.INTEGER, SammType.DOUBLE, SammType.FLOAT, SammType.DATE,
               SammType.TIME, SammType.DATE_TIME, SammType.DATE_TIME_STAMP, SammType.G_YEAR, SammType.G_MONTH, SammType.G_YEAR_MONTH,
               SammType.G_DAY, SammType.G_MONTH_DAY, SammType.DURATION, SammType.YEAR_MONTH_DURATION, SammType.DAY_TIME_DURATION,
               SammType.BYTE, SammType.SHORT, SammType.INT, SammType.LONG, SammType.UNSIGNED_BYTE, SammType.UNSIGNED_SHORT,
               SammType.UNSIGNED_INT, SammType.UNSIGNED_LONG, SammType.POSITIVE_INTEGER, SammType.NON_NEGATIVE_INTEGER,
               SammType.NEGATIVE_INTEGER, SammType.NON_POSITIVE_INTEGER, SammType.HEX_BINARY, SammType.BASE64_BINARY, SammType.ANY_URI,
               SammType.LANG_STRING, SammType.CURIE );

   private static final Map<String, SammType<?>> TYPES_BY_URI = ALL_TYPES.stream()
         .map( type -> Map.<String, SammType<?>> entry( type.getURI(), type ) )
         .collect( asMap() );

   public static Optional<SammType<?>> typeByUri( final String uri ) {
      return Optional.ofNullable( TYPES_BY_URI.get( uri ) );
   }

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
         return parseTypedValue( lexicalForm );
      } catch ( final Exception exception ) {
         if ( checking ) {
            throw exception;
         }
      }
      return lexicalForm;
   }

   @Override
   @SuppressWarnings( "unchecked" )
   public String unparse( final Object value ) {
      return serialize( (T) value );
   }

   @Override
   public Optional<T> parseTyped( final String lexicalForm ) {
      try {
         return Optional.of( parseTypedValue( lexicalForm ) );
      } catch ( final Exception exception ) {
         if ( checking ) {
            throw exception;
         }
      }
      return Optional.empty();
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

   @Override
   public String toString() {
      return getClass().getName();
   }

   /**
    * Separate implementation for the "extended" RDF types that can not be based on {@link XSDDatatype}.
    *
    * @param <T> the Java class that represents values for the type
    */
   public abstract static non-sealed class SammExtendedXsdType<T> implements SammType<T> {
      private final String uri;
      private final Class<T> javaClass;

      public SammExtendedXsdType(
            final Resource dataTypeResource,
            final Class<T> javaClass
      ) {
         uri = dataTypeResource.getURI();
         this.javaClass = javaClass;
      }

      @Override
      public String serialize( final T value ) {
         return value.toString();
      }

      @Override
      public abstract boolean isValid( final String lexicalForm );

      protected abstract T parseTypedValue( final String lexicalForm );

      @Override
      public Optional<T> parseTyped( final String lexicalForm ) {
         try {
            return Optional.of( parseTypedValue( lexicalForm ) );
         } catch ( final RuntimeException exception ) {
            if ( checking ) {
               throw exception;
            }
         }
         return Optional.empty();
      }

      @Override
      public String getURI() {
         return uri;
      }

      @Override
      @SuppressWarnings( "unchecked" )
      public String unparse( final Object value ) {
         return serialize( (T) value );
      }

      @Override
      public Object parse( final String lexicalForm ) throws DatatypeFormatException {
         try {
            return parseTypedValue( lexicalForm );
         } catch ( final Exception exception ) {
            if ( checking ) {
               throw exception;
            }
         }
         return lexicalForm;
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

      @Override
      public String getUrn() {
         return uri;
      }

      @Override
      public String toString() {
         return getClass().getName();
      }
   }
}
