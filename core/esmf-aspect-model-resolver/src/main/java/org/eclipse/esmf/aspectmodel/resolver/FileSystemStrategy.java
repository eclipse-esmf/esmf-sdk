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

package org.eclipse.esmf.aspectmodel.resolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

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
public class FileSystemStrategy extends AbstractResolutionStrategy {
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
    * @return The model on success,
    * {@link IllegalArgumentException} if the model file can not be read,
    * {@link org.apache.jena.riot.RiotException} on parser error,
    * {@link MalformedURLException} if the AspectModelUrn is invalid,
    * {@link FileNotFoundException} if no file containing the element was found
    */
   @Override
   public Try<Model> apply( final AspectModelUrn aspectModelUrn ) {
      final Path directory = modelsRoot.directoryForNamespace( aspectModelUrn );
      final File namedResourceFile = directory.resolve( aspectModelUrn.getName() + ".ttl" ).toFile();
      if ( namedResourceFile.exists() ) {
         return loadFromUri( namedResourceFile.toURI() );
      }

      LOG.warn( "Looking for {}, but no {}.ttl was found. Inspecting files in {}", aspectModelUrn.getName(),
            aspectModelUrn.getName(), directory );

      final File[] files = Optional.ofNullable( directory.toFile().listFiles() ).orElse( new File[] {} );
      Arrays.sort( files );

      for ( final File file : files ) {
         if ( !file.isFile() || !file.getName().endsWith( ".ttl" ) ) {
            continue;
         }
         LOG.debug( "Looking for {} in {}", aspectModelUrn, file );
         final Try<Model> tryModel = loadFromUri( file.toURI() );
         if ( tryModel.isFailure() ) {
            LOG.debug( "Could not load model from {}", file, tryModel.getCause() );
         } else {
            final Model model = tryModel.get();
            if ( AspectModelResolver.containsDefinition( model, aspectModelUrn ) ) {
               return Try.success( model );
            } else {
               LOG.debug( "File {} does not contain {}", file, aspectModelUrn );
            }
         }
      }
      return Try.failure(
            new FileNotFoundException( "No model file containing " + aspectModelUrn + " could be found in directory: " + directory ) );
   }

   @Override
   public String toString() {
      return "FileSystemStrategy(root=" + modelsRoot + ')';
   }
}
