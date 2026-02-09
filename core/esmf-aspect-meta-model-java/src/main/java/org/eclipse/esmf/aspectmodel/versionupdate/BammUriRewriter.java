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

import static org.eclipse.esmf.aspectmodel.StreamUtil.asMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.InvalidVersionException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.samm.KnownVersion;

import com.google.common.collect.Streams;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

/**
 * A {@link AbstractUriRewriter} that replaces all references to the legacy BAMM Aspect Meta Model to their corresponding SAMM counterparts
 */
public class BammUriRewriter extends AbstractUriRewriter {
   private final BammVersion bammVersion;

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
               case "bamm" -> Map.entry( SammNs.SAMM.getShortForm(), targetPrefixes.get( SammNs.SAMM.getShortForm() ) );
               case "bamm-c" -> Map.entry( SammNs.SAMMC.getShortForm(), targetPrefixes.get( SammNs.SAMMC.getShortForm() ) );
               case "bamm-e" -> Map.entry( SammNs.SAMME.getShortForm(), targetPrefixes.get( SammNs.SAMME.getShortForm() ) );
               case "unit" -> Map.entry( SammNs.UNIT.getShortForm(), targetPrefixes.get( SammNs.UNIT.getShortForm() ) );
               default -> {
                  final String uri = sourceModel.getNsPrefixURI( prefix );
                  final Resource resource = sourceModel.createResource( uri );

                  yield Map.entry(
                        prefix,
                        rewriteUri( resource, oldToNewNamespaces ).orElse( uri )
                  );
               }
            } )
            .collect( asMap() );
   }

   @Override
   protected Map<String, String> buildReplacementPrefixMap( final Model sourceModel, final Map<String, String> targetPrefixes ) {
      // The mapping of the URNs of the legacy BAMM Aspect Meta model to their corresponding SAMM counterparts
      return Map.of(
            "urn:bamm:io.openmanufacturing:meta-model:" + bammVersion.versionString() + "#",
            targetPrefixes.get( SammNs.SAMM.getShortForm() ),
            "urn:bamm:io.openmanufacturing:characteristic:" + bammVersion.versionString() + "#",
            targetPrefixes.get( SammNs.SAMMC.getShortForm() ),
            "urn:bamm:io.openmanufacturing:entity:" + bammVersion.versionString() + "#", targetPrefixes.get( SammNs.SAMME.getShortForm() ),
            "urn:bamm:io.openmanufacturing:unit:" + bammVersion.versionString() + "#", targetPrefixes.get( SammNs.UNIT.getShortForm() )
      );
   }

   @Override
   protected Optional<String> rewriteUri( final Resource resource, final Map<String, String> oldToNewNamespaces ) {
      final String oldUri = resource.getURI();

      if ( oldUri == null || !oldUri.startsWith( "urn:bamm:" ) ) {
         return Optional.empty();
      }

      String result = oldUri;
      for ( final Map.Entry<String, String> mapEntry : oldToNewNamespaces.entrySet() ) {
         result = result.replace( mapEntry.getKey(), mapEntry.getValue() );
      }

      // This catches the regular (i.e., non meta-model) URNs
      result = result.replace( "urn:bamm:", AspectModelUrn.PROTOCOL_AND_NAMESPACE_PREFIX );
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

      final Map<String, String> targetPrefixes = RdfNamespace.createPrefixMap( getTargetKnownVersion() );
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
      sourceModel.clearNsPrefixMap();
      sourceModel.setNsPrefixes( buildPrefixMap( sourceModel, targetPrefixes, oldToNewNamespaces ) );
      return sourceModel;
   }

   public enum BammVersion {
      BAMM_1_0_0( "1.0.0" ),
      BAMM_2_0_0( "2.0.0" );

      private final String versionString;

      BammVersion( final String versionString ) {
         this.versionString = versionString;
      }

      public String versionString() {
         return versionString;
      }
   }
}
