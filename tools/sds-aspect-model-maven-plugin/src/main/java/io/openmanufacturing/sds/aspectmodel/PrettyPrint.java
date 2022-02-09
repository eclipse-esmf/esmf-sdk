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
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.serializer.PrettyPrinter;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;

@Mojo( name = "prettyPrint", defaultPhase =  LifecyclePhase.GENERATE_RESOURCES )
public class PrettyPrint extends AspectModelMojo {

   private final Logger logger = LoggerFactory.getLogger( PrettyPrint.class );

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      validateParameters();

      final Map<AspectModelUrn, VersionedModel> aspectModels = loadButNotResolveModels();
      for ( final Map.Entry<AspectModelUrn, VersionedModel> aspectModelEntry : aspectModels.entrySet() ) {
         final AspectModelUrn aspectModelUrn = aspectModelEntry.getKey();
         final PrintWriter printWriter = initializePrintWriter( aspectModelUrn );
         final VersionedModel versionedModel = aspectModelEntry.getValue();
         final PrettyPrinter prettyPrinter = new PrettyPrinter( versionedModel, aspectModelUrn, printWriter );
         prettyPrinter.print();
         printWriter.close();
         logger.info( "Successfully printed Aspect Model {}.", aspectModelUrn.getName() );
      }
   }
}
