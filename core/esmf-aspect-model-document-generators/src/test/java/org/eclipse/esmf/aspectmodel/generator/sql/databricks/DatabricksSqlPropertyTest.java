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

import java.util.Optional;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

public class DatabricksSqlPropertyTest {
   @Provide
   Arbitrary<DatabricksType.DatabricksDecimal> anyDecimal() {
      final Arbitrary<DatabricksType.DatabricksDecimal> defaultDecimal = Arbitraries.of( new DatabricksType.DatabricksDecimal() );
      final Arbitrary<DatabricksType.DatabricksDecimal> decimalWithPrecision = Arbitraries.integers().between( 1, 20 )
            .map( precision -> new DatabricksType.DatabricksDecimal( Optional.of( precision ) ) );
      final Arbitrary<DatabricksType.DatabricksDecimal> decimalWithPrecisionAndScale = Arbitraries.integers().between( 1, 20 )
            .flatMap( precision -> Arbitraries.integers().between( 1, 20 )
                  .map( scale -> new DatabricksType.DatabricksDecimal( Optional.of( precision ), Optional.of( scale ) ) ) );
      return Arbitraries.oneOf( defaultDecimal, decimalWithPrecision, decimalWithPrecisionAndScale );
   }

   @Provide
   Arbitrary<DatabricksType> anyArray() {
      return anyType().map( DatabricksType.DatabricksArray::new );
   }

   @Provide
   Arbitrary<Boolean> nullableOrNot() {
      return Arbitraries.of( true, false );
   }

   @Provide
   Arbitrary<String> anyComment() {
      return Arbitraries.strings().ofMaxLength( 10 );
   }

   @Provide
   Arbitrary<DatabricksType.DatabricksStructEntry> anyStructEntry() {
      return Combinators.combine( anyColumnName(), anyType(), nullableOrNot(), anyComment().map( Optional::of ) )
            .as( DatabricksType.DatabricksStructEntry::new );
   }

   @Provide
   Arbitrary<DatabricksType> anyStruct() {
      return anyStructEntry().list().ofMinSize( 1 ).ofMaxSize( 3 ).map( DatabricksType.DatabricksStruct::new );
   }

   @Provide
   Arbitrary<DatabricksType> anyStandardType() {
      return Arbitraries.of( DatabricksType.BIGINT, DatabricksType.BINARY,
            DatabricksType.BOOLEAN, DatabricksType.DATE, DatabricksType.DOUBLE, DatabricksType.FLOAT, DatabricksType.INT,
            DatabricksType.SMALLINT, DatabricksType.STRING, DatabricksType.TIMESTAMP, DatabricksType.TIMESTAMP_NTZ,
            DatabricksType.TINYINT );
   }

   @Provide
   Arbitrary<DatabricksType> anyMap() {
      final Arbitrary<DatabricksType> anyTypeWithoutMap = Arbitraries.lazyOf( this::anyStandardType, this::anyDecimal, this::anyArray,
            this::anyStruct );
      final Arbitrary<DatabricksType> anyType = Arbitraries.lazyOf( this::anyStandardType, this::anyDecimal, this::anyArray,
            this::anyStruct, this::anyMap );
      return Combinators.combine( anyTypeWithoutMap, anyType ).as( DatabricksType.DatabricksMap::new );
   }

   @Provide
   Arbitrary<DatabricksType> anyType() {
      return Arbitraries.lazyOf( this::anyStandardType, this::anyDecimal, this::anyArray, this::anyStruct, this::anyMap );
   }

   @Provide
   Arbitrary<String> anyColumnName() {
      return Arbitraries.strings().withCharRange( 'a', 'z' ).withChars( '_' ).ofMinLength( 1 ).ofMaxLength( 10 );
   }

   @Provide
   Arbitrary<DatabricksColumnDefinition> syntheticDatabricksColumnDefinition() {
      return Combinators.combine( anyColumnName(), anyType(), nullableOrNot(), anyComment().map( Optional::of ) )
            .as( DatabricksColumnDefinition::new );
   }

   @Provide
   Arbitrary<String> anyDatabricksColumnDefinition() {
      return syntheticDatabricksColumnDefinition().map( DatabricksColumnDefinition::toString );
   }

   @Property
   boolean isValidColumnDefinition( @ForAll( "anyDatabricksColumnDefinition" ) final String columnDefintition ) {
      final DatabricksColumnDefinition column = new DatabricksColumnDefinitionParser( columnDefintition ).get();
      return column.toString().equals( columnDefintition );
   }
}
