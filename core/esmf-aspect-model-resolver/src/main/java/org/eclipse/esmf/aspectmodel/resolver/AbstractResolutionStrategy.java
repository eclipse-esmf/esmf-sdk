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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;

import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;

/**
 * Abstract base class for the implementation of {@link ResolutionStrategy}s.
 */
public abstract class AbstractResolutionStrategy implements ResolutionStrategy {
   /**
    * Loads an Aspect model from a resolveable URI
    *
    * @param uri The URI
    * @return The model
    */
   protected Try<Model> loadFromUri( final URI uri ) {
      try {
         return loadFromUrl( uri.toURL() );
      } catch ( final MalformedURLException exception ) {
         return Try.failure( exception );
      }
   }

   /**
    * Loads an Aspect model from a resolveable URL
    *
    * @param url The URL
    * @return The model
    */
   protected Try<Model> loadFromUrl( final URL url ) {
      return Try.ofSupplier( () -> TurtleLoader.openUrl( url ) ).flatMap( TurtleLoader::loadTurtle );
   }
}
