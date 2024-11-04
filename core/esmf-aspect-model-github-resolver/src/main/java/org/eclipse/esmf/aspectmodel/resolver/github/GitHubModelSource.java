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
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.resolver.Download;
import org.eclipse.esmf.aspectmodel.resolver.GithubRepository;
import org.eclipse.esmf.aspectmodel.resolver.ModelSource;
import org.eclipse.esmf.aspectmodel.resolver.ProxyConfig;
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
   private final ProxyConfig proxyConfig;
   File repositoryZipFile = null;
   private List<AspectModelFile> files = null;
   protected final GithubRepository repository;
   protected final String directory;

   /**
    * Constructor.
    *
    * @param repository the repository this model sources refers to
    * @param directory the relative directory inside the repository
    * @param proxyConfig the proxy configuration
    */
   public GitHubModelSource( final GithubRepository repository, final String directory, final ProxyConfig proxyConfig ) {
      this.repository = repository;
      this.directory = Optional.ofNullable( directory ).map( d ->
            d.endsWith( "/" ) ? d.substring( 0, d.length() - 1 ) : d ).orElse( "" );
      this.proxyConfig = proxyConfig;
   }

   /**
    * Constructor. Proxy settings are automatically detected.
    *
    * @param repository the repository this model sources refers to
    * @param directory the relative directory inside the repository
    */
   public GitHubModelSource( final GithubRepository repository, final String directory ) {
      this( repository, directory, ProxyConfig.detectProxySettings() );
   }

   private String sourceUrl( final String filename ) {
      return "https://%s/%s/%s/blob/%s/%s".formatted( repository.host(), repository.owner(), repository.repository(),
            repository.branchOrTag().name(), filename );
   }

   private void init() {
      try {
         final Path tempDirectory = Files.createTempDirectory( "esmf" );
         final File outputZipFile = tempDirectory.resolve( ZonedDateTime.now( ZoneId.systemDefault() )
               .format( DateTimeFormatter.ofPattern( "uuuu-MM-dd.HH.mm.ss" ) ) + ".zip" ).toFile();
         repositoryZipFile = new Download( proxyConfig ).downloadFile( repository.zipLocation(), outputZipFile );
         loadFilesFromZip();
         final boolean packageIsDeleted = outputZipFile.delete() && tempDirectory.toFile().delete();
         if ( packageIsDeleted ) {
            LOG.debug( String.format( "Temporary package file %s was deleted", outputZipFile.getName() ) );
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
            final String pathPrefix = repository.repository() + "-" + repository.branchOrTag().name() + "/" + directory;
            if ( !zipEntry.getName().startsWith( pathPrefix ) || !zipEntry.getName().endsWith( ".ttl" ) ) {
               return Stream.empty();
            }
            final int offset = pathPrefix.endsWith( "/" ) ? 0 : 1;
            final String path = zipEntry.getName().substring( pathPrefix.length() + offset );
            // Path should now look like org.eclipse.esmf.example/1.0.0/File.ttl
            final String[] parts = path.split( "/" );
            if ( parts.length != 3 || AspectModelUrn.from( "urn:samm:" + parts[0] + ":" + parts[1] ).isFailure() ) {
               LOG.debug( "Tried to load file {} but the path contains no valid URN structure", zipEntry.getName() );
               return Stream.<AspectModelFile> empty();
            }
            final String relativeFilePath = zipEntry.getName().substring( zipEntry.getName().indexOf( "/" ) + 1 );
            return Try.of( () -> zipFile.getInputStream( zipEntry ) ).toJavaStream().map( inputStream ->
                  AspectModelFileLoader.load( inputStream, Optional.of( URI.create( sourceUrl( relativeFilePath ) ) ) ) );
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
