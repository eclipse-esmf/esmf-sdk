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

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      final AspectModelJsonSchemaGenerator generator = new AspectModelJsonSchemaGenerator();
      final VersionedModel model = loadModelOrFail( aspectModelFilePath );
      final Aspect aspect = AspectModelLoader.fromVersionedModelUnchecked( model );
      final JsonNode schema = generator.apply( aspect );

      final OutputStream out = getStreamForFile( aspect.getName() + ".schema.json", outputDirectory );
      final ObjectMapper objectMapper = new ObjectMapper();
      try {
         objectMapper.writerWithDefaultPrettyPrinter().writeValue( out, schema );
         out.flush();
         logger.info( "Successfully generated JSON Schema for Aspect model." );
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not format JSON Schema", exception );
      }
   }
}
