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
package org.eclipse.esmf.aspectmodel.resolver.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.esmf.aspectmodel.ValueParsingException;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.aspectmodel.resolver.parser.ReaderRiotTurtle;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;

import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.system.FactoryRDFStd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TurtleLoader {
   private static final Logger LOG = LoggerFactory.getLogger( TurtleLoader.class );

   private static volatile boolean isTurtleRegistered = false;

   private TurtleLoader() {
   }

   public static void init() {
      SammXsdType.setupTypeMapping();
      registerTurtle();
   }

   /**
    * Loads a Turtle model from an input stream
    *
    * @param inputStream The input stream
    * @return The model on success, a corresponding exception otherwise
    */
   public static Try<Model> loadTurtle( @Nullable final InputStream inputStream ) {
      if ( inputStream == null ) {
         return Try.failure( new IllegalArgumentException() );
      }
      final String modelContent = new BufferedReader(
            new InputStreamReader( inputStream, StandardCharsets.UTF_8 ) )
            .lines()
            .collect( Collectors.joining( "\n" ) );
      return loadTurtle( modelContent );
   }

   /**
    * Loads a Turtle model from a given URL. Note that this does not honor proxies nor redirects and is intended for resolving resource://
    * URLs.
    *
    * @param url The input url
    * @return The model on success, a corresponding exception otherwise
    */
   public static Try<Model> loadTurtle( final URL url ) {
      try {
         return loadTurtle( url.openStream() );
      } catch ( final IOException exception ) {
         return Try.failure( exception );
      }
   }

   /**
    * Loads a Turtle model from a String containing RDF/Turtle and keeps track of the logical source location
    *
    * @param modelContent the model content
    * @param location the location of the input source
    * @return the model on success, a corresponding exception otherwise
    */
   public static Try<Model> loadTurtle( @Nonnull final String modelContent, @Nullable final URI location ) {
      init();
      try ( final InputStream turtleInputStream = new ByteArrayInputStream( modelContent.getBytes( StandardCharsets.UTF_8 ) ) ) {
         final Model streamModel = RDFParser.create()
               .factory( new FactoryRDFStd() )
               .source( turtleInputStream )
               .lang( Lang.TURTLE )
               .toModel();
         return Try.success( streamModel );
      } catch ( final ValueParsingException exception ) {
         // This is thrown by the custom value parsers in SammXsdType
         exception.setSourceDocument( modelContent );
         exception.setSourceLocation( location );
         throw exception;
      } catch ( final RiotException exception ) {
         // Thrown by Jena for regular syntax errors
         return Try.failure( new ParserException( exception, modelContent, location ) );
      } catch ( final IOException exception ) {
         return Try.failure( exception );
      }
   }

   /**
    * Loads a Turtle model from a String containing RDF/Turtle
    *
    * @param modelContent The model content
    * @return The model on success, a corresponding exception otherwise
    */
   public static Try<Model> loadTurtle( @Nullable final String modelContent ) {
      return loadTurtle( Objects.requireNonNull( modelContent ), null );
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

   /**
    * Opens an URL and returns its InputStream, but does not throw a checked exception.
    *
    * @param url The URL to open
    * @return The InputStream on success
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
