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
public class GenerateDiagramTest extends AspectModelMojoTest {
   @Test
   public void testGenerateDiagramsValidAspectModel() throws Exception {
      final Mojo generateDiagram = getMojo( "generate-diagram-pom-valid-aspect-model", "generateDiagram" );
      assertThatCode( generateDiagram::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "Aspect_en.svg" ) ).exists();
      assertThat( generatedFilePath( "Aspect_en.png" ) ).exists();
   }

   @Test
   public void testGenerateDiagramsInvalidTargetFormat() throws Exception {
      final Mojo generateDiagram = getMojo( "generate-diagram-pom-invalid-target-format", "generateDiagram" );
      assertThatCode( generateDiagram::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Invalid target format: jpg. Valid formats are SVG, PNG." );
   }
}
