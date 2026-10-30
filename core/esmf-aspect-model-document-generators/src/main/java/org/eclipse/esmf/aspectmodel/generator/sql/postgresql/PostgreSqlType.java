/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
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

import java.util.Optional;

/**
 * PostgreSQL data types supported for custom columns.
 */
public sealed interface PostgreSqlType {
   PostgreSqlType BIGINT = new SimplePostgreSqlType( "BIGINT" );
   PostgreSqlType BOOLEAN = new SimplePostgreSqlType( "BOOLEAN" );
   PostgreSqlType BYTEA = new SimplePostgreSqlType( "BYTEA" );
   PostgreSqlType DATE = new SimplePostgreSqlType( "DATE" );
   PostgreSqlType DOUBLE_PRECISION = new SimplePostgreSqlType( "DOUBLE PRECISION" );
   PostgreSqlType INTEGER = new SimplePostgreSqlType( "INTEGER" );
   PostgreSqlType REAL = new SimplePostgreSqlType( "REAL" );
   PostgreSqlType SMALLINT = new SimplePostgreSqlType( "SMALLINT" );
   PostgreSqlType TEXT = new SimplePostgreSqlType( "TEXT" );
   PostgreSqlType TIME = new SimplePostgreSqlType( "TIME" );
   PostgreSqlType TIMESTAMP = new SimplePostgreSqlType( "TIMESTAMP" );
   PostgreSqlType TIMESTAMP_WITH_TIME_ZONE = new SimplePostgreSqlType( "TIMESTAMP WITH TIME ZONE" );

   record PostgreSqlNumeric(
         Optional<Integer> precision, Optional<Integer> scale
   ) implements PostgreSqlType {
      public PostgreSqlNumeric( final Optional<Integer> precision ) {
         this( precision, Optional.empty() );
      }

      public PostgreSqlNumeric() {
         this( Optional.empty(), Optional.empty() );
      }

      @Override
      public String toString() {
         return precision.map( value -> "NUMERIC(" + value + scale.map( scaleValue -> "," + scaleValue ).orElse( "" ) + ")" )
               .orElse( "NUMERIC" );
      }
   }

   record PostgreSqlArray(
         PostgreSqlType elementType
   ) implements PostgreSqlType {
      @Override
      public String toString() {
         return elementType + "[]";
      }
   }

   record SimplePostgreSqlType(
         String sql
   ) implements PostgreSqlType {
      @Override
      public String toString() {
         return sql;
      }
   }
}
