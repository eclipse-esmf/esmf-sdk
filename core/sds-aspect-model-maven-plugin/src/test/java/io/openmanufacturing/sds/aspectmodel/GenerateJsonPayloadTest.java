/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.File;

import org.apache.maven.plugin.Mojo;
import org.junit.Test;

public class GenerateJsonPayloadTest extends AspectModelMojoTest {

   @Test
   public void testGenerateJsonPayload() throws Exception {
      final File testPom = getTestFile( "src/test/resources/generate-json-payload-pom-valid-aspect-model.xml" );
      final Mojo generateJsonPayload = lookupMojo( "generateJsonPayload", testPom );
      assertThatCode( generateJsonPayload::execute ).doesNotThrowAnyException();

      assertGeneratedFileExists( "Aspect.json" );
      deleteGeneratedFile( "Aspect.json" );
   }
}
