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

import java.util.Locale;

import org.eclipse.esmf.aspectmodel.generator.sql.SqlGenerationConfig;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Configuration specific to the Databricks denormalized SQL generation.
 *
 * @param createTableCommandPrefix the command for table creation, default is "CREATE TABLE IF NOT EXISTS"
 * @param includeTableComment whether to include a comment for the table
 * @param includeColumnComments whether to include comments for the columns
 * @param commentLanguage the language to use for comments
 * @param decimalPrecision the precision to use for decimal columns, see <a
 * href="https://docs.databricks.com/en/sql/language-manual/data-types/decimal-type.html">DECIMAL type</a> for more info.
 */
@RecordBuilder
public record DatabricksSqlGenerationConfig(
      String createTableCommandPrefix,
      boolean includeTableComment,
      boolean includeColumnComments,
      Locale commentLanguage,
      int decimalPrecision
) implements SqlGenerationConfig.DialectSpecificConfig {
   public DatabricksSqlGenerationConfig() {
      this( null, true, true, Locale.ENGLISH, 10 );
   }

   public DatabricksSqlGenerationConfig {
      if ( createTableCommandPrefix == null ) {
         createTableCommandPrefix = "CREATE TABLE IF NOT EXISTS";
      }
      if ( decimalPrecision <= 0 ) {
         decimalPrecision = 10;
      }
      if ( decimalPrecision > 38 ) {
         decimalPrecision = 38;
      }
   }
}
