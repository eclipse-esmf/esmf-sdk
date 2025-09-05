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

package org.eclipse.esmf.aspectmodel.loader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.eclipse.esmf.test.shared.AspectModelAsserts.assertThat;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AbstractEntity;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.ComplexType;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Namespace;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.characteristic.Code;
import org.eclipse.esmf.metamodel.characteristic.Either;
import org.eclipse.esmf.metamodel.characteristic.SingleEntity;
import org.eclipse.esmf.metamodel.impl.DefaultEntity;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestModel;

import org.apache.jena.vocabulary.XSD;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AspectModelInstantiatorTest extends AbstractAspectModelInstantiatorTest {
   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   void testLoadAspectExpectSuccess( final TestAspect aspect ) {
      assertThatCode( () -> loadAspect( aspect ) ).doesNotThrowAnyException();
   }

   @Test
   void testAspectTransformationExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_SEE_ATTRIBUTE );
      final AspectModelUrn expectedAspectModelUrn = TestAspect.ASPECT_WITH_SEE_ATTRIBUTE.getUrn();
      assertBaseAttributes( aspect, expectedAspectModelUrn, "AspectWithSeeAttribute", "Test Aspect",
            "This is a test Aspect.", "http://example.com/" );
   }

   @Test
   void testPropertyInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "testProperty" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_PROPERTY );

      assertThat( aspect ).properties().hasSize( 1 );
      assertThat( aspect ).isNoCollectionAspect();

      final Property property = aspect.getProperties().get( 0 );

      assertBaseAttributes( property, expectedAspectModelUrn, "testProperty", "Test Property",
            "This is a test property.", "http://example.com/me", "http://example.com/" );
      assertThat( property ).exampleValue().value().isEqualTo( "Example Value" );
      assertThat( property ).isMandatory();
      assertThat( property ).characteristic().hasName( "Text" );
      assertThat( property.getDataType() ).isInstanceOf( Optional.class );
      assertThat( property.getDataType().get() ).isInstanceOf( Scalar.class );
   }

   @Test
   void testOptionalPropertyInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "testProperty" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_OPTIONAL_PROPERTY );

      assertThat( aspect ).properties().hasSize( 1 );

      final Property property = aspect.getProperties().get( 0 );

      assertBaseAttributes( property, expectedAspectModelUrn, "testProperty", "Test Property",
            "This is a test property.", "http://example.com/me", "http://example.com/" );

      assertThat( property ).hasNoExampleValue();
      assertThat( property ).isOptional();
      assertThat( property ).hasPayloadName( "testProperty" );
      assertThat( property ).isInPayload();
      assertThat( property ).characteristic().hasName( "Text" );
   }

   @Test
   void testNotInPayloadPropertyInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "description" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_NOT_IN_PAYLOAD_PROPERTIES );

      final Property entityEnumProperty = aspect.getProperties().get( 0 );
      final Entity entity = (Entity) entityEnumProperty.getDataType().get();
      final Property property = entity.getProperties().get( 1 );

      assertBaseAttributes( property, expectedAspectModelUrn, "description", "Test Property",
            "This is a test property.", "http://example.com/me", "http://example.com/" );

      assertThat( property ).hasNoExampleValue();
      assertThat( property ).isNotOptional();
      assertThat( property ).isNotInPayload();
      assertThat( property ).characteristic().hasName( "Text" );
   }

   @Test
   void testPropertyWithPayloadNameInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "testProperty" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME );

      assertThat( aspect ).properties().hasSize( 1 );

      final Property property = aspect.getProperties().get( 0 );

      assertBaseAttributes( property, expectedAspectModelUrn, "testProperty", "Test Property",
            "This is a test property.", "http://example.com/me", "http://example.com/" );

      assertThat( property ).hasNoExampleValue();
      assertThat( property ).isNotOptional();
      assertThat( property ).isInPayload();
      assertThat( property ).hasPayloadName( "test" );
      assertThat( property ).characteristic().hasName( "Text" );
   }

   @Test
   void testEitherCharacteristicInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestEither" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_EITHER );

      assertThat( aspect ).properties().hasSize( 1 );

      final Either either = (Either) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( either, expectedAspectModelUrn, "TestEither",
            "Test Either", "This is a test Either.", "http://example.com/" );

      assertThat( either.getDataType() ).isNotPresent();
      assertThat( either ).left().hasName( "Text" );
      assertThat( either ).right().hasName( "Boolean" );
   }

   @Test
   void testSingleEntityCharacteristicInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "EntityCharacteristic" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY );

      assertThat( aspect ).properties().hasSize( 1 );

      final SingleEntity singleEntity = (SingleEntity) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( singleEntity, expectedAspectModelUrn, "EntityCharacteristic",
            "Test Entity Characteristic", "This is a test Entity Characteristic", "http://example.com/" );

      assertThat( singleEntity ).dataType().isEntity();
   }

   @Test
   void testAbstractEntityInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "AbstractTestEntity" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY );

      assertThat( aspect ).properties().hasSize( 1 );

      final Entity entity = (Entity) aspect.getProperties().get( 0 ).getCharacteristic().get().getDataType().get();
      final AbstractEntity abstractEntity = (AbstractEntity) entity.getExtends().get();
      assertBaseAttributes( abstractEntity, expectedAspectModelUrn, "AbstractTestEntity",
            "Abstract Test Entity", "This is a abstract test entity" );
      final List<ComplexType> extendingElements = abstractEntity.getExtendingElements();
      assertThat( extendingElements ).hasSize( 1 );
      assertThat( extendingElements.get( 0 ) ).isEqualTo( entity );
   }

   @Test
   void testCollectionWithAbstractEntityInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "AbstractTestEntity" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY );

      assertThat( aspect ).properties().hasSize( 1 );

      final AbstractEntity abstractEntity = (AbstractEntity) aspect.getProperties().get( 0 ).getCharacteristic().get().getDataType().get();
      assertThat( abstractEntity.getExtends() ).isEmpty();
      assertBaseAttributes( abstractEntity, expectedAspectModelUrn, "AbstractTestEntity", null,
            "This is an abstract test entity" );
      final List<ComplexType> extendingElements = abstractEntity.getExtendingElements();
      assertThat( extendingElements ).hasSize( 1 );
   }

   @Test
   void testCodeCharacteristicInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestCode" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_CODE );

      assertThat( aspect ).properties().hasSize( 1 );

      final Code code = (Code) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( code, expectedAspectModelUrn, "TestCode",
            "Test Code", "This is a test code.", "http://example.com/" );

      assertThat( code ).dataType().isScalarThat().hasUrn( XSD.xint.getURI() );
   }

   @Test
   void testCollectionAspectInstantiationExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LIST );
      final AspectModelUrn expectedAspectModelUrn = TestAspect.ASPECT_WITH_LIST.getUrn();

      assertBaseAttributes( aspect, expectedAspectModelUrn, "AspectWithList", "Test Aspect",
            "This is a test description",
            "http://example.com/" );

      assertThat( aspect ).properties().hasSize( 1 );
      assertThat( aspect ).isCollectionAspect();
   }

   @Test
   void testAspectWithTwoCollectionsInstantiationExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_TWO_LISTS );

      final AspectModelUrn expectedAspectModelUrn = TestAspect.ASPECT_WITH_TWO_LISTS.getUrn();

      assertBaseAttributes( aspect, expectedAspectModelUrn, "AspectWithTwoLists", "Test Aspect",
            "This is a test description",
            "http://example.com/" );

      assertThat( aspect ).properties().hasSize( 2 );
      assertThat( aspect ).isNoCollectionAspect();
   }

   @Test
   void testAspectWithListAndAdditionalPropertyInstantiationExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LIST_AND_ADDITIONAL_PROPERTY );

      final AspectModelUrn expectedAspectModelUrn = TestAspect.ASPECT_WITH_LIST_AND_ADDITIONAL_PROPERTY.getUrn();

      assertBaseAttributes( aspect, expectedAspectModelUrn, "AspectWithListAndAdditionalProperty",
            "Test Aspect", "This is a test description", "http://example.com/" );

      assertThat( aspect ).properties().hasSize( 2 );
      assertThat( aspect ).isCollectionAspect();
   }

   @Test
   void testAspectWithRecursivePropertyWithOptional() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL );
      assertThat( aspect ).properties().hasSize( 1 );
      final Property firstProperty = aspect.getProperties().get( 0 );
      final Property secondProperty = ( (DefaultEntity) firstProperty.getCharacteristic().get().getDataType().get() ).getProperties()
            .get( 0 );
      final Property thirdProperty = ( (DefaultEntity) secondProperty.getCharacteristic().get().getDataType().get() ).getProperties()
            .get( 0 );
      assertThat( firstProperty ).isNotEqualTo( secondProperty );
      assertThat( secondProperty ).isEqualTo( thirdProperty );
      assertThat( firstProperty.getCharacteristic().get().urn() ).isEqualTo( secondProperty.getCharacteristic().get().urn() );
      assertThat( secondProperty.getCharacteristic().get().urn() ).isEqualTo( thirdProperty.getCharacteristic().get().urn() );
   }

   @Test
   void testMetaModelBaseAttributesFactoryMethod() {
      final AspectModelUrn urn = AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.samm:1.0.0#TestAspect" );
      final MetaModelBaseAttributes baseAttributes = MetaModelBaseAttributes.builder().withUrn( urn ).build();
      assertThat( baseAttributes.urn() ).isEqualTo( urn );
      assertThat( baseAttributes.getPreferredNames() ).isEmpty();
      assertThat( baseAttributes.getDescriptions() ).isEmpty();
      assertThat( baseAttributes.getSee() ).isEmpty();
   }

   @Test
   void testMetaModelBaseAttributesBuilder() {
      final AspectModelUrn urn = AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.samm:1.0.0#TestAspect" );
      final MetaModelBaseAttributes baseAttributes = MetaModelBaseAttributes.builder()
            .withUrn( urn )
            .withDescription( Locale.ENGLISH, "description" )
            .withPreferredName( Locale.ENGLISH, "preferredName" )
            .withSee( "see1" ).withSee( "see2" )
            .build();

      assertThat( baseAttributes.urn() ).isEqualTo( urn );
      assertThat( baseAttributes.getPreferredNames() ).hasSize( 1 )
            .allMatch( preferredName -> preferredName.getLanguageTag().equals( Locale.ENGLISH ) );
      assertThat( baseAttributes.getDescriptions() ).hasSize( 1 )
            .allMatch( description -> description.getLanguageTag().equals( Locale.ENGLISH ) );
      assertThat( baseAttributes.getSee() ).hasSize( 2 ).contains( "see1", "see2" );
   }

   @Test
   void testLoadAspectWithNamespaceDescription() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_NAMESPACE_DESCRIPTION );
      final Namespace namespace = aspect.getSourceFile().namespace();
      assertThat( namespace ).urn().hasNamespaceMainPart( aspect.urn().getNamespaceMainPart() );
      assertThat( namespace ).urn().hasVersion( aspect.urn().getVersion() );
      assertThat( namespace ).hasPreferredName( "Test namespace", Locale.ENGLISH );
      assertThat( namespace ).hasDescription( "Test of the namespace pseudo element", Locale.ENGLISH );
      assertThat( namespace ).see().hasSize( 1 ).contains( "http://example.com/" );
      assertThat( namespace ).elements().containsAll( List.of( aspect, aspect.getProperties().get( 0 ) ) );
   }
}
