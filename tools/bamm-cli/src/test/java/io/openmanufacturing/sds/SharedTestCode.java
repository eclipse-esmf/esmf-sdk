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

package io.openmanufacturing.sds;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * The core test methods that are shared between {@link BammCliUnitTest} and {@link BammCliIntegrationTest}
 */
public class SharedTestCode {
   private final ProcessLauncher launcher;

   public SharedTestCode( final ProcessLauncher launcher ) {
      this.launcher = launcher;
   }

   @Test
   public void testNoArgs() {
      final ProcessLauncher.ExecutionResult result = launcher.apply();
      assertThat( result.exitStatus() ).isEqualTo( 0 );
      assertThat( result.stdout() ).contains( "Usage:" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectWithoutSubcommand() {
      final ProcessLauncher.ExecutionResult result = launcher.apply( "aspect" );
      assertThat( result.exitStatus() ).isEqualTo( 2 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "Missing required parameter" );
   }
}
