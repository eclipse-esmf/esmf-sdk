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

import static java.util.stream.Collectors.toSet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.DetailedViolationFormatter;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationFormatter;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;

import io.vavr.control.Either;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class AspectModelMojo extends AbstractMojo {
   @Parameter( defaultValue = "${basedir}/src/main/resources/aspects" )
   private final String modelsRootDirectory = System.getProperty( "user.dir" ) + "/src/main/resources/aspects";

   @Parameter( required = true, property = "include" )
   protected Set<String> includes;

   @Parameter
   protected String outputDirectory = "";

   @Parameter( defaultValue = "false" )
   protected boolean detailedValidationMessages;

   protected void validateParameters() throws MojoExecutionException {
      if ( includes == null || includes.isEmpty() ) {
         throw new MojoExecutionException( "Missing configuration. Please provide Aspect Models to be included." );
      }
   }

   protected Set<Aspect> loadAspects() throws MojoExecutionException {
      Set<AspectModel> models = loadModels();
      return models.stream().map( AspectModel::aspect ).collect( toSet() );
   }

   protected Set<AspectModel> loadModels() throws MojoExecutionException {
      try {
         final Path modelsRoot = Path.of( modelsRootDirectory );
         final ResolutionStrategy fileSystemStrategy = new FileSystemStrategy( modelsRoot );
         final Set<AspectModel> result = new HashSet<>();

         for ( final String inputUrn : includes ) {
            final AspectModelUrn urn = AspectModelUrn.fromUrn( inputUrn );
            final Either<List<Violation>, AspectModel> loadingResult = new AspectModelValidator().loadModel( () ->
                  new AspectModelLoader( fileSystemStrategy ).load( urn ) );
            if ( loadingResult.isLeft() ) {
               final List<Violation> violations = loadingResult.getLeft();
               final String errorMessage = detailedValidationMessages
                     ? new DetailedViolationFormatter().apply( violations )
                     : new ViolationFormatter().apply( violations );
               throw new MojoExecutionException( errorMessage );
            }
            result.add( loadingResult.get() );
         }
         return result;
      } catch ( final MojoExecutionException exception ) {
         throw exception;
      } catch ( final Throwable throwable ) {
         throw new MojoExecutionException( "Processing error while loading Aspects", throwable );
      }
   }

   protected FileOutputStream getOutputStreamForFile( final String artifactName, final String outputDirectory ) {
      try {
         final Path outputPath = Path.of( outputDirectory );
         Files.createDirectories( outputPath );
         return new FileOutputStream( outputPath.resolve( artifactName ).toFile() );
      } catch ( final IOException e ) {
         throw new RuntimeException( "Could not write to output " + outputDirectory );
      }
   }
}
