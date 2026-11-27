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

package org.eclipse.esmf.aspectmodel.generator.sql;

/**
 * Represents a SQL constraint
 */
public interface SqlConstraint {
   /**
    * A table constraint refers to a table
    */
   interface TableConstraint extends SqlConstraint {
   }

   /**
    * A column constraint refers to a given column in a table
    */
   interface ColumnConstraint extends SqlConstraint {
      String column();
   }
}
