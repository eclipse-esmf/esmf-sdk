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

package org.eclipse.esmf.aspectmodel.urn;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.From;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.StringLength;

/**
 * Property-based tests for AspectModelUrn
 */
public class AspectModelUrnPropertyTest {
   private boolean isValidUrn( final String urn ) {
      try {
         AspectModelUrn.fromUrn( urn );
      } catch ( final Exception e ) {
         return false;
      }
      return true;
   }

   @Property
   public boolean notAllStringsAreValidUrns( @ForAll final String string ) {
      return !isValidUrn( string );
   }

   @Provide
   Arbitrary<String> validModelUrnType() {
      return Arbitraries.of( "aspect-model", "characteristic", "entity" );
   }

   @Provide
   Arbitrary<String> validVersion() {
      final Arbitrary<Integer> major = Arbitraries.integers().greaterOrEqual( 0 );
      final Arbitrary<Integer> minor = Arbitraries.integers().greaterOrEqual( 0 );
      final Arbitrary<Integer> maintenance = Arbitraries.integers().greaterOrEqual( 0 );
      return Combinators.combine( major, minor, maintenance )
            .as( ( i1, i2, i3 ) -> String.format( "%d.%d.%d", i1, i2, i3 ) );
   }

   @Provide
   Arbitrary<String> firstLetter() {
      return Arbitraries.strings().withCharRange( 'a', 'z' ).withCharRange( 'A', 'Z' ).ofLength( 1 );
   }

   @Provide
   Arbitrary<String> subsequentChars() {
      return Arbitraries.strings().alpha().numeric().ofMinLength( 1 ).ofMaxLength( 62 );
   }

   @Provide
   Arbitrary<String> secondPartOfNamespace() {
      return Arbitraries.strings().withChars( '-' ).alpha().numeric().ofMinLength( 1 ).ofMaxLength( 61 );
   }

   @Property
   public boolean allValidModelStringsAreValidModelUrns(
         @ForAll @From( "firstLetter" ) final String firstLetter,
         @ForAll @From( "subsequentChars" ) final String subsequentChars,
         @ForAll @From( "secondPartOfNamespace" ) final String secondPartOfNamespace,
         @ForAll( "validModelUrnType" ) final String urnType,
         @ForAll @AlphaChars @StringLength( min = 1, max = 100 ) final String elementName,
         @ForAll( "validVersion" ) final String version ) {
      final String namespace = firstLetter + subsequentChars + "." + secondPartOfNamespace;
      return isValidUrn( String.format( "urn:samm:%s:%s:%s:%s", namespace, urnType, elementName, version ) );
   }

   @Property
   public boolean allValidMetaModelStringsAreValidMetaModelUrns(
         @ForAll @From( "firstLetter" ) final String firstLetter,
         @ForAll @From( "subsequentChars" ) final String subsequentChars,
         @ForAll @From( "secondPartOfNamespace" ) final String secondPartOfNamespace,
         @ForAll( "validVersion" ) final String version ) {
      final String namespace = firstLetter + subsequentChars + "." + secondPartOfNamespace;
      return isValidUrn( String.format( "urn:samm:%s:meta-model:%s#Foo", namespace, version ) );
   }
}
