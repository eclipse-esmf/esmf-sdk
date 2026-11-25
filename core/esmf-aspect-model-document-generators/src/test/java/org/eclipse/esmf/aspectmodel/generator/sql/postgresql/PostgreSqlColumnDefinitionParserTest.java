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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.eclipse.esmf.aspectmodel.generator.DocumentGenerationException;

import org.junit.jupiter.api.Test;

class PostgreSqlColumnDefinitionParserTest {
   @Test
   void testParseColumn() {
      final PostgreSqlColumnDefinition result = new PostgreSqlColumnDefinitionParser(
            "custom NUMERIC(23, 14)[] NOT NULL" ).get();

      assertThat( result.name() ).isEqualTo( "custom" );
      assertThat( result.type().toString() ).isEqualTo( "NUMERIC(23,14)[]" );
      assertThat( result.nullable() ).isFalse();
   }

   @Test
   void testParseNullableStandardTypeAndArray() {
      final PostgreSqlColumnDefinition result =
            new PostgreSqlColumnDefinitionParser( "tags TEXT[]" ).get();

      assertThat( result.name() ).isEqualTo( "tags" );
      assertThat( result.type() ).isEqualTo( new PostgreSqlType.PostgreSqlArray( PostgreSqlType.TEXT ) );
      assertThat( result.nullable() ).isTrue();
   }

   @Test
   void testParseNumericWithoutScale() {
      assertThat( new PostgreSqlColumnDefinitionParser( "amount NUMERIC(23)" ).get().type() )
            .isEqualTo( new PostgreSqlType.PostgreSqlNumeric( Optional.of( 23 ), Optional.empty() ) );
      assertThat( new PostgreSqlColumnDefinitionParser( "amount NUMERIC" ).get().type() )
            .isEqualTo( new PostgreSqlType.PostgreSqlNumeric( Optional.empty(), Optional.empty() ) );
   }

   @Test
   void testRejectMalformedOrUnknownDefinitions() {
      assertThatThrownBy( () -> new PostgreSqlColumnDefinitionParser( "missing-type" ).get() )
            .isInstanceOf( DocumentGenerationException.class );
      assertThatThrownBy( () -> new PostgreSqlColumnDefinitionParser( "value UNKNOWN" ).get() )
            .isInstanceOf( DocumentGenerationException.class )
            .hasMessageContaining( "Could not parse PostgreSQL type" );
   }
}
