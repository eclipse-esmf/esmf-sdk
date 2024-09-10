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

import java.io.File;

import org.apache.maven.plugin.Mojo;
import org.junit.Test;

public class GenerateJsonLdTest extends AspectModelMojoTest {

   @Test
   public void testGenerateAsyncApiSpecJsonValidAspectModel() throws Exception {
      final File testPom = getTestFile( "generate-jsonld-spec-json-pom-to-file.xml" );
      final Mojo generateAsyncApiSpec = lookupMojo( "generateJsonLd", testPom );
      assertThatCode( generateAsyncApiSpec::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "AspectWithEvent.jsonld" ) ).exists();
   }
}
