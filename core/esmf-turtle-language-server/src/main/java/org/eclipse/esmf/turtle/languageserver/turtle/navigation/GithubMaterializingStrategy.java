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

package org.eclipse.esmf.turtle.languageserver.turtle.navigation;

import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategySupport;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.resolver.github.GitHubStrategy;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFileBuilder;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

public class GithubMaterializingStrategy implements ResolutionStrategy {
   private final GitHubStrategy delegate;

   public GithubMaterializingStrategy( final GitHubStrategy delegate ) {
      this.delegate = delegate;
   }

   @Override
   public AspectModelFile apply( final AspectModelUrn urn, final ResolutionStrategySupport support )
         throws ModelResolutionException {
      final AspectModelFile resolved = delegate.apply( urn, support );
      final Optional<URI> sourceLocation = resolved.sourceLocation();
      if ( sourceLocation.isEmpty() || "file".equals( sourceLocation.get().getScheme() ) ) {
         return resolved;
      }

      final String filename = resolved.filename().orElseGet( () -> urn.getName() + ".ttl" );
      final String uniqueName = "github-" + urn.getNamespaceMainPart() + "-" + urn.getVersion() + "-" + filename;
      final Path localFile = ExternalModelFileCache.materialize( uniqueName, resolved.sourceRepresentation() );
      return RawAspectModelFileBuilder.builder()
            .sourceRepresentation( resolved.sourceRepresentation() )
            .sourceModel( resolved.sourceModel() )
            .sourceLocation( Optional.of( localFile.toUri() ) )
            .build();
   }

   @Override
   public Stream<URI> listContents() {
      return delegate.listContents();
   }

   @Override
   public Stream<URI> listContentsForNamespace( final AspectModelUrn namespace ) {
      return delegate.listContentsForNamespace( namespace );
   }

   @Override
   public Stream<AspectModelFile> loadContents() {
      return delegate.loadContents();
   }

   @Override
   public Stream<AspectModelFile> loadContentsForNamespace( final AspectModelUrn namespace ) {
      return delegate.loadContentsForNamespace( namespace );
   }
}
