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
}
