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
import java.util.Set;

import org.eclipse.esmf.aspectmodel.generator.json.AspectModelJsonPayloadGenerator;
import org.eclipse.esmf.metamodel.Aspect;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = "generateJsonPayload", defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateJsonPayload extends AspectModelMojo {
   private static final Logger LOG = LoggerFactory.getLogger( GenerateJsonPayload.class );

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      validateParameters();

      final Set<Aspect> aspects = loadAspects();
      try {
         for ( final Aspect context : aspects ) {
            final AspectModelJsonPayloadGenerator generator = new AspectModelJsonPayloadGenerator( context );
            generator.generateJsonPretty( name -> getOutputStreamForFile( name + ".json", outputDirectory ) );
         }
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not generate JSON payload.", exception );
      }
      LOG.info( "Successfully generated example JSON payloads for Aspect Models." );
   }
}
