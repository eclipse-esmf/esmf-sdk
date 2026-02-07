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

package org.eclipse.esmf.aspectmodel.resolver.fs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class ModelsRoot {

   private static final Logger LOG = LoggerFactory.getLogger( ModelsRoot.class );
   private final Path root;

   protected ModelsRoot( final Path root ) {
      this.root = root;
   }

   public Path rootPath() {
      return root;
   }

   public Stream<Path> paths() {
      return contents().map( Paths::get );
   }

   public abstract Stream<URI> contents();

   public abstract Stream<URI> namespaceContents( AspectModelUrn namespace );

   public abstract Path directoryForNamespace( final AspectModelUrn urn );

   public File determineAspectModelFile( final AspectModelUrn urn ) {
      return constructAspectModelFilePath( urn ).toFile();
   }

   /**
    * Resolve the aspect model file for the given {@link AspectModelUrn}.
    *
    * <p>Constructs the file path by resolving the namespace directory.
    * Validates the file using its canonical path.
    *
    * <p>Returns Optional.empty() if the resolution fails or file does not exist.
    *
    * @param urn the {@link AspectModelUrn} representing the aspect model.
    * @return Optional of the resolved {@link File}, or Optional.empty() if resolution fails.
    */
   public Optional<File> resolveAspectModelFile( final AspectModelUrn urn ) {
      Path path = constructAspectModelFilePath( urn );
      return resolveByCanonicalPath( path );
   }

   private Path constructAspectModelFilePath( final AspectModelUrn urn ) {
      return directoryForNamespace( urn ).resolve( urn.getName() + ".ttl" );
   }

   private static Optional<File> resolveByCanonicalPath( final Path path ) {
      File file = path.toFile();
      try {
         if ( file.exists() && Objects.equals( path.normalize().toString(), file.getCanonicalPath() ) ) {
            return Optional.of( file );
         }
      } catch ( IOException exception ) {
         LOG.error( "Error resolving canonical path for file: {}", file.getPath(), exception );
      }
      throw new ModelResolutionException( "Resolving path failed for file " + file.getPath() );
   }
}
