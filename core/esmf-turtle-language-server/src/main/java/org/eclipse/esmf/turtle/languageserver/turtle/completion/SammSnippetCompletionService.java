/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.turtle.languageserver.turtle.completion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammSnippetCatalog.SnippetDefinition;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammCompletionContext.TargetPosition;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

public final class SammSnippetCompletionService {
   private final SammSuggestionContextDetector detector = new SammSuggestionContextDetector();
   private final SammSnippetCatalog catalog = new SammSnippetCatalog();

   public List<CompletionItem> complete( final ParsedDocument document, final Position position ) {
      return detector.detect( new TurtleCompletionTree( document ).cursorAt( position ) )
            .map( this::complete )
            .orElseGet( List::of );
   }

   public List<CompletionItem> complete( final SammCompletionContext context ) {
      final List<SnippetDefinition> matching = catalog.matchingSnippets( context );
      final List<CompletionItem> items = new ArrayList<>( matching.size() );
      for ( int i = 0; i < matching.size(); i++ ) {
         items.add( toCompletionItem( matching.get( i ), context, i ) );
      }
      return items;
   }

   private static CompletionItem toCompletionItem( final SnippetDefinition snippet, final SammCompletionContext context, final int rank ) {
      final CompletionItem item = new CompletionItem( snippet.label() );
      item.setKind( CompletionItemKind.Snippet );
      item.setDetail( "SAMM " + context.namespaces().version().toVersionString() );
      item.setFilterText( computeFilterText( snippet, context ) );
      item.setSortText( String.format( "%04d", rank ) );
      item.setInsertTextFormat( InsertTextFormat.Snippet );
      item.setTextEdit( Either.forLeft( new TextEdit( context.edit().range(), snippet.render( context ) ) ) );
      return item;
   }

   /**
    * The client requests the items once and then filters and ranks them itself on every following
    * keystroke, by matching the text in the edit range against the filter text. The filter text
    * therefore consists of the names of the snippet only and never of the text that was typed when the
    * items were requested: that text would be matched again by the client and distort its ranking.
    *
    * @return the names the client matches the typed text against
    */
   private static String computeFilterText( final SnippetDefinition snippet, final SammCompletionContext context ) {
      final List<String> parts = new ArrayList<>();
      if ( context.targetPosition() == TargetPosition.DOCUMENT_HEADER ) {
         parts.add( "@prefix" );
      } else if ( context.targetPosition() == TargetPosition.PREDICATE ) {
         final String prefix = snippet.predicatePrefix( context );
         if ( !prefix.isEmpty() ) {
            parts.add( prefix + snippet.localName() );
         }
      }
      parts.addAll( snippet.searchTerms() );
      final String names = String.join( " ", parts );
      return context.targetPosition().isAnonymousObject() ? anonymousObjectStart( context.edit().typedText() ) + names : names;
   }

   /**
    * The edit range of an anonymous object starts at its opening bracket, so the bracket is part of
    * the text the client filters with and must be part of the filter text as well.
    *
    * @return the opening bracket followed by the whitespace that separates it from the name; a single
    *         space if none is typed, which matches the name both directly after the bracket and
    *         after a space
    */
   private static String anonymousObjectStart( final String typed ) {
      int end = 1;
      while ( end < typed.length() && Character.isWhitespace( typed.charAt( end ) ) ) {
         end++;
      }
      return end == 1 ? "[ " : typed.substring( 0, end );
   }
}
