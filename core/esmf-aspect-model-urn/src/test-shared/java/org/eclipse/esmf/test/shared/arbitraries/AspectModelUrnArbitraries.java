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

package org.eclipse.esmf.test.shared.arbitraries;

import java.util.List;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

/**
 * Provides {@link Arbitrary}s for Aspect Model URNs.
 */
public interface AspectModelUrnArbitraries {
   @Provide
   default Arbitrary<String> anyUrnVersion() {
      final Arbitrary<Integer> major = Arbitraries.integers().between( 0, 20 );
      final Arbitrary<Integer> minor = Arbitraries.integers().between( 0, 20 );
      final Arbitrary<Integer> maintenance = Arbitraries.integers().between( 0, 20 );
      return Combinators.combine( major, minor, maintenance )
            .as( ( i1, i2, i3 ) -> String.format( "%d.%d.%d", i1, i2, i3 ) );
   }

   @Provide
   default Arbitrary<String> anyUrnNamespacePart() {
      // Regex as defined in AspectModelUrn: "[a-zA-Z][a-zA-Z0-9]{1,62}\\.[a-zA-Z0-9-]{1,63}(\\.[a-zA-Z0-9_-]{1,63})*"
      final Arbitrary<String> firstSection = Combinators.combine(
            Arbitraries.strings().withCharRange( 'a', 'z' ).withCharRange( 'A', 'Z' ).ofLength( 1 ),
            Arbitraries.strings().withCharRange( 'a', 'z' ).withCharRange( 'A', 'Z' ).numeric().ofMinLength( 1 ).ofMaxLength( 62 )
      ).as( ( firstChar, subsequentChars ) -> firstChar + subsequentChars );

      final Arbitrary<String> secondSection = Arbitraries.strings().withCharRange( 'a', 'z' )
            .withCharRange( 'A', 'Z' ).numeric().withChars( '-' ).ofMinLength( 1 ).ofMaxLength( 63 ).map( section -> "." + section );

      final Arbitrary<String> subsequentSection = Arbitraries.strings().withCharRange( 'a', 'z' ).withCharRange( 'A', 'Z' )
            .numeric().withChars( '_', '-' ).ofMinLength( 1 ).ofMaxLength( 63 ).map( section -> "." + section );
      final Arbitrary<List<String>> subsequentSections = subsequentSection.list().ofMinSize( 0 ).ofMaxSize( 3 );
      return Combinators.combine( firstSection, secondSection, subsequentSections ).as( ( first, second, subsequent ) ->
            first + second + String.join( "", subsequent ) );
   }

   @Provide
   default Arbitrary<String> anyMetaModelUrnType() {
      return Arbitraries.of( "meta-model", "characteristic", "entity", "unit" );
   }

   @Provide
   default Arbitrary<String> anyModelElementName() {
      // Regex as defined in AspectModelUrn: "\\p{Alpha}\\p{Alnum}*"
      return Combinators.combine(
            Arbitraries.strings().alpha().ofLength( 1 ),
            Arbitraries.strings().alpha().numeric().ofMinLength( 0 ).ofMaxLength( 5 )
      ).as( ( first, second ) -> first + second );
   }

   default Arbitrary<String> anyModelElementUrnForElementName( final String elementName ) {
      return Combinators.combine( anyUrnNamespacePart(), anyUrnVersion() )
            .as( ( namespace, version ) -> String.format( "urn:samm:%s:%s#%s", namespace, version, elementName ) )
            .filter( urn -> urn.length() <= 256 );
   }

   @Provide
   default Arbitrary<String> anyModelElementUrn() {
      return Combinators.combine( anyUrnNamespacePart(), anyUrnVersion(), anyModelElementName() )
            .as( ( namespace, version, elementName ) ->
                  String.format( "urn:samm:%s:%s#%s", namespace, version, elementName ) )
            .filter( urn -> urn.length() <= 256 );
   }

   @Provide
   default Arbitrary<String> anyMetaModelElementUrn() {
      return Combinators.combine( anyUrnNamespacePart(), anyMetaModelUrnType(), anyUrnVersion(), anyModelElementName() )
            .as( ( namespace, urnType, version, elementName ) ->
                  String.format( "urn:samm:%s:%s:%s#%s", namespace, urnType, version, elementName ) )
            .filter( urn -> urn.length() <= 256 );
   }

   @Provide
   default Arbitrary<String> anyAspectModelUrn() {
      return Arbitraries.oneOf( anyMetaModelElementUrn(), anyModelElementUrn() );
   }
}
