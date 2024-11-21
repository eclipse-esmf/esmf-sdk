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

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class AspectModelDocumentationGeneratorTest {
   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   public void testGeneration( final TestAspect testAspect ) {
      assertThatCode( () -> {
         final String html = generateHtmlDocumentation( testAspect );
         assertThat( html ).doesNotContain( "UnnamedCharacteristic" );
         // No unresolved template variables
         assertThat( html ).doesNotContainPattern( "$[a-zA-Z]" );
      } ).doesNotThrowAnyException();
   }

   @Test
   public void testAspectWithEntityCollection() throws Throwable {
      final String htmlResult = generateHtmlDocumentation( TestAspect.ASPECT_WITH_ENTITY_COLLECTION );

      assertThat( htmlResult ).isNotEmpty();
      assertThat( htmlResult ).contains( "<h1 id=\"AspectWithEntityCollection\">Aspect Model Test Aspect</h1>" );
      assertThat( htmlResult ).contains(
            "<h3 id=\"org-eclipse-esmf-test-AspectWithEntityCollection-org-eclipse-esmf-test-testProperty-property\">Test Property</h3>" );
      assertThat( htmlResult ).contains(
            "<h5 id=\"org-eclipse-esmf-test-TestEntity-org-eclipse-esmf-test-entityProperty-property\">Entity Property</h5>" );
   }

   @Test
   public void testAspectWithCollectionOfSimpleType() throws Throwable {
      final String htmlResult = generateHtmlDocumentation( TestAspect.ASPECT_WITH_COLLECTION_OF_SIMPLE_TYPE );

      assertThat( htmlResult ).isNotEmpty();
      assertThat( htmlResult ).contains( "<h1 id=\"AspectWithCollectionOfSimpleType\">Aspect Model AspectWithCollectionOfSimpleType</h1>" );
      assertThat( htmlResult ).contains(
            "<h3 id=\"org-eclipse-esmf-test-AspectWithCollectionOfSimpleType-org-eclipse-esmf-test-testList-property\">testList</h3>" );
      // example value
      assertThat( htmlResult ).containsIgnoringWhitespaces( "<div class=\"w-80\">Example</div><div class=\"w-full\">35</div>" );
   }

   @Test
   public void testScriptTagIsEscaped() throws IOException {
      assertThat( generateHtmlDocumentation( TestAspect.ASPECT_WITH_SCRIPT_TAGS ) )
            .isNotEmpty()
            .doesNotContain( "<script>alert('Should not be alerted');</script>" );
   }

   @Test
   public void testRubyGemUpdateCommandIsNotExecuted() throws IOException {
      try ( final ByteArrayOutputStream stdOut = new ByteArrayOutputStream() ) {
         System.setOut( new PrintStream( stdOut ) );
         generateHtmlDocumentation( TestAspect.ASPECT_WITH_RUBY_GEM_UPDATE_COMMAND );
         assertThat( stdOut.toString() ).doesNotContain( "gem" );
      }
   }

   @Test
   public void testHtmlTagsAreEscaped() throws IOException {
      assertThat( generateHtmlDocumentation( TestAspect.ASPECT_WITH_HTML_TAGS ) )
            .isNotEmpty()
            .doesNotContain( "<img src=xss.png onerror=alert('Boom!')>" )
            .doesNotContain( "<p>inside html tag</p>" )
            .doesNotContain( "Preferred Name <input value=''/><script>alert('Boom!')</script>'/>" );
   }

   @Test
   public void testEncodedTextIsNotDecoded() throws IOException {
      assertThat( generateHtmlDocumentation( TestAspect.ASPECT_WITH_ENCODED_STRINGS ) )
            .doesNotContain( "This is an Aspect with encoded text." )
            .contains( "VGhpcyBpcyBhbiBBc3BlY3Qgd2l0aCBlbmNvZGVkIHRleHQu" );
   }

   @Test
   public void testAspectModelUrnIsDisplayed() throws IOException {
      assertThat( generateHtmlDocumentation( TestAspect.ASPECT_WITH_HTML_TAGS ) )
            .contains( "urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithHtmlTags" );
   }

   @Test
   public void testDocInfosAreDisplayed() throws IOException {
      assertThat( generateHtmlDocumentation( TestAspect.ASPECT_WITH_HTML_TAGS ) )
            .contains( ".toc-list" )
            .contains( "aspect-model-diagram" )
            .contains( "function toggleLicenseDetails()" )
            .contains( "MIT License | https://github.com/anvaka/panzoom/blob/master/LICENSE" )
            .contains( "MIT License | https://github.com/tscanlin/tocbot/blob/master/LICENSE" )
            .contains( "tailwindcss v2.2.7 | MIT License | https://tailwindcss.com" )
            .contains( "enumerable" );
   }

   @Test
   public void testDocumentationIsNotEmptyForModelWithoutLanguageTags() throws IOException {
      final String aspectWithoutLanguageTags = generateHtmlDocumentation( TestAspect.ASPECT_WITHOUT_LANGUAGE_TAGS );
      assertThat( aspectWithoutLanguageTags ).isNotEmpty();
   }

   @Test
   public void testAspectWithAbstractSingleEntityExpectSuccess() throws IOException {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY );
      assertThat( documentation ).contains(
            "<h3 id=\"org-eclipse-esmf-test-AspectWithAbstractSingleEntity-org-eclipse-esmf-test-testProperty-property\">testProperty</h3"
                  + ">" );
      assertThat( documentation ).contains(
            "<h5 id=\"org-eclipse-esmf-test-AbstractTestEntity-org-eclipse-esmf-test-abstractTestProperty-property\">abstractTestProperty"
                  + "</h5>" );
      assertThat( documentation ).contains(
            "<h5 id=\"org-eclipse-esmf-test-ExtendingTestEntity-org-eclipse-esmf-test-entityProperty-property\">entityProperty</h5>" );
   }

   @Test
   public void testAspectWithAbstractEntityExpectSuccess() throws IOException {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY );
      assertThat( documentation ).contains(
            "<h3 id=\"org-eclipse-esmf-test-AspectWithAbstractEntity-org-eclipse-esmf-test-testProperty-property\">Test Property</h3>" );
      assertThat( documentation ).contains(
            "<h5 id=\"org-eclipse-esmf-test-AbstractTestEntity-org-eclipse-esmf-test-abstractTestProperty-property\">abstractTestProperty"
                  + "</h5>" );
      assertThat( documentation ).contains(
            "<h5 id=\"org-eclipse-esmf-test-ExtendingTestEntity-org-eclipse-esmf-test-entityProperty-property\">Entity Property</h5>" );
   }

   @Test
   public void testAspectWithCollectionWithAbstractEntityExpectSuccess() throws IOException {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY );
      assertThat( documentation ).contains(
            "<h3 id=\"org-eclipse-esmf-test-AspectWithCollectionWithAbstractEntity-org-eclipse-esmf-test-testProperty-property"
                  + "\">testProperty</h3>" );
      assertThat( documentation ).contains(
            "<h5 id=\"org-eclipse-esmf-test-AbstractTestEntity-org-eclipse-esmf-test-abstractTestProperty-property\">abstractTestProperty"
                  + "</h5>" );
      assertThat( documentation ).contains(
            "<h5 id=\"org-eclipse-esmf-test-ExtendingTestEntity-org-eclipse-esmf-test-entityProperty-property\">entityProperty</h5>" );
   }

   @Test
   public void testAspectWithQuantifiableWithoutUnit() throws IOException {
      try ( final ByteArrayOutputStream stdOut = new ByteArrayOutputStream() ) {
         System.setOut( new PrintStream( stdOut ) );
         assertThatCode( () -> generateHtmlDocumentation( TestAspect.ASPECT_WITH_QUANTIFIABLE_WITHOUT_UNIT ) )
               .doesNotThrowAnyException();
      }
   }

   @Test
   public void testAspectWithConstraintWithSeeAttribute() throws IOException {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_CONSTRAINT_WITH_SEE_ATTRIBUTE );
      assertThat( documentation ).contains(
            "<h3 id=\"org-eclipse-esmf-test-AspectWithConstraintWithSeeAttribute-org-eclipse-esmf-test-testPropertyTwo-property"
                  + "\">testPropertyTwo</h3>" );
      assertThat( documentation ).contains(
            "<div class=\"table-cell pb-3 col-span-2\">Trait</div>" );
      assertThat( documentation ).contains(
            "<li>http://example.com/me2</li>" );
   }

   private String generateHtmlDocumentation( final TestAspect testAspect ) throws IOException {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      final AspectModelDocumentationGenerator aspectModelDocumentationGenerator = new AspectModelDocumentationGenerator( aspect );
      return aspectModelDocumentationGenerator.getContent();
   }
}
