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

import java.util.List;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.services.LanguageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextDocumentClientNotifier {
   private static final Logger LOG = LoggerFactory.getLogger( TextDocumentClientNotifier.class );

   private final DocumentDiagnosticsService diagnosticsService;
   private LanguageClient client;

   public TextDocumentClientNotifier( final DocumentDiagnosticsService diagnosticsService ) {
      this.diagnosticsService = diagnosticsService;
   }

   public void connect( final LanguageClient client ) {
      this.client = client;
   }

   public void publishCombinedDiagnostics( final String uri ) {
      if ( client == null ) {
         LOG.warn( "[publishDiagnostics] client is null, skipping for uri={}", uri );
         return;
      }

      final List<Diagnostic> diagnostics = diagnosticsService.getCombined( uri );
      LOG.debug( "[publish diagnostics] publishing {} diagnostic(s) for uri={}", diagnostics.size(), uri );
      client.publishDiagnostics( new PublishDiagnosticsParams( uri, diagnostics ) );
   }

   public void publishEmptyDiagnostics( final String uri ) {
      if ( client == null ) {
         return;
      }

      client.publishDiagnostics( new PublishDiagnosticsParams( uri, List.of() ) );
   }
}
