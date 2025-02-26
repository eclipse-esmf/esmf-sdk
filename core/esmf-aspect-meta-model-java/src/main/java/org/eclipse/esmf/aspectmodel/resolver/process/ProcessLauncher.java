/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.resolver.process;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ProcessExecutionException;

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
      final ExecutionResult result = apply( arguments );
      if ( result.exitStatus() != 0 ) {
         throw new ProcessExecutionException(
               "Execution failed (status " + result.exitStatus + "):\n"
                     + "stdout:"
                     + result.stdout()
                     + "\n"
                     + "stderr:"
                     + result.stderr()
         );
      }
      return result;
   }

   public record ExecutionContext( List<String> arguments, Optional<byte[]> stdin, File workingDirectory ) {
   }

   public record ExecutionResult( int exitStatus, String stdout, String stderr, byte[] stdoutRaw, byte[] stderrRaw ) {
   }
}
