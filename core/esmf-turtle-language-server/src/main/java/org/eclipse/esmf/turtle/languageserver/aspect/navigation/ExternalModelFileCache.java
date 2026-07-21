/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.turtle.languageserver.aspect.navigation;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;

import net.harawata.appdirs.AppDirsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExternalModelFileCache {
   private static final Logger LOG = LoggerFactory.getLogger( ExternalModelFileCache.class );

   private static final Path CACHE_DIR = Path.of( AppDirsFactory.getInstance().getUserCacheDir( "esmf", "1", "esmf" ) );
   private static final Map<String, Path> CACHED_FILES = new ConcurrentHashMap<>();

   private ExternalModelFileCache() {}

   public static Path materialize( final String uniqueName, final String content ) {
      return CACHED_FILES.computeIfAbsent( uniqueName, key -> {
         ensureCacheDirExists();
         final Path target = CACHE_DIR.resolve( key );
         try {
            Files.writeString( target, content );
            LOG.debug( "[model-file-cache] materialized {} to {}", key, target );
         } catch ( final IOException exception ) {
            throw new ModelResolutionException( "Could not materialize model file " + key, exception );
         }
         return target;
      } );
   }

   public static boolean isCachedModelUri( final String uri ) {
      try {
         final Path parent = Path.of( new URI( uri ) ).getParent();
         return parent != null && parent.toAbsolutePath().equals( CACHE_DIR.toAbsolutePath() );
      } catch ( final URISyntaxException | IllegalArgumentException _ ) {
         return false;
      }
   }

   private static void ensureCacheDirExists() {
      if ( !Files.exists( CACHE_DIR ) ) {
         try {
            Files.createDirectories( CACHE_DIR );
         } catch ( final IOException exception ) {
            throw new ModelResolutionException( "Unable to create model cache directory at " + CACHE_DIR, exception );
         }
      }
   }
}
