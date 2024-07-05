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

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
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
   public AspectModelFile apply( final AspectModelUrn input, final ResolutionSupport resolutionSupport ) {
      return strategies.stream()
            .map( strategy -> Try.of( () -> strategy.apply( input, resolutionSupport ) ) )
            .filter( Try::isSuccess )
            .findFirst()
            .map( Try::get )
            .orElseThrow( () ->
                  new ModelResolutionException( "No strategy could resolve the input: " + strategies.stream().map( Object::toString )
                        .collect( Collectors.joining() ) ) );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", EitherStrategy.class.getSimpleName() + "[", "]" )
            .add( "strategies=" + strategies )
            .toString();
   }
}
