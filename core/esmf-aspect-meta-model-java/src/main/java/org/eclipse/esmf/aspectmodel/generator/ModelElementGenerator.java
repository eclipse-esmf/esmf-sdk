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

package org.eclipse.esmf.aspectmodel.generator;

import org.eclipse.esmf.metamodel.ModelElement;

/**
 * Specific type of {@link Generator} that takes model elements as inputs
 *
 * @param <E> the model element type
 * @param <I> the type that uniquely identifies the artifact in the scope of the generation process
 * @param <T> the artifact's content type, e.g. String or byte[]; the "output" type of the generation function
 * @param <C> the config object for the generator
 * @param <A> the type of the artifact that is generated
 */
public abstract class ModelElementGenerator<E extends ModelElement, I, T, C extends GenerationConfig, A extends Artifact<I, T>>
      extends Generator<E, I, T, C, A> {
   public ModelElementGenerator( final E modelElement, final C config ) {
      super( modelElement, config );
   }

   protected ModelElement modelElement() {
      return getFocus();
   }
}
