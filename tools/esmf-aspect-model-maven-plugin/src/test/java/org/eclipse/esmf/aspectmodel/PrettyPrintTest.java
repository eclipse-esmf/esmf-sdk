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
public class PrettyPrintTest extends AspectModelMojoTest {
   @Test
   @InjectMojo(
         goal = PrettyPrint.MAVEN_GOAL,
         pom = "src/test/resources/test-pom-valid-aspect-model-output-directory/pom.xml"
   )
   public void testPrettyPrintValidAspectModel( final PrettyPrint prettyPrint ) {
      assertThatCode( prettyPrint::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect.ttl" ) ).exists();
   }

   @Test
   @InjectMojo(
         goal = PrettyPrint.MAVEN_GOAL,
         pom = "src/test/resources/prettyprint-pom-invalid-aspect-model/pom.xml"
   )
   public void testPrettyPrintInvalidAspectModel( final PrettyPrint prettyPrint ) {
      assertThatCode( prettyPrint::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessageContaining( "Error at line 17 column 3" );
      assertThat( generatedFilePath( "Aspect.ttl" ) ).doesNotExist();
   }
}
