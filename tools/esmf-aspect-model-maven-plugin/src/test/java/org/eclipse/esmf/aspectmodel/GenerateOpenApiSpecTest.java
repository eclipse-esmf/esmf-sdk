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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

public class GenerateOpenApiSpecTest extends AspectModelMojoTest {
   @Test
   public void testGenerateOpenApiSpecJsonValidAspectModel() throws Exception {
      final File testPom = getTestFile( "src/test/resources/generate-openapi-spec-json-pom-valid-aspect-model.xml" );
      final Mojo generateOpenApiSpec = lookupMojo( "generateOpenApiSpec", testPom );
      assertThatCode( generateOpenApiSpec::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect.oai.json" ) ).exists();
   }

   /**
    * Verify that a preferred language can be chosen during open api specification generation from the given aspect model.
    *
    * @throws Exception in case of any error during execution of the test.
    */
   @Test
   public void testGenerateOpenApiSpecJsonValidAspectModelWithLanguageParameter() throws Exception {
      final File testPom = getTestFile( "src/test/resources/generate-openapi-spec-json-pom-valid-aspect-model-language.xml" );
      final Mojo generateOpenApiSpec = lookupMojo( "generateOpenApiSpec", testPom );
      assertThatCode( generateOpenApiSpec::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "AspectWithEnglishAndGermanDescription.oai.json" ) ).exists();
   }

   @Test
   public void testGenerateOpenApiSpecYamlValidAspectModel() throws Exception {
      final File testPom = getTestFile( "src/test/resources/generate-openapi-spec-yaml-pom-valid-aspect-model.xml" );
      final Mojo generateOpenApiSpec = lookupMojo( "generateOpenApiSpec", testPom );
      assertThatCode( generateOpenApiSpec::execute ).doesNotThrowAnyException();

      final Path generatedFile = generatedFilePath( "Aspect.oai.yaml" );
      assertThat( generatedFile ).exists();

      final String yamlContent = new String( Files.readAllBytes( generatedFile ) );

      assertThat( yamlContent ).isNotEmpty();
      assertThat( yamlContent ).contains("\"post\":");
      assertThat( yamlContent ).contains("\"put\":");
      assertThat( yamlContent ).contains("\"patch\":");
   }

   @Test
   public void testGenerateOpenApiSpecJsonWithSeparateSchemaFiles() throws Exception {
      final File testPom = getTestFile( "src/test/resources/generate-openapi-spec-json-pom-separate-schema-files.xml" );
      final Mojo generateOpenApiSpec = lookupMojo( "generateOpenApiSpec", testPom );
      assertThatCode( generateOpenApiSpec::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "AspectWithEntity.oai.json" ) ).exists();
      assertThat( generatedFilePath( "AspectWithEntity.json" ) ).exists();
      assertThat( generatedFilePath( "TestEntity.json" ) ).exists();
   }

   @Test
   public void testGenerateOpenApiSpecYamlWithSeparateSchemaFiles() throws Exception {
      final File testPom = getTestFile( "src/test/resources/generate-openapi-spec-yaml-pom-separate-schema-files.xml" );
      final Mojo generateOpenApiSpec = lookupMojo( "generateOpenApiSpec", testPom );
      assertThatCode( generateOpenApiSpec::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "AspectWithEntity.oai.yaml" ) ).exists();
      assertThat( generatedFilePath( "AspectWithEntity.yaml" ) ).exists();
      assertThat( generatedFilePath( "TestEntity.yaml" ) ).exists();
   }

   @Test
   public void testGenerateOpenApiSpecInvalidOutputFormat() throws Exception {
      final File testPom = getTestFile( "src/test/resources/generate-openapi-spec-pom-invalid-format.xml" );
      final Mojo generateOpenApiSpec = lookupMojo( "generateOpenApiSpec", testPom );
      assertThatCode( generateOpenApiSpec::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Invalid output format." );
      assertThat( generatedFilePath( "Aspect.oai.json" ) ).doesNotExist();
   }
}
