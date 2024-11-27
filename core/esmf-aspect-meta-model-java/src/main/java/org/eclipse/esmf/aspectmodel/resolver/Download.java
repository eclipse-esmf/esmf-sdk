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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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
    * Download the file and return the HTTP response
    *
    * @param fileUrl the URL
    * @param headers list of additional headers to set
    * @return the response
    */
   public byte[] downloadFileContent( final URL fileUrl, final Map<String, String> headers ) {
      return downloadFileAsResponse( fileUrl, headers ).body();
   }

   /**
    * Download the file and return the contents as byte array
    *
    * @param fileUrl the URL
    * @param headers list of additional headers to set
    * @return the file contents
    */
   public HttpResponse<byte[]> downloadFileAsResponse( final URL fileUrl, final Map<String, String> headers ) {
      try {
         final HttpClient.Builder clientBuilder = HttpClient.newBuilder()
               .version( HttpClient.Version.HTTP_1_1 )
               .followRedirects( HttpClient.Redirect.ALWAYS )
               .connectTimeout( Duration.ofSeconds( 10 ) );
         Optional.ofNullable( proxyConfig.proxy() ).ifPresent( clientBuilder::proxy );
         Optional.ofNullable( proxyConfig.authenticator() ).ifPresent( clientBuilder::authenticator );
         final HttpClient client = clientBuilder.build();
         final String[] headersArray = headers.entrySet().stream()
               .flatMap( entry -> Stream.of( entry.getKey(), entry.getValue() ) )
               .toList()
               .toArray( new String[0] );
         final HttpRequest request = HttpRequest.newBuilder()
               .uri( fileUrl.toURI() )
               .headers( headersArray )
               .build();
         return client.send( request, HttpResponse.BodyHandlers.ofByteArray() );
      } catch ( final InterruptedException | URISyntaxException | IOException exception ) {
         throw new ModelResolutionException( "Could not retrieve " + fileUrl, exception );
      }
   }

   /**
    * Download the file and return the contents as byte array
    *
    * @param fileUrl the URL
    * @return the file contents
    */
   public byte[] downloadFile( final URL fileUrl ) {
      return downloadFileContent( fileUrl, Map.of() );
   }

   /**
    * Download the file and write it to the file system
    *
    * @param fileUrl the URL
    * @param outputFile the output file to write
    * @return the file written
    */
   public File downloadFile( final URL fileUrl, final File outputFile ) {
      return downloadFile( fileUrl, Map.of(), outputFile );
   }

   public File downloadFile( final URL fileUrl, final Map<String, String> headers, final File outputFile ) {
      try ( final FileOutputStream outputStream = new FileOutputStream( outputFile ) ) {
         final HttpResponse<byte[]> httpResponse = downloadFileAsResponse( fileUrl, headers );
         if ( httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300 ) {
            outputStream.write( httpResponse.body() );
         } else {
            throw new ModelResolutionException( "Could not download file (status code: " + httpResponse.statusCode() + ")" );
         }
      } catch ( final IOException exception ) {
         throw new ModelResolutionException( "Could not write file " + outputFile, exception );
      }

      LOG.info( "Downloaded {} to local file {}", fileUrl.getPath(), outputFile );
      return outputFile;
   }
}
