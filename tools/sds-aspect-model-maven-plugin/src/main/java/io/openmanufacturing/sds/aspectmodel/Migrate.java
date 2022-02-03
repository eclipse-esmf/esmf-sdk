/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel;

import java.io.File;
import java.io.PrintWriter;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.serializer.PrettyPrinter;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.versionupdate.MigratorService;
import io.vavr.control.Try;

@Mojo( name = "migrate", defaultPhase =  LifecyclePhase.INITIALIZE )
public class Migrate extends AspectModelMojo {

   private final Logger logger = LoggerFactory.getLogger( Migrate.class );

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      final File inputFile = new File( aspectModelFilePath );
      final AspectModelUrn aspectModelUrn = fileToUrn( inputFile );
      final PrintWriter printWriter = initializePrintWriter( aspectModelUrn );

      final MigratorService migratorService = new MigratorService();
      final VersionedModel versionedModel = loadButNotResolveModel();
      final Try<VersionedModel> migratedModel = migratorService.updateMetaModelVersion( versionedModel );
      if ( migratedModel.isFailure() ) {
         throw new MojoFailureException( "Failed to migrate Aspect model.", migratedModel.getCause() );
      }
      final PrettyPrinter prettyPrinter = new PrettyPrinter( migratedModel.get(), aspectModelUrn, printWriter );
      prettyPrinter.print();
      printWriter.close();
      logger.info( "Successfully migrated Aspect model to latest BAMM version." );
   }
}
