/*
 * Copyright (c) 2024 Bosch Software Innovations GmbH. All rights reserved.
 */

package org.eclipse.esmf.aspectmodel.generator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.esmf.metamodel.ModelElement;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for the generation of {@link Artifact}s.
 *
 * @param <F> the focus type, e.g. Aspect or AspectModel
 * @param <I> the type that uniquely identifies the artifact in the scope of the generation process
 * @param <T> the artifact's content type, e.g. String or byte[]
 * @param <C> the config object for the genererator
 * @param <A> the type of the artifact that is generated
 */
public abstract class Generator<F, I, T, C extends GenerationConfig, A extends Artifact<I, T>> {
   private static final Logger LOG = LoggerFactory.getLogger( Generator.class );
   protected final F focus;

   @Getter
   protected final C config;
   protected final Comparator<ModelElement> uniqueByModelElementIdentifier = ( modelElementOne, modelElementTwo ) -> {
      final String modelElementOneIdentifier = modelElementOne.urn().toString();
      final String modelElementTwoIdentifier = modelElementTwo.urn().toString();
      return modelElementOneIdentifier.compareTo( modelElementTwoIdentifier );
   };

   protected Generator( final F focus, final C config ) {
      this.focus = focus;
      this.config = config;
   }

   /**
    * Generates artifacts from the given Aspect model. As this generation may produce multiple artifacts, the generator
    * provides the caller with the identifer of the respective artifact via the callback function. The caller needs to
    * provide an {@link OutputStream} for the artifact, e.g. a suitable FileOutputStream.
    *
    * @param nameMapper the callback function that maps artifact identifiers to OutputStreams
    */
   public void generate( final Function<I, OutputStream> nameMapper ) {
      generate().toList().forEach( generationResult -> write( generationResult, nameMapper ) );
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
            .orElseThrow( () -> new GenerationException( "Could not generate artifact" ) );
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
   protected void write( final Artifact<I, T> artifact, final Function<I, OutputStream> nameMapper ) {
      try ( final OutputStream output = nameMapper.apply( artifact.getId() ) ) {
         output.write( artifact.serialize() );
         output.flush();
      } catch ( final IOException exception ) {
         LOG.error( "Failure during writing of generated artifact", exception );
      }
   }
}
