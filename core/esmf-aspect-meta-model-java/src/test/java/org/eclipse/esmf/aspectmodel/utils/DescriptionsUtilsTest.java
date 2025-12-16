/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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
package org.eclipse.esmf.aspectmodel.utils;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DescriptionsUtilsTest {

   private static String testDescription;

   @BeforeAll
   public static void init() {
      final AspectModel aspectModel = TestResources.load( TestAspect.ASPECT_WITH_MARKDOWN_DESCRIPTION );
      final AspectModelFile originalFile = aspectModel.files().iterator().next();
      testDescription = originalFile.elements().getFirst().getDescription( Locale.ENGLISH );
   }

   @Test
   void testExtractNotesSingleNote() {
      final List<String> notes = DescriptionsUtils.notes( testDescription );
      assertThat( notes ).hasSize( 1 );
      assertEquals( "This is a note block.\nIt supports multiple lines.\nHere's a second line of the note.", notes.get( 0 ) );
   }

   @Test
   void testExtractExamplesMultipleExamples() {
      final List<String> examples = DescriptionsUtils.examples( testDescription );

      assertEquals( 2, examples.size() );
      assertEquals( "This is the first example block.\nIt can span several lines, and supports *italic* and **bold** text.",
            examples.get( 0 ) );
      assertEquals( "This is the second example.\nAlso multiline, for testing multiple example entries.", examples.get( 1 ) );
   }

   @Test
   void testExtractExamplesMultipleExamplesWithBoldAndItalicText() {
      final String html = DescriptionsUtils.toHtml( testDescription );

      assertThat( html ).contains(
            "This is the first example block.\nIt can span several lines, and supports <em>italic</em> and <strong>bold</strong> text." );
   }

   @Test
   void testExtractSourcesWithLink() {
      final List<String> sources = DescriptionsUtils.sources( testDescription );
      assertEquals( 1, sources.size() );
      assertThat( sources.get( 0 ) ).contains( "ISO 12345:2023, section 4.2.1\n" + "with an inline [link](https://www.example.com/spec)." );
   }

   @Test
   void testMixedBlockTypes() {
      assertEquals( 1, DescriptionsUtils.notes( testDescription ).size() );
      assertEquals( 2, DescriptionsUtils.examples( testDescription ).size() );
      assertEquals( 1, DescriptionsUtils.sources( testDescription ).size() );
   }

   @Test
   void testNoBlocks() {
      final String description = "This is a plain description without any special blocks.";
      assertThat( DescriptionsUtils.notes( description ) ).isEmpty();
      assertThat( DescriptionsUtils.examples( description ) ).isEmpty();
      assertThat( DescriptionsUtils.sources( description ) ).isEmpty();
   }

   @Test
   void testToHtmlWithAllBlockTypes() {
      final String description = """
            > NOTE: This is a note.
            > With multiple lines.
                
            > EXAMPLE 1: First example.
            > Additional example content.
                
            > EXAMPLE 2: Second example.
                
            > SOURCE: Source information here.
                
            Some **markdown** content here.
            1. Ordered
            2. List
            """;

      final String html = DescriptionsUtils.toHtml( testDescription );

      assertThat( html ).contains( "<div class=\"note\">" );
      assertThat( html ).contains( "This is a note block.\nIt supports multiple lines.\nHere's a second line of the note." );
      assertTrue( html.contains( "<ul class=\"example-list\">" ) || html.contains( "<div class=\"example\">" ) );
      assertThat( html ).contains(
            "This is the first example block.\nIt can span several lines, and supports <em>italic</em> and <strong>bold</strong> text." );
      assertThat( html ).contains( "<div class=\"source\">" );
      assertThat( html ).contains( "ISO 12345:2023, section 4.2.1\nwith an inline <a href=\"https://www.example.com/spec\">link</a>." );
      assertThat( html ).contains( "<ol>" );
   }

   @Test
   void testMarkdownRenderingBulletList() {
      String html = DescriptionsUtils.toHtml( testDescription );
      assertThat( html ).contains( "<ul>" );
      assertThat( html ).contains( "<li>Item A</li>" );
      assertThat( html ).contains( "<li>Item B</li>" );
      assertThat( html ).contains( "<li>Item C</li>" );
   }

   @Test
   void testMarkdownRenderingOrderedList() {
      String html = DescriptionsUtils.toHtml( testDescription );
      assertThat( html ).contains( "<ol>" );
      assertThat( html ).contains( "<li>First</li>" );
      assertThat( html ).contains( "<li>Second</li>" );
      assertThat( html ).contains( "<li>Third</li>" );
   }

   @Test
   void testMarkdownRenderingWithLink() {
      String html = DescriptionsUtils.toHtml( testDescription );
      assertThat( html ).contains( "<a href=\"https://example.com\">Visit Example</a>" );
   }

   @Test
   void testHtmlOutputDoesNotContainMarkdownSyntax() {
      String html = DescriptionsUtils.toHtml( testDescription );
      assertThat( html ).doesNotContain( "[Visit Example](https://example.com)" );
   }

   @Test
   void testStripIndentSingleLine() {
      String input = "    only one line";
      String expected = "only one line";
      String result = DescriptionsUtils.stripIndent( input );
      assertEquals( expected, result );
   }
}
