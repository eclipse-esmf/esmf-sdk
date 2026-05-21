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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectDiagnosticMapper;
import org.eclipse.esmf.turtle.languageserver.aspect.service.AspectModelValidationService;
import org.eclipse.esmf.turtle.languageserver.aspect.service.AspectValidationCoordinator;
import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDiagnosticsService;
import org.eclipse.esmf.turtle.languageserver.structure.TurtleTokenService;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleSyntaxDiagnosticsService;
import org.eclipse.esmf.turtle.languageserver.turtle.navigation.TurtlePrefixDefinitionService;

import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurtleTextDocumentService implements TextDocumentService {
   private static final Logger LOG = LoggerFactory.getLogger( TurtleTextDocumentService.class );

   private final TextDocumentClientNotifier clientNotifier;
   private final TurtlePrefixDefinitionService prefixDefinitionService;
   private final AspectValidationCoordinator aspectValidationCoordinator;
   private final TreeSitterTurtleParserService turtleParserService;
   private final AspectDiagnosticsWorkflow aspectDiagnosticsWorkflow;
   private final TurtleTokenService tokenService;
   private final TurtleDiagnosticsService syntaxDiagnostics;
   private final TurtleDiagnosticsService aspectModelValidation;
   private final Map<String, Document> documents = new HashMap<>();

   public TurtleTextDocumentService() {
      clientNotifier = new TextDocumentClientNotifier( new AspectDiagnosticMapper() );
      prefixDefinitionService = new TurtlePrefixDefinitionService();
      aspectValidationCoordinator = new AspectValidationCoordinator( new AspectModelValidationService() );
      turtleParserService = new TreeSitterTurtleParserService();
      aspectDiagnosticsWorkflow = new AspectDiagnosticsWorkflow( aspectValidationCoordinator, clientNotifier );
      tokenService = new TurtleTokenService( turtleParserService );
      syntaxDiagnostics = new TurtleSyntaxDiagnosticsService();
      aspectModelValidation = new AspectModelValidationService();
   }

   public void connect( final LanguageClient client ) {
      clientNotifier.connect( client );
   }

   public void shutdown() {
      aspectValidationCoordinator.close();
   }

   public DiagnosticReport validateDocument( final String uri ) {
      final Document document = documents.get( uri );
      if ( document == null ) {
         return DiagnosticReport.EMPTY;
      }
      final ParsedDocument parsedDocument = turtleParserService.apply( document );
      return syntaxDiagnostics.defaultValidate( parsedDocument )
            .merge( aspectModelValidation.defaultValidate( parsedDocument ) );
   }

   @Override
   public void didOpen( final DidOpenTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      final String content = params.getTextDocument().getText();
      LOG.info( "[didOpen] uri={}, contentLength={}", uri, content.length() );
      final Document document = new Document( uri, content );
      documents.put( uri, document );
      turtleParserService.onOpen( document );
      final ParsedDocument parsedDocument = turtleParserService.apply( document );
      final DiagnosticReport report = syntaxDiagnostics.onOpen( parsedDocument )
            .merge( aspectModelValidation.onOpen( parsedDocument ) );
      clientNotifier.publishDiagnostics( document, report );
   }

   @Override
   public void didChange( final DidChangeTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documents.get( params.getTextDocument().getUri() );
      for ( final TextDocumentContentChangeEvent change : params.getContentChanges() ) {
         document.update( change.getRange(), change.getText() );
         turtleParserService.onChange( document, change );
      }
      LOG.debug( "[didChange] uri={}, changes={}", uri, params.getContentChanges().size() );
      final ParsedDocument parsedDocument = turtleParserService.apply( document );
      final DiagnosticReport syntaxReport = syntaxDiagnostics.onChange( parsedDocument );
      final DiagnosticReport report = syntaxReport.isEmpty()
            ? syntaxReport
            : syntaxReport.merge( aspectModelValidation.onChange( parsedDocument ) );
      clientNotifier.publishDiagnostics( document, report );
      aspectDiagnosticsWorkflow.onDocumentChanged( document );
   }

   @Override
   public void didClose( final DidCloseTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      LOG.info( "[didClose] uri={}", uri );
      final Document document = documents.get( uri );
      aspectDiagnosticsWorkflow.onDocumentClosed( document );
      documents.remove( uri );
      clientNotifier.publishEmptyDiagnostics( uri );
   }

   @Override
   public void didSave( final DidSaveTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documents.get( uri );
      LOG.info( "[didSave] uri={}", uri );
      document.getRope().rebalance();
      turtleParserService.onOpen( document );
      final ParsedDocument parsedDocument = turtleParserService.apply( document );
      final DiagnosticReport report = syntaxDiagnostics.onSave( parsedDocument )
            .merge( aspectModelValidation.onSave( parsedDocument ) );
      clientNotifier.publishDiagnostics( document, report );
      aspectDiagnosticsWorkflow.onDocumentSaved( parsedDocument );
   }

   @Override
   public CompletableFuture<SemanticTokens> semanticTokensFull( final SemanticTokensParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documents.get( uri );
      LOG.info( "[semanticTokensFull] uri={}", uri );
      final SemanticTokens semanticTokens = tokenService.buildSemanticTokens( document );
      return CompletableFuture.completedFuture( semanticTokens );
   }

   @Override
   public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition( final DefinitionParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documents.get( uri );
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
