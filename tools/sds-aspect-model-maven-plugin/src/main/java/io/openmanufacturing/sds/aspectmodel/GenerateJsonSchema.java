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
import java.io.OutputStream;
import java.util.Locale;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.openmanufacturing.sds.aspectmodel.generator.jsonschema.AspectModelJsonSchemaGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.metamodel.Aspect;
import io.openmanufacturing.sds.metamodel.loader.AspectModelLoader;

@Mojo( name = "generateJsonSchema", defaultPhase =  LifecyclePhase.GENERATE_RESOURCES )
public class GenerateJsonSchema extends AspectModelMojo {

   private final Logger logger = LoggerFactory.getLogger( GenerateJsonSchema.class );
   private final AspectModelJsonSchemaGenerator generator = new AspectModelJsonSchemaGenerator();

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      validateParameters();

      final Set<VersionedModel> aspectModels = loadModelsOrFail();
      try {
         for ( final VersionedModel aspectModel : aspectModels ) {
            final Aspect aspect = AspectModelLoader.fromVersionedModelUnchecked( aspectModel );
            //TODO to extend so that language can be selected as in bamm-cli
            final JsonNode schema = generator.apply( aspect, Locale.ENGLISH );

            final OutputStream out = getStreamForFile( aspect.getName() + ".schema.json", outputDirectory );
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue( out, schema );
            out.flush();
         }
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not format JSON Schema", exception );
      }
      logger.info( "Successfully generated JSON Schema for Aspect Models." );
   }
}
