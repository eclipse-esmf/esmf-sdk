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

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * This class abstracts running a "process", i.e. running a program by providing its arguments, optional stdin and its working directory,
 * and representing the output using exit status and stdout and stderr streams.
 */
public abstract class ProcessLauncher implements Function<ProcessLauncher.ExecutionContext, ProcessLauncher.ExecutionResult> {
   /**
    * Convenience constructor that to apply the launcher to a list of arguments with empty stdin and default working directory
    */
   public ExecutionResult apply( final String... arguments ) {
      return apply( new ExecutionContext( Arrays.asList( arguments ), Optional.empty(), new File( System.getProperty( "user.dir" ) ) ) );
   }

   public ExecutionResult runAndExpectSuccess( final String... arguments ) {
      final ExecutionResult result = apply( new ExecutionContext( Arrays.asList( arguments ), Optional.empty(), new File( System.getProperty( "user.dir" ) ) ) );
      if ( result.exitStatus() != 0 ) {
         System.out.printf( "Execution failed (status %d):%n", result.exitStatus() );
         System.out.println( "stdout:" );
         System.out.println( result.stdout() );
         System.out.println();
         System.out.println( "stderr:" );
         System.out.println( result.stderr() );
         fail();
      }
      return result;
   }

   public static record ExecutionContext(List<String> arguments, Optional<byte[]> stdin, File workingDirectory) {
   }

   public static record ExecutionResult(int exitStatus, String stdout, String stderr) {
   }
}
