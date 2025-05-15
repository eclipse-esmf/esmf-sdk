package org.eclipse.esmf.aspectmodel.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class DescriptionsUtilsTest {
   @Test
   void testExtractNotes_singleNote() {
      final String description = "> NOTE: This is a note.\n> Continued on the next line.";
      final List<String> notes = DescriptionsUtils.notes( description );
      assertEquals( 1, notes.size() );
      assertEquals( "This is a note.\nContinued on the next line.", notes.get( 0 ) );
   }

   @Test
   void testExtractExamples_multipleExamples() {
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
   void testExtractSources_withLink() {
      final String description = "> SOURCE: Source with [link](https://example.com)";
      final List<String> sources = DescriptionsUtils.sources( description );
      assertEquals( 1, sources.size() );
      assertTrue( sources.get( 0 ).contains( "[link](https://example.com)" ) );
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
      assertTrue( DescriptionsUtils.notes( description ).isEmpty() );
      assertTrue( DescriptionsUtils.examples( description ).isEmpty() );
      assertTrue( DescriptionsUtils.sources( description ).isEmpty() );
   }

   @Test
   void testToHtml_withAllBlockTypes() {
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

      assertTrue( html.contains( "<div class=\"note\">" ) );
      assertTrue( html.contains( "This is a note." ) );
      assertTrue( html.contains( "<ul class=\"example-list\">" ) || html.contains( "<div class=\"example\">" ) );
      assertTrue( html.contains( "First example." ) );
      assertTrue( html.contains( "<div class=\"source\">" ) );
      assertTrue( html.contains( "Source information here." ) );
      assertTrue( html.contains( "<strong>markdown</strong>" ) );
      assertTrue( html.contains( "<ol>" ) );
   }
}
