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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.generator.docu.AspectModelDocumentationGenerator;
import io.openmanufacturing.sds.aspectmodel.resolver.ModelResolutionException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationReport;
import io.openmanufacturing.sds.aspectmodel.validation.services.AspectModelValidator;
import io.vavr.control.Try;

@Mojo( name = "generateDocumentation", defaultPhase =  LifecyclePhase.GENERATE_RESOURCES )
public class GenerateDocumentation extends AspectModelMojo {

   private final Logger logger = LoggerFactory.getLogger( GenerateDocumentation.class );

   @Parameter( required = false )
   protected String htmlCustomCSSFilePath;

   @Override
   public void execute() throws MojoExecutionException {
      try {
         final AspectModelDocumentationGenerator generator = new AspectModelDocumentationGenerator( loadModelOrFail( aspectModelFilePath ) );
         final Map<AspectModelDocumentationGenerator.HtmlGenerationOption, String> generationArgs = new HashMap<>();
         generationArgs.put( AspectModelDocumentationGenerator.HtmlGenerationOption.STYLESHEET, "" );
         if ( htmlCustomCSSFilePath != null && htmlCustomCSSFilePath.isEmpty() ) {
            final String css = FileUtils.readFileToString( new File( htmlCustomCSSFilePath ), "UTF-8" );
            generationArgs.put( AspectModelDocumentationGenerator.HtmlGenerationOption.STYLESHEET, css );
         }
         generator.generate( artifact -> getStreamForFile( artifact, outputDirectory ), generationArgs );
         logger.info( "Successfully generated Aspect model documentation." );
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not load custom CSS file.", exception );
      }
   }

   private VersionedModel loadModelOrFail( final String aspectModelFilePath ) throws MojoExecutionException {
      final Try<VersionedModel> versionedModel = loadAndResolveModel( aspectModelFilePath );
      if ( versionedModel.isFailure() ) {
         final Throwable loadModelFailureCause = versionedModel.getCause();

         // Model can not be loaded, root cause e.g. File not found
         if ( loadModelFailureCause instanceof IllegalArgumentException ) {
            final String errorMessage = String.format( "Can not open file for reading: %s.", aspectModelFilePath );
            throw new MojoExecutionException( errorMessage );
         }

         if ( loadModelFailureCause instanceof ModelResolutionException ) {
            throw new MojoExecutionException( "Could not resolve all model elements" );
         }

         // Another exception, e.g. syntax error. Let the validator handle this
         final AspectModelValidator validator = new AspectModelValidator();
         final ValidationReport report = validator.validate( versionedModel );
         throw new MojoExecutionException( report.toString() );
      }
      return versionedModel.get();
   }

   private FileOutputStream getStreamForFile( final String artifactName, final String outputDirectory ) {
      try {
         final File directory = new File( outputDirectory );
         directory.mkdirs();
         final File file = new File( directory.getPath() + File.separator + artifactName );
         return new FileOutputStream( file );
      } catch ( final FileNotFoundException exception ) {
         throw new RuntimeException( "Output file for Aspect model documentation generation not found.", exception );
      }
   }
}
