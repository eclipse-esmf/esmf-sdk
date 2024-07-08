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

package org.eclipse.esmf.aspectmodel.java;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.java.metamodel.StaticMetaModelJavaGenerator;
import org.eclipse.esmf.aspectmodel.java.pojo.AspectModelJavaGenerator;
import org.eclipse.esmf.aspectmodel.java.types.Either;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.staticmetamodel.StaticContainerProperty;
import org.eclipse.esmf.staticmetamodel.StaticProperty;
import org.eclipse.esmf.staticmetamodel.StaticUnitProperty;
import org.eclipse.esmf.staticmetamodel.constraint.StaticConstraintContainerProperty;
import org.eclipse.esmf.staticmetamodel.constraint.StaticConstraintProperty;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;
import org.eclipse.esmf.test.TestSharedAspect;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

abstract class StaticMetaModelGeneratorTest {
   Collection<JavaGenerator> getGenerators( final TestAspect testAspect, final boolean executeLibraryMacros, final File templateLibFile ) {
      final AspectModel aspectModel = TestResources.load( testAspect );
      final Aspect aspect = aspectModel.aspect();
      final JavaCodeGenerationConfig config = JavaCodeGenerationConfigBuilder.builder()
            .enableJacksonAnnotations( false )
            .executeLibraryMacros( executeLibraryMacros )
            .templateLibFile( templateLibFile )
            .packageName( aspect.urn().getNamespace() )
            .build();
      final JavaGenerator pojoGenerator = new AspectModelJavaGenerator( aspect, config );
      final JavaGenerator staticGenerator = new StaticMetaModelJavaGenerator( aspect, config );
      return List.of( pojoGenerator, staticGenerator );
   }

   Collection<JavaGenerator> getGenerators( final TestAspect testAspect ) {
      final AspectModel aspectModel = TestResources.load( testAspect );
      final Aspect aspect = aspectModel.aspect();
      final JavaCodeGenerationConfig config = JavaCodeGenerationConfigBuilder.builder()
            .enableJacksonAnnotations( false )
            .executeLibraryMacros( false )
            .packageName( aspect.urn().getNamespace() )
            .build();
      final JavaGenerator pojoGenerator = new AspectModelJavaGenerator( aspect, config );
      final JavaGenerator staticGenerator = new StaticMetaModelJavaGenerator( aspect, config );
      return List.of( pojoGenerator, staticGenerator );
   }

   Collection<JavaGenerator> getGenerators( final TestSharedAspect testAspect ) {
      final AspectModel aspectModel = TestResources.load( testAspect );
      final Aspect aspect = aspectModel.aspect();
      final JavaCodeGenerationConfig config = JavaCodeGenerationConfigBuilder.builder()
            .enableJacksonAnnotations( false )
            .executeLibraryMacros( false )
            .packageName( aspect.urn().getNamespace() )
            .build();
      final JavaGenerator pojoGenerator = new AspectModelJavaGenerator( aspect, config );
      final JavaGenerator staticGenerator = new StaticMetaModelJavaGenerator( aspect, config );
      return List.of( pojoGenerator, staticGenerator );
   }

   /**
    * Static methods to conveniently generate property type tokens that can be used for test assertions.
    */
   static class TypeTokens {
      /**
       * Create a type token for a static property.
       *
       * @param containingClass the class that contains the property
       * @param propertyClass the class of the property
       * @param <C> type of the containing class
       * @param <T> type of the property
       * @return the newly created type token
       */
      static <C, T> TypeToken<StaticProperty<C, T>> staticProperty( final Class<C> containingClass, final Class<T> propertyClass ) {
         return new TypeToken<StaticProperty<C, T>>() {
         }
               .where( new TypeParameter</*C*/>() {}, containingClass )
               .where( new TypeParameter</*T*/>() {}, propertyClass );
      }

      /**
       * Create a type token for a static property.
       *
       * @param containingClass the class that contains the property
       * @param propertyTypeToken the class of the property, given as a TypeToken
       * @param <C> type of the containing class
       * @param <T> type of the property
       * @return the newly created type token
       */
      static <C, T> TypeToken<StaticProperty<C, T>> staticProperty( final Class<C> containingClass, final TypeToken<T> propertyTypeToken ) {
         return new TypeToken<StaticProperty<C, T>>() {
         }
               .where( new TypeParameter</*C*/>() {}, containingClass )
               .where( new TypeParameter</*T*/>() {}, propertyTypeToken );
      }

      /**
       * Create a type token for a static unit property.
       *
       * @param containingClass the class that contains the property
       * @param propertyClass the class of the property
       * @param <C> type of the containing class
       * @param <T> type of the property
       * @return the newly created type token
       */
      static <C, T> TypeToken<StaticUnitProperty<C, T>> staticUnitProperty( final Class<C> containingClass, final Class<T> propertyClass ) {
         return new TypeToken<StaticUnitProperty<C, T>>() {
         }
               .where( new TypeParameter</*C*/>() {}, containingClass )
               .where( new TypeParameter</*T*/>() {}, propertyClass );
      }

      /**
       * Create a type token for a static container property.
       *
       * @param containingClass the class that contains the property
       * @param containedClass the class of the value inside the container
       * @param propertyClass a type token for the property (i.e. the container class and the contained class)
       * @param <E> type of the containing class
       * @param <C> type of the value inside the container
       * @param <T> type of the property (i.e. the container type and the contained type)
       * @return the newly created type token
       */
      static <E, C, T> TypeToken<StaticContainerProperty<E, C, T>> staticContainerProperty( final Class<E> containingClass,
            final Class<C> containedClass, final TypeToken<T> propertyClass ) {
         return new TypeToken<StaticContainerProperty<E, C, T>>() {
         }
               .where( new TypeParameter</*E*/>() {}, containingClass )
               .where( new TypeParameter</*C*/>() {}, containedClass )
               .where( new TypeParameter</*T*/>() {}, propertyClass );
      }

      /**
       * Create a type token for a static constraint property.
       *
       * @param containingClass the class that contains the property
       * @param propertyClass the class of the property
       * @param constraintClass the class of the constraint
       * @param <E> type of the containing class
       * @param <T> type of the property
       * @param <C> type of the constraint
       * @return the newly created type token
       */
      static <E, T, C extends Characteristic> TypeToken<StaticConstraintProperty<E, T, C>> staticConstraintProperty(
            final Class<E> containingClass, final Class<T> propertyClass, final Class<C> constraintClass ) {
         return new TypeToken<StaticConstraintProperty<E, T, C>>() {
         }
               .where( new TypeParameter</*E*/>() {}, containingClass )
               .where( new TypeParameter</*T*/>() {}, propertyClass )
               .where( new TypeParameter</*C*/>() {}, constraintClass );
      }

      /**
       * Create a type token for a static constraint container property.
       *
       * @param containingClass the class that contains the property
       * @param containedClass the class of the value inside the container
       * @param propertyClass a type token for the property (i.e. the container class and the contained class)
       * @param constraintClass the class of the constraint
       * @param <E> type of the containing class
       * @param <R> type of the contained class
       * @param <T> type of the property
       * @param <C> type of the constraint
       * @return the newly created type token
       */
      static <E, R, T, C extends Characteristic> TypeToken<StaticConstraintContainerProperty<E, R, T, C>> staticConstraintContainerProperty(
            final Class<E> containingClass, final Class<R> containedClass, final TypeToken<T> propertyClass,
            final Class<C> constraintClass ) {
         return new TypeToken<StaticConstraintContainerProperty<E, R, T, C>>() {
         }
               .where( new TypeParameter</*E*/>() {}, containingClass )
               .where( new TypeParameter</*R*/>() {}, containedClass )
               .where( new TypeParameter</*T*/>() {}, propertyClass )
               .where( new TypeParameter</*C*/>() {}, constraintClass );
      }

      /**
       * Create a type token for an {@link Optional}.
       *
       * @param clazz the class of the element inside the {@code Optional}
       * @param <T> the type of the element inside the {@code Optional}
       * @return the newly created type token
       */
      static <T> TypeToken<Optional<T>> optional( final Class<T> clazz ) {
         return new TypeToken<Optional<T>>() {
         }
               .where( new TypeParameter</*T*/>() {}, clazz );
      }

      /**
       * Create a type token for a {@link Collection}.
       *
       * @param clazz the class of the element inside the {@code Collection}
       * @param <T> the type of the element inside the {@code Collection}
       * @return the newly created type token
       */
      static <T> TypeToken<Collection<T>> collection( final Class<T> clazz ) {
         return new TypeToken<Collection<T>>() {
         }
               .where( new TypeParameter</*T*/>() {}, clazz );
      }

      /**
       * Create a type token for a {@link List}.
       *
       * @param clazz the class of the element inside the {@code List}
       * @param <T> the type of the element inside the {@code List}
       * @return the newly created type token
       */
      static <T> TypeToken<List<T>> list( final Class<T> clazz ) {
         return new TypeToken<List<T>>() {
         }
               .where( new TypeParameter</*T*/>() {}, clazz );
      }

      /**
       * Create a type token for a {@link LinkedHashSet}.
       *
       * @param clazz the class of the element inside the {@code LinkedHashSet}
       * @param <T> the type of the element inside the {@code LinkedHashSet}
       * @return the newly created type token
       */
      static <T> TypeToken<LinkedHashSet<T>> linkedHashSet( final Class<T> clazz ) {
         return new TypeToken<LinkedHashSet<T>>() {
         }
               .where( new TypeParameter</*T*/>() {}, clazz );
      }

      /**
       * Create a type token for a {@link Either}.
       *
       * @param leftClass the class of the left element inside the {@code Either}
       * @param rightClass the class of the right element inside the {@code Either}
       * @param <L> the type of the left element inside the {@code Either}
       * @param <L> the type of the right element inside the {@code Either}
       * @return the newly created type token
       */
      static <L, R> TypeToken<Either<L, R>> either( final Class<L> leftClass, final Class<R> rightClass ) {
         return new TypeToken<Either<L, R>>() {
         }
               .where( new TypeParameter</*L*/>() {}, leftClass )
               .where( new TypeParameter</*R*/>() {}, rightClass );
      }
   }

   /**
    * Provides a builder for field assertions that already contains the standard assertions for each static metamodel test.
    *
    * @param className the static meta model class name
    * @return the pre-initialized builder
    */
   static ImmutableMap.Builder<String, Object> fieldAssertions( final String className ) {
      return ImmutableMap.<String, Object> builder()
            .put( "NAMESPACE", String.class )
            .put( "MODEL_ELEMENT_URN", String.class )
            .put( "CHARACTERISTIC_NAMESPACE", String.class )
            .put( "INSTANCE", className );
   }

   /**
    * Looks up a class from a code generation result using the default package {@code org.eclipse.esmf.test}.
    *
    * @param result the code generation result to look up the class from
    * @param className the class name
    * @return the class or {@code null} if it could not be found
    */
   static Class<?> findGeneratedClass( final GenerationResult result, final String className ) {
      return findGeneratedClass( result, "org.eclipse.esmf.test", className );
   }

   /**
    * Looks up a class from a code generation result using a given package and class name.
    *
    * @param result the code generation result to look up the class from
    * @param packageName the package name
    * @param className the class name
    * @return the class or {@code null} if it could not be found
    */
   static Class<?> findGeneratedClass( final GenerationResult result, final String packageName, final String className ) {
      return result.getGeneratedClass( new QualifiedName( className, packageName ) );
   }
}
