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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.eclipse.esmf.turtle.languageserver.aspect.request.ValidateDocumentParams;
import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TurtleTextDocumentService;
import org.eclipse.esmf.turtle.languageserver.lsp.workspace.TurtleWorkspaceService;
import org.eclipse.esmf.turtle.languageserver.structure.TurtleTokenService;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.SaveOptions;
import org.eclipse.lsp4j.SemanticTokensWithRegistrationOptions;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.TextDocumentSyncOptions;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurtleLanguageServer implements LanguageServer, LanguageClientAware {
   // R=18, D=4, F=6
   public static final int DEFAULT_PORT = 1846;

   private static final Logger LOG = LoggerFactory.getLogger( TurtleLanguageServer.class );
   private static volatile boolean serverRunning = true;
   private final TurtleTextDocumentService textDocumentService;
   private final TurtleWorkspaceService workspaceService;

   public TurtleLanguageServer() {
      textDocumentService = new TurtleTextDocumentService();
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
      capabilities.setSemanticTokensProvider(
            new SemanticTokensWithRegistrationOptions( TurtleTokenService.SUPPORTED_TOKEN_TYPES, true, false ) );
      return CompletableFuture.completedFuture( new InitializeResult( capabilities ) );
   }

   @Override
   public CompletableFuture<Object> shutdown() {
      textDocumentService.shutdown();
      return CompletableFuture.completedFuture( null );
   }

   @Override
   public void exit() {
      System.exit( 0 );
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
      if ( params == null || params.uri() == null ) {
         return CompletableFuture.completedFuture( DiagnosticReport.EMPTY );
      }
      return CompletableFuture.completedFuture( textDocumentService.validateDocument( params.uri() ) );
   }

   /**
    * Starts the language server using stdin/stdout communication.
    * This method does not return.
    */
   @SuppressWarnings( "UseOfSystemOutOrSystemErr" )
   public static void launchForStdio() {
      final TurtleLanguageServer server = new TurtleLanguageServer();
      try {
         final Launcher<LanguageClient> launcher = Launcher.createLauncher( server, LanguageClient.class, System.in, System.out );
         server.connect( launcher.getRemoteProxy() );
         launcher.startListening().get();
      } catch ( final InterruptedException exception ) {
         Thread.currentThread().interrupt();
         LOG.error( "Language server listener was interrupted", exception );
      } catch ( final Exception exception ) {
         LOG.error( "Language server terminated with an error", exception );
      }
   }

   /**
    * Starts the language server using socket communication.
    * This method does not return.
    *
    * @param port the port to listen on
    */
   public static void launchForSocket( final int port ) {
      try ( final AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open() ) {
         serverSocket.bind( new InetSocketAddress( "localhost", port ) );
         LOG.info( "Starting language server on port {}", port );

         while ( serverRunning ) {
            LOG.info( "Waiting for client connection on port {}", port );
            final AsynchronousSocketChannel socketChannel = serverSocket.accept().get();
            LOG.info( "Client connected" );

            // Handle each connection in a separate thread so we can immediately accept new connections
            final Thread clientThread = new Thread( () -> handleClientConnection( socketChannel ) );
            clientThread.setName( "LSP-Client-Handler-" + System.currentTimeMillis() );
            clientThread.setDaemon( true );
            clientThread.start();
         }
      } catch ( final IOException exception ) {
         LOG.error( "Could not launch language server", exception );
      } catch ( final InterruptedException exception ) {
         Thread.currentThread().interrupt();
         serverRunning = false;
         LOG.error( "Language server listener was interrupted", exception );
      } catch ( final ExecutionException exception ) {
         LOG.error( "Error accepting client connection", exception );
      }
   }

   private static void handleClientConnection( final AsynchronousSocketChannel socketChannel ) {
      try ( final var inputStream = Channels.newInputStream( socketChannel );
            final var outputStream = Channels.newOutputStream( socketChannel );
            final var executorService = Executors.newCachedThreadPool();
            socketChannel ) {
         final TurtleLanguageServer languageServer = new TurtleLanguageServer();
         final Launcher<LanguageClient> launcher =
               Launcher.createIoLauncher( languageServer, LanguageClient.class, inputStream,
                     outputStream, executorService, Function.identity() );
         languageServer.connect( launcher.getRemoteProxy() );
         launcher.startListening().get();
         LOG.info( "Client disconnected" );
      } catch ( final InterruptedException exception ) {
         Thread.currentThread().interrupt();
         LOG.error( "Client connection handler was interrupted", exception );
      } catch ( final Exception exception ) {
         LOG.error( "Error handling client connection", exception );
      }
   }
}
