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

package org.eclipse.esmf.aspectmodel.generator.diagram;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

public class AspectModelDiagramGeneratorTest extends MetaModelVersions {
   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   void testGen( final TestAspect testAspect ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      final AspectModelDiagramGenerator generator = new AspectModelDiagramGenerator( aspect );
      assertThatCode( () -> {
         final ByteArrayOutputStream out = new ByteArrayOutputStream();
         generator.generateDiagram( AspectModelDiagramGenerator.Format.SVG, Locale.ENGLISH, out );
      } ).doesNotThrowAnyException();
   }

   @ParameterizedTest
   @ValueSource( strings = { "UTF-8", "US-ASCII", "Windows-1252" } )
   void generateDiagramsShouldReturnUtf8StringRegardlessOfPlatformEncoding( final String encoding ) {
      final String platformEncoding = System.getProperty( "file.encoding" );
      try {
         System.setProperty( "file.encoding", encoding );
         final Aspect aspect = TestResources.load( TestAspect.ASPECT ).aspect();
         final AspectModelDiagramGenerator generator = new AspectModelDiagramGenerator( aspect );

         assertThatCode( () -> {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            generator.generateDiagram( AspectModelDiagramGenerator.Format.SVG, Locale.ENGLISH, out );
            final String svg = out.toString( StandardCharsets.UTF_8 );
            assertThat( svg ).contains( "«Aspect»" );
            assertThat( out.toByteArray() ).containsSubsequence( "«Aspect»".getBytes( StandardCharsets.UTF_8 ) );
         } ).doesNotThrowAnyException();
      } finally {
         System.setProperty( "file.encoding", platformEncoding );
      }
   }
}
