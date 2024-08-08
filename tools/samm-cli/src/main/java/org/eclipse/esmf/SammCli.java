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

import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_COMMAND_LIST;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.eclipse.esmf.aas.AasCommand;
import org.eclipse.esmf.aas.AasToCommand;
import org.eclipse.esmf.aas.to.AasToAspectCommand;
import org.eclipse.esmf.aspect.AspectCommand;
import org.eclipse.esmf.aspect.AspectPrettyPrintCommand;
import org.eclipse.esmf.aspect.AspectToCommand;
import org.eclipse.esmf.aspect.to.AspectToSvgCommand;
import org.eclipse.esmf.substitution.IsWindows;

import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;

@CommandLine.Command( name = SammCli.COMMAND_NAME,
      description = "Command line tool for working with Aspect Models",
      subcommands = { CommandLine.HelpCommand.class },
      headerHeading = "@|bold Usage|@:%n%n",
      descriptionHeading = "%n@|bold Description|@:%n%n",
      parameterListHeading = "%n@|bold Parameters|@:%n",
      optionListHeading = "%n@|bold Options|@:%n",
      footer = "%nRun @|bold " + SammCli.COMMAND_NAME + " help <command>|@ to display its help, e.g.:%n"
            + "   @|bold " + SammCli.COMMAND_NAME + " help "
            + AspectCommand.COMMAND_NAME + " " + AspectToCommand.COMMAND_NAME + " " + AspectToSvgCommand.COMMAND_NAME + "|@%n"
            + "or @|bold " + SammCli.COMMAND_NAME + " help "
            + AspectCommand.COMMAND_NAME + " " + AspectPrettyPrintCommand.COMMAND_NAME + "|@%n"
            + "or @|bold " + SammCli.COMMAND_NAME + " help "
            + AasCommand.COMMAND_NAME + " " + AasToCommand.COMMAND_NAME + " " + AasToAspectCommand.COMMAND_NAME + "|@%n"
            + "%nDocumentation: https://eclipse-esmf.github.io/esmf-documentation/index.html"
)
@SuppressWarnings( "squid:S1147" ) // System.exit is really required here, this is a CLI tool
public class SammCli extends AbstractCommand {
   public static final String COMMAND_NAME = "samm";

   private final CommandLine commandLine;

   static class CustomCommandListRenderer implements CommandLine.IHelpSectionRenderer {
      @Override
      public String render( final CommandLine.Help help ) {
         final CommandLine.Model.CommandSpec spec = help.commandSpec();
         if ( spec.subcommands().isEmpty() ) {
            return "";
         }

         final CommandLine.Help.TextTable textTable = CommandLine.Help.TextTable.forColumns( help.colorScheme(),
               new CommandLine.Help.Column( 15, 2, CommandLine.Help.Column.Overflow.SPAN ),
               new CommandLine.Help.Column( spec.usageMessage().width() - 15, 2, CommandLine.Help.Column.Overflow.WRAP ) );
         textTable.setAdjustLineBreaksForWideCJKCharacters( spec.usageMessage().adjustLineBreaksForWideCJKCharacters() );
         spec.subcommands().values().forEach( subcommand -> addHierarchy( subcommand, textTable, "" ) );
         return textTable.toString();
      }

      private void addHierarchy( final CommandLine cmd, final CommandLine.Help.TextTable textTable, final String indent ) {
         final String names = cmd.getCommandSpec().names().toString();
         final String formattedNames = names.substring( 1, names.length() - 1 );
         final String description = description( cmd.getCommandSpec().usageMessage() );
         textTable.addRowValues( indent + formattedNames, description );
         cmd.getSubcommands().values().forEach( sub -> addHierarchy( sub, textTable, indent + "  " ) );
      }

      private String description( final CommandLine.Model.UsageMessageSpec usageMessage ) {
         if ( usageMessage.header().length > 0 ) {
            return usageMessage.header()[0];
         }
         if ( usageMessage.description().length > 0 ) {
            return usageMessage.description()[0];
         }
         return "";
      }
   }

   public SammCli() {
      final CommandLine initialCommandLine = new CommandLine( this )
            .addSubcommand( new AspectCommand() )
            .addSubcommand( new AasCommand() )
            .setCaseInsensitiveEnumValuesAllowed( true )
            .setExecutionStrategy( LoggingMixin::executionStrategy );
      initialCommandLine.getHelpSectionMap().put( SECTION_KEY_COMMAND_LIST, new CustomCommandListRenderer() );
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
      // Check if the .exe was started on Windows without arguments: Most likely opened from Explorer or Desktop.
      // If yes, open a command prompt to continue working instead.
      if ( new IsWindows().getAsBoolean() && argv.length == 0 ) {
         ProcessHandle.current().info().command().ifPresent( executable -> {
            // Only spawn terminals for native executable
            if ( !executable.endsWith( "java.exe" ) ) {
               try {
                  final File exeFile = new File( executable );
                  final String directory = exeFile.getParent();
                  final String exeFileName = exeFile.getName();
                  Runtime.getRuntime().exec( "cmd /k cd /d \"" + directory + "\" & start cmd /k " + exeFileName + " help" );
                  System.exit( 0 );
               } catch ( final Exception e ) {
                  // Ignore, continue as usual
               }
            }
         } );
      }

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
      // Explicitly allow 'samm help command subcommand...' also if the subcommand is 'to' (e.g., aspect to, aas to) and
      // usually receives a mandatory input file as its first parameter, e.g.:
      // What a user wants to enter:     What we need to provide to picocli
      // "help aspect to sql"         -> "aspect _ to help sql"
      final String[] adjustedArgv;
      final List<String> argvList = Arrays.asList( argv );
      final int helpAspectToIndex = Collections.indexOfSubList( argvList,
            List.of( "help", AspectCommand.COMMAND_NAME, AspectToCommand.COMMAND_NAME ) );
      final int helpAasToIndex = Collections.indexOfSubList( argvList,
            List.of( "help", AasCommand.COMMAND_NAME, AasToCommand.COMMAND_NAME ) );
      final int helpAspectXIndex = Collections.indexOfSubList( argvList, List.of( "help", AspectCommand.COMMAND_NAME ) );
      final int helpAasXIndex = Collections.indexOfSubList( argvList, List.of( "help", AasCommand.COMMAND_NAME ) );
      if ( helpAspectToIndex != -1 || helpAasToIndex != -1 ) {
         final int index = Integer.max( helpAspectToIndex, helpAasToIndex );
         final List<String> customArgv = new ArrayList<>( argvList.subList( 0, index ) );
         customArgv.add( argvList.get( index + 1 ) );
         customArgv.add( "_" );
         customArgv.add( argvList.get( index + 2 ) );
         customArgv.add( "help" );
         customArgv.addAll( argvList.subList( index + 3, argvList.size() ) );
         adjustedArgv = customArgv.toArray( new String[] {} );
      } else if ( helpAspectXIndex != -1 || helpAasXIndex != -1 ) {
         // "help aspect prettyprint" -> "aspect help prettyprint"
         final int index = Integer.max( helpAspectXIndex, helpAasXIndex );
         final List<String> customArgv = new ArrayList<>( argvList.subList( 0, index ) );
         customArgv.add( argvList.get( index + 1 ) );
         customArgv.add( "help" );
         customArgv.addAll( argvList.subList( index + 2, argvList.size() ) );
         adjustedArgv = customArgv.toArray( new String[] {} );
      } else {
         adjustedArgv = argv;
      }

      final int exitCode = command.commandLine.execute( adjustedArgv );
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
      System.out.println( format( "Run @|bold " + commandLine.getCommandName() + " help|@ for help, e.g.:" ) );
      System.out.println( format( "    @|bold " + commandLine.getCommandName() + " help "
            + AspectCommand.COMMAND_NAME + " " + AspectToCommand.COMMAND_NAME + " " + AspectToSvgCommand.COMMAND_NAME + "|@" ) );
   }
}
