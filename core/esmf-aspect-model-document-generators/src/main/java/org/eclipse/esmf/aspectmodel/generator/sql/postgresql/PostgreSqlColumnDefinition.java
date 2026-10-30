/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

package org.eclipse.esmf.aspectmodel.generator.sql.postgresql;

import java.util.Optional;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Represents a column definition in PostgreSQL.
 *
 * @param name the name of the column
 * @param type the type of the column
 * @param nullable whether the column is nullable
 * @param comment an optional comment for the column
 */
@RecordBuilder
public record PostgreSqlColumnDefinition(
      String name,
      PostgreSqlType type,
      boolean nullable,
      Optional<String> comment
) {
   @Override
   public String toString() {
      return "%s %s%s".formatted( name(), type(), nullable ? "" : " NOT NULL" );
   }
}
