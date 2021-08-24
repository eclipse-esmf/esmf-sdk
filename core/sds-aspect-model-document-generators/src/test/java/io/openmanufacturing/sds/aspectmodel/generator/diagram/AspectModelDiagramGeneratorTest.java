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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import com.google.common.collect.ImmutableList;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.RDF;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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
}
