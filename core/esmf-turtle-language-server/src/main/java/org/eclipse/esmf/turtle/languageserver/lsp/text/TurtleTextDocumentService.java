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

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.esmf.aspectmodel.ViolationReport;
import org.eclipse.esmf.turtle.languageserver.aspect.navigation.AspectCrossFileDefinitionService;
import org.eclipse.esmf.turtle.languageserver.lsp.ResolutionStrategyService;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.DiagnosticMapper;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.ResolutionStrategyAwareViolationProvider;
import org.eclipse.esmf.turtle.languageserver.lsp.diagnostic.ViolationProvider;
import org.eclipse.esmf.turtle.languageserver.structure.DocumentSymbolService;
import org.eclipse.esmf.turtle.languageserver.structure.TurtleTokenService;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleCompletionService;
import org.eclipse.esmf.turtle.languageserver.turtle.ValidationCoordinator;
import org.eclipse.esmf.turtle.languageserver.turtle.navigation.TurtleDefinitionService;

import com.google.common.collect.Streams;
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
   private final AspectCrossFileDefinitionService aspectCrossFileDefinitionService;
   private final TurtleCompletionService turtleCompletionService;
   private final ValidationCoordinator validationCoordinator;
   private final TreeSitterTurtleParserService turtleParserService;
   private final TurtleTokenService tokenService;
   private final DocumentSymbolService documentSymbolService;
   private final ResolutionStrategyService resolutionStrategyService = new ResolutionStrategyService();
   private final Map<String, Document> documents = new ConcurrentHashMap<>();
   private final ExecutorService asyncExecutor = Executors.newCachedThreadPool(
         r -> {
            final Thread t = new Thread( r, "semantic-models-async-worker" );
            t.setDaemon( true );
            return t;
         } );
   private final AtomicBoolean closed = new AtomicBoolean();

   public TurtleTextDocumentService() {
      clientNotifier = new TextDocumentClientNotifier( new DiagnosticMapper() );
      turtleDefinitionService = new TurtleDefinitionService();
      turtleCompletionService = new TurtleCompletionService();
      turtleParserService = new TreeSitterTurtleParserService();
      tokenService = new TurtleTokenService();
      aspectCrossFileDefinitionService = new AspectCrossFileDefinitionService( turtleParserService, documents, resolutionStrategyService );
      documentSymbolService = new DocumentSymbolService( turtleParserService );
      final List<ViolationProvider> violationProviders =
            Streams.stream( ServiceLoader.load( ViolationProvider.class ).iterator() ).toList();
      violationProviders.forEach( provider -> {
         if ( provider instanceof final ResolutionStrategyAwareViolationProvider aware ) {
            aware.setResolutionStrategyService( resolutionStrategyService );
         }
      } );
      validationCoordinator = new ValidationCoordinator( violationProviders, clientNotifier::publishDiagnostics, turtleParserService );
   }

   public void connect( final LanguageClient client ) {
      clientNotifier.connect( client );
   }

   public ResolutionStrategyService resolutionStrategyService() {
      return resolutionStrategyService;
   }

   public void shutdown() {
      if ( !closed.compareAndSet( false, true ) ) {
         return;
      }
      validationCoordinator.close();
      asyncExecutor.shutdownNow();
      try {
         if ( !asyncExecutor.awaitTermination( 5, TimeUnit.SECONDS ) ) {
            LOG.warn( "Timed out waiting for language-server request workers to stop" );
         }
      } catch ( final InterruptedException exception ) {
         Thread.currentThread().interrupt();
      }
      documents.clear();
      turtleParserService.close();
   }

   public CompletableFuture<ViolationReport> validateDocument( final String uri ) {
      final Document document = documents.get( uri );
      if ( document == null ) {
         return CompletableFuture.completedFuture( ViolationReport.EMPTY );
      }
      final ParsedDocument parsedDocument = turtleParserService.apply( document );
      return validationCoordinator.validateAsync( parsedDocument );
   }

   @Override
   public void didOpen( final DidOpenTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      final String content = params.getTextDocument().getText();
      LOG.info( "[didOpen] uri={}, contentLength={}", uri, content.length() );
      final Document document = new Document( URI.create( uri ), content );
      documents.put( uri, document );
      turtleParserService.onOpen( document );
      final ParsedDocument parsedDocument = turtleParserService.apply( document );
      validationCoordinator.onDocumentOpened( parsedDocument );
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
      LOG.debug( "[didChange] URI={}, changes={}", uri, params.getContentChanges().size() );
      final ParsedDocument parsedDocument = turtleParserService.apply( document );
      validationCoordinator.onDocumentChanged( parsedDocument );
   }

   @Override
   public void didClose( final DidCloseTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      LOG.info( "[didClose] uri={}", uri );
      final Document document = documents.remove( uri );
      if ( document != null ) {
         validationCoordinator.onDocumentClosed( document );
         turtleParserService.remove( document );
      }
   }

   @Override
   public void didSave( final DidSaveTextDocumentParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documents.get( uri );
      LOG.info( "[didSave] URI={}", uri );
      if ( document == null ) {
         LOG.warn( "[didSave] received save for unknown document: {}", uri );
         return;
      }
      document.rope().rebalance();
      turtleParserService.onOpen( document );
      final ParsedDocument parsedDocument = turtleParserService.apply( document );
      validationCoordinator.onDocumentSaved( parsedDocument );
   }

   @Override
   public CompletableFuture<SemanticTokens> semanticTokensFull( final SemanticTokensParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documents.get( uri );
      LOG.info( "[semanticTokensFull] URI={}", uri );
      if ( document == null ) {
         return CompletableFuture.completedFuture( new SemanticTokens( List.of() ) );
      }
      return CompletableFuture.supplyAsync(
            () -> turtleParserService.withParsedDocument( document, tokenService::buildSemanticTokens ), asyncExecutor );
   }

   @Override
   public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition( final DefinitionParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documents.get( uri );
      if ( document == null ) {
         return CompletableFuture.completedFuture( Either.forLeft( List.of() ) );
      }

      return CompletableFuture.supplyAsync( () -> {
         return turtleParserService.withParsedDocument( document, parsedDocument -> {
            Optional<Location> declaration = turtleDefinitionService.findDefinition( parsedDocument, params.getPosition() );

            if ( declaration.isEmpty() && aspectCrossFileDefinitionService != null ) {
               declaration = aspectCrossFileDefinitionService.findDefinition( parsedDocument, params.getPosition() );
            }

            return declaration
                  .<Either<List<? extends Location>, List<? extends LocationLink>>>map(
                        location -> Either.forLeft( List.of( location ) ) )
                  .orElseGet( () -> Either.forLeft( List.of() ) );
         } );
      }, asyncExecutor );
   }

   @Override
   public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol( final DocumentSymbolParams params ) {
      final String uri = params.getTextDocument().getUri();
      final Document document = documents.get( uri );
      LOG.debug( "[documentSymbol] URI={}", uri );
      if ( document == null ) {
         return CompletableFuture.completedFuture( List.of() );
      }
      return CompletableFuture.supplyAsync( () -> turtleParserService.withParsedDocument( document,
            parsedDocument -> documentSymbolService.symbols( parsedDocument ).stream()
                  .map( Either::<SymbolInformation, DocumentSymbol>forRight ).toList() ),
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
         return turtleParserService.withParsedDocument( document,
               parsedDocument -> Either.forLeft( turtleCompletionService.complete( parsedDocument, position ) ) );
      }, asyncExecutor );
   }
}
