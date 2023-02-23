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

package org.eclipse.esmf.aspectmodel;

import java.io.PrintWriter;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.versionupdate.MigratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.esmf.aspectmodel.serializer.PrettyPrinter;

import io.vavr.control.Try;

@Mojo( name = "migrate", defaultPhase =  LifecyclePhase.INITIALIZE )
public class Migrate extends AspectModelMojo {

   private final Logger logger = LoggerFactory.getLogger( Migrate.class );
   private final MigratorService migratorService = new MigratorService();

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      validateParameters();

      final Map<AspectModelUrn, VersionedModel> aspectModels = loadButNotResolveModels();
      for ( Map.Entry<AspectModelUrn, VersionedModel> aspectModelEntry : aspectModels.entrySet() ) {
         final AspectModelUrn aspectModelUrn = aspectModelEntry.getKey();
         final PrintWriter printWriter = initializePrintWriter( aspectModelUrn );
         final VersionedModel versionedModel = aspectModelEntry.getValue();
         final Try<VersionedModel> migratedModel = migratorService.updateMetaModelVersion( versionedModel );
         if ( migratedModel.isFailure() ) {
            final String errorMessage = String.format( "Failed to migrate Aspect Model %s.", aspectModelUrn.getName() );
            throw new MojoFailureException( errorMessage, migratedModel.getCause() );
         }
         final PrettyPrinter prettyPrinter = new PrettyPrinter( migratedModel.get(), aspectModelUrn, printWriter );
         prettyPrinter.print();
         printWriter.close();
         logger.info( "Successfully migrated Aspect Model {} to latest SAMM version.", aspectModelUrn.getName() );
      }
   }
}
