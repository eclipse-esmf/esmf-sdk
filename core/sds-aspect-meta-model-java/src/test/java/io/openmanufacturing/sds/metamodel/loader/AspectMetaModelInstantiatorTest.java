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

package io.openmanufacturing.sds.metamodel.loader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.jena.vocabulary.XSD;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.AbstractEntity;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Code;
import io.openmanufacturing.sds.metamodel.ComplexType;
import io.openmanufacturing.sds.metamodel.Either;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.ScalarValue;
import io.openmanufacturing.sds.metamodel.SingleEntity;
import io.openmanufacturing.sds.metamodel.impl.DefaultEntity;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestModel;

public class AspectMetaModelInstantiatorTest extends MetaModelInstantiatorTest {
   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   public void testLoadAspectExpectSuccess( final TestAspect aspect ) {
      assertThatCode( () ->
            loadAspect( aspect, KnownVersion.getLatest() )
      ).doesNotThrowAnyException();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectTransformationExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_SEE, metaModelVersion );
      final AspectModelUrn expectedAspectModelUrn = TestAspect.ASPECT_WITH_SEE.getUrn();
      assertBaseAttributes( aspect, expectedAspectModelUrn, "AspectWithSee", "Test Aspect With See",
            "This is a test description", "http://example.com/omp" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testPropertyInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "testProperty" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_PROPERTY, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );
      assertThat( aspect.isCollectionAspect() ).isFalse();

      final Property property = aspect.getProperties().get( 0 );

      assertBaseAttributes( property, expectedAspectModelUrn, "testProperty", "Test Property",
            "This is a test property.", "http://example.com/me", "http://example.com/omp" );
      assertThat( property.getExampleValue() ).map( value -> value.as( ScalarValue.class ).getValue() ).contains( "Example Value" );
      assertThat( property.isOptional() ).isFalse();
      assertThat( property.getCharacteristic().get().getName() ).isEqualTo( "Text" );
      assertThat( property.getDataType() ).isInstanceOf( Optional.class );
      assertThat( property.getDataType().get() ).isInstanceOf( Scalar.class );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testOptionalPropertyInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "testProperty" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTY, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Property property = aspect.getProperties().get( 0 );

      assertBaseAttributes( property, expectedAspectModelUrn, "testProperty", "Test Property",
            "This is a test property.", "http://example.com/me", "http://example.com/omp" );

      assertThat( property.getExampleValue() ).isEmpty();
      assertThat( property.isOptional() ).isTrue();
      assertThat( property.getPayloadName() ).isEqualTo( "testProperty" );
      assertThat( property.isNotInPayload() ).isFalse();
      assertThat( property.getCharacteristic().get().getName() ).isEqualTo( "Text" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testNotInPayloadPropertyInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "description" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_NOT_IN_PAYLOAD_PROPERTIES, metaModelVersion );

      final Property entityEnumProperty = aspect.getProperties().get( 0 );
      final Entity entity = (Entity) entityEnumProperty.getDataType().get();
      final Property property = entity.getProperties().get( 1 );

      assertBaseAttributes( property, expectedAspectModelUrn, "description", "Test Property",
            "This is a test property.", "http://example.com/me", "http://example.com/omp" );

      assertThat( property.getExampleValue() ).isEmpty();
      assertThat( property.isOptional() ).isFalse();
      assertThat( property.isNotInPayload() ).isTrue();
      assertThat( property.getCharacteristic().get().getName() ).isEqualTo( "Text" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testPropertyWithPayloadNameInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "testProperty" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Property property = aspect.getProperties().get( 0 );

      assertBaseAttributes( property, expectedAspectModelUrn, "testProperty", "Test Property",
            "This is a test property.", "http://example.com/me", "http://example.com/omp" );

      assertThat( property.getExampleValue() ).isEmpty();
      assertThat( property.isOptional() ).isFalse();
      assertThat( property.getPayloadName() ).isEqualTo( "test" );
      assertThat( property.isNotInPayload() ).isFalse();
      assertThat( property.getCharacteristic().get().getName() ).isEqualTo( "Text" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEitherCharacteristicInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestEither" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_EITHER, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Either either = (Either) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( either, expectedAspectModelUrn, "TestEither",
            "Test Either", "This is a test Either.", "http://example.com/omp" );

      assertThat( either.getDataType() ).isNotPresent();
      assertThat( either.getLeft().getName() ).isEqualTo( "Text" );
      assertThat( either.getRight().getName() ).isEqualTo( "Boolean" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testSingleEntityCharacteristicInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "EntityCharacteristic" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final SingleEntity singleEntity = (SingleEntity) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( singleEntity, expectedAspectModelUrn, "EntityCharacteristic",
            "Test Entity Characteristic", "This is a test Entity Characteristic", "http://example.com/omp" );

      assertThat( singleEntity.getDataType().get() ).isInstanceOf( Entity.class );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testAbstractEntityInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "AbstractTestEntity" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Entity entity = (Entity) aspect.getProperties().get( 0 ).getCharacteristic().get().getDataType().get();
      final AbstractEntity abstractEntity = (AbstractEntity) entity.getExtends().get();
      assertBaseAttributes( abstractEntity, expectedAspectModelUrn, "AbstractTestEntity",
            "Abstract Test Entity", "This is a abstract test entity" );
      final List<ComplexType> extendingElements = abstractEntity.getExtendingElements();
      assertThat( extendingElements ).hasSize( 1 );
      assertThat( extendingElements.get( 0 ) ).isEqualTo( entity );
   }

   @ParameterizedTest
   @MethodSource( value = "versionsStartingWith2_0_0" )
   public void testCollectionWithAbstractEntityInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "AbstractTestEntity" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final AbstractEntity abstractEntity = (AbstractEntity) aspect.getProperties().get( 0 ).getCharacteristic().get().getDataType().get();
      assertThat( abstractEntity.getExtends() ).isEmpty();
      assertBaseAttributes( abstractEntity, expectedAspectModelUrn, "AbstractTestEntity", "AbstractTestEntity", "This is an abstract test entity" );
      final List<ComplexType> extendingElements = abstractEntity.getExtendingElements();
      assertThat( extendingElements ).hasSize( 1 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testCodeCharacteristicInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestCode" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_CODE, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Code code = (Code) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( code, expectedAspectModelUrn, "TestCode",
            "Test Code", "This is a test code.", "http://example.com/omp" );

      final Scalar scalar = (Scalar) code.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xint.getURI() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testCollectionAspectInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LIST, metaModelVersion );
      final AspectModelUrn expectedAspectModelUrn = TestAspect.ASPECT_WITH_LIST.getUrn();

      assertBaseAttributes( aspect, expectedAspectModelUrn, "AspectWithList", "Test Aspect",
            "This is a test description",
            "http://example.com/omp" );

      assertThat( aspect.getProperties() ).hasSize( 1 );
      assertThat( aspect.isCollectionAspect() ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithTwoCollectionsInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_TWO_LISTS, metaModelVersion );

      final AspectModelUrn expectedAspectModelUrn = TestAspect.ASPECT_WITH_TWO_LISTS.getUrn();

      assertBaseAttributes( aspect, expectedAspectModelUrn, "AspectWithTwoLists", "Test Aspect",
            "This is a test description",
            "http://example.com/omp" );

      assertThat( aspect.getProperties() ).hasSize( 2 );
      assertThat( aspect.isCollectionAspect() ).isFalse();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithListAndAdditionalPropertyInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LIST_AND_ADDITIONAL_PROPERTY, metaModelVersion );

      final AspectModelUrn expectedAspectModelUrn = TestAspect.ASPECT_WITH_LIST_AND_ADDITIONAL_PROPERTY.getUrn();

      assertBaseAttributes( aspect, expectedAspectModelUrn, "AspectWithListAndAdditionalProperty",
            "Test Aspect", "This is a test description", "http://example.com/omp" );

      assertThat( aspect.getProperties() ).hasSize( 2 );
      assertThat( aspect.isCollectionAspect() ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithRecursivePropertyWithOptional( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL, metaModelVersion );
      assertThat( aspect.getProperties().size() ).isEqualTo( 1 );
      final Property firstProperty = aspect.getProperties().get( 0 );
      final Property secondProperty = ((DefaultEntity) firstProperty.getCharacteristic().get().getDataType().get()).getProperties().get( 0 );
      final Property thirdProperty = ((DefaultEntity) secondProperty.getCharacteristic().get().getDataType().get()).getProperties().get( 0 );
      assertThat( firstProperty ).isNotEqualTo( secondProperty );
      assertThat( secondProperty ).isEqualTo( thirdProperty );
      assertThat( firstProperty.getCharacteristic() ).isEqualTo( secondProperty.getCharacteristic() );
      assertThat( secondProperty.getCharacteristic() ).isEqualTo( thirdProperty.getCharacteristic() );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testMetaModelBaseAttributesFactoryMethod( final KnownVersion metaModelVersion ) {
      final AspectModelUrn urn = AspectModelUrn.fromUrn( "urn:bamm:io.openmanufacturing:aspect-model:TestAspect:1.0.0" );
      final MetaModelBaseAttributes baseAttributes = MetaModelBaseAttributes.from( metaModelVersion, urn, "someName" );

      assertThat( baseAttributes.getUrn() ).contains( urn );
      assertThat( baseAttributes.getName() ).isEqualTo( "someName" );
      assertThat( baseAttributes.getPreferredNames() ).isEmpty();
      assertThat( baseAttributes.getDescriptions() ).isEmpty();
      assertThat( baseAttributes.getSee() ).isEmpty();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testMetaModelBaseAttributesBuilder( final KnownVersion metaModelVersion ) {
      final AspectModelUrn urn = AspectModelUrn.fromUrn( "urn:bamm:io.openmanufacturing:aspect-model:TestAspect:1.0.0" );
      final MetaModelBaseAttributes baseAttributes = MetaModelBaseAttributes.builderFor( "someName" )
            .withMetaModelVersion( metaModelVersion )
            .withUrn( urn )
            .withDescription( Locale.ENGLISH, "description" )
            .withPreferredName( Locale.ENGLISH, "preferredName" )
            .withSee( "see1" ).withSee( "see2" )
            .build();

      assertThat( baseAttributes.getUrn() ).contains( urn );
      assertThat( baseAttributes.getName() ).isEqualTo( "someName" );
      assertThat( baseAttributes.getPreferredNames() ).hasSize( 1 ).allMatch( preferredName -> preferredName.getLanguageTag().equals( Locale.ENGLISH ) );
      assertThat( baseAttributes.getDescriptions() ).hasSize( 1 ).allMatch( description -> description.getLanguageTag().equals( Locale.ENGLISH ) );
      assertThat( baseAttributes.getSee() ).hasSize( 2 ).contains( "see1", "see2" );
   }
}
