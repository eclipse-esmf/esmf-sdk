/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.metamodel.impl;

import java.io.InputStream;
import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.esmf.metamodel.ModelInput;

public record DefaultModelInput(
      Supplier<InputStream> contentProvider,
      Optional<URI> location
) implements ModelInput {}
