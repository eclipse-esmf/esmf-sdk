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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The tests for the CLI that are executed by Maven Surefire. They work using the {@link MainClassProcessLauncher}, i.e. directly call
 * the main function in {@link BammCli}.
 */
@ExtendWith( LogExtension.class )
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
public class BammCliTest {
   protected ProcessLauncher bammCli;

   @BeforeAll
   public void setup() {
      bammCli = new MainClassProcessLauncher( BammCli.class );
   }

   @Test
   public void testNoArgs() {
      final ProcessLauncher.ExecutionResult result = bammCli.apply();
      assertThat( result.exitStatus() ).isEqualTo( 0 );
      assertThat( result.stdout() ).contains( "Usage:" );
      assertThat( result.stderr() ).isEmpty();
   }

   @Test
   public void testAspectWithoutSubcommand() {
      final ProcessLauncher.ExecutionResult result = bammCli.apply( "aspect" );
      assertThat( result.exitStatus() ).isEqualTo( 2 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "Missing required parameter" );
   }

   @Test
   public void testWrongArgs() {
      final ProcessLauncher.ExecutionResult result = bammCli.apply( "-i", "doesnotexist" );
      assertThat( result.exitStatus() ).isEqualTo( 2 );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).contains( "Unknown options" );
   }
}
