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

package org.eclipse.esmf.aspectmodel.resolver;

import java.util.function.Function;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

import io.vavr.control.Try;
import org.apache.jena.rdf.model.Model;

/**
 * Represents one way to load and resolve an Aspect model from a given source.
 */
public interface ResolutionStrategy extends Function<AspectModelUrn, Try<Model>> {
}
