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
import org.junit.jupiter.api.Test;

@MojoTest
public class GenerateJsonLdTest extends AspectModelMojoTest {
   @Test
   @InjectMojo(
         goal = GenerateJsonLd.MAVEN_GOAL,
         pom = "src/test/resources/generate-jsonld-spec-json-pom-to-file/pom.xml"
   )
   public void testGenerateJsonLdValidAspectModel( final GenerateJsonLd generateJsonLd ) {
      assertThatCode( generateJsonLd::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "AspectWithEvent.json" ) ).exists();
   }
}
