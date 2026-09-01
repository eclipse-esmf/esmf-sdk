/* Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
 * SPDX-License-Identifier: MPL-2.0 */
package org.eclipse.esmf.turtle.languageserver.graphical.source;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;

/** Owns graphical-view source snapshots, local-URI policy and trusted persisted fallback. */
public final class GraphicalViewSourceContext {
   private final Map<String, Document> documents;
   private final Set<String> trustedRenderedSourceUris = java.util.concurrent.ConcurrentHashMap.newKeySet();

   public GraphicalViewSourceContext( final Map<String, Document> documents ) {
      this.documents = documents;
   }

   public Optional<GraphicalViewSourceSnapshot> openSnapshot( final String sourceUri ) {
      if ( !isLocalFileUri( sourceUri ) ) {
         return Optional.empty();
      }
      return snapshotOpenDocument( sourceUri );
   }

   public Optional<GraphicalViewSourceSnapshot> requestSnapshot( final String sourceUri ) {
      if ( !isLocalFileUri( sourceUri ) ) {
         return Optional.empty();
      }
      return snapshotOpenDocument( sourceUri ).or( () -> trustedRenderedSourceUris.contains( sourceUri )
            ? persistedSnapshot( URI.create( sourceUri ) )
            : Optional.empty() );
   }

   /** Loads a target URI produced by the established model resolution strategy. */
   public Optional<GraphicalViewSourceSnapshot> resolvedSnapshot( final URI resolvedUri ) {
      if ( resolvedUri == null || !isLocalFileUri( resolvedUri.toString() ) ) {
         return Optional.empty();
      }
      return snapshotOpenDocument( resolvedUri.toString() ).or( () -> persistedSnapshot( resolvedUri ) );
   }

   public void trustAfterValidatedRender( final String sourceUri ) {
      if ( isLocalFileUri( sourceUri ) ) {
         trustedRenderedSourceUris.add( sourceUri );
      }
   }

   public static boolean isLocalFileUri( final String value ) {
      try {
         final URI uri = URI.create( value );
         if ( !"file".equalsIgnoreCase( uri.getScheme() ) || uri.isOpaque() || uri.getAuthority() != null
               || uri.getRawPath() == null ) {
            return false;
         }
         final Path path = Path.of( uri );
         return path.isAbsolute() && path.normalize().equals( path );
      } catch ( final IllegalArgumentException exception ) {
         return false;
      }
   }

   private Optional<GraphicalViewSourceSnapshot> snapshotOpenDocument( final String sourceUri ) {
      final Document open = documents.get( sourceUri );
      return open == null ? Optional.empty() : Optional.of( new GraphicalViewSourceSnapshot( open.uri(), open.content() ) );
   }

   private Optional<GraphicalViewSourceSnapshot> persistedSnapshot( final URI uri ) {
      try {
         return Optional.of( new GraphicalViewSourceSnapshot( uri, Files.readString( Path.of( uri ) ) ) );
      } catch ( final IOException | IllegalArgumentException exception ) {
         return Optional.empty();
      }
   }
}
