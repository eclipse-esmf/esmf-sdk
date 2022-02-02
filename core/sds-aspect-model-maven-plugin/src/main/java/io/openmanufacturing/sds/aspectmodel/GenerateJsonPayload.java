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

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.generator.json.AspectModelJsonPayloadGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;

@Mojo( name = "generateJsonPayload", defaultPhase =  LifecyclePhase.GENERATE_RESOURCES )
public class GenerateJsonPayload extends AspectModelMojo {

   private final Logger logger = LoggerFactory.getLogger( GenerateJsonPayload.class );

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      try {
         final VersionedModel model = loadModelOrFail( aspectModelFilePath );
         final AspectModelJsonPayloadGenerator generator = new AspectModelJsonPayloadGenerator( model );
         generator.generateJsonPretty( name -> getStreamForFile( name + ".json", outputDirectory ) );
         logger.info( "Successfully generated example JSON payload for Aspect model." );
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not generate JSON payload.", exception );
      }
   }
}
