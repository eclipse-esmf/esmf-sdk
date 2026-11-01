/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;

/**
 * Tests Maven configuration, schema output and failure handling for Protobuf generation.
 */
@MojoTest
public class GenerateProtobufSchemaTest extends AspectModelMojoTest {
   @Test
   @InjectMojo(
      goal = GenerateProtobufSchema.MAVEN_GOAL,
      pom = "src/test/resources/test-pom-valid-aspect-model-output-directory/pom.xml" )
   void generatesSchema( final GenerateProtobufSchema mojo ) throws Exception {
      mojo.execute();
      assertThat( generatedFilePath( "Aspect.proto" ) ).content()
            .isEqualTo( "syntax = \"proto3\";\n\npackage org.eclipse.esmf.test.v1_0_0;\n\nmessage Aspect {\n}\n" );
   }

   @Test
   @InjectMojo(
      goal = GenerateProtobufSchema.MAVEN_GOAL,
      pom = "src/test/resources/test-pom-valid-aspect-model-output-directory/pom.xml" )
   void generatesMultipleSchemasInMissingOutputDirectory( final GenerateProtobufSchema mojo ) throws Exception {
      mojo.includes = Set.of( "urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithEntity",
            "urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollectionOfSimpleType" );
      mojo.outputDirectory = generatedFilePath( "nested", "schemas" ).toString();
      mojo.execute();
      assertThat( generatedFilePath( "nested", "schemas", "AspectWithEntity.proto" ) ).content().isEqualTo( """
         syntax = "proto3";

         package org.eclipse.esmf.test.v1_0_0;

         message AspectWithEntity {
           .org.eclipse.esmf.test.v1_0_0.TestEntity testProperty = 1;
         }

         message TestEntity {
           string entityProperty = 1;
         }
         """ );
      assertThat( generatedFilePath( "nested", "schemas", "AspectWithCollectionOfSimpleType.proto" ) ).content()
            .contains( "message AspectWithCollectionOfSimpleType {", "repeated int32 testList = 1;" );
   }

   @Test
   @InjectMojo(
      goal = GenerateProtobufSchema.MAVEN_GOAL,
      pom = "src/test/resources/test-pom-valid-aspect-model-output-directory/pom.xml" )
   void rejectsMissingIncludes( final GenerateProtobufSchema mojo ) {
      mojo.includes = Set.of();
      assertThatThrownBy( mojo::execute ).isInstanceOf( MojoExecutionException.class )
            .hasMessageContaining( "Please provide Aspect Models" );
      assertThat( generatedFilePath( "Aspect.proto" ) ).doesNotExist();
   }

   @Test
   @InjectMojo(
      goal = GenerateProtobufSchema.MAVEN_GOAL,
      pom = "src/test/resources/prettyprint-pom-invalid-aspect-model/pom.xml" )
   void rejectsInvalidModel( final GenerateProtobufSchema mojo ) {
      assertThatThrownBy( mojo::execute ).isInstanceOf( MojoExecutionException.class );
      assertThat( generatedFilePath( "InvalidSyntax.proto" ) ).doesNotExist();
   }

   @Test
   @InjectMojo(
      goal = GenerateProtobufSchema.MAVEN_GOAL,
      pom = "src/test/resources/test-pom-valid-aspect-model-output-directory/pom.xml" )
   void reportsUnwritableOutput( final GenerateProtobufSchema mojo ) throws IOException {
      Files.createDirectory( generatedFilePath( "Aspect.proto" ) );
      assertThatThrownBy( mojo::execute ).isInstanceOf( MojoExecutionException.class ).hasMessageContaining( "Could not write" );
   }
}
