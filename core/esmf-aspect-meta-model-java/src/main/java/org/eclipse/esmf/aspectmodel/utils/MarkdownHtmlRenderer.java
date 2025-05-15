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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * A utility class for converting SAMM-flavored Markdown descriptions into HTML.
 *
 * <p> This renderer supports a limited subset of Markdown syntax and introduces
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
    * @param descriptions A set of Markdown description blocks to render.
    * @return Combined HTML output representing all given descriptions.
    */
   public static String renderHtmlFromDescriptions( final Set<String> descriptions ) {
      StringBuilder result = new StringBuilder();
      for ( String desc : descriptions ) {
         result.append( processSpecialBlocks( desc ) ).append( "\n" );
      }
      return result.toString();
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
      String[] lines = stripLines( rawMarkdown );
      StringBuilder markdownBuffer = new StringBuilder();
      Map<String, List<String>> specialBlocks = collectSpecialBlocks( lines, markdownBuffer );

      StringBuilder html = new StringBuilder();
      specialBlocks.forEach( ( type, items ) -> html.append( renderSpecialBlock( type, items ) ) );

      Node parsed = PARSER.parse( markdownBuffer.toString() );
      html.append( RENDERER.render( parsed ) );
      return html.toString();
   }

   /**
    * Renders a list of extracted special blocks into HTML.
    *
    * - For {@code NOTE} and {@code SOURCE}, each entry is rendered in a {@code <div>} with a matching class.<br>
    * - For {@code EXAMPLE}, a single example is rendered as a {@code <div>}; multiple examples as a {@code <ul>}.
    *
    * @param type  The type of the special block (e.g., "NOTE", "EXAMPLE", "SOURCE").
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
    * @param lines          Stripped lines from the raw markdown block.
    * @param markdownBuffer Buffer to store non-special markdown content.
    * @return A map of special block types to their associated content.
    */
   private static Map<String, List<String>> collectSpecialBlocks( final String[] lines, final StringBuilder markdownBuffer ) {
      Pattern pattern = Pattern.compile( "^>\\s*(NOTE|EXAMPLE|SOURCE)(\\s+\\d+)?:\\s*(.*)", Pattern.CASE_INSENSITIVE );
      Map<String, List<String>> specialBlocks = new LinkedHashMap<>();

      String currentType = null;
      StringBuilder block = new StringBuilder();

      for ( String line : lines ) {
         Matcher matcher = pattern.matcher( line );
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
    * @param block       The current content buffer for the block.
    * @param target      The target map of blocks.
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


