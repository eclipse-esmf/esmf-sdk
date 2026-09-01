/* Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0 */
package org.eclipse.esmf.turtle.languageserver.graphical.source;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GraphicalViewSourceContextTest {
   @Test
   void openSnapshotRemainsImmutableAfterDocumentChange() {
      final String uri = "file:///snapshot.ttl";
      final Document document = new Document( uri, "first" );
      final GraphicalViewSourceContext context = new GraphicalViewSourceContext( Map.of( uri, document ) );

      final GraphicalViewSourceSnapshot snapshot = context.requestSnapshot( uri ).orElseThrow();
      document.update( null, "second" );

      assertThat( snapshot.content() ).isEqualTo( "first" );
      assertThat( context.requestSnapshot( uri ).orElseThrow().content() ).isEqualTo( "second" );
   }

   @Test
   void closedPersistedSourceIsUnavailableUntilExplicitlyTrusted( @TempDir final Path directory ) throws Exception {
      final Path file = Files.writeString( directory.resolve( "Closed.ttl" ), "persisted" );
      final String uri = file.toUri().toString();
      final GraphicalViewSourceContext context = new GraphicalViewSourceContext( Map.of() );

      assertThat( context.requestSnapshot( uri ) ).isEmpty();
      context.trustAfterValidatedRender( uri );
      assertThat( context.requestSnapshot( uri ) ).get().extracting( GraphicalViewSourceSnapshot::content )
            .isEqualTo( "persisted" );
   }

   @Test
   void resolvedExternalSourceUsesItsCurrentOpenSnapshotWithoutFilesystemAccess( @TempDir final Path directory ) {
      final String uri = directory.resolve( "External.ttl" ).toUri().toString();
      final Map<String, Document> documents = new ConcurrentHashMap<>();
      documents.put( uri, new Document( uri, "current open external" ) );
      final GraphicalViewSourceContext context = new GraphicalViewSourceContext( documents );

      assertThat( context.resolvedSnapshot( java.net.URI.create( uri ) ) ).get()
            .extracting( GraphicalViewSourceSnapshot::content ).isEqualTo( "current open external" );
   }

   @Test
   void unsafeUriShapesAreRejectedBeforeFilesystemAccess() {
      for ( final String uri : java.util.List.of( "file:opaque.ttl", "file://server/share.ttl", "file:relative.ttl",
            "file:///tmp/../secret.ttl", "file:///bad%ZZ.ttl", "https://example/model.ttl" ) ) {
         assertThat( GraphicalViewSourceContext.isLocalFileUri( uri ) ).isFalse();
      }
      assertThat( GraphicalViewSourceContext.isLocalFileUri( "file:///tmp/model.ttl" ) ).isTrue();
   }
}
