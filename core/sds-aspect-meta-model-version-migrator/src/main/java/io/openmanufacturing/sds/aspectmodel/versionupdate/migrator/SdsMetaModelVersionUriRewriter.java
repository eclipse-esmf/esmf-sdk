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

package io.openmanufacturing.sds.aspectmodel.versionupdate.migrator;

import java.util.Map;
import java.util.Optional;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

public class SdsMetaModelVersionUriRewriter extends AbstractUriRewriter {

   public SdsMetaModelVersionUriRewriter( final KnownVersion sourceVersion, final KnownVersion targetVersion ) {
      super( sourceVersion, targetVersion );
   }

   @Override
   public int order() {
      return 50;
   }

   @Override
   protected Optional<String> rewriteUri( final String oldUri, final Map<String, String> oldToNewNamespaces ) {
      final String[] uriParts = oldUri.split( "#" );
      if ( uriParts.length == 1 ) {
         return Optional.empty();
      }
      return oldToNewNamespaces.keySet()
            .stream()
            .filter( key -> key.equals( uriParts[0] + "#" ) )
            .findAny()
            .map( key -> oldToNewNamespaces.get( key ) + uriParts[1] );
   }

   @Override
   public Optional<String> getDescription() {
      return Optional.of( String
            .format( "Change meta model version from %s to %s in URIs", getSourceKnownVersion().toVersionString(),
                  getTargetKnownVersion().toVersionString() ) );
   }
}
