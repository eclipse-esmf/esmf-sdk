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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ProcessLauncher} that spawns an external operating system process
 */
public class OsProcessLauncher extends ProcessLauncher {
   private static final Logger LOG = LoggerFactory.getLogger( OsProcessLauncher.class );
   private final List<String> commandWithArguments;

   public OsProcessLauncher( final List<String> commandWithArguments ) {
      this.commandWithArguments = commandWithArguments;
   }

   @Override
   public ExecutionResult apply( final ExecutionContext context ) {
      try {
         final List<String> commandWithAllArguments = new ArrayList<>();
         commandWithAllArguments.addAll( commandWithArguments );
         commandWithAllArguments.addAll( context.arguments() );
         LOG.info( "Launch process with args: {} {}",
               commandWithAllArguments.get( 0 ), commandWithAllArguments.stream()
                     .skip( 1 )
                     .map( argument -> String.format( "\"%s\"", argument ) )
                     .collect( Collectors.joining( " " ) ) );
         final Process process = Runtime.getRuntime().exec( commandWithAllArguments.toArray( new String[0] ), null, context.workingDirectory() );
         final BufferedReader stdoutReader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
         final BufferedReader stderrReader = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );
         if ( context.stdin().isPresent() ) {
            IOUtils.copy( new ByteArrayInputStream( context.stdin().get() ), process.getOutputStream() );
         }
         process.getOutputStream().close();
         process.waitFor();

         final String stdout = IOUtils.toString( stdoutReader );
         final String stderr = IOUtils.toString( stderrReader );
         return new ExecutionResult( process.exitValue(), stdout, stderr );
      } catch ( final IOException | InterruptedException exception ) {
         fail( exception );
      }
      return null;
   }
}
