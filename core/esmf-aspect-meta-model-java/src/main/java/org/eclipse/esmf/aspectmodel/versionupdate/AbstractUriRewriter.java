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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.samm.KnownVersion;

import com.google.common.collect.Streams;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

/**
 * Abstract migration function that is used to apply a change to all URIs in a model
 */
public abstract class AbstractUriRewriter extends AbstractSammMigrator {
   protected AbstractUriRewriter( final KnownVersion sourceVersion, final KnownVersion targetVersion ) {
      super( sourceVersion, targetVersion, 100 );
   }

   /**
    * The URI rewriter implementation decides whether a URI needs to be rewritten, given the map of old to new namespaces
    *
    * @param oldUri the URI to rewrite
    * @param oldToNewNamespaces the map of old to new namespaces
    * @return empty if the URI should be kept as-is, the replacement URI otherwise
    */
   protected abstract Optional<String> rewriteUri( String oldUri, Map<String, String> oldToNewNamespaces );

   protected Resource updateResource( final Resource resource, final Map<String, String> oldToNewNamespaces ) {
      if ( resource.isAnon() ) {
         return resource;
      }

      return rewriteUri( resource.getURI(), oldToNewNamespaces ).map( uriref ->
            resource.getModel().createResource( uriref ) ).orElse( resource );
   }

   protected Property updateProperty( final Property property, final Map<String, String> oldToNewNamespaces ) {
      return rewriteUri( property.getURI(), oldToNewNamespaces ).map( uriref ->
            property.getModel().createProperty( uriref ) ).orElse( property );
   }

   protected Literal updateLiteral( final Literal literal, final Map<String, String> oldToNewNamespaces ) {
      return Optional.ofNullable( literal.getDatatypeURI() )
            .flatMap( uri -> rewriteUri( uri, oldToNewNamespaces ) )
            .map( uri -> literal.getModel().createTypedLiteral( literal.getLexicalForm(), NodeFactory.getType( uri ) ) )
            .orElse( literal );
   }

   protected RDFNode updateRdfNode( final RDFNode rdfNode, final Map<String, String> oldToNewNamespaces ) {
      if ( rdfNode.isResource() ) {
         return updateResource( rdfNode.asResource(), oldToNewNamespaces );
      } else if ( rdfNode.isLiteral() ) { // needed especially for "curie" literals, which are versioned: "unit:day"^^samm:curie
         return updateLiteral( rdfNode.asLiteral(), oldToNewNamespaces );
      }
      return rdfNode;
   }

   protected Map<String, String> buildReplacementPrefixMap( final Model sourceModel, final Map<String, String> targetPrefixes ) {
      final Map<String, String> sourcePrefixes = RdfNamespace.createPrefixMap( getSourceKnownVersion() );
      final Map<String, String> oldToNewNamespaces = new HashMap<>();
      for ( final Map.Entry<String, String> targetEntry : targetPrefixes.entrySet() ) {
         final String prefix = targetEntry.getKey();
         if ( prefix != null ) {
            final String sourceUri = sourcePrefixes.get( prefix );
            if ( sourceUri != null && !sourceUri.equals( targetEntry.getValue() ) ) {
               oldToNewNamespaces.put( sourceUri, targetEntry.getValue() );
            }
         }
      }

      return oldToNewNamespaces;
   }

   /**
    * Builds the map of RDF prefixes to set in the migrated model, e.g. "xsd" -> XSD.NS
    *
    * @param sourceModel the source model
    * @param targetPrefixes the target prefix map, containing e.g. "samm" -> samm.getNamespace()
    * @param oldToNewNamespaces the map of old RDF namespaces to their new counterparts
    * @return the prefix map
    */
   protected Map<String, String> buildPrefixMap( final Model sourceModel, final Map<String, String> targetPrefixes,
         final Map<String, String> oldToNewNamespaces ) {
      return sourceModel.getNsPrefixMap().keySet().stream().map( prefix ->
                  Map.<String, String> entry( prefix, targetPrefixes.getOrDefault( prefix, sourceModel.getNsPrefixURI( prefix ) ) ) )
            .collect( asMap() );
   }

   @Override
   public Model migrate( final Model sourceModel ) {
      final Model targetModel = ModelFactory.createDefaultModel();

      final Map<String, String> targetPrefixes = RdfNamespace.createPrefixMap( getTargetKnownVersion() );
      final Map<String, String> oldToNewNamespaces = buildReplacementPrefixMap( sourceModel, targetPrefixes );

      Streams.stream( sourceModel.listStatements() ).map( statement -> targetModel
            .createStatement(
                  updateResource( statement.getSubject(), oldToNewNamespaces ),
                  updateProperty( statement.getPredicate(), oldToNewNamespaces ),
                  updateRdfNode( statement.getObject(), oldToNewNamespaces )
            ) ).forEach( targetModel::add );

      targetModel.setNsPrefixes( buildPrefixMap( sourceModel, targetPrefixes, oldToNewNamespaces ) );
      return targetModel;
   }
}
