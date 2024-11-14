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

import java.io.FileOutputStream;
import java.io.IOException;
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

import io.vavr.control.Either;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public abstract class AspectModelMojo extends AbstractMojo {
   @Parameter( defaultValue = "${basedir}" )
   private String basedir;

   @Parameter
   private String modelsRootDirectory;

   @Parameter( required = true, property = "include" )
   protected Set<String> includes;

   @Parameter
   protected String outputDirectory = "";

   @Parameter( defaultValue = "false" )
   protected boolean detailedValidationMessages;

   @Parameter
   protected String githubServerId;

   @Parameter( defaultValue = "${session}", readonly = true )
   private MavenSession session;

   protected GithubModelSourceConfig gitHubConfig;

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

      final Map<AspectModel, Aspect> result = new HashMap<>();

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
         result.put( aspectModel, aspect );
      }
      return result;
   }

   protected FileOutputStream getOutputStreamForFile( final String artifactName, final String outputDirectory ) {
      try {
         final Path outputPath = Path.of( outputDirectory );
         Files.createDirectories( outputPath );
         return new FileOutputStream( outputPath.resolve( artifactName ).toFile() );
      } catch ( final IOException exception ) {
         throw new RuntimeException( "Could not write to output " + outputDirectory );
      }
   }

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      if ( Boolean.TRUE.equals( skip ) ) {
         getLog().info( "Generation is skipped." );
         return;
      }

      if ( githubServerId != null ) {
         if ( session == null ) {
            getLog().warn( "Could not read Maven session, ignoring GitHub server configuration." );
         } else {
            final Server server = session.getSettings().getServer( githubServerId );
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

               final List<Proxy> proxies = session.getSettings().getProxies();
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
}
