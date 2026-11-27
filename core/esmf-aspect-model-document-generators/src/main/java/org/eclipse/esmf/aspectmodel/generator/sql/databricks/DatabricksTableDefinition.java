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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.generator.sql.SqlTableDefinition;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlValue;

public record DatabricksTableDefinition(
      String name,
      List<DatabricksColumnDefinition> columns,
      Optional<String> comment,
      List<DatabricksConstraint> tableConstraints,
      Map<String, SqlValue<? extends DatabricksType>> tableProperties
) implements SqlTableDefinition<DatabricksType, DatabricksColumnDefinition, DatabricksConstraint> {}
