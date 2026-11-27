/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.eclipse.esmf.aspectmodel.generator.sql.databricks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.jena.vocabulary.XSD;

import org.eclipse.esmf.aspectmodel.generator.DocumentGenerationException;
import org.eclipse.esmf.test.TestAspect;

import org.junit.jupiter.api.Test;

class DatabricksSqlDialectTest extends DatabricksTestBase {
   @Test
   void testDialectFactoriesAndTypeMappings() {
      final DatabricksSqlGenerationConfig config = DatabricksSqlGenerationConfigBuilder.builder()
            .decimalPrecision( 38 )
            .decimalScale( 12 )
            .build();
      final DatabricksSqlDialect dialect = new DatabricksSqlDialect( config );

      assertThat( dialect.sqlIdColumnType() ).isEqualTo( DatabricksType.BIGINT );
      assertThat( dialect.sqlArrayType( DatabricksType.STRING ).toString() ).isEqualTo( "ARRAY<STRING>" );
      assertThat( dialect.sqlType( XSD.decimal.getURI() ).toString() ).isEqualTo( "DECIMAL(38,12)" );
      assertThat( dialect.sqlType( XSD.unsignedLong.getURI() ).toString() ).isEqualTo( "DECIMAL(20)" );
      assertThatThrownBy( () -> dialect.sqlType( "unknown:type" ) )
            .isInstanceOf( DocumentGenerationException.class )
            .hasMessageContaining( "Unable to map SAMM type unknown:type to corresponding SQL type" );

      assertThat( dialect.sqlPrimaryKeyConstraint( "_id" ).toString() ).isEqualTo( "PRIMARY KEY" );
      assertThat( dialect.sqlForeignKeyConstraint( "_id", "parent", "_id" ).toString() )
            .isEqualTo( "FOREIGN KEY (_id) REFERENCES parent(_id)" );
      assertThat( dialect.sqlUniqueConstraint( "u_value", "value" ).toString() )
            .isEqualTo( "CONSTRAINT u_value UNIQUE (value)" );
   }

   @Test
   void testRenderTableWithPropertiesCommentAndConstraints() {
      final DatabricksSqlGenerationConfig config = DatabricksSqlGenerationConfigBuilder.builder().build();
      final DatabricksSqlDialect dialect = new DatabricksSqlDialect( config );
      final DatabricksTableDefinition table = dialect.tableDefinition(
            "example",
            List.of( new DatabricksColumnDefinition( "value", DatabricksType.STRING, false, Optional.of( "A value" ) ) ),
            Optional.of( "Example table" ),
            List.of( new DatabricksConstraint.Unique( "value" ) ),
            Map.of( "urn", dialect.sqlStringValue( "urn:test" ) ) );

      assertThat( dialect.renderTableDefinition( table, config ) ).isEqualTo(
            """
               CREATE TABLE IF NOT EXISTS example (
                 value STRING NOT NULL COMMENT 'A value',
                 CONSTRAINT u_value UNIQUE (value)
               )
               COMMENT 'Example table'
               TBLPROPERTIES ('urn'='urn:test');
               """ );
   }

   @Test
   void testRenderCommentsWithLinebreaksAndSingleQuotes() {
      assertThat( sql( TestAspect.ASPECT_WITH_MARKDOWN_DESCRIPTION ) )
            .contains( "COMMENT '\\n      This is a sample" )
            .contains( "Here\\'s a second line" );
   }
}
