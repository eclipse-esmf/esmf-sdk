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

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The definition of a relational table
 *
 * @param <D> the type of the column datatypes
 * @param <C> the column definition type
 * @param <X> the constraint type
 */
public interface SqlTableDefinition<
      D extends SqlType,
      C extends SqlColumnDefinition<D, C>,
      X extends SqlConstraint> {
   String name();

   List<C> columns();

   Optional<String> comment();

   List<X> tableConstraints();

   Map<String, SqlValue<? extends D>> tableProperties();
}
