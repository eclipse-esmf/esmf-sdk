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

package org.eclipse.esmf.aspectmodel.serializer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class PrettyPrinterTest {
   @ParameterizedTest
   @EnumSource( value = TestAspect.class, mode = EnumSource.Mode.EXCLUDE, names = {
         // contains blank nodes which are not referenced from an aspect and therefore not pretty-printed
         "MODEL_WITH_BLANK_AND_ADDITIONAL_NODES"
   } )
   void testPrettyPrinter( final TestAspect testAspect ) {
      final AspectModel aspectModel = TestResources.load( testAspect );
      final AspectModelFile originalFile = aspectModel.files().iterator().next();

      final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      final PrintWriter writer = new PrintWriter( buffer, false, StandardCharsets.UTF_8 );
      new PrettyPrinter( originalFile, writer ).print();
      writer.flush();

      final InputStream bufferInput = new ByteArrayInputStream( buffer.toByteArray() );
      final Model prettyPrintedModel = TurtleLoader.loadTurtle( buffer.toString( StandardCharsets.UTF_8 ) ).get();

      assertThat( RdfComparison.hash( originalFile.sourceModel() ).equals( RdfComparison.hash( prettyPrintedModel ) ) ).isTrue();
   }
}
