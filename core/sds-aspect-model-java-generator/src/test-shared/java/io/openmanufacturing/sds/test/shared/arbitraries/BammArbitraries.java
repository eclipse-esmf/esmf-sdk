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

package io.openmanufacturing.sds.test.shared.arbitraries;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.rdf.model.Resource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.BammDataType;
import io.openmanufacturing.sds.aspectmodel.resolver.services.ExtendedXsdDataType;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.urn.ElementType;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMMC;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMME;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Characteristic;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.EntityInstance;
import io.openmanufacturing.sds.metamodel.Event;
import io.openmanufacturing.sds.metamodel.Operation;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.Value;
import io.openmanufacturing.sds.metamodel.datatypes.LangString;
import io.openmanufacturing.sds.metamodel.impl.DefaultAspect;
import io.openmanufacturing.sds.metamodel.impl.DefaultCharacteristic;
import io.openmanufacturing.sds.metamodel.impl.DefaultEntity;
import io.openmanufacturing.sds.metamodel.impl.DefaultEntityInstance;
import io.openmanufacturing.sds.metamodel.impl.DefaultEvent;
import io.openmanufacturing.sds.metamodel.impl.DefaultOperation;
import io.openmanufacturing.sds.metamodel.impl.DefaultProperty;
import io.openmanufacturing.sds.metamodel.impl.DefaultScalar;
import io.openmanufacturing.sds.metamodel.impl.DefaultScalarValue;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;
import net.jqwik.api.Tuple;

/**
 * Provides {@link Arbitrary}s for Aspect model elements.
 */
public interface BammArbitraries extends UriArbitraries, XsdArbitraries {
   BAMM bamm( KnownVersion metaModelVersion );

   BAMMC bammc( KnownVersion metaModelVersion );

   BAMME bamme( KnownVersion metaModelVersion );

   @Provide
   default Arbitrary<Scalar> anyScalar() {
      final Arbitrary<String> uris = Arbitraries.of(
            ExtendedXsdDataType.supportedXsdTypes.stream().map( RDFDatatype::getURI ).collect( Collectors.toList() ) );
      return Combinators.combine( uris, anyMetaModelVersion() ).as( DefaultScalar::new );
   }

   @Provide
   default Arbitrary<KnownVersion> anyMetaModelVersion() {
      return Arbitraries.of( KnownVersion.getLatest() );
   }

   @Provide
   default Arbitrary<String> anyModelElementVersion() {
      final Arbitrary<Integer> major = Arbitraries.integers().greaterOrEqual( 0 );
      final Arbitrary<Integer> minor = Arbitraries.integers().greaterOrEqual( 0 );
      final Arbitrary<Integer> maintenance = Arbitraries.integers().greaterOrEqual( 0 );
      return Combinators.combine( major, minor, maintenance )
            .as( ( i1, i2, i3 ) -> String.format( "%d.%d.%d", i1, i2, i3 ) );
   }

   @Provide
   default Arbitrary<String> anyNamespace() {
      return anyHostname().map( hostname -> {
         final String[] parts = hostname.split( "\\." );
         return IntStream.range( 0, parts.length )
               .mapToObj( index -> parts[parts.length - index - 1] )
               .map( part -> part.replace( "-", "_" ) )
               .collect( Collectors.joining( "." ) );
      } );
   }

   @Provide
   default Arbitrary<String> anyUpperCaseElementName() {
      return anyLowerCaseElementName().map( StringUtils::capitalize );
   }

   @Provide
   default Arbitrary<String> anyLowerCaseElementName() {
      final Arbitrary<String> firstCharacter =
            Arbitraries.strings().ofMinLength( 1 ).ofMaxLength( 1 ).alpha().map( String::toLowerCase );
      final Arbitrary<String> rest = Arbitraries.strings().ofMinLength( 3 ).ofMaxLength( 10 ).alpha().numeric();
      return Combinators.combine( firstCharacter, rest ).as( ( f, r ) -> String.format( "%s%s", f, r ) );
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyEntityUrnInMetaModelScope() {
      return anyMetaModelVersion().flatMap( metaModelVersion ->
            Arbitraries.of( bamme( metaModelVersion ).allEntities()
                  .map( Resource::getURI )
                  .map( AspectModelUrn::fromUrn )
                  .collect( Collectors.toList() ) ) );
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyCharacteristicUrnInMetaModelScope() {
      return anyMetaModelVersion().flatMap( metaModelVersion ->
            Arbitraries.of( bammc( metaModelVersion ).allCharacteristics()
                  .map( Resource::getURI )
                  .map( AspectModelUrn::fromUrn )
                  .collect( Collectors.toList() ) ) );
   }

   @Provide
   default Arbitrary<String> anyPayloadName() {
      return Arbitraries.strings().ofMinLength( 1 ).ofMaxLength( 5 ).alpha().numeric().withChars( '_', '-' );
   }

   enum TopLevelElementType {
      ASPECT_MODEL( ElementType.ASPECT_MODEL ),
      ENTITY( ElementType.ENTITY ),
      CHARACTERISTIC( ElementType.CHARACTERISTIC );

      TopLevelElementType( final ElementType elementType ) {
         this.elementType = elementType;
      }

      ElementType elementType;
   }

   @Provide
   default Arbitrary<ElementType> anyTopLevelElementType() {
      return Arbitraries.of( TopLevelElementType.values() ).map( type -> type.elementType );
   }

   @Provide
   default Arbitrary<String> anyTopLevelElementName() {
      return anyUpperCaseElementName();
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyTopLevelElementUrnInItsOwnScope( final TopLevelElementType type ) {
      return Combinators.combine( anyNamespace(), anyTopLevelElementName(), anyModelElementVersion() )
            .as( ( namespace, entityName, version ) ->
                  String.format( "urn:bamm:%s:%s:%s:%s", namespace, type.elementType.getValue(), entityName, version ) )
            .map( AspectModelUrn::fromUrn );
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyEntityUrnInItsOwnScope() {
      return anyTopLevelElementUrnInItsOwnScope( TopLevelElementType.ENTITY );
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyCharacteristicUrnInItsOwnScope() {
      return anyTopLevelElementUrnInItsOwnScope( TopLevelElementType.CHARACTERISTIC );
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyAspectUrn() {
      return anyTopLevelElementUrnInItsOwnScope( TopLevelElementType.ASPECT_MODEL );
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyPropertyUrn() {
      return anyLowerCaseModelElementUrnInTopLevelElementScope();
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyOperationUrn() {
      return anyLowerCaseModelElementUrnInTopLevelElementScope();
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyEventUrn() {
      return anyUpperCaseModelElementUrnInTopLevelElementScope();
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyCharacteristicUrn() {
      return Arbitraries.oneOf(
            anyCharacteristicUrnInMetaModelScope(),
            anyUpperCaseModelElementUrnInTopLevelElementScope(),
            anyCharacteristicUrnInItsOwnScope() );
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyEntityUrn() {
      return Arbitraries.oneOf(
            anyEntityUrnInMetaModelScope(),
            anyUpperCaseModelElementUrnInTopLevelElementScope(),
            anyEntityUrnInItsOwnScope() );
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyModelElementUrnInTopLevelElementScope(
         final Arbitrary<String> anyElementName ) {
      return Combinators
            .combine( anyNamespace(), anyTopLevelElementType(), anyTopLevelElementName(),
                  anyModelElementVersion(), anyElementName )
            .as( ( namespace, containingElementType, containingElementName, version, elementName ) ->
                  String.format( "urn:bamm:%s:%s:%s:%s#%s", namespace, containingElementType.getValue(), containingElementName, version, elementName ) )
            .map( AspectModelUrn::fromUrn );
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyLowerCaseModelElementUrnInTopLevelElementScope() {
      return anyModelElementUrnInTopLevelElementScope( anyLowerCaseElementName() );
   }

   @Provide
   default Arbitrary<AspectModelUrn> anyUpperCaseModelElementUrnInTopLevelElementScope() {
      return anyModelElementUrnInTopLevelElementScope( anyUpperCaseElementName() );
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
               final MetaModelBaseAttributes baseAttributes = new MetaModelBaseAttributes(
                     metaModelVersion, characteristicUrn, characteristicUrn.getName(), preferredNames, descriptions, see );
               return new DefaultCharacteristic( baseAttributes, Optional.of( dataType ) );
            } );
   }

   @Provide
   default Arbitrary<Aspect> anyAspect() {
      return Combinators.combine( anyMetaModelVersion(), anyAspectUrn(), anyPreferredNames(), anyDescriptions(),
                  anySee(), anyProperty().list().ofMinSize( 1 ).ofMaxSize( 3 ), anyOperation().list().ofMaxSize( 3 ), anyEvent().list().ofMaxSize( 3 ) )
            .as( ( metaModelVersion, aspectUrn, preferredNames, descriptions, see, properties, operations, events ) -> {
               final MetaModelBaseAttributes baseAttributes = new MetaModelBaseAttributes(
                     metaModelVersion, aspectUrn, aspectUrn.getName(), preferredNames, descriptions, see );
               return new DefaultAspect( baseAttributes, properties, operations, events, false );
            } );
   }

   @Provide
   default Arbitrary<Operation> anyOperation() {
      return Combinators.combine( anyMetaModelVersion(), anyOperationUrn(), anyPreferredNames(), anyDescriptions(),
                  anySee(), anyProperty().list().ofMinSize( 1 ).ofMaxSize( 3 ), anyProperty().optional() )
            .as( ( metaModelVersion, operationUrn, preferredNames, descriptions, see, inputs, output ) -> {
               final MetaModelBaseAttributes baseAttributes = new MetaModelBaseAttributes(
                     metaModelVersion, operationUrn, operationUrn.getName(), preferredNames, descriptions, see );
               return new DefaultOperation( baseAttributes, inputs, output );
            } );
   }

   @Provide
   default Arbitrary<Event> anyEvent() {
      return Combinators.combine( anyMetaModelVersion(), anyEventUrn(), anyPreferredNames(), anyDescriptions(),
                  anySee(), anyProperty().list().ofMinSize( 1 ).ofMaxSize( 3 ) )
            .as( ( metaModelVersion, eventUrn, preferredNames, descriptions, see, properties ) -> {
               final MetaModelBaseAttributes baseAttributes = new MetaModelBaseAttributes(
                     metaModelVersion, eventUrn, eventUrn.getName(), preferredNames, descriptions, see );
               return new DefaultEvent( baseAttributes, properties );
            } );
   }

   @Provide
   default Arbitrary<Property> anyProperty() {
      return Combinators
            .combine( anyMetaModelVersion(), anyPropertyUrn(), anyPreferredNames(), anyDescriptions(), anySee(),
                  anyCharacteristic(), anyPayloadName() )
            .as( ( metaModelVersion, propertyUrn, preferredNames, descriptions, see, characteristic, payloadName ) -> {
               final MetaModelBaseAttributes baseAttributes = new MetaModelBaseAttributes(
                     metaModelVersion, propertyUrn, propertyUrn.getName(), preferredNames, descriptions, see );
               return new DefaultProperty( baseAttributes, characteristic, Optional.empty(), false, false, Optional.of( payloadName ) );
            } );
   }

   @Provide
   default Arbitrary<Entity> anyEntity() {
      return Combinators
            .combine( anyMetaModelVersion(), anyEntityUrn(), anyPreferredNames(), anyDescriptions(), anySee(),
                  anyProperty().list().ofMinSize( 1 ).ofMaxSize( 3 ) )
            .as( ( metaModelVersion, entityUrn, preferredNames, descriptions, see, properties ) -> {
               final MetaModelBaseAttributes baseAttributes = new MetaModelBaseAttributes(
                     metaModelVersion, entityUrn, entityUrn.getName(), preferredNames, descriptions, see );
               return DefaultEntity.createDefaultEntity( baseAttributes, properties, Optional.empty() );
            } );
   }

   @Provide
   default Arbitrary<? extends Type> anyType() {
      return Arbitraries.oneOf( anyScalar().map( x -> x ), anyEntity().map( x -> x ) );
   }

   private Value buildScalarValue( final Object value, final Scalar type ) {
      return new DefaultScalarValue( value, type );
   }

   @Provide
   default Arbitrary<Value> anyValueForRdfType( final RDFDatatype rdfDatatype, final Scalar type ) {
      final Arbitrary<String> anyString = Arbitraries.strings().ofMinLength( 1 ).ofMaxLength( 25 );

      switch ( rdfDatatype.getURI().split( "#" )[1] ) {
      case "boolean":
         return anyBoolean().map( x -> buildScalarValue( x, type ) );
      case "decimal":
      case "integer":
         return Arbitraries.bigIntegers().map( x -> buildScalarValue( x, type ) );
      case "double":
         return Arbitraries.doubles().map( x -> buildScalarValue( x, type ) );
      case "float":
         return Arbitraries.floats().map( x -> buildScalarValue( x, type ) );
      case "date":
         return anyDate().map( x -> buildScalarValue( x, type ) );
      case "time":
         return anyTime().map( x -> buildScalarValue( x, type ) );
      case "anyDateTime":
         return anyDateTime().map( x -> buildScalarValue( x, type ) );
      case "anyDateTimeStamp":
         return anyDateTimeStamp().map( x -> buildScalarValue( x, type ) );
      case "gYear":
         return anyGYear().map( x -> buildScalarValue( x, type ) );
      case "gMonth":
         return anyGMonth().map( x -> buildScalarValue( x, type ) );
      case "gDay":
         return anyGDay().map( x -> buildScalarValue( x, type ) );
      case "gYearMonth":
         return anyGYearMonth().map( x -> buildScalarValue( x, type ) );
      case "gMonthDay":
         return anyGMonthDay().map( x -> buildScalarValue( x, type ) );
      case "duration":
         return anyDuration().map( x -> buildScalarValue( x, type ) );
      case "yearMonthDuation":
         return anyYearMonthDuration().map( x -> buildScalarValue( x, type ) );
      case "dayTimeDuration":
         return anyDayTimeDuration().map( x -> buildScalarValue( x, type ) );
      case "byte":
         return Arbitraries.bytes().map( x -> buildScalarValue( x, type ) );
      case "short":
         return Arbitraries.shorts().map( x -> buildScalarValue( x, type ) );
      case "unsignedByte":
         return anyUnsignedByte().map( x -> buildScalarValue( x, type ) );
      case "int":
         return Arbitraries.integers().map( x -> buildScalarValue( x, type ) );
      case "unsignedShort":
         return anyUnsignedShort().map( x -> buildScalarValue( x, type ) );
      case "long":
         return Arbitraries.longs().map( x -> buildScalarValue( x, type ) );
      case "unsignedLong":
         return anyUnsignedLong().map( x -> buildScalarValue( x, type ) );
      case "positiveInteger":
         return anyPositiveInteger().map( x -> buildScalarValue( x, type ) );
      case "nonNegativeInteger":
         return anyNonNegativeInteger().map( x -> buildScalarValue( x, type ) );
      case "hexBinary":
         return anyHexBinary().map( x -> buildScalarValue( x, type ) );
      case "base64Binary":
         return anyBase64Binary().map( x -> buildScalarValue( x, type ) );
      case "anyURI":
         return anyUri().map( x -> buildScalarValue( x, type ) );
      case "curie":
         return anyMetaModelVersion().map( BammDataType::curie ).map( x -> buildScalarValue( x, type ) );
      case "langString":
         return Combinators.combine( anyString, anyLocale() )
               .as( LangString::new )
               .map( x -> buildScalarValue( x, type ) );
      }
      return anyString.map( x -> buildScalarValue( x, type ) );
   }

   @Provide
   default Arbitrary<EntityInstance> anyEntityInstance( final Entity entity ) {
      final Arbitrary<Map<Property, Value>> entityAssertions = Arbitraries.of( entity.getProperties() ).flatMap( property -> {
         final Arbitrary<Property> key = Arbitraries.of( property );
         final Arbitrary<Value> value = property.getDataType().map( this::anyValueForType ).orElse( Arbitraries.of( (Value) null ) );
         return Arbitraries.maps( key, value );
      } );
      return Combinators.combine( anyMetaModelVersion(), anyAspectUrn(), anyPreferredNames(), anyDescriptions(), anySee(), entityAssertions )
            .as( ( metaModelVersion, aspectUrn, preferredNames, descriptions, see, assertions ) -> {
               final MetaModelBaseAttributes baseAttributes = new MetaModelBaseAttributes(
                     metaModelVersion, aspectUrn, aspectUrn.getName(), preferredNames, descriptions, see );
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
      return ExtendedXsdDataType
            .supportedXsdTypes.stream()
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
