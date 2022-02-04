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

import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.File;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

public class MojoConfigTest extends AspectModelMojoTest {

   @Test
   public void testInvalidModelsRoot() throws Exception {
      final File testPom = getTestFile( "src/test/resources/test-pom-invalid-models-root.xml" );
      final Mojo validate = lookupMojo( "validate", testPom );
      assertThatCode( validate::execute )
            .isInstanceOf( MojoFailureException.class )
            .hasMessage( "Validation report: Validation failed: \n"
                  + "The Aspect Model could not be validated: Model could not be resolved entirely: The AspectModel: "
                  + "urn:bamm:io.openmanufacturing.test:1.0.0#Aspect could not be found in directory: "
                  + "C:\\Workspace\\sds-sdk\\tools\\sds-aspect-model-maven-plugin\\src\\main\\resources\\io.openmanufacturing.test\\1.0.0" );
   }

   @Test
   public void testDefaultModelsRoot() throws Exception {
      final File testPom = getTestFile( "src/test/resources/test-pom-default-models-root.xml" );
      final Mojo validate = lookupMojo( "validate", testPom );
      assertThatCode( validate::execute ).doesNotThrowAnyException();
   }

   @Test
   public void testMissingIncludes() throws Exception {
      final File testPom = getTestFile( "src/test/resources/test-pom-missing-includes.xml" );
      final Mojo validate = lookupMojo( "validate", testPom );
      assertThatCode( validate::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Missing configuration. Please provide Aspect Models to be included." );
   }

   @Test
   public void testMissingInclude() throws Exception {
      final File testPom = getTestFile( "src/test/resources/test-pom-missing-include.xml" );
      final Mojo validate = lookupMojo( "validate", testPom );
      assertThatCode( validate::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Missing configuration. Please provide Aspect Models to be included." );
   }

}
