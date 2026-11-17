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
import java.util.Locale;
import java.util.Map;

import org.eclipse.esmf.aspectmodel.generator.GenerationConfig;

public interface AspectSqlGenerationConfig<D, C extends SqlColumnDefinition<D, C>> extends GenerationConfig {
   enum MappingStrategy {
      DENORMALIZED,
      NORMALIZED
   }

   MappingStrategy mappingStrategy();

   String createTableCommandPrefix();

   boolean includeTableComment();

   boolean includeColumnComments();

   Locale commentLanguage();

   int decimalPrecision();

   int decimalScale();

   List<C> customColumns();

   Map<String, List<C>> customColumnsPerTable();

   List<String> tableConstraints();

   Map<String, List<String>> tableConstraintsPerTable();
}
