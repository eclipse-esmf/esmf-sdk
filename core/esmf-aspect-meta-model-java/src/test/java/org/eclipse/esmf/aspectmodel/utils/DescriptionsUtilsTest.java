package org.eclipse.esmf.aspectmodel.utils;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class DescriptionsUtilsTest {
   @Test
   void testExtractNotesSingleNote() {
      final String description = "> NOTE: This is a note.\n> Continued on the next line.";
      final List<String> notes = DescriptionsUtils.notes( description );
      assertThat( notes ).hasSize( 1 );
      assertEquals( "This is a note.\nContinued on the next line.", notes.get( 0 ) );
   }

   @Test
   void testExtractExamplesMultipleExamples() {
      final String description =
            """
                  > EXAMPLE 1: First example.
                  > More detail.
                                    
                  > EXAMPLE 2: Second example.
                  """;
      final List<String> examples = DescriptionsUtils.examples( description );

      assertEquals( 2, examples.size() );
      assertEquals( "First example.\nMore detail.", examples.get( 0 ) );
      assertEquals( "Second example.", examples.get( 1 ) );
   }

   @Test
   void testExtractSourcesWithLink() {
      final String description = "> SOURCE: Source with [link](https://example.com)";
      final List<String> sources = DescriptionsUtils.sources( description );
      assertEquals( 1, sources.size() );
      assertThat( sources.get( 0 ) ).contains( "[link](https://example.com)" );
   }

   @Test
   void testMixedBlockTypes() {
      final String description =
            """
                  > NOTE: A note block.
                  > EXAMPLE: An example block.
                                    
                  > SOURCE: A source block.
                  """;
      assertEquals( 1, DescriptionsUtils.notes( description ).size() );
      assertEquals( 1, DescriptionsUtils.examples( description ).size() );
      assertEquals( 1, DescriptionsUtils.sources( description ).size() );
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
      final String description =
            """
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

      final String html = DescriptionsUtils.toHtml( description );

      assertThat( html ).contains( "<div class=\"note\">" );
      assertThat( html ).contains( "This is a note." );
      assertTrue( html.contains( "<ul class=\"example-list\">" ) || html.contains( "<div class=\"example\">" ) );
      assertThat( html ).contains( "First example." );
      assertThat( html ).contains( "<div class=\"source\">" );
      assertThat( html ).contains( "Source information here." );
      assertThat( html ).contains( "<strong>markdown</strong>" );
      assertThat( html ).contains( "<ol>" );
   }

   @Test
   void testMarkdownRenderingBulletList() {
      String description = """
            This is a list:
            * Item A
            * Item B
            * Item C
            """;
      String html = DescriptionsUtils.toHtml( description );
      assertThat( html ).contains( "<ul>" );
      assertThat( html ).contains( "<li>Item A</li>" );
      assertThat( html ).contains( "<li>Item B</li>" );
      assertThat( html ).contains( "<li>Item C</li>" );
   }

   @Test
   void testMarkdownRenderingOrderedList() {
      String description = """
            Steps:
            1. First
            2. Second
            3. Third
            """;
      String html = DescriptionsUtils.toHtml( description );
      assertThat( html ).contains( "<ol>" );
      assertThat( html ).contains( "<li>First</li>" );
      assertThat( html ).contains( "<li>Second</li>" );
      assertThat( html ).contains( "<li>Third</li>" );
   }

   @Test
   void testMarkdownRenderingSpecialBlock() {
      String description =
            """
                  > NOTE: This is a note.
                  > Continued here.
                  """;
      String html = DescriptionsUtils.toHtml( description );
      assertThat( html ).contains( "<div class=\"note\">" );
      assertThat( html ).contains( "This is a note." );
      assertThat( html ).contains( "Continued here." );
   }

   @Test
   void testMarkdownRenderingWithLink() {
      String description =
            "Here is a [link](https://example.com) in the text.";
      String html = DescriptionsUtils.toHtml( description );
      assertThat( html ).contains( "<a href=\"https://example.com\">link</a>" );
   }

   @Test
   void testHtmlOutputDoesNotContainMarkdownSyntax() {
      String description =
            "This is a [link](https://example.com).";
      String html = DescriptionsUtils.toHtml( description );
      assertThat( html ).doesNotContain( "[link](https://example.com)" );
   }
}
