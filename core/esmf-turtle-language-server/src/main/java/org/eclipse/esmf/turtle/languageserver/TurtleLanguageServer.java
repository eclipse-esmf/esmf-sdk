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

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.SaveOptions;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.TextDocumentSyncOptions;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.aspect.request.ValidateDocumentParams;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TurtleTextDocumentService;
import org.eclipse.esmf.turtle.languageserver.lsp.workspace.TurtleWorkspaceService;

public class TurtleLanguageServer implements LanguageServer, LanguageClientAware {
   private final TurtleTextDocumentService textDocumentService;
   private final TurtleWorkspaceService workspaceService;

   public TurtleLanguageServer() {
      this( new TurtleTextDocumentService() );
   }

   TurtleLanguageServer( final TurtleTextDocumentService textDocumentService ) {
      this.textDocumentService = textDocumentService;
      workspaceService = new TurtleWorkspaceService( textDocumentService );
   }

   @Override
   public CompletableFuture<InitializeResult> initialize( final InitializeParams params ) {
      final ServerCapabilities capabilities = new ServerCapabilities();
      final TextDocumentSyncOptions syncOptions = new TextDocumentSyncOptions();
      syncOptions.setOpenClose( true );
      syncOptions.setChange( TextDocumentSyncKind.Full );
      syncOptions.setSave( new SaveOptions( true ) );
      capabilities.setTextDocumentSync( syncOptions );
      capabilities.setDefinitionProvider( true );

      return CompletableFuture.completedFuture( new InitializeResult( capabilities ) );
   }

   @Override
   public CompletableFuture<Object> shutdown() {
      textDocumentService.shutdown();
      return CompletableFuture.completedFuture( null );
   }

   @Override
   public void exit() {
      throw new UnsupportedOperationException();
   }

   @Override
   public TextDocumentService getTextDocumentService() {
      return textDocumentService;
   }

   @Override
   public WorkspaceService getWorkspaceService() {
      return workspaceService;
   }

   @Override
   public void connect( final LanguageClient client ) {
      textDocumentService.connect( client );
   }

   @JsonRequest( "turtle/aspectValidation/validateDocument" )
   public CompletableFuture<DiagnosticReport> validateDocument( final ValidateDocumentParams params ) {
      final String uri = params != null ? params.uri() : null;
      return CompletableFuture.completedFuture( textDocumentService.validateDocument( uri ) );
   }
}
