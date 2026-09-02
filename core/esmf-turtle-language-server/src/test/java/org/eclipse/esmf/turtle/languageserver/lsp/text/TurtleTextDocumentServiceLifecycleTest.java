/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0
 */
package org.eclipse.esmf.turtle.languageserver.lsp.text;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.eclipse.esmf.turtle.languageserver.graphical.protocol.GraphicalViewRenderParams;

import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TurtleTextDocumentServiceLifecycleTest {
   @Test
   void normalShutdownStopsLanguageAndGraphicalWorkerResources( @TempDir final Path directory ) throws Exception {
      final String model = """
         @prefix : <urn:samm:example.lifecycle:1.0.0#> .
         @prefix samm: <urn:samm:org.eclipse.esmf.samm:meta-model:2.2.0#> .
         :Lifecycle a samm:Aspect ; samm:properties (); samm:operations () .
         """;
      final String uri = Files.writeString( directory.resolve( "Lifecycle.ttl" ), model ).toUri().toString();
      final TurtleTextDocumentService service = new TurtleTextDocumentService();
      service.didOpen( new DidOpenTextDocumentParams( new TextDocumentItem( uri, "turtle", 1, model ) ) );
      service.semanticTokensFull( new SemanticTokensParams( new TextDocumentIdentifier( uri ) ) )
            .get( 5, TimeUnit.SECONDS );
      service.renderGraphicalView( new GraphicalViewRenderParams( uri ) ).get( 30, TimeUnit.SECONDS );
      assertThat( ownedThreads() ).isNotEmpty();

      service.shutdown();

      final long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos( 2 );
      while ( System.nanoTime() < deadline && !ownedThreads().isEmpty() ) {
         Thread.sleep( 10 );
      }
      assertThat( ownedThreads() ).isEmpty();
   }

   private static java.util.List<String> ownedThreads() {
      return Thread.getAllStackTraces().keySet().stream().filter( Thread::isAlive ).map( Thread::getName )
            .filter( name -> name.startsWith( "semantic-models-async-worker" ) || name.startsWith( "graphical-view-" ) )
            .toList();
   }
}
