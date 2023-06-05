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

import java.io.File;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;

public class PrettyPrintTest extends AspectModelMojoTest {

   @Test
   public void testPrettyPrintValidAspectModel() throws Exception {
      final File testPom = getTestFile( "src/test/resources/test-pom-valid-aspect-model-output-directory.xml" );
      final Mojo prettyPrint = lookupMojo( "prettyPrint", testPom );
      assertThatCode( prettyPrint::execute ).doesNotThrowAnyException();

      assertGeneratedFileExists( "Aspect.ttl" );
      deleteGeneratedFile( "Aspect.ttl" );
   }

   @Test
   public void testPrettyPrintInvalidAspectModel() throws Exception {
      final File testPom = getTestFile( "src/test/resources/prettyprint-pom-invalid-aspect-model.xml" );
      final Mojo prettyPrint = lookupMojo( "prettyPrint", testPom );
      assertThatCode( prettyPrint::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Failed to load Aspect Model InvalidSyntax." )
            .hasCauseInstanceOf( ParserException.class )
            .getCause()
            .hasMessageContaining( "Triples not terminated by DOT" );
      assertGeneratedFileDoesNotExist( "Aspect.ttl" );
   }

}
