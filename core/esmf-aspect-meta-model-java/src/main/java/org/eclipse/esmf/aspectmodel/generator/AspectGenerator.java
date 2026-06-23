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

import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.visitor.AspectStreamTraversalVisitor;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.ModelElement;

/**
 * Base class for the generation of {@link Artifact}s from a single {@link Aspect}.
 *
 * @param <I> the type that uniquely identifies the artifact in the scope of the generation process
 * @param <T> the artifact's content type, e.g. String or byte[]
 * @param <C> the config object for the generator
 * @param <A> the type of the artifact that is generated
 */
public abstract class AspectGenerator<I, T, C extends GenerationConfig, A extends Artifact<I, T>>
      extends StructureElementGenerator<Aspect, I, T, C, A> {
   protected AspectGenerator( final Aspect aspect, final C config ) {
      super( aspect, config );
   }

   protected Aspect aspect() {
      return getFocus();
   }

   protected <E extends ModelElement> Stream<E> elements( final Class<E> clazz ) {
      return aspect().accept( new AspectStreamTraversalVisitor(), null )
            .filter( clazz::isInstance )
            .map( clazz::cast )
            .sorted( uniqueByModelElementIdentifier )
            .distinct();
   }

   protected <E extends ModelElement> Stream<A> applyArtifactGenerator(
         final Class<E> clazz, final ArtifactGenerator<I, T, E, C, A> artifactGenerator, final C config ) {
      return elements( clazz ).map( element -> artifactGenerator.apply( element, config ) );
   }
}
