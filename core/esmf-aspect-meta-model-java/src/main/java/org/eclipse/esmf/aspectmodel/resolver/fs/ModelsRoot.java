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
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class ModelsRoot {

   private static final Logger LOG = LoggerFactory.getLogger( ModelsRoot.class );
   private static final File EMPTY_FILE = new File( "" );
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

   /**
    * Determines the aspect model file for the given {@link AspectModelUrn}.
    *
    * <p>Constructs the file path by resolving the namespace directory.
    * Validates the file using its canonical path.
    *
    * <p>Returns an empty file if the resolution fails.
    *
    * @param urn the {@link AspectModelUrn} representing the aspect model.
    * @return the resolved {@link File}, or an empty file if resolution fails.
    */
   public File determineAspectModelFile( final AspectModelUrn urn ) {
      Path path = directoryForNamespace( urn ).resolve( urn.getName() + ".ttl" );
      return resolveByCanonicalPath( path );
   }

   private static File resolveByCanonicalPath( final Path path ) {
      File file = path.toFile();
      try {
         if ( file.exists() && Objects.equals( path.normalize().toString(), file.getCanonicalPath() ) ) {
            return file;
         }
      } catch ( IOException exception ) {
         LOG.error( "Error resolving canonical path for file: {}", file.getPath(), exception );
      }
      return EMPTY_FILE;
   }
}
