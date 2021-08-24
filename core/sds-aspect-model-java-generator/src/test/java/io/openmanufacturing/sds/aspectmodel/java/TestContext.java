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

package io.openmanufacturing.sds.aspectmodel.java;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

import org.apache.commons.io.IOUtils;

import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestResources;
import io.openmanufacturing.sds.test.shared.compiler.JavaCompiler;

public class TestContext {
   public static ThrowingFunction<Collection<JavaGenerator>, GenerationResult, IOException> generateAspectCode(
         final TestAspect aspect, final KnownVersion version ) {
      return generators -> {
         final File tempDirectory = Files.createTempDirectory( "junit" ).toFile();
         final Map<QualifiedName, Class<?>> generatedClassess = generateJavaCode( tempDirectory, generators,
               aspect, version );
         return new GenerationResult( tempDirectory, generatedClassess );
      };
   }

   public static ThrowingFunction<Collection<JavaGenerator>, StaticClassGenerationResult, IOException> generateStaticAspectCode(
         final TestAspect aspect, final KnownVersion version ) {
      return generators -> {
         final File tempDirectory = Files.createTempDirectory( "junit" ).toFile();
         final Map<QualifiedName, Class<?>> generatedClasses = generateJavaCode( tempDirectory, generators,
               aspect, version );
         return new StaticClassGenerationResult( tempDirectory, generatedClasses );
      };
   }

   private static Map<QualifiedName, Class<?>> generateJavaCode( final File tempDirectory,
         final Collection<JavaGenerator> generators,
         final TestAspect aspect,
         final KnownVersion version ) throws IOException {
      final Optional<String> customJavaPackageName = Optional.empty();
      final VersionedModel model = TestResources.getModel( aspect, version ).get();
      final Aspect aspectMetaModel = AspectModelLoader.fromVersionedModelUnchecked( model );
      final String javaPackage = customJavaPackageName.filter( javaPackageName -> !javaPackageName.isEmpty() )
                                                      .orElse(
                                                            aspectMetaModel.getAspectModelUrn().get().getNamespace() );

      final File subFolder = new File(
            tempDirectory.getAbsolutePath() + File.separator + javaPackage.replace( '.', File.separatorChar ) );
      if ( !subFolder.mkdirs() ) {
         throw new IOException( "Could not create directory: " + subFolder );
      }

      final Map<QualifiedName, ByteArrayOutputStream> outputs = new LinkedHashMap<>();
      generators.forEach( generator ->
            generator.generate( name -> outputs.computeIfAbsent( name, name2 -> new ByteArrayOutputStream() ) ) );

      final Map<QualifiedName, String> sources = new LinkedHashMap<>();
      final List<QualifiedName> loadOrder = new ArrayList<>();

      for ( final Map.Entry<QualifiedName, ByteArrayOutputStream> entry : outputs.entrySet() ) {
         final QualifiedName className = entry.getKey();
         final byte[] classContent = entry.getValue().toByteArray();
         loadOrder.add( className );
         sources.put( className, new String( classContent, StandardCharsets.UTF_8 ) );
         writeFile( className.getClassName(), classContent, subFolder );
      }

      final List<String> referencedClasses = generators.stream().flatMap(
            generator -> Stream.concat( generator.getConfig().getImportTracker().getUsedImports().stream(),
                  generator.getConfig().getImportTracker().getUsedStaticImports().stream() ) )
                                                       .collect( Collectors.toList() );

      return JavaCompiler.compile( loadOrder, sources, referencedClasses );
   }

   private static void writeFile( final String className, final byte[] content, final File targetDirectory ) {
      final String fileName = className + ".java";
      try {
         IOUtils.write( content, Files.newOutputStream( new File( targetDirectory, fileName ).toPath() ) );
      } catch ( final IOException e ) {
         fail( "Could not create file " + fileName );
      }
   }
}
