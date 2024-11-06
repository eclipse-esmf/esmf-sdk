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

package org.eclipse.esmf.buildtime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Streams;

/**
 * This class runs at build time. It will adjust the configs that were created by the native image agent,
 * which sometimes create configurations which lead to failures during either compilation or runtime.
 */
public class CustomizeGraalVmConfigs {
   public static void main( final String[] args ) throws IOException {
      // args[0] is set to the path containing the GraalVM configs to adjust by the Maven build
      final File configsDirectory = new File( args[0] );
      if ( !configsDirectory.exists() || !configsDirectory.isDirectory() ) {
         System.err.println( "Warning: Native resource config directory " + configsDirectory + " not found, skipping customizing" );
         return;
      }
      final Path configsPath = configsDirectory.toPath();
      adjustResourceConfig( configsPath.resolve( "resource-config.json" ).toFile() );
      adjustReflectConfig( configsPath.resolve( "reflect-config.json" ).toFile() );
   }

   private static void adjustResourceConfig( final File resourceConfig ) throws IOException {
      if ( !resourceConfig.exists() ) {
         System.err.println( "Warning: Native resource config " + resourceConfig + " not found, skipping customizing" );
         return;
      }

      final List<Predicate<JsonNode>> resourceIncludesToDelete = List.of(
            // This include is deleted because it leads to "Class path contains multiple SLF4J bindings" warnings
            includeNode -> includeNode.asText().contains( "org/slf4j/impl/StaticLoggerBinder.class" )
      );
      final String content = Files.readString( resourceConfig.toPath() );
      final ObjectMapper mapper = new ObjectMapper();
      final JsonNode root = mapper.readTree( content );
      final JsonNode includes = root.get( "resources" ).get( "includes" );
      if ( includes == null || includes.elements() == null ) {
         return;
      }
      for ( final Iterator<JsonNode> i = includes.elements(); i.hasNext(); ) {
         final JsonNode include = i.next();
         for ( final Predicate<JsonNode> decideIfNodeShouldBeDeleted : resourceIncludesToDelete ) {
            final JsonNode pattern = include.get( "pattern" );
            if ( pattern != null && decideIfNodeShouldBeDeleted.test( include.get( "pattern" ) ) ) {
               i.remove();
            }
         }
      }

      try ( final FileOutputStream out = new FileOutputStream( resourceConfig ) ) {
         mapper.writerWithDefaultPrettyPrinter().writeValue( out, root );
      }
   }

   private static Class<?> instantiateParameterType( final String typeName ) {
      for ( final Class<?> simpleType : List.of(
            boolean.class, byte.class, int.class, long.class, float.class, double.class,
            boolean[].class, byte[].class, int[].class, long[].class, float[].class, double[].class
      ) ) {
         if ( typeName.equals( simpleType.getSimpleName() ) ) {
            return simpleType;
         }
      }

      try {
         return Class.forName( typeName, false, CustomizeGraalVmConfigs.class.getClassLoader() );
      } catch ( final ClassNotFoundException e ) {
         return null;
      }
   }

   private static void removeNonExistingtMethods( final JsonNode entry, final Class<?> clazz ) {
      final JsonNode methods = entry.get( "methods" );
      if ( methods == null ) {
         return;
      }
      for ( final Iterator<JsonNode> it = methods.elements(); it.hasNext(); ) {
         final JsonNode method = it.next();
         try {
            final String methodName = method.get( "name" ).asText();
            final Class<?>[] parameterTypes = Streams.stream( method.get( "parameterTypes" ).elements() )
                  .map( param -> instantiateParameterType( param.asText() ) )
                  .toArray( Class<?>[]::new );
            if ( !methodName.equals( "<init>" ) ) {
               clazz.getMethod( methodName, parameterTypes );
            }
         } catch ( final Throwable t ) {
            // The method can not be found via reflection, so it can be considered a spurious entry. Remove it.
            it.remove();
         }
      }
      if ( !methods.elements().hasNext() ) {
         ( (ObjectNode) entry ).remove( "methods" );
      }
   }

   private static boolean shouldEntryBeRemovedFromReflectConfig( final JsonNode entry ) {
      final String className = entry.get( "name" ).asText();
      for ( final String nonReflectionClass : List.of(
            // These three reflection entries are added by the native image agent, but cause the native compilation to fail
            "jdk.internal.loader.BuiltinClassLoader",
            "jdk.internal.loader.ClassLoaders$AppClassLoader",
            "jdk.internal.loader.ClassLoaders$PlatformClassLoader",
            // The remaining exludes are generated by the native-image agent during test runs and are explicitly not
            // needed at native-image compile time
            "org.eclipse.esmf.test.",
            "test.test.test",
            "org.junit.",
            "com.sun.tools.javac"
      ) ) {
         if ( className.contains( nonReflectionClass ) ) {
            return true;
         }
      }

      if ( className.matches( "org\\.eclipse\\.esmf\\.*Test" ) ) {
         return true;
      }

      try {
         final Class<?> clazz = Class.forName( className, false, CustomizeGraalVmConfigs.class.getClassLoader() );
         removeNonExistingtMethods( entry, clazz );
         return false;
      } catch ( final ClassNotFoundException e ) {
         // If the class can not be instantiated, it's also a spurious entry added by the native-image agent; remove it.
         return true;
      }
   }

   private static void adjustReflectConfig( final File reflectConfig ) throws IOException {
      if ( !reflectConfig.exists() ) {
         System.err.println( "Warning: Native resource config " + reflectConfig + " not found, skipping customizing" );
         return;
      }

      final String content = Files.readString( reflectConfig.toPath() );
      final ObjectMapper mapper = new ObjectMapper();
      final JsonNode root = mapper.readTree( content );
      for ( final Iterator<JsonNode> it = root.elements(); it.hasNext(); ) {
         final JsonNode entry = it.next();
         if ( shouldEntryBeRemovedFromReflectConfig( entry ) ) {
            it.remove();
         }
      }

      try ( final FileOutputStream out = new FileOutputStream( reflectConfig ) ) {
         mapper.writerWithDefaultPrettyPrinter().writeValue( out, root );
      }
   }
}
