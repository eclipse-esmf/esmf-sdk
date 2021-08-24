/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.java;

import static io.openmanufacturing.sds.aspectmodel.java.AspectModelJavaUtil.generateEnumKey;

import java.util.Optional;
import java.util.stream.IntStream;

import io.openmanufacturing.sds.metamodel.Type;
import io.openmanufacturing.sds.test.shared.arbitraries.PropertyBasedTest;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Tuple;

public class AspectModelJavaUtilTest extends PropertyBasedTest {
   private boolean isValidJavaIdentifier( final String value ) {
      if ( value == null || value.length() == 0 ) {
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
   public boolean generatedEnumKeysAreValidJavaIdentifiers(
         @ForAll( "anyValidTypeValuePair" ) final Tuple.Tuple2<Type, Object> tuple ) {
      final ImportTracker importTracker = new ImportTracker();
      final String result = generateEnumKey( Optional.of( tuple.get1() ), tuple.get2(), importTracker );
      return isValidJavaIdentifier( result );
   }
}
