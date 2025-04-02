/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.versionupdate;

import java.util.Map;
import java.util.Optional;

import org.eclipse.esmf.samm.KnownVersion;

public class SammMetaModelVersionUriRewriter extends AbstractUriRewriter {
   public SammMetaModelVersionUriRewriter( final KnownVersion sourceVersion, final KnownVersion targetVersion ) {
      super( sourceVersion, targetVersion );
   }

   @Override
   public int order() {
      return 50;
   }

   @Override
   protected Optional<String> rewriteUri( final String oldUri, final Map<String, String> oldToNewNamespaces ) {
      final int indexOfHash = oldUri.indexOf( '#' );
      if ( indexOfHash == -1 ) {
         return Optional.empty();
      }
      final String newNamespace = oldToNewNamespaces.get( oldUri.substring( 0, indexOfHash + 1 ) );
      if ( newNamespace == null ) {
         return Optional.empty();
      }
      return Optional.of( newNamespace + oldUri.substring( indexOfHash + 1 ) );
   }

   @Override
   public Optional<String> getDescription() {
      return Optional.of( String
            .format( "Change meta model version from %s to %s in URIs", getSourceKnownVersion().toVersionString(),
                  getTargetKnownVersion().toVersionString() ) );
   }
}
