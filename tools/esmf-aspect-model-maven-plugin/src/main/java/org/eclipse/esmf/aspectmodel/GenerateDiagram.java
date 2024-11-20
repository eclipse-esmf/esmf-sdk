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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.generator.DocumentGenerationException;
import org.eclipse.esmf.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import org.eclipse.esmf.aspectmodel.generator.diagram.DiagramGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.diagram.DiagramGenerationConfigBuilder;
import org.eclipse.esmf.metamodel.Aspect;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = GenerateDiagram.MAVEN_GOAL, defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateDiagram extends AspectModelMojo {
   public static final String MAVEN_GOAL = "generateDiagram";
   private static final Logger LOG = LoggerFactory.getLogger( GenerateDiagram.class );

   @Parameter( required = true, property = "targetFormat" )
   private Set<String> targetFormats;

   @Override
   public void executeGeneration() throws MojoExecutionException {
      validateParameters();

      final Set<Aspect> aspects = loadAspects();
      try {
         final Set<DiagramGenerationConfig.Format> formats = targetFormats.stream()
               .map( targetFormat -> DiagramGenerationConfig.Format.valueOf( targetFormat.toUpperCase() ) )
               .collect( Collectors.toSet() );

         for ( final Aspect aspect : aspects ) {
            for ( final DiagramGenerationConfig.Format format : formats ) {
               final DiagramGenerationConfig config = DiagramGenerationConfigBuilder.builder().format( format ).build();
               new AspectModelDiagramGenerator( aspect, config )
                     .generateThrowing( name -> getOutputStreamForFile( name, outputDirectory ) );
            }
         }
      } catch ( final DocumentGenerationException exception ) {
         throw new MojoExecutionException( "Could not generate diagram.", exception );
      }
      LOG.info( "Successfully generated Aspect Model diagram(s)." );
   }

   @Override
   protected void validateParameters() throws MojoExecutionException {
      if ( targetFormats == null || targetFormats.isEmpty() ) {
         throw new MojoExecutionException( "Please provide target formats." );
      }

      for ( final String targetFormat : targetFormats ) {
         if ( Arrays.stream( DiagramGenerationConfig.Format.values() )
               .noneMatch( x -> x.toString().equals( targetFormat.toUpperCase() ) ) ) {
            throw new MojoExecutionException( "Invalid target format: " + targetFormat + ". Valid formats are "
                  + DiagramGenerationConfig.Format.allValues() + "." );
         }
      }
      super.validateParameters();
   }
}
