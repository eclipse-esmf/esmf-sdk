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

import java.nio.charset.StandardCharsets;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

public class AspectModelDiagramGeneratorTest {
   @ParameterizedTest
   @Execution( ExecutionMode.CONCURRENT )
   @EnumSource( value = TestAspect.class )
   void testGen( final TestAspect testAspect ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      final AspectModelDiagramGenerator generator = new AspectModelDiagramGenerator( aspect );
      assertThatCode( () -> {
         assertThat( generator.getContent() ).isNotEmpty();
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
            final byte[] out = generator.getContent();
            final String svg = new String( out, StandardCharsets.UTF_8 );
            assertThat( svg ).contains( "«Aspect»" );
            assertThat( out ).containsSubsequence( "«Aspect»".getBytes( StandardCharsets.UTF_8 ) );
         } ).doesNotThrowAnyException();
      } finally {
         System.setProperty( "file.encoding", platformEncoding );
      }
   }

   @Test
   void testGenerateDiagramsInMultipleLanguages() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT_WITH_ENGLISH_AND_GERMAN_DESCRIPTION );
      final AspectModelDiagramGenerator generator = new AspectModelDiagramGenerator(
            aspectModel.aspect(), DiagramGenerationConfigBuilder.builder().format( DiagramGenerationConfig.Format.SVG ).build() );
      assertThat( generator.generate().toList() ).size().isEqualTo( 2 );
   }
}
