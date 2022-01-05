/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.java;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.openmanufacturing.sds.aspectmodel.java.exception.CodeGenerationException;

public class JavaCodeGenerationConfigurationTest {

   final String currentWorkingDirectory = System.getProperty( "user.dir" );
   private final File templateLibFile = Path.of( currentWorkingDirectory, "/templates", "/test-macro-lib.vm" ).toFile();
   private final File emptyTemplateLibFile = Path.of( "" ).toFile();
   private final File nonExistingTemplateLibPath = Path.of( "/templates", "/non-existing.vm" ).toFile();

   @Test
   public void testValidTemplateLibConfig() {
      assertThatCode( () ->
            new JavaCodeGenerationConfig( true, "", true, templateLibFile )
      ).doesNotThrowAnyException();

      assertThatCode( () ->
            new JavaCodeGenerationConfig( true, "", false, emptyTemplateLibFile )
      ).doesNotThrowAnyException();
   }

   @Test
   public void testTemplateLibConfigMissingFile() {
      assertThatCode( () ->
            new JavaCodeGenerationConfig( true, "", true, emptyTemplateLibFile )
      ).isExactlyInstanceOf( CodeGenerationException.class ).hasMessage( "Missing configuration. Please provide path to velocity template library file." );
   }

   @Test
   public void testTemplateLibConfigNonExistingFile() {
      assertThatCode( () ->
            new JavaCodeGenerationConfig( true, "", true, nonExistingTemplateLibPath )
      ).isExactlyInstanceOf( CodeGenerationException.class )
            .hasMessage( "Incorrect configuration. Please provide a valid path to the velocity template library file." );
   }

}
