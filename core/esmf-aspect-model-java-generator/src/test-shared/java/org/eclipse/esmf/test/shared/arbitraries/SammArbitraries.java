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

package org.eclipse.esmf.test.shared.arbitraries;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.Event;
import org.eclipse.esmf.metamodel.Operation;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.datatype.LangString;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;
import org.eclipse.esmf.metamodel.impl.DefaultAspect;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.metamodel.impl.DefaultEntity;
import org.eclipse.esmf.metamodel.impl.DefaultEntityInstance;
import org.eclipse.esmf.metamodel.impl.DefaultEvent;
import org.eclipse.esmf.metamodel.impl.DefaultOperation;
import org.eclipse.esmf.metamodel.impl.DefaultProperty;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;
import org.eclipse.esmf.metamodel.impl.DefaultScalarValue;
import org.eclipse.esmf.samm.KnownVersion;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;
import net.jqwik.api.Tuple;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.datatypes.RDFDatatype;

/**
 * Provides {@link Arbitrary}s for Aspect model elements.
 */
public interface SammArbitraries extends AspectModelUrnArbitraries, UriArbitraries, XsdArbitraries {
   @Provide
   default Arbitrary<Scalar> anyScalar() {
      return Arbitraries.of( SammXsdType.ALL_TYPES.stream().map( RDFDatatype::getURI ).collect( Collectors.toList() ) )
            .map( DefaultScalar::new );
   }

   @Provide
   default Arbitrary<KnownVersion> anyMetaModelVersion() {
      return Arbitraries.of( KnownVersion.getLatest() );
   }

   @Provide
   default Arbitrary<String> anyUpperCaseElementName() {
      return anyModelElementName().map( StringUtils::capitalize );
   }

   private String decapitalize( final String string ) {
      if ( string == null || string.isEmpty() ) {
         return string;
      }
      final char[] c = string.toCharArray();
      c[0] = Character.toLowerCase( c[0] );
      return new String( c );
   }

   @Provide
   default Arbitrary<String> anyLowerCaseElementName() {
      return anyModelElementName().map( this::decapitalize );
   }

   @Provide
   default Arbitrary<String> anyPayloadName() {
      return Arbitraries.strings().ofMinLength( 1 ).ofMaxLength( 5 ).alpha().numeric().withChars( '_', '-' );
   }

   @Provide
   default Arbitrary<String> anyTopLevelElementName() {
      return anyUpperCaseElementName();
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyAspectUrn() {
      return anyModelElementUrn().map( AspectModelUrn::fromUrn );
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyPropertyUrn() {
      return anyLowerCaseElementName().flatMap( this::anyModelElementUrnForElementName ).map( AspectModelUrn::fromUrn );
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyOperationUrn() {
      return anyLowerCaseElementName().flatMap( this::anyModelElementUrnForElementName ).map( AspectModelUrn::fromUrn );
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyEventUrn() {
      return anyLowerCaseElementName().flatMap( this::anyModelElementUrnForElementName ).map( AspectModelUrn::fromUrn );
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyCharacteristicUrn() {
      return anyUpperCaseElementName().flatMap( this::anyModelElementUrnForElementName ).map( AspectModelUrn::fromUrn );
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyEntityUrn() {
      return anyUpperCaseElementName().flatMap( this::anyModelElementUrnForElementName ).map( AspectModelUrn::fromUrn );
   }

   @Provide
   default Arbitrary<Locale> anyLocale() {
      return Arbitraries.of( Locale.ENGLISH, Locale.GERMAN );
   }

   @Provide
   default Arbitrary<Set<LangString>> anyLocalizedString() {
      final Arbitrary<String> values = Arbitraries.strings().ofMinLength( 1 ).ofMaxLength( 10 );
      return Combinators.combine( anyLocale(), values ).as( ( locale, string ) -> new LangString( string, locale ) ).set();
   }

   @Provide
   default Arbitrary<Set<LangString>> anyPreferredNames() {
      return anyLocalizedString();
   }

   @Provide
   default Arbitrary<Set<LangString>> anyDescriptions() {
      return anyLocalizedString();
   }

   @Provide
   default Arbitrary<List<String>> anySee() {
      return anyUri().list().ofMaxSize( 3 );
   }

   @Provide
   default Arbitrary<Characteristic> anyCharacteristic() {
      return Combinators
            .combine( anyMetaModelVersion(), anyCharacteristicUrn(), anyPreferredNames(), anyDescriptions(), anySee(),
                  anyScalar() )
            .as( ( metaModelVersion, characteristicUrn, preferredNames, descriptions, see, dataType ) -> {
               final MetaModelBaseAttributes baseAttributes = MetaModelBaseAttributes.builder()
                     .withUrn( characteristicUrn )
                     .withPreferredNames( preferredNames )
                     .withDescriptions( descriptions )
                     .withSee( see )
                     .build();
               return new DefaultCharacteristic( baseAttributes, Optional.of( dataType ) );
            } );
   }

   @Provide
   default Arbitrary<Aspect> anyAspect() {
      return Combinators.combine( anyMetaModelVersion(), anyAspectUrn(), anyPreferredNames(), anyDescriptions(),
                  anySee(), anyProperty().list().ofMinSize( 1 ).ofMaxSize( 3 ), anyOperation().list().ofMaxSize( 3 ),
                  anyEvent().list().ofMaxSize( 3 ) )
            .as( ( metaModelVersion, aspectUrn, preferredNames, descriptions, see, properties, operations, events ) -> {
               final MetaModelBaseAttributes baseAttributes = MetaModelBaseAttributes.builder()
                     .withUrn( aspectUrn )
                     .withPreferredNames( preferredNames )
                     .withDescriptions( descriptions )
                     .withSee( see )
                     .build();

               return new DefaultAspect( baseAttributes, properties, operations, events );
            } );
   }

   @Provide
   default Arbitrary<Operation> anyOperation() {
      return Combinators.combine( anyMetaModelVersion(), anyOperationUrn(), anyPreferredNames(), anyDescriptions(),
                  anySee(), anyProperty().list().ofMinSize( 1 ).ofMaxSize( 3 ), anyProperty().optional() )
            .as( ( metaModelVersion, operationUrn, preferredNames, descriptions, see, inputs, output ) -> {
               final MetaModelBaseAttributes baseAttributes = MetaModelBaseAttributes.builder()
                     .withUrn( operationUrn )
                     .withPreferredNames( preferredNames )
                     .withDescriptions( descriptions )
                     .withSee( see )
                     .build();
               return new DefaultOperation( baseAttributes, inputs, output );
            } );
   }

   @Provide
   default Arbitrary<Event> anyEvent() {
      return Combinators.combine( anyMetaModelVersion(), anyEventUrn(), anyPreferredNames(), anyDescriptions(),
                  anySee(), anyProperty().list().ofMinSize( 1 ).ofMaxSize( 3 ) )
            .as( ( metaModelVersion, eventUrn, preferredNames, descriptions, see, properties ) -> {
               final MetaModelBaseAttributes baseAttributes = MetaModelBaseAttributes.builder()
                     .withUrn( eventUrn )
                     .withPreferredNames( preferredNames )
                     .withDescriptions( descriptions )
                     .withSee( see )
                     .build();
               return new DefaultEvent( baseAttributes, properties );
            } );
   }

   @Provide
   default Arbitrary<Property> anyProperty() {
      return Combinators.combine( anyMetaModelVersion(), anyPropertyUrn(), anyPreferredNames(), anyDescriptions(), anySee(),
                  anyCharacteristic(), anyPayloadName() )
            .as( ( metaModelVersion, propertyUrn, preferredNames, descriptions, see, characteristic, payloadName ) -> {
               final MetaModelBaseAttributes baseAttributes = MetaModelBaseAttributes.builder()
                     .withUrn( propertyUrn )
                     .withPreferredNames( preferredNames )
                     .withDescriptions( descriptions )
                     .withSee( see )
                     .build();
               return new DefaultProperty( baseAttributes, Optional.of( characteristic ), Optional.empty(), false, false,
                     Optional.of( payloadName ), false,
                     Optional.empty() );
            } );
   }

   @Provide
   default Arbitrary<Entity> anyEntity() {
      return Combinators.combine( anyMetaModelVersion(), anyEntityUrn(), anyPreferredNames(), anyDescriptions(), anySee(),
                  anyProperty().list().ofMinSize( 1 ).ofMaxSize( 3 ) )
            .as( ( metaModelVersion, entityUrn, preferredNames, descriptions, see, properties ) -> {
               final MetaModelBaseAttributes baseAttributes = MetaModelBaseAttributes.builder()
                     .withUrn( entityUrn )
                     .withPreferredNames( preferredNames )
                     .withDescriptions( descriptions )
                     .withSee( see )
                     .build();
               return new DefaultEntity( baseAttributes, properties );
            } );
   }

   @Provide
   default Arbitrary<? extends Type> anyType() {
      return Arbitraries.oneOf( anyScalar().map( x -> x ), anyEntity().map( x -> x ) );
   }

   private Value buildScalarValue( final Object value, final Scalar type ) {
      return new DefaultScalarValue( value, type, null );
   }

   @Provide
   default Arbitrary<Value> anyValueForRdfType( final RDFDatatype rdfDatatype, final Scalar type ) {
      final Arbitrary<String> anyString = Arbitraries.strings().ofMinLength( 1 ).ofMaxLength( 25 );

      return switch ( rdfDatatype.getURI().split( "#" )[1] ) {
         case "boolean" -> anyBoolean().map( x -> buildScalarValue( x, type ) );
         case "decimal", "integer" -> Arbitraries.bigIntegers().map( x -> buildScalarValue( x, type ) );
         case "double" -> Arbitraries.doubles().map( x -> buildScalarValue( x, type ) );
         case "float" -> Arbitraries.floats().map( x -> buildScalarValue( x, type ) );
         case "date" -> anyDate().map( x -> buildScalarValue( x, type ) );
         case "time" -> anyTime().map( x -> buildScalarValue( x, type ) );
         case "anyDateTime" -> anyDateTime().map( x -> buildScalarValue( x, type ) );
         case "anyDateTimeStamp" -> anyDateTimeStamp().map( x -> buildScalarValue( x, type ) );
         case "gYear" -> anyGyear().map( x -> buildScalarValue( x, type ) );
         case "gMonth" -> anyGmonth().map( x -> buildScalarValue( x, type ) );
         case "gDay" -> anyGday().map( x -> buildScalarValue( x, type ) );
         case "gYearMonth" -> anyGyearMonth().map( x -> buildScalarValue( x, type ) );
         case "gMonthDay" -> anyGmonthDay().map( x -> buildScalarValue( x, type ) );
         case "duration" -> anyDuration().map( x -> buildScalarValue( x, type ) );
         case "yearMonthDuation" -> anyYearMonthDuration().map( x -> buildScalarValue( x, type ) );
         case "dayTimeDuration" -> anyDayTimeDuration().map( x -> buildScalarValue( x, type ) );
         case "byte" -> Arbitraries.bytes().map( x -> buildScalarValue( x, type ) );
         case "short" -> Arbitraries.shorts().map( x -> buildScalarValue( x, type ) );
         case "unsignedByte" -> anyUnsignedByte().map( x -> buildScalarValue( x, type ) );
         case "int" -> Arbitraries.integers().map( x -> buildScalarValue( x, type ) );
         case "unsignedShort" -> anyUnsignedShort().map( x -> buildScalarValue( x, type ) );
         case "long" -> Arbitraries.longs().map( x -> buildScalarValue( x, type ) );
         case "unsignedLong" -> anyUnsignedLong().map( x -> buildScalarValue( x, type ) );
         case "positiveInteger" -> anyPositiveInteger().map( x -> buildScalarValue( x, type ) );
         case "nonNegativeInteger" -> anyNonNegativeInteger().map( x -> buildScalarValue( x, type ) );
         case "hexBinary" -> anyHexBinary().map( x -> buildScalarValue( x, type ) );
         case "base64Binary" -> anyBase64Binary().map( x -> buildScalarValue( x, type ) );
         case "anyURI" -> anyUri().map( x -> buildScalarValue( x, type ) );
         case "curie" -> anyCurie().map( x -> buildScalarValue( x, type ) );
         case "langString" -> Combinators.combine( anyString, anyLocale() )
               .as( LangString::new )
               .map( x -> buildScalarValue( x, type ) );
         default -> anyString.map( x -> buildScalarValue( x, type ) );
      };
   }

   @Provide
   default Arbitrary<EntityInstance> anyEntityInstance( final Entity entity ) {
      final Arbitrary<Map<Property, Value>> entityAssertions = Arbitraries.of( entity.getProperties() ).flatMap( property -> {
         final Arbitrary<Property> key = Arbitraries.of( property );
         final Arbitrary<Value> value = property.getDataType().map( this::anyValueForType ).orElse( Arbitraries.of( (Value) null ) );
         return Arbitraries.maps( key, value );
      } );
      return Combinators.combine( anyMetaModelVersion(), anyAspectUrn(), anyPreferredNames(), anyDescriptions(), anySee(),
                  entityAssertions )
            .as( ( metaModelVersion, aspectUrn, preferredNames, descriptions, see, assertions ) -> {
               final MetaModelBaseAttributes baseAttributes = MetaModelBaseAttributes.builder()
                     .withUrn( aspectUrn )
                     .withPreferredNames( preferredNames )
                     .withDescriptions( descriptions )
                     .withSee( see )
                     .build();
               return new DefaultEntityInstance( baseAttributes, assertions, entity );
            } );
   }

   @Provide
   default Arbitrary<Value> anyValueForType( final Type type ) {
      if ( type.is( Scalar.class ) ) {
         return anyValueForScalarType( type.as( Scalar.class ) );
      }
      return anyEntityInstance( type.as( Entity.class ) ).map( x -> x );
   }

   @Provide
   default Arbitrary<Value> anyValueForScalarType( final Scalar type ) {
      return SammXsdType
            .ALL_TYPES.stream()
            .filter( dataType -> dataType.getURI().equals( type.getUrn() ) )
            .map( rdfDatatype -> anyValueForRdfType( rdfDatatype, type ) )
            .findFirst()
            .orElseThrow( () -> new RuntimeException( "Could not generate values for type " + type ) );
   }

   @Provide
   default Arbitrary<Tuple.Tuple2<Type, Value>> anyValidTypeValuePair() {
      return anyType().flatMap( type -> anyValueForType( type ).map( value -> Tuple.of( type, value ) ) );
   }
}
