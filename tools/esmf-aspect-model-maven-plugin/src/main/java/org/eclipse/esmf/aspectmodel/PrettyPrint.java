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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = "prettyPrint", defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class PrettyPrint extends AspectModelMojo {
   private static final Logger LOG = LoggerFactory.getLogger( PrettyPrint.class );

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      validateParameters();

      for ( final AspectModel aspectModel : loadModels() ) {
         final Aspect aspect = aspectModel.aspect();
         final String formatted = AspectSerializer.INSTANCE.apply( aspect );
         final String aspectModelFileName = String.format( "%s.ttl", aspect.getName() );
         try ( final FileOutputStream streamForFile = getOutputStreamForFile( aspectModelFileName, outputDirectory );
               final PrintWriter writer = new PrintWriter( streamForFile ) ) {
            writer.println( formatted );
         } catch ( final IOException exception ) {
            throw new MojoExecutionException( "Could not write Aspect", exception );
         }
         LOG.info( "Successfully printed Aspect Model {}.", aspect.getName() );
      }
   }
}
