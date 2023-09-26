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

/**
 * This class runs at build time, after the executable jar for samm-cli has been executed with the GraalVM native image agent,
 * but before the native image compiler is started. It will adjust the configs that were created by the native image agent,
 * which sometimes create configurations which lead to failures during either compilation or runtime.
 */
public class CustomizeGraalVmConfigs {
   public static void main( final String[] args ) throws IOException {
      // args[0] is set to the path containing the GraalVM configs to adjust by the Maven build
      final File configsDirectory = new File( args[0] );
      if ( !configsDirectory.exists() || !configsDirectory.isDirectory() ) {
         System.err.println( "Warning: Native resource config directory " + configsDirectory + " not found, skipping customizing" );
         System.exit( 0 );
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

   private static void adjustReflectConfig( final File reflectConfig ) throws IOException {
      if ( !reflectConfig.exists() ) {
         System.err.println( "Warning: Native resource config " + reflectConfig + " not found, skipping customizing" );
         return;
      }

      final List<Predicate<JsonNode>> reflectEntriesToDelete = List.of(
            // These three reflection entries are added by the native image agent, but cause the native compilation to fail
            includeNode -> includeNode.asText().contains( "jdk.internal.loader.BuiltinClassLoader" ),
            includeNode -> includeNode.asText().contains( "jdk.internal.loader.ClassLoaders$AppClassLoader" ),
            includeNode -> includeNode.asText().contains( "jdk.internal.loader.ClassLoaders$PlatformClassLoader" )
      );
      final String content = Files.readString( reflectConfig.toPath() );
      final ObjectMapper mapper = new ObjectMapper();
      final JsonNode root = mapper.readTree( content );
      for ( final Iterator<JsonNode> i = root.elements(); i.hasNext(); ) {
         final JsonNode include = i.next();
         for ( final Predicate<JsonNode> decideIfNodeShouldBeDeleted : reflectEntriesToDelete ) {
            final JsonNode name = include.get( "name" );
            if ( name != null && decideIfNodeShouldBeDeleted.test( include.get( "name" ) ) ) {
               i.remove();
            }
         }
      }

      try ( final FileOutputStream out = new FileOutputStream( reflectConfig ) ) {
         mapper.writerWithDefaultPrettyPrinter().writeValue( out, root );
      }
   }
}
