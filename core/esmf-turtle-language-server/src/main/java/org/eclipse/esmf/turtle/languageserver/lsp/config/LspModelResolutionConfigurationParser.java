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

package org.eclipse.esmf.turtle.languageserver.lsp.config;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.esmf.aspectmodel.resolver.GithubRepository;
import org.eclipse.esmf.aspectmodel.resolver.github.GithubModelSourceConfig;
import org.eclipse.esmf.aspectmodel.resolver.github.GithubModelSourceConfigBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses the LSP modelResolution configuration out of the raw LSP {@code settings} payload that is
 * delivered by
 * the IDE, either via {@code workspace/didChangeConfiguration} or as part of a {@code
 * workspace/configuration}
 * response.
 *
 * <p>
 * The expected shape is:
 * <pre>{@code
 * {
 * "semantic-models": {
 * "modelResolution": {
 * "githubRepositories": [ {
 * "repository": "owner/repository",
 * "path": "/some/directory",
 * "branch": "branch-name",
 * "tag": "tag-name",
 * "token": "..."
 * } ]
 * }
 * }
 * }
 * }</pre>
 */
public final class LspModelResolutionConfigurationParser {
   private static final Logger LOG = LoggerFactory.getLogger( LspModelResolutionConfigurationParser.class );
   private static final Gson GSON = new Gson();
   private static final String DEFAULT_BRANCH = "main";
   private static final Pattern REPOSITORY_SPEC = Pattern.compile( "(?<owner>[^/\\s]+)/(?<repository>[^/\\s]+)" );

   private LspModelResolutionConfigurationParser() {}

   public static List<GithubModelSourceConfig> parse( final Object settings ) {
      final ModelResolutionSettings modelResolution = toJsonObject( settings )
            .map( LspModelResolutionConfigurationParser::locateModelResolutionSection )
            .flatMap( LspModelResolutionConfigurationParser::parseModelResolutionSettings )
            .orElseGet( ModelResolutionSettings::empty );

      final List<GithubModelSourceConfig> result = Optional.ofNullable( modelResolution.githubRepositories() )
            .stream()
            .flatMap( List::stream )
            .filter( Objects::nonNull )
            .map( LspModelResolutionConfigurationParser::toModelSourceConfig )
            .flatMap( Optional::stream )
            .toList();
      LOG.info( "[modelResolution config] configured {} GitHub resolution source(s)", result.size() );
      return result;
   }

   private static JsonObject locateModelResolutionSection( final JsonObject settings ) {
      final JsonElement semanticModels = settings.get( "semantic-models" );
      final JsonElement modelResolution = semanticModels != null && semanticModels.isJsonObject()
            ? semanticModels.getAsJsonObject().get( "modelResolution" )
            : null;
      return modelResolution != null && modelResolution.isJsonObject() ? modelResolution.getAsJsonObject() : new JsonObject();
   }

   private static Optional<ModelResolutionSettings> parseModelResolutionSettings( final JsonObject section ) {
      try {
         return Optional.ofNullable( GSON.fromJson( section, ModelResolutionSettings.class ) );
      } catch ( final JsonParseException exception ) {
         LOG.warn( "[modelResolution config] could not interpret settings payload: {}", exception.getMessage() );
         return Optional.empty();
      }
   }

   private static Optional<JsonObject> toJsonObject( final Object settings ) {
      if ( settings instanceof final JsonElement jsonElement && jsonElement.isJsonObject() ) {
         return Optional.of( jsonElement.getAsJsonObject() );
      }
      if ( settings != null ) {
         LOG.warn( "[modelResolution config] could not interpret settings payload of type {}", settings.getClass().getName() );
      }
      return Optional.empty();
   }

   private static Optional<GithubModelSourceConfig> toModelSourceConfig( final RepositoryEntry entry ) {
      try {
         return Optional.of( GithubModelSourceConfigBuilder.builder()
               .repository( parseRepository( entry.repository(), entry.branch(), entry.tag() ) )
               .directory( entry.path() )
               .token( nonBlank( entry.token() ).orElse( null ) )
               .build() );
      } catch ( final RuntimeException exception ) {
         LOG.warn( "[modelResolution config] ignoring invalid GitHub repository entry '{}': {}", entry, exception.getMessage() );
         return Optional.empty();
      }
   }

   private static GithubRepository parseRepository( final String repositorySpec, final String branch, final String tag ) {
      final String spec = nonBlank( repositorySpec )
            .orElseThrow( () -> new IllegalArgumentException( "Repository entry must contain a non-blank 'repository' value" ) );
      final Matcher matcher = REPOSITORY_SPEC.matcher( spec );
      if ( !matcher.matches() ) {
         throw new IllegalArgumentException( "Expected '<owner>/<repository>', got: '" + spec + "'" );
      }
      final String owner = matcher.group( "owner" );
      final String repository = matcher.group( "repository" );
      final GithubRepository.Ref branchOrTag = nonBlank( tag ).<GithubRepository.Ref>map( GithubRepository.Tag::new )
            .or( () -> nonBlank( branch ).<GithubRepository.Ref>map( GithubRepository.Branch::new ) )
            .orElseGet( () -> new GithubRepository.Branch( DEFAULT_BRANCH ) );
      return new GithubRepository( owner, repository, branchOrTag );
   }

   private static Optional<String> nonBlank( final String value ) {
      return Optional.ofNullable( value ).filter( s -> !s.isBlank() );
   }

   private record ModelResolutionSettings(
         List<RepositoryEntry> githubRepositories
   ) {
      static ModelResolutionSettings empty() {
         return new ModelResolutionSettings( List.of() );
      }
   }

   private record RepositoryEntry(
         String repository,
         String path,
         String branch,
         String tag,
         String token
   ) {}
}
