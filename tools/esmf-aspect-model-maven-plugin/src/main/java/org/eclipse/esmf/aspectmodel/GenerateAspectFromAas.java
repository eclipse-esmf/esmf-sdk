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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.esmf.aspectmodel.aas.AasToAspectModelGenerator;
import org.eclipse.esmf.aspectmodel.serializer.AspectSerializer;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.Aspect;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo( name = GenerateAspectFromAas.MAVEN_GOAL, defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateAspectFromAas extends AspectModelMojo {
   public static final String MAVEN_GOAL = "generateAspectFromAas";

   @Override
   public void executeGeneration() throws MojoExecutionException, MojoFailureException {
      validateParameters();
      for ( final String include : includes ) {
         final AasToAspectModelGenerator generator = AasToAspectModelGenerator.fromFile( new File( include ) );

         for ( final Aspect aspect : generator.generateAspects() ) {
            try ( final FileOutputStream outputStreamForFile = new FileOutputStream( getOutputFile( aspect ) ) ) {
               outputStreamForFile.write( AspectSerializer.INSTANCE.aspectToString( aspect ).getBytes() );
            } catch ( final IOException exception ) {
               throw new MojoExecutionException( "Could not write file", exception );
            }
         }
      }
   }

   private File getOutputFile( final Aspect aspect ) throws MojoExecutionException {
      final AspectModelUrn urn = aspect.urn();
      final Path outputPath = Path.of( outputDirectory, urn.getNamespaceMainPart(), urn.getVersion() );
      try {
         Files.createDirectories( outputPath );
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not create directory " + outputPath );
      }
      return outputPath.resolve( aspect.getName() + ".ttl" ).toFile();
   }
}
