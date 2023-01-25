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

package io.openmanufacturing.sds.metamodel;

import io.openmanufacturing.sds.aspectmodel.resolver.services.VersionedModel;

/**
 * The AspectContext wraps a loaded/resolved Aspect Model and a single Aspect that was instantiated from this model, i.e.,
 * which must be defined in the RDF model.
 * @param rdfModel the RDF model
 * @param aspect the Aspect
 */
public record AspectContext(VersionedModel rdfModel, Aspect aspect) {
}
