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
 * A ResolutionStrategy that executes an external command, which will be executed using a {@link CommandExecutor}.
 */
public class ExternalResolverStrategy implements ResolutionStrategy {
   private final String command;

   public ExternalResolverStrategy( final String command ) {
      this.command = command;
   }

   @Override
   public AspectModelFile apply( final AspectModelUrn aspectModelUrn, final ResolutionStrategySupport resolutionStrategySupport ) {
      try {
         final String commandWithParameters = command + " " + aspectModelUrn.toString();
         final String result = CommandExecutor.executeCommand( commandWithParameters );
         return AspectModelFileLoader.load( result );
      } catch ( final ModelResolutionException exception ) {
         final ModelResolutionException.LoadingFailure failure = new ModelResolutionException.LoadingFailure(
               aspectModelUrn, "The output of '" + command + "'", "Command evaluation failed", exception );
         throw new ModelResolutionException( failure );
      }
   }

   @Override
   public Stream<URI> listContents() {
      return Stream.empty();
   }

   @Override
   public Stream<URI> listContentsForNamespace( final AspectModelUrn namespace ) {
      return Stream.empty();
   }

   @Override
   public Stream<AspectModelFile> loadContents() {
      return Stream.empty();
   }

   @Override
   public Stream<AspectModelFile> loadContentsForNamespace( final AspectModelUrn namespace ) {
      return Stream.empty();
   }
}
