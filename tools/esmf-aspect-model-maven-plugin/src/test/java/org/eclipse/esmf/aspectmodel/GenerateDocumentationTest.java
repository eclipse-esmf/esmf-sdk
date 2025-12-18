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
import static org.assertj.core.api.Assertions.assertThatCode;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;

@MojoTest
public class GenerateDocumentationTest extends AspectModelMojoTest {
   @Test
   @InjectMojo(
         goal = GenerateDocumentation.MAVEN_GOAL,
         pom = "src/test/resources/test-pom-valid-aspect-model-output-directory/pom.xml"
   )
   public void testGenerateDocumentation( final GenerateDocumentation generateDocumentation ) {
      assertThatCode( generateDocumentation::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect_en.html" ) ).exists();
   }

   @Test
   @InjectMojo(
         goal = GenerateDocumentation.MAVEN_GOAL,
         pom = "src/test/resources/generate-documentation-pom-invalid-aspect-model/pom.xml"
   )
   public void testGenerateDocumentationInvalidAspectModel( final GenerateDocumentation generateDocumentation ) {
      assertThatCode( generateDocumentation::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessageContaining( "Error at line 17 column 3" );
      assertThat( generatedFilePath( "Aspect_en.html" ) ).doesNotExist();
   }
}
