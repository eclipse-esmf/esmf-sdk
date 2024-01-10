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

package examples;

// tag::imports[]
import java.util.ArrayList;
import java.util.List;

import io.vavr.collection.Stream;
import io.vavr.control.Try;
// end::imports[]
import org.junit.jupiter.api.Test;

public class HandlingTry {
   private Try<List<String>> someMethodReturningTryOfListOfString() {
      return Try.success( List.of( "hello", "world" ) );
   }

   @Test
   public void handlingTry() {
      // tag::handlingTry[]
      final Try<List<String>> tryList = someMethodReturningTryOfListOfString();

      // Option 1: forEach to execute code on the value
      tryList.forEach( valueList -> {
         final List<String> result1 = new ArrayList<>();
         for ( final String element : valueList ) {
            result1.add( element.toUpperCase() );
         }
      } );

      // Option 2: map/flatMap values
      final List<String> result2 = tryList.toStream().flatMap( Stream::ofAll )
            .map( String::toUpperCase ).toJavaList();

      // Option 3: "extract" value and throw on failure
      final List<String> valueList = tryList.getOrElseThrow( () ->
            new RuntimeException( tryList.getCause() ) );
      final List<String> result3 = new ArrayList<>();
      for ( final String element : valueList ) {
         result3.add( element.toUpperCase() );
      }
      // end::handlingTry[]
   }
}
