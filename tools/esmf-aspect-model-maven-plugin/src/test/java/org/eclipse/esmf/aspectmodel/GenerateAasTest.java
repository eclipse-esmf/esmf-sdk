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

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

@SuppressWarnings( "JUnitMixedFramework" )
public class GenerateAasTest extends AspectModelMojoTest {
   @Test
   public void testGenerateAasXmlValidAspectModel() throws Exception {
      final Mojo generateAas = getMojo( "generate-aas-xml-pom-valid-aspect-model", "generateAas" );
      assertThatCode( generateAas::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect.xml" ) ).exists();
   }

   @Test
   public void testGenerateAasJsonValidAspectModel() throws Exception {
      final Mojo generateAas = getMojo( "generate-aas-json-pom-valid-aspect-model", "generateAas" );
      assertThatCode( generateAas::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect.json" ) ).exists();
   }

   @Test
   public void testGenerateAasAasxValidAspectModel() throws Exception {
      final Mojo generateAas = getMojo( "generate-aas-aasx-pom-valid-aspect-model", "generateAas" );
      assertThatCode( generateAas::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect.aasx" ) ).exists();
   }

   @Test
   public void testGenerateAasInvalidTargetFormat() throws Exception {
      final Mojo generateAas = getMojo( "generate-aas-pom-invalid-target-format", "generateAas" );
      assertThatCode( generateAas::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Invalid target format: html. Valid formats are aasx, xml, json." );
   }
}
