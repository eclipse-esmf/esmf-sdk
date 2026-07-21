/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.turtle.languageserver.lsp;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.esmf.aspectmodel.resolver.EitherStrategy;
import org.eclipse.esmf.aspectmodel.resolver.FileSystemStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.fs.FlatModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.fs.StructuredModelsRoot;
import org.eclipse.esmf.aspectmodel.resolver.github.GitHubStrategy;
import org.eclipse.esmf.aspectmodel.resolver.github.GithubModelSourceConfig;
import org.eclipse.esmf.turtle.languageserver.aspect.MetaModelStrategy;
import org.eclipse.esmf.turtle.languageserver.lsp.config.LspModelResolutionConfigurationParser;
import org.eclipse.esmf.turtle.languageserver.lsp.config.GithubResolutionConfiguration;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.turtle.navigation.GithubMaterializingStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResolutionStrategyService {
   private static final Logger LOG = LoggerFactory.getLogger( ResolutionStrategyService.class );

   private final GithubResolutionConfiguration githubConfiguration = new GithubResolutionConfiguration();
   private final MetaModelStrategy metaModelStrategy = new MetaModelStrategy();
   private final Map<GithubModelSourceConfig, ResolutionStrategy> githubStrategyCache = new ConcurrentHashMap<>();

   public void applyConfigurationChange( final Object settings ) {
      final List<GithubModelSourceConfig> repositories = LspModelResolutionConfigurationParser.parse( settings );
      githubConfiguration.update( repositories );
      // Drop cached strategies for repositories that are no longer configured (or whose token/branch/etc.
      // changed, which yields a different config key), so we don't keep serving stale downloads.
      githubStrategyCache.keySet().retainAll( new HashSet<>( repositories ) );
      LOG.debug( "[resolution-strategy-service] GitHub configuration updated: {} repositories configured", repositories.size() );
   }

   public ResolutionStrategy buildResolutionStrategyForDocument( final ParsedDocument parsedDocument ) {
      final Path openFilePath;
      try {
         openFilePath = Path.of( new URI( parsedDocument.getUri() ) );
      } catch ( final URISyntaxException e ) {
         throw new ModelResolutionException( "Failed to parse URI: " + parsedDocument.getUri(), e );
      }
      final FileSystemStrategy structuredStrategy = new FileSystemStrategy(
            new StructuredModelsRoot( openFilePath.getParent().getParent().getParent() ) );
      final FileSystemStrategy flatStrategy = new FileSystemStrategy( new FlatModelsRoot( openFilePath.getParent() ) );

      final List<ResolutionStrategy> strategies = new ArrayList<>();
      strategies.add( structuredStrategy );
      strategies.add( flatStrategy );
      strategies.add( metaModelStrategy );
      for ( final GithubModelSourceConfig repository : githubConfiguration.repositories() ) {
         strategies.add( githubStrategyCache.computeIfAbsent( repository,
               config -> new GithubMaterializingStrategy( new GitHubStrategy( config ) ) ) );
      }
      return new EitherStrategy( strategies );
   }
}
