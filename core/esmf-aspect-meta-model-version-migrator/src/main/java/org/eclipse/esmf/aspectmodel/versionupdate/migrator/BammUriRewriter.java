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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.InvalidVersionException;
import org.eclipse.esmf.aspectmodel.vocabulary.Namespace;
import org.eclipse.esmf.samm.KnownVersion;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

/**
 * A {@link AbstractUriRewriter} that replaces all references to the legacy BAMM Aspect Meta Model to their corresponding SAMM counterparts
 */
public class BammUriRewriter extends AbstractUriRewriter {
   private final BammVersion bammVersion;

   private static final String SAMM_C_PREFIX = "samm-c";
   private static final String SAMM_E_PREFIX = "samm-e";

   public BammUriRewriter( final BammVersion bammVersion ) {
      // Translating versions will only fail if there are no SAMM versions (i.e., KnownVersion) for the versions in BAMM_VERSION
      super( KnownVersion.fromVersionString( bammVersion.versionString() ).orElseThrow( () ->
                  new InvalidVersionException( "BAMM version " + bammVersion.versionString() + " can not be translated to SAMM" ) ),
            KnownVersion.getLatest() );
      this.bammVersion = bammVersion;
   }

   @Override
   protected Map<String, String> buildPrefixMap( final Model sourceModel, final Map<String, String> targetPrefixes,
         final Map<String, String> oldToNewNamespaces ) {
      return sourceModel.getNsPrefixMap().keySet().stream()
            .map( prefix -> switch ( prefix ) {
               case "bamm" -> Map.entry( "samm", targetPrefixes.get( "samm" ) );
               case "bamm-c" -> Map.entry( SAMM_C_PREFIX, targetPrefixes.get( SAMM_C_PREFIX ) );
               case "bamm-e" -> Map.entry( SAMM_E_PREFIX, targetPrefixes.get( SAMM_E_PREFIX ) );
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
            "urn:bamm:io.openmanufacturing:meta-model:" + bammVersion.versionString() + "#", targetPrefixes.get( "samm" ),
            "urn:bamm:io.openmanufacturing:characteristic:" + bammVersion.versionString() + "#", targetPrefixes.get( SAMM_C_PREFIX ),
            "urn:bamm:io.openmanufacturing:entity:" + bammVersion.versionString() + "#", targetPrefixes.get( SAMM_E_PREFIX ),
            "urn:bamm:io.openmanufacturing:unit:" + bammVersion.versionString() + "#", targetPrefixes.get( "unit" )
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

   private boolean modelContainsBammPrefixes( final Model model ) {
      final String bammPrefix = "urn:bamm:io.openmanufacturing:meta-model:";
      final Predicate<String> isBammRelated = uri ->
            uri.startsWith( bammPrefix ) && uri.contains( bammVersion.versionString() );
      return // BAMM prefix is present
            model.getNsPrefixMap().values().stream().anyMatch( isBammRelated )
                  || // Or any referred resource uses a BAMM URN
                  Streams.stream( model.listObjectsOfProperty( RDF.type ) )
                        .flatMap( object -> object.isURIResource() ? Stream.of( object.asResource().getURI() ) : Stream.empty() )
                        .anyMatch( isBammRelated );
   }

   @Override
   public Model migrate( final Model sourceModel ) {
      if ( !modelContainsBammPrefixes( sourceModel ) ) {
         return sourceModel;
      }

      final Map<String, String> targetPrefixes = Namespace.createPrefixMap( getTargetKnownVersion() );
      final Map<String, String> oldToNewNamespaces = buildReplacementPrefixMap( sourceModel, targetPrefixes );

      final List<Statement> remappedStatements = new ArrayList<>();

      // it is important to do the remapping "in situ" (in the same model), because otherwise the position information would be lost.
      sourceModel.listStatements().forEach( statement ->
            remappedStatements.add( sourceModel.createStatement( updateResource( statement.getSubject(), oldToNewNamespaces ),
                  updateProperty( statement.getPredicate(), oldToNewNamespaces ),
                  updateRdfNode( statement.getObject(), oldToNewNamespaces ) ) )
      );

      sourceModel.removeAll();
      remappedStatements.forEach( sourceModel::add );
      sourceModel.setNsPrefixes( buildPrefixMap( sourceModel, targetPrefixes, oldToNewNamespaces ) );
      return sourceModel;
   }

   public enum BammVersion {
      BAMM_1_0_0,
      BAMM_2_0_0;

      public String versionString() {
         return toString().replaceFirst( "BAMM_(\\d+)_(\\d+)_(\\d+)", "$1.$2.$3" );
      }
   }
}
