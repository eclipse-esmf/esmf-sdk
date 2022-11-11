/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.generator;

import java.util.function.BiFunction;

import io.openmanufacturing.sds.metamodel.ModelElement;

/**
 * A generator that can take an Aspect model element (e.g. an Aspect or an Enumeration) as input and generate an {@link
 * Artifact} for it.
 *
 * @param <I> the type that uniquely identifies the artifact in the scope of the generation process
 * @param <T> the content type of the artifact, e.g. String or byte[]
 * @param <E> the Aspect model element type this generator handles
 * @param <C> the configuration for the generation
 * @param <R> the result type
 */
public interface ArtifactGenerator<I, T, E extends ModelElement, C extends GenerationConfig, R extends Artifact<I, T>>
      extends BiFunction<E, C, R> {
}
