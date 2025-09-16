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

import org.junit.jupiter.api.Test;

class ModelsRootTest {

   private static final File EMPTY_FILE = new File( "" );

   @Test
   void resolveByCanonicalPathShouldReturnFileWhenCanonicalPathMatches() throws Exception {
      Path testPath = Paths.get( "src/test/resources/resolve", "Aspect.ttl" ).toAbsolutePath();

      File result = invokeResolveByCanonicalPath( testPath );

      assertThat( result )
            .matches( File::exists )
            .isEqualTo( testPath.toFile() );
   }

   @Test
   void resolveByCanonicalPathShouldReturnFileWhenCanonicalPathMatchesForSpecificPath() throws Exception {
      Path testPath = Paths.get( "src/test/resources/../resources/resolve", "Aspect.ttl" ).toAbsolutePath();

      File result = invokeResolveByCanonicalPath( testPath );

      assertThat( result )
            .matches( File::exists )
            .isEqualTo( testPath.toFile() );
   }

   @Test
   void resolveByCanonicalPathShouldReturnEmptyFileWhenCanonicalPathDoesNotMatch() throws Exception {
      Path invalidPath = Paths.get( "src/test/resources/resolve", "aspect.ttl" ).toAbsolutePath();

      File result = invokeResolveByCanonicalPath( invalidPath );

      assertThat( result ).isEqualTo( EMPTY_FILE );
   }

   @Test
   void resolveByCanonicalPathShouldReturnEmptyFileWhenIoExceptionOccurs() throws Exception {
      Path pathCausingIOException = Paths.get( "pathCausingIOException" );

      File result = invokeResolveByCanonicalPath( pathCausingIOException );

      assertThat( result ).isEqualTo( EMPTY_FILE );
   }

   private static File invokeResolveByCanonicalPath( Path path ) throws Exception {
      Method method = ModelsRoot.class.getDeclaredMethod( "resolveByCanonicalPath", Path.class );
      method.setAccessible( true );
      return (File) method.invoke( null, path );
   }
}