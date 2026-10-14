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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserRegistry;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.system.FactoryRDFStd;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.Symbol;

import org.eclipse.esmf.aspectmodel.ValueParsingException;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.aspectmodel.resolver.parser.ReaderRiotTurtle;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vavr.control.Try;

public final class TurtleLoader {
   private static final Logger LOG = LoggerFactory.getLogger( TurtleLoader.class );
   private static volatile boolean isTurtleRegistered = false;

   private TurtleLoader() {}

   public static void init() {
      registerTurtle();
   }

   /**
    * Loads a Turtle model from an input stream
    *
    * @param inputStream The input stream
    * @return The model on success, a corresponding exception otherwise
    */
   public static Try<Model> loadTurtle( @NonNull final InputStream inputStream ) {
      final String modelContent = new BufferedReader(
            new InputStreamReader( inputStream, StandardCharsets.UTF_8 ) )
                  .lines()
                  .collect( Collectors.joining( "\n" ) );
      return loadTurtle( modelContent );
   }

   /**
    * Loads a Turtle model from a given URL. Note that this does not honor proxies nor redirects and is
    * intended for resolving resource:// URLs.
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
    * Loads a Turtle model from a String containing RDF/Turtle and keeps track of the logical source
    * location
    *
    * @param modelContent the model content
    * @param location the location of the input source
    * @return the model on success, a corresponding exception otherwise
    */
   public static Try<Model> loadTurtle( final String modelContent, final URI location ) {
      init();
      try {
         final Context context = Context.create();
         context.put( DOCUMENT_URI, location );
         final Model streamModel = RDFParser.create()
               .factory( new FactoryRDFStd() )
               .fromString( modelContent )
               .lang( Lang.TURTLE )
               .context( context )
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
      }
   }

   public static class TurtleLoaderCustomSymbol extends Symbol {
      public TurtleLoaderCustomSymbol( final String symbolName ) {
         super( symbolName );
      }
   }

   public static final Symbol TREE_SITTER_SYNTAX_TREE = new TurtleLoaderCustomSymbol( "treesittersyntaxtree" );
   public static final Symbol DOCUMENT_URI = new TurtleLoaderCustomSymbol( "documenturi" );

   /**
    * Loads a Turtle model from an RDF/Turtle syntax tree and keeps track of the logical source
    * location
    *
    * @param syntaxTree the syntax tree of the model
    * @param location the location of the input source
    * @return the model on success, a corresponding exception otherwise
    */
   public static Try<Model> loadTurtle( final TurtleSyntaxTree syntaxTree, final URI location ) {
      init();
      final String sourceRepresentation = syntaxTree.content();
      try {
         final Context context = Context.create();
         context.put( TREE_SITTER_SYNTAX_TREE, syntaxTree );
         context.put( DOCUMENT_URI, location );
         final Model streamModel = RDFParser.create()
               .factory( new FactoryRDFStd() )
               .fromString( sourceRepresentation )
               .lang( Lang.TURTLE )
               .context( context )
               .toModel();
         return Try.success( streamModel );
      } catch ( final ValueParsingException exception ) {
         // This is thrown by the custom value parsers in SammXsdType
         exception.setSourceDocument( sourceRepresentation );
         exception.setSourceLocation( location );
         throw exception;
      } catch ( final RiotException exception ) {
         // Thrown by Jena for regular syntax errors
         return Try.failure( new ParserException( exception, sourceRepresentation, location ) );
      }
   }

   /**
    * Loads a Turtle model from a String containing RDF/Turtle
    *
    * @param modelContent The model content
    * @return The model on success, a corresponding exception otherwise
    */
   public static Try<Model> loadTurtle( final String modelContent ) {
      return loadTurtle( Objects.requireNonNull( modelContent ), URI.create( "inmemory:model" + modelContent.hashCode() ) );
   }

   private static void registerTurtle() {
      if ( isTurtleRegistered ) {
         return;
      }
      synchronized ( TurtleLoader.class ) {
         if ( !isTurtleRegistered ) {
            SammXsdType.setupTypeMapping();
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
