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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.functions.ThrowingFunction;
import org.eclipse.esmf.test.shared.compiler.JavaCompiler;

import org.apache.commons.io.IOUtils;

public class TestContext {
   public static ThrowingFunction<Collection<JavaGenerator>, GenerationResult, IOException> generateAspectCode() {
      return generators -> {
         final File tempDirectory = Files.createTempDirectory( "junit" ).toFile();
         final Map<QualifiedName, Class<?>> generatedClasses = generateJavaCode( tempDirectory, generators );
         return new GenerationResult( tempDirectory, generatedClasses );
      };
   }

   public static ThrowingFunction<Collection<JavaGenerator>, StaticClassGenerationResult, IOException> generateStaticAspectCode() {
      return generators -> {
         final File tempDirectory = Files.createTempDirectory( "junit" ).toFile();
         final Map<QualifiedName, Class<?>> generatedClasses = generateJavaCode( tempDirectory, generators );
         return new StaticClassGenerationResult( tempDirectory, generatedClasses );
      };
   }

   private static Map<QualifiedName, Class<?>> generateJavaCode( final File tempDirectory, final Collection<JavaGenerator> generators )
         throws IOException {
      final File subFolder = new File(
            tempDirectory.getAbsolutePath() + File.separator + generators.iterator().next().getConfig().packageName()
                  .replace( '.', File.separatorChar ) );
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

      final List<String> referencedClasses = generators.stream().flatMap( generator ->
                  Stream.concat( generator.getConfig().importTracker().getUsedImports().stream(),
                        generator.getConfig().importTracker().getUsedStaticImports().stream() ) )
            .collect( Collectors.toList() );

      return JavaCompiler.compile( loadOrder, sources, referencedClasses ).compilationUnits();
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
