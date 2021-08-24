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

package io.openmanufacturing.sds.test;

import java.util.Arrays;
import java.util.stream.Stream;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;

public abstract class MetaModelVersions {

   protected MetaModelVersions() {
   }

   protected static Stream<KnownVersion> allVersions() {
      return Arrays.stream( KnownVersion.values() ).dropWhile( KnownVersion.BAMM_1_0_0::isNewerThan );
   }

   protected static Stream<KnownVersion> versionsStartingWith( final KnownVersion version ) {
      return allVersions().dropWhile( version::isNewerThan );
   }

   protected static Stream<KnownVersion> versionsUpToIncluding( final KnownVersion version ) {
      return allVersions().takeWhile( v -> version.isNewerThan( v ) || v.equals( version ) );
   }

   protected static Stream<KnownVersion> versionsBetween( final KnownVersion start, final KnownVersion end ) {
      return versionsStartingWith( start ).takeWhile( v -> !v.isNewerThan( end ) );
   }

   protected static Stream<KnownVersion> latestVersion() {
      return Stream.of( KnownVersion.getLatest() );
   }

   @SuppressWarnings( "squid:S00100" ) // Underscores are required to make version unambiguous
   protected static Stream<KnownVersion> versionsStartingWith1_0_0() {
      return versionsStartingWith( KnownVersion.BAMM_1_0_0 );
   }
}
