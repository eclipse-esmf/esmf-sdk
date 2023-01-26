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
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.generator.diagram.AspectModelDiagramGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.metamodel.AspectContext;

@Mojo( name = "generateDiagram", defaultPhase =  LifecyclePhase.GENERATE_RESOURCES )
public class GenerateDiagram extends AspectModelMojo {

   private final Logger logger = LoggerFactory.getLogger( GenerateDiagram.class );

   @Parameter( required = true, property = "targetFormat" )
   private Set<String> targetFormats;

   @Override
   public void execute() throws MojoExecutionException {
      validateParameters();

      final Set<AspectContext> aspectModels = loadModelsOrFail();
      try {
         final Set<AspectModelDiagramGenerator.Format> formats = targetFormats.stream()
               .map( targetFormat -> AspectModelDiagramGenerator.Format.valueOf( targetFormat.toUpperCase() ) )
               .collect( Collectors.toSet() );

         for ( final AspectContext aspectModel : aspectModels ) {
            final AspectModelDiagramGenerator generator = new AspectModelDiagramGenerator( aspectModel.rdfModel() );
            generator.generateDiagrams( formats, name -> getStreamForFile( name, outputDirectory ) );
         }
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not generate diagram.", exception );
      } catch ( final IllegalArgumentException exception ) {
         throw new MojoExecutionException( "Invalid target format provided. Possible formats are dot, svg & png.", exception );
      }
      logger.info( "Successfully generated Aspect Model diagram(s)." );
   }

   @Override
   protected void validateParameters() throws MojoExecutionException {
      if ( targetFormats == null || targetFormats.isEmpty() ) {
         throw new MojoExecutionException( "Please provide target formats." );
      }
      super.validateParameters();
   }
}
