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

import java.util.Optional;

/**
 * SQL column definition
 *
 * @param <C> type for column datatypes
 * @param <D> the self type
 */
public interface SqlColumnDefinition<
      D extends SqlType,
      C extends SqlColumnDefinition<D, C>> {
   String name();

   <R extends D> R type();

   boolean nullable();

   Optional<String> comment();
}
