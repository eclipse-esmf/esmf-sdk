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

import java.util.List;
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
 * @param customColumns custom columns to add to the table
 */
@RecordBuilder
public record DatabricksSqlGenerationConfig(
      String createTableCommandPrefix,
      boolean includeTableComment,
      boolean includeColumnComments,
      Locale commentLanguage,
      int decimalPrecision,
      int decimalScale,
      List<DatabricksColumnDefinition> customColumns
) implements SqlGenerationConfig.DialectSpecificConfig {
   public static final String DEFAULT_TABLE_COMMAND_PREFIX = "CREATE TABLE IF NOT EXISTS";
   // As defined in https://docs.databricks.com/en/sql/language-manual/data-types/decimal-type.html
   public static final int DECIMAL_DEFAULT_PRECISION = 10;
   // As defined in https://docs.databricks.com/en/sql/language-manual/data-types/decimal-type.html
   public static final int DECIMAL_MAX_PRECISION = 38;
   // As defined in https://docs.databricks.com/en/sql/language-manual/data-types/decimal-type.html
   public static final int DECIMAL_DEFAULT_SCALE = 0;
   public static final boolean DEFAULT_INCLUDE_TABLE_COMMENT = true;
   public static final boolean DEFAULT_INCLUDE_COLUMN_COMMENTS = true;
   public static final Locale DEFAULT_COMMENT_LANGUAGE = Locale.ENGLISH;

   public DatabricksSqlGenerationConfig() {
      this( DEFAULT_TABLE_COMMAND_PREFIX, DEFAULT_INCLUDE_TABLE_COMMENT, DEFAULT_INCLUDE_COLUMN_COMMENTS, DEFAULT_COMMENT_LANGUAGE,
            DECIMAL_DEFAULT_PRECISION, DECIMAL_DEFAULT_SCALE, List.of() );
   }

   public DatabricksSqlGenerationConfig {
      if ( createTableCommandPrefix == null ) {
         createTableCommandPrefix = DEFAULT_TABLE_COMMAND_PREFIX;
      }
      if ( decimalPrecision <= 0 ) {
         decimalPrecision = DECIMAL_DEFAULT_PRECISION;
      }
      if ( decimalPrecision > DECIMAL_MAX_PRECISION ) {
         decimalPrecision = DECIMAL_MAX_PRECISION;
      }
      if ( decimalScale < 0 ) {
         decimalScale = DECIMAL_DEFAULT_SCALE;
      }
      if ( decimalScale > decimalPrecision ) {
         decimalScale = decimalPrecision;
      }
      if ( commentLanguage == null ) {
         commentLanguage = DEFAULT_COMMENT_LANGUAGE;
      }
      if ( customColumns == null ) {
         customColumns = List.of();
      }
   }
}
