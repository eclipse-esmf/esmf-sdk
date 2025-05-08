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

package org.eclipse.esmf.nativefeatures;

import static org.graalvm.nativeimage.impl.ConfigurationCondition.alwaysTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.eclipse.esmf.substitution.IsLinux;
import org.eclipse.esmf.substitution.IsWindows;

import org.graalvm.nativeimage.ImageInfo;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.impl.ConfigurationCondition;
import org.graalvm.nativeimage.impl.RuntimeClassInitializationSupport;
import org.graalvm.nativeimage.impl.RuntimeJNIAccessSupport;
import org.graalvm.nativeimage.impl.RuntimeProxyCreationSupport;
import org.graalvm.nativeimage.impl.RuntimeReflectionSupport;
import org.graalvm.nativeimage.impl.RuntimeResourceSupport;

/**
 * Helper class for registration of resources, classes etc. for GraalVM native image compilation.
 */
public class Native {
   private final Class<?> clazz;
   private static final Supplier<RuntimeResourceSupport> RUNTIME_RESOURCE_SUPPORT;
   private static final Supplier<RuntimeClassInitializationSupport> RUNTIME_CLASS_INITIALIZATION_SUPPORT;
   private static final Supplier<RuntimeReflectionSupport> RUNTIME_REFLECTION_SUPPORT;
   private static final Supplier<RuntimeJNIAccessSupport> RUNTIME_JNI_ACCESS_SUPPORT;
   private static final Supplier<RuntimeProxyCreationSupport> RUNTIME_PROXY_CREATION_SUPPORT;

   static {
      /*
       If the class runs in regular Java context (i.e., outside native-image compilation) the native runtime access
       is replaced with dummy implementations. This turns all native setup calls into NOP calls instead of throwing
       exceptions, which allows testing class instantiation from the Feature classes.
      */
      if ( ImageInfo.inImageBuildtimeCode() ) {
         RUNTIME_RESOURCE_SUPPORT = () -> ImageSingletons.lookup( RuntimeResourceSupport.class );
         RUNTIME_CLASS_INITIALIZATION_SUPPORT = () -> ImageSingletons.lookup( RuntimeClassInitializationSupport.class );
         RUNTIME_REFLECTION_SUPPORT = () -> ImageSingletons.lookup( RuntimeReflectionSupport.class );
         RUNTIME_JNI_ACCESS_SUPPORT = () -> ImageSingletons.lookup( RuntimeJNIAccessSupport.class );
         RUNTIME_PROXY_CREATION_SUPPORT = () -> ImageSingletons.lookup( RuntimeProxyCreationSupport.class );
      } else {
         RUNTIME_RESOURCE_SUPPORT = DummyRuntimeResourceSupport::new;
         RUNTIME_CLASS_INITIALIZATION_SUPPORT = DummyRuntimeClassInitializationSupport::new;
         RUNTIME_REFLECTION_SUPPORT = DummyRuntimeReflectionSupport::new;
         RUNTIME_JNI_ACCESS_SUPPORT = DummyRuntimeJniAccessSupport::new;
         RUNTIME_PROXY_CREATION_SUPPORT = DummyRuntimeProxyCreationSupport::new;
      }
   }

   private Native( final Class<?> clazz ) {
      this.clazz = clazz;
   }

   public static Native forClass( final Class<?> clazz ) {
      return new Native( clazz );
   }

   public static Native forArrayClass( final String fullyQualifiedJavaClass ) {
      return forClass( "[L" + fullyQualifiedJavaClass + ";" );
   }

   public static Native forClass( final String fullyQualifiedJavaClass ) {
      final Class<?> clazz;
      try {
         clazz = Class.forName( fullyQualifiedJavaClass, false, Native.class.getClassLoader() );
      } catch ( final ClassNotFoundException exception ) {
         throw new RuntimeException( exception );
      }
      return forClass( clazz );
   }

   public Native initializeAtBuildTime() {
      final StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
      final String unqualifiedName = clazz.getTypeName().substring( clazz.getTypeName().lastIndexOf( '.' ) + 1 );
      final String caller = stacktrace[1].getClassName() + "." + stacktrace[1].getMethodName();
      final String reason = "from feature %s with '%s.class'".formatted( caller, unqualifiedName );
      RUNTIME_CLASS_INITIALIZATION_SUPPORT.get().initializeAtBuildTime( clazz, reason );
      return this;
   }

   public Native registerEverythingForReflection() {
      return registerClassForReflection()
            .registerAllFieldsForReflection()
            .registerAllConstructorsForReflection()
            .registerAllMethodsForReflection();
   }

   public Native registerClassForReflection() {
      RUNTIME_REFLECTION_SUPPORT.get().register( alwaysTrue(), clazz );
      return this;
   }

   public Native registerAllFieldsForReflection() {
      RUNTIME_REFLECTION_SUPPORT.get().register( alwaysTrue(), false, clazz.getDeclaredFields() );
      return this;
   }

   public Native registerFieldsForReflection( @Nonnull final String... fieldNames ) {
      for ( final Field field : clazz.getDeclaredFields() ) {
         for ( final String targetField : fieldNames ) {
            if ( field.getName().equals( targetField ) ) {
               RUNTIME_REFLECTION_SUPPORT.get().register( alwaysTrue(), false, field );
            }
         }
      }
      return this;
   }

   public Native registerAllConstructorsForReflection() {
      RUNTIME_REFLECTION_SUPPORT.get().register( alwaysTrue(), false, clazz.getDeclaredConstructors() );
      return this;
   }

   public Native registerConstructorForReflection( @Nonnull final Class<?>... args ) {
      try {
         RUNTIME_REFLECTION_SUPPORT.get().register( alwaysTrue(), false, clazz.getDeclaredConstructor( args ) );
      } catch ( final NoSuchMethodException exception ) {
         throw new RuntimeException( "Could not find constructor in " + clazz + " with args "
               + Arrays.stream( args ).map( Class::toString ).collect( Collectors.joining( ",", "[", "]" ) ) );
      }
      return this;
   }

   public Native registerAllMethodsForReflection() {
      RUNTIME_REFLECTION_SUPPORT.get().register( alwaysTrue(), false, clazz.getDeclaredMethods() );
      return this;
   }

   public Native registerMethodForReflection( final String name, @Nonnull final Class<?>... args ) {
      try {
         RUNTIME_REFLECTION_SUPPORT.get().register( alwaysTrue(), false, clazz.getDeclaredMethod( name, args ) );
      } catch ( final NoSuchMethodException exception ) {
         throw new RuntimeException( "Could not find method " + name + " in " + clazz + " with args "
               + Arrays.stream( args ).map( Class::toString ).collect( Collectors.joining( ",", "[", "]" ) ) );
      }
      return this;
   }

   public Native registerEverythingForJni() {
      return registerClassForJni()
            .registerAllFieldsForJni()
            .registerAllConstructorsForJni()
            .registerAllMethodsForJni();
   }

   public Native registerClassForJni() {
      RUNTIME_JNI_ACCESS_SUPPORT.get().register( alwaysTrue(), clazz );
      return this;
   }

   public Native registerAllFieldsForJni() {
      for ( final Field field : clazz.getDeclaredFields() ) {
         RUNTIME_JNI_ACCESS_SUPPORT.get().register( alwaysTrue(), false, field );
      }
      return this;
   }

   public Native registerFieldsForJni( @Nonnull final String... fieldNames ) {
      for ( final Field field : clazz.getDeclaredFields() ) {
         for ( final String targetField : fieldNames ) {
            if ( field.getName().equals( targetField ) ) {
               RUNTIME_JNI_ACCESS_SUPPORT.get().register( alwaysTrue(), false, field );
            }
         }
      }
      return this;
   }

   public Native registerAllConstructorsForJni() {
      for ( final Constructor<?> constructor : clazz.getDeclaredConstructors() ) {
         RUNTIME_JNI_ACCESS_SUPPORT.get().register( alwaysTrue(), false, constructor );
      }
      return this;
   }

   public Native registerConstructorForJni( @Nonnull final Class<?>... args ) {
      try {
         RUNTIME_JNI_ACCESS_SUPPORT.get().register( alwaysTrue(), false, clazz.getDeclaredConstructor( args ) );
      } catch ( final NoSuchMethodException exception ) {
         throw new RuntimeException( "Could not find constructor in " + clazz + " with args "
               + Arrays.stream( args ).map( Class::toString ).collect( Collectors.joining( ",", "[", "]" ) ) );
      }
      return this;
   }

   public Native registerAllMethodsForJni() {
      for ( final Method method : clazz.getDeclaredMethods() ) {
         RUNTIME_JNI_ACCESS_SUPPORT.get().register( alwaysTrue(), false, method );
      }
      return this;
   }

   public Native registerMethodForJni( final String name, @Nonnull final Class<?>... args ) {
      try {
         RUNTIME_JNI_ACCESS_SUPPORT.get().register( alwaysTrue(), false, clazz.getDeclaredMethod( name, args ) );
      } catch ( final NoSuchMethodException exception ) {
         throw new RuntimeException( "Could not find method " + name + " in " + clazz + " with args "
               + Arrays.stream( args ).map( Class::toString ).collect( Collectors.joining( ",", "[", "]" ) ) );
      }
      return this;
   }

   public Native registerProxyCreationSupport( final Class<?>... classes ) {
      RUNTIME_PROXY_CREATION_SUPPORT.get().addProxyClass( classes );
      return this;
   }

   public static void addResource( final String resource ) {
      RUNTIME_RESOURCE_SUPPORT.get().addResources( alwaysTrue(), "\\Q" + resource + "\\E" );
   }

   public static void addResourcesPattern( final String pattern ) {
      RUNTIME_RESOURCE_SUPPORT.get().addResources( alwaysTrue(), pattern );
   }

   public static void addResourceBundle( final String name ) {
      RUNTIME_RESOURCE_SUPPORT.get().addResourceBundles( alwaysTrue(), name );
   }

   /**
    * @deprecated Use {@link #addResourceBundle(String)} instead
    */
   @Deprecated( forRemoval = true )
   public static void addClassBasedResourceBundle( final String name, final String className ) {
      RUNTIME_RESOURCE_SUPPORT.get().addResourceBundles( alwaysTrue(), name );
   }

   /**
    * Returns the class for the given class name.
    *
    * @param className the class name
    * @return the class
    */
   public static Class<?> clazz( final String className ) {
      try {
         return Class.forName( className );
      } catch ( final ClassNotFoundException exception ) {
         throw new RuntimeException( "Could not access for reflection registry", exception );
      }
   }

   protected static boolean isLinux() {
      return new IsLinux().getAsBoolean();
   }

   protected static boolean isWindows() {
      return new IsWindows().getAsBoolean();
   }

   private static class DummyRuntimeResourceSupport<C> implements RuntimeResourceSupport {
      @Override
      public void addResources( final ConfigurationCondition condition, final String pattern ) {
         // nothing
      }

      @Override
      public void injectResource( final Module module, final String resourcePath, final byte[] resourceContent ) {
         // nothing
      }

      @Override
      public void ignoreResources( final ConfigurationCondition condition, final String pattern ) {
         // nothing
      }

      @Override
      public void addResourceBundles( final ConfigurationCondition condition, final String name ) {
         // nothing
      }

      @Override
      public void addResourceBundles( final ConfigurationCondition condition, final String basename, final Collection<Locale> locales ) {
         // nothing
      }
   }

   private static class DummyRuntimeClassInitializationSupport implements RuntimeClassInitializationSupport {
      @Override
      public void initializeAtRunTime( final String name, final String reason ) {
         // nothing
      }

      @Override
      public void initializeAtBuildTime( final String name, final String reason ) {
         // nothing
      }

      @Override
      public void rerunInitialization( final String name, final String reason ) {
         // nothing
      }

      @Override
      public void initializeAtRunTime( final Class<?> clazz, final String reason ) {
         // nothing
      }

      @Override
      public void rerunInitialization( final Class<?> clazz, final String reason ) {
         // nothing
      }

      @Override
      public void initializeAtBuildTime( final Class<?> clazz, final String reason ) {
         // nothing
      }
   }

   private static class DummyRuntimeJniAccessSupport implements RuntimeJNIAccessSupport {
      @Override
      public void register( final ConfigurationCondition condition, final boolean unsafeAllocated, final Class<?> clazz ) {
         // nothing
      }

      @Override
      public void register( final ConfigurationCondition condition, final boolean queriedOnly, final Executable... methods ) {
         // nothing
      }

      @Override
      public void register( final ConfigurationCondition condition, final boolean finalIsWritable, final Field... fields ) {
         // nothing
      }
   }

   private static class DummyRuntimeReflectionSupport implements RuntimeReflectionSupport {
      @Override
      public void registerAllMethodsQuery( final ConfigurationCondition condition, final boolean queriedOnly, final Class<?> clazz ) {
         // nothing
      }

      @Override
      public void registerAllDeclaredMethodsQuery( final ConfigurationCondition condition, final boolean queriedOnly,
            final Class<?> clazz ) {
         // nothing
      }

      @Override
      public void registerAllFieldsQuery( final ConfigurationCondition condition, final Class<?> clazz ) {
         //nothing
      }

      @Override
      public void registerAllDeclaredFieldsQuery( final ConfigurationCondition condition, final Class<?> clazz ) {
         //nothing
      }

      @Override
      public void registerAllConstructorsQuery( final ConfigurationCondition condition, final boolean queriedOnly, final Class<?> clazz ) {
         //nothing
      }

      @Override
      public void registerAllDeclaredConstructorsQuery( final ConfigurationCondition condition, final boolean queriedOnly,
            final Class<?> clazz ) {
         //nothing
      }

      @Override
      public void registerAllClassesQuery( final ConfigurationCondition condition, final Class<?> clazz ) {
         //nothing
      }

      @Override
      public void registerAllDeclaredClassesQuery( final ConfigurationCondition condition, final Class<?> clazz ) {
         //nothing
      }

      @Override
      public void registerAllRecordComponentsQuery( final ConfigurationCondition condition, final Class<?> clazz ) {
         //nothing
      }

      @Override
      public void registerAllPermittedSubclassesQuery( final ConfigurationCondition condition, final Class<?> clazz ) {
         //nothing
      }

      @Override
      public void registerAllNestMembersQuery( final ConfigurationCondition condition, final Class<?> clazz ) {
         //nothing
      }

      @Override
      public void registerAllSignersQuery( final ConfigurationCondition condition, final Class<?> clazz ) {
         //nothing
      }

      @Override
      public void registerClassLookupException( final ConfigurationCondition condition, final String typeName, final Throwable t ) {
         //nothing
      }

      @Override
      public void registerClassLookup( final ConfigurationCondition condition, final String typeName ) {
         //nothing
      }

      @Override
      public void registerFieldLookup( final ConfigurationCondition condition, final Class<?> declaringClass, final String fieldName ) {
         //nothing
      }

      @Override
      public void registerMethodLookup( final ConfigurationCondition condition, final Class<?> declaringClass, final String methodName,
            final Class<?>... parameterTypes ) {
         //nothing
      }

      @Override
      public void registerConstructorLookup( final ConfigurationCondition condition, final Class<?> declaringClass,
            final Class<?>... parameterTypes ) {
         //nothing
      }

      @Override
      public void register( final ConfigurationCondition condition, final boolean unsafeAllocated, final Class<?> clazz ) {
         //nothing
      }

      @Override
      public void register( final ConfigurationCondition condition, final boolean queriedOnly, final Executable... methods ) {
         //nothing
      }

      @Override
      public void register( final ConfigurationCondition condition, final boolean finalIsWritable, final Field... fields ) {
         //nothing
      }
   }

   private static class DummyRuntimeProxyCreationSupport implements RuntimeProxyCreationSupport {
      @Override
      public void addProxyClass( final Class<?>... interfaces ) {
         //nothing
      }
   }
}
