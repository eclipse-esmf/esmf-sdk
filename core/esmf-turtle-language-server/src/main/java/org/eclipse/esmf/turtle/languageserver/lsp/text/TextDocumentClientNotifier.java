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

import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticMapper;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticReport;

import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.services.LanguageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextDocumentClientNotifier {
   private static final Logger LOG = LoggerFactory.getLogger( TextDocumentClientNotifier.class );

   private final DiagnosticMapper diagnosticMapper;
   private LanguageClient client;

   public TextDocumentClientNotifier( final DiagnosticMapper diagnosticMapper ) {
      this.diagnosticMapper = diagnosticMapper;
   }

   public void connect( final LanguageClient client ) {
      this.client = client;
   }

   public void publishDiagnostics( final Document document, final DiagnosticReport diagnostics ) {
      if ( client == null ) {
         LOG.warn( "[publishDiagnostics] client is null, skipping for URI={}", document.uri() );
         return;
      }

      LOG.debug( "[publish diagnostics] publishing {} diagnostic(s) for URI={}", diagnostics.diagnostics().size(), document.uri() );
      diagnosticMapper.apply( document, diagnostics ).forEach( ( uri, diags ) -> {
         client.publishDiagnostics( new PublishDiagnosticsParams( uri.toString(), diags ) );
      } );
   }

   public void publishEmptyDiagnostics( final String uri ) {
      if ( client == null ) {
         return;
      }

      client.publishDiagnostics( new PublishDiagnosticsParams( uri, List.of() ) );
   }
}
