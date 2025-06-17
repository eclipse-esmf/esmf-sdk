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
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

/**
 * A models root directory that assumes all model files are in the same directory.
 */
public class FlatModelsRoot extends ModelsRoot {
   public FlatModelsRoot( final Path root ) {
      super( root );
   }

   @Override
   public Path directoryForNamespace( final AspectModelUrn urn ) {
      return rootPath();
   }

   @Override
   public String toString() {
      return "FlatModelsRoot(rootPath=" + rootPath() + ")";
   }

   @Override
   public Stream<URI> contents() {
      return Arrays.stream( Optional.ofNullable( rootPath().toFile().listFiles() ).orElse( new File[] {} ) )
            .filter( file -> file.getName().endsWith( ".ttl" ) )
            .sorted( Comparator.comparing( File::getName ) )
            .map( File::toURI );
   }

   @Override
   public Stream<URI> namespaceContents( final AspectModelUrn namespace ) {
      return paths()
            .filter( path -> {
               try ( final Stream<String> lines = Files.lines( path ) ) {
                  return lines.takeWhile( line -> line.startsWith( "#" ) || line.startsWith( "@prefix" ) )
                        .anyMatch( line -> line.startsWith( "@prefix : " ) && line.endsWith( "<" + namespace.getUrnPrefix() + "> ." ) );
               } catch ( final IOException e ) {
                  return false;
               }
            } )
            .map( Path::toUri );
   }
}
