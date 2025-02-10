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

import java.nio.charset.StandardCharsets;

/**
 * Represents an artifact during generation, for example a single generated class.
 *
 * @param <I> the type that uniquely identifies the artifact in the scope of the generation process
 * @param <T> the content type, e.g. String or byte[]
 */
public interface Artifact<I, T> {
   I getId();

   T getContent();

   default byte[] serialize() {
      return getContent().toString().getBytes( StandardCharsets.UTF_8 );
   }
}
