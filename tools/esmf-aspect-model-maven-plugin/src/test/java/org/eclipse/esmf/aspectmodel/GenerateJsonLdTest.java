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
import org.junit.Test;

@SuppressWarnings( "JUnitMixedFramework" )
public class GenerateJsonLdTest extends AspectModelMojoTest {
   @Test
   public void testGenerateJsonLdValidAspectModel() throws Exception {
      final Mojo generateJsonLd = getMojo( "generate-jsonld-spec-json-pom-to-file", "generateJsonLd" );
      assertThatCode( generateJsonLd::execute ).doesNotThrowAnyException();
      assertThat( generatedFilePath( "AspectWithEvent.json" ) ).exists();
   }
}
