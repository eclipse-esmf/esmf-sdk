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

package org.eclipse.esmf;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The CLI integration tests that are executed by Maven Failsafe.
 * The tests either execute the CLI's executable jar (using the {@link ExecutableJarLauncher}) or the CLI's native binary (using the
 * {@link BinaryLauncher}). Which one is executed is determined using the system property "packaging-type" which can be "jar" or "native".
 * See the documentation of the respective launchers on how they are configured.
 */
@ExtendWith( LogExtension.class )
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
public class SammCliIntegrationTest extends SammCliTest {
   @BeforeEach
   @Override
   public void beforeEach() throws IOException {
      sammCli = Optional.ofNullable( System.getProperty( "packaging-type" ) ).orElse( "jar" ).equals( "jar" )
            ? new ExecutableJarLauncher()
            : new BinaryLauncher();
      outputDirectory = Files.createTempDirectory( "junit" );
   }
}
