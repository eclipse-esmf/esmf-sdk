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

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Base class for SQL dialect implementations
 *
 * @param <U> the SQL dialect-specific configuration type
 * @param <D> the type of the column datatypes
 * @param <C> the column definition type
 * @param <T> the table definition type
 * @param <X> the constraint type
 */
public abstract class SqlDialectBase<
      U extends SqlGenerationConfig.DialectSpecificConfig<D, C, X>,
      D extends SqlType,
      C extends SqlColumnDefinition<D, C>,
      T extends SqlTableDefinition<D, C, X>,
      X extends SqlConstraint>
      implements SqlDialect<U, D, C, T, X> {

   protected void renderTableDefinitionBaseStructure( final StringBuilder builder, final T tableDefinition, final U config ) {
      builder.append( config.createTableCommandPrefix() ).append( " " );
      builder.append( tableDefinition.name() ).append( " " );
      final String extraLineBreak = tableDefinition.columns().isEmpty() ? "" : "\n";
      final String extraIndent = tableDefinition.columns().isEmpty() ? "" : "  ";
      builder.append( Stream.concat(
            tableDefinition.columns().stream().map( column -> {
               final String constraints = tableDefinition.tableConstraints().stream()
                     .filter( constraint -> constraint instanceof final SqlConstraint.ColumnConstraint columnConstraint
                           && columnConstraint.column().equals( column.name() ) )
                     .sorted()
                     .map( Object::toString )
                     .collect( Collectors.joining( " " ) );
               return column.toString() + ( constraints.isEmpty() ? "" : " " + constraints );
            } ),
            tableDefinition.tableConstraints().stream().filter( SqlConstraint.TableConstraint.class::isInstance ).map( Object::toString )
      ).collect( Collectors.joining( ",\n  ", "(\n" + extraIndent, extraLineBreak + ")" ) ) );
   }
}
