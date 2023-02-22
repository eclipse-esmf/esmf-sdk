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

import java.io.OutputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.NamedElement;
import org.eclipse.esmf.metamodel.visitor.AspectStreamTraversalVisitor;

/**
 * Base class for the generation of {@link Artifact}s.
 *
 * @param <I> the type that uniquely identifies the artifact in the scope of the generation process
 * @param <T> the artifact's content type, e.g. String or byte[]
 */
public abstract class Generator<I, T> {
   protected final Aspect aspectModel;
   private static final Map<Object, String> generatedModelElementIdentifiers = new HashMap<>();

   protected Generator( final Aspect aspectModel ) {
      this.aspectModel = aspectModel;
   }

   private static Comparator<NamedElement> uniqueByModelElementIdentifier() {
      return ( modelElementOne, modelElementTwo ) -> {
         final String modelElementOneIdentifier = modelElementOne
               .getAspectModelUrn()
               .map( aspectModelUrn -> aspectModelUrn.getUrn().toString() )
               .orElse( generateIdentifierForAnonymousModelElement( modelElementOne ) );
         final String modelElementTwoIdentifier = modelElementTwo
               .getAspectModelUrn()
               .map( aspectModelUrn -> aspectModelUrn.getUrn().toString() )
               .orElse( generateIdentifierForAnonymousModelElement( modelElementTwo ) );

         return modelElementOneIdentifier.compareTo( modelElementTwoIdentifier );
      };
   }

   private static String generateIdentifierForAnonymousModelElement( final Object modelElement ) {
      return generatedModelElementIdentifiers.computeIfAbsent( modelElement, element ->
            "GeneratedElementId_" + generatedModelElementIdentifiers.size() );
   }

   protected <E extends NamedElement> Stream<E> elements( final Class<E> clazz ) {
      return aspectModel.accept( new AspectStreamTraversalVisitor(), null )
                        .filter( clazz::isInstance )
                        .map( clazz::cast )
                        .sorted( uniqueByModelElementIdentifier() )
                        .distinct();
   }

   protected <E extends NamedElement, C extends GenerationConfig, R extends Artifact<I, T>> Stream<R> applyTemplate(
         final Class<E> clazz, final ArtifactGenerator<I, T, E, C, R> artifactGenerator, final C config ) {
      return elements( clazz ).map( element -> artifactGenerator.apply( element, config ) );
   }

   /**
    * Generates artifacts from the given Aspect model. As this generation may produce multiple artifacts, the generator
    * provides the caller with the identifer of the respective artifact via the callback function. The caller needs to
    * provide an {@link OutputStream} for the artifact, e.g. a suitable FileOutputStream.
    *
    * @param nameMapper the callback function that maps artifact identifiers to OutputStreams
    */
   public void generate( final Function<I, OutputStream> nameMapper ) {
      generateArtifacts().forEach( generationResult -> write( generationResult, nameMapper ) );
   }

   /**
    * Generates artifacts for the given Aspect model
    *
    * @return the stream of artifacts
    */
   protected abstract Stream<Artifact<I, T>> generateArtifacts();

   /**
    * Writes an artifact to the corresponding output stream
    *
    * @param artifact the artifact
    * @param nameMapper the function that provides the output stream for the artifact
    */
   protected abstract void write( final Artifact<I, T> artifact, final Function<I, OutputStream> nameMapper );
}
