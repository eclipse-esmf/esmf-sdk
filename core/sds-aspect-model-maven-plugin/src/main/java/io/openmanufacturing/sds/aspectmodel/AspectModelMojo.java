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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import io.openmanufacturing.sds.aspectmodel.resolver.AspectModelResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.FileSystemStrategy;
import io.openmanufacturing.sds.aspectmodel.resolver.ModelResolutionException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.services.TurtleLoader;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationReport;
import io.openmanufacturing.sds.aspectmodel.validation.services.AspectModelValidator;
import io.vavr.CheckedFunction1;
import io.vavr.control.Option;
import io.vavr.control.Try;

public abstract class AspectModelMojo extends AbstractMojo {

   @Parameter( required = true )
   protected String aspectModelFilePath;

   @Parameter
   protected String outputDirectory = "";

   protected Try<VersionedModel> loadAndResolveModel( final String aspectModelFilePath ) throws MojoExecutionException {
      final File inputFile = new File( aspectModelFilePath ).getAbsoluteFile();
      final AspectModelUrn urn = fileToUrn( inputFile );
      return getModelRoot( inputFile ).flatMap( modelsRoot ->
            new AspectModelResolver().resolveAspectModel( new FileSystemStrategy( modelsRoot ), urn ) );
   }

   protected VersionedModel loadModelOrFail( final String aspectModelFilePath ) throws MojoExecutionException {
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

   protected VersionedModel loadButNotResolveModel() throws MojoExecutionException {
      final File inputFile = new File( aspectModelFilePath ).getAbsoluteFile();
      try ( final InputStream inputStream = new FileInputStream( inputFile ) ) {
         final SdsAspectMetaModelResourceResolver metaModelResourceResolver = new SdsAspectMetaModelResourceResolver();
         final Try<VersionedModel> versionedModel = TurtleLoader.loadTurtle( inputStream )
               .flatMap( model -> metaModelResourceResolver.getBammVersion( model )
               .flatMap( metaModelVersion -> metaModelResourceResolver.mergeMetaModelIntoRawModel( model, metaModelVersion ) ) );
         if ( versionedModel.isFailure() ) {
            throw new MojoExecutionException( "Failed to load Aspect model.", versionedModel.getCause() );
         }
         return versionedModel.get();
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Failed to load Aspect model.", exception );
      }
   }

   protected FileOutputStream getStreamForFile( final String artifactName, final String outputDirectory ) {
      try {
         final File directory = new File( outputDirectory );
         directory.mkdirs();
         final File file = new File( directory.getPath() + File.separator + artifactName );
         return new FileOutputStream( file );
      } catch ( final FileNotFoundException exception ) {
         throw new RuntimeException( "Output file for Aspect model documentation generation not found.", exception );
      }
   }

   protected static AspectModelUrn fileToUrn( final File inputFile ) throws MojoExecutionException {
      final File versionDirectory = inputFile.getParentFile();
      final String rawErrorMessage = "Could not determine parent directory of %s. Please verify that the model directory structure is correct.";
      if ( versionDirectory == null ) {
         final String errorMessage = String.format( rawErrorMessage, inputFile );
         throw new MojoExecutionException( errorMessage );
      }

      final String version = versionDirectory.getName();
      final File namespaceDirectory = versionDirectory.getParentFile();
      if ( namespaceDirectory == null ) {
         final String errorMessage = String.format( rawErrorMessage, versionDirectory );
         throw new MojoExecutionException( errorMessage );
      }

      final String namespace = namespaceDirectory.getName();
      final String aspectName = FilenameUtils.removeExtension( inputFile.getName() );
      final String urn = String.format( "urn:bamm:%s:%s#%s", namespace, version, aspectName );
      final Try<AspectModelUrn> aspectModelUrn = new SdsAspectMetaModelResourceResolver().getAspectModelUrn( urn );
      if ( aspectModelUrn.isFailure() ) {
         final String urnRawErrorMessage = "The URN constructed from the input file path is invalid: %s. Please verify that the model directory structure is correct.";
         final String errorMessage = String.format( urnRawErrorMessage, urn );
         throw new MojoExecutionException( errorMessage );
      }
      return aspectModelUrn.get();
   }

   private static Try<Path> getModelRoot( final File inputFile ) {
      return Option.of( Paths.get( inputFile.getParent(), "..", ".." ) )
            .map( Path::toFile )
            .flatMap( file -> CheckedFunction1.lift( File::getCanonicalFile ).apply( file ) )
            .map( File::toPath )
            .filter( path -> path.toFile().exists() && path.toFile().isDirectory() )
            .toTry( () -> new MojoExecutionException( "Could not locate models root directory" ) );
   }

}
