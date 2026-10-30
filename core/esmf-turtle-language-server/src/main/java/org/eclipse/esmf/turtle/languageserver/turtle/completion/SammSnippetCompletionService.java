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
      return detector.detect( document, position )
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

   private static String computeFilterText( final SnippetDefinition snippet, final SammCompletionContext context ) {
      final String typed = context.edit().typedText();
      final String prefix = context.targetPosition() == TargetPosition.PREDICATE ? context.resolvedSammPrefix() : "";
      final String qualifiedName = prefix + snippet.defaultFilterText();
      if ( typed.isEmpty() ) {
         return qualifiedName.equals( snippet.defaultFilterText() )
               ? snippet.defaultFilterText() + " " + snippet.label()
               : qualifiedName + " " + snippet.defaultFilterText() + " " + snippet.label();
      }
      return typed + " " + qualifiedName + " " + snippet.defaultFilterText() + " " + snippet.label();
   }
}
