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
import java.util.Map;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.generator.json.AspectModelJsonPayloadGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.metamodel.AspectContext;

@Mojo( name = "generateJsonPayload", defaultPhase =  LifecyclePhase.GENERATE_RESOURCES )
public class GenerateJsonPayload extends AspectModelMojo {

   private final Logger logger = LoggerFactory.getLogger( GenerateJsonPayload.class );

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      validateParameters();

      final Set<AspectContext> aspectModels = loadModelsOrFail();
      try {
         for ( AspectContext context : aspectModels ) {
            final AspectModelJsonPayloadGenerator generator = new AspectModelJsonPayloadGenerator( context );
            generator.generateJsonPretty( name -> getStreamForFile( name + ".json", outputDirectory ) );
         }
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not generate JSON payload.", exception );
      }
      logger.info( "Successfully generated example JSON payloads for Aspect Models." );
   }
}
