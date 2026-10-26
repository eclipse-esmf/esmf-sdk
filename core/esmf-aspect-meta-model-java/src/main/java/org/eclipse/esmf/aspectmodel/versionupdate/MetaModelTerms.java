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

import java.net.URL;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.urn.ElementType;
import org.eclipse.esmf.samm.KnownVersion;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

/**
 * The SAMM meta model terms (classes, properties and predefined instances) that each
 * {@link KnownVersion} defines.
 *
 * <p>
 * The SAMM meta model resources of all known versions are shipped side by side on the class path,
 * but the rest of the SDK only ever reads those of {@link KnownVersion#getLatest()}. This class
 * reads
 * the definitions of the older versions as well, so that an Aspect Model file can be checked
 * against
 * the meta model version it actually declares.
 *
 * <p>
 * The shipped definitions are the single source of truth. They are read rather than duplicated
 * here, so adding a meta model version needs no change to this class. Each version is read once, on
 * first use, and only when a file declaring that version is loaded.
 *
 * <p>
 * Unit definitions are deliberately not part of the inventory: the {@code unit:} namespace changed
 * its structure between meta model versions (see {@link UnitInSammNamespaceMigrator}) and the units
 * catalogue is large, so reading it would be both expensive and error prone.
 */
final class MetaModelTerms {
   private static final String SAMM_NAMESPACE_MAIN_PART = "org.eclipse.esmf.samm";
   private static final Map<KnownVersion, Set<AspectModelUrn>> DEFINED_TERMS = new EnumMap<>( KnownVersion.class );

   private MetaModelTerms() {}

   /**
    * Returns all meta model terms the given version defines. The terms are held in a set because the
    * check looks each term up by identity and does so once per distinct URI of a loaded file.
    *
    * @param metaModelVersion the meta model version
    * @return the terms
    */
   static synchronized Set<AspectModelUrn> definedTerms( final KnownVersion metaModelVersion ) {
      return DEFINED_TERMS.computeIfAbsent( metaModelVersion, MetaModelTerms::readDefinedTerms );
   }

   /**
    * Determines whether the given meta model version defines the given term.
    *
    * @param urn the URN of a meta model term
    * @param metaModelVersion the meta model version
    * @return true if the version defines the term
    */
   static boolean isDefinedIn( final AspectModelUrn urn, final KnownVersion metaModelVersion ) {
      return definedTerms( metaModelVersion ).contains( urn );
   }

   /**
    * Returns the oldest known meta model version that defines the given term. The term is identified
    * by
    * its element type and name, because the URN itself contains the version and therefore differs
    * between versions.
    *
    * @param urn the URN of a meta model term, in any version
    * @return the version that introduced the term, or empty if no known version defines it
    */
   static Optional<KnownVersion> firstVersionDefining( final AspectModelUrn urn ) {
      return KnownVersion.getVersions().stream()
            .filter( version -> definedTerms( version ).stream().anyMatch( knownTerm -> isSameTerm( knownTerm, urn ) ) )
            .findFirst();
   }

   /**
    * Determines whether the given URN addresses a term in one of the SAMM meta model namespaces, as
    * opposed to an element in a user namespace.
    *
    * @param urn the URN to check
    * @return true if the URN addresses a meta model term
    */
   static boolean isMetaModelTerm( final AspectModelUrn urn ) {
      return SAMM_NAMESPACE_MAIN_PART.equals( urn.getNamespaceMainPart() )
            && ( urn.getElementType() == ElementType.META_MODEL
                  || urn.getElementType() == ElementType.CHARACTERISTIC
                  || urn.getElementType() == ElementType.ENTITY
                  || urn.getElementType() == ElementType.UNIT );
   }

   private static Set<AspectModelUrn> readDefinedTerms( final KnownVersion metaModelVersion ) {
      final Set<AspectModelUrn> terms = new LinkedHashSet<>();
      for ( final MetaModelFile definitionFile : definitionFiles() ) {
         // Not every definition file exists in every version: samm-e:Quantity for example was only
         // introduced in SAMM 2.2.0, so a missing resource is not an error
         MetaModelFile.findMetaModelResource( definitionFile.getSection(), metaModelVersion,
               definitionFile.filename().orElseThrow() )
               .flatMap( MetaModelTerms::loadModel )
               .ifPresent( model -> collectTerms( model, terms ) );
      }
      return Set.copyOf( terms );
   }

   /**
    * The meta model files that contain term definitions. Shape definitions are not considered, because
    * they constrain terms rather than define them, and the units catalogue is excluded on purpose.
    */
   private static List<MetaModelFile> definitionFiles() {
      return Stream.concat(
            MetaModelFile.getMetaModelDefinitionsFiles().stream(),
            MetaModelFile.getElementDefinitionsFiles().stream() )
            .filter( file -> file != MetaModelFile.UNITS )
            .toList();
   }

   private static Optional<Model> loadModel( final URL url ) {
      return TurtleLoader.loadTurtle( url ).toJavaOptional();
   }

   private static void collectTerms( final Model model, final Set<AspectModelUrn> terms ) {
      for ( final Resource subject : model.listSubjects().toList() ) {
         if ( subject.isAnon() || subject.getURI() == null ) {
            continue;
         }
         AspectModelUrn.from( subject.getURI() ).toJavaOptional()
               .filter( MetaModelTerms::isMetaModelTerm )
               .ifPresent( terms::add );
      }
   }

   private static boolean isSameTerm( final AspectModelUrn one, final AspectModelUrn other ) {
      return one.getElementType() == other.getElementType() && one.getName().equals( other.getName() );
   }
}
