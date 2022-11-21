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
package io.openmanufacturing.sds.aspectmodel.versionupdate;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.VersionNumber;
import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.InvalidVersionException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.versionupdate.migrator.Migrator;
import io.vavr.control.Try;

/**
 * The service that migrates all migrators in the correct order.
 */
public class MigratorService {
   private static final Logger LOG = LoggerFactory.getLogger( MigratorService.class );
   private final Optional<MigratorFactory> migratorFactory;
   private final SdsMigratorFactory sdsMigratorFactory = new SdsMigratorFactory();

   public MigratorService() {
      migratorFactory = Optional.empty();
   }

   public MigratorService( final MigratorFactory migratorFactory ) {
      this.migratorFactory = Optional.of( migratorFactory );
   }

   public Optional<MigratorFactory> getMigratorFactory() {
      return migratorFactory;
   }

   public SdsMigratorFactory getSdsMigratorFactory() {
      return sdsMigratorFactory;
   }

   private Model execute( final Migrator migrator, final Model sourceModel ) {
      LOG.info( "Start Migration for {} to {}", migrator.sourceVersion(), migrator.targetVersion() );
      final String description = migrator.getDescription().orElse( "" );
      LOG.info( "Migration step {} {}", migrator.getClass().getSimpleName(), description );
      final Model targetModel = migrator.migrate( sourceModel );
      LOG.info( "End Migration" );
      return targetModel;
   }

   /**
    * Semantically migrates an Aspect model from its current meta model version to a given target meta model version.
    * This is done by composing the {@link Migrator}s that update from one version to the next into one function
    * which is then applied to the given source model.
    *
    * @param versionedModel the source model
    * @return the resulting {@link VersionedModel} that corresponds to the input Aspect model, but with the new meta model version
    */
   public Try<VersionedModel> updateMetaModelVersion( final VersionedModel versionedModel ) {
      final VersionNumber targetVersion = VersionNumber.parse( KnownVersion.getLatest().toVersionString() );
      VersionNumber sourceVersion = versionedModel.getMetaModelVersion();
      Model migrationModel = versionedModel.getRawModel();

      if ( migratorFactory.isPresent() ) {
         migrationModel = customMigration( migratorFactory.get(), sourceVersion, versionedModel );
         sourceVersion = VersionNumber.parse( KnownVersion.BAMM_1_0_0.toVersionString() );
      }

      if ( sourceVersion.equals( targetVersion ) ) {
         return getSdsMigratorFactory().createAspectMetaModelResourceResolver().mergeMetaModelIntoRawModel( migrationModel, targetVersion );
      }

      if ( sourceVersion.greaterThan( targetVersion ) ) {
         return Try.failure( new InvalidVersionException(
               String.format( "Model version %s can not be updated to version %s", sourceVersion, targetVersion ) ) );
      }

      return migration( sourceVersion, targetVersion, migrationModel );
   }

   private Model customMigration( final MigratorFactory migratorFactory, final VersionNumber sourceVersion, final VersionedModel versionedModel ) {
      return migrate( migratorFactory.createMigrators(), sourceVersion, migratorFactory.getLatestVersion(), versionedModel.getRawModel() );
   }

   private Try<VersionedModel> migration( final VersionNumber sourceVersion, final VersionNumber targetVersion, final Model targetModel ) {
      final Model model = migrate( sdsMigratorFactory.createMigrators(), sourceVersion, targetVersion, targetModel );
      return getSdsMigratorFactory().createAspectMetaModelResourceResolver().mergeMetaModelIntoRawModel( model, targetVersion );
   }

   private Model migrate( final List<Migrator> migrators, final VersionNumber sourceVersion, final VersionNumber targetVersion, final Model targetModel ) {
      if ( migrators.isEmpty() ) {
         return targetModel;
      }

      final Comparator<Migrator> comparator = Comparator.comparing( Migrator::sourceVersion );
      final List<Migrator> migratorSet = migrators.stream()
            .sorted( comparator.thenComparing( Migrator::order ) )
            .dropWhile( migrator -> !migrator.sourceVersion().equals( sourceVersion ) )
            .takeWhile( migrator -> !migrator.targetVersion().greaterThan( targetVersion ) )
            .collect( Collectors.toList() );

      Model migratorTargetModel = targetModel;
      for ( final Migrator migrator : migratorSet ) {
         migratorTargetModel = execute( migrator, migratorTargetModel );
      }
      return migratorTargetModel;
   }
}
