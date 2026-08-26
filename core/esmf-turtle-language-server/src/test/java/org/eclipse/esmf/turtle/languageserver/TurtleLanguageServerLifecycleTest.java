/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.turtle.languageserver;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.esmf.turtle.languageserver.lsp.text.TurtleTextDocumentService;

import org.junit.jupiter.api.Test;

class TurtleLanguageServerLifecycleTest {
   @Test
   void normalShutdownClosesGraphicalAndLanguageFeatureExecutors() throws Exception {
      final TurtleLanguageServer server = new TurtleLanguageServer();

      server.shutdown().get( 1, TimeUnit.SECONDS );

      assertThat( textDocuments( server ).executorsShutdown() ).isTrue();
      assertThat( awaitExecutorTermination( server ) ).isTrue();
   }

   @Test
   void abruptSocketDisconnectClosesPerConnectionServerExecutorsInFinally() throws Exception {
      final TurtleLanguageServer server = new TurtleLanguageServer();
      try ( final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open() ) {
         listener.bind( new InetSocketAddress( "localhost", 0 ) );
         final java.util.concurrent.Future<AsynchronousSocketChannel> accepted = listener.accept();
         final AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
         client.connect( listener.getLocalAddress() ).get( 2, TimeUnit.SECONDS );
         final AsynchronousSocketChannel serverSocket = accepted.get( 2, TimeUnit.SECONDS );
         final CompletableFuture<Void> handler = CompletableFuture.runAsync(
               () -> TurtleLanguageServer.handleClientConnection( serverSocket, server ) );

         client.close();
         handler.get( 5, TimeUnit.SECONDS );
      }

      assertThat( textDocuments( server ).executorsShutdown() ).isTrue();
      assertThat( awaitExecutorTermination( server ) ).isTrue();
   }

   private static boolean awaitExecutorTermination( final TurtleLanguageServer server ) throws InterruptedException {
      final long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos( 2 );
      while ( System.nanoTime() < deadline && !textDocuments( server ).executorsTerminated() ) {
         Thread.sleep( 10 );
      }
      return textDocuments( server ).executorsTerminated();
   }

   private static TurtleTextDocumentService textDocuments( final TurtleLanguageServer server ) {
      return (TurtleTextDocumentService) server.getTextDocumentService();
   }
}
