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

package org.eclipse.esmf.aspectmodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public abstract class AspectModelMojoTest extends AbstractMojoTestCase {

   protected void assertGeneratedFileExists( final String fileName ) {
      final Path generatedFile = getGeneratedFilePath( fileName, Optional.empty() );
      final boolean generatedFileExists = Files.exists( generatedFile );
      assertThat( generatedFileExists ).isTrue();
   }

   protected void assertGeneratedFileExists( final String fileName, final String subFolder ) {
      final Path generatedFile = getGeneratedFilePath( fileName, Optional.of( subFolder ) );
      final boolean generatedFileExists = Files.exists( generatedFile );
      assertThat( generatedFileExists ).isTrue();
   }

   protected void assertGeneratedFileDoesNotExist( final String fileName ) {
      final Path generatedFile = getGeneratedFilePath( fileName, Optional.empty() );
      final boolean generatedFileExists = Files.exists( generatedFile );
      assertThat( generatedFileExists ).isFalse();
   }

   protected void deleteGeneratedFile( final String fileName, final String subFolder ) throws IOException {
      final Path generatedFile = getGeneratedFilePath( fileName, Optional.of( subFolder ) );
      Files.delete( generatedFile );
   }

   protected void deleteGeneratedFile( final String fileName ) throws IOException {
      final Path generatedFile = getGeneratedFilePath( fileName, Optional.empty() );
      Files.delete( generatedFile );
   }

   private Path getGeneratedFilePath( final String fileName, final Optional<String> subfolder ) {
      final String baseOutputDir = System.getProperty( "user.dir" ) + "/target/test-artifacts";
      final String outputDirectory = subfolder.map( path -> baseOutputDir + File.separator + path )
            .orElseGet( () -> baseOutputDir );
      return Path.of( outputDirectory + "/" + fileName );
   }
}
