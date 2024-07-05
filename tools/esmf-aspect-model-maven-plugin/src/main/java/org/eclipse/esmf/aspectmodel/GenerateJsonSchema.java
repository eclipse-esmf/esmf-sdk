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
import java.io.OutputStream;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.generator.jsonschema.AspectModelJsonSchemaGenerator;
import org.eclipse.esmf.aspectmodel.generator.jsonschema.JsonSchemaGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.jsonschema.JsonSchemaGenerationConfigBuilder;
import org.eclipse.esmf.metamodel.Aspect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = "generateJsonSchema", defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateJsonSchema extends AspectModelMojo {
   private static final Logger LOG = LoggerFactory.getLogger( GenerateJsonSchema.class );

   @Parameter( defaultValue = "en" )
   private String language;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      validateParameters();

      final Set<Aspect> aspects = loadAspects();
      final Locale locale = Optional.ofNullable( language ).map( Locale::forLanguageTag ).orElse( Locale.ENGLISH );
      try {
         for ( final Aspect aspect : aspects ) {
            final JsonSchemaGenerationConfig config = JsonSchemaGenerationConfigBuilder.builder()
                  .locale( locale )
                  .build();
            final JsonNode schema = AspectModelJsonSchemaGenerator.INSTANCE.apply( aspect, config ).getContent();
            final OutputStream out = getOutputStreamForFile( aspect.getName() + ".schema.json", outputDirectory );
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue( out, schema );
            out.flush();
         }
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not format JSON Schema", exception );
      }
      LOG.info( "Successfully generated JSON Schema for Aspect Models." );
   }
}
