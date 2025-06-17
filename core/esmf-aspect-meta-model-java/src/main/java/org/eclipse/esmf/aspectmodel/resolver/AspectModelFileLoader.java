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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnmappableCharacterException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.exceptions.ModelResolutionException;
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
   /**
    * Loads the content of an AspectModelFile from a file
    *
    * @param file the model file
    * @return the loaded file content
    */
   public static RawAspectModelFile load( final File file ) {
      try {
         final String stringContent = content( new FileInputStream( file ), file.toURI() );
         final RawAspectModelFile fromString = load( stringContent, file.toURI() );
         return new RawAspectModelFile( stringContent, fromString.sourceModel(), fromString.headerComment(), Optional.of( file.toURI() ) );
      } catch ( final ModelResolutionException exception ) {
         if ( exception.getMessage().startsWith( "Encountered invalid encoding" ) ) {
            throw new ModelResolutionException( new ModelResolutionException.LoadingFailure(
                  file.toString(), exception.getMessage(), exception ) );
         }
         throw exception;
      } catch ( final FileNotFoundException exception ) {
         throw new ModelResolutionException( new ModelResolutionException.LoadingFailure(
               file.toString(), "File not found: " + file, exception ) );
      }
   }

   /**
    * Loads the content of an AspectModelFile from an RDF/Turtle string
    *
    * @param rdfTurtle the model in RDF/Turtle syntax
    * @return the loaded file content
    * @deprecated Use {@link #load(String, URI)} instead
    */
   @Deprecated( forRemoval = true )
   public static RawAspectModelFile load( final String rdfTurtle ) {
      return load( rdfTurtle, buildArtificialUri( rdfTurtle, "rdfturtle" ) );
   }

   /**
    * Loads the content of an AspectModelFile from an RDF/Turtle string
    *
    * @param rdfTurtle the model in RDF/Turtle syntax
    * @param sourceLocation the logical location of the file source
    * @return the loaded file content
    */
   public static RawAspectModelFile load( final String rdfTurtle, final URI sourceLocation ) {
      final List<String> headerComment = headerComment( rdfTurtle );
      final Try<Model> tryModel = TurtleLoader.loadTurtle( rdfTurtle, sourceLocation );
      if ( tryModel.isFailure() && tryModel.getCause() instanceof final ParserException parserException ) {
         throw parserException;
      }
      final Model model = tryModel.getOrElseThrow(
            () -> new ModelResolutionException( "Can not load model", tryModel.getCause() ) );
      return new RawAspectModelFile( rdfTurtle, model, headerComment, Optional.of( sourceLocation ) );
   }

   /**
    * Loads the content of an AspectModelFile from an input stream
    *
    * @param inputStream the input stream
    * @return the loaded file content
    * @deprecated Use {@link #load(InputStream, URI)}  instead
    */
   @Deprecated( forRemoval = true )
   public static RawAspectModelFile load( final InputStream inputStream ) {
      return load( inputStream, buildArtificialUri( inputStream, "inputstream" ) );
   }

   /**
    * Loads the content of an AspectModelFile from an input stream
    *
    * @param inputStream the input stream
    * @param sourceLocation the logical location of the file source
    * @return the loaded file content
    */
   public static RawAspectModelFile load( final InputStream inputStream, final URI sourceLocation ) {
      return load( content( inputStream, sourceLocation ), sourceLocation );
   }

   /**
    * Loads the content of an AspectModelFile from an input stream
    *
    * @param inputStream the input stream
    * @param sourceLocation the logical location of the file source
    * @return the loaded file content
    * @deprecated Use {@link #load(InputStream, URI)}  instead
    */
   @Deprecated( forRemoval = true )
   public static RawAspectModelFile load( final InputStream inputStream, final Optional<URI> sourceLocation ) {
      return load( inputStream, sourceLocation.orElse( buildArtificialUri( inputStream, "inputstream" ) ) );
   }

   /**
    * Loads the content of an AspectModelFile from an RDF model
    *
    * @param model the input model
    * @return the loaded file content
    * @deprecated Use {@link #load(Model, URI)} instead
    */
   @Deprecated( forRemoval = true )
   public static RawAspectModelFile load( final Model model ) {
      return load( model, buildArtificialUri( model, "model" ) );
   }

   /**
    * Loads the content of an AspectModelFile from an RDF model
    *
    * @param model the input model
    * @param sourceLocation the logical location of the file file source
    * @return the loaded file content
    */
   public static RawAspectModelFile load( final Model model, final URI sourceLocation ) {
      return new RawAspectModelFile( RdfUtil.modelToString( model ), model, List.of(), Optional.of( sourceLocation ) );
   }

   /**
    * Loads the content of an AspectModelFile from raw bytes
    *
    * @param content the file content
    * @param sourceLocation the logical location of the source file
    * @return the loaded file content
    */
   public static RawAspectModelFile load( final byte[] content, final URI sourceLocation ) {
      return load( new ByteArrayInputStream( content ), sourceLocation );
   }

   /**
    * Loads the content of an AspectModelFile from raw bytes
    *
    * @param content the file content
    * @param sourceLocation the logical location of the source file
    * @return the loaded file content
    * @deprecated Use {@link #load(byte[], URI)} instead
    */
   @Deprecated( forRemoval = true )
   public static RawAspectModelFile load( final byte[] content, final Optional<URI> sourceLocation ) {
      return load( new ByteArrayInputStream( content ), sourceLocation );
   }

   /**
    * Loads the content of an AspectModelFile from raw bytes
    *
    * @param content the file content
    * @return the loaded file content
    * @deprecated Use {@link #load(byte[], URI)} instead
    */
   @Deprecated( forRemoval = true )
   public static RawAspectModelFile load( final byte[] content ) {
      return load( new ByteArrayInputStream( content ), buildArtificialUri( content, "bytes" ) );
   }

   /**
    * Loads the content of an AspectModelFile from a URL
    *
    * @param url the source location of the file
    * @return the loaded file content
    */
   public static RawAspectModelFile load( final URL url ) {
      if ( url.getProtocol().equals( "file" ) ) {
         try {
            return load( Paths.get( url.toURI() ).toFile() );
         } catch ( final URISyntaxException exception ) {
            throw new ModelResolutionException( "Can not load model from file URL", exception );
         }
      } else if ( url.getProtocol().equals( "http" ) || url.getProtocol().equals( "https" ) ) {
         // Downloading from http(s) should take proxy settings into consideration, so we don't just .openStream() here
         final byte[] fileContent = new Download().downloadFile( url );
         return load( fileContent );
      }
      try {
         // Other URLs (e.g. resource://) we just load using openStream()
         return load( url.openStream(), Optional.of( url.toURI() ) );
      } catch ( final IOException | URISyntaxException exception ) {
         throw new ModelResolutionException( "Can not load model from URL", exception );
      }
   }

   /**
    * Loads the content of an AspectModelFile from a URI that is a valid URL
    *
    * @param uri the source location of the file
    * @return the loaded file content
    * @throws ModelResolutionException if the provided URI is no valid URL
    */
   public static RawAspectModelFile load( final URI uri ) {
      try {
         return load( uri.toURL() );
      } catch ( final MalformedURLException exception ) {
         throw new ModelResolutionException( "Can not load model from URIs that are not URLs", exception );
      }
   }

   private static String content( final InputStream inputStream, final URI sourceLocation ) {
      try {
         final byte[] bytes = inputStream.readAllBytes();
         final CharsetDecoder charsetDecoder = StandardCharsets.UTF_8.newDecoder();
         charsetDecoder.decode( ByteBuffer.wrap( bytes ) );
         return new String( bytes, StandardCharsets.UTF_8 );
      } catch ( final MalformedInputException | UnmappableCharacterException exception ) {
         throw new ModelResolutionException( new ModelResolutionException.LoadingFailure(
               sourceLocation.toString(), "Encountered invalid encoding in input", exception ) );
      } catch ( final IOException exception ) {
         throw new ModelResolutionException( new ModelResolutionException.LoadingFailure(
               sourceLocation.toString(), "Could not load content from input stream", exception ) );
      }
   }

   private static List<String> headerComment( final String content ) {
      return content.lines()
            .dropWhile( String::isBlank )
            .takeWhile( line -> line.startsWith( "#" ) )
            .map( line -> line.substring( 1 ).trim() )
            .toList();
   }

   private static URI buildArtificialUri( final Object object, final String objectType ) {
      return URI.create( "inmemory:%s:%s".formatted( objectType, object.hashCode() ) );
   }
}
