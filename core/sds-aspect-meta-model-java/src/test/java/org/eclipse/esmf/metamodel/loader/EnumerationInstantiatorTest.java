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

package org.eclipse.esmf.metamodel.loader;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.assertj.core.api.Assertions;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.datatypes.LangString;
import org.eclipse.esmf.characteristic.impl.DefaultSet;
import org.eclipse.esmf.characteristic.impl.DefaultSingleEntity;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestModel;

public class EnumerationInstantiatorTest extends MetaModelInstantiatorTest {

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumerationCharacteristicInstantiationExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestEnumeration" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENUMERATION, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );
      final Enumeration enumeration = (Enumeration) aspect.getProperties().get( 0 ).getCharacteristic().get();
      assertBaseAttributes( enumeration, expectedAspectModelUrn, "TestEnumeration",
            "Test Enumeration", "This is a test for enumeration.", "http://example.com/omp" );

      final Scalar scalar = (Scalar) enumeration.getDataType().get();
      assertThat( scalar.getUrn() ).isEqualTo( XSD.integer.getURI() );
      assertThat( enumeration.getValues() ).hasSize( 3 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumerationCharacteristicWithEntityDataTypeExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestEnumeration" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Enumeration enumeration = aspect.getProperties().get( 0 ).getCharacteristic().get().as( Enumeration.class );

      assertBaseAttributes( enumeration, expectedAspectModelUrn, "TestEnumeration",
            "Test Enumeration", "This is a test for enumeration.", "http://example.com/omp" );

      Assertions.assertThat( enumeration.getDataType().get() ).isInstanceOf( Entity.class );

      final EntityInstance entityInstance = enumeration.getValues().get( 0 ).as( EntityInstance.class );
      final Property property = entityInstance.getEntityType().getPropertyByName( "entityProperty" ).get();
      final String entityValue = entityInstance.getAssertions().get( property ).as( ScalarValue.class ).getValue().toString();
      assertThat( entityValue ).isEqualTo( "This is a test." );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumerationCharacteristicWithEntityDataTypeWithOptionalAndNotInPayloadPropertiesExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_WITH_OPTIONAL_AND_NOT_IN_PAYLOAD_PROPERTIES, metaModelVersion );
      final Enumeration enumeration = (Enumeration) aspect.getProperties().get( 0 ).getCharacteristic().get();

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
   public void testEnumerationCharacteristicWithListOfEntityDataTypeExpectSuccess( final KnownVersion metaModelVersion ) {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn
            .fromUrn( TestModel.TEST_NAMESPACE + "TestEnumeration" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LIST_ENTITY_ENUMERATION, metaModelVersion );

      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Enumeration enumeration = (Enumeration) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( enumeration, expectedAspectModelUrn, "TestEnumeration",
            "Test Enumeration", "This is a test for enumeration.", "http://example.com/omp" );

      Assertions.assertThat( enumeration.getDataType().get() ).isInstanceOf( Entity.class );

      final EntityInstance entityInstance = enumeration.getValues().get( 0 ).as( EntityInstance.class );
      final Entity entity = entityInstance.getEntityType();
      final Property property = entity.getPropertyByName( "entityProperty" ).get();
      final Collection<Value> values = entityInstance.getAssertions().get( property ).as( CollectionValue.class ).getValues();
      final String joined = values.stream().map( value -> value.as( ScalarValue.class ).getValue().toString() ).collect( Collectors.joining( "," ) );
      assertThat( joined ).isEqualTo( "foo,bar,baz" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumerationCharacteristicWithNestedEntityAndNotInPayloadDataTypeExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_NESTED_ENTITY_ENUMERATION_WITH_NOT_IN_PAYLOAD, metaModelVersion );
      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Enumeration enumeration = (Enumeration) aspect.getProperties().get( 0 ).getCharacteristic().get();
      Assertions.assertThat( enumeration.getDataType().get() ).isInstanceOf( Entity.class );

      final Entity dataType = enumeration.getDataType().get().as( Entity.class );
      assertThat( dataType.getProperties().size() ).isEqualTo( 2 );

      final Property nestedEntityProperty = dataType.getProperties().get( 1 );
      assertThat( nestedEntityProperty.isNotInPayload() ).isTrue();
      assertThat( nestedEntityProperty.getCharacteristic().get() ).isExactlyInstanceOf( DefaultSingleEntity.class );

      final EntityInstance entityInstance = enumeration.getValues().get( 0 ).as( EntityInstance.class );
      final Entity entity = entityInstance.getEntityType();
      final Property property = entity.getPropertyByName( "entityProperty" ).get();
      assertThat( entityInstance.getAssertions().get( property ).as( ScalarValue.class ).getValue() ).isEqualTo( "This is a test." );

      final Property nestedInstanceProperty = entity.getPropertyByName( "nestedEntityProperty" ).get();
      final EntityInstance nestedEntityInstance = entityInstance.getAssertions().get( nestedInstanceProperty ).as( EntityInstance.class );

      final Entity nestedEntity = nestedEntityInstance.getEntityType();
      assertThat( nestedEntityInstance.getAssertions().get( nestedEntity.getPropertyByName( "notInPayloadProperty" ).get() ).as( ScalarValue.class ).getValue()
            .toString() ).isEqualTo( "foo" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumerationCharacteristicWithNestedEntityListAndNotInPayloadDataTypeExpectSuccess( final KnownVersion metaModelVersion ) throws Exception {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_NESTED_ENTITY_LIST_ENUMERATION_WITH_NOT_IN_PAYLOAD, metaModelVersion );
      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Enumeration enumeration = aspect.getProperties().get( 0 ).getCharacteristic().get().as( Enumeration.class );
      Assertions.assertThat( enumeration.getDataType().get() ).isInstanceOf( Entity.class );

      final Entity dataType = enumeration.getDataType().get().as( Entity.class );
      assertThat( dataType.getProperties().size() ).isEqualTo( 2 );

      final Property nestedEntityListProperty = dataType.getProperties().get( 1 );
      assertThat( nestedEntityListProperty.isNotInPayload() ).isTrue();
      assertThat( nestedEntityListProperty.getCharacteristic().get() ).isExactlyInstanceOf( DefaultSet.class );

      final EntityInstance entityInstance = enumeration.getValues().get( 0 ).as( EntityInstance.class );
      final Entity entity = entityInstance.getEntityType();
      final Property entityProperty = entity.getProperties().stream().filter( property -> property.getName().equals( "entityProperty" ) ).findAny().get();
      assertThat( entityInstance.getAssertions().get( entityProperty ).as( ScalarValue.class ).getValue() ).isEqualTo( "This is a test." );

      final CollectionValue nestedEntityListValue = entityInstance.getAssertions().get( nestedEntityListProperty ).as( CollectionValue.class );
      Assertions.assertThat( nestedEntityListValue.getValues() ).hasSize( 1 );

      final EntityInstance nestedEntityInstance = nestedEntityListValue.getValues().iterator().next().as( EntityInstance.class );
      final Entity nestedEntity = nestedEntityInstance.getEntityType();
      final Property nestedEntityProperty = nestedEntity.getProperties().stream().filter( property ->
            property.getName().equals( "notInPayloadProperty" ) ).findAny().get();
      assertThat( nestedEntityInstance.getAssertions().get( nestedEntityProperty ).as( ScalarValue.class ).getValue() ).isEqualTo( "foo" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testEnumerationWithNestedEntityWithLangStringExpectSuccess( final KnownVersion metaModelVersion ) {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_LANG_STRING, metaModelVersion );
      assertThat( aspect.getProperties() ).hasSize( 1 );

      final Enumeration enumeration = aspect.getProperties().get( 0 ).getCharacteristic().get().as( Enumeration.class );
      final EntityInstance entityInstance = enumeration.getValues().get( 0 ).as( EntityInstance.class );
      final Property property = entityInstance.getEntityType().getPropertyByName( "entityProperty" ).get();
      final ScalarValue scalarValue = entityInstance.getAssertions().get( property ).as( ScalarValue.class );
      assertThat( scalarValue.getType().getUrn() ).isEqualTo( RDF.langString.getURI() );
      final LangString langString = (LangString) scalarValue.getValue();
      assertThat( langString.getValue() ).isEqualTo( "This is a test." );
      assertThat( langString.getLanguageTag() ).isEqualTo( Locale.ENGLISH );
   }
}
