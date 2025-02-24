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

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.eclipse.esmf.aspectmodel.resolver.exceptions.ProcessExecutionException;
import org.eclipse.esmf.aspectmodel.resolver.process.BinaryLauncher;
import org.eclipse.esmf.aspectmodel.resolver.process.ExecutableJarLauncher;
import org.eclipse.esmf.aspectmodel.resolver.process.ProcessLauncher;

/**
 * Executes an external resolver via the underlying OS command and returns the stdout from the command as result.
 */
public class CommandExecutor {
   public static String executeCommand( final String command ) {
      final List<String> parts = Arrays.asList( command.split( " " ) );
      final String executableOrJar = parts.get( 0 );
      final ProcessLauncher processLauncher = executableOrJar.toLowerCase().endsWith( ".jar" )
            ? new ExecutableJarLauncher( new File( executableOrJar ) )
            : new BinaryLauncher( new File( executableOrJar ) );
      final List<String> arguments = parts.size() == 1
            ? List.of()
            : parts.subList( 1, parts.size() );
      final ProcessLauncher.ExecutionContext context = new ProcessLauncher.ExecutionContext(
            arguments, Optional.empty(), new File( System.getProperty( "user.dir" ) ) );
      final ProcessLauncher.ExecutionResult result = processLauncher.apply( context );

      if ( result.exitStatus() == 0 ) {
         return result.stdout();
      }

      throw new ProcessExecutionException( "Execution of '" + executableOrJar + "' failed (status " + result.exitStatus() + "). "
            + "Error output: " + result.stderr() );
   }

   private static boolean isJarInvocation( final String command ) {
      final StringTokenizer st = new StringTokenizer( command, " " );
      if ( st.hasMoreTokens() ) {
         return st.nextToken().toUpperCase().endsWith( ".JAR" );
      }
      return false;
   }

   private static String getOutputFrom( final InputStream stream ) {
      try ( final Scanner s = new Scanner( stream ).useDelimiter( "\\A" ) ) {
         return s.hasNext() ? s.next() : "";
      }
   }
}
