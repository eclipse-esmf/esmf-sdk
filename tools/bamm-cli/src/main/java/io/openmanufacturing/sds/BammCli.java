/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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
import java.util.Properties;

import org.fusesource.jansi.AnsiConsole;

import guru.nidi.graphviz.engine.Graphviz;
import io.openmanufacturing.sds.aspect.AspectCommand;
import io.openmanufacturing.sds.substitution.GraalVmJsGraphvizEngine;
import picocli.CommandLine;

@CommandLine.Command( name = BammCli.COMMAND_NAME,
      description = "Command line tool for working with Aspect Models",
      subcommands = { CommandLine.HelpCommand.class },
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      footer = "%nRun @|bold " + BammCli.COMMAND_NAME + " help <command>|@ to display its help."
            + "%nDocumentation: https://openmanufacturingplatform.github.io/sds-documentation/"
)
@SuppressWarnings( "squid:S1147" ) // System.exit is really required here, this is a CLI tool
public class BammCli extends AbstractCommand {
   public static final String COMMAND_NAME = "bamm";

   private final CommandLine commandLine;

   public BammCli() {
      final CommandLine initialCommandLine = new CommandLine( this )
            .addSubcommand( new AspectCommand() )
            .setCaseInsensitiveEnumValuesAllowed( true )
            .setExecutionStrategy( LoggingMixin::executionStrategy );
      final CommandLine.IExecutionExceptionHandler defaultExecutionExceptionHandler = initialCommandLine.getExecutionExceptionHandler();
      commandLine = initialCommandLine.setExecutionExceptionHandler( new CommandLine.IExecutionExceptionHandler() {
         @Override
         public int handleExecutionException( final Exception exception, final CommandLine commandLine, final CommandLine.ParseResult parseResult )
               throws Exception {
            if ( exception.getClass().getName()
                  .equals( String.format( "%s.MainClassProcessLauncher$SystemExitCaptured", BammCli.class.getPackageName() ) ) ) {
               // If the exception we encounter is a SystemExitCaptured, this is part of the security manager in the test suite that
               // captures System.exit() calls and throws an exception there. We don't want PicoCli to do anything further with that
               // (i.e., serialize the stacktrace to stderr), so we'll just return here.
               return 1;
            }
            // Delegate to the default execution exception handler
            return defaultExecutionExceptionHandler.handleExecutionException( exception, commandLine, parseResult );
         }
      } );
   }

   @CommandLine.Mixin
   LoggingMixin loggingMixin;

   @CommandLine.Option( names = { "--version" }, description = "Show current version" )
   private boolean version;

   int run( final String... argv ) {
      return commandLine.execute( argv );
   }

   int runWithExceptionHandler( final CommandLine.IExecutionExceptionHandler exceptionHandler, final String... argv ) {
      final CommandLine.IExecutionExceptionHandler oldHandler = commandLine.getExecutionExceptionHandler();
      try {
         commandLine.setExecutionExceptionHandler( exceptionHandler );
         return commandLine.execute( argv );
      } finally {
         commandLine.setExecutionExceptionHandler( oldHandler );
      }
   }

   public static void main( final String[] argv ) {
      setupGraphvizJava();
      AnsiConsole.systemInstall();

      final BammCli command = new BammCli();
      final int exitCode = command.commandLine.execute( argv );
      AnsiConsole.systemUninstall();
      System.exit( exitCode );
   }

   private static void setupGraphvizJava() {
      Graphviz.useEngine( new GraalVmJsGraphvizEngine() );
   }

   protected String format( final String string ) {
      return commandLine.getColorScheme().ansi().string( string );
   }

   private Properties loadProperties( final String filename ) {
      final Properties properties = new Properties();
      final InputStream propertiesResource = BammCli.class.getClassLoader().getResourceAsStream( filename );
      try {
         properties.load( propertiesResource );
      } catch ( final IOException exception ) {
         throw new RuntimeException( "Failed to load Properties: " + filename );
      }
      return properties;
   }

   @Override
   public void run() {
      if ( version ) {
         final Properties applicationProperties = loadProperties( "application.properties" );
         final Properties gitProperties = loadProperties( "git.properties" );
         System.out.printf( "bamm-cli - %s%nVersion: %s%nBuild date: %s%nGit commit: %s%n",
               applicationProperties.get( "application.name" ),
               applicationProperties.get( "version" ),
               applicationProperties.get( "build.date" ),
               gitProperties.get( "git.commit.id" ) );
         System.exit( 0 );
      }
      System.out.println( commandLine.getHelp().fullSynopsis() );
      System.out.println( format( "Run @|bold " + commandLine.getCommandName() + " help|@ for help." ) );
   }
}
