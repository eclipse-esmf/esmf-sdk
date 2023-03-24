/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.versionupdate.migrator;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.eclipse.esmf.samm.KnownVersion;

/**
 * A {@link AbstractUriRewriter} that replaces all references to the legacy BAMM Aspect Meta Model to their corresponding SAMM counterparts
 */
public class BammUriRewriter extends AbstractUriRewriter {
   public BammUriRewriter() {
      super( KnownVersion.getLatest(), KnownVersion.getLatest() );
   }

   @Override
   protected Map<String, String> buildPrefixMap( final Model sourceModel, final Map<String, String> targetPrefixes,
         final Map<String, String> oldToNewNamespaces ) {
      return sourceModel.getNsPrefixMap().keySet().stream()
            .map( prefix -> switch ( prefix ) {
               case "bamm" -> Map.entry( "samm", targetPrefixes.get( "samm" ) );
               case "bamm-c" -> Map.entry( "samm-c", targetPrefixes.get( "samm-c" ) );
               case "bamm-e" -> Map.entry( "samm-e", targetPrefixes.get( "samm-e" ) );
               case "unit" -> Map.entry( "unit", targetPrefixes.get( "unit" ) );
               default -> Map.entry( prefix, rewriteUri( sourceModel.getNsPrefixURI( prefix ), oldToNewNamespaces )
                     .orElse( sourceModel.getNsPrefixURI( prefix ) ) );
            } )
            .collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue ) );
   }

   @Override
   protected Map<String, String> buildReplacementPrefixMap( final Model sourceModel, final Map<String, String> targetPrefixes ) {
      // The mapping of the URNs of the legacy BAMM Aspect Meta model to their corresponding SAMM counterparts
      return Map.of(
            "urn:bamm:io.openmanufacturing:meta-model:2.0.0#", targetPrefixes.get( "samm" ),
            "urn:bamm:io.openmanufacturing:characteristic:2.0.0#", targetPrefixes.get( "samm-c" ),
            "urn:bamm:io.openmanufacturing:entity:2.0.0#", targetPrefixes.get( "samm-c" ),
            "urn:bamm:io.openmanufacturing:unit:2.0.0#", targetPrefixes.get( "unit" )
      );
   }

   @Override
   protected Optional<String> rewriteUri( final String oldUri, final Map<String, String> oldToNewNamespaces ) {
      if ( !oldUri.startsWith( "urn:bamm:" ) ) {
         return Optional.empty();
      }
      String result = oldUri;
      for ( final Map.Entry<String, String> mapEntry : oldToNewNamespaces.entrySet() ) {
         result = result.replace( mapEntry.getKey(), mapEntry.getValue() );
      }
      // This catches the regular (i.e., non meta-model) URNs
      result = result.replace( "urn:bamm:", "urn:samm:" );
      return Optional.of( result );
   }
}
