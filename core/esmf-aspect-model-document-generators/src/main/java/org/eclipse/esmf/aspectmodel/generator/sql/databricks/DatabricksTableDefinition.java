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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.sql.SqlGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlTableDefinition;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlValue;

public record DatabricksTableDefinition(
      String name,
      List<DatabricksColumnDefinition> columns,
      Optional<String> comment,
      List<String> tableConstraints,
      Map<String, SqlValue<? extends DatabricksType>> tableProperties
) implements SqlTableDefinition<DatabricksType, DatabricksColumnDefinition> {
   @Override
   public String apply( final SqlGenerationConfig.DialectSpecificConfig<DatabricksType, DatabricksColumnDefinition> config ) {
      final StringBuilder builder = new StringBuilder();
      builder.append( config.createTableCommandPrefix() ).append( " " );
      builder.append( name ).append( " " );
      final String extraLineBreak = columns.isEmpty() ? "" : "\n";
      final String extraIndent = columns.isEmpty() ? "" : "  ";
      builder.append( Stream.concat(
            columns.stream().map( column -> column.apply( config ) ),
            config.tableConstraints().stream()
      ).collect( Collectors.joining( ",\n  ", "(\n" + extraIndent, extraLineBreak + ")\n" ) ) );
      comment.ifPresent( theComment -> builder.append( new DatabricksCommentDefinition( theComment ) ).append( "\n" ) );
      builder.append( tableProperties().entrySet().stream().sorted()
            .map( tablePropertyEntry -> "'%s'=%s".formatted( tablePropertyEntry.getKey(), tablePropertyEntry.getValue() ) )
            .collect( Collectors.joining( ", ", "TBLPROPERTIES (", ");\n" ) ) );
      return builder.toString();
   }
}
