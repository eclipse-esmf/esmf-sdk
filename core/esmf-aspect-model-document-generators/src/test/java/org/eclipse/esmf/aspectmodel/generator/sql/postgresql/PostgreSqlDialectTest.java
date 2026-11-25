/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.eclipse.esmf.aspectmodel.generator.sql.postgresql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.apache.jena.vocabulary.XSD;

import org.eclipse.esmf.aspectmodel.generator.DocumentGenerationException;

import org.junit.jupiter.api.Test;

class PostgreSqlDialectTest {
   @Test
   void testDialectFactoriesAndTypeMappings() {
      final PostgreSqlGenerationConfig config = PostgreSqlGenerationConfigBuilder.builder()
            .decimalPrecision( 23 )
            .decimalScale( 14 )
            .build();
      final PostgreSqlDialect dialect = new PostgreSqlDialect( config );

      assertThat( dialect.sqlIdColumnType() ).isEqualTo( PostgreSqlType.BIGINT );
      assertThat( dialect.sqlArrayType( PostgreSqlType.TEXT ).toString() ).isEqualTo( "TEXT[]" );
      assertThat( dialect.sqlType( XSD.decimal.getURI() ).toString() ).isEqualTo( "NUMERIC(23,14)" );
      assertThat( dialect.sqlType( XSD.unsignedLong.getURI() ).toString() ).isEqualTo( "NUMERIC(20)" );
      assertThatThrownBy( () -> dialect.sqlType( "unknown:type" ) )
            .isInstanceOf( DocumentGenerationException.class )
            .hasMessageContaining( "Unable to map SAMM type unknown:type to corresponding SQL type" );
      assertThat( dialect.sqlPrimaryKeyConstraint( "_id" ).toString() ).isEqualTo( "PRIMARY KEY" );
      assertThat( dialect.sqlForeignKeyConstraint( "_id", "parent", "_id" ).toString() )
            .isEqualTo( "REFERENCES parent (_id)" );
      assertThat( dialect.sqlUniqueConstraint( "u_value", "value" ).toString() )
            .isEqualTo( "CONSTRAINT u_value UNIQUE" );
   }

   @Test
   void testRenderTableWithCommentsAndColumnConstraint() {
      final PostgreSqlGenerationConfig config = new PostgreSqlGenerationConfig();
      final PostgreSqlDialect dialect = new PostgreSqlDialect( config );
      final PostgreSqlColumnDefinition column =
            new PostgreSqlColumnDefinition( "value", PostgreSqlType.TEXT, false, Optional.of( "A value" ) );
      final PostgreSqlTableDefinition table = dialect.tableDefinition(
            "example",
            List.of( column ),
            Optional.of( "Example table" ),
            List.of( new PostgreSqlConstraint.Unique( "u_value", "value" ) ),
            java.util.Map.of() );

      assertThat( dialect.renderTableDefinition( table, config ) ).isEqualTo(
            """
               CREATE TABLE IF NOT EXISTS example (
                 value TEXT NOT NULL CONSTRAINT u_value UNIQUE
               );
               COMMENT ON TABLE example IS 'Example table';
               COMMENT ON COLUMN example.value IS 'A value';
               """ );
   }
}
