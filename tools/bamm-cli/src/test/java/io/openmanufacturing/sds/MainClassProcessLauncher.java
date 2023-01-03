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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Permission;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ProcessLauncher} that executes the static main(String[] args) function of a given class. It installs a {@link SecurityManager} that
 * captures System.exit() calls from the tested code.
 * NOTE: The SecurityManager class is <a href="https://openjdk.org/jeps/411">deprecated for removal</a> as of Java 17, however, no replacement
 * mechanism for capturing System.exit() <a href="https://bugs.openjdk.org/browse/JDK-8199704">has been defined yet</a>. Therefore, there is currently
 * no other way than using the deprecated class.
 */
public class MainClassProcessLauncher extends ProcessLauncher {
   private static final Logger LOG = LoggerFactory.getLogger( MainClassProcessLauncher.class );
   private final Class<?> mainClass;

   // JAnsi also does some equilibristics with the System.out and System.err and caches the results, so make sure the debug streams
   // remain the same throughout the execution of all tests so as to not conflict with these JAnsi cached streams.
   static private final ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
   static private final ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();
   static private final PrintStream testOut = new PrintStream( stdoutBuffer );
   static private final PrintStream testErr = new PrintStream( stderrBuffer );

   public MainClassProcessLauncher( final Class<?> mainClass ) {
      this.mainClass = mainClass;
   }

   @Override
   public ExecutionResult apply( final ExecutionContext context ) {
      final SecurityManager originalSecurityManager = System.getSecurityManager();

      final CaptureSystemExit securityManager = new CaptureSystemExit( originalSecurityManager );
      System.setSecurityManager( securityManager );

      final PrintStream originalStdout = System.out;
      final PrintStream originalStderr = System.err;
      final InputStream originalStdin = System.in;
      final String originalUserDir = System.getProperty( "user.dir" );
      stdoutBuffer.reset();
      stderrBuffer.reset();
      LOG.info( "Launch class with args: {} {}", mainClass.getName(), context.arguments().stream()
            .map( argument -> String.format( "\"%s\"", argument ) ).collect( Collectors.joining( " " ) ) );
      System.setOut( testOut );
      System.setErr( testErr );

      if ( context.stdin().isPresent() ) {
         System.setIn( new ByteArrayInputStream( context.stdin().get() ) );
      }

      try {
         System.setProperty( "user.dir", context.workingDirectory().getAbsolutePath() );
         final Method method = mainClass.getMethod( "main", String[].class );
         method.invoke( null, (Object) context.arguments().toArray( new String[0] ) );
      } catch ( final InvocationTargetException e ) {
         // Ignore System.exit
         if ( !e.getCause().getClass().equals( SystemExitCaptured.class ) ) {
            fail( e );
         }
      } catch ( final NoSuchMethodException | IllegalAccessException e ) {
         fail( e );
      } finally {
         System.setSecurityManager( originalSecurityManager );
         System.setOut( originalStdout );
         System.setErr( originalStderr );
         if ( context.stdin().isPresent() ) {
            System.setIn( originalStdin );
         }
         System.setProperty( "user.dir", originalUserDir );
      }

      return new ExecutionResult( securityManager.getExitCode(), stdoutBuffer.toString(), stderrBuffer.toString() );
   }

   private static class CaptureSystemExit extends SecurityManager {
      private final SecurityManager delegateSecurityManager;
      private int exitCode = 0;

      CaptureSystemExit( final SecurityManager delegateSecurityManager ) {
         this.delegateSecurityManager = delegateSecurityManager;
      }

      int getExitCode() {
         return exitCode;
      }

      @Override
      public void checkPermission( final Permission permission ) {
         if ( delegateSecurityManager != null ) {
            delegateSecurityManager.checkPermission( permission );
         }
      }

      @Override
      public void checkExit( final int i ) {
         exitCode = i;
         throw new SystemExitCaptured();
      }
   }

   private static class SystemExitCaptured extends RuntimeException {
      private static final long serialVersionUID = 7327597963660480174L;
   }
}
