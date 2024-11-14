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

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

@SuppressWarnings( "JUnitMixedFramework" )
public class GenerateDocumentationTest extends AspectModelMojoTest {
   @Test
   public void testGenerateDocumentation() throws Exception {
      final Mojo generateDocumentation = getMojo( "test-pom-valid-aspect-model-output-directory", "generateDocumentation" );
      assertThatCode( generateDocumentation::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect_en.html" ) ).exists();
   }

   @Test
   public void testGenerateDocumentationInvalidAspectModel() throws Exception {
      final Mojo generateDocumentation = getMojo( "generate-documentation-pom-invalid-aspect-model", "generateDocumentation" );
      assertThatCode( generateDocumentation::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessageContaining( "Syntax error in line 17, column 2" );
      assertThat( generatedFilePath( "Aspect_en.html" ) ).doesNotExist();
   }
}
