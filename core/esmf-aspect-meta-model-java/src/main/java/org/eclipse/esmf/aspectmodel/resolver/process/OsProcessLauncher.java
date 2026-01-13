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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ProcessExecutionException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ProcessLauncher} that spawns an external operating system process
 */
public class OsProcessLauncher extends ProcessLauncher<Process> {
   private static final Logger LOG = LoggerFactory.getLogger( OsProcessLauncher.class );
   private final List<String> commandWithArguments;

   public OsProcessLauncher( final List<String> commandWithArguments, final boolean diableWarning ) {
      if ( diableWarning ) {
         // Temporary disable warning messages from the output error stream until https://github.com/oracle/graal/issues/12623 is resolved
         // Delete these two arguments in github actions too
         List<String> modifiedCommand = new ArrayList<>( commandWithArguments );
         modifiedCommand.add( 1, "--enable-native-access=ALL-UNNAMED" );
         modifiedCommand.add( 2, "--sun-misc-unsafe-memory-access=allow" );
         this.commandWithArguments = modifiedCommand;
      } else {
         this.commandWithArguments = commandWithArguments;
      }
   }

   protected File workingDirectoryForSubprocess( final ExecutionContext context ) {
      return context.workingDirectory();
   }

   @Override
   public ExecutionResult apply( final ExecutionContext context ) {
      try {
         final List<String> commandWithAllArguments = new ArrayList<>();
         commandWithAllArguments.addAll( commandWithArguments );
         commandWithAllArguments.addAll( context.arguments() );
         LOG.info( "Launch process in working dir {} with args: {} {}",
               context.workingDirectory(), commandWithAllArguments.get( 0 ), commandWithAllArguments.stream()
                     .skip( 1 )
                     .map( argument -> String.format( "\"%s\"", argument ) )
                     .collect( Collectors.joining( " " ) ) );
         final Process process = Runtime.getRuntime()
               .exec( commandWithAllArguments.toArray( new String[0] ), null, workingDirectoryForSubprocess( context ) );
         if ( context.stdin().isPresent() ) {
            IOUtils.copy( new ByteArrayInputStream( context.stdin().get() ), process.getOutputStream() );
         }
         process.getOutputStream().close();

         // Reading process' stdout and stderr must take place in separate threads, since the length of their content could be larger
         // than the size of the inputstreams's internal buffer
         final ExecutorService executor = Executors.newFixedThreadPool( 2 );
         final Future<ByteArrayOutputStream> stdoutFuture = executor.submit( new StreamAccumulator( process.getInputStream() ) );
         final Future<ByteArrayOutputStream> stderrFuture = executor.submit( new StreamAccumulator( process.getErrorStream() ) );

         LOG.debug( "Waiting for process {} to finish", process.pid() );
         context.processConsumer().ifPresent( consumer -> consumer.accept( process ) );
         process.waitFor();

         final byte[] stdoutRaw;
         final byte[] stderrRaw;
         try {
            stdoutRaw = stdoutFuture.get().toByteArray();
            stderrRaw = stderrFuture.get().toByteArray();
         } catch ( final ExecutionException | InterruptedException exception ) {
            throw new ProcessExecutionException( exception );
         } finally {
            executor.shutdown();
         }

         return new ExecutionResult( process.exitValue(), new String( stdoutRaw, StandardCharsets.UTF_8 ),
               new String( stderrRaw, StandardCharsets.UTF_8 ),
               stdoutRaw, stderrRaw );
      } catch ( final IOException | InterruptedException exception ) {
         throw new ProcessExecutionException( exception );
      }
   }

   /**
    * Reads an InputStream until it's finished
    */
   static class StreamAccumulator implements Callable<ByteArrayOutputStream> {
      private final InputStream in;

      StreamAccumulator( final InputStream in ) {
         this.in = in;
      }

      @Override
      public ByteArrayOutputStream call() throws Exception {
         try ( in ) {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            in.transferTo( buffer );
            return buffer;
         }
      }
   }
}
