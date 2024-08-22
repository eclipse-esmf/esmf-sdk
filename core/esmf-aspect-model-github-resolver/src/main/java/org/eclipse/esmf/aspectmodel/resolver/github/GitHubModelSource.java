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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.resolver.ModelSource;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import com.google.common.collect.Streams;
import io.soabase.recordbuilder.core.RecordBuilder;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A model source for remote GitHub repositories
 */
public class GitHubModelSource implements ModelSource {
   private static final Logger LOG = LoggerFactory.getLogger( GitHubModelSource.class );
   private static final String GITHUB_BASE = "https://github.com";
   private static final String GITHUB_ZIP_URL = GITHUB_BASE + "/%s/%s/archive/refs/heads/%s.zip";
   private final Config config;
   File repositoryZipFile = null;
   private List<AspectModelFile> files = null;
   protected final String orgName;
   protected final String repositoryName;
   protected final String branchName;
   protected final String directory;

   /**
    * Constructor.
    *
    * @param repositoryName the repository name in the form 'org/reponame'
    * @param branchName the branch name
    * @param directory the directory in the repository in the form 'some/directory', empty for root
    * @param config sets proxy configuration
    */
   public GitHubModelSource( final String repositoryName, final String branchName, final String directory, final Config config ) {
      final String[] split = repositoryName.split( "/" );
      orgName = split[0];
      this.repositoryName = split[1];
      this.branchName = branchName;
      this.directory = directory.endsWith( "/" ) ? directory.substring( 0, directory.length() - 1 ) : directory;
      this.config = config;
   }

   /**
    * Constructor. Proxy settings are automatically detected.
    *
    * @param repositoryName the repository name in the form 'org/reponame'
    * @param branchName the branch name
    * @param directory the directory in the repository in the form 'some/directory', empty for root
    */
   public GitHubModelSource( final String repositoryName, final String branchName, final String directory ) {
      this( repositoryName, branchName, directory, detectProxySettings() );
   }

   private static Config detectProxySettings() {
      final String envProxy = System.getenv( "http_proxy" );
      if ( envProxy != null && System.getProperty( "http.proxyHost" ) == null ) {
         final Pattern proxyPattern = Pattern.compile( "http://([^:]*):(\\d+)/?" );
         final Matcher matcher = proxyPattern.matcher( envProxy );
         if ( matcher.matches() ) {
            final String host = matcher.group( 1 );
            final String port = matcher.group( 2 );
            System.setProperty( "http.proxyHost", host );
            System.setProperty( "http.proxyPort", port );
         } else {
            LOG.debug( "The value of the 'http_proxy' environment variable is malformed, ignoring: {}", envProxy );
         }
      }

      final String host = System.getProperty( "http.proxyHost" );
      final String port = System.getProperty( "http.proxyPort" );
      if ( host != null && port != null ) {
         return GitHubModelSourceConfigBuilder.builder()
               .proxy( ProxySelector.of( new InetSocketAddress( host, Integer.parseInt( port ) ) ) )
               .build();
      }
      return GitHubModelSourceConfigBuilder.builder().build();
   }

   @RecordBuilder
   public record Config(
         ProxySelector proxy,
         Authenticator authenticator
   ) {}

   private String sourceUrl( final String filename ) {
      return GITHUB_BASE + "/" + orgName + "/" + repositoryName + "/blob/" + branchName + "/" + filename;
   }

   private void init() {
      try {
         final Path tempDirectory = Files.createTempDirectory( "esmf" );
         final File outputZipFile = tempDirectory.resolve( ZonedDateTime.now( ZoneId.systemDefault() )
               .format( DateTimeFormatter.ofPattern( "uuuu-MM-dd.HH.mm.ss" ) ) + ".zip" ).toFile();
         repositoryZipFile = downloadFile( new URL( String.format( GITHUB_ZIP_URL, orgName, repositoryName, branchName ) ), outputZipFile );
         loadFilesFromZip();
         final boolean packageIsDeleted = outputZipFile.delete() && tempDirectory.toFile().delete();
         if ( packageIsDeleted ) {
            LOG.debug( String.format( "Temporary package file %s was deleted", outputZipFile.getName() ) );
         }
      } catch ( final IOException exception ) {
         throw new GitHubResolverException( exception );
      }
   }

   File downloadFile( final URL fileUrl, final File outputFile ) {
      try ( final FileOutputStream outputStream = new FileOutputStream( outputFile ) ) {
         final HttpClient.Builder clientBuilder = HttpClient.newBuilder()
               .version( HttpClient.Version.HTTP_1_1 )
               .followRedirects( HttpClient.Redirect.ALWAYS )
               .connectTimeout( Duration.ofSeconds( 10 ) );
         Optional.ofNullable( config.proxy() ).ifPresent( clientBuilder::proxy );
         Optional.ofNullable( config.authenticator() ).ifPresent( clientBuilder::authenticator );
         final HttpClient client = clientBuilder.build();
         final HttpRequest request = HttpRequest.newBuilder().uri( fileUrl.toURI() ).build();
         final HttpResponse<byte[]> response = client.send( request, HttpResponse.BodyHandlers.ofByteArray() );
         outputStream.write( response.body() );
      } catch ( final URISyntaxException | IOException | InterruptedException exception ) {
         throw new GitHubResolverException( "Could not write file " + outputFile, exception );
      }

      LOG.info( "Downloaded {} repository to local file {}", fileUrl.getPath(), outputFile );
      return outputFile;
   }

   /**
    * Loads the AspectModelFiles from the downloaded .zip
    */
   void loadFilesFromZip() {
      try ( final ZipFile zipFile = new ZipFile( repositoryZipFile ) ) {
         LOG.debug( "Loading Aspect Model files from {}", repositoryZipFile );
         files = Streams.stream( zipFile.entries().asIterator() ).flatMap( zipEntry -> {
            final String pathPrefix = repositoryName + "-" + branchName + "/" + directory;
            if ( !zipEntry.getName().startsWith( pathPrefix ) || !zipEntry.getName().endsWith( ".ttl" ) ) {
               return Stream.empty();
            }
            final String path = zipEntry.getName().substring( pathPrefix.length() + 1 );
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
