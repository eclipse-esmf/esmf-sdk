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

package org.eclipse.esmf.test.shared.arbitraries;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

/**
 * Provides {@link Arbitrary}s for URI/URL/URN-related values.
 */
public interface UriArbitraries {

   @Provide
   default Arbitrary<String> anyIpV4Address() {
      final Arbitrary<Integer> numericPart = Arbitraries.integers().between( 0, 255 );
      return Combinators.combine( numericPart, numericPart, numericPart, numericPart )
                        .as( ( part1, part2, part3, part4 ) ->
                              String.format( "%d.%d.%d.%d", part1, part2, part3, part3 ) );
   }

   @Provide
   default Arbitrary<String> anyIpV6Address() {
      return Arbitraries.of( "1:2:3:4:5:6:7:8", "1:2:3:4:5:6:7::", "1:2:3:4:5:6::8", "1:2:3:4:5::7:8",
            "1:2:3:4:5::8", "1:2:3:4::6:7:8", "1:2:3:4::8", "1:2:3::5:6:7:8", "1:2:3::8", "1:2::4:5:6:7:8", "1:2::8",
            "1::", "1::3:4:5:6:7:8", "1::3:4:5:6:7:8", "1::4:5:6:7:8", "1::5:6:7:8", "1::6:7:8", "1::7:8", "1::8",
            "2001:db8:3:4::192.0.2.33", "64:ff9b::192.0.2.33", "::", "::255.255.255.255", "::2:3:4:5:6:7:8",
            "::2:3:4:5:6:7:8", "::8", "::ffff:0:255.255.255.255", "::ffff:255.255.255.255", "fe80::7:8%1",
            "fe80::7:8%eth0" ).map( pattern -> String.format( "[%s]", pattern ) );
   }

   @Provide
   default Arbitrary<String> anyHostname() {
      final Arbitrary<String> anyPart = Arbitraries.strings().alpha().withChars( '-' )
                                                   .ofMinLength( 3 ).ofMaxLength( 8 )
                                                   .filter( part -> !part.startsWith( "-" ) && !part.endsWith( "-" ) );
      return anyPart.list().ofMinSize( 1 ).ofMaxSize( 4 ).map( list -> String.join( ".", list ) );
   }

   @Provide
   default Arbitrary<String> anyHost() {
      return Arbitraries.oneOf( anyHostname(), anyIpV4Address(), anyIpV6Address() );
   }

   @Provide
   default Arbitrary<String> anyUrlScheme() {
      return Arbitraries.of( "http", "https" );
   }

   @Provide
   default Arbitrary<String> anyUrlPath() {
      return Arbitraries.strings().alpha().numeric().ofMinLength( 1 ).ofMaxLength( 5 )
                        .map( part -> '/' + part ).list().ofMinSize( 0 ).ofMaxSize( 5 )
                        .map( partsList -> String.join( "", partsList ) );
   }

   @Provide
   default Arbitrary<String> anyUrlFragment() {
      return Arbitraries.strings().alpha().numeric().withChars( '-', '_' ).ofMinLength( 0 )
                        .ofMaxLength( 5 ).map( anchor -> !anchor.isEmpty() ? '#' + anchor : anchor );
   }

   @Provide
   default Arbitrary<String> anyUrl() {
      return Combinators.combine( anyUrlScheme(), anyHost(), anyUrlPath(), anyUrlFragment() )
                        .as( ( protocol, hostname, path, fragment ) ->
                              String.format( "%s://%s%s%s", protocol, hostname, path, fragment ) );
   }

   @Provide
   default Arbitrary<String> anyUrn() {
      final Arbitrary<String> anyIdentifier = Arbitraries.strings().ofMinLength( 1 ).ofMaxLength( 5 ).alpha();
      final Arbitrary<String> anySpecificPart = Arbitraries.strings();
      return Combinators.combine( anyIdentifier, anySpecificPart )
                        .as( ( identifier, specificPart ) -> String.format( "urn:%s:%s", identifier, specificPart ) );
   }

   @Provide
   default Arbitrary<String> anyUri() {
      return Arbitraries.oneOf( anyUrl(), anyUrn() );
   }
}
