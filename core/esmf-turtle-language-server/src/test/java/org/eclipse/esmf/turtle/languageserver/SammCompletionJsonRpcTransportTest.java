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

package org.eclipse.esmf.turtle.languageserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.esmf.turtle.languageserver.JsonRpcTransportTestSupport.request;
import static org.eclipse.esmf.turtle.languageserver.JsonRpcTransportTestSupport.withConnection;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.esmf.turtle.languageserver.lsp.text.TurtleTextDocumentService;

import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

class SammCompletionJsonRpcTransportTest {
   private static final String URI = "file:///samm-snippet-demo.ttl";
   private static final int SNIPPET_KIND = CompletionItemKind.Snippet.getValue();
   private static final int SNIPPET_FORMAT = InsertTextFormat.Snippet.getValue();
   private static final String SOURCE = """
      @prefix : <urn:samm:example.snippets:1.0.0#> .
      @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
      :p a samm:Property ;
          samm:des""";
   private static final String POSITION_PARAMS = """
      {"textDocument":{"uri":"file:///samm-snippet-demo.ttl"},"position":{"line":3,"character":12}}""";

   @Test
   void returnsOrdinarySnippetsWithoutAdvertisingInlineSupport() throws Exception {
      final TurtleLanguageServer server = new TurtleLanguageServer();
      final TurtleTextDocumentService documents = (TurtleTextDocumentService) server.getTextDocumentService();
      try {
         withConnection( server, client -> {
            final JsonNode initialized = request( client, 1, "initialize", """
               {"capabilities":{"textDocument":{"completion":{"completionItem":{"snippetSupport":true}},"inlineCompletion":{}}}}""" );
            final JsonNode capabilities = initialized.path( "result" ).path( "capabilities" );
            assertThat( capabilities.has( "inlineCompletionProvider" ) ).isFalse();
            assertThat( capabilities.path( "completionProvider" ).path( "triggerCharacters" ).toString() ).isEqualTo( "[\":\",\"[\"]" );
            documents.didOpen( new DidOpenTextDocumentParams( new TextDocumentItem( URI, "turtle", 1, SOURCE ) ) );

            final JsonNode completion = request( client, 2, "textDocument/completion", POSITION_PARAMS );
            assertThat( completion.has( "error" ) ).isFalse();
            boolean foundSnippet = false;
            for ( final JsonNode item : completion.path( "result" ) ) {
               if ( item.path( "kind" ).asInt() == SNIPPET_KIND ) {
                  foundSnippet = true;
                  assertThat( item.path( "insertTextFormat" ).asInt() ).isEqualTo( SNIPPET_FORMAT );
                  assertThat( item.path( "textEdit" ).path( "newText" ).asText() ).isEqualTo( "samm:description \"${1}\"@en ;" );
               }
            }
            assertThat( foundSnippet ).isTrue();

            documents.didChange( new DidChangeTextDocumentParams( new VersionedTextDocumentIdentifier( URI, 2 ),
                  List.of( new TextDocumentContentChangeEvent( SOURCE + " ; samm:description \"existing\"@en ." ) ) ) );
            final JsonNode changed = request( client, 3, "textDocument/completion", POSITION_PARAMS );
            assertThat( changed.has( "error" ) ).isFalse();
            assertThat( changed.path( "result" ).isEmpty() ).isFalse();
            for ( final JsonNode item : changed.path( "result" ) ) {
               assertThat( item.path( "kind" ).asInt() ).isNotEqualTo( SNIPPET_KIND );
            }
            documents.didClose( new DidCloseTextDocumentParams( new TextDocumentIdentifier( URI ) ) );
            final JsonNode closed = request( client, 4, "textDocument/completion", POSITION_PARAMS );
            assertThat( closed.has( "error" ) ).isFalse();
            assertThat( closed.path( "result" ).size() ).isZero();
         } );
      } finally {
         server.shutdown().get( 5, TimeUnit.SECONDS );
      }
   }

   @Test
   void olderClientsKeepVocabularyWithoutSnippetsOrInlineProvider() throws Exception {
      final TurtleLanguageServer server = new TurtleLanguageServer();
      final TurtleTextDocumentService documents = (TurtleTextDocumentService) server.getTextDocumentService();
      try {
         withConnection( server, client -> {
            final JsonNode initialized = request( client, 1, "initialize", "{\"capabilities\":{}}" );
            assertThat( initialized.path( "result" ).path( "capabilities" ).has( "inlineCompletionProvider" ) ).isFalse();
            documents.didOpen( new DidOpenTextDocumentParams( new TextDocumentItem( URI, "turtle", 1, SOURCE ) ) );
            final JsonNode completion = request( client, 2, "textDocument/completion", POSITION_PARAMS );
            assertThat( completion.path( "result" ).isEmpty() ).isFalse();
            for ( final JsonNode item : completion.path( "result" ) ) {
               assertThat( item.path( "insertTextFormat" ).asInt() ).isNotEqualTo( SNIPPET_FORMAT );
            }
         } );
      } finally {
         server.shutdown().get( 5, TimeUnit.SECONDS );
      }
   }
}
