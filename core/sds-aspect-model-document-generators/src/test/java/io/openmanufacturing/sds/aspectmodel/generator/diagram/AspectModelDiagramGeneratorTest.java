/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.generator.diagram;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;

public class AspectModelDiagramGeneratorTest extends MetaModelVersions {

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testAspectWithRecursivePropertyWithOptional( final KnownVersion metaModelVersion ) throws IOException {
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_RECURSIVE_PROPERTY_WITH_OPTIONAL,
            metaModelVersion );
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      context.service().generateDiagram( AspectModelDiagramGenerator.Format.DOT, Locale.ENGLISH, outStream );
      final String result = outStream.toString( StandardCharsets.UTF_8.name() );

      // Aspect Node
      assertThat( result ).containsOnlyOnce( "testPropertyProperty [label=\"{ «Property»\\ntestProperty|}\"]" );
      assertThat( result ).containsOnlyOnce( "TestEntityEntity [label=\"{ «Entity»\\nTestEntity|}\"]" );
      assertThat( result ).containsOnlyOnce(
            "testItemCharacteristicCharacteristic [label=\"{ «Characteristic»\\ntestItemCharacteristic|}\"]" );
      assertThat( result ).containsOnlyOnce(
            "AspectWithRecursivePropertyWithOptionalAspect [label=\"{ «Aspect»\\nAspectWithRecursivePropertyWithOptional|}\"]" );
      assertThat( result ).containsOnlyOnce(
            "testPropertyProperty -> testItemCharacteristicCharacteristic [label=\"characteristic\"]" );
      assertThat( result )
            .containsOnlyOnce( "testItemCharacteristicCharacteristic -> TestEntityEntity [label=\"dataType\"]" );
      assertThat( result )
            .containsOnlyOnce( "TestEntityEntity -> testPropertyProperty [label=\"property (optional)\"]" );
      assertThat( result ).containsOnlyOnce(
            "AspectWithRecursivePropertyWithOptionalAspect -> testPropertyProperty [label=\"property\"]" );
   }

   @Test
   void testNoStaticFunctionRegistryIsUsed() {
      // the calculation of synthetic names (which involves function registry) is only done when blank nodes are present in the model
      final TestContext context = new TestContext( TestAspect.ASPECT_WITH_BLANK_NODE, KnownVersion.BAMM_2_0_0 );
      // if static (global) registry was used, this second context would overwrite it with a different model
      final TestContext context2 = new TestContext( TestAspect.ASPECT_WITH_BLANK_NODE, KnownVersion.BAMM_2_0_0 );
      assertDoesNotThrow( () -> context.service().generateDiagrams( AspectModelDiagramGenerator.Format.SVG, ( path ) -> new ByteArrayOutputStream() ) );
   }
}
