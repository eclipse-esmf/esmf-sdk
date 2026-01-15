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

package org.eclipse.esmf.aspectmodel.generator.ts;

import java.util.List;

import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.characteristic.StructuredValue;

/**
 * Encapsulates a {@link Property} and, if it uses a {@link StructuredValue} characteristic, the corresponding
 * deconstruction rule and the referenced Properties
 */
public record DeconstructionSet(
      Property originalProperty,
      String deconstructionRule,
      List<Property> elementProperties
) {}
