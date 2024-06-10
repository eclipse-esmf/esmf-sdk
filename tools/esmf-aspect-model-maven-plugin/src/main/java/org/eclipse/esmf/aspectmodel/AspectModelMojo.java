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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.services.SammAspectMetaModelResourceResolver;
import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;
import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.DetailedViolationFormatter;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationFormatter;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;

import io.vavr.control.Try;
import org.apache.commons.lang3.tuple.Pair;
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

   protected Set<Try<Pair<VersionedModel, Aspect>>> loadAndResolveModels() {
      final Path modelsRoot = Path.of( modelsRootDirectory );
      return includes.stream().map( AspectModelUrn::fromUrn )
            .map( urn -> new AspectModelResolver().resolveAspectModel( new FileSystemStrategy( modelsRoot ), urn )
                  .flatMap( versionedModel ->
                        AspectModelLoader.getSingleAspect( versionedModel, aspect -> aspect.getName().equals( urn.getName() ) )
                              .map( aspectModel -> Pair.of( versionedModel, aspectModel ) ) )
            )
            .collect( toSet() );
   }

   protected Set<Aspect> loadModelsOrFail() throws MojoExecutionException {
      final Set<Aspect> result = new HashSet<>();
      for ( final Try<Pair<VersionedModel, Aspect>> model : loadAndResolveModels() ) {
         if ( model.isFailure() ) {
            handleFailedModelResolution( model );
         }
         result.add( model.get().getValue() );
      }
      return result;
   }

   private void handleFailedModelResolution( final Try<Pair<VersionedModel, Aspect>> failedContext ) throws MojoExecutionException {
      final Throwable loadModelFailureCause = failedContext.getCause();

      // Model can not be loaded, root cause e.g. File not found
      if ( loadModelFailureCause instanceof IllegalArgumentException ) {
         throw new MojoExecutionException( "Can not open file in models root directory.", loadModelFailureCause );
      }

      if ( loadModelFailureCause instanceof ModelResolutionException ) {
         throw new MojoExecutionException( "Could not resolve all model elements", loadModelFailureCause );
      }

      // Another exception, e.g. syntax error. Let the validator handle this
      final AspectModelValidator validator = new AspectModelValidator();
      final List<Violation> violations = validator.validateModel( failedContext.map( Pair::getKey ) );
      final String errorMessage = detailedValidationMessages
            ? new DetailedViolationFormatter().apply( violations )
            : new ViolationFormatter().apply( violations );
      throw new MojoExecutionException( errorMessage, loadModelFailureCause );
   }

   protected Map<AspectModelUrn, VersionedModel> loadButNotResolveModels() throws MojoExecutionException {
      final Map<AspectModelUrn, VersionedModel> versionedModels = new HashMap<>();
      for ( final String urn : includes ) {
         final AspectModelUrn aspectModelUrn = AspectModelUrn.fromUrn( urn );
         final String aspectModelFilePath = String.format( "%s/%s/%s/%s.ttl", modelsRootDirectory, aspectModelUrn.getNamespace(),
               aspectModelUrn.getVersion(), aspectModelUrn.getName() );

         final File inputFile = new File( aspectModelFilePath ).getAbsoluteFile();
         try ( final InputStream inputStream = new FileInputStream( inputFile ) ) {
            final SammAspectMetaModelResourceResolver metaModelResourceResolver = new SammAspectMetaModelResourceResolver();
            final Try<VersionedModel> versionedModel = TurtleLoader.loadTurtle( inputStream )
                  .flatMap( model -> metaModelResourceResolver.getMetaModelVersion( model )
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

   protected FileOutputStream getOutputStreamForFile( final String artifactName, final String outputDirectory ) {
      try {
         final Path outputPath = Path.of( outputDirectory );
         Files.createDirectories( outputPath );
         return new FileOutputStream( outputPath.resolve( artifactName ).toFile() );
      } catch ( final IOException e ) {
         throw new RuntimeException( "Could not write to output " + outputDirectory );
      }
   }

   protected PrintWriter initializePrintWriter( final AspectModelUrn aspectModelUrn ) throws IOException {
      final String aspectModelFileName = String.format( "%s.ttl", aspectModelUrn.getName() );
      try ( final FileOutputStream streamForFile = getOutputStreamForFile( aspectModelFileName, outputDirectory ) ) {
         return new PrintWriter( streamForFile );
      }
   }
}
