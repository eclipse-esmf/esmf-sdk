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

package org.eclipse.esmf.aspectmodel;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class VersionNumberTest {

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testToString( final TestVersionNumber versionNumber ) {
      assertThat( versionNumber.versionNumber.toString() ).hasToString( versionNumber.version );
   }

   @ParameterizedTest
   @MethodSource( value = "allVersions" )
   public void testParseAndCompare( final TestVersionNumber versionNumber ) {
      assertThat( VersionNumber.parse( versionNumber.version ) ).isEqualTo( versionNumber.versionNumber );
      assertThat( VersionNumber.parse( versionNumber.version ).compareTo( versionNumber.versionNumber ) )
            .isZero();
      assertThat( VersionNumber.parse( versionNumber.version ).nextMinor().compareTo( versionNumber.versionNumber ) )
            .isEqualTo( 1 );
      assertThat( VersionNumber.parse( versionNumber.version ).nextMicro().compareTo( versionNumber.versionNumber ) )
            .isEqualTo( 1 );
      assertThat( VersionNumber.parse( versionNumber.version ).nextMajor().compareTo( versionNumber.versionNumber ) )
            .isEqualTo( 1 );

      assertThat( versionNumber.versionNumber.compareTo( VersionNumber.parse( versionNumber.version ).nextMajor() ) )
            .isEqualTo( -1 );
      assertThat( versionNumber.versionNumber.compareTo( VersionNumber.parse( versionNumber.version ).nextMinor() ) )
            .isEqualTo( -1 );
      assertThat( versionNumber.versionNumber.compareTo( VersionNumber.parse( versionNumber.version ).nextMicro() ) )
            .isEqualTo( -1 );

      assertThat(
            VersionNumber.parse( versionNumber.version ).nextMinor().greaterThan( versionNumber.versionNumber ) )
            .isTrue();
      assertThat(
            VersionNumber.parse( versionNumber.version ).nextMicro().greaterThan( versionNumber.versionNumber ) )
            .isTrue();
      assertThat(
            VersionNumber.parse( versionNumber.version ).nextMajor().greaterThan( versionNumber.versionNumber ) )
            .isTrue();
   }

   @ParameterizedTest
   @MethodSource( value = "invalidVersions" )
   public void testParseInvalidVersions( final TestVersionNumber versionNumber ) {
      assertThatThrownBy( () -> VersionNumber.parse( versionNumber.version ) )
            .isInstanceOf( UnsupportedVersionException.class );
   }

   @Test
   public void testGreaterThan() {
      assertThat( VersionNumber.parse( "111" ).toString() )
            .hasToString( "111.0.0" );
   }

   private static List<TestVersionNumber> allVersions() {
      return List.of( new TestVersionNumber( new VersionNumber( 1, 1, 1 ), "1.1.1" ),
            new TestVersionNumber( new VersionNumber( 0, 1, 1 ), "0.1.1" ),
            new TestVersionNumber( new VersionNumber( 10, 1, 1 ), "10.1.1" ),
            new TestVersionNumber( new VersionNumber( 100, 0, 0 ), "100.0.0" ),
            new TestVersionNumber( new VersionNumber( 100, 101, 101 ), "100.101.101" ),
            new TestVersionNumber( new VersionNumber( 100, 101, 101 ), "100.101.101" ),
            new TestVersionNumber( new VersionNumber( 8, 10, 3 ), "8.10.3" ),
            new TestVersionNumber( new VersionNumber( 5, 54, 888 ), "5.54.888" ),
            new TestVersionNumber( new VersionNumber( 87, 12, 111 ), "87.12.111" ),
            new TestVersionNumber( new VersionNumber( 5, 0, 1 ), "5.0.1" ),
            new TestVersionNumber( new VersionNumber( 7, 0, 12 ), "7.0.12" ),
            new TestVersionNumber( new VersionNumber( 5, 101, 0 ), "5.101.0" ),
            new TestVersionNumber( new VersionNumber( 8, 1, 0 ), "8.1.0" ) );
   }

   private static List<TestVersionNumber> invalidVersions() {
      return List.of( new TestVersionNumber( "a.1.1" ),
            new TestVersionNumber( "" ),
            new TestVersionNumber( null ),
            new TestVersionNumber( "0.1a.1" ),
            new TestVersionNumber( "1.122.b" ),
            new TestVersionNumber( "1c.1.1" ),
            new TestVersionNumber( "1ccc" ),
            new TestVersionNumber( "1.1.1c" ),
            new TestVersionNumber( "1.1d.1" ),
            new TestVersionNumber( "1.1b1" ),
            new TestVersionNumber( "1..1.1" ),
            new TestVersionNumber( "1...1" ),
            new TestVersionNumber( "1.1.1.1.1" ),
            new TestVersionNumber( "1...1.1" ) );
   }

   private static class TestVersionNumber {
      private VersionNumber versionNumber;
      private final String version;

      public TestVersionNumber( final String version ) {
         this.version = version;
      }

      public TestVersionNumber( final VersionNumber versionNumber, final String version ) {
         this.versionNumber = versionNumber;
         this.version = version;
      }
   }
}
