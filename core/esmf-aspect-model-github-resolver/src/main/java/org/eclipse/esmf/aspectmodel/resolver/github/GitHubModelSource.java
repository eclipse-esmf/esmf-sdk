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

package org.eclipse.esmf.aspectmodel.resolver.github;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.resolver.Download;
import org.eclipse.esmf.aspectmodel.resolver.GithubRepository;
import org.eclipse.esmf.aspectmodel.resolver.ModelSource;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import com.google.common.collect.Streams;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A model source for remote GitHub repositories
 */
public class GitHubModelSource implements ModelSource {
   private static final Logger LOG = LoggerFactory.getLogger( GitHubModelSource.class );
   File repositoryZipFile = null;
   private List<AspectModelFile> files = null;
   GithubModelSourceConfig config;

   public GitHubModelSource( final GithubModelSourceConfig config ) {
      this.config = config;
   }

   /**
    * Convenience constructor for public repositories. Proxy settings are automatically detected, no authentication is used.
    *
    * @param repository the repository this model sources refers to
    * @param directory the relative directory inside the repository
    */
   public GitHubModelSource( final GithubRepository repository, final String directory ) {
      this( GithubModelSourceConfigBuilder.builder()
            .repository( repository )
            .directory( directory )
            .build() );
   }

   private String sourceUrl( final String filename ) {
      return "https://%s/%s/%s/blob/%s/%s".formatted(
            config.repository().host().equals( "api.github.com" ) ? "github.com" : config.repository().host(),
            config.repository().owner(),
            config.repository().repository(),
            config.repository().branchOrTag().name(),
            filename );
   }

   private void init() {
      try {
         final Path tempDirectory = Files.createTempDirectory( "esmf" );
         final File outputZipFile = tempDirectory.resolve( ZonedDateTime.now( ZoneId.systemDefault() )
               .format( DateTimeFormatter.ofPattern( "uuuu-MM-dd.HH.mm.ss" ) ) + ".zip" ).toFile();
         final Map<String, String> headers = new HashMap<>();
         headers.put( "Accept", "application/vnd.github+json" );
         headers.put( "X-GitHub-Api-Version", "2022-11-28" );
         if ( config.token() != null ) {
            headers.put( "Authorization", "Bearer " + config.token() );
         }
         repositoryZipFile = new Download( config.proxyConfig() )
               .downloadFile( config.repository().zipLocation(), headers, outputZipFile );
         loadFilesFromZip();
         final boolean packageIsDeleted = outputZipFile.delete() && tempDirectory.toFile().delete();
         if ( packageIsDeleted ) {
            LOG.debug( "Temporary package file {} was deleted", outputZipFile.getName() );
         }
      } catch ( final IOException exception ) {
         throw new GitHubResolverException( exception );
      }
   }

   /**
    * Loads the AspectModelFiles from the downloaded .zip
    */
   void loadFilesFromZip() {
      try ( final ZipFile zipFile = new ZipFile( repositoryZipFile ) ) {
         LOG.debug( "Loading Aspect Model files from {}", repositoryZipFile );
         files = Streams.stream( zipFile.entries().asIterator() ).flatMap( zipEntry -> {
            final String path = zipEntry.getName().substring( zipEntry.getName().indexOf( '/' ) + 1 );
            if ( !zipEntry.getName().endsWith( ".ttl" ) ) {
               return Stream.empty();
            }
            final String[] parts = path.split( "/" );
            if ( parts.length < 3 ||
                  AspectModelUrn.from( "urn:samm:" + parts[parts.length - 3] + ":" + parts[parts.length - 2] ).isFailure() ) {
               LOG.debug( "Tried to load file {} but the path contains no valid URN structure", zipEntry.getName() );
               return Stream.<AspectModelFile> empty();
            }
            final URI uri = URI.create( sourceUrl( path ) );
            final Try<RawAspectModelFile> file = Try.of( () -> zipFile.getInputStream( zipEntry ) )
                  .map( inputStream -> AspectModelFileLoader.load( inputStream, Optional.of( uri ) ) );
            if ( file.isFailure() ) {
               LOG.debug( "Tried to load {}, but it failed", uri );
            }
            return file.toJavaStream();
         } ).toList();
      } catch ( final IOException exception ) {
         throw new GitHubResolverException( exception );
      }
   }

   @Override
   public Stream<URI> listContents() {
      if ( files == null ) {
         init();
      }
      return files.stream().flatMap( file -> file.sourceLocation().stream() );
   }

   @Override
   public Stream<URI> listContentsForNamespace( final AspectModelUrn namespace ) {
      return listContents()
            .filter( uri -> uri.toString().contains( namespace.getNamespaceMainPart() + "/" + namespace.getVersion() + "/" ) );
   }

   @Override
   public Stream<AspectModelFile> loadContents() {
      if ( files == null ) {
         init();
      }
      return files.stream();
   }

   @Override
   public Stream<AspectModelFile> loadContentsForNamespace( final AspectModelUrn namespace ) {
      if ( files == null ) {
         init();
      }
      return files.stream()
            .filter( uri -> uri.toString().contains( namespace.getNamespaceMainPart() + "/" + namespace.getVersion() + "/" ) );
   }
}
