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

package org.eclipse.esmf.test;

import java.util.Arrays;
import java.util.stream.Stream;

import org.eclipse.esmf.samm.KnownVersion;

public abstract class MetaModelVersions {

   protected MetaModelVersions() {
   }

   protected static Stream<KnownVersion> allVersions() {
      return Arrays.stream( KnownVersion.values() ).dropWhile( KnownVersion.SAMM_1_0_0::isNewerThan );
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
   protected static Stream<KnownVersion> versionsStartingWith2_0_0() {
      return versionsStartingWith( KnownVersion.SAMM_2_0_0 );
   }

   protected static Stream<KnownVersion> versionsUpToIncluding1_0_0() {
      return versionsUpToIncluding( KnownVersion.SAMM_1_0_0 );
   }

   protected static Stream<KnownVersion> versionsUpToIncluding2_0_0() {
      return versionsUpToIncluding( KnownVersion.SAMM_2_0_0 );
   }
}
