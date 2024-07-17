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

package org.eclipse.esmf.aspectmodel.java;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.test.shared.arbitraries.PropertyBasedTest;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Tuple;

public class AspectModelJavaUtilTest extends PropertyBasedTest {
   private boolean isValidJavaIdentifier( final String value ) {
      if ( value == null || value.isEmpty() ) {
         return false;
      }
      final boolean firstCharacterValid = Character.isJavaIdentifierStart( value.charAt( 0 ) );
      if ( value.length() == 1 ) {
         return firstCharacterValid;
      }
      return firstCharacterValid && IntStream.range( 1, value.length() - 1 )
            .mapToObj( value::charAt )
            .map( Character::isJavaIdentifierPart )
            .reduce( true, ( x, y ) -> x && y );
   }

   @Property
   public boolean generatedEnumKeysAreValidJavaIdentifiers( @ForAll( "anyValidTypeValuePair" ) final Tuple.Tuple2<Type, Value> tuple ) {
      final String result = AspectModelJavaUtil.generateEnumKey( tuple.get2() );
      return isValidJavaIdentifier( result );
   }

   @ParameterizedTest
   @MethodSource
   void generatedEnumKeysAreExpectedEnumNames( final String enumValueName, final String expectedEnumValueName ) {
      final String result = AspectModelJavaUtil.toConstant( enumValueName );
      Assertions.assertThat( result ).isEqualTo( expectedEnumValueName );
   }

   static Stream<Arguments> generatedEnumKeysAreExpectedEnumNames() {
      return Stream.of(
            Arguments.arguments( "ABC", "ABC" ),
            Arguments.arguments( "abc", "ABC" ),
            Arguments.arguments( "someEnum", "SOME_ENUM" ),
            Arguments.arguments( "SOME_ENUM", "SOME_ENUM" ),
            Arguments.arguments( "SomeEnum", "SOME_ENUM" ),
            Arguments.arguments( "aB", "A_B" ),
            Arguments.arguments( "aBc", "A_BC" ),
            Arguments.arguments( "a_b_c", "A_B_C" )
      );
   }

}
