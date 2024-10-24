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

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.InvalidVersionException;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.urn.ElementType;
import org.eclipse.esmf.samm.KnownVersion;

import com.google.common.collect.ImmutableList;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The service that migrates all migrators in the correct order.
 */
public class MetaModelVersionMigrator implements UnaryOperator<AspectModelFile> {
   public static final MetaModelVersionMigrator INSTANCE = new MetaModelVersionMigrator();
   private static final Logger LOG = LoggerFactory.getLogger( MetaModelVersionMigrator.class );

   private MetaModelVersionMigrator() {
   }

   private final List<Migrator> migrators = ImmutableList.<Migrator> builder()
         .add( new SammMetaModelVersionUriRewriter( KnownVersion.SAMM_2_0_0, KnownVersion.SAMM_2_1_0 ) )
         .add( new SammMetaModelVersionUriRewriter( KnownVersion.SAMM_1_0_0, KnownVersion.SAMM_2_0_0 ) )
         .add( new SammRemoveSammNameMigrator( KnownVersion.SAMM_1_0_0, KnownVersion.SAMM_2_0_0 ) )
         .add( new UnitInSammNamespaceMigrator() )
         .build();

   private Model execute( final Migrator migrator, final Model sourceModel ) {
      LOG.info( "Start Migration for {} to {}", migrator.sourceVersion(), migrator.targetVersion() );
      final String description = migrator.getDescription().orElse( "" );
      LOG.info( "Migration step {} {}", migrator.getClass().getSimpleName(), description );
      final Model targetModel = migrator.migrate( sourceModel );
      LOG.info( "End Migration" );
      return targetModel;
   }

   private Model convertBammToSamm( final Model model ) {
      final BammUriRewriter bamm100UriRewriter = new BammUriRewriter( BammUriRewriter.BammVersion.BAMM_1_0_0 );
      final BammUriRewriter bamm200UriRewriter = new BammUriRewriter( BammUriRewriter.BammVersion.BAMM_2_0_0 );
      return bamm200UriRewriter.migrate( bamm100UriRewriter.migrate( model ) );
   }

   /**
    * Returns the meta model version used in the model
    *
    * @param model an Aspect model file
    * @return the meta model versions
    */
   private VersionNumber getUsedMetaModelVersion( final Model model ) {
      final String sammUrnStart = String.format( "%s:%s", AspectModelUrn.VALID_PROTOCOL, AspectModelUrn.VALID_NAMESPACE_IDENTIFIER );
      final Set<VersionNumber> result = model.listObjects()
            .toList()
            .stream()
            .filter( RDFNode::isURIResource )
            .map( RDFNode::asResource )
            .map( Resource::getURI )
            .filter( uri -> uri.startsWith( sammUrnStart ) )
            .flatMap( uri -> AspectModelUrn.from( uri ).toJavaStream() )
            .filter( urn -> (urn.getElementType().equals( ElementType.META_MODEL ) || urn.getElementType()
                  .equals( ElementType.CHARACTERISTIC )) )
            .map( AspectModelUrn::getVersion )
            .map( VersionNumber::parse )
            .collect( Collectors.toSet() );
      if ( result.size() == 1 ) {
         return result.iterator().next();
      } else if ( result.size() > 1 ) {
         throw new ModelResolutionException( "Aspect Model refers more than one SAMM version" );
      } else {
         // Model does not contain any elements. Default to the latest meta model version.
         return VersionNumber.parse( KnownVersion.getLatest().toVersionString() );
      }
   }

   /**
    * Semantically migrates an Aspect model file from its current meta model version to a given target meta model version.
    * This is done by composing the {@link Migrator}s that update from one version to the next into one function
    * which is then applied to the given source model.
    *
    * @param modelFile the source model file
    * @return the resulting {@link AspectModelFile} that corresponds to the input Aspect model file, but with the new meta model version
    */
   //   public AspectModelFile updateMetaModelVersion( final AspectModelFile modelFile ) {
   @Override
   public AspectModelFile apply( final AspectModelFile modelFile ) {
      // Before any semantic migration, perform the mechanical translation of legacy BAMM models
      final Model input = convertBammToSamm( modelFile.sourceModel() );

      final VersionNumber latestKnownVersion = VersionNumber.parse( KnownVersion.getLatest().toVersionString() );
      final VersionNumber sourceVersion = getUsedMetaModelVersion( input );
      Model migrationModel = modelFile.sourceModel();

      if ( sourceVersion.greaterThan( latestKnownVersion ) ) {
         // looks like unreachable
         throw new InvalidVersionException(
               String.format( "Model version %s can not be updated to version %s", sourceVersion, latestKnownVersion ) );
      }

      if ( !sourceVersion.equals( latestKnownVersion ) ) {
         migrationModel = migrate( migrators, sourceVersion, latestKnownVersion, migrationModel );
      }

      return new RawAspectModelFile( migrationModel, modelFile.headerComment(), modelFile.sourceLocation() );
   }

   private Model migrate( final List<Migrator> migrators, final VersionNumber sourceVersion, final VersionNumber targetVersion,
         final Model targetModel ) {
      if ( migrators.isEmpty() ) {
         return targetModel;
      }

      final Comparator<Migrator> comparator = Comparator.comparing( Migrator::sourceVersion );
      final List<Migrator> migratorSet = migrators.stream()
            .sorted( comparator.thenComparing( Migrator::order ) )
            .dropWhile( migrator -> !migrator.sourceVersion().equals( sourceVersion ) )
            .takeWhile( migrator -> !migrator.targetVersion().greaterThan( targetVersion ) )
            .toList();

      Model migratorTargetModel = targetModel;
      for ( final Migrator migrator : migratorSet ) {
         migratorTargetModel = execute( migrator, migratorTargetModel );
      }
      return migratorTargetModel;
   }
}
