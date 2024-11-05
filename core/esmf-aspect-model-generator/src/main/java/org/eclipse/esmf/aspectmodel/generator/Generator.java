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

package org.eclipse.esmf.aspectmodel.generator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.visitor.AspectStreamTraversalVisitor;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.ModelElement;

/**
 * Base class for the generation of {@link Artifact}s.
 *
 * @param <I> the type that uniquely identifies the artifact in the scope of the generation process
 * @param <T> the artifact's content type, e.g. String or byte[]
 * @param <C> the config object for the genererator
 */
public abstract class Generator<I, T, C extends GenerationConfig, A extends Artifact<I, T>> {
   protected final C config;
   protected final Aspect aspect;
   protected final Comparator<ModelElement> uniqueByModelElementIdentifier = ( modelElementOne, modelElementTwo ) -> {
      final String modelElementOneIdentifier = modelElementOne.urn().toString();
      final String modelElementTwoIdentifier = modelElementTwo.urn().toString();
      return modelElementOneIdentifier.compareTo( modelElementTwoIdentifier );
   };

   protected Generator( final Aspect aspect, final C config ) {
      this.aspect = aspect;
      this.config = config;
   }

   public C getConfig() {
      return config;
   }

   protected <E extends ModelElement> Stream<E> elements( final Class<E> clazz ) {
      return aspect.accept( new AspectStreamTraversalVisitor(), null )
            .filter( clazz::isInstance )
            .map( clazz::cast )
            .sorted( uniqueByModelElementIdentifier )
            .distinct();
   }

   protected <E extends ModelElement> Stream<A> applyTemplate(
         final Class<E> clazz, final ArtifactGenerator<I, T, E, C, A> artifactGenerator, final C config ) {
      return elements( clazz ).map( element -> artifactGenerator.apply( element, config ) );
   }

   protected void writeCharSequenceToOutputStream( final CharSequence charSequence, final OutputStream outputStream )
         throws IOException {
      try ( final Writer writer = new OutputStreamWriter( outputStream, StandardCharsets.UTF_8 ) ) {
         for ( int i = 0; i < charSequence.length(); i++ ) {
            writer.write( charSequence.charAt( i ) );
         }
         writer.flush();
      }
   }

   /**
    * Generates artifacts from the given Aspect model. As this generation may produce multiple artifacts, the generator
    * provides the caller with the identifer of the respective artifact via the callback function. The caller needs to
    * provide an {@link OutputStream} for the artifact, e.g. a suitable FileOutputStream.
    *
    * @param nameMapper the callback function that maps artifact identifiers to OutputStreams
    */
   public void generate( final Function<I, OutputStream> nameMapper ) {
      final List<A> artifacts = generate().toList();
      artifacts.forEach( generationResult -> write( generationResult, nameMapper ) );
   }

   /**
    * Generates artifacts for the given Aspect model
    *
    * @return the stream of artifacts
    */
   public abstract Stream<A> generate();

   /**
    * Assumes that the generator returns exactly one artifact and returns this
    *
    * @return the generated artifact
    */
   public A singleResult() {
      return generate()
            .findFirst()
            .orElseThrow( () -> new GenerationException( "Could not generate artifact for " + aspect.getName() ) );
   }

   /**
    * Assumes that the generator returns exactly one artifact and returns its content
    *
    * @return the artifact's content
    */
   public T getContent() {
      return singleResult().getContent();
   }

   /**
    * Writes an artifact to the corresponding output stream
    *
    * @param artifact the artifact
    * @param nameMapper the function that provides the output stream for the artifact
    */
   protected abstract void write( final Artifact<I, T> artifact, final Function<I, OutputStream> nameMapper );
}
