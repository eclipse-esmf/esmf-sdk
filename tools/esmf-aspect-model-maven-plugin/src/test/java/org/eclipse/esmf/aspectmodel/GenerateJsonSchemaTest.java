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
import org.junit.jupiter.api.Test;

@MojoTest
public class GenerateJsonSchemaTest extends AspectModelMojoTest {
   @Test
   @InjectMojo(
         goal = GenerateJsonSchema.MAVEN_GOAL,
         pom = "src/test/resources/test-pom-valid-aspect-model-output-directory/pom.xml"
   )
   public void testGenerateJsonSchemaTest( final GenerateJsonSchema generateJsonSchema ) {
      assertThatCode( generateJsonSchema::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect.schema.json" ) ).exists();
   }

   /**
    * Verify that a preferred language can be chosen during json schema generation from the given aspect model.
    */
   @Test
   @InjectMojo(
         goal = GenerateJsonSchema.MAVEN_GOAL,
         pom = "src/test/resources/generate-schema-json-pom-valid-aspect-model-language/pom.xml"
   )
   public void testGenerateOpenApiSpecJsonValidAspectModelWithLanguageParameter( final GenerateJsonSchema generateJsonSchema ) {
      assertThatCode( generateJsonSchema::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "AspectWithEnglishAndGermanDescription.schema.json" ) ).exists();
   }
}
