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

package org.eclipse.esmf.aspectmodel.java;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.staticmetamodel.ComputedProperty;
import org.eclipse.esmf.staticmetamodel.StaticContainerProperty;
import org.eclipse.esmf.staticmetamodel.StaticProperty;
import org.eclipse.esmf.staticmetamodel.predicate.PropertyPredicates;
import org.eclipse.esmf.staticmetamodel.propertychain.ContainerPropertyChain;
import org.eclipse.esmf.staticmetamodel.propertychain.PropertyChain;
import org.eclipse.esmf.test.TestAspect;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ExtendedStaticMetaModelFunctionalityTest extends StaticMetaModelGeneratorTest {
   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testComputedProperties( final KnownVersion metaModelVersion ) throws IOException, ReflectiveOperationException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_EXTENDED_ENUMS;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );

      final Class<?> aspectClass = findGeneratedClass( result, "AspectWithExtendedEnums" );
      final Class<?> metaAspectClass = findGeneratedClass( result, "MetaAspectWithExtendedEnums" );
      final Class<? extends Enum> evaluationResultsClass = (Class<? extends Enum>) findGeneratedClass( result, "EvaluationResults" );
      final Class<? extends Enum> yesNoClass = (Class<? extends Enum>) findGeneratedClass( result, "YesNo" );

      final StaticProperty<Object, Object> resultProperty = (StaticProperty<Object, Object>) metaAspectClass.getField( "RESULT" )
            .get( null );
      final Method getValueOfEvaluationResults = evaluationResultsClass.getDeclaredMethod( "getValue" );
      final Object resultGoodEnumValue = Enum.valueOf( evaluationResultsClass, "RESULT_GOOD" );
      final Object resultGoodValue = getValueOfEvaluationResults.invoke( resultGoodEnumValue );
      final Object yesEnumValue = Enum.valueOf( yesNoClass, "YES" );

      final Object aspectInstance = ConstructorUtils.invokeConstructor( aspectClass, resultGoodEnumValue, yesEnumValue );

      final ComputedProperty<Object, Object> unwrapEnumValue = ComputedProperty.of( resultProperty, results -> {
         try {
            return getValueOfEvaluationResults.invoke( results );
         } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
         } catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
         }
      } );

      assertThat( unwrapEnumValue.getValue( aspectInstance ) ).isEqualTo( resultGoodValue );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testPropertyChain( final KnownVersion metaModelVersion ) throws IOException, ReflectiveOperationException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_NESTED_ENTITY;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );

      final Class<?> aspectClass = findGeneratedClass( result, "AspectWithNestedEntity" );
      final Class<?> entityClass = findGeneratedClass( result, "Entity" );
      final Class<?> nestedEntityClass = findGeneratedClass( result, "NestedTestEntity" );
      final Class<?> metaAspectClass = findGeneratedClass( result, "MetaAspectWithNestedEntity" );
      final Class<?> metaEntity = findGeneratedClass( result, "MetaEntity" );
      final Class<?> metaNestedEntity = findGeneratedClass( result, "MetaNestedTestEntity" );

      final StaticProperty<Object, Object> entityProperty =
            (StaticProperty<Object, Object>) metaAspectClass.getField( "ENTITY" ).get( null );

      final StaticProperty<Object, Object> entityStringProperty =
            (StaticProperty<Object, Object>) metaEntity.getField( "TEST_STRING" ).get( null );
      final StaticProperty<Object, Object> entityNestedEntityProperty =
            (StaticProperty<Object, Object>) metaEntity.getField( "NESTED_ENTITY" ).get( null );

      final StaticProperty<Object, Object> nestedEntityStringProperty =
            (StaticProperty<Object, Object>) metaNestedEntity.getField( "TEST_STRING" ).get( null );

      final PropertyChain<Object, Object> entityStringChain = PropertyChain.from( entityProperty ).to( entityStringProperty );
      final PropertyChain<Object, Object> nestedEntityStringChain = PropertyChain.from( entityProperty )
            .via( entityNestedEntityProperty )
            .to( nestedEntityStringProperty );

      final Object nestedEntityInstance = ConstructorUtils.invokeConstructor( nestedEntityClass, "nested-entity-string" );
      final Object entityInstance = ConstructorUtils.invokeConstructor( entityClass, nestedEntityInstance, "entity-string" );
      final Object aspectInstance = ConstructorUtils.invokeConstructor( aspectClass, entityInstance );

      assertThat( entityStringChain.getProperties() ).hasSize( 2 );
      assertThat( entityStringChain.getFirstProperty() ).isEqualTo( entityProperty );
      assertThat( entityStringChain.getLastProperty() ).isEqualTo( entityStringProperty );
      assertThat( entityStringChain.getPropertyType() ).isEqualTo( String.class );
      assertThat( entityStringChain.getContainingType() ).isEqualTo( aspectClass );
      assertThat( entityStringChain.getValue( aspectInstance ) ).isEqualTo( "entity-string" );

      assertThat( nestedEntityStringChain.getProperties() ).hasSize( 3 );
      assertThat( nestedEntityStringChain.getFirstProperty() ).isEqualTo( entityProperty );
      assertThat( nestedEntityStringChain.getLastProperty() ).isEqualTo( nestedEntityStringProperty );
      assertThat( nestedEntityStringChain.getPropertyType() ).isEqualTo( String.class );
      assertThat( nestedEntityStringChain.getContainingType() ).isEqualTo( aspectClass );
      assertThat( nestedEntityStringChain.getValue( aspectInstance ) ).isEqualTo( "nested-entity-string" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testCollectionPropertyChain( final KnownVersion metaModelVersion ) throws IOException, ReflectiveOperationException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_ENTITY_WITH_NESTED_ENTITY_LIST_PROPERTY;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );

      final Class<?> aspectClass = findGeneratedClass( result, "AspectWithEntityWithNestedEntityListProperty" );
      final Class<?> entityClass = findGeneratedClass( result, "Entity" );
      final Class<?> nestedEntityClass = findGeneratedClass( result, "NestedEntity" );
      final Class<?> metaAspectClass = findGeneratedClass( result, "MetaAspectWithEntityWithNestedEntityListProperty" );
      final Class<?> metaEntity = findGeneratedClass( result, "MetaEntity" );
      final Class<?> metaNestedEntity = findGeneratedClass( result, "MetaNestedEntity" );

      final StaticProperty<Object, Object> entityProperty =
            (StaticProperty<Object, Object>) metaAspectClass.getField( "TEST_PROPERTY" ).get( null );

      final StaticContainerProperty<Object, Object, Object> entityNestedListProperty =
            (StaticContainerProperty<Object, Object, Object>) metaEntity.getField( "TEST_LIST" ).get( null );

      final StaticProperty<Object, Object> nestedEntityStringProperty =
            (StaticProperty<Object, Object>) metaNestedEntity.getField( "NESTED_ENTITY_PROPERTY" ).get( null );

      final ContainerPropertyChain<Object, List<Object>, Object> entityStringCollectionChain = PropertyChain.from( entityProperty )
            .viaCollection( entityNestedListProperty )
            .to( nestedEntityStringProperty );

      final Object nestedEntityInstance1 = ConstructorUtils.invokeConstructor( nestedEntityClass, "nested-entity-string-1" );
      final Object nestedEntityInstance2 = ConstructorUtils.invokeConstructor( nestedEntityClass, "nested-entity-string-2" );
      final Object entityInstance = ConstructorUtils.invokeConstructor( entityClass, Short.valueOf( (short) 123 ),
            List.of( nestedEntityInstance1, nestedEntityInstance2 ) );
      final Object aspectInstance = ConstructorUtils.invokeConstructor( aspectClass, entityInstance );

      assertThat( entityStringCollectionChain.getProperties() ).hasSize( 3 );
      assertThat( entityStringCollectionChain.getFirstProperty() ).isEqualTo( entityProperty );
      assertThat( entityStringCollectionChain.getLastProperty() ).isEqualTo( nestedEntityStringProperty );
      assertThat( entityStringCollectionChain.getPropertyType() ).isEqualTo( List.class );
      assertThat( entityStringCollectionChain.getContainingType() ).isEqualTo( aspectClass );
      assertThat( entityStringCollectionChain.getValue( aspectInstance ) ).containsExactlyInAnyOrder( "nested-entity-string-1",
            "nested-entity-string-2" );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testSinglePropertyPredicates( final KnownVersion metaModelVersion ) throws IOException, ReflectiveOperationException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_NESTED_ENTITY_LIST;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );

      final Class<?> aspectClass = findGeneratedClass( result, "AspectWithNestedEntityList" );
      final Class<?> entityClass = findGeneratedClass( result, "TestFirstEntity" );
      final Class<?> nestedEntityClass = findGeneratedClass( result, "TestSecondEntity" );
      final Class<?> metaAspectClass = findGeneratedClass( result, "MetaAspectWithNestedEntityList" );
      final Class<?> metaEntity = findGeneratedClass( result, "MetaTestFirstEntity" );
      final Class<?> metaNestedEntity = findGeneratedClass( result, "MetaTestSecondEntity" );

      final Object ne1 = ConstructorUtils.invokeConstructor( nestedEntityClass, null, "nested-string1" );
      final Object ne2 = ConstructorUtils.invokeConstructor( nestedEntityClass, null, "nested-string2" );

      final Object e1 = ConstructorUtils.invokeConstructor( entityClass, "string1", 1, 1.0f, List.of( ne1, ne2 ) );
      final Object e2 = ConstructorUtils.invokeConstructor( entityClass, "string2", 2, 2.0f, List.of( ne2 ) );
      final Object e3 = ConstructorUtils.invokeConstructor( entityClass, "string3", 3, 3.0f, List.of() );

      final StaticProperty<Object, String> stringProperty =
            (StaticProperty<Object, String>) metaEntity.getField( "TEST_STRING" ).get( null );
      final StaticProperty<Object, Integer> intProperty =
            (StaticProperty<Object, Integer>) metaEntity.getField( "TEST_INT" ).get( null );
      final var entities = List.of( e1, e2, e3 );

      final var matchSingleString = PropertyPredicates.on( stringProperty ).isEqualTo( "string1" );
      assertThat( entities.stream().filter( matchSingleString ) ).containsExactly( e1 );
      assertThat( entities.stream().filter( matchSingleString.negate() ) ).containsExactlyInAnyOrder( e2, e3 );

      final var matchSubString = PropertyPredicates.matchOn( stringProperty ).contains( "ing1" );
      assertThat( entities.stream().filter( matchSubString ) ).containsExactly( e1 );
      assertThat( entities.stream().filter( matchSubString.negate() ) ).containsExactlyInAnyOrder( e2, e3 );

      final var matchRegex = PropertyPredicates.matchOn( stringProperty ).matches( ".*[12]" );
      assertThat( entities.stream().filter( matchRegex ) ).containsExactlyInAnyOrder( e1, e2 );
      assertThat( entities.stream().filter( matchRegex.negate() ) ).containsExactly( e3 );

      assertThat( entities.stream().filter( PropertyPredicates.compareOn( intProperty ).greaterThan( 1 ) ) )
            .containsExactlyInAnyOrder( e2, e3 );
      assertThat( entities.stream().filter( PropertyPredicates.compareOn( intProperty ).atLeast( 1 ) ) )
            .containsExactlyInAnyOrder( e1, e2, e3 );
      assertThat( entities.stream().filter( PropertyPredicates.compareOn( intProperty ).lessThan( 3 ) ) )
            .containsExactlyInAnyOrder( e1, e2 );
      assertThat( entities.stream().filter( PropertyPredicates.compareOn( intProperty ).atMost( 3 ) ) )
            .containsExactlyInAnyOrder( e1, e2, e3 );

      assertThat( entities.stream().filter( PropertyPredicates.compareOn( intProperty ).withinClosed( 1, 3 ) ) )
            .containsExactlyInAnyOrder( e1, e2, e3 );
      assertThat( entities.stream().filter( PropertyPredicates.compareOn( intProperty ).withinClosedOpen( 1, 3 ) ) )
            .containsExactlyInAnyOrder( e1, e2 );
      assertThat( entities.stream().filter( PropertyPredicates.compareOn( intProperty ).withinOpen( 1, 3 ) ) )
            .containsExactly( e2 );
      assertThat( entities.stream().filter( PropertyPredicates.compareOn( intProperty ).withinOpenClosed( 1, 3 ) ) )
            .containsExactlyInAnyOrder( e2, e3 );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   void testCollectionPropertyPredicates( final KnownVersion metaModelVersion ) throws IOException, ReflectiveOperationException {
      final TestAspect aspect = TestAspect.ASPECT_WITH_NESTED_ENTITY_LIST;
      final StaticClassGenerationResult result = TestContext.generateStaticAspectCode().apply( getGenerators( aspect, metaModelVersion ) );

      final Class<?> aspectClass = findGeneratedClass( result, "AspectWithNestedEntityList" );
      final Class<?> entityClass = findGeneratedClass( result, "TestFirstEntity" );
      final Class<?> nestedEntityClass = findGeneratedClass( result, "TestSecondEntity" );
      final Class<?> metaAspectClass = findGeneratedClass( result, "MetaAspectWithNestedEntityList" );
      final Class<?> metaEntity = findGeneratedClass( result, "MetaTestFirstEntity" );
      final Class<?> metaNestedEntity = findGeneratedClass( result, "MetaTestSecondEntity" );

      final Object ne1 = ConstructorUtils.invokeConstructor( nestedEntityClass, null, "nested-string1" );
      final Object ne2 = ConstructorUtils.invokeConstructor( nestedEntityClass, null, "nested-string2" );

      final Object e1 = ConstructorUtils.invokeConstructor( entityClass, "string1", 1, 1.0f, List.of( ne1, ne2 ) );
      final Object e2 = ConstructorUtils.invokeConstructor( entityClass, "string2", 2, 2.0f, List.of( ne2 ) );
      final Object e3 = ConstructorUtils.invokeConstructor( entityClass, "string3", 3, 3.0f, List.of() );

      final var entities = List.of( e1, e2, e3 );

      final StaticContainerProperty entityProperty =
            (StaticContainerProperty) metaEntity.getField( "TEST_SECOND_LIST" ).get( null );
      final StaticProperty<Object, String> nestedStringProperty =
            (StaticProperty<Object, String>) metaNestedEntity.getField( "RANDOM_VALUE" ).get( null );
      final ContainerPropertyChain entityStringCollectionChain =
            PropertyChain.fromCollection( entityProperty ).to( nestedStringProperty );
      final var matchNestedEntityString1 = PropertyPredicates.onCollection( entityStringCollectionChain ).contains( "nested-string1" );
      final var matchNestedEntityString2 = PropertyPredicates.onCollection( entityStringCollectionChain ).contains( "nested-string2" );
      assertThat( entities.stream().filter( matchNestedEntityString1 ) ).containsExactly( e1 );
      assertThat( entities.stream().filter( matchNestedEntityString2 ) ).containsExactly( e1, e2 );

      final var matchNestedEntityStringAny = PropertyPredicates.onCollection( entityStringCollectionChain )
            .containsAnyOf( List.of( "nested-string1", "nested-string2" ) );
      final var matchNestedEntityStringAll = PropertyPredicates.onCollection( entityStringCollectionChain )
            .containsAllOf( List.of( "nested-string1", "nested-string2" ) );
      assertThat( entities.stream().filter( matchNestedEntityStringAny ) ).containsExactlyInAnyOrder( e1, e2 );
      assertThat( entities.stream().filter( matchNestedEntityStringAll ) ).containsExactly( e1 );
   }
}
