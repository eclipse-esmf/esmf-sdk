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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
   public static final int PORT = 1846;
   private static final Logger LOG = LoggerFactory.getLogger( App.class );

   static void main( final String[] args ) {
      LOG.info( "Starting lsp-server on port {}", PORT );
      final TurtleLanguageServer languageServer = new TurtleLanguageServer();
      try ( final AsynchronousServerSocketChannel serverSocket = AsynchronousServerSocketChannel.open() ) {
         serverSocket.bind( new InetSocketAddress( "localhost", PORT ) );
         final AsynchronousSocketChannel socketChannel = serverSocket.accept().get();
         final Launcher<LanguageClient> launcher =
               Launcher.createIoLauncher( languageServer, LanguageClient.class, Channels.newInputStream( socketChannel ),
                     Channels.newOutputStream( socketChannel ), Executors.newCachedThreadPool(), Function.identity() );
         final Future<?> future = launcher.startListening();
         languageServer.connect( launcher.getRemoteProxy() );
         while ( !future.isDone() ) {
            // noinspection BusyWait
            Thread.sleep( 10_000L );
         }
      } catch ( final InterruptedException | ExecutionException | IOException exception ) {
         LOG.warn( "Could not launch Language Server", exception );
      }
   }
}
