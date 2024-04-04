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

import static org.assertj.core.api.Assertions.*;

import java.io.File;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

public class GenerateAsyncApiSpecTest extends AspectModelMojoTest {
   @Test
   public void testGenerateAsyncApiSpecJsonValidAspectModel() throws Exception {
      final File testPom = getTestFile( "src/test/resources/generate-asyncapi-spec-json-pom-valid-aspect-model.xml" );
      final Mojo generateOpenApiSpec = lookupMojo( "generateAsyncApiSpec", testPom );
      assertThatCode( generateOpenApiSpec::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect.aai.json" ) ).exists();
   }

   /**
    * Verify that a preferred language can be chosen during async api specification generation from the given aspect model.
    *
    * @throws Exception in case of any error during execution of the test.
    */
   @Test
   public void testGenerateAsyncApiSpecJsonValidAspectModelWithLanguageParameter() throws Exception {
      final File testPom = getTestFile( "src/test/resources/generate-asyncapi-spec-json-pom-valid-aspect-model-language.xml" );
      final Mojo generateOpenApiSpec = lookupMojo( "generateAsyncApiSpec", testPom );
      assertThatCode( generateOpenApiSpec::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "AspectWithEnglishAndGermanDescription.aai.json" ) ).exists();
   }

   @Test
   public void testGenerateAsyncApiSpecJsonWithSeparateSchemaFiles() throws Exception {
      final File testPom = getTestFile( "src/test/resources/generate-asyncapi-spec-json-pom-separate-schema-files.xml" );
      final Mojo generateOpenApiSpec = lookupMojo( "generateAsyncApiSpec", testPom );
      assertThatCode( generateOpenApiSpec::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "AspectWithEvent.aai.json" ) ).exists();
      assertThat( generatedFilePath( "SomeEvent.json" ) ).exists();
   }

   @Test
   public void testGenerateAsyncApiSpecInvalidOutputFormat() throws Exception {
      final File testPom = getTestFile( "src/test/resources/generate-asyncapi-spec-pom-invalid-format.xml" );
      final Mojo generateOpenApiSpec = lookupMojo( "generateAsyncApiSpec", testPom );
      assertThatCode( generateOpenApiSpec::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Invalid output format." );
      assertThat( generatedFilePath( "Aspect.aai.json" ) ).doesNotExist();
   }
}
