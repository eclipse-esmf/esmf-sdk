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

import org.eclipse.esmf.aspectmodel.generator.sql.SqlConstraint;

import org.jspecify.annotations.NonNull;

public sealed interface DatabricksConstraint extends SqlConstraint {
   record GenericConstraint(
         String constraint
   ) implements DatabricksConstraint, TableConstraint {
      @Override
      public @NonNull String toString() {
         return constraint;
      }
   }

   record PrimaryKey(
         String column
   ) implements DatabricksConstraint, ColumnConstraint {
      @Override
      public @NonNull String toString() {
         return "PRIMARY KEY";
      }
   }

   record ForeignKey(
         String ownColumn,
         String foreignTable,
         String foreignColumn
   ) implements DatabricksConstraint, TableConstraint {
      @Override
      public @NonNull String toString() {
         return "FOREIGN KEY (" + ownColumn + ") REFERENCES " + foreignTable + "(" + foreignColumn + ")";
      }
   }

   record Unique(
         String name,
         String column
   ) implements DatabricksConstraint, TableConstraint {
      @Override
      public @NonNull String toString() {
         return "CONSTRAINT " + name + " UNIQUE (" + column + ")";
      }
   }
}
