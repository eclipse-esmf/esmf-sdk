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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ProcessExecutionException;
import org.eclipse.esmf.aspectmodel.resolver.process.BinaryLauncher;
import org.eclipse.esmf.aspectmodel.resolver.process.ExecutableJarLauncher;

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
      if ( Optional.ofNullable( System.getProperty( "packaging-type" ) ).orElse( "jar" ).equals( "jar" ) ) {
         final String jarFile = System.getProperty( "executableJar" );
         if ( jarFile == null || !new File( jarFile ).exists() ) {
            throw new ProcessExecutionException( "Executable jar " + jarFile + " not found" );
         }
         sammCli = new ExecutableJarLauncher( new File( jarFile ), List.of( "-Djava.awt.headless=true" ) );
      } else {
         String binary = System.getProperty( "binary" );
         if ( System.getProperty( "os.name" ).startsWith( "Windows" ) ) {
            binary = binary.replace( "/", "\\" );
            binary = binary + ".exe";
         }
         final File binaryFile = new File( binary );
         if ( binary == null || !binaryFile.exists() ) {
            throw new ProcessExecutionException( "Binary " + binary + " not found" );
         }
         sammCli = new BinaryLauncher( binaryFile );
      }
      outputDirectory = Files.createTempDirectory( "junit" );
   }
}
