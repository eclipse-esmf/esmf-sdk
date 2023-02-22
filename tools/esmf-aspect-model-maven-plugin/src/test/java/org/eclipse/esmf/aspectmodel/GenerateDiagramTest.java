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

import static org.assertj.core.api.Assertions.*;

import java.io.File;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

public class GenerateDiagramTest extends AspectModelMojoTest {

   @Test
   public void testGenerateDiagramsValidAspectModel() throws Exception {
      final File testPom = getTestFile( "src/test/resources/generate-diagram-pom-valid-aspect-model.xml" );
      final Mojo generateDiagram = lookupMojo( "generateDiagram", testPom );
      assertThatCode( generateDiagram::execute ).doesNotThrowAnyException();

      assertGeneratedFileExists( "Aspect_en.dot" );
      assertGeneratedFileExists( "Aspect_en.png" );

      deleteGeneratedFile( "Aspect_en.dot" );
      deleteGeneratedFile( "Aspect_en.png" );
   }

   @Test
   public void testGenerateDiagramsInvalidTargetFormat() throws Exception {
      final File testPom = getTestFile( "src/test/resources/generate-diagram-pom-invalid-target-format.xml" );
      final Mojo generateDiagram = lookupMojo( "generateDiagram", testPom );
      assertThatCode( generateDiagram::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Invalid target format provided. Possible formats are dot, svg & png." );
   }
}
