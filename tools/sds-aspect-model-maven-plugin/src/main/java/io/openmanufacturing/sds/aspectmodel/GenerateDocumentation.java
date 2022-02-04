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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.generator.docu.AspectModelDocumentationGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;

@Mojo( name = "generateDocumentation", defaultPhase =  LifecyclePhase.GENERATE_RESOURCES )
public class GenerateDocumentation extends AspectModelMojo {

   private final Logger logger = LoggerFactory.getLogger( GenerateDocumentation.class );

   @Parameter
   private String htmlCustomCSSFilePath = "";

   @Override
   public void execute() throws MojoExecutionException {
      validateParameters();

      try {
         final Set<VersionedModel> aspectModels = loadModelsOrFail();
         for ( final VersionedModel aspectModel : aspectModels ) {
            final AspectModelDocumentationGenerator generator = new AspectModelDocumentationGenerator( aspectModel );
            final Map<AspectModelDocumentationGenerator.HtmlGenerationOption, String> generationArgs = new HashMap<>();
            generationArgs.put( AspectModelDocumentationGenerator.HtmlGenerationOption.STYLESHEET, "" );
            if ( !htmlCustomCSSFilePath.isEmpty() ) {
               final String css = FileUtils.readFileToString( new File( htmlCustomCSSFilePath ), "UTF-8" );
               generationArgs.put( AspectModelDocumentationGenerator.HtmlGenerationOption.STYLESHEET, css );
            }
            generator.generate( artifact -> getStreamForFile( artifact, outputDirectory ), generationArgs );
         }
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not load custom CSS file.", exception );
      }
      logger.info( "Successfully generated Aspect model documentation." );
   }
}
