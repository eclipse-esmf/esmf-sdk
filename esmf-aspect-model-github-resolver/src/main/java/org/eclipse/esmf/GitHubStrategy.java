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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelFileLoader;
import org.eclipse.esmf.aspectmodel.resolver.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategySupport;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import io.vavr.control.Try;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitHubStrategy implements ResolutionStrategy {

   private static final Logger LOG = LoggerFactory.getLogger( GitHubStrategy.class );

   protected final String repositoryUrl;

   protected final String searchDirectory;

   public GitHubStrategy( final String repositoryUrl, final String searchDirectory ) {
      this.repositoryUrl = repositoryUrl;
      this.searchDirectory = searchDirectory; // "."
   }

   @Override
   public AspectModelFile apply( final AspectModelUrn aspectModelUrn, final ResolutionStrategySupport resolutionStrategySupport )
         throws ModelResolutionException {

      try {
         GitHub github = GitHub.connectAnonymously();

         GHRepository repository = github.getRepository( repositoryUrl );

         final List<GHContent> contents = repository.getDirectoryContent( searchDirectory );
         for ( GHContent content : contents ) {
            if ( content.isFile() && content.getName().endsWith( ".ttl" ) ) {

               final File file = new File( content.getDownloadUrl() );

               final Try<AspectModelFile> tryModel = Try.of(
                     () -> AspectModelFileLoader.load( new URL( content.getDownloadUrl() ).openStream() ) );

               if ( tryModel.isFailure() ) {
                  LOG.debug( "Could not load model from {}", file, tryModel.getCause() );
               } else {
                  final AspectModelFile model = tryModel.get();
                  if ( resolutionStrategySupport.containsDefinition( model, aspectModelUrn ) ) {
                     return model;
                  } else {
                     LOG.debug( "File {} does not contain {}", file, aspectModelUrn );
                  }
               }
            }
         }
      } catch ( IOException e ) {
         e.printStackTrace();
      }

      return null;
   }
}
