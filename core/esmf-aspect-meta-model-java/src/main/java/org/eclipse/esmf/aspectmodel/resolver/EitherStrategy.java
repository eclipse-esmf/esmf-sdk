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
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import io.vavr.control.Try;

/**
 * A Resolution strategy that supports multiple types of inputs and wraps sub-resolution strategies
 */
public class EitherStrategy implements ResolutionStrategy {
   final List<ResolutionStrategy> strategies;

   public EitherStrategy( final List<ResolutionStrategy> strategies ) {
      this.strategies = strategies;
   }

   public EitherStrategy( final ResolutionStrategy... strategies ) {
      this( List.of( strategies ) );
   }

   public EitherStrategy( final ResolutionStrategy strategy1, final ResolutionStrategy strategy2 ) {
      this( List.of( strategy1, strategy2 ) );
   }

   @Override
   public AspectModelFile apply( final AspectModelUrn input, final ResolutionStrategySupport resolutionStrategySupport ) {
      final List<ModelResolutionException.LoadingFailure> checkedLocations = new ArrayList<>();
      for ( final ResolutionStrategy strategy : strategies ) {
         final Try<AspectModelFile> tryFile = Try.of( () -> strategy.apply( input, resolutionStrategySupport ) );
         if ( tryFile.isFailure() ) {
            if ( tryFile.getCause() instanceof final ModelResolutionException modelResolutionException ) {
               checkedLocations.addAll( modelResolutionException.getCheckedLocations() );
            }
            continue;
         }
         return tryFile.get();
      }
      throw new ModelResolutionException( checkedLocations );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", EitherStrategy.class.getSimpleName() + "[", "]" )
            .add( "strategies=" + strategies )
            .toString();
   }

   @Override
   public Stream<URI> listContents() {
      return strategies.stream().flatMap( ResolutionStrategy::listContents );
   }

   @Override
   public Stream<URI> listContentsForNamespace( final AspectModelUrn namespace ) {
      return strategies.stream().flatMap( strategy -> strategy.listContentsForNamespace( namespace ) );
   }

   @Override
   public Stream<AspectModelFile> loadContents() {
      return strategies.stream().flatMap( ResolutionStrategy::loadContents );
   }

   @Override
   public Stream<AspectModelFile> loadContentsForNamespace( final AspectModelUrn namespace ) {
      return strategies.stream().flatMap( strategy -> strategy.loadContentsForNamespace( namespace ) );
   }
}
