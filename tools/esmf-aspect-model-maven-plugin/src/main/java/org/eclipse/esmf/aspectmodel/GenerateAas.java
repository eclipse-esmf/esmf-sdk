/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

import org.eclipse.esmf.aspectmodel.aas.AasFileFormat;
import org.eclipse.esmf.aspectmodel.aas.AasGenerationConfig;
import org.eclipse.esmf.aspectmodel.aas.AasGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.aas.AspectModelAasGenerator;
import org.eclipse.esmf.metamodel.Aspect;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo( name = GenerateAas.MAVEN_GOAL, defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateAas extends AspectModelMojo {
   public static final String MAVEN_GOAL = "generateAas";

   @Parameter( required = true, property = "targetFormat" )
   private String targetFormat;

   @Override
   public void executeGeneration() throws MojoExecutionException, MojoFailureException {
      validateParameters();
      final Set<Aspect> aspects = loadAspects();
      final AasGenerationConfig config = AasGenerationConfigBuilder.builder()
            .format( AasFileFormat.valueOf( targetFormat.toUpperCase() ) )
            .build();
      for ( final Aspect aspect : aspects ) {
         new AspectModelAasGenerator( aspect, config ).generateThrowing(
               name -> getOutputStreamForFile( name, outputDirectory ) );
      }
   }

   @Override
   protected void validateParameters() throws MojoExecutionException {
      if ( targetFormat == null ) {
         throw new MojoExecutionException( "Please provide target format." );
      }
      if ( Arrays.stream( AasFileFormat.values() )
            .noneMatch( x -> x.toString().equals( targetFormat.toLowerCase() ) ) ) {
         throw new MojoExecutionException( "Invalid target format: " + targetFormat + ". Valid formats are "
               + AasFileFormat.allValues() + "." );
      }
      super.validateParameters();
   }
}
