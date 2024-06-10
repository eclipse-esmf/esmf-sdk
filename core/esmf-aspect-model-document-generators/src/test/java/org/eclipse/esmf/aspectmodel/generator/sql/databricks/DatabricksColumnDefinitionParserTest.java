/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;

import org.eclipse.esmf.test.TestAspect;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class DatabricksColumnDefinitionParserTest extends DatabricksTestBase {
   @Test
   void testMinimalDefinition() {
      final DatabricksColumnDefinition definition = new DatabricksColumnDefinitionParser( "abc STRING" ).get();
      assertThat( definition.name() ).isEqualTo( "abc" );
      assertThat( definition.type() ).isEqualTo( DatabricksType.STRING );
   }

   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   void testParseSqlForAspectModel( final TestAspect testAspect ) {
      final String sql = sql( testAspect );
      final String parsedAndSerializedSql = sql.lines()
            .filter( line -> line.startsWith( "  " ) )
            .map( line -> "  " + new DatabricksColumnDefinitionParser( line.trim() ).get().toString() )
            .collect( Collectors.joining( "\n" ) );
      assertThat( sql.lines()
            .filter( line -> line.startsWith( "  " ) )
            .map( line -> line.replaceAll( ",$", "" ) )
            .collect( Collectors.joining( "\n" ) ) )
            .isEqualTo( parsedAndSerializedSql );
   }

   @Test
   void testParseCommentWithEscapes() {
      final String line = "column STRING COMMENT 'Test with \\' test'";
      final DatabricksColumnDefinitionParser parser = new DatabricksColumnDefinitionParser( line );
      final DatabricksColumnDefinition result = parser.get();
      assertThat( result.comment() ).contains( "Test with ' test" );
   }
}
