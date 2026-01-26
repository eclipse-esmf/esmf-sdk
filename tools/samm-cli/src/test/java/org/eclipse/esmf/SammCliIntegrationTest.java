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
import java.nio.file.Path;
import java.util.List;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ProcessExecutionException;
import org.eclipse.esmf.aspectmodel.resolver.process.BinaryLauncher;
import org.eclipse.esmf.aspectmodel.resolver.process.ExecutableJarLauncher;
import org.eclipse.esmf.aspectmodel.resolver.process.ProcessLauncher;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestModel;

import org.apache.tika.mime.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * The CLI integration tests that are executed by Maven Failsafe.
 * The tests either execute the CLI's executable jar (using the {@link ExecutableJarLauncher}) or the CLI's native binary (using the
 * {@link BinaryLauncher}). Which one is executed is determined using the system property "packaging-type" which can be "jar" or "native".
 * See the documentation of the respective launchers on how they are configured.
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
      return new ExecutableJarLauncher( new File( jarFile ), List.of( "-Djava.awt.headless=true" ) );
   }

   @Test
   void testVerboseOutput() {
      final ProcessLauncher.ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", defaultInputFile,
            "validate", "-vvv" );
      assertThat( result.stdout() ).contains( "Input model is valid" );
      assertThat( result.stderr() ).contains( "DEBUG" );
   }

   @ParameterizedTest
   @EnumSource( TestAspect.class )
   @Execution( ExecutionMode.CONCURRENT )
   void testAspectValidateValidModelAllTestFiles( final TestModel aspect ) {
      final String input = inputFile( aspect ).getAbsolutePath();
      final ProcessLauncher.ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", input, "validate" );
      assertThat( result.stdout() ).contains( "Input model is valid" );
      assertThat( result.stderr() ).isEmpty();
   }

   @ParameterizedTest
   @EnumSource( TestAspect.class )
   @Execution( ExecutionMode.CONCURRENT )
   void testAspectToAasXmlToFileAllTestFiles( final TestModel aspect, @TempDir final Path outputDirectory ) {
      final String input = inputFile( aspect ).getAbsolutePath();
      final File targetFile = outputFile( outputDirectory, "output.xml" );
      assertThat( targetFile ).doesNotExist();
      final ProcessLauncher.ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", input, "to", "aas",
            "--format",
            "xml", "-o", targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( targetFile ).content().startsWith( "<?xml" );
   }

   @ParameterizedTest
   @EnumSource( TestAspect.class )
   @Execution( ExecutionMode.CONCURRENT )
   void testAspectToAasXmlToStdoutAllTestFiles( final TestModel aspect ) {
      final String input = inputFile( aspect ).getAbsolutePath();
      final ProcessLauncher.ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", input, "to", "aas",
            "--format",
            "xml" );
      assertThat( result.stdout() ).startsWith( "<?xml" );
      assertThat( result.stderr() ).isEmpty();
   }

   @ParameterizedTest
   @EnumSource( TestAspect.class )
   @Execution( ExecutionMode.CONCURRENT )
   void testAspectToAasAasxToFileAllTestFiles( final TestModel aspect, @TempDir final Path outputDirectory ) {
      final String input = inputFile( aspect ).getAbsolutePath();
      final File targetFile = outputFile( outputDirectory, "output.aasx" );
      assertThat( targetFile ).doesNotExist();
      final ProcessLauncher.ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", input, "to", "aas",
            "--format",
            "aasx", "-o", targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( contentType( targetFile ) ).isEqualTo( MediaType.application( "x-tika-ooxml" ) );
   }

   @ParameterizedTest
   @EnumSource( TestAspect.class )
   @Execution( ExecutionMode.CONCURRENT )
   void testAspectToAasJsonToFileAllTestFiles( final TestModel aspect, @TempDir final Path outputDirectory ) {
      final String input = inputFile( aspect ).getAbsolutePath();
      final File targetFile = outputFile( outputDirectory, "output.json" );
      assertThat( targetFile ).doesNotExist();
      final ProcessLauncher.ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", input, "to", "aas",
            "--format",
            "json", "-o",
            targetFile.getAbsolutePath() );
      assertThat( result.stdout() ).isEmpty();
      assertThat( result.stderr() ).isEmpty();
      assertThat( targetFile ).exists();
      assertThat( contentType( targetFile ) ).isEqualTo( MediaType.text( "plain" ) );
   }

   @ParameterizedTest
   @EnumSource( TestAspect.class )
   @Execution( ExecutionMode.CONCURRENT )
   void testAspectToAasJsonToStdoutAllTestFiles( final TestModel aspect ) {
      final String input = inputFile( aspect ).getAbsolutePath();
      final ProcessLauncher.ExecutionResult result = sammCli.runAndExpectSuccess( "--disable-color", "aspect", input, "to", "aas",
            "--format", "json" );
      assertThat( result.stderr() ).isEmpty();
      assertThat( contentType( result.stdoutRaw() ) ).isEqualTo( MediaType.text( "plain" ) );
   }
}
