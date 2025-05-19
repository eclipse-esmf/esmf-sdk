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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for extracting and rendering structured content blocks (such as NOTE, EXAMPLE, SOURCE)
 * from SAMM-compliant Markdown descriptions.
 *
 * <p>This class supports parsing multi-line Markdown-style input and extracting semantically significant
 * sections such as {@code > NOTE: ...}, {@code > EXAMPLE: ...}, and {@code > SOURCE: ...}.
 * These blocks can be retrieved as plain text or rendered into HTML using {@link MarkdownHtmlRenderer}.
 */
public class DescriptionsUtils {

   private DescriptionsUtils() {
   }

   /**
    * A regex pattern used to identify special SAMM-style Markdown blocks.
    * Matches lines beginning with {@code > NOTE:}, {@code > EXAMPLE:}, or {@code > SOURCE:},
    * optionally followed by a number (e.g., {@code > EXAMPLE 2: ...}).
    */
   static final Pattern BLOCK_PATTERN = Pattern.compile(
         "^>\\s*(NOTE|EXAMPLE|SOURCE)(\\s+\\d+)?:\\s*(.*)",
         Pattern.CASE_INSENSITIVE
   );

   /**
    * Extracts all {@code NOTE} blocks from the given set of Markdown description strings.
    *
    * @param description A line Markdown description.
    * @return A list of extracted NOTE block contents.
    */
   public static List<String> notes( final String description ) {
      return extractBlock( description, "NOTE" );
   }

   /**
    * Extracts all {@code EXAMPLE} blocks from the given set of Markdown description strings.
    *
    * @param description A line Markdown description.
    * @return A list of extracted EXAMPLE block contents.
    */
   public static List<String> examples( final String description ) {
      return extractBlock( description, "EXAMPLE" );
   }

   /**
    * Extracts all {@code SOURCE} blocks from the given set of Markdown description strings.
    *
    * @param description A line Markdown description.
    * @return A list of extracted SOURCE block contents.
    */
   public static List<String> sources( final String description ) {
      return extractBlock( description, "SOURCE" );
   }

   /**
    * Renders the given set of Markdown description strings into semantic HTML.
    * Uses {@link MarkdownHtmlRenderer} to process both special blocks and general Markdown syntax.
    *
    * @param description A line of Markdown description string.
    * @return The HTML representation of the combined input.
    */
   public static String toHtml( final String description ) {
      return MarkdownHtmlRenderer.renderHtmlFromDescriptions( description );
   }

   /**
    * Extracts all blocks of a specified type (e.g., NOTE, EXAMPLE, SOURCE) from a set of Markdown strings.
    *
    * <p>Each block is expected to begin with a {@code > TYPE:} line and may span multiple lines,
    * each of which begins with {@code >}.
    *
    * @param description A line Markdown description string.
    * @param type The type of block to extract ("NOTE", "EXAMPLE", or "SOURCE").
    * @return A list of extracted block contents for the specified type.
    */
   private static List<String> extractBlock( final String description, final String type ) {
      List<String> result = new ArrayList<>();
      extractFromDescription( stripIndent( description ), type, result );
      return result;
   }

   private static void extractFromDescription( final String desc, final String type, final List<String> result ) {
      String[] lines = desc.split( "\\R" );
      boolean[] insideBlock = { false };
      StringBuilder blockContent = new StringBuilder();

      for ( String line : lines ) {
         handleLine( line, type, insideBlock, blockContent, result );
      }

      if ( insideBlock[0] && !blockContent.isEmpty() ) {
         result.add( blockContent.toString().strip() );
      }
   }

   private static void handleLine( final String line, final String type, boolean[] insideBlock,
         StringBuilder blockContent, List<String> result ) {
      Matcher matcher = BLOCK_PATTERN.matcher( line );
      if ( matcher.find() ) {
         String currentType = matcher.group( 1 ).toUpperCase();
         String content = matcher.group( 3 ); // Corrected: group(3) is the actual content

         flushBlock( insideBlock, blockContent, result );

         if ( currentType.equals( type.toUpperCase() ) ) {
            blockContent.append( content ).append( "\n" );
            insideBlock[0] = true;
         } else {
            insideBlock[0] = false;
         }
      } else if ( insideBlock[0] && line.startsWith( ">" ) ) {
         blockContent.append( line.substring( 1 ).stripLeading() ).append( "\n" );
      } else if ( insideBlock[0] ) {
         flushBlock( insideBlock, blockContent, result );
      }
   }

   private static void flushBlock( boolean[] insideBlock, StringBuilder blockContent, List<String> result ) {
      if ( insideBlock[0] && !blockContent.isEmpty() ) {
         result.add( blockContent.toString().strip() );
         blockContent.setLength( 0 );
         insideBlock[0] = false;
      }
   }

   static String stripIndent( final String string ) {
      final int indent = string.lines()
            .filter( line -> !line.isEmpty() )
            .map( line -> line.indexOf( line.trim() ) )
            .filter( offset -> offset > 0 )
            .min( Integer::compareTo )
            .orElse( 0 );
      return string.lines()
            .map( line -> indent <= line.length() ? line.substring( indent ) : line )
            .collect( Collectors.joining( "\n" ) );
   }
}

