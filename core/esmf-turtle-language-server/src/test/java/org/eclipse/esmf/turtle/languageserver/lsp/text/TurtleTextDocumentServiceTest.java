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

package org.eclipse.esmf.turtle.languageserver.lsp.text;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.MessageActionItem;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.ShowMessageRequestParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.services.LanguageClient;
import org.junit.jupiter.api.Test;

class TurtleTextDocumentServiceTest {
   @Test
   void renamingDocumentClearsOldDiagnosticsAndPublishesNewDiagnostics() throws InterruptedException {
      final String oldUri = "file:///old.ttl";
      final String newUri = "file:///new.ttl";
      final String content = """
         <urn:subject> <urn:predicate> .
         <urn:other> <urn:predicate> .
         """;
      final BlockingQueue<PublishDiagnosticsParams> published = new LinkedBlockingQueue<>();
      final TurtleTextDocumentService service = new TurtleTextDocumentService();
      service.connect( new LanguageClient() {
         @Override
         public void telemetryEvent( final Object object ) {}

         @Override
         public void publishDiagnostics( final PublishDiagnosticsParams diagnostics ) {
            published.add( diagnostics );
         }

         @Override
         public void showMessage( final MessageParams messageParams ) {}

         @Override
         public CompletableFuture<MessageActionItem> showMessageRequest( final ShowMessageRequestParams requestParams ) {
            return CompletableFuture.completedFuture( null );
         }

         @Override
         public void logMessage( final MessageParams message ) {}
      } );

      try {
         service.didOpen( new DidOpenTextDocumentParams( new TextDocumentItem( oldUri, "turtle", 1, content ) ) );
         final PublishDiagnosticsParams original = published.poll( 5, TimeUnit.SECONDS );
         assertThat( original ).isNotNull();
         assertThat( original.getUri() ).isEqualTo( oldUri );
         assertThat( original.getDiagnostics() ).hasSize( 2 );

         service.didClose( new DidCloseTextDocumentParams( new TextDocumentIdentifier( oldUri ) ) );
         service.didOpen( new DidOpenTextDocumentParams( new TextDocumentItem( newUri, "turtle", 1, content ) ) );

         final PublishDiagnosticsParams cleared = published.poll( 5, TimeUnit.SECONDS );
         assertThat( cleared ).isNotNull();
         assertThat( cleared.getUri() ).isEqualTo( oldUri );
         assertThat( cleared.getDiagnostics() ).isEmpty();

         final PublishDiagnosticsParams renamed = published.poll( 5, TimeUnit.SECONDS );
         assertThat( renamed ).isNotNull();
         assertThat( renamed.getUri() ).isEqualTo( newUri );
         assertThat( renamed.getDiagnostics() ).isEqualTo( original.getDiagnostics() );
      } finally {
         service.shutdown();
      }
   }
}
