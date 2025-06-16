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

import static picocli.CommandLine.Spec.Target.MIXEE;

import java.util.stream.Stream;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.apache.jena.shared.LockMRSW;
import org.apache.jena.sparql.core.mem.PMapQuadTable;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

public class LoggingMixin {
   private @CommandLine.Spec( MIXEE ) CommandLine.Model.CommandSpec mixee;

   private boolean[] verbosity = new boolean[0];

   private static LoggingMixin getTopLevelCommandLoggingMixin( final CommandLine.Model.CommandSpec commandSpec ) {
      return ( (SammCli) commandSpec.root().userObject() ).loggingMixin;
   }

   public static int executionStrategy( final CommandLine.ParseResult parseResult ) {
      getTopLevelCommandLoggingMixin( parseResult.commandSpec() ).configureLoggers();
      return new CommandLine.RunLast().execute( parseResult );
   }

   @CommandLine.Option( names = { "-v", "--verbose" }, description = {
         "Specify multiple -v options to increase verbosity,",
         "e.g. use `-v`, `-vv` or `-vvv` for more details" } )
   public void setVerbose( final boolean[] verbosity ) {
      getTopLevelCommandLoggingMixin( mixee ).verbosity = verbosity;
   }

   public boolean[] getVerbosity() {
      return getTopLevelCommandLoggingMixin( mixee ).verbosity;
   }

   public Level calcLogLevel() {
      return switch ( getVerbosity().length ) {
         case 0 -> Level.OFF;
         case 1 -> Level.INFO;
         case 2 -> Level.DEBUG;
         default -> Level.TRACE;
      };
   }

   public void configureLoggers() {
      final Level level = getTopLevelCommandLoggingMixin( mixee ).calcLogLevel();
      final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
      final Logger root = loggerContext.getLogger( Logger.ROOT_LOGGER_NAME );
      root.detachAndStopAllAppenders();
      root.setLevel( level );

      // In any case disable messages from these particularly spammy and unhelpful loggers
      Stream.of( LockMRSW.class, PMapQuadTable.class ).forEach( loggerClass -> {
         final Logger logger = loggerContext.getLogger( loggerClass );
         logger.setLevel( Level.OFF );
      } );

      if ( level == Level.OFF ) {
         return;
      }

      final ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
      appender.setName( "ConsoleLogger" );
      appender.setContext( loggerContext );
      appender.setTarget( "System.err" );

      final ThresholdFilter filter = new ThresholdFilter();
      filter.setLevel( level.toString() );
      filter.setContext( loggerContext );
      filter.start();
      appender.addFilter( filter );

      final PatternLayoutEncoder layoutEncoder = new PatternLayoutEncoder();
      layoutEncoder.setContext( loggerContext );
      layoutEncoder.setPattern( "%date %level %logger: %msg%n" );
      layoutEncoder.start();
      appender.setEncoder( layoutEncoder );
      appender.start();

      root.addAppender( appender );
   }
}

