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

import org.apache.jena.rdf.model.Model;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import io.vavr.control.Try;

/**
 * A Resolution strategy that supports two types of inputs and wraps two dedicated sub-resolution strategies
 * for each of the inputs
 */
public class EitherStrategy implements ResolutionStrategy {
   private final ResolutionStrategy strategy1;
   private final ResolutionStrategy strategy2;

   public EitherStrategy( final ResolutionStrategy strategy1, final ResolutionStrategy strategy2 ) {
      this.strategy1 = strategy1;
      this.strategy2 = strategy2;
   }

   @Override
   public Try<Model> apply( final AspectModelUrn input ) {
      return strategy1.apply( input ).orElse( () -> strategy2.apply( input ) );
   }

   @Override
   public Try<Model> applyUri( URI uri ) {
      return strategy1.applyUri( uri ).orElse( () -> strategy2.applyUri( uri ) );
   }

   public InputStream read( URI uri ) {
      return Try.of( () -> strategy1.read( uri ) ).getOrElseTry( () -> strategy2.read( uri ) );
   }

   @Override
   public String toString() {
      return "EitherStrategy(strategy1=" + strategy1 + ", strategy2=" + strategy2 + ")";
   }
}
