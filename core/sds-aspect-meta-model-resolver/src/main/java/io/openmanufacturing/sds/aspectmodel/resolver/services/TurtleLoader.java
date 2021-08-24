/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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
package io.openmanufacturing.sds.aspectmodel.resolver.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.annotation.Nullable;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Try;

public final class TurtleLoader {
   private static final Logger LOG = LoggerFactory.getLogger( TurtleLoader.class );

   private TurtleLoader() {

   }

   /**
    * Loads a Turtle model from an input stream
    *
    * @param inputStream The input stream
    * @return The model on success, a corresponding exception otherwise
    */
   public static Try<Model> loadTurtle( @Nullable final InputStream inputStream ) {
      DataType.setupTypeMapping();

      if ( inputStream == null ) {
         return Try.failure( new IllegalArgumentException() );
      }

      final Model streamModel = ModelFactory.createDefaultModel();
      try ( final InputStream turtleInputStream = inputStream ) {
         streamModel.read( turtleInputStream, "", RDFLanguages.TURTLE.getName() );
         return Try.success( streamModel );
      } catch ( final IllegalArgumentException exception ) {
         LOG.error( "Invalid value encountered in Aspect Model.", exception );
         final String incorrectDataTypeDefinitionMessage = "%s is not a valid value for the defined data type.";
         final String formattedErrorMessage = String
               .format( incorrectDataTypeDefinitionMessage, exception.getMessage() );
         return Try.failure( new IllegalArgumentException( formattedErrorMessage ) );
      } catch ( final IOException | RiotException exception ) {
         return Try.failure( exception );
      }
   }

   /**
    * Opens an URL and returns its InputStream, but does not throw a checked exception.
    *
    * @param url The URL to open
    * @return The InputStream on success
    *
    * @throws IllegalArgumentException if an {@link IOException} occurs
    */
   public static InputStream openUrl( final URL url ) {
      try {
         return url.openStream();
      } catch ( final IOException exception ) {
         throw new IllegalArgumentException( exception );
      }
   }
}
