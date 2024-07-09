/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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
package org.eclipse.esmf.aspectmodel.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;

import org.junit.jupiter.api.Test;

public class NumericTypeTraitsTest {
   @Test
   void testIsFloatingPointNumberType() {
      // all floating point types
      assertThat( NumericTypeTraits.isFloatingPointNumberType( Float.class ) ).isTrue();
      assertThat( NumericTypeTraits.isFloatingPointNumberType( Double.class ) ).isTrue();
      assertThat( NumericTypeTraits.isFloatingPointNumberType( BigDecimal.class ) ).isTrue();

      // all integral types
      assertThat( NumericTypeTraits.isFloatingPointNumberType( Byte.class ) ).isFalse();
      assertThat( NumericTypeTraits.isFloatingPointNumberType( Short.class ) ).isFalse();
      assertThat( NumericTypeTraits.isFloatingPointNumberType( Integer.class ) ).isFalse();
      assertThat( NumericTypeTraits.isFloatingPointNumberType( Long.class ) ).isFalse();
      assertThat( NumericTypeTraits.isFloatingPointNumberType( BigInteger.class ) ).isFalse();
   }

   @Test
   void testGetNativeMinValue() {
      assertThat( NumericTypeTraits.getNativeMinValue( Short.class ) ).isEqualTo( Short.MIN_VALUE );
      assertThat( NumericTypeTraits.getNativeMinValue( Integer.class ) ).isEqualTo( Integer.MIN_VALUE );
      assertThat( NumericTypeTraits.getNativeMinValue( Long.class ) ).isEqualTo( Long.MIN_VALUE );
      assertThat( NumericTypeTraits.getNativeMinValue( Float.class ) ).isEqualTo( -Float.MAX_VALUE );
      assertThat( NumericTypeTraits.getNativeMinValue( Double.class ) ).isEqualTo( -Double.MAX_VALUE );
      assertThat( NumericTypeTraits.getNativeMinValue( BigDecimal.class ).doubleValue() )
            .isEqualTo( -Double.MAX_VALUE );
   }

   @Test
   void testGetNativeMaxValue() {
      assertThat( NumericTypeTraits.getNativeMaxValue( Short.class ) ).isEqualTo( Short.MAX_VALUE );
      assertThat( NumericTypeTraits.getNativeMaxValue( Integer.class ) ).isEqualTo( Integer.MAX_VALUE );
      assertThat( NumericTypeTraits.getNativeMaxValue( Long.class ) ).isEqualTo( Long.MAX_VALUE );
      assertThat( NumericTypeTraits.getNativeMaxValue( Float.class ) ).isEqualTo( Float.MAX_VALUE );
      assertThat( NumericTypeTraits.getNativeMaxValue( Double.class ) ).isEqualTo( Double.MAX_VALUE );
      assertThat( NumericTypeTraits.getNativeMaxValue( BigDecimal.class ).doubleValue() ).isEqualTo( Double.MAX_VALUE );
   }

   @Test
   void testGetModelMinValue() {
      // int maps to normal integer, so native type range should apply
      final Type intType = new DefaultScalar( SammXsdType.INT.getURI() );
      assertThat( NumericTypeTraits.getModelMinValue( intType ) ).isEqualTo( Integer.MIN_VALUE );

      // unsigned model types do not have native Java equivalents, so they map to the next wider type with model range set
      final Type unsignedShort = new DefaultScalar( SammXsdType.UNSIGNED_SHORT.getURI() );
      assertThat( NumericTypeTraits.getModelMinValue( unsignedShort ) ).isEqualTo( 0 );

      // no lower bound
      final Type negativeInteger = new DefaultScalar( SammXsdType.NEGATIVE_INTEGER.getURI() );
      assertThat( NumericTypeTraits.getModelMinValue( negativeInteger ).doubleValue() )
            .isEqualTo( -Double.MAX_VALUE );
   }

   @Test
   void testGetModelMaxValue() {
      // int maps to normal integer, so native type range should apply
      final Type intType = new DefaultScalar( SammXsdType.INT.getURI() );
      assertThat( NumericTypeTraits.getModelMaxValue( intType ) ).isEqualTo( Integer.MAX_VALUE );

      // unsigned model types do not have native Java equivalents, so they map to the next wider type with model range set
      final Type unsignedShort = new DefaultScalar( SammXsdType.UNSIGNED_SHORT.getURI() );
      assertThat( NumericTypeTraits.getModelMaxValue( unsignedShort ) ).isEqualTo( 65535 );

      final Type negativeInteger = new DefaultScalar( SammXsdType.NEGATIVE_INTEGER.getURI() );
      assertThat( NumericTypeTraits.getModelMaxValue( negativeInteger ) ).isEqualTo( -1 );
   }

   @Test
   void testPolymorphicAdd() {
      assertThat( NumericTypeTraits.polymorphicAdd( (byte) 4, 1 ) ).isEqualTo( (byte) 5 );
      assertThat( NumericTypeTraits.polymorphicAdd( (short) 12222, -1 ) ).isEqualTo( (short) 12221 );
      assertThat( NumericTypeTraits.polymorphicAdd( 4, 1 ) ).isEqualTo( 5 );
      assertThat( NumericTypeTraits.polymorphicAdd( 12345L, 3 ) ).isEqualTo( 12348L );
      assertThat( NumericTypeTraits.polymorphicAdd( 1.34f, 2.22f ) ).isEqualTo( 3.56f );
      assertThat( NumericTypeTraits.polymorphicAdd( BigInteger.valueOf( 20 ), 100 ) )
            .isEqualTo( BigInteger.valueOf( 120 ) );
   }

   @Test
   void testConvertToBigDecimal() {
      assertThat( NumericTypeTraits.convertToBigDecimal( 6374 ).compareTo( BigDecimal.valueOf( 6374 ) ) )
            .isEqualTo( 0 );
      assertThat(
            NumericTypeTraits.convertToBigDecimal( Long.MAX_VALUE ).compareTo( BigDecimal.valueOf( Long.MAX_VALUE ) ) )
            .isEqualTo( 0 );
      assertThat( NumericTypeTraits.convertToBigDecimal( Double.MAX_VALUE )
            .compareTo( BigDecimal.valueOf( Double.MAX_VALUE ) ) ).isEqualTo( 0 );
   }
}
