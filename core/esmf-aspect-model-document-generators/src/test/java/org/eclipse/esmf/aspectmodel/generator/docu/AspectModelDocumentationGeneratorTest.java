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

package org.eclipse.esmf.aspectmodel.generator.docu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.test.MetaModelVersions;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

public class AspectModelDocumentationGeneratorTest extends MetaModelVersions {

   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   public void testGeneration( final TestAspect testAspect ) {
      assertThatCode( () -> {
         final String html = generateHtmlDocumentation( testAspect, KnownVersion.getLatest() );
         assertThat( html ).doesNotContain( "UnnamedCharacteristic" );
         // No unresolved template variables
         assertThat( html ).doesNotContainPattern( "$[a-zA-Z]" );
      } ).doesNotThrowAnyException();
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testAspectWithEntityCollection( final KnownVersion metaModelVersion ) throws Throwable {
      final String htmlResult = generateHtmlDocumentation( TestAspect.ASPECT_WITH_ENTITY_COLLECTION, metaModelVersion );

      assertThat( htmlResult ).isNotEmpty();
      assertThat( htmlResult ).contains( "<h1 id=\"AspectWithEntityCollection\">Aspect Model Test Aspect</h1>" );
      assertThat( htmlResult ).contains(
            "<h3 id=\"org-eclipse-esmf-test-AspectWithEntityCollection-org-eclipse-esmf-test-testProperty-property\">Test Property</h3>" );
      assertThat( htmlResult ).contains(
            "<h5 id=\"org-eclipse-esmf-test-TestEntity-org-eclipse-esmf-test-entityProperty-property\">Entity Property</h5>" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testAspectWithCollectionOfSimpleType( final KnownVersion metaModelVersion ) throws Throwable {
      final String htmlResult = generateHtmlDocumentation( TestAspect.ASPECT_WITH_COLLECTION_OF_SIMPLE_TYPE, metaModelVersion );

      assertThat( htmlResult ).isNotEmpty();
      assertThat( htmlResult ).contains( "<h1 id=\"AspectWithCollectionOfSimpleType\">Aspect Model AspectWithCollectionOfSimpleType</h1>" );
      assertThat( htmlResult ).contains(
            "<h3 id=\"org-eclipse-esmf-test-AspectWithCollectionOfSimpleType-org-eclipse-esmf-test-testList-property\">testList</h3>" );
      // example value
      assertThat( htmlResult ).containsIgnoringWhitespaces( "<div class=\"w-80\">Example</div><div class=\"w-full\">35</div>" );
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
            .contains( "urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithHtmlTags" );
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
      assertThat( documentation ).contains(
            "<h3 id=\"org-eclipse-esmf-test-AspectWithAbstractSingleEntity-org-eclipse-esmf-test-testProperty-property\">testProperty</h3"
                  + ">" );
      assertThat( documentation ).contains(
            "<h5 id=\"org-eclipse-esmf-test-AbstractTestEntity-org-eclipse-esmf-test-abstractTestProperty-property\">abstractTestProperty"
                  + "</h5>" );
      assertThat( documentation ).contains(
            "<h5 id=\"org-eclipse-esmf-test-ExtendingTestEntity-org-eclipse-esmf-test-entityProperty-property\">entityProperty</h5>" );
   }

   @ParameterizedTest
   @MethodSource( "versionsStartingWith2_0_0" )
   public void testAspectWithAbstractEntityExpectSuccess( final KnownVersion metaModelVersion ) throws IOException {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY, metaModelVersion );
      assertThat( documentation ).contains(
            "<h3 id=\"org-eclipse-esmf-test-AspectWithAbstractEntity-org-eclipse-esmf-test-testProperty-property\">Test Property</h3>" );
      assertThat( documentation ).contains(
            "<h5 id=\"org-eclipse-esmf-test-AbstractTestEntity-org-eclipse-esmf-test-abstractTestProperty-property\">abstractTestProperty"
                  + "</h5>" );
      assertThat( documentation ).contains(
            "<h5 id=\"org-eclipse-esmf-test-ExtendingTestEntity-org-eclipse-esmf-test-entityProperty-property\">Entity Property</h5>" );
   }

   @ParameterizedTest
   @MethodSource( "versionsStartingWith2_0_0" )
   public void testAspectWithCollectionWithAbstractEntityExpectSuccess( final KnownVersion metaModelVersion ) throws IOException {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY, metaModelVersion );
      assertThat( documentation ).contains(
            "<h3 id=\"org-eclipse-esmf-test-AspectWithCollectionWithAbstractEntity-org-eclipse-esmf-test-testProperty-property"
                  + "\">testProperty</h3>" );
      assertThat( documentation ).contains(
            "<h5 id=\"org-eclipse-esmf-test-AbstractTestEntity-org-eclipse-esmf-test-abstractTestProperty-property\">abstractTestProperty"
                  + "</h5>" );
      assertThat( documentation ).contains(
            "<h5 id=\"org-eclipse-esmf-test-ExtendingTestEntity-org-eclipse-esmf-test-entityProperty-property\">entityProperty</h5>" );
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testAspectWithQuantifiableWithoutUnit( final KnownVersion metaModelVersion ) throws IOException {
      try ( final ByteArrayOutputStream stdOut = new ByteArrayOutputStream() ) {
         System.setOut( new PrintStream( stdOut ) );
         assertThatCode( () -> generateHtmlDocumentation( TestAspect.ASPECT_WITH_QUANTIFIABLE_WITHOUT_UNIT, metaModelVersion ) )
               .doesNotThrowAnyException();
      }
   }

   @ParameterizedTest
   @MethodSource( "allVersions" )
   public void testAspectWithConstraintWithSeeAttribute( final KnownVersion metaModelVersion ) throws IOException {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_CONSTRAINT_WITH_SEE_ATTRIBUTE, metaModelVersion );
      assertThat( documentation ).contains(
            "<h3 id=\"org-eclipse-esmf-test-AspectWithConstraintWithSeeAttribute-org-eclipse-esmf-test-testPropertyTwo-property"
                  + "\">testPropertyTwo</h3>" );
      assertThat( documentation ).contains(
            "<div class=\"table-cell pb-3 col-span-2\">Trait</div>" );
      assertThat( documentation ).contains(
            "<li>http://example.com/me2</li>" );
   }

   private String generateHtmlDocumentation( final TestAspect model, final KnownVersion testedVersion ) throws IOException {
      final VersionedModel versionedModel = TestResources.getModel( model, testedVersion ).get();
      final Aspect aspect = AspectModelLoader.getSingleAspect( versionedModel ).getOrElseThrow( () -> new RuntimeException() );
      final AspectModelDocumentationGenerator aspectModelDocumentationGenerator = new AspectModelDocumentationGenerator( aspect );

      try ( final ByteArrayOutputStream result = new ByteArrayOutputStream() ) {
         aspectModelDocumentationGenerator.generate( name -> result, Map.of() );
         return result.toString( StandardCharsets.UTF_8 );
      }
   }
}
