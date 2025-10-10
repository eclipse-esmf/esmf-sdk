/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.serializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFile;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.RawAspectModelFileBuilder;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.ModelElement;
import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SimpleRdfNamespace;

import org.apache.jena.rdf.model.Model;

/**
 * Functions to write Aspect Models and Aspect Model files to Strings or their respective source locations
 */
public class AspectSerializer {
   public static final AspectSerializer INSTANCE = new AspectSerializer();
   private final Map<String, Function<URI, OutputStream>> protocolHandlers = new HashMap<>();

   protected AspectSerializer() {
      registerProtocolHandler( "file", this::createFileOutputStream );
   }

   private OutputStream createFileOutputStream( final URI outputUri ) {
      try {
         return new FileOutputStream( new File( outputUri ) );
      } catch ( final FileNotFoundException exception ) {
         throw new SerializationException( exception );
      }
   }

   /**
    * Serializes all files of an Aspect Model to their respective source locations.
    * <b>Attention:</b> This method does not check validity of paths or existance of files and will overwrite without further checks.
    *
    * @param aspectModel the Aspect Model
    */
   public void write( final AspectModel aspectModel ) {
      aspectModel.files().forEach( this::write );
   }

   /**
    * Returns a URL for the Aspect Model file if it can be determined.
    *
    * @param aspectModelFile the input Aspect Model file
    * @return the Aspect Model file's location as URL
    * @throws SerializationException if the file has no source location or the source location URI is no URL
    */
   public URL aspectModelFileUrl( final AspectModelFile aspectModelFile ) {
      if ( aspectModelFile.sourceLocation().isEmpty() ) {
         throw new SerializationException( "Aspect Model file has no source location" );
      }

      final URI uri = aspectModelFile.sourceLocation().get();
      final URL url;
      try {
         url = uri.toURL();
      } catch ( final MalformedURLException exception ) {
         throw new SerializationException( "Aspect Model file can only be written to locations given by URLs" );
      }

      return url;
   }

   /**
    * Writes the content of an Aspect Model file to its defined source location
    *
    * @param aspectModelFile the Aspect Model file
    * @throws SerializationException if writing the file failed
    */
   public void write( final AspectModelFile aspectModelFile ) {
      final URL url = aspectModelFileUrl( aspectModelFile );
      final Function<URI, OutputStream> protocolHandler = protocolHandlers.get( url.getProtocol() );
      if ( protocolHandler == null ) {
         throw new SerializationException( "Don't know how to write " + url.getProtocol() + " URLs: " + url );
      }

      final String content = aspectModelFileToString( aspectModelFile );
      try ( final OutputStream out = protocolHandler.apply( aspectModelFile.sourceLocation().orElseThrow( () ->
            new SerializationException( "Can not write Aspect Model File: No source location is set"
                  + aspectModelFile.filename().map( name -> ": " + name ).orElse( "" ) ) ) ) ) {
         out.write( content.getBytes( StandardCharsets.UTF_8 ) );
      } catch ( final IOException exception ) {
         throw new SerializationException( exception );
      }
   }

   public void registerProtocolHandler( final String protocol, final Function<URI, OutputStream> protocolHandler ) {
      protocolHandlers.put( protocol, protocolHandler );
   }

   /**
    * Serializes an Aspect and the elements in the same file
    *
    * @param aspect the Aspect
    * @return the String representation in RDF/Turtle
    */
   public String aspectToString( final Aspect aspect ) {
      return modelElementToString( aspect );
   }

   /**
    * Serializes a model element and the elements it depends on
    *
    * @param element the model element
    * @return the String representation in RDF/Turtle
    */
   public String modelElementToString( final ModelElement element ) {
      if ( element.getSourceFile() != null ) {
         return aspectModelFileToString( element.getSourceFile() );
      }

      // The element has no source file, it was probably created programmatically.
      // Construct a virtual AspectModelFile with an RDF representation that can be serialized.

      final RdfNamespace namespace = new SimpleRdfNamespace( "", element.urn().getUrnPrefix() );
      final Model rdfModel = element.accept( new RdfModelCreatorVisitor( namespace ), null ).model();
      RdfUtil.cleanPrefixes( rdfModel );
      final AspectModel aspectModel = new AspectModelLoader().loadAspectModelFiles(
            List.of( RawAspectModelFileBuilder.builder().sourceModel( rdfModel ).build() ) );
      final AspectModelFile newSourceFile = aspectModel.files().getFirst();
      return aspectModelFileToString( newSourceFile );
   }

   /**
    * Serializes an Aspect Model file
    *
    * @param aspectModelFile the Aspect Model file
    * @return the String representation in RDF/Turtle
    */
   public String aspectModelFileToString( final AspectModelFile aspectModelFile ) {
      if ( aspectModelFile instanceof final RawAspectModelFile rawAspectModelFile ) {
         return rawAspectModelFile.sourceRepresentation();
      }
      final StringWriter stringWriter = new StringWriter();
      try ( final PrintWriter printWriter = new PrintWriter( stringWriter ) ) {
         final PrettyPrinter prettyPrinter = new PrettyPrinter( aspectModelFile, printWriter );
         prettyPrinter.print();
      }
      return stringWriter.toString();
   }
}
