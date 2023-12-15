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
package org.eclipse.esmf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.esmf.aas.AasCommand;
import org.eclipse.esmf.aspect.AspectCommand;
import org.eclipse.esmf.aas.AasCommand;

import org.fusesource.jansi.AnsiConsole;

import picocli.CommandLine;

@CommandLine.Command( name = SammCli.COMMAND_NAME,
      description = "Command line tool for working with Aspect Models",
      subcommands = { CommandLine.HelpCommand.class },
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      footer = "%nRun @|bold " + SammCli.COMMAND_NAME + " help <command>|@ to display its help."
            + "%nDocumentation: https://eclipse-esmf.github.io/esmf-documentation/index.html"
)
@SuppressWarnings( "squid:S1147" ) // System.exit is really required here, this is a CLI tool
public class SammCli extends AbstractCommand {
   public static final String COMMAND_NAME = "samm";

   private final CommandLine commandLine;

   public SammCli() {
      final CommandLine initialCommandLine = new CommandLine( this )
            .addSubcommand( new AspectCommand() )
            .addSubcommand( new AasCommand() )
            .setCaseInsensitiveEnumValuesAllowed( true )
            .setExecutionStrategy( LoggingMixin::executionStrategy );
      final CommandLine.IExecutionExceptionHandler defaultExecutionExceptionHandler = initialCommandLine.getExecutionExceptionHandler();
      commandLine = initialCommandLine.setExecutionExceptionHandler( new CommandLine.IExecutionExceptionHandler() {
         @Override
         public int handleExecutionException( final Exception exception, final CommandLine commandLine,
               final CommandLine.ParseResult parseResult )
               throws Exception {
            if ( exception.getClass().getName()
                  .equals( String.format( "%s.MainClassProcessLauncher$SystemExitCaptured", SammCli.class.getPackageName() ) ) ) {
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

   @CommandLine.Option( names = { "--disable-color", "-D" }, description = "Disable colored output" )
   private boolean disableColor;

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
      NativeImageHelpers.ensureRequiredEnvironment();

      // The disabling color switch needs to be checked before PicoCLI initialization
      boolean disableColor = false;
      for ( final String arg : argv ) {
         if ( arg.equals( "--disable-color" ) || arg.equals( "-D" ) ) {
            disableColor = true;
         }
      }

      // Imply disabling color when PNGs are generated, otherwise ANSI escapes scramble binary output
      if ( argv.length >= 4
            && argv[argv.length - 4].equals( "aspect" )
            && argv[argv.length - 2].equals( "to" )
            && argv[argv.length - 1].equals( "png" ) ) {
         disableColor = true;
      }

      if ( !disableColor ) {
         AnsiConsole.systemInstall();
      }

      final SammCli command = new SammCli();
      final int exitCode = command.commandLine.execute( argv );
      if ( !disableColor ) {
         AnsiConsole.systemUninstall();
      }
      System.exit( exitCode );
   }

   protected String format( final String string ) {
      return commandLine.getColorScheme().ansi().string( string );
   }

   private Properties loadProperties( final String filename ) {
      final Properties properties = new Properties();
      final InputStream propertiesResource = SammCli.class.getClassLoader().getResourceAsStream( filename );
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
         System.out.printf( "samm-cli - %s%nVersion: %s%nBuild date: %s%nGit commit: %s%n",
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
