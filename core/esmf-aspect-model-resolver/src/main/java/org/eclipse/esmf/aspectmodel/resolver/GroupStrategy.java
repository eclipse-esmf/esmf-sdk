/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import io.vavr.control.Try;

/**
 * A Resolution strategy that supports multiple sub-resolution strategies and wraps them in a group
 */
public class GroupStrategy implements ResolutionStrategy {
   private final List<ResolutionStrategy> strategies;

   /**
    * @param strategies the sub-resolution strategies to be wrapped
    */
   public GroupStrategy( final ResolutionStrategy... strategies ) {
      this( List.of( strategies ) );
   }

   /**
    * @param strategies the sub-resolution strategies to be wrapped
    */
   public GroupStrategy( final List<ResolutionStrategy> strategies ) {
      this.strategies = List.copyOf( strategies );
   }

   @Override
   public Try<Model> apply( final AspectModelUrn input ) {
      return strategies.stream().map( strategy -> strategy.apply( input ) ).filter( Try::isSuccess ).findFirst()
            .orElseGet( () -> Try.failure( new ModelResolutionException( "No strategy was able to resolve " + input ) ) );
   }

   @Override
   public Try<Model> applyUri( URI uri ) {
      return strategies.stream().map( strategy -> strategy.applyUri( uri ) ).filter( Try::isSuccess ).findFirst()
            .orElseGet( () -> Try.failure( new ModelResolutionException( "No strategy was able to resolve " + uri ) ) );
   }

   @Override
   public InputStream read( URI uri ) {
      return strategies.stream()
            .map( strategy -> Try.of( () -> strategy.read( uri ) ) )
            .filter( Try::isSuccess )
            .findFirst()
            .map( Try::get )
            .orElseThrow();

   }

   @Override
   public String toString() {
      return "GroupStrategy(" + strategies.stream().map( Object::toString ).collect( Collectors.joining( ", " ) ) + ")";
   }
}
