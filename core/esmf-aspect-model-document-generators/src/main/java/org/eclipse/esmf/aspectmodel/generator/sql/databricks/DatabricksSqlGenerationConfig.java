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
import java.util.Map;

import org.eclipse.esmf.aspectmodel.generator.sql.SqlGenerationConfig;

import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Configuration specific to the Databricks denormalized SQL generation.
 *
 * @param createTableCommandPrefix the command for table creation, default is "CREATE TABLE IF NOT
 *        EXISTS"
 * @param includeTableComment whether to include a comment for the table
 * @param includeColumnComments whether to include comments for the columns
 * @param commentLanguage the language to use for comments
 * @param decimalPrecision the precision to use for decimal columns, see <a href=
 *        "https://docs.databricks.com/en/sql/language-manual/data-types/decimal-type.html">DECIMAL
 *        type</a> for more info.
 * @param decimalScale the scale to use for decimal columns, see <a href=
 *        "https://docs.databricks.com/en/sql/language-manual/data-types/decimal-type.html">DECIMAL
 *        type</a> for more info.
 * @param flattenScalarCollections whether to flatten scalar collections into a single column
 * @param customColumns custom columns to add to the table (for
 *        {@link SqlGenerationConfig.MappingStrategy#DENORMALIZED})
 * @param customColumnsPerTable custom columns to add to each table, indexed by table name (for
 *        {@link SqlGenerationConfig.MappingStrategy#NORMALIZED})
 * @param tableConstraints SQL table constraints to add to the table (for
 *        {@link SqlGenerationConfig.MappingStrategy#DENORMALIZED})
 * @param tableConstraintsPerTable SQL table constraints to add to each table, indexed by table name
 *        (for {@link SqlGenerationConfig.MappingStrategy#NORMALIZED})
 */
@RecordBuilder
public record DatabricksSqlGenerationConfig(
      String createTableCommandPrefix,
      Boolean includeTableComment,
      Boolean includeColumnComments,
      Locale commentLanguage,
      int decimalPrecision,
      int decimalScale,
      Boolean flattenScalarCollections,
      List<DatabricksColumnDefinition> customColumns,
      Map<String, List<DatabricksColumnDefinition>> customColumnsPerTable,
      List<DatabricksConstraint> tableConstraints,
      Map<String, List<DatabricksConstraint>> tableConstraintsPerTable
) implements SqlGenerationConfig.DialectSpecificConfig<DatabricksType, DatabricksColumnDefinition, DatabricksConstraint> {
   public static final String DEFAULT_TABLE_COMMAND_PREFIX = "CREATE TABLE IF NOT EXISTS";
   // As defined in https://docs.databricks.com/en/sql/language-manual/data-types/decimal-type.html
   public static final int DECIMAL_DEFAULT_PRECISION = 10;
   // As defined in https://docs.databricks.com/en/sql/language-manual/data-types/decimal-type.html
   public static final int DECIMAL_MAX_PRECISION = 38;
   // As defined in https://docs.databricks.com/en/sql/language-manual/data-types/decimal-type.html
   public static final int DECIMAL_DEFAULT_SCALE = 0;
   public static final boolean DEFAULT_INCLUDE_TABLE_COMMENT = true;
   public static final boolean DEFAULT_INCLUDE_COLUMN_COMMENTS = true;
   public static final boolean DEFAULT_FLATTEN_SCALAR_COLLECTIONS = false;
   public static final Locale DEFAULT_COMMENT_LANGUAGE = Locale.ENGLISH;

   public DatabricksSqlGenerationConfig {
      if ( createTableCommandPrefix == null ) {
         createTableCommandPrefix = DEFAULT_TABLE_COMMAND_PREFIX;
      }
      if ( includeTableComment == null ) {
         includeTableComment = DEFAULT_INCLUDE_TABLE_COMMENT;
      }
      if ( includeColumnComments == null ) {
         includeColumnComments = DEFAULT_INCLUDE_COLUMN_COMMENTS;
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
      if ( flattenScalarCollections == null ) {
         flattenScalarCollections = DEFAULT_FLATTEN_SCALAR_COLLECTIONS;
      }
      if ( tableConstraints == null ) {
         tableConstraints = List.of();
      }
      if ( tableConstraintsPerTable == null ) {
         tableConstraintsPerTable = Map.of();
      }
      if ( customColumns == null ) {
         customColumns = List.of();
      }
      if ( customColumnsPerTable == null ) {
         customColumnsPerTable = Map.of();
      }
   }
}
