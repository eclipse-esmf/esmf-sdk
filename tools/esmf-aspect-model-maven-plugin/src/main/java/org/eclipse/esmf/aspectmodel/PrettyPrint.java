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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.serializer.PrettyPrinter;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = "prettyPrint", defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class PrettyPrint extends AspectModelMojo {
   private final Logger logger = LoggerFactory.getLogger( PrettyPrint.class );

   @Override
   public void executeGeneration() throws MojoExecutionException, MojoFailureException {
      validateParameters();
      
      final Map<AspectModelUrn, VersionedModel> aspectModels = loadButNotResolveModels();
      for ( final Map.Entry<AspectModelUrn, VersionedModel> aspectModelEntry : aspectModels.entrySet() ) {
         final AspectModelUrn aspectModelUrn = aspectModelEntry.getKey();
         try ( final PrintWriter printWriter = initializePrintWriter( aspectModelUrn ) ) {
            final VersionedModel versionedModel = aspectModelEntry.getValue();
            final PrettyPrinter prettyPrinter = new PrettyPrinter( versionedModel, aspectModelUrn, printWriter );
            prettyPrinter.print();
         } catch ( final IOException exception ) {
            throw new MojoExecutionException( "Could not write file", exception );
         }
         logger.info( "Successfully printed Aspect Model {}.", aspectModelUrn.getName() );
      }
   }
}
