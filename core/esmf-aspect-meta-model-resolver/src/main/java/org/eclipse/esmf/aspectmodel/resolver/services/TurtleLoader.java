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
package org.eclipse.esmf.aspectmodel.resolver.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.RiotException;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.aspectmodel.resolver.parser.ReaderRiotTurtle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Try;

public final class TurtleLoader {
   private static final Logger LOG = LoggerFactory.getLogger( TurtleLoader.class );

   private static volatile boolean isTurtleRegistered = false;

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

      final String modelContent = new BufferedReader(
            new InputStreamReader( inputStream, StandardCharsets.UTF_8 ) )
            .lines()
            .collect( Collectors.joining( "\n" ) );

      final Model streamModel = ModelFactory.createDefaultModel();
      registerTurtle();
      try ( final InputStream turtleInputStream = new ByteArrayInputStream( modelContent.getBytes( StandardCharsets.UTF_8 ) ) ) {
         streamModel.read( turtleInputStream, "", RDFLanguages.TURTLE.getName() );
         return Try.success( streamModel );
      } catch ( final IllegalArgumentException exception ) {
         LOG.error( "Invalid value encountered in Aspect Model.", exception );
         final String incorrectDataTypeDefinitionMessage = "%s is not a valid value for the defined data type.";
         final String formattedErrorMessage = String
               .format( incorrectDataTypeDefinitionMessage, exception.getMessage() );
         return Try.failure( new IllegalArgumentException( formattedErrorMessage ) );
      } catch ( final IOException exception ) {
         return Try.failure( exception );
      } catch ( final RiotException exception ) {
         return Try.failure( new ParserException( exception, modelContent ) );
      }
   }

   private static void registerTurtle() {
      if ( isTurtleRegistered ) {
         return;
      }
      synchronized ( TurtleLoader.class ) {
         if ( !isTurtleRegistered ) {
            RDFParserRegistry.registerLangTriples( Lang.TURTLE, ReaderRiotTurtle.factory );
            isTurtleRegistered = true;
         }
      }
   }
}
