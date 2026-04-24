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
import java.util.concurrent.CompletableFuture;

import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationResult;
import org.eclipse.esmf.turtle.languageserver.aspect.service.AspectModelValidationService;
import org.eclipse.esmf.turtle.languageserver.aspect.service.AspectValidationCoordinator;
import org.eclipse.esmf.turtle.languageserver.aspect.service.DefaultAspectModelValidationService;
import org.eclipse.esmf.turtle.languageserver.turtle.navigation.TurtlePrefixDefinitionService;

import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurtleTextDocumentService implements TextDocumentService {
   private static final Logger LOG = LoggerFactory.getLogger( TurtleTextDocumentService.class );
   private final DocumentStore documentStore;
   private final DocumentDiagnosticsService diagnosticsService;
   private final TextDocumentClientNotifier clientNotifier;
   private final TurtlePrefixDefinitionService prefixDefinitionService;
   private final DocumentAspectValidationService documentValidationService;
   private final AspectDiagnosticsWorkflow aspectDiagnosticsWorkflow;
   private final AspectValidationCoordinator aspectValidationCoordinator;
   private final TurtleParserService turtleParserService;

   public TurtleTextDocumentService() {
      this( new DefaultAspectModelValidationService() );
   }

   public TurtleTextDocumentService( final AspectModelValidationService aspectValidationService ) {
      this(
            new DocumentStore(),
            new DocumentDiagnosticsService(),
            new TurtlePrefixDefinitionService(),
            new AspectValidationCoordinator( aspectValidationService ),
            new TurtleParserService()
      );
   }

   TurtleTextDocumentService(
         final DocumentStore documentStore,
         final DocumentDiagnosticsService diagnosticsService,
         final TurtlePrefixDefinitionService prefixDefinitionService,
         final AspectValidationCoordinator aspectValidationCoordinator,
         final TurtleParserService turtleParserService ) {
      this.documentStore = documentStore;
      this.diagnosticsService = diagnosticsService;
      this.prefixDefinitionService = prefixDefinitionService;
      this.aspectValidationCoordinator = aspectValidationCoordinator;
      clientNotifier = new TextDocumentClientNotifier( diagnosticsService );
      documentValidationService = new DocumentAspectValidationService( aspectValidationCoordinator );
      aspectDiagnosticsWorkflow =
            new AspectDiagnosticsWorkflow( aspectValidationCoordinator, diagnosticsService, clientNotifier, documentStore );
      this.turtleParserService = turtleParserService;
   }

   public void connect( final LanguageClient client ) {
      clientNotifier.connect( client );
   }

   public void shutdown() {
      aspectValidationCoordinator.close();
   }

   public AspectValidationResult validateDocument( final String uri ) {
      return documentValidationService.validateDocument( uri, documentStore.get( uri ) );
   }

   @Override
   public void didOpen( final DidOpenTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      final String content = params.getTextDocument().getText();
      LOG.info( "[didOpen] uri={}, contentLength={}", uri, content.length() );
      final Document document = new Document( uri, content );
      documentStore.put( document );
      diagnosticsService.updateSyntax( document );
      clientNotifier.publishCombinedDiagnostics( uri );
   }

   @Override
   public void didChange( final DidChangeTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documentStore.get( params.getTextDocument().getUri() );
      for ( final TextDocumentContentChangeEvent change : params.getContentChanges() ) {
         document.update( change.getRange(), change.getText() );
         turtleParserService.onChange( document, change );
      }
      LOG.debug( "[didChange] uri={}, changes={}", uri, params.getContentChanges().size() );
      diagnosticsService.updateSyntax( document );
      aspectDiagnosticsWorkflow.onDocumentChanged( uri );
      clientNotifier.publishCombinedDiagnostics( uri );
   }

   @Override
   public void didClose( final DidCloseTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      LOG.info( "[didClose] uri={}", uri );
      documentStore.remove( uri );
      aspectDiagnosticsWorkflow.onDocumentClosed( uri );
      clientNotifier.publishEmptyDiagnostics( uri );
   }

   @Override
   public void didSave( final DidSaveTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documentStore.get( uri );
      LOG.info( "[didSave] uri={}", uri );
      diagnosticsService.updateSyntax( document );
      clientNotifier.publishCombinedDiagnostics( uri );
      aspectDiagnosticsWorkflow.onDocumentSaved( uri );
   }

   @Override
   public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition( final DefinitionParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documentStore.get( uri );
      if ( document == null ) {
         return CompletableFuture.completedFuture( Either.forLeft( List.of() ) );
      }

      final Location declaration = prefixDefinitionService.findPrefixDeclaration( document, params.getPosition() );
      if ( declaration == null ) {
         return CompletableFuture.completedFuture( Either.forLeft( List.of() ) );
      }

      return CompletableFuture.completedFuture( Either.forLeft( List.of( declaration ) ) );
   }
}
