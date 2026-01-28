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

import org.apache.jena.rdf.model.Resource;

public class SammMetaModelVersionUriRewriter extends AbstractUriRewriter {

   private static final int ORDER = 50;

   public SammMetaModelVersionUriRewriter( final KnownVersion sourceVersion, final KnownVersion targetVersion ) {
      super( sourceVersion, targetVersion );
   }

   @Override
   public int order() {
      return ORDER;
   }

   protected Optional<String> rewriteUri( final Resource resource, final Map<String, String> oldToNewNamespaces ) {

      final String oldNamespace = resource.getNameSpace();

      return Optional.ofNullable( oldToNewNamespaces.get( oldNamespace ) )
            .map( newNamespace -> newNamespace + resource.getLocalName() );
   }

   @Override
   public Optional<String> getDescription() {
      return Optional.of( String
            .format( "Change meta model version from %s to %s in URIs", getSourceKnownVersion().toVersionString(),
                  getTargetKnownVersion().toVersionString() ) );
   }
}
