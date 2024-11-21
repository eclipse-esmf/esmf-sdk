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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.ExternalResolverStrategy;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.GithubRepository;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.github.GitHubStrategy;
import org.eclipse.esmf.aspectmodel.resolver.github.GithubModelSourceConfig;
import org.eclipse.esmf.aspectmodel.resolver.github.GithubModelSourceConfigBuilder;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.aspectmodel.validation.services.DetailedViolationFormatter;
import org.eclipse.esmf.aspectmodel.validation.services.ViolationFormatter;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;

import io.vavr.control.Either;

/**
 * Base functionality for all InputHandler implementations
 */
@SuppressWarnings( "UseOfSystemOutOrSystemErr" )
public abstract class AbstractInputHandler implements InputHandler {
   protected final String input;
   protected final ResolverConfigurationMixin resolverConfig;
   protected final boolean details;

   public AbstractInputHandler( final String input, final ResolverConfigurationMixin resolverConfig, final boolean details ) {
      this.input = input;
      this.resolverConfig = resolverConfig;
      this.details = details;
   }

   /**
    * Returns the list of resolution strategies specific to this input type
    *
    * @return the resolution strategies
    */
   protected abstract List<ResolutionStrategy> resolutionStrategies();

   /**
    * Returns the expected Aspect name depending on the input, e.g. the file base name.
    *
    * @return the expected Aspect name
    */
   protected abstract String expectedAspectName();

   protected List<ResolutionStrategy> configuredStrategies() {
      final List<ResolutionStrategy> strategies = new ArrayList<>();
      if ( resolverConfig == null ) {
         return strategies;
      }
      final List<String> modelsRoots = resolverConfig.modelsRoots == null
            ? List.of()
            : resolverConfig.modelsRoots;
      for ( final String modelsRoot : modelsRoots ) {
         strategies.add( new FileSystemStrategy( new StructuredModelsRoot( Path.of( modelsRoot ) ) ) );
      }
      if ( resolverConfig.commandLine != null && !resolverConfig.commandLine.isBlank() ) {
         strategies.add( new ExternalResolverStrategy( resolverConfig.commandLine ) );
      }
      if ( resolverConfig.gitHubResolutionOptions != null && resolverConfig.gitHubResolutionOptions.gitHubName != null ) {
         final String[] parts = resolverConfig.gitHubResolutionOptions.gitHubName.split( "/" );
         final String owner = parts[0];
         final String repositoryName = parts[1];
         final GithubRepository.Ref branchOrTag = resolverConfig.gitHubResolutionOptions.gitHubTag != null
               ? new GithubRepository.Tag( resolverConfig.gitHubResolutionOptions.gitHubTag )
               : new GithubRepository.Branch( resolverConfig.gitHubResolutionOptions.gitHubBranch );
         final GithubRepository repository = new GithubRepository( owner, repositoryName, branchOrTag );
         final GithubModelSourceConfig modelSourceConfig = GithubModelSourceConfigBuilder.builder()
               .repository( repository )
               .directory( resolverConfig.gitHubResolutionOptions.gitHubDirectory )
               .token( resolverConfig.gitHubResolutionOptions.gitHubToken )
               .build();
         strategies.add( new GitHubStrategy( modelSourceConfig ) );
      }
      return strategies;
   }

   @Override
   public AspectModelLoader aspectModelLoader() {
      return new AspectModelLoader( resolutionStrategies() );
   }

   protected AspectModel applyAspectModelLoader( final Function<AspectModelLoader, AspectModel> loader ) {
      final Either<List<Violation>, AspectModel> validModelOrViolations = new AspectModelValidator().loadModel( () ->
            loader.apply( aspectModelLoader() ) );
      if ( validModelOrViolations.isLeft() ) {
         final List<Violation> violations = validModelOrViolations.getLeft();
         if ( details ) {
            System.out.println( new DetailedViolationFormatter().apply( violations ) );
         } else {
            System.out.println( new ViolationFormatter().apply( violations ) );
         }
         System.exit( 1 );
         return null;
      }
      return validModelOrViolations.get();
   }

   @Override
   public Aspect loadAspect() {
      final AspectModel aspectModel = loadAspectModel();

      final String expectedAspectName = expectedAspectName();
      if ( aspectModel.aspects().isEmpty() ) {
         throw new CommandException( new ModelResolutionException( "No Aspects were found in the model" ) );
      }
      if ( aspectModel.aspects().size() == 1 ) {
         return aspectModel.aspect();
      }
      return aspectModel.aspects().stream()
            .filter( aspect -> aspect.getName().equals( expectedAspectName ) )
            .findFirst()
            .orElseThrow( () -> new ModelResolutionException(
                  "Found multiple Aspects in the input " + input + ", but none is called '"
                        + expectedAspectName + "': " + aspectModel.aspects().stream().map( Aspect::getName )
                        .collect( Collectors.joining( ", " ) ) ) );
   }
}
