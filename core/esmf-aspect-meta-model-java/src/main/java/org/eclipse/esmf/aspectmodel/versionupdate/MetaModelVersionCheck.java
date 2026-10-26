/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import org.eclipse.esmf.aspectmodel.MetaModelVersionException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.urn.ElementType;
import org.eclipse.esmf.samm.KnownVersion;

/**
 * Checks an Aspect Model file against the SAMM meta model version it declares.
 *
 * <p>
 * The check must run on the parsed but not yet migrated RDF graph: migration rewrites meta model
 * URIs to the latest version without verifying that the rewritten term existed in the source
 * version,
 * so afterwards a file that declared an older version is indistinguishable from a correct one.
 *
 * <p>
 * Two rules are enforced:
 * <ol>
 * <li>Every meta model term the file uses must be defined in the version the file declares.</li>
 * <li>Every meta model URI must carry the declared version, so that a single file does not mix
 * namespaces of different meta model versions.</li>
 * </ol>
 */
final class MetaModelVersionCheck {
   private MetaModelVersionCheck() {}

   /**
    * Checks the given model against the meta model version it declares.
    *
    * @param model the parsed, not yet migrated RDF model of a single Aspect Model file
    * @param declaredVersion the meta model version the file declares
    * @param sourceLocation the human readable location of the file, used in the messages
    * @return the messages describing the inconsistencies, empty if the file is consistent
    */
   static List<MetaModelVersionException.Problem> check( final Model model, final KnownVersion declaredVersion,
         final URI sourceLocation ) {
      // Keyed by term URI so that a term used many times is only reported once
      final Map<String, MetaModelVersionException.Problem> messages = new LinkedHashMap<>();
      for ( final CandidateUri uri : candidateUris( model ) ) {
         AspectModelUrn.from( uri.uri() ).toJavaOptional()
               .filter( MetaModelTerms::isMetaModelTerm )
               .flatMap( urn -> checkTerm( urn, uri.node(), declaredVersion, sourceLocation )
                     .map( problem -> Map.entry( urn.getUrn().toString(), problem ) ) )
               .ifPresent( entry -> messages.putIfAbsent( entry.getKey(), entry.getValue() ) );
      }
      return List.copyOf( messages.values() );
   }

   private static Optional<MetaModelVersionException.Problem> checkTerm( final AspectModelUrn urn, final RDFNode node,
         final KnownVersion declaredVersion, final URI sourceLocation ) {
      if ( !declaredVersion.toVersionString().equals( urn.getVersion() ) ) {
         // Name where the version comes from: it is taken from the samm: prefix, so without this the
         // message reads as if the reported term were the one at fault rather than the mismatching prefix
         final String message = String.format(
               "The samm: prefix declares SAMM %s, but %s is from SAMM %s. All SAMM namespaces in a file must use the same "
                     + "meta model version.",
               declaredVersion.toVersionString(), urn.getUrn(), urn.getVersion() );
         return Optional.of( new MetaModelVersionException.Problem( message, sourceLocation, node ) );
      }

      // Unit definitions are not part of the term inventory, see MetaModelTerms
      if ( urn.getElementType() == ElementType.UNIT || MetaModelTerms.isDefinedIn( urn, declaredVersion ) ) {
         return Optional.empty();
      }

      // Only a term that a *newer* meta model version defines indicates that the file declares too low a
      // version. A term that no version knows is either a typo, which SHACL reports with a more specific
      // message, or an element a model legitimately adds to one of the SAMM namespaces. A term that only
      // an *older* version defines was removed rather than added, which the migrators handle: samm:name,
      // for example, exists in SAMM 1.0.0 and is dropped by SammRemoveSammNameMigrator.
      return MetaModelTerms.firstVersionDefining( urn )
            .filter( introducedIn -> introducedIn.isNewerThan( declaredVersion ) )
            .map( introducedIn -> {
               final String message = String.format(
                     "%s is not defined in SAMM %s (introduced in %s). Change the meta model version declared in this "
                           + "file to at least %s.",
                     urn.getUrn(), declaredVersion.toVersionString(), introducedIn.toVersionString(),
                     introducedIn.toVersionString() );
               return new MetaModelVersionException.Problem( message, sourceLocation, node );
            } );
   }

   /**
    * Represents a candidate URI with the RDF node this URI is related to. In most cases, this is the
    * URI of the node itself (if it's a resource), but for literals it is the datatype URI of the
    * literal.
    *
    * @param uri the URI
    * @param node the corresponding node
    */
   private record CandidateUri(
         String uri,
         RDFNode node
   ) {}

   /**
    * Collects the distinct URIs of the model that may address a meta model term. Datatypes of literals
    * are included because they are versioned as well and are rewritten during migration, see
    * {@link AbstractUriRewriter#updateLiteral}.
    *
    * <p>
    * Subjects and predicates repeat across the statements of a model, so the URIs are collected into a
    * set: this check runs on every load, and each URI that survives here is parsed into an
    * {@link AspectModelUrn}, which is comparatively expensive.
    */
   private static Set<CandidateUri> candidateUris( final Model model ) {
      final Set<CandidateUri> uris = new LinkedHashSet<>();
      for ( final Statement statement : model.listStatements().toList() ) {
         addIfUriResource( statement.getSubject(), uris );
         addIfUriResource( statement.getPredicate(), uris );
         final RDFNode object = statement.getObject();
         if ( object.isLiteral() ) {
            addIfInSammUrnSpace( object.asLiteral().getDatatypeURI(), object, uris );
         } else {
            addIfUriResource( object, uris );
         }
      }
      return uris;
   }

   private static void addIfUriResource( final RDFNode node, final Set<CandidateUri> uris ) {
      if ( node.isURIResource() ) {
         addIfInSammUrnSpace( node.asResource().getURI(), node, uris );
      }
   }

   /**
    * Only a URI in the SAMM URN space can address a meta model term. Filtering on the prefix first
    * keeps most of a model's URIs out of the URN parsing below, in particular all XSD datatypes and
    * all RDF and SHACL terms.
    */
   private static void addIfInSammUrnSpace( final String uri, final RDFNode node, final Set<CandidateUri> uris ) {
      if ( uri != null && uri.startsWith( AspectModelUrn.PROTOCOL_AND_NAMESPACE_PREFIX ) ) {
         uris.add( new CandidateUri( uri, node ) );
      }
   }
}
