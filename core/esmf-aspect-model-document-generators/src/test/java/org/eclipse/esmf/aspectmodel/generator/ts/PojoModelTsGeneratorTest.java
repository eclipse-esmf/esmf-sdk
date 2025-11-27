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

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.eclipse.esmf.test.TestAspect;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class PojoModelTsGeneratorTest extends TsGeneratorTestBase {

   @ParameterizedTest
   @EnumSource( value = TestAspect.class, mode = EnumSource.Mode.EXCLUDE, names = { "ASPECT_WITH_NAMESPACE_DESCRIPTION" } )
   void testCodeGeneration( final TestAspect testAspect ) throws IOException {
      final String className = testAspect.getName();
      final String fileName = "ts/snapshots/" + className + ".ts";
      final URL resource = classLoader.getResource( fileName );

      assertThat( resource ).withFailMessage( "File: " + fileName + " not found" ).isNotNull();

      Path snapshotPath = Paths.get( resource.getPath() );
      String snapshotContent = Files.readString( snapshotPath );
      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( testAspect ) );
      final String resultTs = result.sources().entrySet().stream()
            .filter( entry -> entry.getKey().fileName().equals( className ) )
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
   //      @ParameterizedTest
   @EnumSource( value = TestAspect.class, mode = EnumSource.Mode.EXCLUDE, names = { "ASPECT_WITH_NAMESPACE_DESCRIPTION" } )
   void overwriteSnapshotsPojo( final TestAspect testAspect ) throws IOException {
      final String className = testAspect.getName();
      final String fileName = "ts/snapshots/" + className + ".ts";

      Path snapshotPath = Paths.get( "src/test/resources", fileName );
      if ( !Files.exists( snapshotPath ) ) {
         throw new IOException( "Snapshot file not found: " + snapshotPath );
      }

      final GenerationResult result = TestContext.generateAspectCode().apply( getGenerators( testAspect ) );

      final String resultTs = result.sources().entrySet().stream()
            .filter( entry -> entry.getKey().fileName().equals( className ) )
            .map( Map.Entry::getValue )
            .findFirst()
            .orElseThrow( () -> new IOException( "Generated content not found for: " + className ) );

      Files.writeString( snapshotPath, resultTs );
   }
}
