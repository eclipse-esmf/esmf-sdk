package org.eclipse.esmf.aspectmodel.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class DescriptionsUtilsTest {
   @Test
   void testExtractNotes_singleNote() {
      String description = "> NOTE: This is a note.\n> Continued on the next line.";
      List<String> notes = DescriptionsUtils.notes( description );
      assertEquals( 1, notes.size() );
      assertEquals( "This is a note.\nContinued on the next line.", notes.get( 0 ) );
   }

   @Test
   void testExtractExamples_multipleExamples() {
      String description =
            "> EXAMPLE 1: First example.\n> More detail.\n" +
            "> EXAMPLE 2: Second example.";
      List<String> examples = DescriptionsUtils.examples( description );

      assertEquals( 2, examples.size() );
      assertEquals( "First example.\nMore detail.", examples.get( 0 ) );
      assertEquals( "Second example.", examples.get( 1 ) );
   }

   @Test
   void testExtractSources_withLink() {
      String description = "> SOURCE: Source with [link](https://example.com)";
      List<String> sources = DescriptionsUtils.sources( description );
      assertEquals( 1, sources.size() );
      assertTrue( sources.get( 0 ).contains( "[link](https://example.com)" ) );
   }

   @Test
   void testMixedBlockTypes() {
      String description =
            "> NOTE: A note block.\n" +
            "> EXAMPLE: An example block.\n" +
            "> SOURCE: A source block.";
      assertEquals( 1, DescriptionsUtils.notes( description ).size() );
      assertEquals( 1, DescriptionsUtils.examples( description ).size() );
      assertEquals( 1, DescriptionsUtils.sources( description ).size() );
   }

   @Test
   void testNoBlocks() {
      String description = "This is a plain description without any special blocks.";
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
                  """
      ;

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
