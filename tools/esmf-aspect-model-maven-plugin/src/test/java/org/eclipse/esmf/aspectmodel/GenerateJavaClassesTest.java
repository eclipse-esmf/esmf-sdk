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

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

@SuppressWarnings( "JUnitMixedFramework" )
public class GenerateJavaClassesTest extends AspectModelMojoTest {
   @Test
   public void testGenerateJavaClassesValidAspectModel() throws Exception {
      final Mojo generateJavaClasses = getMojo( "test-pom-valid-aspect-model-output-directory", "generateJavaClasses" );
      assertThatCode( generateJavaClasses::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "org", "eclipse", "esmf", "test", "Aspect.java" ) ).exists();
   }

   @Test
   public void testGenerateJavaClassesCustomPackageName() throws Exception {
      final Mojo generateJavaClasses = getMojo( "generate-java-classes-pom-custom-package-name", "generateJavaClasses" );
      assertThatCode( generateJavaClasses::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "example", "com", "Aspect.java" ) ).exists();
   }

   @Test
   public void testGenerateJavaClassesPackageInterpolation() throws Exception {
      final Mojo generateJavaClasses = getMojo( "generate-java-classes-pom-package-interpolation", "generateJavaClasses" );
      assertThatCode( generateJavaClasses::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "com", "example", "shared", "v1", "v0", "v0", "AspectWithExtendedEntity.java" ) ).exists();
      assertThat( generatedFilePath( "com", "example", "v1", "v0", "v0", "Aspect.java" ) ).exists();
   }

   @Test
   public void testGenerateJavaClassesInvalidTemplateLibFile() throws Exception {
      final Mojo generateJavaClasses = getMojo( "generate-java-classes-pom-invalid-template-lib-file", "generateJavaClasses" );
      assertThatCode( generateJavaClasses::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Missing configuration. Valid path to velocity template library file must be provided." );
   }

   @Test
   public void testSkipPluginExecution() throws Exception {
      final Mojo generateJavaClasses = getMojo( "test-skip-plugin-execution", "generateJavaClasses" );
      assertThatCode( generateJavaClasses::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "org", "eclipse", "esmf", "test", "Aspect.java" ) ).doesNotExist();
   }

   @Test
   public void testGenerateJavaClassesAspectWithPrefixAndPostfix() throws Exception {
      final Mojo generateJavaClasses = getMojo( "generate-java-classes-pom-with-prefix-and-postfix", "generateJavaClasses" );
      assertThatCode( generateJavaClasses::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "example", "com", "BaseAspectPostfix.java" ) ).exists();
   }

   @Test
   public void testGenerateJavaClassesEntityWithPrefixAndPostfix() throws Exception {
      final Mojo generateJavaClasses = getMojo( "generate-java-classes-pom-entity-with-prefix-and-postfix", "generateJavaClasses" );
      assertThatCode( generateJavaClasses::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "example", "com", "BaseAspectWithEntityPostfix.java" ) ).exists();
      assertThat( generatedFilePath( "example", "com", "BaseTestEntityPostfix.java" ) ).exists();
   }
}
