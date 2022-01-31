/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

public class GenerateDocumentationTest extends AbstractMojoTestCase {

   private final String outputDirectory = System.getProperty("user.dir") + "/target/test-artifacts";
   private final Path generatedFile = Path.of( outputDirectory + "/Aspect_en.html" );

   @Test
   public void testGenerateDocumentation() throws Exception {
      final File testPom = getTestFile( "src/test/resources/generate-documentation-pom-valid-aspect-model.xml" );
      final Mojo generateDocumentation = lookupMojo( "generateDocumentation", testPom );
      assertThatCode( generateDocumentation::execute ).doesNotThrowAnyException();
      final boolean generatedFileExists = Files.exists( generatedFile );
      assertThat( generatedFileExists ).isTrue();
      Files.delete( generatedFile );
   }

   @Test
   public void testGenerateDocumentationInvalidAspectModel() throws Exception {
      final File testPom = getTestFile( "src/test/resources/generate-documentation-pom-invalid-aspect-model.xml" );
      final Mojo generateDocumentation = lookupMojo( "generateDocumentation", testPom );
      assertThatCode( generateDocumentation::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Validation report: Validation failed: \nThe Aspect Model contains invalid syntax at line number 17 and column number 2." );
      final boolean generatedFileExists = Files.exists( generatedFile );
      assertThat( generatedFileExists ).isFalse();
   }

}
