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

package org.eclipse.esmf.aspectmodel.resolver.fs;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.esmf.aspectmodel.resolver.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import io.vavr.control.Try;

/**
 * Represents the root directory of the directory hierarchy in which Aspect Models are organized.
 * The directory is assumed to contain a file system hierarchy as follows: {@code N/V/X.ttl} where N is the namespace,
 * V is the version of the namespace and X is the name of the model element (Aspect, Characteristic, ...).
 * <pre>
 * models   <-- this is the modelsRoot
 * └── com.example
 *     ├── 1.0.0
 *     │   ├── ExampleAspect.ttl
 *     │   ├── exampleProperty.ttl
 *     │   └── ExampleCharacteristic.ttl
 *     └── 1.1.0
 *         └── ...
 * </pre>
 */
public class StructuredModelsRoot extends ModelsRoot {
   private static final String PATH_SEPARATOR = "/";

   public StructuredModelsRoot( Path modelsRoot ) {
      super( modelsRoot.toUri() );
   }

   public StructuredModelsRoot( final URI resource ) {
      super( forUri( resource ).get() );
   }

   private static Try<URI> forUri( final URI uri ) {
      return getModelRoot( uri )
            .recoverWith( x -> Try.failure( new ModelResolutionException( "Could not locate models root directory for " + uri, x ) ) );
   }

   @Override
   public URI directoryForNamespace( final AspectModelUrn urn ) {
      return rootPath()
            .resolve( urn.getNamespace() + PATH_SEPARATOR )
            .resolve( urn.getVersion() + PATH_SEPARATOR );
   }

   @Override
   public String toString() {
      return "StructuredModelsRoot(rootPath=" + rootPath() + ")";
   }

   /**
    * From an input Aspect Model file, determines the models root directory if it exists
    *
    * @param input the input model uri
    * @return the models root directory
    */
   public static Try<URI> getModelRoot( final URI input ) {
      return Try.of( () -> new File( input ) )
            .flatMap( StructuredModelsRoot::findModelsRoot )
            .orElse( StructuredModelsRoot.findModelsRootFromUrl( input ) )
            ;
   }

   private static Try<URI> findModelsRootFromUrl( URI input ) {
      return Try.of(() -> input.resolve( "../.." ) )
            .map( URI::normalize );
   }

   private static Try<URI> findModelsRoot( File file ) {
      return Try.success( file.getParent() )
            .map( parent -> Paths.get( parent, "..", ".." ) )
            .map( Path::toFile )
            .mapTry( File::getCanonicalFile )
            .filter( path -> path.exists() && path.isDirectory() )
            .map( File::toURI )
            .toTry( () -> new ModelResolutionException( "Could not locate models root directory for " + file ) );
   }

}
