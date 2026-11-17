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

package org.eclipse.esmf.aspectmodel.generator.sql.postgresql;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.sql.SqlGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlTableDefinition;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlValue;

public record PostgreSqlTableDefinition(
      String name,
      List<PostgreSqlColumnDefinition> columns,
      Optional<String> comment,
      List<String> tableConstraints,
      Map<String, SqlValue<? extends PostgreSqlType>> tableProperties
) implements SqlTableDefinition<PostgreSqlType, PostgreSqlColumnDefinition> {
   @Override
   public String apply( final SqlGenerationConfig.DialectSpecificConfig<PostgreSqlType, PostgreSqlColumnDefinition> config ) {
      final StringBuilder builder = new StringBuilder();
      builder.append( config.createTableCommandPrefix() ).append( " " );
      builder.append( name ).append( " " );
      final String extraLineBreak = columns.isEmpty() ? "" : "\n";
      final String extraIndent = columns.isEmpty() ? "" : "  ";
      builder.append( Stream.concat(
            columns.stream().map( PostgreSqlColumnDefinition::toString ),
            config.tableConstraints().stream()
      ).collect( Collectors.joining( ",\n  ", "(\n" + extraIndent, extraLineBreak + ");\n" ) ) );

      comment.ifPresent( theComment -> {
         builder.append( "COMMENT ON TABLE " ).append( name ).append( " IS '" ).append( theComment ).append( "';\n" );
      } );
      columns.forEach( column -> {
         column.comment().ifPresent( theComment -> {
            builder.append( "COMMENT ON COLUMN " ).append( name ).append( "." ).append( column.name() )
                  .append( " IS '" ).append( theComment ).append( "';\n" );
         } );
      } );
      builder.append( tableProperties().entrySet().stream().sorted()
            .map( tablePropertyEntry -> "-- %s: %s".formatted( tablePropertyEntry.getKey(),
                  tablePropertyEntry.getValue().lexical() ) )
            .collect( Collectors.joining( "\n", "", "\n" ) ) );
      return builder.toString();
   }
}
