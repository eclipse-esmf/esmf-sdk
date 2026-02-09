/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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
public class GenerateAsyncApiSpecTest extends AspectModelMojoTest {
   @Test
   @InjectMojo(
         goal = GenerateAsyncApiSpec.MAVEN_GOAL,
         pom = "src/test/resources/generate-asyncapi-spec-json-pom-valid-aspect-model/pom.xml"
   )
   public void testGenerateAsyncApiSpecJsonValidAspectModel( final GenerateAsyncApiSpec generateAsyncApiSpec ) {
      assertThatCode( generateAsyncApiSpec::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect.aai.json" ) ).exists();
   }

   /**
    * Verify that a preferred language can be chosen during async api specification generation from the given aspect model.
    */
   @Test
   @InjectMojo(
         goal = GenerateAsyncApiSpec.MAVEN_GOAL,
         pom = "src/test/resources/generate-asyncapi-spec-json-pom-valid-aspect-model-language/pom.xml"
   )
   public void testGenerateAsyncApiSpecJsonValidAspectModelWithLanguageParameter( final GenerateAsyncApiSpec generateAsyncApiSpec ) {
      assertThatCode( generateAsyncApiSpec::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "AspectWithEnglishAndGermanDescription.aai.json" ) ).exists();
   }

   @Test
   @InjectMojo(
         goal = GenerateAsyncApiSpec.MAVEN_GOAL,
         pom = "src/test/resources/generate-asyncapi-spec-json-pom-separate-schema-files/pom.xml"
   )
   public void testGenerateAsyncApiSpecJsonWithSeparateSchemaFiles( final GenerateAsyncApiSpec generateAsyncApiSpec ) {
      assertThatCode( generateAsyncApiSpec::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "AspectWithEvent.aai.json" ) ).exists();
      assertThat( generatedFilePath( "SomeEvent.json" ) ).exists();
   }

   @Test
   @InjectMojo(
         goal = GenerateAsyncApiSpec.MAVEN_GOAL,
         pom = "src/test/resources/generate-asyncapi-spec-pom-invalid-format/pom.xml"
   )
   public void testGenerateAsyncApiSpecInvalidOutputFormat( final GenerateAsyncApiSpec generateAsyncApiSpec ) {
      assertThatCode( generateAsyncApiSpec::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Invalid output format." );
      assertThat( generatedFilePath( "Aspect.aai.json" ) ).doesNotExist();
   }
}
