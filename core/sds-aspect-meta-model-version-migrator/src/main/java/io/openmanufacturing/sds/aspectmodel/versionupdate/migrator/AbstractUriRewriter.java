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

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import com.google.common.collect.Streams;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.vocabulary.Namespace;

/**
 * Abstract migration function that is used to apply a change to all URIs in a model
 */
public abstract class AbstractUriRewriter extends AbstractSdsMigrator {
   protected AbstractUriRewriter( final KnownVersion sourceVersion, final KnownVersion targetVersion ) {
      super( sourceVersion, targetVersion, 100 );
   }

   protected abstract Optional<String> rewriteUri( String oldUri, Map<String, String> oldToNewNamespaces );

   protected Resource updateResource( final Resource resource, final Map<String, String> oldToNewNamespaces ) {
      if ( resource.isAnon() ) {
         return resource;
      }

      return rewriteUri( resource.getURI(), oldToNewNamespaces ).map( ResourceFactory::createResource ).orElse( resource );
   }

   protected Property updateProperty( final Property property, final Map<String, String> oldToNewNamespaces ) {
      return rewriteUri( property.getURI(), oldToNewNamespaces ).map( ResourceFactory::createProperty ).orElse( property );
   }

   protected RDFNode updateRdfNode( final RDFNode rdfNode, final Map<String, String> oldToNewNamespaces ) {
      if ( rdfNode.isResource() ) {
         return updateResource( rdfNode.asResource(), oldToNewNamespaces );
      }
      return rdfNode;
   }

   @Override
   public Model migrate( final Model sourceModel ) {
      final Model targetModel = ModelFactory.createDefaultModel();

      final Map<String, String> targetPrefixes = Namespace.createPrefixMap( getTargetKnownVersion() );

      final Map<String, String> sourcePrefixes = Namespace.createPrefixMap( getSourceKnownVersion() );
      final Map<String, String> oldToNewNamespaces = new HashMap<>();
      for ( final Map.Entry<String, String> targetEntry : targetPrefixes.entrySet() ) {
         final String prefix = targetEntry.getKey();
         if ( prefix != null ) {
            final String sourceUri = sourcePrefixes.get( prefix );
            if ( sourceUri != null && !sourceUri.equals( targetEntry.getValue() )) {
               oldToNewNamespaces.put( sourceUri, targetEntry.getValue() );
            }
         }
      }

      Streams.stream( sourceModel.listStatements() ).map( statement -> targetModel
            .createStatement(
                  updateResource( statement.getSubject(), oldToNewNamespaces ),
                  updateProperty( statement.getPredicate(), oldToNewNamespaces ),
                  updateRdfNode( statement.getObject(), oldToNewNamespaces )
            ) ).forEach( targetModel::add );

      final Map<String, String> newPrefixMap =
            sourceModel.getNsPrefixMap().keySet().stream()
                  .map( prefix -> new AbstractMap.SimpleEntry<>( prefix,
                        Optional.ofNullable( targetPrefixes.get( prefix ) ).orElse( sourceModel.getNsPrefixURI( prefix ) ) ) )
                  .collect( Collectors.toMap( AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue ) );
      targetModel.setNsPrefixes( newPrefixMap );

      return targetModel;
   }
}
