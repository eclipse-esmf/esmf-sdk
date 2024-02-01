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

import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.staticmetamodel.ComputedProperty;
import org.eclipse.esmf.staticmetamodel.PropertyChain;
import org.eclipse.esmf.staticmetamodel.StaticProperty;
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
}
