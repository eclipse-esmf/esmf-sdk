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
import org.junit.Test;

import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.ParserException;

public class MigrateTest extends AspectModelMojoTest {

   @Test
   public void testMigrateValidAspectModel() throws Exception {
      final File testPom = getTestFile( "src/test/resources/test-pom-valid-aspect-model-output-directory.xml" );
      final Mojo migrate = lookupMojo( "migrate", testPom );
      assertThatCode( migrate::execute ).doesNotThrowAnyException();

      assertGeneratedFileExists( "Aspect.ttl" );
      deleteGeneratedFile( "Aspect.ttl" );
   }

   @Test
   public void testMigrateInvalidAspectModel() throws Exception {
      final File testPom = getTestFile( "src/test/resources/migrate-pom-invalid-aspect-model.xml" );
      final Mojo migrate = lookupMojo( "migrate", testPom );
      assertThatCode( migrate::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Failed to load Aspect Model InvalidSyntax." )
            .hasCauseInstanceOf( ParserException.class )
            .getCause()
            .hasMessageContaining( "Triples not terminated by DOT" );
      assertGeneratedFileDoesNotExist( "Aspect.ttl" );
   }

}
