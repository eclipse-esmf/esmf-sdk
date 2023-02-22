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
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;

public class ValidateTest extends AspectModelMojoTest {
   @Test
   public void testValidateValidAspectModel() throws Exception {
      final File testPom = getTestFile( "src/test/resources/validate-pom-valid-aspect-model.xml" );
      final Mojo validate = lookupMojo( "validate", testPom );
      assertThatCode( validate::execute )
            .doesNotThrowAnyException();
   }

   @Test
   public void testValidateInvalidAspectModel() throws Exception {
      final File testPom = getTestFile( "src/test/resources/validate-pom-invalid-aspect-model.xml" );
      final Mojo validate = lookupMojo( "validate", testPom );
      assertThatCode( validate::execute )
            .isInstanceOf( MojoFailureException.class )
            .hasMessageContaining( "Syntax error in line 17, column 2" );
   }
}
