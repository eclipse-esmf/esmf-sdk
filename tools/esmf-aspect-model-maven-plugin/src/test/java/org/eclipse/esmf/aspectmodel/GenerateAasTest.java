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
public class GenerateAasTest extends AspectModelMojoTest {
   @Test
   @InjectMojo(
         goal = GenerateAas.MAVEN_GOAL,
         pom = "src/test/resources/generate-aas-xml-pom-valid-aspect-model/pom.xml"
   )
   public void testGenerateAasXmlValidAspectModel( final GenerateAas generateAas ) {
      assertThatCode( generateAas::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect.xml" ) ).exists();
   }

   @Test
   @InjectMojo(
         goal = GenerateAas.MAVEN_GOAL,
         pom = "src/test/resources/generate-aas-json-pom-valid-aspect-model/pom.xml"
   )
   public void testGenerateAasJsonValidAspectModel( final GenerateAas generateAas ) {
      assertThatCode( generateAas::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect.json" ) ).exists();
   }

   @Test
   @InjectMojo(
         goal = GenerateAas.MAVEN_GOAL,
         pom = "src/test/resources/generate-aas-aasx-pom-valid-aspect-model/pom.xml"
   )
   public void testGenerateAasAasxValidAspectModel( final GenerateAas generateAas ) {
      assertThatCode( generateAas::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect.aasx" ) ).exists();
   }

   @Test
   @InjectMojo(
         goal = GenerateAas.MAVEN_GOAL,
         pom = "src/test/resources/generate-aas-pom-invalid-target-format/pom.xml"
   )
   public void testGenerateAasInvalidTargetFormat( final GenerateAas generateAas ) {
      assertThatCode( generateAas::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Invalid target format: html. Valid formats are aasx, xml, json." );
   }
}
