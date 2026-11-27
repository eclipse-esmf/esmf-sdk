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
 * Represents a specific SQL dialect
 *
 * @param <U> the SQL dialect-specific configuration type
 * @param <D> the type of the column datatypes
 * @param <C> the column definition type
 * @param <T> the table definition type
 * @param <X> the constraint type
 */
public interface SqlDialect<
      U extends SqlGenerationConfig.DialectSpecificConfig<D, C, X>,
      D extends SqlType,
      C extends SqlColumnDefinition<D, C>,
      T extends SqlTableDefinition<D, C, X>,
      X extends SqlConstraint> {
   T tableDefinition( String name, List<C> columns, Optional<String> comment, List<X> tableConstraints,
         Map<String, SqlValue<? extends D>> tableProperties );

   C columnDefinition( final String name, final D datatype, final boolean isNullable, final Optional<String> comment );

   /**
    * Type-safe cast of a string into a SQL-dialect specific string type
    *
    * @param value the value
    * @param <V> the concrete type
    * @return the value in the type of the SQL dialect
    */
   <V extends D> SqlValue<V> sqlStringValue( final String value );

   D sqlType( String sammTypeUri );

   D sqlIdColumnType();

   <A extends D> A sqlArrayType( D baseType );

   <A extends X> A sqlPrimaryKeyConstraint( String column );

   <A extends X> A sqlForeignKeyConstraint( String column, String foreignTable, String foreignColumn );

   <A extends X> A sqlUniqueConstraint( String name, String column );

   String renderTableDefinition( T tableDefinition, U config );
}
