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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ProcessExecutionException;
import org.eclipse.esmf.aspectmodel.resolver.process.ExecutableJarLauncher;
import org.eclipse.esmf.aspectmodel.resolver.process.ProcessLauncher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The CLI integration tests that are executed by Maven Failsafe.
 * The tests execute the CLI's executable jar (using the {@link ExecutableJarLauncher}).
 */
@ExtendWith( LogExtension.class )
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
public class SammCliIntegrationTest extends SammCliAbstractTest {
   @Override
   protected ProcessLauncher<?> getCli() {
      final String jarFile = System.getProperty( "executableJar" );
      if ( jarFile == null || !new File( jarFile ).exists() ) {
         throw new ProcessExecutionException( "Executable jar " + jarFile + " not found" );
      }
      return new ExecutableJarLauncher( new File( jarFile ), List.of( "-Djava.awt.headless=true" ), true );
   }

   @Test
   void testAspectValidateValidModel() {
      final ProcessLauncher.ExecutionResult result =
            sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile, "validate" );
      assertThat( result.stdout() ).contains( "Input model is valid" );
      assertThat( result.stderr() ).isEmpty();
   }
}
