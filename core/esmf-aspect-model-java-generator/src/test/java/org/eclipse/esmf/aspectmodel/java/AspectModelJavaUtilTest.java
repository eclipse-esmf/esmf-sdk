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

import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.Value;
import org.eclipse.esmf.test.shared.arbitraries.PropertyBasedTest;

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
   public boolean generatedEnumKeysAreValidJavaIdentifiers( @ForAll( "anyValidTypeValuePair" ) final Tuple.Tuple2<Type, Value> tuple ) {
      final String result = AspectModelJavaUtil.generateEnumKey( tuple.get2() );
      return isValidJavaIdentifier( result );
   }
}
