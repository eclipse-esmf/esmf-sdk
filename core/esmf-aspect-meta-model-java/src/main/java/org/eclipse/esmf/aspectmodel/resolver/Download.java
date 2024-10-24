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

package org.eclipse.esmf.aspectmodel.resolver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience class to download a file via HTTP, which the ability to auto-detect and use proxy settings
 */
public class Download {
   private static final Logger LOG = LoggerFactory.getLogger( Download.class );
   private final ProxyConfig proxyConfig;

   public Download( final ProxyConfig proxyConfig ) {
      this.proxyConfig = proxyConfig;
   }

   public Download() {
      this( ProxyConfig.detectProxySettings() );
   }

   /**
    * Download the file and return the contents as byte array
    *
    * @param fileUrl the URL
    * @return the file contents
    */
   public byte[] downloadFile( final URL fileUrl ) {
      try {
         final HttpClient.Builder clientBuilder = HttpClient.newBuilder()
               .version( HttpClient.Version.HTTP_1_1 )
               .followRedirects( HttpClient.Redirect.ALWAYS )
               .connectTimeout( Duration.ofSeconds( 10 ) );
         Optional.ofNullable( proxyConfig.proxy() ).ifPresent( clientBuilder::proxy );
         Optional.ofNullable( proxyConfig.authenticator() ).ifPresent( clientBuilder::authenticator );
         final HttpClient client = clientBuilder.build();
         final HttpRequest request = HttpRequest.newBuilder().uri( fileUrl.toURI() ).build();
         final HttpResponse<byte[]> response = client.send( request, HttpResponse.BodyHandlers.ofByteArray() );
         return response.body();
      } catch ( final InterruptedException | URISyntaxException | IOException exception ) {
         throw new ModelResolutionException( "Could not retrieve " + fileUrl, exception );
      }
   }

   /**
    * Download the file and write it to the file system
    *
    * @param fileUrl the URL
    * @param outputFile the output file to write
    * @return the file written
    */
   public File downloadFile( final URL fileUrl, final File outputFile ) {
      try ( final FileOutputStream outputStream = new FileOutputStream( outputFile ) ) {
         final byte[] fileContent = downloadFile( fileUrl );
         outputStream.write( fileContent );
      } catch ( final IOException exception ) {
         throw new ModelResolutionException( "Could not write file " + outputFile, exception );
      }

      LOG.info( "Downloaded {} to local file {}", fileUrl.getPath(), outputFile );
      return outputFile;
   }
}
