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

package org.eclipse.esmf.aspectmodel.resolver;

import java.io.InputStream;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.function.FailableBiFunction;

/**
 * Executes an external resolver via the underlying OS command and returns the stdout from the command as result.
 */
public class CommandExecutor {
   public static String executeCommand( String command ) {
      // convenience: if just the name of the jar is given, expand to the proper java invocation command
      if ( isJarInvocation( command ) ) {
         command = String.format( "%s -jar %s", ProcessHandle.current().info().command().orElse( "java" ), command );
      }

      try {
         final Process p = Runtime.getRuntime().exec( command );

         // Start threads to consume the stdout and stderr
         Future<String> stdout = readStreamAsync( p.getInputStream() );
         Future<String> stderr = readStreamAsync( p.getErrorStream() );

         final boolean result = p.waitFor( 60, TimeUnit.SECONDS );
         if ( !result || p.exitValue() != 0 ) {
            FailableBiFunction<String, Future<String>, String, Exception> ifFuture =
                  ( title, future ) -> future.get().isEmpty() ? "" : title + future.get();
            throw new ModelResolutionException( "The attempt to execute external resolver [" + command + "] failed with the exit value: " + p.exitValue() +
                  ifFuture.apply( ", and stdout: ", stdout ) + ifFuture.apply( ", and stderr: ", stderr ) );
         }
         return stdout.get();
      } catch ( ModelResolutionException e ) {
         throw e;
      } catch ( final Exception e ) {
         throw new ModelResolutionException( "The attempt to execute external resolver [" + command + "] failed with the error: " + e.getMessage(), e );
      }
   }

   private static Future<String> readStreamAsync( InputStream stream ) {
      ExecutorService executor = Executors.newSingleThreadExecutor();
      return executor.submit( () -> {
         String output = getOutputFrom( stream );
         executor.shutdown(); ;
         return output;
      } );
   }

   private static boolean isJarInvocation( final String command ) {
      final StringTokenizer st = new StringTokenizer( command, " " );
      if ( st.hasMoreTokens() ) {
         return st.nextToken().toUpperCase().endsWith( ".JAR" );
      }
      return false;
   }

   private static String getOutputFrom( final InputStream stream ) {
      final Scanner s = new Scanner( stream ).useDelimiter( "\\A" );
      return s.hasNext() ? s.next() : "";
   }
}
