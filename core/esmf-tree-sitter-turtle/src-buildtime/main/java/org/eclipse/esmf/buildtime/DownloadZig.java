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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.eclipse.esmf.util.download.Download;
import org.eclipse.esmf.util.download.ProxyConfig;
import org.eclipse.esmf.util.process.BinaryLauncher;
import org.eclipse.esmf.util.process.ProcessLauncher;

public class DownloadZig extends ZigContext {
   private static final String ZIG_PUB_KEY = "RWSGOq2NVecA2UPNdBUZykf1CCb147pkmdtYxgb3Ti+JO/wCYvhbAb/U";
   private static final URL MIRRORS_LIST_URL = url( "https://ziglang.org/download/community-mirrors.txt" );

   private final ProxyConfig proxyConfig;
   private final String minisignVersion;

   public DownloadZig( final Path cacheLocation, final String zigVersion, final String minisignVersion ) {
      super( cacheLocation, zigVersion );
      proxyConfig = ProxyConfig.detectProxySettings();
      this.minisignVersion = minisignVersion;
   }

   private File getOrDownloadFile( final URL location ) {
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

   private String zigReleaseFileName() {
      return switch ( currentOs ) {
         case WINDOWS -> "zig-x86_64-windows-%s.zip".formatted( zigVersion );
         case MACOS, LINUX -> "zig-%s-%s-%s.tar.xz".formatted( currentArchitecture, currentOs, zigVersion );
      };
   }

   private String minisignReleaseFileName() {
      return switch ( currentOs ) {
         case WINDOWS -> "minisign-%s-win64.zip".formatted( minisignVersion );
         case MACOS -> "minisign-%s-macos.zip".formatted( minisignVersion );
         case LINUX -> "minisign-%s-linux.tar.gz".formatted( minisignVersion );
      };
   }

   private Path minisignExecutablePath() {
      return switch ( currentOs ) {
         case WINDOWS -> Path.of( "minisign-win64", "minisign.exe" );
         case MACOS -> Path.of( "minisign" );
         case LINUX -> Path.of( "minisign-linux", currentArchitecture.toString(), "minisign" );
      };
   }

   private List<String> zigMirrorUrls() {
      final File mirrors = getOrDownloadFile( MIRRORS_LIST_URL );
      try {
         return Files.readAllLines( mirrors.toPath() ).stream().collect( Collectors.collectingAndThen( Collectors.toList(), collected -> {
            Collections.shuffle( collected );
            return collected;
         } ) );
      } catch ( final IOException exception ) {
         throw new BuildTimeException( exception );
      }
   }

   private File getOrDownloadFileFromMirror( final List<String> mirrorUrls, final File outputFile ) {
      if ( outputFile.exists() ) {
         return outputFile;
      }
      final List<String> triedLocations = new ArrayList<>();
      for ( final Iterator<String> it = mirrorUrls.iterator(); it.hasNext() && !outputFile.exists(); ) {
         final String mirrorUrl = it.next();
         try {
            final URL url = url( mirrorUrl + "/" + outputFile.getName() );
            triedLocations.add( url.toString() );
            return getOrDownloadFile( url );
         } catch ( final Exception exception ) {
            // try the next mirror
         }
      }
      throw new BuildTimeException( "Could not retrieve " + outputFile.getName() + " from any known mirror. Tried:"
            + triedLocations.stream().map( url -> " - " + url )
                  .collect( Collectors.joining( "\n" ) ) );
   }

   private static URL url( final String url ) {
      try {
         return URI.create( url ).toURL();
      } catch ( final MalformedURLException exception ) {
         throw new BuildTimeException( exception );
      }
   }

   private void extractZip( final File zipFile, final Path outputDir ) {
      if ( outputDir.toFile().exists() ) {
         return;
      } else {
         mkdir( outputDir );
      }

      try ( final ZipInputStream zipInputStream = new ZipInputStream( new FileInputStream( zipFile ) ) ) {
         for ( ZipEntry entry = zipInputStream.getNextEntry(); entry != null; entry = zipInputStream.getNextEntry() ) {
            final Path output = outputDir.resolve( entry.getName() );
            if ( entry.isDirectory() ) {
               mkdir( output );
            } else {
               try ( final FileOutputStream outputStream = new FileOutputStream( output.toFile() ) ) {
                  IOUtils.copy( zipInputStream, outputStream );
               }
            }
         }
      } catch ( final IOException exception ) {
         throw new BuildTimeException( exception );
      }
   }

   private void extractTar( final File tarFile, final Path outputDir ) {
      if ( outputDir.toFile().exists() ) {
         return;
      } else {
         mkdir( outputDir );
      }

      final String name = tarFile.getName().toLowerCase();
      if ( !name.endsWith( ".tar.gz" ) && !name.endsWith( ".tar.xz" ) && !name.endsWith( ".tgz" ) ) {
         throw new BuildTimeException( "Only .tar.gz, .tgz, and .tar.xz files are supported" );
      }

      try ( final FileInputStream fileInputStream = new FileInputStream( tarFile ) ) {
         final InputStream compressorInputStream = name.endsWith( ".tar.xz" )
               ? new XZCompressorInputStream( fileInputStream )
               : new GzipCompressorInputStream( fileInputStream );
         try ( final TarArchiveInputStream tarInputStream = new TarArchiveInputStream( compressorInputStream ) ) {
            for ( TarArchiveEntry entry = tarInputStream.getNextEntry(); entry != null; entry = tarInputStream.getNextEntry() ) {
               final Path output = outputDir.resolve( entry.getName() );
               if ( entry.isDirectory() ) {
                  mkdir( output );
               } else {
                  try ( final FileOutputStream outputStream = new FileOutputStream( output.toFile() ) ) {
                     IOUtils.copy( tarInputStream, outputStream );
                  }
               }
            }
         }

      } catch ( final IOException exception ) {
         throw new BuildTimeException( exception );
      }
   }

   private void extractArchive( final File archiveFile, final Path targetDirectory ) {
      if ( archiveFile.getName().endsWith( ".zip" ) ) {
         extractZip( archiveFile, targetDirectory );
      } else if ( archiveFile.getName().endsWith( ".tar.xz" ) || archiveFile.getName().endsWith( ".tar.gz" )
            || archiveFile.getName().endsWith( ".tgz" ) ) {
         extractTar( archiveFile, targetDirectory );
      } else {
         throw new BuildTimeException( "Do not know how to extract archive: " + archiveFile );
      }
   }

   private File downloadAndExtractMinisign() {
      final Path minisignDir = cacheLocation.resolve( "minisign" );
      final File minisignExe = minisignDir.resolve( minisignExecutablePath() ).toFile();
      if ( minisignExe.exists() ) {
         return minisignExe;
      }
      final File minisignArchive = getOrDownloadFile( url( "https://github.com/jedisct1/minisign/releases/download/" + minisignVersion
            + "/" + minisignReleaseFileName() ) );
      extractArchive( minisignArchive, minisignDir );
      if ( currentOs == OperatingSystem.LINUX || currentOs == OperatingSystem.MACOS ) {
         minisignExe.setExecutable( true );
      }
      try {
         FileUtils.delete( minisignArchive );
      } catch ( final IOException exception ) {
         // ignore, since it's only the cache
      }
      return minisignExe;
   }

   private File downloadAndExtractZig( final File minisignExe ) {
      final File zigExe = zigExe();
      if ( zigExe.exists() ) {
         return zigExe;
      }

      final List<String> mirrors = zigMirrorUrls();
      final String fileName = zigReleaseFileName();
      final File signatureOutputFile = cacheLocation.resolve( fileName + ".minisig" ).toFile();
      final File zigReleaseArchive = getOrDownloadFileFromMirror( mirrors, cacheLocation.resolve( fileName ).toFile() );
      getOrDownloadFileFromMirror( mirrors, signatureOutputFile );
      validateZigReleaseSignature( minisignExe, zigReleaseArchive );

      if ( !zigDir().toFile().exists() ) {
         extractArchive( zigReleaseArchive, zigDir() );
         if ( currentOs == OperatingSystem.LINUX || currentOs == OperatingSystem.MACOS ) {
            zigExe.setExecutable( true );
         }
      }
      try {
         FileUtils.delete( zigReleaseArchive );
         FileUtils.delete( signatureOutputFile );
      } catch ( final IOException exception ) {
         // ignore, since it's only the cache
      }
      return zigExe;
   }

   private void validateZigReleaseSignature( final File minisignExe, final File zigReleaseArchive ) {
      final ProcessLauncher.ExecutionResult minisignExecutionResult = new BinaryLauncher( minisignExe ).apply(
            List.of( "-qVm", zigReleaseArchive.getAbsolutePath(), "-P", ZIG_PUB_KEY ), Optional.empty(),
            zigReleaseArchive.getParentFile() );
      if ( minisignExecutionResult.exitStatus() != 0 ) {
         throw new BuildTimeException( "Signature of " + zigReleaseArchive + " does not match" );
      }
   }

   static void main( final String[] args ) {
      final Path cacheLocation = Path.of( args[0] );
      final String zigVersion = args[1];
      final String minisignVersion = args[2];

      final DownloadZig downloadZig = new DownloadZig( cacheLocation, zigVersion, minisignVersion );
      final File minisignExe = downloadZig.downloadAndExtractMinisign();
      downloadZig.downloadAndExtractZig( minisignExe );
   }
}
