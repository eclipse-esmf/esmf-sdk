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

package org.eclipse.esmf;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.GitHubFileLocation;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.github.GitHubStrategy;
import org.eclipse.esmf.aspectmodel.resolver.github.GithubModelSourceConfig;
import org.eclipse.esmf.aspectmodel.resolver.github.GithubModelSourceConfigBuilder;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.validation.ValidatorConfig;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.AspectModel;

import io.vavr.control.Either;

/**
 * The GitHubUrlInputHandler knows how to load an Aspect Model from a given URL which points to a location in a models root
 * stored in a GitHub repository
 */
public class GitHubUrlInputHandler extends AbstractInputHandler {
   private final String url;
   private final GitHubFileLocation location;

   public GitHubUrlInputHandler( final String input, final ResolverConfigurationMixin resolverConfig, final boolean details,
         final ValidatorConfig validatorConfig ) {
      super( input, resolverConfig, details, validatorConfig );
      location = GitHubFileLocation.parse( input ).orElseThrow( () ->
            new CommandException( "Input URL format is not supported: " + input ) );
      url = input;
   }

   @Override
   protected List<ResolutionStrategy> resolutionStrategies() {
      final List<ResolutionStrategy> strategies = new ArrayList<>();
      final GithubModelSourceConfig modelSourceConfig = GithubModelSourceConfigBuilder.builder()
            .repository( location.repositoryLocation() )
            .directory( location.directory() )
            .token( resolverConfig.gitHubToken )
            .build();
      strategies.add( new GitHubStrategy( modelSourceConfig ) );
      strategies.addAll( configuredStrategies() );
      return strategies;
   }

   @Override
   protected String expectedAspectName() {
      return location.filename().replace( ".ttl", "" );
   }

   public static boolean appliesToInput( final String input ) {
      return input.startsWith( "https://github.com/" );
   }

   @Override
   public AspectModel loadAspectModel() {
      final AspectModelUrn urn = AspectModelUrn.from( location.namespaceMainPart(), location.version(), expectedAspectName() )
            .getOrElseThrow( () -> new CommandException( "Could not construct valid Aspect Model URN from input URL: " + url ) );
      final Function<AspectModelLoader, AspectModel> loaderFunction = validate
            ? ( (Function<AspectModelLoader, Either<List<Violation>, AspectModel>>) loader ->
            loader.withValidation( validator ).load( urn ) ).andThen( this::getAspectModelOrPrintValidationReport )
            : ( aspectModelLoader -> aspectModelLoader.load( urn ) );
      return applyAspectModelLoader( loaderFunction );
   }

   @Override
   public URI inputUri() {
      return URI.create( url );
   }

   @Override
   public AspectModelFile loadAspectModelFile() {
      final AspectModel aspectModel = loadAspectModel();
      return aspectModel.files()
            .stream()
            .filter( file -> file.sourceLocation().map( location -> location.equals( inputUri() ) ).orElse( false ) )
            .findFirst()
            .orElseThrow( () -> new CommandException( "Could not load: " + inputUri() ) );
   }
}
