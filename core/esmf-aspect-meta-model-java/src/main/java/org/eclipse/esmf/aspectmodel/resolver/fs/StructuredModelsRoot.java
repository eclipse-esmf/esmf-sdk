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
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.VersionNumber;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

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
   public StructuredModelsRoot( final Path path ) {
      super( path );
   }

   @Override
   public Path directoryForNamespace( final AspectModelUrn urn ) {
      return rootPath().resolve( urn.getNamespaceMainPart() ).resolve( urn.getVersion() );
   }

   @Override
   public String toString() {
      return "StructuredModelsRoot(rootPath=" + rootPath() + ")";
   }

   @Override
   public Stream<URI> contents() {
      try {
         try ( final Stream<Path> path = Files.walk( rootPath(), 3 ) ) {
            return path.map( Path::toFile )
                  .filter( file -> file.getName().endsWith( ".ttl" ) )
                  .filter( file -> VersionNumber.tryParse( file.getParentFile().getName() )
                        .flatMap( versionNumber -> AspectModelUrn.from( String.format( "urn:samm:%s:%s",
                              file.getParentFile().getParentFile().getName(), versionNumber ) ) ).isSuccess() )
                  .sorted( Comparator.comparing( File::getName ) )
                  .map( File::getAbsoluteFile )
                  .map( File::toURI )
                  .toList()
                  .stream();
         }
      } catch ( final IOException exception ) {
         throw new ModelResolutionException( "Could not list contents of " + rootPath(), exception );
      }
   }

   @Override
   public Stream<URI> namespaceContents( final AspectModelUrn namespace ) {
      final File namespaceDirectory = rootPath()
            .resolve( namespace.getNamespaceMainPart() )
            .resolve( namespace.getVersion() )
            .toFile();
      return Arrays.stream( Objects.requireNonNull( namespaceDirectory.listFiles( file ->
                  file.getName().endsWith( ".ttl" ) ) ) )
            .map( File::toURI );
   }
}
