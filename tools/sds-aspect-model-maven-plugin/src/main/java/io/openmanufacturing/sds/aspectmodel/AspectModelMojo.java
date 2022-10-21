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
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import io.openmanufacturing.sds.aspectmodel.resolver.AspectModelResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.FileSystemStrategy;
import io.openmanufacturing.sds.aspectmodel.resolver.ModelResolutionException;
import io.openmanufacturing.sds.aspectmodel.resolver.services.SdsAspectMetaModelResourceResolver;
import io.openmanufacturing.sds.aspectmodel.resolver.services.TurtleLoader;
import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;
import io.openmanufacturing.sds.aspectmodel.shacl.violation.Violation;
import io.openmanufacturing.sds.aspectmodel.urn.AspectModelUrn;
import io.openmanufacturing.sds.aspectmodel.validation.services.AspectModelValidator;
import io.openmanufacturing.sds.aspectmodel.validation.services.ViolationFormatter;
import io.vavr.control.Try;

public abstract class AspectModelMojo extends AbstractMojo {

   @Parameter( defaultValue = "${basedir}/src/main/resources/aspects" )
   private final String modelsRootDirectory = System.getProperty( "user.dir" ) + "/src/main/resources/aspects";

   @Parameter( required = true, property = "include" )
   private Set<String> includes;

   @Parameter
   protected String outputDirectory = "";

   protected void validateParameters() throws MojoExecutionException {
      if ( includes == null || includes.isEmpty() ) {
         throw new MojoExecutionException( "Missing configuration. Please provide Aspect Models to be included." );
      }
   }

   protected Set<Try<VersionedModel>> loadAndResolveModels() throws MojoExecutionException {
      final Path modelsRoot = Path.of( modelsRootDirectory );
      return includes.stream().map( AspectModelUrn::fromUrn )
            .map( urn -> new AspectModelResolver().resolveAspectModel( new FileSystemStrategy( modelsRoot ), urn ) )
            .collect( Collectors.toSet() );
   }

   protected Set<VersionedModel> loadModelsOrFail() throws MojoExecutionException {
      final Set<VersionedModel> versionedModels = new HashSet<>();
      final Set<Try<VersionedModel>> resolvedModels = loadAndResolveModels();
      for ( final Try<VersionedModel> versionedModel : resolvedModels ) {
         if ( versionedModel.isSuccess() ) {
            versionedModels.add( versionedModel.get() );
            continue;
         }
         handleFailedModelResolution( versionedModel );
      }
      return versionedModels;
   }

   private void handleFailedModelResolution( final Try<VersionedModel> failedModel ) throws MojoExecutionException {
      final Throwable loadModelFailureCause = failedModel.getCause();

      // Model can not be loaded, root cause e.g. File not found
      if ( loadModelFailureCause instanceof IllegalArgumentException ) {
         throw new MojoExecutionException( "Can not open file in models root directory.", loadModelFailureCause );
      }

      if ( loadModelFailureCause instanceof ModelResolutionException ) {
         throw new MojoExecutionException( "Could not resolve all model elements", loadModelFailureCause );
      }

      // Another exception, e.g. syntax error. Let the validator handle this
      final AspectModelValidator validator = new AspectModelValidator();
      final List<Violation> violations = validator.validateModel( failedModel );
      throw new MojoExecutionException( new ViolationFormatter().apply( violations ), loadModelFailureCause );
   }

   protected Map<AspectModelUrn, VersionedModel> loadButNotResolveModels() throws MojoExecutionException {
      final Map<AspectModelUrn, VersionedModel> versionedModels = new HashMap<>();
      for ( final String urn : includes ) {
         final AspectModelUrn aspectModelUrn = AspectModelUrn.fromUrn( urn );
         final String aspectModelFilePath = String.format( "%s/%s/%s/%s.ttl", modelsRootDirectory, aspectModelUrn.getNamespace(), aspectModelUrn.getVersion(),
               aspectModelUrn.getName() );

         final File inputFile = new File( aspectModelFilePath ).getAbsoluteFile();
         try ( final InputStream inputStream = new FileInputStream( inputFile ) ) {
            final SdsAspectMetaModelResourceResolver metaModelResourceResolver = new SdsAspectMetaModelResourceResolver();
            final Try<VersionedModel> versionedModel = TurtleLoader.loadTurtle( inputStream )
                  .flatMap( model -> metaModelResourceResolver.getBammVersion( model )
                        .flatMap( metaModelVersion -> metaModelResourceResolver.mergeMetaModelIntoRawModel( model, metaModelVersion ) ) );
            if ( versionedModel.isFailure() ) {
               final String errorMessage = String.format( "Failed to load Aspect Model %s.", aspectModelUrn.getName() );
               throw new MojoExecutionException( errorMessage, versionedModel.getCause() );
            }
            versionedModels.put( aspectModelUrn, versionedModel.get() );
         } catch ( final IOException exception ) {
            final String errorMessage = String.format( "Failed to load Aspect Model %s.", aspectModelUrn.getName() );
            throw new MojoExecutionException( errorMessage, exception );
         }
      }
      return versionedModels;
   }

   protected FileOutputStream getStreamForFile( final String artifactName, final String outputDirectory ) {
      try {
         final File directory = new File( outputDirectory );
         directory.mkdirs();
         final File file = new File( directory.getPath() + File.separator + artifactName );
         return new FileOutputStream( file );
      } catch ( final FileNotFoundException exception ) {
         throw new RuntimeException( "Output file for Aspect Model documentation generation not found.", exception );
      }
   }

   protected PrintWriter initializePrintWriter( final AspectModelUrn aspectModelUrn ) {
      final String aspectModelFileName = String.format( "%s.ttl", aspectModelUrn.getName() );
      final FileOutputStream streamForFile = getStreamForFile( aspectModelFileName, outputDirectory );
      return new PrintWriter( streamForFile );
   }

}
