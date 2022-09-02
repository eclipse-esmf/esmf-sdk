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
import java.util.Scanner;
import java.util.StringTokenizer;

import io.openmanufacturing.sds.exception.CommandException;

public class CommandExecutor {

   public static String executeCommand( String command ) {
      // convenience: if just the name of the jar is given, expand to the proper java invocation command
      if ( isJarInvocation( command ) ) {
         command = "java -jar " + command;
      }

      try {
         final Process p = Runtime.getRuntime().exec( command );
         final int result = p.waitFor();
         final Scanner s = new Scanner( p.getInputStream() ).useDelimiter( "\\A" );
         final String output = s.hasNext() ? s.next() : "";
         if ( result != 0 ) {
            throw new CommandException( output );
         }
         return output;
      } catch ( final IOException | InterruptedException e ) {
         throw new CommandException( e );
      }
   }

   private static boolean isJarInvocation( final String command ) {
      final StringTokenizer st = new StringTokenizer( command, " " );
      if ( st.hasMoreTokens() ) {
         return st.nextToken().toUpperCase().endsWith( ".JAR" );
      }
      return false;
   }
}
