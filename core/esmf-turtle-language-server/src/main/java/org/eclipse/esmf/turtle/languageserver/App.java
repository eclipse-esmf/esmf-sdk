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

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
   private static final Logger LOG = LoggerFactory.getLogger( App.class );

   static void main( final String[] args ) {
      LOG.info( "Starting lsp-server" );
      final TurtleLanguageServer server = new TurtleLanguageServer();

      try {
         final Launcher<LanguageClient> launcher = Launcher.createLauncher(
               server,
               LanguageClient.class,
               System.in,
               System.out
         );

         server.connect( launcher.getRemoteProxy() );
         launcher.startListening().get();
         LOG.info( "Language server listener stopped" );
      } catch ( final InterruptedException ex ) {
         Thread.currentThread().interrupt();
         LOG.error( "Language server listener was interrupted", ex );
      } catch ( final Exception ex ) {
         LOG.error( "Language server terminated with an error", ex );
      }
   }
}
