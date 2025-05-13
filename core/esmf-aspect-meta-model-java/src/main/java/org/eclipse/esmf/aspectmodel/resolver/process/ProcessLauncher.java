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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ProcessExecutionException;

/**
 * This class abstracts running a "process", i.e. running a program by providing its arguments, optional stdin and its working directory,
 * and representing the output using exit status and stdout and stderr streams.
 *
 * @param <T> the type of "process" that is created (such as {@link Process} or {@link Thread})
 */
public abstract class ProcessLauncher<T> implements Function<ProcessLauncher<T>.ExecutionContext, ProcessLauncher.ExecutionResult> {
   /**
    * Convenience method to apply the launcher to a list of arguments with empty stdin and default working directory
    */
   public ExecutionResult apply( final String... arguments ) {
      return apply( new ExecutionContext( Arrays.asList( arguments ), Optional.empty(), new File( System.getProperty( "user.dir" ) ) ) );
   }

   public ExecutionResult runAndExpectSuccess( final String... arguments ) {
      final ExecutionResult result = apply( arguments );
      if ( result.exitStatus() != 0 ) {
         throw new ProcessExecutionException(
               "Execution failed (status %d):\nstdout:%s\nstderr:%s".formatted( result.exitStatus, result.stdout(), result.stderr() ) );
      }
      return result;
   }

   public ExecutionResult apply( final List<String> arguments, final Optional<byte[]> stdin, final File workingDirectory,
         final Consumer<T> processConsumer ) {
      return apply( new ExecutionContext( arguments, stdin, workingDirectory, Optional.of( processConsumer ) ) );
   }

   public ExecutionResult apply( final List<String> arguments, final Optional<byte[]> stdin, final File workingDirectory ) {
      return apply( new ExecutionContext( arguments, stdin, workingDirectory ) );
   }

   /**
    * The execution context determines inputs for the new process: arguments, stdin and working directory
    */
   public final class ExecutionContext {
      private final List<String> arguments;
      private final Optional<byte[]> stdin;
      private final File workingDirectory;
      private final Optional<Consumer<T>> processConsumer;

      public ExecutionContext( final List<String> arguments, final Optional<byte[]> stdin, final File workingDirectory,
            final Optional<Consumer<T>> processConsumer ) {
         this.arguments = arguments;
         this.stdin = stdin;
         this.workingDirectory = workingDirectory;
         this.processConsumer = processConsumer;
      }

      public ExecutionContext( final List<String> arguments, final Optional<byte[]> stdin, final File workingDirectory ) {
         this( arguments, stdin, workingDirectory, Optional.empty() );
      }

      public List<String> arguments() {
         return arguments;
      }

      public Optional<byte[]> stdin() {
         return stdin;
      }

      public File workingDirectory() {
         return workingDirectory;
      }

      public Optional<Consumer<T>> processConsumer() {
         return processConsumer;
      }

      @Override
      public boolean equals( final Object obj ) {
         if ( obj == this ) {
            return true;
         }
         if ( obj == null || obj.getClass() != getClass() ) {
            return false;
         }
         @SuppressWarnings( "unchecked" ) final ExecutionContext that = (ExecutionContext) obj;
         return Objects.equals( arguments, that.arguments ) &&
               Objects.equals( stdin, that.stdin ) &&
               Objects.equals( workingDirectory, that.workingDirectory ) &&
               Objects.equals( processConsumer, that.processConsumer );
      }

      @Override
      public int hashCode() {
         return Objects.hash( arguments, stdin, workingDirectory, processConsumer );
      }

      @Override
      public String toString() {
         return "ExecutionContext[" +
               "arguments=" + arguments + ", " +
               "stdin=" + stdin + ", " +
               "workingDirectory=" + workingDirectory + ", " +
               "processConsumer=" + processConsumer + ']';
      }
   }

   /**
    * The execution result provides the process' status and stderr and stdout results
    */
   public record ExecutionResult(
         int exitStatus,
         String stdout,
         String stderr,
         byte[] stdoutRaw,
         byte[] stderrRaw
   ) {}
}
