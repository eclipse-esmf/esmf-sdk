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
public class GenerateStaticJavaClassesTest extends AspectModelMojoTest {
   @Test
   @InjectMojo(
         goal = GenerateStaticJavaClasses.MAVEN_GOAL,
         pom = "src/test/resources/test-pom-valid-aspect-model-output-directory/pom.xml"
   )
   public void testGenerateJavaClassesValidAspectModel( final GenerateStaticJavaClasses generateStaticJavaClasses ) {
      assertThatCode( generateStaticJavaClasses::execute ).doesNotThrowAnyException();
      final String packagePath = "org/eclipse/esmf/test";
      assertThat( generatedFilePath( packagePath, "MetaAspect.java" ) ).exists();
   }

   @Test
   @InjectMojo(
         goal = GenerateStaticJavaClasses.MAVEN_GOAL,
         pom = "src/test/resources/generate-static-java-classes-pom-custom-package-name/pom.xml"
   )
   public void testGenerateJavaClassesCustomPackageName( final GenerateStaticJavaClasses generateStaticJavaClasses ) {
      assertThatCode( generateStaticJavaClasses::execute ).doesNotThrowAnyException();
      final String packagePath = "example/com";
      assertThat( generatedFilePath( packagePath, "MetaAspect.java" ) ).exists();
   }

   @Test
   @InjectMojo(
         goal = GenerateStaticJavaClasses.MAVEN_GOAL,
         pom = "src/test/resources/generate-static-java-classes-pom-package-interpolation/pom.xml"
   )
   public void testGenerateJavaClassesPackageInterpolation( final GenerateStaticJavaClasses generateStaticJavaClasses ) {
      assertThatCode( generateStaticJavaClasses::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "com", "example", "shared", "v1", "v0", "v0", "MetaAspectWithExtendedEntity.java" ) ).exists();
      assertThat( generatedFilePath( "com", "example", "v1", "v0", "v0", "MetaAspect.java" ) ).exists();
   }

   @Test
   @InjectMojo(
         goal = GenerateStaticJavaClasses.MAVEN_GOAL,
         pom = "src/test/resources/generate-static-java-classes-pom-invalid-template-lib-file/pom.xml"
   )
   public void testGenerateJavaClassesInvalidTemplateLibFile( final GenerateStaticJavaClasses generateStaticJavaClasses ) {
      assertThatCode( generateStaticJavaClasses::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Missing configuration. Valid path to velocity template library file must be provided." );
   }
}
