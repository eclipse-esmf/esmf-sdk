/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel;

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.GithubRepository;
import org.eclipse.esmf.aspectmodel.resolver.ProxyConfig;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.github.GitHubStrategy;
import org.eclipse.esmf.aspectmodel.resolver.github.GithubModelSourceConfig;
import org.eclipse.esmf.aspectmodel.resolver.github.GithubModelSourceConfigBuilder;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.DetailedViolationFormatter;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationFormatter;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public abstract class AspectModelMojo extends AbstractMojo {
   protected static final ObjectMapper YAML_MAPPER = new YAMLMapper().enable( YAMLGenerator.Feature.MINIMIZE_QUOTES );
   protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

   @Parameter
   protected String modelsRootDirectory;

   @Parameter( required = true, property = "include" )
   protected Set<String> includes;

   @Parameter
   protected String outputDirectory = "";

   @Parameter
   protected String generatedSourcesDirectory = "";

   @Parameter( defaultValue = "false" )
   protected boolean detailedValidationMessages;

   @Parameter
   protected String githubServerId;

   @Parameter( defaultValue = "${session}", readonly = true )
   protected MavenSession mavenSession;

   protected GithubModelSourceConfig gitHubConfig;

   private Map<AspectModel, Aspect> aspects;

   public AspectModelMojo() {
   }

   protected AspectModelMojo( final Map<AspectModel, Aspect> aspects ) {
      this.aspects = aspects;
   }

   /**
    * Skip the execution.
    */
   @Parameter( name = "skip", property = "gen.skip", defaultValue = "false" )
   private Boolean skip;

   protected void validateParameters() throws MojoExecutionException {
      if ( includes == null || includes.isEmpty() ) {
         throw new MojoExecutionException( "Missing configuration. Please provide Aspect Models to be included." );
      }
   }

   protected Set<Aspect> loadAspects() throws MojoExecutionException {
      return new HashSet<>( loadAspectModels().values() );
   }

   protected Set<AspectModel> loadModels() throws MojoExecutionException {
      return new HashSet<>( loadAspectModels().keySet() );
   }

   private Map<AspectModel, Aspect> loadAspectModels() throws MojoExecutionException {
      if ( aspects != null ) {
         return aspects;
      }
      final List<ResolutionStrategy> strategies = new ArrayList<>();
      if ( modelsRootDirectory != null ) {
         final Path modelsRoot = Path.of( modelsRootDirectory );
         strategies.add( new FileSystemStrategy( modelsRoot ) );
      }
      if ( gitHubConfig != null ) {
         strategies.add( new GitHubStrategy( gitHubConfig ) );
      }
      if ( strategies.isEmpty() ) {
         throw new MojoExecutionException(
               "Neither modelsRootDirectory nor gitHubServerId were configured, don't know how to resolve Aspect Models" );
      }

      aspects = new HashMap<>();

      final AspectModelLoader aspectModelLoader = new AspectModelLoader( strategies );
      for ( final String inputUrn : includes ) {
         final AspectModelUrn urn = AspectModelUrn.fromUrn( inputUrn );
         final Either<List<Violation>, AspectModel> loadingResult = new AspectModelValidator().loadModel( () ->
               aspectModelLoader.load( urn ) );
         if ( loadingResult.isLeft() ) {
            final List<Violation> violations = loadingResult.getLeft();
            final String errorMessage = detailedValidationMessages
                  ? new DetailedViolationFormatter().apply( violations )
                  : new ViolationFormatter().apply( violations );
            throw new MojoExecutionException( errorMessage );
         }
         final AspectModel aspectModel = loadingResult.get();
         final Aspect aspect = aspectModel.aspects().stream()
               .filter( theAspect -> theAspect.urn().equals( urn ) )
               .findFirst()
               .orElseThrow( () -> new MojoExecutionException( "Loaded Aspect Model does not contain Aspect " + urn ) );
         aspects.put( aspectModel, aspect );
      }
      return aspects;
   }

   protected File getOutFile( final String artifactName, final String outputDirectory ) throws MojoExecutionException {
      try {
         final Path outputPath = Path.of( outputDirectory );
         Files.createDirectories( outputPath );
         return outputPath.resolve( artifactName ).toFile();
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not create missing directories for path " + outputDirectory );
      }
   }

   protected FileOutputStream getOutputStreamForFile( final String artifactName, final String outputDirectory )
         throws MojoExecutionException {
      try {
         return new FileOutputStream( getOutFile( artifactName, outputDirectory ) );
      } catch ( final IOException exception ) {
         throw new MojoExecutionException( "Could not write to output " + outputDirectory );
      }
   }

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      if ( Boolean.TRUE.equals( skip ) ) {
         getLog().info( "Generation is skipped." );
         return;
      }

      if ( githubServerId != null ) {
         if ( mavenSession == null ) {
            getLog().warn( "Could not read Maven session, ignoring GitHub server configuration." );
         } else {
            final Server server = mavenSession.getSettings().getServer( githubServerId );
            if ( server != null ) {
               final Xpp3Dom dom = (Xpp3Dom) server.getConfiguration();
               final String[] repositoryParts = Optional.ofNullable( dom.getChild( "repository" ) )
                     .map( Xpp3Dom::getValue )
                     .map( repository -> repository.split( "/" ) )
                     .orElseThrow( () -> new MojoExecutionException( "Expected <repository> in settings.xml is missing" ) );
               final String directory = Optional.ofNullable( dom.getChild( "directory" ) )
                     .map( Xpp3Dom::getValue )
                     .orElse( "" );
               final String token = Optional.ofNullable( dom.getChild( "token" ) )
                     .map( Xpp3Dom::getValue )
                     .orElse( null );

               final GithubRepository.Ref ref = Optional.ofNullable( dom.getChild( "branch" ) )
                     .map( Xpp3Dom::getValue )
                     .<GithubRepository.Ref> map( GithubRepository.Branch::new )
                     .or( () -> Optional.ofNullable( dom.getChild( "tag" ) )
                           .map( Xpp3Dom::getValue )
                           .map( GithubRepository.Tag::new ) )
                     .orElse( new GithubRepository.Branch( "main" ) );

               final List<Proxy> proxies = mavenSession.getSettings().getProxies();
               final ProxyConfig proxyConfig = Optional.ofNullable( proxies ).stream().flatMap( Collection::stream )
                     .filter( proxy -> proxy.getProtocol().equals( "https" ) )
                     .findFirst()
                     .or( () -> Optional.ofNullable( proxies ).stream().flatMap( Collection::stream )
                           .filter( proxy -> proxy.getProtocol().equals( "http" ) )
                           .findFirst() )
                     .filter( Proxy::isActive )
                     .map( proxy -> ProxyConfig.from( proxy.getHost(), proxy.getPort() ) )
                     .orElse( ProxyConfig.detectProxySettings() );

               final GithubRepository repository = new GithubRepository( repositoryParts[0], repositoryParts[1], ref );
               gitHubConfig = GithubModelSourceConfigBuilder.builder()
                     .proxyConfig( proxyConfig )
                     .repository( repository )
                     .directory( directory )
                     .token( token )
                     .build();
            }
         }
      }

      executeGeneration();
   }

   abstract void executeGeneration() throws MojoExecutionException, MojoFailureException;

   protected ObjectNode readFile( final String file ) throws MojoExecutionException {
      if ( StringUtils.isBlank( file ) ) {
         return null;
      }
      final String extension = FilenameUtils.getExtension( file ).toUpperCase();
      final Try<String> fileData = Try.of( () -> getFileAsString( file ) ).mapTry( Optional::get );
      return switch ( extension ) {
         case "YAML", "YML" -> (ObjectNode) fileData
               .mapTry( data -> YAML_MAPPER.readValue( data, Object.class ) )
               .mapTry( OBJECT_MAPPER::writeValueAsString )
               .mapTry( OBJECT_MAPPER::readTree )
               .get();
         case "JSON" -> (ObjectNode) fileData
               .mapTry( OBJECT_MAPPER::readTree )
               .get();
         default -> throw new MojoExecutionException( format( "File extension [%s] not supported.", extension ) );
      };
   }

   private static Optional<String> getFileAsString( final String filePath ) throws MojoExecutionException {
      if ( filePath == null || filePath.isEmpty() ) {
         return Optional.empty();
      }
      final File f = new File( filePath );
      if ( f.exists() && !f.isDirectory() ) {
         try ( final InputStream inputStream = new FileInputStream( filePath ) ) {
            return Optional.of( IOUtils.toString( inputStream, StandardCharsets.UTF_8 ) );
         } catch ( final IOException e ) {
            throw new MojoExecutionException( format( "Could not load file %s.", filePath ), e );
         }
      }
      throw new MojoExecutionException( format( "File does not exist %s.", filePath ) );
   }
}
