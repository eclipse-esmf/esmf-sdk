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

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.test.TestAspect;

import org.junit.jupiter.api.Test;

class PostgreSqlDenormalizedSqlGeneratorTest extends PostgreSqlTestBase {
   @Test
   void testGenerationConfigNormalizesInvalidValues() {
      final PostgreSqlGenerationConfig config = PostgreSqlGenerationConfigBuilder.builder().build();
      assertThat( config.createTableCommandPrefix() ).isEqualTo( PostgreSqlGenerationConfig.DEFAULT_TABLE_COMMAND_PREFIX );
      assertThat( config.decimalPrecision() ).isEqualTo( PostgreSqlGenerationConfig.DECIMAL_DEFAULT_PRECISION );
      assertThat( config.decimalScale() ).isEqualTo( PostgreSqlGenerationConfig.DECIMAL_DEFAULT_SCALE );
      assertThat( config.customColumns() ).isEmpty();
      assertThat( config.tableConstraints() ).isEmpty();
   }

   @Test
   void testCollectionAndComments() {
      assertThat( sql( TestAspect.ASPECT_WITH_COLLECTION, new PostgreSqlGenerationConfig() ) ).isEqualTo( """
         CREATE TABLE IF NOT EXISTS aspect_with_collection (
           test_property TEXT[] NOT NULL
         );
         COMMENT ON TABLE aspect_with_collection IS 'This is a test description';
         COMMENT ON COLUMN aspect_with_collection.test_property IS 'This is a test property.';
         -- x-samm-aspect-model-urn: urn:samm:org.eclipse.esmf.test:1.0.0#AspectWithCollection
         """ );
   }

   @Test
   void testPostgreSqlTypes() {
      final String result = sql( TestAspect.ASPECT_WITH_SIMPLE_TYPES, new PostgreSqlGenerationConfig() );

      assertThat( result )
            .contains( "base64_binary_property BYTEA NOT NULL" )
            .contains( "date_property TEXT NOT NULL" )
            .contains( "date_time_stamp_property TIMESTAMP WITH TIME ZONE NOT NULL" )
            .contains( "decimal_property NUMERIC(10,0) NOT NULL" )
            .contains( "double_property DOUBLE PRECISION NOT NULL" )
            .contains( "string_property TEXT NOT NULL" )
            .contains( "time_property TEXT NOT NULL" );
   }

   @Test
   void testTableConstraints() {
      final PostgreSqlGenerationConfig config = PostgreSqlGenerationConfigBuilder.builder()
            .tableConstraints( Stream.of( "PRIMARY KEY (test)", "UNIQUE (test)", "CHECK (test <> 'x')" )
                  .<PostgreSqlConstraint>map( PostgreSqlConstraint.GenericConstraint::new ).toList() )
            .build();
      assertThat( sql( TestAspect.ASPECT_WITH_PROPERTY_WITH_PAYLOAD_NAME, config ) )
            .contains( "PRIMARY KEY (test),\n  UNIQUE (test),\n  CHECK (test <> 'x')\n)" );
   }

   @Test
   void testCustomColumn() {
      final PostgreSqlColumnDefinition customColumn = new PostgreSqlColumnDefinition(
            "custom_column", new PostgreSqlType.PostgreSqlArray( PostgreSqlType.TEXT ), false, Optional.of( "Custom column" ) );
      final PostgreSqlGenerationConfig config = PostgreSqlGenerationConfigBuilder.builder()
            .includeColumnComments( true )
            .customColumns( List.of( customColumn ) )
            .build();

      assertThat( sql( TestAspect.ASPECT_WITH_OPERATION, config ) )
            .contains( "custom_column TEXT[] NOT NULL" )
            .contains( "COMMENT ON COLUMN aspect_with_operation.custom_column IS 'Custom column';" );
   }
}
