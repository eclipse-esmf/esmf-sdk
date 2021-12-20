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

import org.junit.jupiter.api.Test;

import io.openmanufacturing.sds.aspectmodel.java.exception.CodeGenerationException;

public class JavaCodeGenerationConfigurationTest {

   private final String templateLibPath = "/test";
   private final String templateLibFileName = "test.vm";

   @Test
   public void testValidTemplateLibConfig() {
      assertThatCode( () ->
            new JavaCodeGenerationConfig( true, "", true, templateLibPath, templateLibFileName )
      ).doesNotThrowAnyException();

      assertThatCode( () ->
            new JavaCodeGenerationConfig( true, "", false, "", "" )
      ).doesNotThrowAnyException();
   }

   @Test
   public void testTemplateLibConfigMissingPath() {
      assertThatCode( () ->
            new JavaCodeGenerationConfig( true, "", true, "", templateLibFileName )
      ).isExactlyInstanceOf( CodeGenerationException.class ).hasMessage( "Missing configuration. Please provide path to velocity template library file." );
   }

   @Test
   public void testTemplateLibConfigMissingFileName() {
      assertThatCode( () ->
            new JavaCodeGenerationConfig( true, "", true, templateLibPath, "" )
      ).isExactlyInstanceOf( CodeGenerationException.class ).hasMessage( "Missing configuration. Please provide name for velocity template library file." );
   }

}
