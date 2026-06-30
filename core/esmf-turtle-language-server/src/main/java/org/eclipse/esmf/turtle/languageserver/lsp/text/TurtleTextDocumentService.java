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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectDiagnosticMapper;
import org.eclipse.esmf.turtle.languageserver.aspect.service.AspectDocumentValidationService;
import org.eclipse.esmf.turtle.languageserver.aspect.service.AspectValidationCoordinator;
import org.eclipse.esmf.turtle.languageserver.aspect.service.ParsedAspectModelFileLoader;
import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.structure.DocumentSymbolService;
import org.eclipse.esmf.turtle.languageserver.structure.TurtleTokenService;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleCompletionService;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleSyntaxDiagnosticsService;
import org.eclipse.esmf.turtle.languageserver.turtle.navigation.TurtleDefinitionService;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.DocumentSymbolParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurtleTextDocumentService implements TextDocumentService {
   private static final Logger LOG = LoggerFactory.getLogger( TurtleTextDocumentService.class );

   private final TextDocumentClientNotifier clientNotifier;
   private final TurtleDefinitionService turtleDefinitionService;
   private final TurtleCompletionService turtleCompletionService;
   private final AspectValidationCoordinator aspectValidationCoordinator;
   private final TreeSitterTurtleParserService turtleParserService;
   private final TurtleTokenService tokenService;
   private final DocumentSymbolService documentSymbolService;
   private final TurtleSyntaxDiagnosticsService syntaxDiagnostics;
   private final ParsedAspectModelFileLoader aspectModelFileLoader;
   private final AspectDocumentValidationService aspectDocumentValidationService;
   private final Map<String, Document> documents = new HashMap<>();

   public TurtleTextDocumentService() {
      clientNotifier = new TextDocumentClientNotifier( new AspectDiagnosticMapper() );
      turtleDefinitionService = new TurtleDefinitionService();
      turtleCompletionService = new TurtleCompletionService();
      turtleParserService = new TreeSitterTurtleParserService();
      tokenService = new TurtleTokenService( turtleParserService );
      syntaxDiagnostics = new TurtleSyntaxDiagnosticsService();
      aspectModelFileLoader = new ParsedAspectModelFileLoader();
      aspectDocumentValidationService = new AspectDocumentValidationService();
      documentSymbolService = new DocumentSymbolService( turtleParserService );
      // When the coordinator completes an async aspect validation it calls back here.
      // We merge the fresh aspect report with a fresh syntax report and publish once,
      // ensuring neither layer can overwrite the other.
      final BiConsumer<Document, DiagnosticReport> validationCallback = ( document, aspectReport ) -> {
         final ParsedDocument parsedDocument = turtleParserService.apply( document );
         final DiagnosticReport syntaxReport = syntaxDiagnostics.validate( parsedDocument );
         clientNotifier.publishDiagnostics( document, syntaxReport.merge( aspectReport ) );
      };
      aspectValidationCoordinator = new AspectValidationCoordinator( aspectDocumentValidationService, validationCallback );
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
      DiagnosticReport diagnosticReport = syntaxDiagnostics.validate( parsedDocument );
      if ( aspectModelFileLoader.supports( parsedDocument ) ) {
         final DiagnosticReport aspectReport = aspectDocumentValidationService.validate( parsedDocument );
         aspectValidationCoordinator.seedCache( document, aspectReport );
         diagnosticReport = diagnosticReport.merge( aspectReport );
      }
      return diagnosticReport;
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

      DiagnosticReport diagnosticReport = syntaxDiagnostics.validate( parsedDocument );
      if ( aspectModelFileLoader.supports( parsedDocument ) ) {
         diagnosticReport = diagnosticReport.merge( aspectValidationCoordinator.getCachedDiagnostics( document ) );
         aspectValidationCoordinator.onDocumentOpened( parsedDocument );
      }

      clientNotifier.publishDiagnostics( document, diagnosticReport );
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

      DiagnosticReport diagnosticReport = syntaxDiagnostics.validate( parsedDocument );
      if ( aspectModelFileLoader.supports( parsedDocument ) ) {
         diagnosticReport = diagnosticReport.merge( aspectValidationCoordinator.getCachedDiagnostics( document ) );
         aspectValidationCoordinator.onDocumentChanged( parsedDocument );
      }

      clientNotifier.publishDiagnostics( document, diagnosticReport );
   }

   @Override
   public void didClose( final DidCloseTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      LOG.info( "[didClose] uri={}", uri );
      final Document document = documents.get( uri );
      aspectValidationCoordinator.onDocumentClosed( document );
      documents.remove( uri );
   }

   @Override
   public void didSave( final DidSaveTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documents.get( uri );
      LOG.info( "[didSave] uri={}", uri );
      document.getRope().rebalance();
      turtleParserService.onOpen( document );
      final ParsedDocument parsedDocument = turtleParserService.apply( document );

      DiagnosticReport diagnosticReport = syntaxDiagnostics.validate( parsedDocument );
      if ( aspectModelFileLoader.supports( parsedDocument ) ) {
         diagnosticReport = diagnosticReport.merge( aspectValidationCoordinator.getCachedDiagnostics( document ) );
         aspectValidationCoordinator.onDocumentSaved( parsedDocument );
      }

      clientNotifier.publishDiagnostics( document, diagnosticReport );
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

      final ParsedDocument parsedDocument = turtleParserService.apply( document );
      final Optional<Location> declaration =
            turtleDefinitionService.findDefinition( parsedDocument, params.getPosition() );
      return declaration.<CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>>>map(
            location -> CompletableFuture.completedFuture( Either.forLeft( List.of( location ) ) ) )
            .orElseGet( () -> CompletableFuture.completedFuture( Either.forLeft( List.of() ) ) );
   }

   @Override
   public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol( final DocumentSymbolParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documents.get( uri );
      LOG.debug( "[documentSymbol] uri={}", uri );
      if ( document == null ) {
         return CompletableFuture.completedFuture( List.of() );
      }
      final List<Either<SymbolInformation, DocumentSymbol>> symbols = documentSymbolService.symbols( document ).stream()
            .map( Either::<SymbolInformation, DocumentSymbol>forRight ).toList();
      return CompletableFuture.completedFuture( symbols );
   }

   @Override
   public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion( final CompletionParams position ) {
      final String uri = position.getTextDocument().getUri();
      final Document document = documents.get( uri );
      final ParsedDocument parsedDocument = turtleParserService.apply( document );
      return CompletableFuture.completedFuture(
            Either.forLeft( turtleCompletionService.complete( parsedDocument, position ) ) );
   }
}
