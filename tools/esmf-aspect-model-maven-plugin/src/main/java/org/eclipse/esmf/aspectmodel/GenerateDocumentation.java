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

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.generator.docu.AspectModelDocumentationGenerator;
import org.eclipse.esmf.aspectmodel.generator.docu.DocumentationGenerationConfigBuilder;
import org.eclipse.esmf.metamodel.Aspect;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = "generateDocumentation", defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateDocumentation extends AspectModelMojo {
   private static final Logger LOG = LoggerFactory.getLogger( GenerateDocumentation.class );

   @Parameter
   private final String htmlCustomCssFilePath = "";

   @Override
   public void executeGeneration() throws MojoExecutionException {
      validateParameters();

      try {
         final Set<Aspect> aspects = loadAspects();
         for ( final Aspect model : aspects ) {
            final DocumentationGenerationConfigBuilder configBuilder = DocumentationGenerationConfigBuilder.builder();
            if ( !htmlCustomCssFilePath.isEmpty() ) {
               final String css = FileUtils.readFileToString( new File( htmlCustomCssFilePath ), "UTF-8" );
               configBuilder.stylesheet( css );
            }
            new AspectModelDocumentationGenerator( model, configBuilder.build() ).generate();
         }
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not load custom CSS file.", exception );
      }
      LOG.info( "Successfully generated Aspect model documentation." );
   }
}
