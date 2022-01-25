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
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openmanufacturing.sds.aspectmodel.resolver.AspectModelResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.FileSystemStrategy;
import io.openmanufacturing.sds.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.validation.report.ValidationReport;
import io.openmanufacturing.sds.aspectmodel.validation.services.AspectModelValidator;
import io.vavr.CheckedFunction1;
import io.vavr.control.Option;
import io.vavr.control.Try;

@Mojo( name = "validate", defaultPhase =  LifecyclePhase.VALIDATE )
public class Validate extends AspectModelMojo {

   private final Logger logger = LoggerFactory.getLogger( Validate.class );

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      final Try<VersionedModel> versionedModel = loadAndResolveModel( aspectModelFilePath );

      final AspectModelValidator validator = new AspectModelValidator();
      final ValidationReport report = validator.validate( versionedModel );
      if ( !report.conforms() ) {
         throw new MojoFailureException( report.toString() );
      }
      logger.info( "Aspect Model is valid." );
   }

   private Try<VersionedModel> loadAndResolveModel( final String aspectModelFilePath ) throws MojoExecutionException {
      final File inputFile = new File( aspectModelFilePath ).getAbsoluteFile();
      final AspectModelUrn urn = fileToUrn( inputFile );
      return getModelRoot( inputFile ).flatMap( modelsRoot ->
            new AspectModelResolver().resolveAspectModel( new FileSystemStrategy( modelsRoot ), urn ) );
   }

   private static AspectModelUrn fileToUrn( final File inputFile ) throws MojoExecutionException {
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
