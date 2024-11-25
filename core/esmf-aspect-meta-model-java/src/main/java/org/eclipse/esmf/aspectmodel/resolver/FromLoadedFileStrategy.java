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

package org.eclipse.esmf.aspectmodel.resolver;

import java.net.URI;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

/**
 * Simple resolution strategy that resolves a URN by returning a previously loaded AspectModelFile.
 */
public class FromLoadedFileStrategy implements ResolutionStrategy {
   private final AspectModelFile aspectModelFile;

   public FromLoadedFileStrategy( final AspectModelFile aspectModelFile ) {
      this.aspectModelFile = aspectModelFile;
   }

   @Override
   public AspectModelFile apply( final AspectModelUrn aspectModelUrn, final ResolutionStrategySupport resolutionStrategySupport )
         throws ModelResolutionException {
      if ( resolutionStrategySupport.containsDefinition( aspectModelFile, aspectModelUrn ) ) {
         return aspectModelFile;
      }
      final ModelResolutionException.LoadingFailure failure = new ModelResolutionException.LoadingFailure(
            aspectModelUrn,
            aspectModelFile.sourceLocation().map( uri -> "In-memory file " + uri ).orElse( "Anonymous in-memory file" ),
            "File does not contain element definition" );
      throw new ModelResolutionException( failure );
   }

   @Override
   public Stream<URI> listContents() {
      return aspectModelFile.sourceLocation().stream();
   }

   @Override
   public Stream<URI> listContentsForNamespace( final AspectModelUrn namespace ) {
      return aspectModelFile.namespace().urn().equals( namespace )
            ? aspectModelFile.sourceLocation().stream()
            : Stream.empty();
   }

   @Override
   public Stream<AspectModelFile> loadContents() {
      return Stream.of( aspectModelFile );
   }

   @Override
   public Stream<AspectModelFile> loadContentsForNamespace( final AspectModelUrn namespace ) {
      return aspectModelFile.namespace().urn().equals( namespace )
            ? Stream.of( aspectModelFile )
            : Stream.empty();
   }
}
