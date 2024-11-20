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

import static org.assertj.core.api.Assertions.assertThatCode;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

@SuppressWarnings( "JUnitMixedFramework" )
public class ValidateTest extends AspectModelMojoTest {
   @Test
   public void testValidateValidAspectModel() throws Exception {
      final Mojo validate = getMojo( "validate-pom-valid-aspect-model", "validate" );
      assertThatCode( validate::execute )
            .doesNotThrowAnyException();
   }

   @Test
   public void testValidateInvalidAspectModel() throws Exception {
      final Mojo validate = getMojo( "validate-pom-invalid-aspect-model", "validate" );
      assertThatCode( validate::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessageContaining( "Syntax error in line 17, column 2" );
   }

   @Test
   public void testValidateMultipleAspectModels() throws Exception {
      final Mojo validate = getMojo( "validate-pom-multiple-aspect-models", "validate" );
      assertThatCode( validate::execute )
            .doesNotThrowAnyException();
   }

   @Test
   public void testValidateWithResolutionFromGitHub() throws Exception {
      final String serverConfig = """
            <configuration>
                <repository>eclipse-esmf/esmf-sdk</repository>
                <directory>core/esmf-test-aspect-models/src/main/resources/valid</directory>
                <branch>main</branch>
            </configuration>
            """;
      final Mojo validate = getMojo( "validate-pom-resolve-from-github", "validate", serverConfig );
      assertThatCode( validate::execute )
            .doesNotThrowAnyException();
   }
}
