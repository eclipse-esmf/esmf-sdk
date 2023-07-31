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

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Try;

/**
 * Resolution strategy for Aspect model URNs that finds Aspect model files in the local file system.
 */
public class FileSystemStrategy extends AbstractResolutionStrategy {
   private static final Logger LOG = LoggerFactory.getLogger( FileSystemStrategy.class );
   protected final Path modelsRoot;

   /**
    * Initialize the FileSystemStrategy with the root path of models. The directory
    * is assumed to contain a file system hierarchy as follows: <code>N/V/X.ttl</code> where N is the namespace,
    * V is the version of the namespace and X is the name of the model element (Aspect, Characteristic, ...).
    * Example:
    * <pre>
    * {@code
    * models   <-- must be configured as modelsRoot
    * └── com.example
    *     ├── 1.0.0
    *     │   ├── ExampleAspect.ttl
    *     │   ├── exampleProperty.ttl
    *     │   └── ExampleCharacteristic.ttl
    *     └── 1.1.0
    *         └── ...
    * }
    * </pre>
    *
    * @param modelsRoot The root directory for model files
    */
   public FileSystemStrategy( final Path modelsRoot ) {
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
      final Path directory = resolve( aspectModelUrn );
      return loadFromDirectory( aspectModelUrn, directory );
   }

   protected Try<Model> loadFromDirectory( final AspectModelUrn aspectModelUrn, final Path directory ) {
      final File namedResourceFile = directory.resolve( aspectModelUrn.getName() + ".ttl" ).toFile();
      if ( namedResourceFile.exists() ) {
         return loadFromUri( namedResourceFile.toURI() );
      }

      LOG.warn( "Looking for {}, but no {}.ttl was found. Inspecting files in {}", aspectModelUrn.getName(),
            aspectModelUrn.getName(), directory );

      return Arrays.stream( Optional.ofNullable( directory.toFile().listFiles() ).orElse( new File[] {} ) )
            .filter( File::isFile )
            .filter( file -> file.getName().endsWith( ".ttl" ) )
            .map( File::toURI )
            .sorted()
            .map( this::loadFromUri )
            .filter( tryModel -> tryModel
                  .map( model -> AspectModelResolver.containsDefinition( model, aspectModelUrn ) )
                  .getOrElse( false ) )
            .findFirst()
            .orElse( Try.failure( new FileNotFoundException(
                  "No model file containing " + aspectModelUrn + " could be found in directory: " + directory ) ) );
   }

   protected Path resolve( final AspectModelUrn aspectModelUrn ) {
      return modelsRoot.resolve( aspectModelUrn.getNamespace() ).resolve( aspectModelUrn.getVersion() );
   }

   @Override
   public String toString() {
      return "FileSystemStrategy(root=" + modelsRoot + ')';
   }

   /**
    * Initialize the File System Strategy where the default namespace is resolved in the root folder.
    */
   public static class DefaultNamespace extends FileSystemStrategy {
      AspectModelUrn defaultUrn;

      public DefaultNamespace( final Path modelsRoot ) {
         super( modelsRoot );
      }

      @Override
      protected Path resolve( final AspectModelUrn urn ) {
         return (urn.getNamespace().equals( defaultUrn.getNamespace() ) && urn.getVersion().equals( defaultUrn.getVersion() ))
               ? modelsRoot
               : super.resolve( urn );
      }

      @Override
      public Try<Model> apply( final AspectModelUrn urn ) {
         final Path directory = resolve( urn );
         return loadFromDirectory( urn, directory )
               .recoverWith( FileNotFoundException.class,
                     ex -> loadFromDirectory( urn, super.resolve( urn ) )
                           .mapFailure( Case( $( instanceOf( FileNotFoundException.class ) ),
                                 e -> new FileNotFoundException( ex.getMessage() + ". AND " + e.getMessage() ) ) )
               );

      }

      public static <T extends ResolutionStrategy> T withDefaultNamespace( final T strategy, final Model model ) {
         if ( strategy instanceof DefaultNamespace ) {
            Optional.ofNullable( model.getNsPrefixURI( "" ) ).ifPresent( ns ->
                  ((DefaultNamespace) strategy).defaultUrn = AspectModelUrn.fromUrn( ns + "DefaultPath" )
            );
         }

         return strategy;
      }

      @Override
      public String toString() {
         return super.toString() + ".DefaultNamespace(ns=" + defaultUrn + ')';
      }
   }
}
