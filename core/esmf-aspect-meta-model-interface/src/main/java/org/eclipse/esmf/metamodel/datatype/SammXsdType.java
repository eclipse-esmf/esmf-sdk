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

import java.util.AbstractList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

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

   // --------- FIX: lazy init to avoid SammType <-> SammXsdType init cycles ---------

   private static volatile List<SammType<?>> allTypesCache;

   private static List<SammType<?>> allTypesInternal() {
      List<SammType<?>> local = allTypesCache;
      if ( local == null ) {
         synchronized ( SammXsdType.class ) {
            local = allTypesCache;
            if ( local == null ) {
               local = List.of(
                     SammType.STRING, SammType.BOOLEAN, SammType.DECIMAL, SammType.INTEGER, SammType.DOUBLE, SammType.FLOAT, SammType.DATE,
                     SammType.TIME, SammType.DATE_TIME, SammType.DATE_TIME_STAMP, SammType.G_YEAR, SammType.G_MONTH, SammType.G_YEAR_MONTH,
                     SammType.G_DAY, SammType.G_MONTH_DAY, SammType.DURATION, SammType.YEAR_MONTH_DURATION, SammType.DAY_TIME_DURATION,
                     SammType.BYTE, SammType.SHORT, SammType.INT, SammType.LONG, SammType.UNSIGNED_BYTE, SammType.UNSIGNED_SHORT,
                     SammType.UNSIGNED_INT, SammType.UNSIGNED_LONG, SammType.POSITIVE_INTEGER, SammType.NON_NEGATIVE_INTEGER,
                     SammType.NEGATIVE_INTEGER, SammType.NON_POSITIVE_INTEGER, SammType.HEX_BINARY, SammType.BASE64_BINARY,
                     SammType.ANY_URI,
                     SammType.LANG_STRING, SammType.CURIE
               );
               allTypesCache = local;
            }
         }
      }
      return local;
   }

   /**
    * Public list of all SAMM types.
    * Implemented as a delegating list to avoid eager evaluation of SammType.* during class initialization.
    */
   public static final List<SammType<?>> ALL_TYPES = new AbstractList<>() {
      @Override
      public SammType<?> get( final int index ) {
         return allTypesInternal().get( index );
      }

      @Override
      public int size() {
         return allTypesInternal().size();
      }

      @Override
      public java.util.Iterator<SammType<?>> iterator() {
         return allTypesInternal().iterator();
      }
   };

   private static volatile Map<String, SammType<?>> typesByUriCache;

   private static Map<String, SammType<?>> typesByUriInternal() {
      Map<String, SammType<?>> local = typesByUriCache;
      if ( local == null ) {
         synchronized ( SammXsdType.class ) {
            local = typesByUriCache;
            if ( local == null ) {
               local = allTypesInternal().stream()
                     .map( type -> Map.<String, SammType<?>> entry( type.getURI(), type ) )
                     .collect( asMap() );
               typesByUriCache = local;
            }
         }
      }
      return local;
   }

   public static Optional<SammType<?>> typeByUri( final String uri ) {
      return Optional.ofNullable( typesByUriInternal().get( uri ) );
   }

   // ------------------------------------------------------------------------------

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
