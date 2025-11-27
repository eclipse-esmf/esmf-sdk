/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.ts;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.esmf.test.TestAspect;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class StaticMetaModelTsGeneratorTest extends TsGeneratorTestBase {

   @ParameterizedTest
   @EnumSource( value = TestAspect.class, mode = EnumSource.Mode.EXCLUDE, names = { "ASPECT_WITH_NAMESPACE_DESCRIPTION" } )
   void testCodeGeneration( final TestAspect testAspect ) throws IOException {
      final String className = testAspect.getName();
      final String fileName = "ts/snapshots/" + "Meta" + className + ".ts";
      final URL resource = classLoader.getResource( fileName );
      assertThat( resource ).withFailMessage( "File: " + fileName + " not found" ).isNotNull();
      Path snapshotPath = Paths.get( resource.getPath() );
      String snapshotContent = Files.readString( snapshotPath );
      final GenerationResult result = TestContext.generateAspectCode().apply( getStaticGenerators( testAspect ) );
      final String resultTs = result.sources().entrySet().stream()
            .filter( entry -> entry.getKey().fileName().equals( "Meta" + className ) )
            .map( Map.Entry::getValue )
            .findFirst()
            .orElseThrow();

      assertThat( resultTs ).isEqualTo( snapshotContent );
   }

   /**
    * This test overwrites snapshot files with newly generated content and is intended for bulk updates
    * when non-critical changes (e.g., formatting, refactoring) are introduced. It is commented out by default
    * and should only be uncommented when snapshots need to be updated. Use this cautiously and ensure changes
    * are reviewed (e.g., using version control) after running this test.
    *
    * @param testAspect The {@code TestAspect} enum value representing the aspect being tested.
    * @throws IOException If an I/O error occurs while reading or writing the snapshot files.
    */
   // Uncomment ONLY for snapshot updates
   // @ParameterizedTest
   @EnumSource( value = TestAspect.class, mode = EnumSource.Mode.EXCLUDE, names = { "ASPECT_WITH_NAMESPACE_DESCRIPTION" } )
   void overwriteSnapshotsMeta( final TestAspect testAspect ) throws IOException {
      final String className = testAspect.getName();
      final String fileName = "ts/snapshots/" + "Meta" + className + ".ts";

      Path snapshotPath = Paths.get( "src/test/resources", fileName );
      if ( !Files.exists( snapshotPath ) ) {
         throw new IOException( "Snapshot file not found: " + snapshotPath );
      }

      final GenerationResult result = TestContext.generateAspectCode().apply( getStaticGenerators( testAspect ) );

      final String resultTs = result.sources().entrySet().stream()
            .filter( entry -> entry.getKey().fileName().equals( "Meta" + className ) )
            .map( Map.Entry::getValue )
            .findFirst()
            .orElseThrow( () -> new IOException( "Generated content not found for: " + className ) );

      Files.writeString( snapshotPath, resultTs );

      System.out.println( "Snapshot file overwritten: " + snapshotPath );
   }

   /**
    * Checks that generated TypeScript code for each aspect compiles successfully using the TypeScript compiler.
    * <b>Prerequisites:</b>
    * <ul>
    *   <li>Node.js and TypeScript must be installed on the system where the test is executed.</li>
    *   <li>The <code>tsc</code> command must be available in the system PATH.</li>
    * </ul>
    * <b>Note:</b> If <code>tsc</code> is not available, this test will be skipped and a message will be logged.
    *
    * @param testAspect The {@link TestAspect} enum value representing the aspect being tested.
    * @throws IOException If an I/O error occurs during compilation.
    * @throws InterruptedException If the compilation process is interrupted.
    */
   @ParameterizedTest
   @EnumSource( value = TestAspect.class, mode = EnumSource.Mode.EXCLUDE, names = { "ASPECT_WITH_NAMESPACE_DESCRIPTION" } )
   void testCodeGenerationCompilationCheck( final TestAspect testAspect ) throws IOException, InterruptedException {
      Assumptions.assumeTrue( isTscAvailable(), "[INFO] TypeScript compiler (tsc) is not available in the system environment. "
            + "Compilation tests will be skipped. "
            + "To enable these tests, please ensure that Node.js and TypeScript are installed and 'tsc' is available in your PATH."
      );

      final String className = testAspect.getName();
      final String fileName = "ts/snapshots/" + "Meta" + className + ".ts";
      final URL resource = classLoader.getResource( fileName );
      assertThat( resource ).withFailMessage( "File: " + fileName + " not found" ).isNotNull();

      Path snapshotPath = Paths.get( resource.getPath() );
      ProcessBuilder pb = new ProcessBuilder(
            "tsc",
            "--noEmit",
            "--lib", "es2018",
            snapshotPath.toAbsolutePath().toString()
      );
      pb.redirectErrorStream( true );
      Process process = pb.start();

      String output;
      try ( BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) ) ) {
         output = reader.lines().collect( Collectors.joining( System.lineSeparator() ) );
      }

      int exitCode = process.waitFor();
      assertThat( exitCode )
            .withFailMessage( "TypeScript compilation failed for file: " + fileName + "\nCompiler output:\n" + output )
            .isZero();
   }

   /**
    * Checks if the TypeScript compiler (tsc) is available in the system environment.
    *
    * @return true if tsc is available and executable, false otherwise.
    */
   static boolean isTscAvailable() {
      try {
         ProcessBuilder pb = new ProcessBuilder( "tsc", "--version" );
         pb.redirectErrorStream( true );
         Process process = pb.start();
         int exitCode = process.waitFor();
         return exitCode == 0;
      } catch ( IOException | InterruptedException e ) {
         return false;
      }
   }
}