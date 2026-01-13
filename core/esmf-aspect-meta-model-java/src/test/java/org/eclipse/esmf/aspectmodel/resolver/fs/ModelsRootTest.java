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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class ModelsRootTest {

   @Test
   void resolveByCanonicalPathShouldReturnFileWhenCanonicalPathMatches() throws Exception {
      Path testPath = Paths.get( "src/test/resources/resolve", "Aspect.ttl" ).toAbsolutePath();

      Optional<File> result = invokeResolveByCanonicalPath( testPath );

      assertThat( result )
            .isPresent()
            .hasValueSatisfying( file -> {
               assertThat( file ).exists();
               assertThat( file ).isEqualTo( testPath.toFile() );
            } );
   }

   @Test
   void resolveByCanonicalPathShouldReturnFileWhenCanonicalPathMatchesForSpecificPath() throws Exception {
      Path testPath = Paths.get( "src/test/resources/../resources/resolve", "Aspect.ttl" ).toAbsolutePath();

      Optional<File> result = invokeResolveByCanonicalPath( testPath );


      assertThat( result )
            .isPresent()
            .hasValueSatisfying( file -> {
               assertThat( file ).exists();
               assertThat( file ).isEqualTo( testPath.toFile() );
            } );
   }

   @Test
   void resolveByCanonicalPathShouldReturnEmptyFileWhenCanonicalPathDoesNotMatch() throws Exception {
      Path invalidPath = Paths.get( "src/test/resources/resolve", "aspect.ttl" ).toAbsolutePath();

      Optional<File> result = invokeResolveByCanonicalPath( invalidPath );

      assertThat( result ).isNotPresent();
   }

   private static Optional<File> invokeResolveByCanonicalPath( Path path ) throws Exception {
      Method method = ModelsRoot.class.getDeclaredMethod( "resolveByCanonicalPath", Path.class );
      method.setAccessible( true );
      return (Optional<File>) method.invoke( null, path );
   }
}