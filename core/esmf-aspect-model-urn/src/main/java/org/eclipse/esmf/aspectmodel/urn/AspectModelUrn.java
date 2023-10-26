/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.urn;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.ImmutableList;

import io.vavr.control.Try;

/**
 * Represents the identifier URN of an Aspect Model.
 *
 * @see <a href="https://eclipse-esmf.github.io/samm-specification/2.0.0/namespaces.html">Definition of the URN</a>
 */
public class AspectModelUrn implements Comparable<AspectModelUrn> {
   public static final String NAMESPACE_REGEX = "([a-zA-Z0-9()+,\\-.:=@;$_!*']|%[0-9a-fA-F]{2})+";
   public static final Pattern NAMESPACE_PATTERN = Pattern.compile( NAMESPACE_REGEX );
   public static final String MODEL_ELEMENT_NAME_REGEX = "\\p{Alpha}\\p{Alnum}*";
   public static final String VERSION_REGEX = "^(\\d+\\.)(\\d+\\.)(\\*|\\d+)$";
   public static final String VALID_PROTOCOL = "urn";
   public static final String VALID_NAMESPACE_IDENTIFIER = "samm";
   public static final int MAX_URN_LENGTH = 256;
   public static final int ASPECT_NAME_INDEX = 4;

   // Indices applicable to all URN formats
   private static final int NAMESPACE_INDEX = 2;

   // Indices applicable to legacy model element URN structure
   private static final int ELEMENT_TYPE_INDEX = 3;
   private static final int VERSION_INDEX_FOR_ASPECTS = 5;
   private static final int MODEL_ELEMENT_NAME_INDEX = 6;

   // Indices applicable to new model element URN structure
   private static final int VERSION_INDEX_FOR_MODEL_ELEMENTS = 3;

   // Indices applicable to meta model URNs
   private static final int VERSION_INDEX_FOR_META_MODEL = 4;
   private static final int META_MODEL_ELEMENT_NAME_INDEX = 5;

   private static final List<ElementType> ELEMENT_TYPES_WITH_VARIABLE_NAMESPACE_STRUCTURE = Arrays
         .asList( ElementType.CHARACTERISTIC, ElementType.ENTITY, ElementType.UNIT );
   private static final List<ElementType> MODEL_ELEMENT_TYPES = Arrays
         .asList( ElementType.ASPECT_MODEL_ELEMENT, ElementType.ENTITY_MODEL_ELEMENT,
               ElementType.CHARACTERISTIC_MODEL_ELEMENT );

   private static final Logger LOG = LoggerFactory.getLogger( AspectModelUrn.class );

   private final String name;
   private final String version;
   private final String namespace;
   private final ElementType elementType;
   private final boolean isSammUrn;

   @JsonValue
   private final URI urn;

   private AspectModelUrn( final URI urn, final String name, final String namespace, final ElementType elementType,
         final String version, final boolean isSammUrn ) {
      this.urn = urn;
      this.name = name;
      this.namespace = namespace;
      this.elementType = elementType;
      this.version = version;
      this.isSammUrn = isSammUrn;
   }

   /**
    * Creates a {@link AspectModelUrn} from a URN.
    *
    * @param urn the urn which will be parsed to create the {@link AspectModelUrn} instance
    * @return {@link AspectModelUrn} containing the individual parts from the urn
    *
    * @throws UrnSyntaxException if {@code urn} is not valid
    */
   @SuppressWarnings( { "squid:S1166" } )
   @JsonCreator
   public static AspectModelUrn fromUrn( final String urn ) {
      try {
         return fromUrn( new URI( urn ) );
      } catch ( final URISyntaxException e ) {
         throw new UrnSyntaxException( UrnSyntaxException.URN_IS_NO_URI );
      }
   }

   /**
    * Creates a {@link AspectModelUrn} from a URN.
    *
    * @param urn the urn which will be parsed to create the {@link AspectModelUrn} instance
    * @return {@link AspectModelUrn} containing the individual parts from the urn
    *
    * @throws UrnSyntaxException if {@code urn} is not valid
    */
   public static AspectModelUrn fromUrn( final URI urn ) {
      checkNotEmpty( urn );

      final List<String> urnParts = ImmutableList.copyOf( urn.toString().split( "[:|#]" ) );
      final int numberOfUrnParts = urnParts.size();
      checkUrn( numberOfUrnParts >= 5, UrnSyntaxException.URN_IS_MISSING_SECTIONS_MESSAGE );

      final String protocol = urnParts.get( 0 );
      checkUrn( protocol.equalsIgnoreCase( VALID_PROTOCOL ), UrnSyntaxException.URN_INVALID_PROTOCOL_MESSAGE,
            VALID_PROTOCOL );

      String namespaceIdentifier = urnParts.get( 1 );
      // This is no public constant, because it's an implementation detail
      if ( namespaceIdentifier.equals( "bamm" ) ) {
         LOG.warn( "Encountered legacy BAMM Aspect Model URN: {}. Support for urn:bamm: will be removed!", urn );
         namespaceIdentifier = "samm";
      }
      checkUrn( namespaceIdentifier.equals( VALID_NAMESPACE_IDENTIFIER ),
            UrnSyntaxException.URN_INVALID_NAMESPACE_IDENTIFIER_MESSAGE, VALID_NAMESPACE_IDENTIFIER );

      final String namespace = urnParts.get( NAMESPACE_INDEX );
      checkUrn( NAMESPACE_PATTERN.matcher( namespace ).matches(),
            UrnSyntaxException.URN_INVALID_NAMESPACE_MESSAGE, NAMESPACE_REGEX );

      final ElementType elementType = getElementType( urnParts );
      final boolean isSammUrn = isSammUrn( urn, urnParts, elementType );
      final String version = getVersion( isSammUrn, urnParts, elementType );
      final String elementName = getName( isSammUrn, urnParts, elementType );
      return new AspectModelUrn( urn, elementName, namespace, elementType, version, isSammUrn );
   }

   /**
    * Checked version of {@link #fromUrn(String)}
    * @param urn the lexical representation of the Aspect Model URN
    * @return the Aspect Model URN, a {@link URISyntaxException} or a {@link UrnSyntaxException}
    */
   public static Try<AspectModelUrn> from( final String urn ) {
      try {
         return from( new URI( urn ) );
      } catch ( final URISyntaxException e ) {
         throw new UrnSyntaxException( UrnSyntaxException.URN_IS_NO_URI );
      }
   }

   /**
    * Checked version of {@link #fromUrn(URI)}
    * @param uri the lexical representation of the Aspect Model URN
    * @return the Aspect Model URN or a {@link UrnSyntaxException}
    */
   public static Try<AspectModelUrn> from( final URI uri ) {
      try {
         return Try.success( fromUrn( uri ) );
      } catch ( final UrnSyntaxException exception ) {
         return Try.failure( exception );
      }
   }

   /**
    * Retrieves the element type from the Aspect Model URN.
    *
    * @return the {@link ElementType} for the given Aspect Model URN
    */
   private static ElementType getElementType( final List<String> urnParts ) {
      final String elementType = urnParts.get( ELEMENT_TYPE_INDEX );
      if ( elementType.equals( ElementType.META_MODEL.getValue() ) ) {
         return ElementType.META_MODEL;
      }
      if ( elementType.equals( ElementType.ASPECT_MODEL.getValue() ) ) {
         return getModelElementType( urnParts, ElementType.ASPECT_MODEL, ElementType.ASPECT_MODEL_ELEMENT );
      }
      if ( elementType.equals( ElementType.ENTITY.getValue() ) ) {
         return getModelElementType( urnParts, ElementType.ENTITY, ElementType.ENTITY_MODEL_ELEMENT );
      }
      if ( elementType.equals( ElementType.CHARACTERISTIC.getValue() ) ) {
         return getModelElementType( urnParts, ElementType.CHARACTERISTIC, ElementType.CHARACTERISTIC_MODEL_ELEMENT );
      }
      if ( elementType.equals( ElementType.UNIT.getValue() ) ) {
         return ElementType.UNIT;
      }
      return ElementType.NONE;
   }

   /**
    * Determines whether the given URN identifies a root model element or a model element (see {@link ElementType}), and
    * returns either the given {@link ElementType} or the given Model {@link ElementType}.
    *
    * @param urnParts the URN being processed split up into its individual parts
    * @param elementType the root element type of element identified by the given URN
    * @param modelElementType the element type to be used in case the given URN does not identify a root element
    */
   private static ElementType getModelElementType( final List<String> urnParts, final ElementType elementType,
         final ElementType modelElementType ) {
      if ( urnParts.size() == MODEL_ELEMENT_NAME_INDEX + 1 ) {
         return modelElementType;
      }
      return elementType;
   }

   /**
    * Retrieves the version from the given URN.
    */
   private static String getVersion( final boolean isSammUrn, final List<String> urnParts,
         final ElementType elementType ) {
      if ( elementType.equals( ElementType.NONE ) ) {
         final String version = urnParts.get( VERSION_INDEX_FOR_MODEL_ELEMENTS );
         checkVersion( version );
         return urnParts.get( VERSION_INDEX_FOR_MODEL_ELEMENTS );
      }
      if ( elementType.equals( ElementType.META_MODEL ) ) {
         final String version = urnParts.get( VERSION_INDEX_FOR_META_MODEL );
         checkVersion( version );
         return urnParts.get( VERSION_INDEX_FOR_META_MODEL );
      }
      if ( ELEMENT_TYPES_WITH_VARIABLE_NAMESPACE_STRUCTURE.contains( elementType ) ) {
         String version = urnParts.get( VERSION_INDEX_FOR_ASPECTS );
         if ( isSammUrn ) {
            version = urnParts.get( VERSION_INDEX_FOR_META_MODEL );
         }
         checkVersion( version );
         return version;
      }
      final String version = urnParts.get( VERSION_INDEX_FOR_ASPECTS );
      checkVersion( version );
      return version;
   }

   /**
    * Retrieves the name from the given URN.
    */
   private static String getName( final boolean isSammUrn, final List<String> urnParts,
         final ElementType elementType ) {
      if ( elementType.equals( ElementType.META_MODEL ) ) {
         final String modelElementName = urnParts.get( META_MODEL_ELEMENT_NAME_INDEX );
         checkElementName( modelElementName, "meta model element" );
         return modelElementName;
      }
      if ( MODEL_ELEMENT_TYPES.contains( elementType ) ) {
         final String modelElementName = urnParts.get( MODEL_ELEMENT_NAME_INDEX );
         checkElementName( modelElementName, elementType.getValue() + " element" );
         return modelElementName;
      }
      if ( ELEMENT_TYPES_WITH_VARIABLE_NAMESPACE_STRUCTURE.contains( elementType ) ) {
         String name = urnParts.get( ASPECT_NAME_INDEX );
         if ( isSammUrn ) {
            name = urnParts.get( META_MODEL_ELEMENT_NAME_INDEX );
         }
         checkElementName( name, elementType.getValue() );
         return name;
      }

      final String modelElementName = urnParts.get( ASPECT_NAME_INDEX );
      checkElementName( modelElementName, "aspect" );
      return modelElementName;
   }

   /**
    * Determines whether the given URN identifies an element which is defined in the context of the SAMM.
    *
    * @return true if the element is defined in the context of the SAMM, false otherwise.
    */
   private static boolean isSammUrn( final URI urn, final List<String> urnParts,
         final ElementType elementType ) {
      if ( elementType == ElementType.NONE || urnParts.size() == MODEL_ELEMENT_NAME_INDEX + 1 ) {
         return false;
      }

      final String regex = ".*\\b" + elementType.getValue() + ":\\b.*\\b#\\b.*";
      return urn.toString().matches( regex );
   }

   private static void checkElementName( final String modelElementName, final String elementTypeForErrorMessage ) {
      checkUrn( modelElementName.matches( MODEL_ELEMENT_NAME_REGEX ),
            UrnSyntaxException.URN_INVALID_ELEMENT_NAME_MESSAGE, elementTypeForErrorMessage,
            MODEL_ELEMENT_NAME_REGEX, modelElementName );
   }

   private static void checkVersion( final String version ) {
      checkUrn( version.matches( VERSION_REGEX ), UrnSyntaxException.URN_INVALID_VERSION, version );
   }

   private static void checkNotEmpty( final URI urn ) {
      checkUrn( urn != null, UrnSyntaxException.URN_IS_NULL_MESSAGE );
      checkUrn( urn.toString().length() <= MAX_URN_LENGTH, UrnSyntaxException.URN_IS_TOO_LONG, MAX_URN_LENGTH );
   }

   /**
    * Returns the full URN of the model element
    *
    * @return the model URN
    */
   public URI getUrn() {
      return urn;
   }

   /**
    * Returns the local name of the model element, e.g. MyAspect
    *
    * @return the local name of the model element
    */
   public String getName() {
      return name;
   }

   /**
    * Returns the version of the model element, e.g. 1.2.3
    *
    * @return the version of the model element
    */
   public String getVersion() {
      return version;
   }

   /**
    * Returns the namespace part of the URN, e.g. com.example.foo
    *
    * @return the namespace part of the URN
    */
   public String getNamespace() {
      return namespace;
   }

   /**
    * Returns prefix part of the URN, i.e. the part up to and including the # but not including the local name,
    * e.g. urn:samm:com.foo.example:1.0.0#
    *
    * @return the prefix part of the URN
    */
   public String getUrnPrefix() {
      return urn.toString().split( "#" )[0] + "#";
   }

   public ElementType getElementType() {
      return elementType;
   }

   public boolean isSammUrn() {
      return isSammUrn;
   }

   /**
    * Returns AspectModelUrn with the same prefix but with different local name.
    *
    * @param name new local name
    * @return the AspectModelUrn
    */
   public AspectModelUrn withName( String name ) {
      return fromUrn( getUrnPrefix() + name );
   }

   /**
    * Checks whether the given expression is true.
    *
    * @param expression a boolean expression to be evaluated
    * @param errorMessage the exception message to use if the check is successful
    * @throws UrnSyntaxException if {@code expression} is false
    */
   private static void checkUrn( final boolean expression, final String errorMessage,
         final Object... errorMessageArguments ) {
      final String formattedErrorMessage = MessageFormat.format( errorMessage, errorMessageArguments );
      if ( !expression ) {
         throw new UrnSyntaxException( formattedErrorMessage );
      }
   }

   @Override
   public String toString() {
      return urn.toString();
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final AspectModelUrn that = (AspectModelUrn) o;
      return Objects.equals( urn, that.urn );
   }

   @Override
   public int hashCode() {
      return Objects.hash( urn );
   }

   @Override
   public int compareTo( final AspectModelUrn o ) {
      return urn.compareTo( o.urn );
   }
}
