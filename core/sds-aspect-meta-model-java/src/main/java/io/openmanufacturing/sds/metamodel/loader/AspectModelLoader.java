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

package io.openmanufacturing.sds.metamodel.loader;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.UnsupportedVersionException;
import io.openmanufacturing.sds.aspectmodel.resolver.AspectModelResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.InvalidModelException;
import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.InvalidRootElementCountException;
import io.openmanufacturing.sds.aspectmodel.resolver.exceptions.InvalidVersionException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.versionupdate.MigratorService;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.vavr.control.Try;

/**
 * Provides functionality to load an Aspect Model from a {@link VersionedModel} and use the correct BAMM resources to
 * instantiate it.
 *
 * Instances of {@code VersionedModel} are gained through an {@link AspectModelResolver}.
 */
public class AspectModelLoader {
   private static final Logger LOG = LoggerFactory.getLogger( AspectModelLoader.class );

   private static final Set<KnownVersion> supportedVersions = ImmutableSet.of(
         KnownVersion.BAMM_1_0_0,
         KnownVersion.BAMM_2_0_0
   );

   private static final MigratorService migratorService = new MigratorService();

   private AspectModelLoader() {
   }

   /**
    * Processes a {@link Model model} and creates an {@link Aspect}.
    *
    * @param versionedModel the Aspect Model to be processed
    * @return A {@link Try} containing the {@link Aspect} for the {@link Model} on success
    *       and an {@link UnsupportedOperationException} when the Aspect model does not contain exactly one Aspect.
    */
   private static Try<Aspect> load( final VersionedModel versionedModel ) {
      final Model model = versionedModel.getModel();
      final Optional<KnownVersion> metaModelVersion = KnownVersion
            .fromVersionString( versionedModel.getVersion().toString() );
      if ( metaModelVersion.isEmpty() ) {
         return Try.failure( new UnsupportedVersionException(
               versionedModel.getVersion() ) );
      }
      final BAMM bamm = new BAMM( metaModelVersion.get() );
      final StmtIterator iterator = model.listStatements( null, RDF.type, bamm.Aspect() );
      try {
         final ModelElementFactory modelElementFactory = new ModelElementFactory( metaModelVersion.get(), model );
         final Aspect aspect = modelElementFactory.create( Aspect.class, iterator.nextStatement().getSubject() );
         return Try.success( aspect );
      } catch ( final RuntimeException exception ) {
         return Try.failure(
               new InvalidModelException( "Could not load Aspect model, please make sure the model is valid.",
                     exception ) );
      }
   }

   /**
    * Creates an {@link Aspect} instance from a Turtle input model.
    *
    * @param versionedModel The RDF model representation of the Aspect model
    * @return A {@link Try} containing the {@link Aspect} on success and an {@link InvalidRootElementCountException}
    *       (when the Aspect model does not contain exactly one Aspect) or {@link InvalidVersionException}
    *       (when the meta model version is not supported by the Aspect loader) on failure
    *
    * @see AspectModelResolver
    */
   public static Try<Aspect> fromVersionedModel( final VersionedModel versionedModel ) {
      return migratorService
            .updateMetaModelVersion( versionedModel )
            .flatMap( migratedModel -> {
               final Optional<KnownVersion> metaModelVersion = KnownVersion
                     .fromVersionString( migratedModel.getVersion().toString() );
               if ( metaModelVersion.isEmpty() ) {
                  return Try.failure( new UnsupportedVersionException( migratedModel.getVersion() ) );
               }

               final KnownVersion version = metaModelVersion.get();
               if ( !supportedVersions.contains( version ) ) {
                  return Try.failure( new InvalidVersionException(
                        "No Aspect loader is known to support BAMM version " + version
                              .toVersionString() ) );
               }

               final List<Try<AspectModelUrn>> list = getUrns( migratedModel, version );
               if ( list.size() != 1 ) {
                  return Try.failure( new InvalidRootElementCountException(
                        "Aspect model does not contain exactly one Aspect" ) );
               }

               return load( migratedModel );
            } );
   }

   private static List<Try<AspectModelUrn>> getUrns( final VersionedModel migratedModel, final KnownVersion version ) {
      final BAMM bamm = new BAMM( version );
      final List<Try<AspectModelUrn>> list =
            migratedModel.getModel().listStatements( null, RDF.type, bamm.Aspect() )
                         .mapWith( statement -> migratorService.getSdsMigratorFactory()
                                                               .createAspectMetaModelResourceResolver()
                                                               .getAspectModelUrn( statement.getSubject().getURI() ) )
                         .toList();
      return list;
   }

   /**
    * Creates an {@link Aspect} instance from a Turtle input model.
    *
    * @param versionedModel The RDF model representation of the Aspect model
    * @return The Aspect
    *
    * @throws AspectLoadingException with a cause of either {@link UnsupportedOperationException}
    *       (when the Aspect model does not contain exactly one Aspect) or {@link InvalidVersionException}
    *       (when the meta model version is not supported by the Aspect loader)
    * @see #fromVersionedModel(VersionedModel)
    */
   public static Aspect fromVersionedModelUnchecked( final VersionedModel versionedModel ) {
      return fromVersionedModel( versionedModel ).getOrElseThrow( cause -> {
         LOG.error( "Could not load Aspect", cause );
         throw new AspectLoadingException( cause );
      } );
   }
}
