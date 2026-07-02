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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectDiagnosticMapper;
import org.eclipse.esmf.turtle.languageserver.aspect.service.AspectModelValidationService;
import org.eclipse.esmf.turtle.languageserver.aspect.service.AspectValidationCoordinator;
import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.structure.DocumentSymbolService;
import org.eclipse.esmf.turtle.languageserver.structure.TurtleTokenService;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleCompletionService;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleSyntaxDiagnosticsService;
import org.eclipse.esmf.turtle.languageserver.turtle.navigation.MetaModelStrategy;
import org.eclipse.esmf.turtle.languageserver.turtle.navigation.TurtleCrossFileDefinitionService;
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
   private final TurtleCrossFileDefinitionService turtleCrossFileDefinitionService;
   private final TurtleCompletionService turtleCompletionService;
   private final AspectValidationCoordinator aspectValidationCoordinator;
   private final TreeSitterTurtleParserService turtleParserService;
   private final TurtleTokenService tokenService;
   private final DocumentSymbolService documentSymbolService;
   private final TurtleSyntaxDiagnosticsService syntaxDiagnostics;
   private final AspectModelValidationService aspectModelValidation;
   private final Map<String, Document> documents = new ConcurrentHashMap<>();
   private final ExecutorService asyncExecutor = Executors.newCachedThreadPool(
         r -> {
            final Thread t = new Thread( r, "lsp-async-worker" );
            t.setDaemon( true );
            return t;
         } );

   public TurtleTextDocumentService() {
      clientNotifier = new TextDocumentClientNotifier( new AspectDiagnosticMapper() );
      turtleDefinitionService = new TurtleDefinitionService();
      turtleCompletionService = new TurtleCompletionService();
      turtleParserService = new TreeSitterTurtleParserService();
      tokenService = new TurtleTokenService( turtleParserService );
      turtleCrossFileDefinitionService = new TurtleCrossFileDefinitionService( turtleParserService, documents );
      syntaxDiagnostics = new TurtleSyntaxDiagnosticsService();
      aspectModelValidation = new AspectModelValidationService();
      documentSymbolService = new DocumentSymbolService( turtleParserService );
      // When the coordinator completes an async aspect validation it calls back here.
      // We merge the fresh aspect report with a fresh syntax report and publish once,
      // ensuring neither layer can overwrite the other.
      final BiConsumer<Document, DiagnosticReport> validationCallback = ( document, aspectReport ) -> {
         final ParsedDocument parsedDocument = turtleParserService.apply( document );
         final DiagnosticReport syntaxReport = syntaxDiagnostics.defaultValidate( parsedDocument );
         clientNotifier.publishDiagnostics( document, syntaxReport.merge( aspectReport ) );
      };
      aspectValidationCoordinator = new AspectValidationCoordinator( aspectModelValidation, validationCallback );
   }

   public void connect( final LanguageClient client ) {
      clientNotifier.connect( client );
   }

   public void shutdown() {
      aspectValidationCoordinator.close();
      asyncExecutor.shutdown();
   }

   public DiagnosticReport validateDocument( final String uri ) {
      final Document document = documents.get( uri );
      if ( document == null ) {
         return DiagnosticReport.EMPTY;
      }
      final ParsedDocument parsedDocument = turtleParserService.apply( document );
      DiagnosticReport diagnosticReport = syntaxDiagnostics.defaultValidate( parsedDocument );
      if ( !MetaModelStrategy.isMetaModelUri( parsedDocument.getUri() ) && parsedDocument.isAspectModel() ) {
         final DiagnosticReport aspectReport = aspectModelValidation.defaultValidate( parsedDocument );
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

      DiagnosticReport diagnosticReport = DiagnosticReport.EMPTY;
      if ( !MetaModelStrategy.isMetaModelUri( parsedDocument.getUri() ) ) {
         diagnosticReport = syntaxDiagnostics.defaultValidate( parsedDocument );
         if ( parsedDocument.isAspectModel() ) {
            final DiagnosticReport aspectReport = aspectModelValidation.onOpen( parsedDocument );
            diagnosticReport = diagnosticReport.merge( aspectReport );
            aspectValidationCoordinator.seedCache( document, aspectReport );
         }
      }

      clientNotifier.publishDiagnostics( document, diagnosticReport );
   }

   @Override
   public void didChange( final DidChangeTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documents.get( uri );
      if ( document == null ) {
         LOG.warn( "[didChange] received change for unknown document: {}", uri );
         return;
      }
      for ( final TextDocumentContentChangeEvent change : params.getContentChanges() ) {
         document.update( change.getRange(), change.getText() );
         turtleParserService.onChange( document, change );
      }
      LOG.debug( "[didChange] uri={}, changes={}", uri, params.getContentChanges().size() );
      final ParsedDocument parsedDocument = turtleParserService.apply( document );

      DiagnosticReport diagnosticReport = DiagnosticReport.EMPTY;
      if ( !MetaModelStrategy.isMetaModelUri( parsedDocument.getUri() ) ) {
         diagnosticReport = syntaxDiagnostics.defaultValidate( parsedDocument );
         if ( parsedDocument.isAspectModel() ) {
            diagnosticReport = diagnosticReport.merge( aspectValidationCoordinator.getCachedDiagnostics( document ) );
            aspectValidationCoordinator.onDocumentChanged( parsedDocument );
         }
      }

      clientNotifier.publishDiagnostics( document, diagnosticReport );
   }

   @Override
   public void didClose( final DidCloseTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      LOG.info( "[didClose] uri={}", uri );
      final Document document = documents.remove( uri );
      if ( document != null ) {
         aspectValidationCoordinator.onDocumentClosed( document );
      }
   }

   @Override
   public void didSave( final DidSaveTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documents.get( uri );
      LOG.info( "[didSave] uri={}", uri );
      if ( document == null ) {
         LOG.warn( "[didSave] received save for unknown document: {}", uri );
         return;
      }
      document.getRope().rebalance();
      turtleParserService.onOpen( document );
      final ParsedDocument parsedDocument = turtleParserService.apply( document );

      DiagnosticReport diagnosticReport = DiagnosticReport.EMPTY;
      if ( !MetaModelStrategy.isMetaModelUri( parsedDocument.getUri() ) ) {
         diagnosticReport = syntaxDiagnostics.defaultValidate( parsedDocument );
         if ( parsedDocument.isAspectModel() ) {
            diagnosticReport = diagnosticReport.merge( aspectValidationCoordinator.getCachedDiagnostics( document ) );
            aspectValidationCoordinator.onDocumentSaved( parsedDocument );
         }
      }

      clientNotifier.publishDiagnostics( document, diagnosticReport );
   }

   @Override
   public CompletableFuture<SemanticTokens> semanticTokensFull( final SemanticTokensParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documents.get( uri );
      LOG.info( "[semanticTokensFull] uri={}", uri );
      if ( document == null ) {
         return CompletableFuture.completedFuture( new SemanticTokens( List.of() ) );
      }
      return CompletableFuture.supplyAsync( () -> tokenService.buildSemanticTokens( document ), asyncExecutor );
   }

   @Override
   public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition( final DefinitionParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documents.get( uri );
      if ( document == null ) {
         return CompletableFuture.completedFuture( Either.forLeft( List.of() ) );
      }

      return CompletableFuture.supplyAsync( () -> {
         final ParsedDocument parsedDocument = turtleParserService.apply( document );
         Optional<Location> declaration = turtleDefinitionService.findDefinition( parsedDocument, params.getPosition() );

         if ( declaration.isEmpty() && turtleCrossFileDefinitionService != null ) {
            declaration = turtleCrossFileDefinitionService.findDefinition( parsedDocument, params.getPosition() );
         }

         return declaration
               .<Either<List<? extends Location>, List<? extends LocationLink>>>map(
                     location -> Either.forLeft( List.of( location ) ) )
               .orElseGet( () -> Either.forLeft( List.of() ) );
      }, asyncExecutor );
   }

   @Override
   public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol( final DocumentSymbolParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documents.get( uri );
      LOG.debug( "[documentSymbol] uri={}", uri );
      if ( document == null ) {
         return CompletableFuture.completedFuture( List.of() );
      }
      return CompletableFuture.supplyAsync( () ->
            documentSymbolService.symbols( document ).stream()
                  .map( Either::<SymbolInformation, DocumentSymbol>forRight )
                  .toList(),
            asyncExecutor );
   }

   @Override
   public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion( final CompletionParams position ) {
      final String uri = position.getTextDocument().getUri();
      final Document document = documents.get( uri );
      if ( document == null ) {
         return CompletableFuture.completedFuture( Either.forLeft( List.of() ) );
      }
      return CompletableFuture.supplyAsync( () -> {
         final ParsedDocument parsedDocument = turtleParserService.apply( document );
         return Either.<List<CompletionItem>, CompletionList>forLeft(
               turtleCompletionService.complete( parsedDocument, position ) );
      }, asyncExecutor );
   }
}
