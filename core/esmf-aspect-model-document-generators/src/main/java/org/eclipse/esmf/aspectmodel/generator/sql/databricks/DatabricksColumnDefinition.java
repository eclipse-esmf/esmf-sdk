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

package org.eclipse.esmf.aspectmodel.generator.sql.databricks;

import java.util.Optional;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Represents a column definition in Databricks SQL.
 *
 * @param name The name of the column.
 * @param type The type of the column.
 * @param nullable Whether the column is nullable.
 * @param comment An optional comment for the column.
 */
@RecordBuilder
public record DatabricksColumnDefinition(
      String name,
      DatabricksType type,
      boolean nullable,
      Optional<String> comment
) {
   @Override
   public String toString() {
      return "%s %s%s%s".formatted(
            name(),
            type(),
            nullable ? "" : " NOT NULL",
            comment.map( theComment -> " " + new DatabricksCommentDefinition( theComment ) ).orElse( "" ) );
   }
}
