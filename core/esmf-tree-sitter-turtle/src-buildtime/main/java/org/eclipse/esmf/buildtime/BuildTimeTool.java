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

package org.eclipse.esmf.buildtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

import org.eclipse.esmf.util.download.Download;
import org.eclipse.esmf.util.download.ProxyConfig;

import jakarta.xml.bind.annotation.adapters.HexBinaryAdapter;

public abstract class BuildTimeTool {
   protected final Path cacheLocation;
   protected final ProxyConfig proxyConfig;

   public BuildTimeTool( final Path cacheLocation ) {
      this.cacheLocation = cacheLocation;
      proxyConfig = ProxyConfig.detectProxySettings();
   }

   protected URL url( final String url ) {
      try {
         return URI.create( url ).toURL();
      } catch ( final MalformedURLException exception ) {
         throw new BuildTimeException( exception );
      }
   }

   protected File getOrDownloadFile( final URL location ) {
      final Path filePath = Paths.get( location.getPath() );
      final String filename = filePath.getName( filePath.getNameCount() - 1 ).toString();
      final File targetFile = cacheLocation.resolve( filename ).toFile();
      if ( targetFile.exists() ) {
         return targetFile;
      }

      final Download.Config config = new Download.Config( proxyConfig, Duration.ofSeconds( 3L ) );
      System.out.println( "Downloading " + location + " to " + targetFile.getName() + "..." );
      return new Download( config ).downloadFile( location, targetFile );
   }

   public String sha1( final File file ) {
      try ( final InputStream input = new FileInputStream( file );
            final DigestInputStream digestStream = new DigestInputStream( input, MessageDigest.getInstance( "SHA-1" ) ) ) {
         digestStream.readAllBytes();
         return new HexBinaryAdapter().marshal( digestStream.getMessageDigest().digest() ).toLowerCase();
      } catch ( final NoSuchAlgorithmException | IOException exception ) {
         throw new BuildTimeException( "Could not calculate SHA1 sum for " + file );
      }
   }
}
