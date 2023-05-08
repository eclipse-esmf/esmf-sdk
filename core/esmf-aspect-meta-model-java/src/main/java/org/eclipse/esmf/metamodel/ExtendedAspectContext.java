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

package org.eclipse.esmf.metamodel;

import java.util.Map;

import org.eclipse.esmf.aspectmodel.resolver.services.VersionedModel;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;

/**
 * The ExtendedAspectContext wraps a loaded/resolved Aspect Model, a single Aspect that was instantiated from this model, i.e.,
 * which must be defined in the RDF model, and a list of all elements that were instantiated in the context of the given model.
 * @param rdfModel the RDF model
 * @param aspect the Aspect
 * @param loadedElements all elements instantiated in the context of the given model
 */
public record ExtendedAspectContext(VersionedModel rdfModel, Aspect aspect, Map<AspectModelUrn, ModelElement> loadedElements) {
}
