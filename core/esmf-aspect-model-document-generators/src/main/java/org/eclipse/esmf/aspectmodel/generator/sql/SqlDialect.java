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

import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.StructureElement;

import com.google.common.base.CaseFormat;

public interface SqlDialect<
      U extends SqlGenerationConfig.DialectSpecificConfig<D, C>,
      D extends SqlType,
      C extends SqlColumnDefinition<D, C>,
      T extends SqlTableDefinition<D, C>> {
   default String tableName( final StructureElement element ) {
      return CaseFormat.UPPER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, element.getName() );
   }

   default String columnName( final Property property ) {
      return CaseFormat.LOWER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, property.getPayloadName() );
   }

   SqlTableDefinition<D, C> tableDefinition( String name, List<C> columns, Optional<String> comment,
         List<String> tableConstraints, Map<String, SqlValue<? extends D>> tableProperties );

   C columnDefinition( final String name, final D datatype, final boolean isNullable,
         final Optional<String> comment );

   /**
    * Type-safe cast of a string into a SQL-dialect specific string type
    *
    * @param value the value
    * @param <V> the concrete type
    * @return the value in the type of the SQL dialect
    */
   <V extends D> SqlValue<V> sqlStringValue( final String value );

   D sqlType( String sammTypeUri );

   <A extends D> A sqlArrayType( D baseType );
}
