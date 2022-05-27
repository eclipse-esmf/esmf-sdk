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

package io.openmanufacturing.sds.aspectmodel.generator.docu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.test.MetaModelVersions;
import io.openmanufacturing.sds.test.TestAspect;
import io.openmanufacturing.sds.test.TestResources;

public class AspectModelDocumentationGeneratorTest extends MetaModelVersions {

   @ParameterizedTest
   //   @EnumSource( value = TestAspect.class )
   @EnumSource( value = TestAspect.class, mode = EnumSource.Mode.INCLUDE, names = {
         "ASPECT_WITH_TIME_SERIES"
   } )
   public void testGeneration( final TestAspect testAspect ) {
      assertThatCode( () -> {
         final String html = generateHtmlDocumentation( testAspect, KnownVersion.getLatest() );
         assertThat( html ).doesNotContain( "UnnamedCharacteristic" );

         final File out = new File( "/home/tax6fe/out.html" );
         try {
            final FileOutputStream outputStream = new FileOutputStream( out );
            outputStream.write( html.getBytes( StandardCharsets.UTF_8 ) );
            outputStream.flush();
            outputStream.close();
         } catch ( final IOException e ) {
            throw new RuntimeException( e );
         }
      } ).doesNotThrowAnyException();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testAspectWithEntityCollection( final KnownVersion metaModelVersion ) throws Throwable {
      final File target = new File( "target", "AspectWithEntityCollection.html" );
      final String htmlResult = generateHtmlDocumentation( TestAspect.ASPECT_WITH_ENTITY_COLLECTION, metaModelVersion );

      try ( final BufferedWriter writer = new BufferedWriter( new FileWriter( target ) ) ) {
         writer.write( htmlResult );
         writer.flush();
      }
      assertThat( htmlResult ).isNotEmpty();
      assertThat( htmlResult ).contains( "<h1 id=\"AspectWithEntityCollection\">Aspect Model Test Aspect</h1>" );
      assertThat( htmlResult ).contains( "<h3 id=\"testProperty-property\">Test Property</h3>" );
      assertThat( htmlResult ).contains( "<h5 id=\"entityProperty-property\">Entity Property</h5>" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testAspectWithCollectionOfSimpleType( final KnownVersion metaModelVersion ) throws Throwable {
      final File target = new File( "target", "AspectWithCollectionOfSimpleType.html" );
      final String htmlResult = generateHtmlDocumentation( TestAspect.ASPECT_WITH_COLLECTION_OF_SIMPLE_TYPE, metaModelVersion );

      try ( final BufferedWriter writer = new BufferedWriter( new FileWriter( target ) ) ) {
         writer.write( htmlResult );
         writer.flush();
      }
      assertThat( htmlResult ).isNotEmpty();
      assertThat( htmlResult ).contains( "<h1 id=\"AspectWithCollectionOfSimpleType\">Aspect Model AspectWithCollectionOfSimpleType</h1>" );
      assertThat( htmlResult ).contains( "<h3 id=\"testList-property\">testList</h3>" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testScriptTagIsEscaped( final KnownVersion metaModelVersion ) throws IOException {
      assertThat( generateHtmlDocumentation( TestAspect.ASPECT_WITH_SCRIPT_TAGS, metaModelVersion ) )
            .isNotEmpty()
            .doesNotContain( "<script>alert('Should not be alerted');</script>" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testRubyGemUpdateCommandIsNotExecuted( final KnownVersion metaModelVersion ) throws IOException {
      try ( final ByteArrayOutputStream stdOut = new ByteArrayOutputStream() ) {
         System.setOut( new PrintStream( stdOut ) );
         generateHtmlDocumentation( TestAspect.ASPECT_WITH_RUBY_GEM_UPDATE_COMMAND, metaModelVersion );
         assertThat( stdOut.toString() ).doesNotContain( "gem" );
      }
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testHtmlTagsAreEscaped( final KnownVersion metaModelVersion ) throws IOException {
      assertThat( generateHtmlDocumentation( TestAspect.ASPECT_WITH_HTML_TAGS, metaModelVersion ) )
            .isNotEmpty()
            .doesNotContain( "<img src=xss.png onerror=alert('Boom!')>" )
            .doesNotContain( "<p>inside html tag</p>" )
            .doesNotContain( "Preferred Name <input value=''/><script>alert('Boom!')</script>'/>" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testEncodedTextIsNotDecoded( final KnownVersion metaModelVersion ) throws IOException {
      assertThat( generateHtmlDocumentation( TestAspect.ASPECT_WITH_ENCODED_STRINGS, metaModelVersion ) )
            .doesNotContain( "This is an Aspect with encoded text." )
            .contains( "VGhpcyBpcyBhbiBBc3BlY3Qgd2l0aCBlbmNvZGVkIHRleHQu" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testAspectModelUrnIsDisplayed( final KnownVersion metaModelVersion ) throws IOException {
      assertThat( generateHtmlDocumentation( TestAspect.ASPECT_WITH_HTML_TAGS, metaModelVersion ) )
            .contains( "urn:bamm:io.openmanufacturing.test:1.0.0#AspectWithHTMLTags" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testDocInfosAreDisplayed( final KnownVersion metaModelVersion ) throws IOException {
      assertThat( generateHtmlDocumentation( TestAspect.ASPECT_WITH_HTML_TAGS, metaModelVersion ) )
            .contains( ".toc-list" )
            .contains( "aspect-model-diagram" )
            .contains( "function toggleLicenseDetails()" )
            .contains( "MIT License | https://github.com/anvaka/panzoom/blob/master/LICENSE" )
            .contains( "MIT License | https://github.com/tscanlin/tocbot/blob/master/LICENSE" )
            .contains( "tailwindcss v2.2.7 | MIT License | https://tailwindcss.com" )
            .contains( "enumerable" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testDocumentationIsNotEmptyForModelWithoutLanguageTags( final KnownVersion metaModelVersion ) throws IOException {
      final String aspectWithoutLanguageTags = generateHtmlDocumentation( TestAspect.ASPECT_WITHOUT_LANGUAGE_TAGS, metaModelVersion );
      assertThat( aspectWithoutLanguageTags ).isNotEmpty();
   }

   @ParameterizedTest
   @MethodSource( "versionsStartingWith2_0_0" )
   public void testAspectWithAbstractSingleEntityExpectSuccess( final KnownVersion metaModelVersion ) throws IOException {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY, metaModelVersion );
      assertThat( documentation ).contains( "<h3 id=\"testProperty-property\">testProperty</h3>" );
      assertThat( documentation ).contains( "<h5 id=\"abstractTestProperty-property\">abstractTestProperty</h5>" );
      assertThat( documentation ).contains( "<h5 id=\"entityProperty-property\">entityProperty</h5>" );
   }

   @ParameterizedTest
   @MethodSource( "versionsStartingWith2_0_0" )
   public void testAspectWithAbstractEntityExpectSuccess( final KnownVersion metaModelVersion ) throws IOException {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY, metaModelVersion );
      assertThat( documentation ).contains( "<h3 id=\"testProperty-property\">Test Property</h3>" );
      assertThat( documentation ).contains( "<h5 id=\"abstractTestProperty-property\">abstractTestProperty</h5>" );
      assertThat( documentation ).contains( "<h5 id=\"entityProperty-property\">Entity Property</h5>" );
   }

   @ParameterizedTest
   @MethodSource( "versionsStartingWith2_0_0" )
   public void testAspectWithCollectionWithAbstractEntityExpectSuccess( final KnownVersion metaModelVersion ) throws IOException {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY, metaModelVersion );
      assertThat( documentation ).contains( "<h3 id=\"testProperty-property\">testProperty</h3>" );
      assertThat( documentation ).contains( "<h5 id=\"abstractTestProperty-property\">abstractTestProperty</h5>" );
      assertThat( documentation ).contains( "<h5 id=\"entityProperty-property\">entityProperty</h5>" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testAspectWithQuantifiableWithoutUnit( final KnownVersion metaModelVersion ) throws IOException {
      try ( final ByteArrayOutputStream stdOut = new ByteArrayOutputStream() ) {
         System.setOut( new PrintStream( stdOut ) );
         assertDoesNotThrow( () -> generateHtmlDocumentation( TestAspect.ASPECT_WITH_QUANTIFIABLE_WITHOUT_UNIT, metaModelVersion ) );
      }
   }

   private String generateHtmlDocumentation( final TestAspect model, final KnownVersion testedVersion ) throws IOException {
      final VersionedModel versionedModel = TestResources.getModel( model, testedVersion ).get();
      final AspectModelDocumentationGenerator aspectModelDocumentationGenerator = new AspectModelDocumentationGenerator( versionedModel );

      try ( final ByteArrayOutputStream result = new ByteArrayOutputStream() ) {
         aspectModelDocumentationGenerator.generate( name -> result, Collections.EMPTY_MAP );
         return result.toString( StandardCharsets.UTF_8.name() );
      }
   }
}
