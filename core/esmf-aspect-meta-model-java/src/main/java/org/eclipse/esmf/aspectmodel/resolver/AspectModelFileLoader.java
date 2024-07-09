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

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ParserException;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.services.TurtleLoader;

import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;

/**
 * Loads an input source into a {@link RawAspectModelFile}, i.e., an Aspect Model file that does not yet provide information
 * about its model elements. This class should normally not be directly used as it is part of the regular Aspect Model loader.
 * Use {@link AspectModelLoader} instead.
 */
public class AspectModelFileLoader {
   public static RawAspectModelFile load( final File file ) {
      try {
         final RawAspectModelFile fromString = load( content( new FileInputStream( file ) ) );
         return new RawAspectModelFile( fromString.sourceModel(), fromString.headerComment(), Optional.of( file.toURI() ) );
      } catch ( final FileNotFoundException exception ) {
         throw new ModelResolutionException( "File not found: " + file, exception );
      }
   }

   public static RawAspectModelFile load( final String rdfTurtle ) {
      final List<String> headerComment = headerComment( rdfTurtle );
      final Try<Model> tryModel = TurtleLoader.loadTurtle( rdfTurtle );
      if ( tryModel.isFailure() && tryModel.getCause() instanceof final ParserException parserException ) {
         throw parserException;
      }
      final Model model = tryModel.getOrElseThrow(
            () -> new ModelResolutionException( "Can not load model", tryModel.getCause() ) );
      return new RawAspectModelFile( model, headerComment, Optional.empty() );
   }

   private static String content( final InputStream inputStream ) {
      return new BufferedReader( new InputStreamReader( inputStream, StandardCharsets.UTF_8 ) ).lines()
            .collect( Collectors.joining( "\n" ) );
   }

   private static List<String> headerComment( final String content ) {
      return content.lines().takeWhile( line -> line.startsWith( "#" ) || isBlank( line ) ).toList();
   }

   public static RawAspectModelFile load( final InputStream inputStream ) {
      final AspectModelFile fromString = load( content( inputStream ) );
      return new RawAspectModelFile( fromString.sourceModel(), fromString.headerComment(), Optional.empty() );
   }

   public static RawAspectModelFile load( final Model model ) {
      return new RawAspectModelFile( model, List.of(), Optional.empty() );
   }

   public static RawAspectModelFile load( final URL url ) {
      if ( url.getProtocol().equals( "file" ) ) {
         try {
            return load( Paths.get( url.toURI() ).toFile() );
         } catch ( final URISyntaxException exception ) {
            throw new ModelResolutionException( "Can not load model from file URL", exception );
         }
      }
      try {
         return load( url.openStream() );
      } catch ( final IOException exception ) {
         throw new ModelResolutionException( "Can not load model from URL", exception );
      }
   }

   public static RawAspectModelFile load( final URI uri ) {
      try {
         return load( uri.toURL() );
      } catch ( final MalformedURLException exception ) {
         throw new ModelResolutionException( "Can not load model from URIs that are not URLs", exception );
      }
   }
}
