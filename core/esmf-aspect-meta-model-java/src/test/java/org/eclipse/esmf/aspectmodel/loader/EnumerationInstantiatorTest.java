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
import static org.eclipse.esmf.test.shared.AspectModelAsserts.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.CollectionValue;
import org.eclipse.esmf.metamodel.Entity;
import org.eclipse.esmf.metamodel.EntityInstance;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.Scalar;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.metamodel.characteristic.Enumeration;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultSet;
import org.eclipse.esmf.metamodel.characteristic.impl.DefaultSingleEntity;
import org.eclipse.esmf.metamodel.datatype.LangString;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestModel;

import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.junit.jupiter.api.Test;

public class EnumerationInstantiatorTest extends AbstractAspectModelInstantiatorTest {
   @Test
   public void testEnumerationCharacteristicInstantiationExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestEnumeration" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENUMERATION );

      assertThat( aspect ).properties().hasSize( 1 );
      final Enumeration enumeration = (Enumeration) aspect.getProperties().get( 0 ).getCharacteristic().get();
      assertBaseAttributes( enumeration, expectedAspectModelUrn, "TestEnumeration",
            "Test Enumeration", "This is a test for enumeration.", "http://example.com/" );

      final Scalar scalar = (Scalar) enumeration.getDataType().get();
      assertThat( scalar ).hasUrn( XSD.integer.getURI() );
      assertThat( enumeration ).values().hasSize( 3 );
   }

   @Test
   public void testEnumerationCharacteristicWithEntityDataTypeExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn.fromUrn( TestModel.TEST_NAMESPACE + "TestEnumeration" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION );

      assertThat( aspect ).properties().hasSize( 1 );

      final Enumeration enumeration = aspect.getProperties().get( 0 ).getCharacteristic().get().as( Enumeration.class );

      assertBaseAttributes( enumeration, expectedAspectModelUrn, "TestEnumeration",
            "Test Enumeration", "This is a test for enumeration.", "http://example.com/" );

      assertThat( enumeration ).dataType().isEntity();

      final EntityInstance entityInstance = enumeration.getValues().get( 0 ).as( EntityInstance.class );
      final Property property = entityInstance.getEntityType().getPropertyByName( "entityProperty" ).get();
      final String entityValue = entityInstance.getAssertions().get( property ).as( ScalarValue.class ).getValue().toString();
      assertThat( entityValue ).isEqualTo( "This is a test." );
   }

   @Test
   public void testEnumerationCharacteristicWithEntityDataTypeWithOptionalAndNotInPayloadPropertiesExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_WITH_OPTIONAL_AND_NOT_IN_PAYLOAD_PROPERTIES );
      final Enumeration enumeration = (Enumeration) aspect.getProperties().get( 0 ).getCharacteristic().get();

      final Type dataType = enumeration.getDataType().get();
      assertThat( dataType ).isInstanceOf( Entity.class );

      final Entity entity = (Entity) dataType;
      final List<Property> entityProperties = entity.getProperties();
      assertThat( entityProperties ).hasSize( 3 );

      final Property entityProperty = entityProperties.get( 0 );
      assertThat( entityProperty ).hasName( "entityProperty" );
      assertThat( entityProperty ).isNotOptional();
      assertThat( entityProperty ).isInPayload();

      final Property optionalEntityProperty = entityProperties.get( 1 );
      assertThat( optionalEntityProperty ).hasName( "optionalEntityProperty" );
      assertThat( optionalEntityProperty ).isOptional();
      assertThat( optionalEntityProperty ).isInPayload();

      final Property notInPayloadEntityProperty = entityProperties.get( 2 );
      assertThat( notInPayloadEntityProperty ).hasName( "notInPayloadEntityProperty" );
      assertThat( notInPayloadEntityProperty ).isNotOptional();
      assertThat( notInPayloadEntityProperty ).isNotInPayload();
   }

   @Test
   public void testEnumerationCharacteristicWithListOfEntityDataTypeExpectSuccess() {
      final AspectModelUrn expectedAspectModelUrn = AspectModelUrn
            .fromUrn( TestModel.TEST_NAMESPACE + "TestEnumeration" );
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_LIST_ENTITY_ENUMERATION );

      assertThat( aspect ).properties().hasSize( 1 );

      final Enumeration enumeration = (Enumeration) aspect.getProperties().get( 0 ).getCharacteristic().get();

      assertBaseAttributes( enumeration, expectedAspectModelUrn, "TestEnumeration",
            "Test Enumeration", "This is a test for enumeration.", "http://example.com/" );

      assertThat( enumeration ).dataType().isEntity();

      final EntityInstance entityInstance = enumeration.getValues().get( 0 ).as( EntityInstance.class );
      final Entity entity = entityInstance.getEntityType();
      final Property property = entity.getPropertyByName( "entityProperty" ).get();
      final Collection<Value> values = entityInstance.getAssertions().get( property ).as( CollectionValue.class ).getValues();
      final String joined = values.stream().map( value -> value.as( ScalarValue.class ).getValue().toString() )
            .collect( Collectors.joining( "," ) );
      assertThat( joined ).isEqualTo( "foo,bar,baz" );
   }

   @Test
   public void testEnumerationCharacteristicWithNestedEntityAndNotInPayloadDataTypeExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_NESTED_ENTITY_ENUMERATION_WITH_NOT_IN_PAYLOAD );
      assertThat( aspect ).properties().hasSize( 1 );

      final Enumeration enumeration = (Enumeration) aspect.getProperties().get( 0 ).getCharacteristic().get();
      assertThat( enumeration ).dataType().isEntity();

      final Entity dataType = enumeration.getDataType().get().as( Entity.class );
      assertThat( dataType ).properties().size().isEqualTo( 2 );

      final Property nestedEntityProperty = dataType.getProperties().get( 1 );
      assertThat( nestedEntityProperty ).isNotInPayload();
      assertThat( nestedEntityProperty ).characteristic().isInstanceOf( DefaultSingleEntity.class );

      final EntityInstance entityInstance = enumeration.getValues().get( 0 ).as( EntityInstance.class );
      final Entity entity = entityInstance.getEntityType();
      final Property property = entity.getPropertyByName( "entityProperty" ).get();
      assertThat( entityInstance.getAssertions().get( property ).as( ScalarValue.class ).getValue() ).isEqualTo( "This is a test." );

      final Property nestedInstanceProperty = entity.getPropertyByName( "nestedEntityProperty" ).get();
      final EntityInstance nestedEntityInstance = entityInstance.getAssertions().get( nestedInstanceProperty ).as( EntityInstance.class );

      final Entity nestedEntity = nestedEntityInstance.getEntityType();
      assertThat( nestedEntityInstance.getAssertions().get( nestedEntity.getPropertyByName( "notInPayloadProperty" ).get() )
            .as( ScalarValue.class ).getValue()
            .toString() ).isEqualTo( "foo" );
   }

   @Test
   public void testEnumerationCharacteristicWithNestedEntityListAndNotInPayloadDataTypeExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_NESTED_ENTITY_LIST_ENUMERATION_WITH_NOT_IN_PAYLOAD );
      assertThat( aspect ).properties().hasSize( 1 );

      final Enumeration enumeration = aspect.getProperties().get( 0 ).getCharacteristic().get().as( Enumeration.class );
      assertThat( enumeration ).dataType().isEntity();

      final Entity dataType = enumeration.getDataType().get().as( Entity.class );
      assertThat( dataType ).properties().size().isEqualTo( 2 );

      final Property nestedEntityListProperty = dataType.getProperties().get( 1 );
      assertThat( nestedEntityListProperty ).isNotInPayload();
      assertThat( nestedEntityListProperty ).characteristic().isInstanceOf( DefaultSet.class );

      final EntityInstance entityInstance = enumeration.getValues().get( 0 ).as( EntityInstance.class );
      final Entity entity = entityInstance.getEntityType();
      final Property entityProperty = entity.getProperties().stream().filter( property -> property.getName().equals( "entityProperty" ) )
            .findAny().get();
      assertThat( entityInstance.getAssertions().get( entityProperty ).as( ScalarValue.class ).getValue() ).isEqualTo( "This is a test." );

      final CollectionValue nestedEntityListValue = entityInstance.getAssertions().get( nestedEntityListProperty )
            .as( CollectionValue.class );
      assertThat( nestedEntityListValue ).values().hasSize( 1 );

      final EntityInstance nestedEntityInstance = nestedEntityListValue.getValues().iterator().next().as( EntityInstance.class );
      final Entity nestedEntity = nestedEntityInstance.getEntityType();
      final Property nestedEntityProperty = nestedEntity.getProperties().stream().filter( property ->
            property.getName().equals( "notInPayloadProperty" ) ).findAny().get();
      assertThat( nestedEntityInstance.getAssertions().get( nestedEntityProperty ).as( ScalarValue.class ).getValue() ).isEqualTo( "foo" );
   }

   @Test
   public void testEnumerationWithNestedEntityWithLangStringExpectSuccess() {
      final Aspect aspect = loadAspect( TestAspect.ASPECT_WITH_ENTITY_ENUMERATION_AND_LANG_STRING );
      assertThat( aspect ).properties().hasSize( 1 );

      final Enumeration enumeration = aspect.getProperties().get( 0 ).getCharacteristic().get().as( Enumeration.class );
      final EntityInstance entityInstance = enumeration.getValues().get( 0 ).as( EntityInstance.class );
      final Property property = entityInstance.getEntityType().getPropertyByName( "entityProperty" ).get();
      final ScalarValue scalarValue = entityInstance.getAssertions().get( property ).as( ScalarValue.class );
      assertThat( scalarValue ).type().hasUrn( RDF.langString.getURI() );
      final LangString langString = (LangString) scalarValue.getValue();
      assertThat( langString.getValue() ).isEqualTo( "This is a test." );
      assertThat( langString.getLanguageTag() ).isEqualTo( Locale.ENGLISH );
   }
}
