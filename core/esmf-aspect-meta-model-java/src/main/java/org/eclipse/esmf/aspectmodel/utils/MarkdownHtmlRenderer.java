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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * A utility class for converting SAMM-flavored Markdown descriptions into HTML.
 *
 * <p>This renderer supports a limited subset of Markdown syntax and introduces
 * custom processing for specific annotated blocks commonly used in SAMM descriptions,
 * such as {@code > NOTE: ...}, {@code > EXAMPLE: ...}, and {@code > SOURCE: ...}.
 * These blocks are extracted and rendered into semantically meaningful HTML
 * structures (e.g., {@code <div class="note">}, {@code <ul class="example-list">}, etc.).
 * Remaining content is rendered using the CommonMark parser.
 */
public class MarkdownHtmlRenderer {

   private static final String CLOSE_DIV_TAG = "</div>";

   /**
    * A reusable CommonMark parser instance for processing standard Markdown syntax.
    */
   private static final Parser PARSER = Parser.builder().build();

   /**
    * A reusable CommonMark HTML renderer instance.
    */
   private static final HtmlRenderer RENDERER = HtmlRenderer.builder().build();

   /**
    * Private constructor to prevent instantiation. This class is intended to be used statically.
    */
   private MarkdownHtmlRenderer() {
   }

   /**
    * Converts a set of multi-line Markdown descriptions into a single HTML string.
    * Each entry in the set is processed independently and merged in the resulting output.
    *
    * @param description A line of Markdown description blocks to render.
    * @return Combined HTML output representing all given descriptions.
    */
   public static String renderHtmlFromDescriptions( final String description ) {
      return processSpecialBlocks( description ) + "\n";
   }

   /**
    * Parses a single Markdown block:
    * <ul>
    *   <li>Identifies and extracts special block types: NOTE, EXAMPLE, and SOURCE</li>
    *   <li>Renders those blocks using custom HTML wrappers</li>
    *   <li>Processes the remaining Markdown using the CommonMark renderer</li>
    * </ul>
    *
    * @param rawMarkdown The full Markdown string to process.
    * @return The rendered HTML output.
    */
   private static String processSpecialBlocks( final String rawMarkdown ) {
      final String[] lines = stripLines( rawMarkdown );

      final StringBuilder html = new StringBuilder();
      final StringBuilder markdownBuffer = new StringBuilder();

      String currentType = null; // NOTE / EXAMPLE / SOURCE / ...
      final StringBuilder blockBuffer = new StringBuilder();

      for ( final String line : lines ) {
         final String trimmedLeading = line.stripLeading();
         final Matcher matcher = DescriptionsUtils.BLOCK_PATTERN.matcher( trimmedLeading );

         final boolean startsNewSpecialBlock = matcher.find();
         final boolean continuesSpecialBlock = currentType != null && trimmedLeading.startsWith( ">" );

         if ( startsNewSpecialBlock ) {
            flushMarkdown( markdownBuffer, html );
            flushSpecialBlock( currentType, blockBuffer, html );

            currentType = matcher.group( 1 ).toUpperCase(); // NOTE / EXAMPLE / SOURCE
            blockBuffer.append( matcher.group( 3 ) ).append( '\n' );
         } else if ( continuesSpecialBlock ) {
            blockBuffer.append( trimmedLeading.substring( 1 ).stripLeading() ).append( '\n' );
         } else {
            flushSpecialBlock( currentType, blockBuffer, html );
            currentType = null;

            markdownBuffer.append( line ).append( '\n' );
         }
      }

      flushSpecialBlock( currentType, blockBuffer, html );
      flushMarkdown( markdownBuffer, html );

      return html.toString();
   }

   private static void flushMarkdown( final StringBuilder markdownBuffer, final StringBuilder html ) {
      if ( markdownBuffer.isEmpty() ) {
         return;
      }
      Node parsed = PARSER.parse( markdownBuffer.toString() );
      html.append( RENDERER.render( parsed ) );
      markdownBuffer.setLength( 0 );
   }

   private static void flushSpecialBlock(
         final String currentType,
         final StringBuilder blockBuffer,
         final StringBuilder html
   ) {
      if ( currentType == null || blockBuffer.isEmpty() ) {
         blockBuffer.setLength( 0 );
         return;
      }

      // keep existing behavior: split EXAMPLE into list entries, NOTE/SOURCE as single block
      List<String> items = splitBlockItems( currentType, blockBuffer.toString() );
      html.append( renderSpecialBlock( currentType, items ) );

      blockBuffer.setLength( 0 );
   }

   private static List<String> splitBlockItems( final String type, final String blockText ) {
      String normalized = blockText.strip();

      // each "EXAMPLE X:" becomes a separate item (your BLOCK_PATTERN already captures the first line;
      // this keeps multiline content per example together, separated by blank lines)
      // If you already had logic in flushBlock(), reuse it here.
      // simplest: treat each EXAMPLE header as separate via BLOCK_PATTERN starts

      return List.of( normalized );
   }

   /**
    * Renders a list of extracted special blocks into HTML.
    *
    * <p>- For {@code NOTE} and {@code SOURCE}, each entry is rendered in a {@code <div>} with a matching class.<br>
    * - For {@code EXAMPLE}, a single example is rendered as a {@code <div>}; multiple examples as a {@code <ul>}.
    *
    * @param type The type of the special block (e.g., "NOTE", "EXAMPLE", "SOURCE").
    * @param items The list of block contents for that type.
    * @return The rendered HTML string for the block.
    */
   private static String renderSpecialBlock( final String type, final List<String> items ) {
      if ( items.isEmpty() ) {
         return "";
      }

      return switch ( type ) {
         case "NOTE", "SOURCE" -> items.stream()
               .map( text -> "<div class=\"" + type.toLowerCase() + "\">"
                     + renderMarkdownInline( text.strip() ) + CLOSE_DIV_TAG + "\n" )
               .collect( Collectors.joining() );

         case "EXAMPLE" -> {
            if ( items.size() == 1 ) {
               yield "<div class=\"example\">" + renderMarkdownInline( items.get( 0 ).strip() ) + CLOSE_DIV_TAG + "\n";
            } else {
               StringBuilder sb = new StringBuilder( "<ul class=\"example-list\">\n" );
               for ( String item : items ) {
                  sb.append( "<li>" ).append( renderMarkdownInline( item.strip() ) ).append( "</li>\n" );
               }
               sb.append( "</ul>\n" );
               yield sb.toString();
            }
         }

         default -> items.stream()
               .map( text -> "<div class=\"block\">" + renderMarkdownInline( text.strip() ) + CLOSE_DIV_TAG + "\n" )
               .collect( Collectors.joining() );
      };
   }

   /**
    * Collects all special block entries (NOTE, EXAMPLE, SOURCE) from the input lines.
    * Lines not belonging to special blocks are appended to the {@code markdownBuffer}.
    *
    * @param lines Stripped lines from the raw markdown block.
    * @param markdownBuffer Buffer to store non-special markdown content.
    * @return A map of special block types to their associated content.
    */
   private static Map<String, List<String>> collectSpecialBlocks( final String[] lines, final StringBuilder markdownBuffer ) {
      Map<String, List<String>> specialBlocks = new LinkedHashMap<>();

      String currentType = null;
      StringBuilder block = new StringBuilder();

      for ( String line : lines ) {
         Matcher matcher = DescriptionsUtils.BLOCK_PATTERN.matcher( line );
         if ( matcher.find() ) {
            flushBlock( currentType, block, specialBlocks );
            currentType = matcher.group( 1 ).toUpperCase();
            block.append( matcher.group( 3 ) ).append( "\n" );
         } else if ( currentType != null && line.startsWith( ">" ) ) {
            block.append( line.substring( 1 ).stripLeading() ).append( "\n" );
         } else {
            flushBlock( currentType, block, specialBlocks );
            currentType = null;
            markdownBuffer.append( line ).append( "\n" );
         }
      }

      flushBlock( currentType, block, specialBlocks );
      return specialBlocks;
   }

   /**
    * Flushes the current block to the target map if non-empty.
    *
    * @param currentType The type of block being collected.
    * @param block The current content buffer for the block.
    * @param target The target map of blocks.
    */
   private static void flushBlock( final String currentType, final StringBuilder block, final Map<String, List<String>> target ) {
      if ( currentType != null && !block.isEmpty() ) {
         target.computeIfAbsent( currentType, k -> new ArrayList<>() ).add( block.toString().strip() );
         block.setLength( 0 );
      }
   }

   /**
    * Splits the raw markdown string into lines and strips leading whitespace from each line.
    *
    * @param rawMarkdown The original multi-line markdown string.
    * @return An array of trimmed lines.
    */
   private static String[] stripLines( final String rawMarkdown ) {
      String[] rawLines = rawMarkdown.split( "\\R", -1 );
      String[] lines = new String[rawLines.length];
      for ( int i = 0; i < rawLines.length; i++ ) {
         lines[i] = rawLines[i].stripLeading();
      }
      return lines;
   }

   /**
    * Renders a single markdown line (inline) to HTML using CommonMark.
    * This is used for special blocks (e.g., NOTE/EXAMPLE/SOURCE) where
    * markdown is allowed but not block-level structure.
    *
    * @param text Markdown content.
    * @return HTML output as string.
    */
   private static String renderMarkdownInline( final String text ) {
      Node node = PARSER.parse( text );
      return RENDERER.render( node ).trim();
   }
}


