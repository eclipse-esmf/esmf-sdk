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

package org.eclipse.esmf.aspectmodel.resolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.fs.ModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resolution strategy for Aspect model URNs that finds Aspect model files in the local file system.
 */
public class FileSystemStrategy implements ResolutionStrategy {
   private static final Logger LOG = LoggerFactory.getLogger( FileSystemStrategy.class );
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
    * {@link org.apache.jena.riot.RiotException} on parser error, {@link MalformedURLException} if the AspectModelUrn is invalid,
    * {@link FileNotFoundException} if no file containing the element was found
    */
   @Override
   public AspectModelFile apply( final AspectModelUrn aspectModelUrn, final ResolutionStrategySupport resolutionStrategySupport ) {
      final File namedResourceFile = modelsRoot.determineAspectModelFile( aspectModelUrn );
      if ( namedResourceFile.exists() ) {
         return AspectModelFileLoader.load( namedResourceFile );
      }
      return modelsRoot.namespaceContents( aspectModelUrn )
            .map( Paths::get )
            .map( Path::toFile )
            .flatMap( file ->
                  Try.of( () -> AspectModelFileLoader.load( file ) )
                        .toJavaStream()
                        .flatMap( aspectModelFile -> resolutionStrategySupport.containsDefinition( aspectModelFile, aspectModelUrn )
                              ? Stream.of( aspectModelFile )
                              : Stream.of() ) )
            .findFirst()
            .orElseThrow( () -> new ModelResolutionException(
                  "No model file containing " + aspectModelUrn + " could be found in models root: " + modelsRoot.rootPath() ) );
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
