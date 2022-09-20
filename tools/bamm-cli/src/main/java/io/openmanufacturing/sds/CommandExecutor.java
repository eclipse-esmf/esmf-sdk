/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.StringTokenizer;

import io.openmanufacturing.sds.exception.CommandException;

// Executes an external resolver via the underlying OS command and returns the stdout from the command as result.
public class CommandExecutor {

   public static String executeCommand( String command ) {
      // convenience: if just the name of the jar is given, expand to the proper java invocation command
      if ( isJarInvocation( command ) ) {
         command = "java -jar " + command;
      }

      try {
         final Process p = Runtime.getRuntime().exec( command );
         final int result = p.waitFor();
         if ( result != 0 ) {
            throw new CommandException( getOutputFrom( p.getErrorStream() ) );
         }
         return getOutputFrom( p.getInputStream() );
      } catch ( final IOException | InterruptedException e ) {
         throw new CommandException( "The attempt to execute external resolver failed with the error:", e );
      }
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
