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

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AspectModelDocumentationGeneratorTest {
   @ParameterizedTest
   @Execution( ExecutionMode.CONCURRENT )
   @EnumSource( value = TestAspect.class )
   void testGeneration( final TestAspect testAspect ) {
      assertThatCode( () -> {
         final String html = generateHtmlDocumentation( testAspect );
         assertThat( html ).doesNotContain( "UnnamedCharacteristic" );
         // No unresolved template variables
         assertThat( html ).doesNotContainPattern( "$[a-zA-Z]" );
      } ).doesNotThrowAnyException();
   }

   @Test
   void testAspectWithEntityCollection() throws Throwable {
      final String htmlResult = generateHtmlDocumentation( TestAspect.ASPECT_WITH_ENTITY_COLLECTION );

      assertThat( htmlResult )
            .isNotEmpty()
            .contains( "<h1 id=\"AspectWithEntityCollection\">Aspect Model Test Aspect</h1>" )
            .contains(
                  "<h3 id=\"org-eclipse-esmf-test-AspectWithEntityCollection-org-eclipse-esmf-test-testProperty-property\">Test "
                        + "Property</h3>" )
            .contains(
                  "<h5 id=\"org-eclipse-esmf-test-TestEntity-org-eclipse-esmf-test-entityProperty-property\">Entity Property</h5>" );
   }

   @Test
   void testAspectWithCollectionOfSimpleType() throws Throwable {
      final String htmlResult = generateHtmlDocumentation( TestAspect.ASPECT_WITH_COLLECTION_OF_SIMPLE_TYPE );

      assertThat( htmlResult )
            .isNotEmpty()
            .contains( "<h1 id=\"AspectWithCollectionOfSimpleType\">Aspect Model AspectWithCollectionOfSimpleType</h1>" )
            .contains(
                  "<h3 id=\"org-eclipse-esmf-test-AspectWithCollectionOfSimpleType-org-eclipse-esmf-test-testList-property\">testList</h3"
                        + ">" )
            .containsIgnoringWhitespaces( "<div class=\"w-80\">Characteristic</div>" );
   }

   @Test
   void testScriptTagIsEscaped() {
      assertThat( generateHtmlDocumentation( TestAspect.ASPECT_WITH_SCRIPT_TAGS ) )
            .isNotEmpty()
            .doesNotContain( "Test preferred name with script: <script>alert('Should not be alerted');</script>" );
   }

   @Test
   void testRubyGemUpdateCommandIsNotExecuted() throws IOException {
      try ( final ByteArrayOutputStream stdOut = new ByteArrayOutputStream() ) {
         System.setOut( new PrintStream( stdOut ) );
         generateHtmlDocumentation( TestAspect.ASPECT_WITH_RUBY_GEM_UPDATE_COMMAND );
         assertThat( stdOut.toString() ).doesNotContain( "gem" );
      }
   }

   @Test
   void testHtmlTagsAreEscaped() {
      assertThat( generateHtmlDocumentation( TestAspect.ASPECT_WITH_HTML_TAGS ) )
            .isNotEmpty()
            .doesNotContain( "<img src=xss.png onerror=alert('Boom!')>" )
            .doesNotContain( "<p>inside html tag</p>" )
            .doesNotContain( "Preferred Name <input value=''/><script>alert('Boom!')</script>'/>" );
   }

   @Test
   void testEncodedTextIsNotDecoded() {
      assertThat( generateHtmlDocumentation( TestAspect.ASPECT_WITH_ENCODED_STRINGS ) )
            .doesNotContain( "This is an Aspect with encoded text." )
            .contains( "VGhpcyBpcyBhbiBBc3BlY3Qgd2l0aCBlbmNvZGVkIHRleHQu" );
   }

   @Test
   void testAspectModelUrnIsDisplayed() {
      assertThat( generateHtmlDocumentation( TestAspect.ASPECT_WITH_HTML_TAGS ) )
            .contains( "urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithHtmlTags" );
   }

   @Test
   void testDocInfosAreDisplayed() {
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
   void testDocumentationIsNotEmptyForModelWithoutLanguageTags() {
      final String aspectWithoutLanguageTags = generateHtmlDocumentation( TestAspect.ASPECT_WITHOUT_LANGUAGE_TAGS );
      assertThat( aspectWithoutLanguageTags ).isNotEmpty();
   }

   @Test
   void testAspectWithAbstractSingleEntityExpectSuccess() {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_ABSTRACT_SINGLE_ENTITY );
      assertThat( documentation )
            .contains(
                  "<h3 id=\"org-eclipse-esmf-test-AspectWithAbstractSingleEntity-org-eclipse-esmf-test-testProperty-property"
                        + "\">testProperty</h3"
                        + ">" )
            .contains(
                  "<h5 id=\"org-eclipse-esmf-test-AbstractTestEntity-org-eclipse-esmf-test-abstractTestProperty-property"
                        + "\">abstractTestProperty"
                        + "</h5>" )
            .contains(
                  "<h5 id=\"org-eclipse-esmf-test-ExtendingTestEntity-org-eclipse-esmf-test-entityProperty-property\">entityProperty</h5"
                        + ">" );
   }

   @Test
   void testAspectWithAbstractEntityExpectSuccess() {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_ABSTRACT_ENTITY );
      assertThat( documentation )
            .contains(
                  "<h3 id=\"org-eclipse-esmf-test-AspectWithAbstractEntity-org-eclipse-esmf-test-testProperty-property\">Test "
                        + "Property</h3>" )
            .contains(
                  "<h5 id=\"org-eclipse-esmf-test-AbstractTestEntity-org-eclipse-esmf-test-abstractTestProperty-property"
                        + "\">abstractTestProperty"
                        + "</h5>" )
            .contains(
                  "<h5 id=\"org-eclipse-esmf-test-ExtendingTestEntity-org-eclipse-esmf-test-entityProperty-property\">Entity "
                        + "Property</h5>" );
   }

   @Test
   void testAspectWithCollectionWithAbstractEntityExpectSuccess() {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_COLLECTION_WITH_ABSTRACT_ENTITY );
      assertThat( documentation )
            .contains(
                  "<h3 id=\"org-eclipse-esmf-test-AspectWithCollectionWithAbstractEntity-org-eclipse-esmf-test-testProperty-property"
                        + "\">testProperty</h3>" )
            .contains(
                  "<h5 id=\"org-eclipse-esmf-test-AbstractTestEntity-org-eclipse-esmf-test-abstractTestProperty-property"
                        + "\">abstractTestProperty"
                        + "</h5>" )
            .contains(
                  "<h5 id=\"org-eclipse-esmf-test-ExtendingTestEntity-org-eclipse-esmf-test-entityProperty-property\">entityProperty</h5"
                        + ">" );
   }

   @Test
   void testAspectWithQuantifiableWithoutUnit() throws IOException {
      try ( final ByteArrayOutputStream stdOut = new ByteArrayOutputStream() ) {
         System.setOut( new PrintStream( stdOut ) );
         assertThatCode( () -> generateHtmlDocumentation( TestAspect.ASPECT_WITH_QUANTIFIABLE_WITHOUT_UNIT ) )
               .doesNotThrowAnyException();
      }
   }

   @Test
   void testAspectWithConstraintWithSeeAttribute() {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_CONSTRAINT_WITH_SEE_ATTRIBUTE );
      assertThat( documentation )
            .contains(
                  "<h3 id=\"org-eclipse-esmf-test-AspectWithConstraintWithSeeAttribute-org-eclipse-esmf-test-testPropertyTwo-property"
                        + "\">testPropertyTwo</h3>" )
            .contains(
                  "<div class=\"table-cell pb-3 col-span-2\">Trait</div>" )
            .contains(
                  "<li>http://example.com/me2</li>" );
   }

   @Test
   void testMarkdownRenderingWithLink() {
      final String htmlResult = generateHtmlDocumentation( TestAspect.ASPECT_WITH_MARKDOWN_DESCRIPTION );
      AssertionsForClassTypes.assertThat( htmlResult ).contains( "<a href=\"https://example.com\">Visit Example</a>" );
   }

   @Test
   void testAspectWithMarkdownDescription() {
      final String htmlResult = generateHtmlDocumentation( TestAspect.ASPECT_WITH_MARKDOWN_DESCRIPTION );
      AssertionsForClassTypes.assertThat( htmlResult ).doesNotContain( "[link](https://www.example.com/spec)" );
   }

   @Test
   void testHtmlOutputDoesNotContainMarkdownSyntax() {
      final String htmlResult = generateHtmlDocumentation( TestAspect.ASPECT_WITH_MARKDOWN_DESCRIPTION );
      AssertionsForClassTypes.assertThat( htmlResult ).doesNotContain( "[Visit Example](https://example.com)" );
   }

   @Test
   void testAspectWithEventRendersEventAndParameters() {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_EVENT );

      assertThat( documentation )
            .contains( "id=\"events\"" )
            .contains( ">Some Event</h3>" )
            .contains( "This is some event" )
            .contains( ">Test Property</h4>" )
            .contains( "This is a test property." )

            // exampleValue
            .contains( "Example Value" )

            // see links
            .contains( "<li>http://example.com/</li>" )
            .contains( "<li>http://example.com/me</li>" );
   }

   @Test
   void testAspectWithEventRendersEventHeadingWithExpectedAnchor() {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_EVENT );

      assertThat( documentation )
            .contains( "<h3 id=\"org-eclipse-esmf-test-SomeEvent-event\">Some Event</h3>" );
   }

   @Test
   void testAspectWithEventRendersParameterPropertyWithExpectedAnchor() {
      final String documentation = generateHtmlDocumentation( TestAspect.ASPECT_WITH_EVENT );

      assertThat( documentation )
            .contains(
                  "<h4 id=\"org-eclipse-esmf-test-AspectWithEvent-org-eclipse-esmf-test-testProperty-property\">Test Property</h4>" );
   }

   private String generateHtmlDocumentation( final TestAspect testAspect ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      final AspectModelDocumentationGenerator aspectModelDocumentationGenerator = new AspectModelDocumentationGenerator( aspect );
      return aspectModelDocumentationGenerator.getContent();
   }
}
