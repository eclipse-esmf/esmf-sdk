/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.sql.databricks;

import org.eclipse.esmf.aspectmodel.generator.sql.SqlValue;

import org.jspecify.annotations.NonNull;
import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record DatabricksSqlValue<T extends DatabricksType>(
      T type,
      Object value
) implements SqlValue<T> {
   @Override
   public @NonNull String toString() {
      return type == DatabricksType.STRING ? "'" + value + "'" : value.toString();
   }
}
