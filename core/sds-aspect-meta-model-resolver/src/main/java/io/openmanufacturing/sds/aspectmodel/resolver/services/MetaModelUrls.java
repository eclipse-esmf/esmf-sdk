/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.resolver.services;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

/**
 * Provides the facility for the resolution of meta model resources
 */
public class MetaModelUrls {
   private static final Logger LOG = LoggerFactory.getLogger( MetaModelUrls.class );

   private MetaModelUrls() {
   }

   /**
    * Create a URL referring to a meta model resource
    *
    * @param part The meta model section, e.g. aspect-meta-model or characteristics
    * @param version The BAMM version
    * @param fileName The respective file name
    * @return The resource URL
    */
   public static Optional<URL> url( final String part, final KnownVersion version, final String fileName ) {
      final String spec = String.format( "bamm/%s/%s/%s", part, version.toVersionString(), fileName );
      try {
         final List<URL> urls = ImmutableList.copyOf( MetaModelUrls.class.getClassLoader().getResources( spec ).asIterator() );
         if ( urls.size() == 1 ) {
            return Optional.of( urls.get( 0 ) );
         }
         if ( urls.isEmpty() ) {
            return nothingFound( spec );
         }

         // If multiple resources with the given spec are found:
         // - return the one from the file system, if it exists
         // - otherwise, the one from jar
         // - otherwise, any of the found resources
         URL jarUrl = null;
         for ( final URL url : urls ) {
            if ( url.getProtocol().equals( "file" ) ) {
               return Optional.of( url );
            }
            if ( url.getProtocol().equals( "jar" ) ) {
               jarUrl = url;
            }
         }
         return Optional.of( jarUrl == null ? urls.get( 0 ) : jarUrl );
      } catch ( final IOException e ) {
         return nothingFound( spec );
      }
   }

   private static Optional<URL> nothingFound( final String spec ) {
      LOG.warn( "Could not resolve meta model resource {}", spec );
      return Optional.empty();
   }
}
