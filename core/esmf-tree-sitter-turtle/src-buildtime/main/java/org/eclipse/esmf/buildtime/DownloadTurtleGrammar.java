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

package org.eclipse.esmf.buildtime;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

public class DownloadTurtleGrammar extends BuildTimeTool {
   private static final String GRAMMAR_JS_SHA1 = "6146fa24f0dd601a34025ef3618c3cd4d5dfdb4b";

   public DownloadTurtleGrammar( final Path cacheLocation ) {
      super( cacheLocation );
   }

   public void downloadTurtleGrammar() {
      final URL url = url( "https://raw.githubusercontent.com/GordianDziwis/tree-sitter-turtle/refs/heads/main/grammar.js" );
      final File grammarJs = getOrDownloadFile( url );
      if ( !grammarJs.exists() ) {
         throw new BuildTimeException( "Downloading Turtle grammar failed" );
      }
      final String sha1 = sha1( grammarJs );
      if ( !sha1.equals( GRAMMAR_JS_SHA1 ) ) {
         throw new BuildTimeException( "Invalid SHA1 sum for grammar.js. Expected: " + GRAMMAR_JS_SHA1 + ", found: " + sha1 );
      }
   }

   static void main( final String[] args ) {
      final Path cacheLocation = Path.of( args[0] );
      final DownloadTurtleGrammar downloadTurtleGrammar = new DownloadTurtleGrammar( cacheLocation );
      downloadTurtleGrammar.downloadTurtleGrammar();
   }
}
