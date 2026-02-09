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
public class GenerateJavaClassesTest extends AspectModelMojoTest {
   @Test
   @InjectMojo(
         goal = GenerateJavaClasses.MAVEN_GOAL,
         pom = "src/test/resources/test-pom-valid-aspect-model-output-directory/pom.xml"
   )
   public void testGenerateJavaClassesValidAspectModel( final GenerateJavaClasses generateJavaClasses ) {
      assertThatCode( generateJavaClasses::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "org", "eclipse", "esmf", "test", "Aspect.java" ) ).exists();
   }

   @Test
   @InjectMojo(
         goal = GenerateJavaClasses.MAVEN_GOAL,
         pom = "src/test/resources/generate-java-classes-pom-custom-package-name/pom.xml"
   )
   public void testGenerateJavaClassesCustomPackageName( final GenerateJavaClasses generateJavaClasses ) {
      assertThatCode( generateJavaClasses::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "example", "com", "Aspect.java" ) ).exists();
   }

   @Test
   @InjectMojo(
         goal = GenerateJavaClasses.MAVEN_GOAL,
         pom = "src/test/resources/generate-java-classes-pom-package-interpolation/pom.xml"
   )
   public void testGenerateJavaClassesPackageInterpolation( final GenerateJavaClasses generateJavaClasses ) {
      assertThatCode( generateJavaClasses::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "com", "example", "shared", "v1", "v0", "v0", "AspectWithExtendedEntity.java" ) ).exists();
      assertThat( generatedFilePath( "com", "example", "v1", "v0", "v0", "Aspect.java" ) ).exists();
   }

   @Test
   @InjectMojo(
         goal = GenerateJavaClasses.MAVEN_GOAL,
         pom = "src/test/resources/generate-java-classes-pom-invalid-template-lib-file/pom.xml"
   )
   public void testGenerateJavaClassesInvalidTemplateLibFile( final GenerateJavaClasses generateJavaClasses ) {
      assertThatCode( generateJavaClasses::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Missing configuration. Valid path to velocity template library file must be provided." );
   }

   @Test
   @InjectMojo(
         goal = GenerateJavaClasses.MAVEN_GOAL,
         pom = "src/test/resources/test-skip-plugin-execution/pom.xml"
   )
   public void testSkipPluginExecution( final GenerateJavaClasses generateJavaClasses ) {
      assertThatCode( generateJavaClasses::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "org", "eclipse", "esmf", "test", "Aspect.java" ) ).doesNotExist();
   }

   @Test
   @InjectMojo(
         goal = GenerateJavaClasses.MAVEN_GOAL,
         pom = "src/test/resources/generate-java-classes-pom-with-prefix-and-postfix/pom.xml"
   )
   public void testGenerateJavaClassesAspectWithPrefixAndPostfix( final GenerateJavaClasses generateJavaClasses ) {
      assertThatCode( generateJavaClasses::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "example", "com", "BaseAspectPostfix.java" ) ).exists();
   }

   @Test
   @InjectMojo(
         goal = GenerateJavaClasses.MAVEN_GOAL,
         pom = "src/test/resources/generate-java-classes-pom-entity-with-prefix-and-postfix/pom.xml"
   )
   public void testGenerateJavaClassesEntityWithPrefixAndPostfix( final GenerateJavaClasses generateJavaClasses ) {
      assertThatCode( generateJavaClasses::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "example", "com", "BaseAspectWithEntityPostfix.java" ) ).exists();
      assertThat( generatedFilePath( "example", "com", "BaseTestEntityPostfix.java" ) ).exists();
   }
}
