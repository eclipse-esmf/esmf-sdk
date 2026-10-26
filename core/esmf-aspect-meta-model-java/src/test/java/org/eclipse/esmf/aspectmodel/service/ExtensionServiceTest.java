/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.aspectmodel.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.ServiceConfigurationError;

import org.eclipse.esmf.annotations.InterfaceVersion;
import org.eclipse.esmf.annotations.RequiredInterfaceVersion;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ExtensionServiceTest {
   @InterfaceVersion( version = 1 )
   private interface FakeExtensionPoint {
   }

   private interface UnversionedExtensionPoint {
   }

   @RequiredInterfaceVersion( version = 1 )
   private static class CompatibleProvider implements FakeExtensionPoint {
   }

   @RequiredInterfaceVersion( version = 2 )
   private static class IncompatibleProvider implements FakeExtensionPoint {
   }

   private static class UnannotatedProvider implements FakeExtensionPoint {
   }

   /**
    * Invokes a private static method of {@link ExtensionService} via reflection,
    * unwrapping any
    * exception thrown by the method itself so that tests can assert on it directly instead of on the
    * {@link InvocationTargetException} wrapper.
    */
   private static Object invokeStatic( final String methodName, final Class<?>[] parameterTypes, final Object... args ) throws Exception {
      final Method method = ExtensionService.class.getDeclaredMethod( methodName, parameterTypes );
      method.setAccessible( true );
      try {
         return method.invoke( null, args );
      } catch ( final InvocationTargetException exception ) {
         final Throwable cause = exception.getCause();
         if ( cause instanceof RuntimeException runtimeException ) {
            throw runtimeException;
         }
         if ( cause instanceof Error error ) {
            throw error;
         }
         throw exception;
      }
   }

   @Test
   void testCurrentInterfaceVersionReturnsAnnotatedVersion() throws Exception {
      final int version = (int) invokeStatic( "currentInterfaceVersion", new Class<?>[] { Class.class }, FakeExtensionPoint.class );

      assertThat( version ).isEqualTo( 1 );
   }

   @Test
   void testCurrentInterfaceVersionThrowsWhenAnnotationMissing() {
      assertThatThrownBy(
            () -> invokeStatic( "currentInterfaceVersion", new Class<?>[] { Class.class }, UnversionedExtensionPoint.class ) )
                  .isInstanceOf( IllegalStateException.class )
                  .hasMessageContaining( "@InterfaceVersion" );
   }

   @Test
   void testIsCompatibleReturnsTrueWhenVersionsMatch() throws Exception {
      final boolean compatible = (boolean) invokeStatic( "isCompatible", new Class<?>[] { Class.class, int.class },
            CompatibleProvider.class, 1 );

      assertThat( compatible ).isTrue();
   }

   @Test
   void testIsCompatibleReturnsFalseWhenVersionsMismatch() throws Exception {
      final boolean compatible = (boolean) invokeStatic( "isCompatible", new Class<?>[] { Class.class, int.class },
            IncompatibleProvider.class, 1 );

      assertThat( compatible ).isFalse();
   }

   @Test
   void testIsCompatibleReturnsFalseWhenRequiredVersionAnnotationMissing() throws Exception {
      final boolean compatible = (boolean) invokeStatic( "isCompatible", new Class<?>[] { Class.class, int.class },
            UnannotatedProvider.class, 1 );

      assertThat( compatible ).isFalse();
   }

   @Test
   void testAddIfCompatibleAddsCompatibleProvider() throws Exception {
      final List<Object> providers = new ArrayList<>();

      invokeStatic( "addIfCompatible", new Class<?>[] { Object.class, int.class, List.class }, new CompatibleProvider(), 1, providers );

      assertThat( providers ).hasSize( 1 );
      assertThat( providers.get( 0 ) ).isInstanceOf( CompatibleProvider.class );
   }

   @Test
   void testAddIfCompatibleSkipsIncompatibleProvider() throws Exception {
      final List<Object> providers = new ArrayList<>();

      invokeStatic( "addIfCompatible", new Class<?>[] { Object.class, int.class, List.class }, new IncompatibleProvider(), 1, providers );

      assertThat( providers ).isEmpty();
   }

   @Test
   void testAddIfCompatibleSkipsProviderMissingRequiredVersionAnnotation() throws Exception {
      final List<Object> providers = new ArrayList<>();

      invokeStatic( "addIfCompatible", new Class<?>[] { Object.class, int.class, List.class }, new UnannotatedProvider(), 1, providers );

      assertThat( providers ).isEmpty();
   }

   @Test
   void testHasNextSafelyReturnsTrueWhenIteratorHasNext() throws Exception {
      final Iterator<String> iterator = List.of( "value" ).iterator();

      final boolean result = (boolean) invokeStatic( "hasNextSafely", new Class<?>[] { Iterator.class, Class.class }, iterator,
            String.class );

      assertThat( result ).isTrue();
   }

   @Test
   void testHasNextSafelyReturnsFalseWhenIteratorIsExhausted() throws Exception {
      final Iterator<String> iterator = List.<String>of().iterator();

      final boolean result = (boolean) invokeStatic( "hasNextSafely", new Class<?>[] { Iterator.class, Class.class }, iterator,
            String.class );

      assertThat( result ).isFalse();
   }

   @Test
   void testHasNextSafelyReturnsFalseWhenIteratorThrowsServiceConfigurationError() throws Exception {
      final Iterator<String> iterator = new Iterator<>() {
         @Override
         public boolean hasNext() {
            throw new ServiceConfigurationError( "broken provider configuration" );
         }

         @Override
         public String next() {
            throw new NoSuchElementException();
         }
      };

      final boolean result = (boolean) invokeStatic( "hasNextSafely", new Class<?>[] { Iterator.class, Class.class }, iterator,
            String.class );

      assertThat( result ).isFalse();
   }

   @Test
   void testHasNextSafelyReturnsFalseWhenIteratorThrowsLinkageError() throws Exception {
      final Iterator<String> iterator = new Iterator<>() {
         @Override
         public boolean hasNext() {
            throw new NoClassDefFoundError( "incompatible bytecode" );
         }

         @Override
         public String next() {
            throw new NoSuchElementException();
         }
      };

      final boolean result = (boolean) invokeStatic( "hasNextSafely", new Class<?>[] { Iterator.class, Class.class }, iterator,
            String.class );

      assertThat( result ).isFalse();
   }

   @Test
   void testNextSafelyReturnsProviderWhenPresent() throws Exception {
      final Iterator<String> iterator = List.of( "value" ).iterator();

      @SuppressWarnings( "unchecked" )
      final Optional<String> result = (Optional<String>) invokeStatic( "nextSafely", new Class<?>[] { Iterator.class, Class.class },
            iterator, String.class );

      assertThat( result ).contains( "value" );
   }

   @Test
   void testNextSafelyReturnsEmptyWhenIteratorThrowsServiceConfigurationError() throws Exception {
      final Iterator<String> iterator = new Iterator<>() {
         @Override
         public boolean hasNext() {
            return true;
         }

         @Override
         public String next() {
            throw new ServiceConfigurationError( "provider could not be instantiated" );
         }
      };

      @SuppressWarnings( "unchecked" )
      final Optional<String> result = (Optional<String>) invokeStatic( "nextSafely", new Class<?>[] { Iterator.class, Class.class },
            iterator, String.class );

      assertThat( result ).isEmpty();
   }

   @Test
   void testNextSafelyReturnsEmptyWhenIteratorThrowsLinkageError() throws Exception {
      final Iterator<String> iterator = new Iterator<>() {
         @Override
         public boolean hasNext() {
            return true;
         }

         @Override
         public String next() {
            throw new NoClassDefFoundError( "incompatible bytecode" );
         }
      };

      @SuppressWarnings( "unchecked" )
      final Optional<String> result = (Optional<String>) invokeStatic( "nextSafely", new Class<?>[] { Iterator.class, Class.class },
            iterator, String.class );

      assertThat( result ).isEmpty();
   }

   @Test
   void testToUrlReturnsUrlForExistingPath( @TempDir final Path tempDir ) throws Exception {
      final Path pluginJar = tempDir.resolve( "plugin.jar" );
      Files.createFile( pluginJar );

      @SuppressWarnings( "unchecked" )
      final Optional<URL> result = (Optional<URL>) invokeStatic( "toUrl", new Class<?>[] { Path.class }, pluginJar );

      assertThat( result ).isPresent();
      assertThat( result.get().getPath() ).endsWith( "plugin.jar" );
   }

   @Test
   void testEnsurePluginDirectoryExistsCreatesMissingDirectory( @TempDir final Path tempDir ) throws Exception {
      final Path pluginDirectory = tempDir.resolve( "plugins" );
      assertThat( Files.exists( pluginDirectory ) ).isFalse();

      invokeStatic( "ensurePluginDirectoryExists", new Class<?>[] { Path.class }, pluginDirectory );

      assertThat( Files.isDirectory( pluginDirectory ) ).isTrue();
   }

   @Test
   void testEnsurePluginDirectoryExistsIsNoOpWhenDirectoryAlreadyExists( @TempDir final Path tempDir ) throws Exception {
      final Path pluginDirectory = tempDir.resolve( "plugins" );
      Files.createDirectories( pluginDirectory );

      invokeStatic( "ensurePluginDirectoryExists", new Class<?>[] { Path.class }, pluginDirectory );

      assertThat( Files.isDirectory( pluginDirectory ) ).isTrue();
   }

   @Test
   void testEnsurePluginDirectoryExistsThrowsModelResolutionExceptionWhenCreationFails( @TempDir final Path tempDir ) throws Exception {
      final Path blockingFile = tempDir.resolve( "blocking-file" );
      Files.createFile( blockingFile );
      final Path unreachableDirectory = blockingFile.resolve( "plugins" );

      assertThatThrownBy( () -> invokeStatic( "ensurePluginDirectoryExists", new Class<?>[] { Path.class }, unreachableDirectory ) )
            .isInstanceOf( ModelResolutionException.class )
            .hasMessageContaining( "Unable to create plugins directory" );
   }

   @Test
   void testCreatePluginClassLoaderCreatesMissingDirectoryAndReturnsUrlClassLoader( @TempDir final Path tempDir ) throws Exception {
      final Path pluginDirectory = tempDir.resolve( "plugins" );

      final ClassLoader classLoader = (ClassLoader) invokeStatic( "createPluginClassLoader", new Class<?>[] { Path.class },
            pluginDirectory );

      assertThat( Files.isDirectory( pluginDirectory ) ).isTrue();
      assertThat( classLoader ).isInstanceOf( URLClassLoader.class );
      try ( URLClassLoader urlClassLoader = (URLClassLoader) classLoader ) {
         assertThat( urlClassLoader.getURLs() ).isEmpty();
      }
   }

   @Test
   void testCreatePluginClassLoaderOnlyIncludesJarFiles( @TempDir final Path tempDir ) throws Exception {
      final Path pluginDirectory = tempDir.resolve( "plugins" );
      Files.createDirectories( pluginDirectory );
      Files.createFile( pluginDirectory.resolve( "extension.jar" ) );
      Files.createFile( pluginDirectory.resolve( "readme.txt" ) );

      final ClassLoader classLoader = (ClassLoader) invokeStatic( "createPluginClassLoader", new Class<?>[] { Path.class },
            pluginDirectory );

      try ( URLClassLoader urlClassLoader = (URLClassLoader) classLoader ) {
         assertThat( urlClassLoader.getURLs() ).hasSize( 1 );
         assertThat( urlClassLoader.getURLs()[0].toString() ).endsWith( "extension.jar" );
      }
   }

   @Test
   void testCreatePluginClassLoaderFallsBackToParentClassLoaderWhenDirectoryIsUnusable( @TempDir final Path tempDir ) throws Exception {
      // A regular file in place of the expected plugin directory makes Files.list(...) fail with an
      // IOException;
      // the method must not propagate that failure but instead fall back to the parent classloader.
      final Path blockingFile = tempDir.resolve( "plugins" );
      Files.createFile( blockingFile );

      final ClassLoader classLoader = (ClassLoader) invokeStatic( "createPluginClassLoader", new Class<?>[] { Path.class },
            blockingFile );

      assertThat( classLoader ).isSameAs( ExtensionService.class.getClassLoader() );
   }
}
