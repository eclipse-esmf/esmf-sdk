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

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.Test;

class AspectModelPostgreSqlDenormalizedSqlVisitorTest {
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
            .contains( "date_property DATE NOT NULL" )
            .contains( "date_time_stamp_property TIMESTAMP WITH TIME ZONE NOT NULL" )
            .contains( "decimal_property NUMERIC(10,0) NOT NULL" )
            .contains( "double_property DOUBLE PRECISION NOT NULL" )
            .contains( "string_property TEXT NOT NULL" );
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

   private String sql( final TestAspect testAspect, final PostgreSqlGenerationConfig config ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      return aspect.accept( new AspectModelPostgreSqlDenormalizedSqlVisitor( config ),
            AspectModelPostgreSqlDenormalizedSqlVisitorContextBuilder.builder().build() );
   }
}
