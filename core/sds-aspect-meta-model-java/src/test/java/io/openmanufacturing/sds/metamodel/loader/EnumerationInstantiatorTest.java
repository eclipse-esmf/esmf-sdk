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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.vocabulary.XSD;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.Entity;
import io.openmanufacturing.sds.metamodel.Enumeration;
import io.openmanufacturing.sds.metamodel.Property;
import io.openmanufacturing.sds.metamodel.Scalar;
import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.metamodel.impl.DefaultSet;
import io.openmanufacturing.sds.metamodel.impl.DefaultSingleEntity;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestModel;
import io.vavr.Tuple;
import io.vavr.Tuple2;

public class EnumerationInstantiatorTest extends MetaModelInstantiatorTest {

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumerationCharacteristicInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn
            .fromUrn( TestModel.TEST_NAMESPACE + "TestEnumeration" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENUMERATION, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Enumeration enumeration = (Enumeration) aspect.getProperties().get( 0 ).getCharacteristic();

      assertBaseAttributes( enumeration, expectedAspectModelUrn, "TestEnumeration",
            "Test Enumeration", "This is a test for enumeration.", "http://example.com/omp" );

      final Scalar scalar = (Scalar) enumeration.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.xint.getURI() );

      assertThat( enumeration.getValues() ).hasSize( 3 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumerationCharacteristicWithEntityDataTypeExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn
            .fromUrn( TestModel.TEST_NAMESPACE + "TestEnumeration" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Enumeration enumeration = (Enumeration) aspect.getProperties().get( 0 ).getCharacteristic();

      assertBaseAttributes( enumeration, expectedAspectModelUrn, "TestEnumeration",
            "Test Enumeration", "This is a test for enumeration.", "http://example.com/omp" );

      assertThat( enumeration.getDataType().get() ).isInstanceOf( Entity.class );

      final Map entityInstance = (Map) enumeration.getValues().get( 0 );
      final String entityValues = (String) entityInstance.get( "entityProperty" );

      assertThat( entityValues ).isEqualTo( "This is a test." );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumerationCharacteristicWithEntityDataTypeWithOptionalAndNotInPayloadPropertiesExpectSuccess(
         final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect(
            TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_WITH_OPTIONAL_AND_NOT_IN_PAYLOAD_PROPERTIES,
            metaModelVersion );
      final Enumeration enumeration = (Enumeration) aspect.getProperties().get( 0 ).getCharacteristic();

      final Type dataType = enumeration.getDataType().get();
      assertThat( dataType ).isInstanceOf( Entity.class );

      final Entity entity = (Entity) dataType;
      final List<Property> entityProperties = entity.getProperties();
      assertThat( entityProperties ).hasSize( 3 );

      final Property entityProperty = entityProperties.get( 0 );
      assertThat( entityProperty.getName() ).isEqualTo( "entityProperty" );
      assertThat( entityProperty.isOptional() ).isFalse();
      assertThat( entityProperty.isNotInPayload() ).isFalse();

      final Property optionalEntityProperty = entityProperties.get( 1 );
      assertThat( optionalEntityProperty.getName() ).isEqualTo( "optionalEntityProperty" );
      assertThat( optionalEntityProperty.isOptional() ).isTrue();
      assertThat( optionalEntityProperty.isNotInPayload() ).isFalse();

      final Property notInPayloadEntityProperty = entityProperties.get( 2 );
      assertThat( notInPayloadEntityProperty.getName() ).isEqualTo( "notInPayloadEntityProperty" );
      assertThat( notInPayloadEntityProperty.isOptional() ).isFalse();
      assertThat( notInPayloadEntityProperty.isNotInPayload() ).isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumerationCharacteristicWithListOfEntityDataTypeExpectSuccess(
         final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn
            .fromUrn( TestModel.TEST_NAMESPACE + "TestEnumeration" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LIST_ENTITY_ENUMERATION, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Enumeration enumeration = (Enumeration) aspect.getProperties().get( 0 ).getCharacteristic();

      assertBaseAttributes( enumeration, expectedAspectModelUrn, "TestEnumeration",
            "Test Enumeration", "This is a test for enumeration.", "http://example.com/omp" );

      assertThat( enumeration.getDataType().get() ).isInstanceOf( Entity.class );

      final Map entityInstance = (Map) enumeration.getValues().get( 0 );
      final List entityValues = (List) entityInstance.get( "entityProperty" );

      assertThat( entityValues.stream().collect( Collectors.joining( "," ) ) ).isEqualTo( "foo,bar,baz" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumerationCharacteristicWithNestedEntityAndNotInPayloadDataTypeExpectSuccess(
         final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_NESTED_ENTITY_ENUMERATION_WITH_NOT_IN_PAYLOAD,
            metaModelVersion );
      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Enumeration enumeration = (Enumeration) aspect.getProperties().get( 0 ).getCharacteristic();
      assertThat( enumeration.getDataType().get() ).isInstanceOf( Entity.class );

      final Entity dataType = (Entity) enumeration.getDataType().get();
      assertThat( dataType.getProperties().size() ).isEqualTo( 2 );

      final Property nestedEntityProperty = dataType.getProperties().get( 1 );
      assertThat( nestedEntityProperty.isNotInPayload() ).isTrue();
      assertThat( nestedEntityProperty.getCharacteristic() ).isExactlyInstanceOf( DefaultSingleEntity.class );

      final Map entityInstance = (Map) enumeration.getValues().get( 0 );
      assertThat( entityInstance.get( "entityProperty" ) ).isEqualTo( "This is a test." );

      final Map nestedEntityInstance = (Map) entityInstance.get( "nestedEntityProperty" );
      assertThat( nestedEntityInstance.get( "notInPayloadProperty" ) ).isEqualTo( "foo" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumerationCharacteristicWithNestedEntityListAndNotInPayloadDataTypeExpectSuccess(
         final KnownVersion metaModelVersion )
         throws Exception {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_NESTED_ENTITY_LIST_ENUMERATION_WITH_NOT_IN_PAYLOAD,
            metaModelVersion );
      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Enumeration enumeration = (Enumeration) aspect.getProperties().get( 0 ).getCharacteristic();
      assertThat( enumeration.getDataType().get() ).isInstanceOf( Entity.class );

      final Entity dataType = (Entity) enumeration.getDataType().get();
      assertThat( dataType.getProperties().size() ).isEqualTo( 2 );

      final Property nestedEntityListProperty = dataType.getProperties().get( 1 );
      assertThat( nestedEntityListProperty.isNotInPayload() ).isTrue();
      assertThat( nestedEntityListProperty.getCharacteristic() ).isExactlyInstanceOf( DefaultSet.class );

      final Map entityInstance = (Map) enumeration.getValues().get( 0 );
      assertThat( entityInstance.get( "entityProperty" ) ).isEqualTo( "This is a test." );

      final HashSet nestedEntityList = (HashSet) entityInstance.get( "nestedEntityListProperty" );
      assertThat( nestedEntityList ).hasSize( 1 );

      final Map nestedEntityInstance = (Map) nestedEntityList.iterator().next();
      assertThat( nestedEntityInstance.get( "notInPayloadProperty" ) ).isEqualTo( "foo" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumerationWithNestedEntityWithLangStringExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_LANG_STRING, metaModelVersion );
      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Enumeration enumeration = (Enumeration) aspect.getProperties().get( 0 ).getCharacteristic();
      final Map<String, Object> entityInstance = (Map<String, Object>) enumeration.getValues().get( 0 );
      assertThat( entityInstance.get( "entityProperty" ) ).isInstanceOf( List.class );
      final List<Tuple2<String, String>> entityProperty = (List<Tuple2<String, String>>) entityInstance
            .get( "entityProperty" );
      assertThat( entityProperty ).containsExactlyInAnyOrder(
            Tuple.of( "de", "Dies ist ein Test." ),
            Tuple.of( "en", "This is a test." )
      );
   }
}
