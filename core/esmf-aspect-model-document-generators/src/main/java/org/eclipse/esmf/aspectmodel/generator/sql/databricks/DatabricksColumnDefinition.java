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

import org.eclipse.esmf.aspectmodel.generator.sql.SqlColumnDefinition;

import org.jspecify.annotations.NonNull;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Represents a column definition in Databricks SQL.
 *
 * @param name The name of the column
 * @param type The type of the column
 * @param nullable Whether the column is nullable
 * @param comment An optional comment for the column
 */
@RecordBuilder
public record DatabricksColumnDefinition(
      String name,
      DatabricksType type,
      boolean nullable,
      Optional<String> comment
) implements SqlColumnDefinition<DatabricksType, DatabricksColumnDefinition> {
   @Override
   public @NonNull String toString() {
      final StringBuilder builder = new StringBuilder();
      builder.append( name() ).append( " " ).append( type() );
      if ( !nullable ) {
         builder.append( " NOT NULL" );
      }
      comment.ifPresent( theComment -> builder.append( " " ).append( new DatabricksCommentDefinition( theComment ) ) );
      return builder.toString();
   }
}
