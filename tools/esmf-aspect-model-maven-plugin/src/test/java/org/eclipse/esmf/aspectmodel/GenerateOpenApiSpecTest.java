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

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

@SuppressWarnings( "JUnitMixedFramework" )
public class GenerateOpenApiSpecTest extends AspectModelMojoTest {
   @Test
   public void testGenerateOpenApiSpecJsonValidAspectModel() throws Exception {
      final Mojo generateOpenApiSpec = getMojo( "generate-openapi-spec-json-pom-valid-aspect-model", "generateOpenApiSpec" );
      assertThatCode( generateOpenApiSpec::execute ).doesNotThrowAnyException();

      final Path generatedFile = generatedFilePath( "Aspect.oai.json" );
      assertThat( generatedFile ).exists();

      final String yamlContent = new String( Files.readAllBytes( generatedFile ) );

      assertThat( yamlContent ).contains( "postAspect" );
      assertThat( yamlContent ).contains( "putAspect" );
      assertThat( yamlContent ).contains( "patchAspect" );
   }

   @Test
   public void testGenerateOpenApiSpecJsonValidAspectModelWithCrudParameters() throws Exception {
      final Mojo generateOpenApiSpec = getMojo( "generate-openapi-spec-json-pom-valid-aspect-model-with-crud-parameters",
            "generateOpenApiSpec" );
      assertThatCode( generateOpenApiSpec::execute ).doesNotThrowAnyException();

      final Path generatedFile = generatedFilePath( "Aspect.oai.json" );
      assertThat( generatedFile ).exists();

      final String yamlContent = new String( Files.readAllBytes( generatedFile ) );

      assertThat( yamlContent ).contains( "postAspect" );
      assertThat( yamlContent ).contains( "putAspect" );
      assertThat( yamlContent ).contains( "patchAspect" );
   }

   /**
    * Verify that a preferred language can be chosen during open api specification generation from the given aspect model.
    *
    * @throws Exception in case of any error during execution of the test.
    */
   @Test
   public void testGenerateOpenApiSpecJsonValidAspectModelWithLanguageParameter() throws Exception {
      final Mojo generateOpenApiSpec = getMojo( "generate-openapi-spec-json-pom-valid-aspect-model-language",
            "generateOpenApiSpec" );
      assertThatCode( generateOpenApiSpec::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "AspectWithEnglishAndGermanDescription.oai.json" ) ).exists();
   }

   @Test
   public void testGenerateOpenApiSpecYamlValidAspectModel() throws Exception {
      final Mojo generateOpenApiSpec = getMojo( "generate-openapi-spec-yaml-pom-valid-aspect-model",
            "generateOpenApiSpec" );
      assertThatCode( generateOpenApiSpec::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect.oai.yaml" ) ).exists();
   }

   @Test
   public void testGenerateOpenApiSpecJsonWithSeparateSchemaFiles() throws Exception {
      final Mojo generateOpenApiSpec = getMojo( "generate-openapi-spec-json-pom-separate-schema-files",
            "generateOpenApiSpec" );
      assertThatCode( generateOpenApiSpec::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "AspectWithEntity.oai.json" ) ).exists();
      assertThat( generatedFilePath( "AspectWithEntity.json" ) ).exists();
      assertThat( generatedFilePath( "TestEntity.json" ) ).exists();
   }

   @Test
   public void testGenerateOpenApiSpecYamlWithSeparateSchemaFiles() throws Exception {
      final Mojo generateOpenApiSpec = getMojo( "generate-openapi-spec-yaml-pom-separate-schema-files",
            "generateOpenApiSpec" );
      assertThatCode( generateOpenApiSpec::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "AspectWithEntity.oai.yaml" ) ).exists();
      assertThat( generatedFilePath( "AspectWithEntity.yaml" ) ).exists();
      assertThat( generatedFilePath( "TestEntity.yaml" ) ).exists();
   }

   @Test
   public void testGenerateOpenApiSpecInvalidOutputFormat() throws Exception {
      final Mojo generateOpenApiSpec = getMojo( "generate-openapi-spec-pom-invalid-format",
            "generateOpenApiSpec" );
      assertThatCode( generateOpenApiSpec::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Invalid output format." );
      assertThat( generatedFilePath( "Aspect.oai.json" ) ).doesNotExist();
   }
}
