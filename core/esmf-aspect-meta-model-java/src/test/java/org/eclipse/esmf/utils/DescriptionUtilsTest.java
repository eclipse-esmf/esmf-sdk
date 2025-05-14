package org.eclipse.esmf.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.utils.DescriptionsUtils;

import org.junit.jupiter.api.Test;

class DescriptionsUtilsTest {
   @Test
   void testExtractNotes_singleNote() {
      Set<String> descriptions = Set.of(
            "> NOTE: This is a note.\n> Continued on the next line."
      );
      List<String> notes = DescriptionsUtils.notes( descriptions );
      assertEquals( 1, notes.size() );
      assertEquals( "This is a note.\nContinued on the next line.", notes.get( 0 ) );
   }

   @Test
   void testExtractExamples_multipleExamples() {
      Set<String> descriptions = Set.of(
            "> EXAMPLE 1: First example.\n> More detail.",
            "> EXAMPLE 2: Second example."
      );
      List<String> examples = DescriptionsUtils.examples( descriptions );

      assertEquals( 2, examples.size() );
      assertEquals( "First example.\nMore detail.", examples.get( 0 ) );
      assertEquals( "Second example.", examples.get( 1 ) );
   }

   @Test
   void testExtractSources_withLink() {
      Set<String> descriptions = Set.of(
            "> SOURCE: Source with [link](https://example.com)"
      );
      List<String> sources = DescriptionsUtils.sources( descriptions );
      assertEquals( 1, sources.size() );
      assertTrue( sources.get( 0 ).contains( "[link](https://example.com)" ) );
   }

   @Test
   void testMixedBlockTypes() {
      Set<String> descriptions = Set.of(
            "> NOTE: A note block.",
            "> EXAMPLE: An example block.",
            "> SOURCE: A source block."
      );
      assertEquals( 1, DescriptionsUtils.notes( descriptions ).size() );
      assertEquals( 1, DescriptionsUtils.examples( descriptions ).size() );
      assertEquals( 1, DescriptionsUtils.sources( descriptions ).size() );
   }

   @Test
   void testNoBlocks() {
      Set<String> descriptions = Set.of(
            "This is a plain description without any special blocks."
      );
      assertTrue( DescriptionsUtils.notes( descriptions ).isEmpty() );
      assertTrue( DescriptionsUtils.examples( descriptions ).isEmpty() );
      assertTrue( DescriptionsUtils.sources( descriptions ).isEmpty() );
   }

   @Test
   public void testToHtml_withAllBlockTypes() {
      Set<String> descriptions = Set.of(
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
      );

      String html = DescriptionsUtils.toHtml( descriptions );

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
