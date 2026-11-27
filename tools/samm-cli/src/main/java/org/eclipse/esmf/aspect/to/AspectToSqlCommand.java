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

package org.eclipse.esmf.aspect.to;

import static org.eclipse.esmf.aspectmodel.StreamUtil.asMap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.esmf.AbstractCommand;
import org.eclipse.esmf.LoggingMixin;
import org.eclipse.esmf.ResolverConfigurationMixin;
import org.eclipse.esmf.aspect.AspectToCommand;
import org.eclipse.esmf.aspectmodel.generator.sql.AspectModelSqlGenerator;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlArtifact;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksColumnDefinition;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksColumnDefinitionParser;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksConstraint;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.generator.sql.postgresql.PostgreSqlColumnDefinition;
import org.eclipse.esmf.aspectmodel.generator.sql.postgresql.PostgreSqlColumnDefinitionParser;
import org.eclipse.esmf.aspectmodel.generator.sql.postgresql.PostgreSqlConstraint;
import org.eclipse.esmf.aspectmodel.generator.sql.postgresql.PostgreSqlGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.sql.postgresql.PostgreSqlGenerationConfigBuilder;
import org.eclipse.esmf.exception.CommandException;
import org.eclipse.esmf.metamodel.Aspect;

import picocli.CommandLine;

@CommandLine.Command(
   name = AspectToSqlCommand.COMMAND_NAME,
   description = "Generate SQL table creation script for an Aspect Model",
   descriptionHeading = "%n@|bold Description|@:%n%n",
   parameterListHeading = "%n@|bold Parameters|@:%n",
   optionListHeading = "%n@|bold Options|@:%n" )
public class AspectToSqlCommand extends AbstractCommand {
   public static final String COMMAND_NAME = "sql";

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
      names = { "--output", "-o" },
      description = "Output file path" )
   private String outputFilePath = "-";

   @CommandLine.Option(
      names = { "--dialect", "-d" },
      description = "The SQL dialect to generate for (default: ${DEFAULT-VALUE})" )
   private SqlGenerationConfig.Dialect dialect = SqlGenerationConfig.Dialect.DATABRICKS;

   @CommandLine.Option(
      names = { "--mapping-strategy", "-s" },
      description = "The mapping strategy to use (default: ${DEFAULT-VALUE})" )
   private SqlGenerationConfig.MappingStrategy strategy = SqlGenerationConfig.MappingStrategy.DENORMALIZED;

   @CommandLine.Option(
      names = { "--language", "-l" },
      description = "The language from the model for which comments should be generated (default: ${DEFAULT-VALUE})" )
   private String language = DatabricksSqlGenerationConfig.DEFAULT_COMMENT_LANGUAGE.getLanguage();

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
      names = { "--include-table-comment", "-tc" },
      description = "Include table comment in the generated SQL script (default: ${DEFAULT-VALUE})" )
   private boolean includeTableComment = DatabricksSqlGenerationConfig.DEFAULT_INCLUDE_TABLE_COMMENT;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
      names = { "--include-column-comments", "-cc" },
      description = "Include column comments in the generated SQL script (default: ${DEFAULT-VALUE})" )
   private boolean includeColumnComments = DatabricksSqlGenerationConfig.DEFAULT_INCLUDE_COLUMN_COMMENTS;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
      names = { "--table-command-prefix", "-tcp" },
      description = "The prefix to use for table creation commands (default: ${DEFAULT-VALUE})" )
   private String tableCommandPrefix = DatabricksSqlGenerationConfig.DEFAULT_TABLE_COMMAND_PREFIX;

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
      names = { "--decimal-precision", "-dp" },
      description = "The precision to use for decimal columns (default: ${DEFAULT-VALUE})" )
   private int decimalPrecision = DatabricksSqlGenerationConfig.DECIMAL_DEFAULT_PRECISION;

   @CommandLine.Option(
      names = { "--decimal-scale", "-ds" },
      description = "The scale to use for decimal columns, between 0 and the value of precision. (default: ${DEFAULT-VALUE})" )
   private int decimalScale = DatabricksSqlGenerationConfig.DECIMAL_DEFAULT_SCALE;

   @CommandLine.Option(
      names = { "--flatten-scalar-collections" },
      description = "Properties with scalar collections are turned to a single column with array type instead of separate table. "
            + "Used in normalized mapping. (default: ${DEFAULT-VALUE})" )
   private boolean flattenScalarCollections = DatabricksSqlGenerationConfig.DEFAULT_FLATTEN_SCALAR_COLLECTIONS;

   @CommandLine.Option(
      names = { "--custom-column", "-col" },
      description = "Custom column to add to the table, can be repeated for multiple columns. Used in denormalized mapping." )
   private List<String> customColumns = List.of();

   @CommandLine.Option(
      names = { "--custom-columns-for-table" },
      description = "Custom column(s) to add to the given table. Separate multiple columns using ';'. Used in normalized mapping." )
   private Map<String, String> customColumnsForTable = Map.of();

   @CommandLine.Option(
      names = { "--table-constraint" },
      description = "SQL table constraint to add, repeatable for multiple constraints. Used in denormalized mapping." )
   private List<String> tableConstraints = List.of();

   @CommandLine.Option(
      names = { "--table-constraints-for-table" },
      description = "SQL table constraint(s) to add to the given table. Separate multiple constraints using ';'. "
            + "Used in normalized mapping." )
   private Map<String, String> tableConstraintsForTable = Map.of();

   @SuppressWarnings( "FieldCanBeLocal" )
   @CommandLine.Option(
      names = { "--details" },
      description = "Print detailed reports on errors" )
   private boolean details = false;

   @CommandLine.ParentCommand
   private AspectToCommand parentCommand;

   @CommandLine.Mixin
   private ResolverConfigurationMixin resolverConfiguration;

   @CommandLine.Mixin
   private LoggingMixin loggingMixin;

   @Override
   public void run() {
      setDetails( details );
      setResolverConfig( resolverConfiguration );

      final Aspect aspect = getInputHandler( parentCommand.parentCommand.getInput() ).loadAspect();
      final SqlGenerationConfig.DialectSpecificConfig<?, ?, ?> generationConfig = switch ( dialect ) {
         case DATABRICKS -> databricksSpecificConfig();
         case POSTGRESQL -> postgreSqlSpecificConfig();
      };
      final SqlGenerationConfig sqlConfig = new SqlGenerationConfig( dialect, strategy, generationConfig );
      final SqlArtifact result = new AspectModelSqlGenerator( aspect, sqlConfig ).singleResult();

      try ( final OutputStream out = getStreamForFile( outputFilePath ) ) {
         out.write( result.getContent().getBytes() );
      } catch ( final IOException exception ) {
         throw new CommandException( exception );
      }
   }

   private DatabricksSqlGenerationConfig databricksSpecificConfig() {
      final List<DatabricksColumnDefinition> columns = customColumns.stream()
            .map( column -> new DatabricksColumnDefinitionParser( column ).get() )
            .toList();
      final List<DatabricksConstraint> constraints = tableConstraints.stream()
            .<DatabricksConstraint>map( DatabricksConstraint.GenericConstraint::new )
            .toList();
      return DatabricksSqlGenerationConfigBuilder.builder()
            .commentLanguage( Locale.forLanguageTag( language ) )
            .includeTableComment( includeTableComment )
            .includeColumnComments( includeColumnComments )
            .createTableCommandPrefix( tableCommandPrefix )
            .decimalPrecision( decimalPrecision )
            .decimalScale( decimalScale )
            .flattenScalarCollections( flattenScalarCollections )
            .customColumns( columns )
            .customColumnsPerTable( customColumnsForTable.entrySet().stream()
                  .map( entry -> Map.entry( entry.getKey(),
                        Arrays.stream( entry.getValue().split( ";" ) )
                              .map( column -> new DatabricksColumnDefinitionParser( column ).get() )
                              .toList() ) )
                  .collect( asMap() ) )
            .tableConstraints( constraints )
            .tableConstraintsPerTable( tableConstraintsForTable.entrySet().stream()
                  .map( entry -> Map.entry( entry.getKey(),
                        Arrays.stream( entry.getValue().split( ";" ) )
                              .<DatabricksConstraint>map( DatabricksConstraint.GenericConstraint::new )
                              .toList() ) )
                  .collect( asMap() ) )
            .build();
   }

   private PostgreSqlGenerationConfig postgreSqlSpecificConfig() {
      final List<PostgreSqlColumnDefinition> columns = customColumns.stream()
            .map( column -> new PostgreSqlColumnDefinitionParser( column ).get() )
            .toList();
      final List<PostgreSqlConstraint> constraints = tableConstraints.stream()
            .<PostgreSqlConstraint>map( PostgreSqlConstraint.GenericConstraint::new )
            .toList();
      return PostgreSqlGenerationConfigBuilder.builder()
            .commentLanguage( Locale.forLanguageTag( language ) )
            .includeTableComment( includeTableComment )
            .includeColumnComments( includeColumnComments )
            .createTableCommandPrefix( tableCommandPrefix )
            .decimalPrecision( decimalPrecision )
            .decimalScale( decimalScale )
            .flattenScalarCollections( flattenScalarCollections )
            .customColumns( columns )
            .customColumnsPerTable( customColumnsForTable.entrySet().stream()
                  .map( entry -> Map.entry( entry.getKey(),
                        Arrays.stream( entry.getValue().split( ";" ) )
                              .map( column -> new PostgreSqlColumnDefinitionParser( column ).get() )
                              .toList() ) )
                  .collect( asMap() ) )
            .tableConstraints( constraints )
            .tableConstraintsPerTable( tableConstraintsForTable.entrySet().stream()
                  .map( entry -> Map.entry( entry.getKey(),
                        Arrays.stream( entry.getValue().split( ";" ) )
                              .<PostgreSqlConstraint>map( PostgreSqlConstraint.GenericConstraint::new )
                              .toList() ) )
                  .collect( asMap() ) )
            .build();
   }
}
