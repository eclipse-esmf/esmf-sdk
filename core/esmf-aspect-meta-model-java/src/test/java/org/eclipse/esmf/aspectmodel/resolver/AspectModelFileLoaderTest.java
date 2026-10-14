/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AspectModelFileLoaderTest {
   private static final String MODEL = "@prefix : <urn:samm:example:1.0.0#> .";

   @Test
   void loadingFileReleasesFileHandle( @TempDir final Path directory ) throws IOException {
      final Path modelFile = Files.writeString( directory.resolve( "Model.ttl" ), MODEL );

      AspectModelFileLoader.load( modelFile.toFile() );

      assertThat( Files.deleteIfExists( modelFile ) ).isTrue();
   }

   @Test
   void loadingUrlClosesStreamOpenedByLoader() throws Exception {
      final AtomicBoolean closed = new AtomicBoolean();
      final InputStream inputStream = trackingInputStream( closed );
      final URL url = URL.of( URI.create( "memory:Model.ttl" ), new URLStreamHandler() {
         @Override
         protected URLConnection openConnection( final URL url ) {
            return new URLConnection( url ) {
               @Override
               public void connect() {}

               @Override
               public InputStream getInputStream() {
                  return inputStream;
               }
            };
         }
      } );

      AspectModelFileLoader.load( url );

      assertThat( closed ).isTrue();
   }

   @Test
   void loadingCallerOwnedStreamDoesNotCloseIt() {
      final AtomicBoolean closed = new AtomicBoolean();
      final InputStream inputStream = trackingInputStream( closed );

      AspectModelFileLoader.load( inputStream, URI.create( "memory:Model.ttl" ) );

      assertThat( closed ).isFalse();
   }

   private static InputStream trackingInputStream( final AtomicBoolean closed ) {
      return new ByteArrayInputStream( MODEL.getBytes( StandardCharsets.UTF_8 ) ) {
         @Override
         public void close() throws IOException {
            closed.set( true );
            super.close();
         }
      };
   }
}
