/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.resolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.fs.ModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RiotException;

/**
 * Resolution strategy for Aspect model URNs that finds Aspect model files in the local file system.
 */
public class FileSystemStrategy implements ResolutionStrategy {
   protected final ModelsRoot modelsRoot;

   /**
    * Initialize the FileSystemStrategy with the root path of models. The directory
    * is assumed to contain a file system hierarchy as follows: {@code N/V/X.ttl} where N is the namespace,
    * V is the version of the namespace and X is the name of the model element (Aspect, Characteristic, ...).
    * Example:
    * <pre>
    * models   <-- must be configured as modelsRoot
    * └── com.example
    *     ├── 1.0.0
    *     │   ├── ExampleAspect.ttl
    *     │   ├── exampleProperty.ttl
    *     │   └── ExampleCharacteristic.ttl
    *     └── 1.1.0
    *         └── ...
    * </pre>
    *
    * @param modelsRoot The root directory for model files
    */
   public FileSystemStrategy( final Path modelsRoot ) {
      this( new StructuredModelsRoot( modelsRoot ) );
   }

   /**
    * Initialize the FileSystemStrategy with the root path of models.
    *
    * @param modelsRoot The root directory for model files
    */
   public FileSystemStrategy( final ModelsRoot modelsRoot ) {
      this.modelsRoot = modelsRoot;
   }

   /**
    * Returns the {@link Model} that corresponds to the given model URN
    *
    * @param aspectModelUrn The model URN
    * @return The model on success, {@link IllegalArgumentException} if the model file can not be read,
    * {@link RiotException} on parser error, {@link MalformedURLException} if the AspectModelUrn is invalid,
    * {@link FileNotFoundException} if no file containing the element was found
    */
   @Override
   public AspectModelFile apply( final AspectModelUrn aspectModelUrn, final ResolutionStrategySupport resolutionStrategySupport ) {
      final List<ModelResolutionException.LoadingFailure> checkedLocations = new ArrayList<>();

      final File namedResourceFile = modelsRoot.determineAspectModelFile( aspectModelUrn );
      if ( namedResourceFile.exists() ) {
         final Try<RawAspectModelFile> tryFile = Try.of( () -> AspectModelFileLoader.load( namedResourceFile ) );
         if ( tryFile.isFailure() ) {
            checkedLocations.add(
                  new ModelResolutionException.LoadingFailure( aspectModelUrn, namedResourceFile.getAbsolutePath(),
                        tryFile.getCause().getMessage(), tryFile.getCause() ) );
         }
         final RawAspectModelFile loadedFile = tryFile.get();
         if ( resolutionStrategySupport.containsDefinition( loadedFile, aspectModelUrn ) ) {
            return loadedFile;
         }
      } else {
         checkedLocations.add( new ModelResolutionException.LoadingFailure( aspectModelUrn, namedResourceFile.getAbsolutePath(),
               "File does not exist" ) );
      }

      for ( final Iterator<URI> it = modelsRoot.namespaceContents( aspectModelUrn ).iterator(); it.hasNext(); ) {
         final URI uri = it.next();
         final File file = Paths.get( uri ).toFile();
         final Try<RawAspectModelFile> tryFile = Try.of( () -> AspectModelFileLoader.load( file ) );
         if ( tryFile.isFailure() ) {
            checkedLocations.add( new ModelResolutionException.LoadingFailure( aspectModelUrn, file.getAbsolutePath(),
                  "Could not load file", tryFile.getCause() ) );
            continue;
         }
         final AspectModelFile result = tryFile.get();
         if ( resolutionStrategySupport.containsDefinition( result, aspectModelUrn ) ) {
            return result;
         }
         checkedLocations.add(
               new ModelResolutionException.LoadingFailure( aspectModelUrn, file.getAbsolutePath(),
                     "File does not contain the element definition" ) );
      }

      throw new ModelResolutionException( checkedLocations );
   }

   @Override
   public String toString() {
      return "FileSystemStrategy(root=" + modelsRoot + ')';
   }

   @Override
   public Stream<URI> listContents() {
      return modelsRoot.contents();
   }

   @Override
   public Stream<URI> listContentsForNamespace( final AspectModelUrn namespace ) {
      return modelsRoot.namespaceContents( namespace );
   }

   @Override
   public Stream<AspectModelFile> loadContents() {
      return modelsRoot.paths()
            .map( Path::toFile )
            .map( file -> Try.of( () -> AspectModelFileLoader.load( file ) ).getOrElseThrow( throwable ->
                  new ModelResolutionException( "Could not load file", throwable ) ) );
   }

   @Override
   public Stream<AspectModelFile> loadContentsForNamespace( final AspectModelUrn namespace ) {
      return modelsRoot.namespaceContents( namespace )
            .map( Paths::get )
            .map( Path::toFile )
            .map( file -> Try.of( () -> AspectModelFileLoader.load( file ) ).getOrElseThrow( throwable ->
                  new ModelResolutionException( "Could not load file", throwable ) ) );
   }
}
