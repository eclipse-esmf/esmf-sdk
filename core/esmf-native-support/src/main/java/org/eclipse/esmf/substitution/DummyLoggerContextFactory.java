/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.substitution;

import java.net.URI;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.EntryMessage;
import org.apache.logging.log4j.message.FlowMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.util.MessageSupplier;
import org.apache.logging.log4j.util.Supplier;

/**
 * This is an implementation of {@link LoggerContextFactory} that returns a logger context which will always provide a
 * null-operation logger.
 */
public class DummyLoggerContextFactory implements LoggerContextFactory {
   private final LoggerContext loggerContext = new DummyLoggerContext();

   @Override
   public LoggerContext getContext( final String fqcn, final ClassLoader loader, final Object externalContext,
         final boolean currentContext ) {
      return loggerContext;
   }

   @Override
   public LoggerContext getContext( final String fqcn, final ClassLoader loader, final Object externalContext, final boolean currentContext,
         final URI configLocation, final String name ) {
      return loggerContext;
   }

   @Override
   public void removeContext( final LoggerContext context ) {
      // do nothing
   }

   public static class DummyLoggerContext implements LoggerContext {
      private final ExtendedLogger logger = new DummyExtendedLogger();

      @Override
      public Object getExternalContext() {
         return null;
      }

      @Override
      public ExtendedLogger getLogger( final String name ) {
         return logger;
      }

      @Override
      public ExtendedLogger getLogger( final String name, final MessageFactory messageFactory ) {
         return logger;
      }

      @Override
      public boolean hasLogger( final String name ) {
         return false;
      }

      @Override
      public boolean hasLogger( final String name, final Class<? extends MessageFactory> messageFactoryClass ) {
         return false;
      }

      @Override
      public boolean hasLogger( final String name, final MessageFactory messageFactory ) {
         return false;
      }
   }

   @SuppressWarnings( "deprecation" )
   public static class DummyExtendedLogger implements ExtendedLogger {
      @Override
      public boolean isEnabled( final Level level, final Marker marker, final Message message, final Throwable t ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker, final CharSequence message, final Throwable t ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker, final Object message, final Throwable t ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker, final String message, final Throwable t ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker, final String message ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker, final String message, final Object... params ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker, final String message, final Object p0 ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker, final String message, final Object p0, final Object p1 ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2 ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3 ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4 ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4,
            final Object p5 ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6 ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7 ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8 ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker, final String message, final Object p0, final Object p1,
            final Object p2, final Object p3, final Object p4, final Object p5,
            final Object p6, final Object p7, final Object p8, final Object p9 ) {
         return false;
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final Message message, final Throwable t ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final CharSequence message, final Throwable t ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final Object message, final Throwable t ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final String message, final Throwable t ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final String message ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final String message, final Object... params ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final String message, final Object p0 ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final String message, final Object p0,
            final Object p1 ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2 ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3 ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3, final Object p4 ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3, final Object p4, final Object p5 ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6 ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7 ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8 ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final String message, final Object p0,
            final Object p1, final Object p2, final Object p3, final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8, final Object p9 ) {
         // do nothing
      }

      @Override
      public void logMessage( final String fqcn, final Level level, final Marker marker, final Message message, final Throwable t ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final MessageSupplier msgSupplier,
            final Throwable t ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final String message,
            final Supplier<?>... paramSuppliers ) {
         // do nothing
      }

      @Override
      public void logIfEnabled( final String fqcn, final Level level, final Marker marker, final Supplier<?> msgSupplier,
            final Throwable t ) {
         // do nothing
      }

      @Override
      public void catching( final Level level, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void catching( final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final Message message ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final Message message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final MessageSupplier messageSupplier ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final CharSequence message ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final CharSequence message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final Object message ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final Object message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final String message ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final String message, final Object... params ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final String message, final Supplier<?>... paramSuppliers ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final String message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final Supplier<?> messageSupplier ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void debug( final Message message ) {
         // do nothing
      }

      @Override
      public void debug( final Message message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void debug( final MessageSupplier messageSupplier ) {
         // do nothing
      }

      @Override
      public void debug( final MessageSupplier messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void debug( final CharSequence message ) {
         // do nothing
      }

      @Override
      public void debug( final CharSequence message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void debug( final Object message ) {
         // do nothing
      }

      @Override
      public void debug( final Object message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void debug( final String message ) {
         // do nothing
      }

      @Override
      public void debug( final String message, final Object... params ) {
         // do nothing
      }

      @Override
      public void debug( final String message, final Supplier<?>... paramSuppliers ) {
         // do nothing
      }

      @Override
      public void debug( final String message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void debug( final Supplier<?> messageSupplier ) {
         // do nothing
      }

      @Override
      public void debug( final Supplier<?> messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final String message, final Object p0 ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final String message, final Object p0, final Object p1 ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final String message, final Object p0, final Object p1, final Object p2 ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3 ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4 ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5 ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6 ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7 ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8 ) {
         // do nothing
      }

      @Override
      public void debug( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7, final Object p8, final Object p9 ) {
         // do nothing
      }

      @Override
      public void debug( final String message, final Object p0 ) {
         // do nothing
      }

      @Override
      public void debug( final String message, final Object p0, final Object p1 ) {
         // do nothing
      }

      @Override
      public void debug( final String message, final Object p0, final Object p1, final Object p2 ) {
         // do nothing
      }

      @Override
      public void debug( final String message, final Object p0, final Object p1, final Object p2, final Object p3 ) {
         // do nothing
      }

      @Override
      public void debug( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4 ) {
         // do nothing
      }

      @Override
      public void debug( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5 ) {
         // do nothing
      }

      @Override
      public void debug( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6 ) {
         // do nothing
      }

      @Override
      public void debug( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7 ) {
         // do nothing
      }

      @Override
      public void debug( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7, final Object p8 ) {
         // do nothing
      }

      @Override
      public void debug( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7, final Object p8, final Object p9 ) {
         // do nothing
      }

      @Override
      public void entry() {
         // do nothing
      }

      @Override
      public void entry( final Object... params ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final Message message ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final Message message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final MessageSupplier messageSupplier ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final CharSequence message ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final CharSequence message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final Object message ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final Object message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      // do nothing
      public void error( final Marker marker, final String message ) {
      }

      @Override
      public void error( final Marker marker, final String message, final Object... params ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final String message, final Supplier<?>... paramSuppliers ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final String message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final Supplier<?> messageSupplier ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void error( final Message message ) {
         // do nothing
      }

      @Override
      public void error( final Message message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void error( final MessageSupplier messageSupplier ) {
         // do nothing
      }

      @Override
      public void error( final MessageSupplier messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void error( final CharSequence message ) {
         // do nothing
      }

      @Override
      public void error( final CharSequence message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void error( final Object message ) {
         // do nothing
      }

      @Override
      public void error( final Object message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void error( final String message ) {
         // do nothing
      }

      @Override
      public void error( final String message, final Object... params ) {
         // do nothing
      }

      @Override
      public void error( final String message, final Supplier<?>... paramSuppliers ) {
         // do nothing
      }

      @Override
      public void error( final String message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void error( final Supplier<?> messageSupplier ) {
         // do nothing
      }

      @Override
      public void error( final Supplier<?> messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final String message, final Object p0 ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final String message, final Object p0, final Object p1 ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final String message, final Object p0, final Object p1, final Object p2 ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3 ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4 ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5 ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6 ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7 ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8 ) {
         // do nothing
      }

      @Override
      public void error( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8, final Object p9 ) {
         // do nothing
      }

      @Override
      public void error( final String message, final Object p0 ) {
         // do nothing
      }

      @Override
      public void error( final String message, final Object p0, final Object p1 ) {
         // do nothing
      }

      @Override
      public void error( final String message, final Object p0, final Object p1, final Object p2 ) {
         // do nothing
      }

      @Override
      public void error( final String message, final Object p0, final Object p1, final Object p2, final Object p3 ) {
         // do nothing
      }

      @Override
      public void error( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4 ) {
         // do nothing
      }

      @Override
      public void error( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5 ) {
         // do nothing
      }

      @Override
      public void error( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6 ) {
         // do nothing
      }

      @Override
      public void error( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7 ) {
         // do nothing
      }

      @Override
      public void error( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7, final Object p8 ) {
         // do nothing
      }

      @Override
      public void error( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9 ) {
         // do nothing
      }

      @Override
      public void exit() {
         // do nothing
      }

      @Override
      public <R> R exit( final R result ) {
         return null;
      }

      @Override
      public void fatal( final Marker marker, final Message message ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final Message message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final MessageSupplier messageSupplier ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final CharSequence message ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final CharSequence message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final Object message ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final Object message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final String message ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final String message, final Object... params ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final String message, final Supplier<?>... paramSuppliers ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final String message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final Supplier<?> messageSupplier ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void fatal( final Message message ) {
         // do nothing
      }

      @Override
      public void fatal( final Message message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void fatal( final MessageSupplier messageSupplier ) {
         // do nothing
      }

      @Override
      public void fatal( final MessageSupplier messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void fatal( final CharSequence message ) {
         // do nothing
      }

      @Override
      public void fatal( final CharSequence message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void fatal( final Object message ) {
         // do nothing
      }

      @Override
      public void fatal( final Object message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void fatal( final String message ) {
         // do nothing
      }

      @Override
      public void fatal( final String message, final Object... params ) {
         // do nothing
      }

      @Override
      public void fatal( final String message, final Supplier<?>... paramSuppliers ) {
         // do nothing
      }

      @Override
      public void fatal( final String message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void fatal( final Supplier<?> messageSupplier ) {
         // do nothing
      }

      @Override
      public void fatal( final Supplier<?> messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final String message, final Object p0 ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final String message, final Object p0, final Object p1 ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final String message, final Object p0, final Object p1, final Object p2 ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3 ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4 ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5 ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6 ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7 ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8 ) {
         // do nothing
      }

      @Override
      public void fatal( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8, final Object p9 ) {
         // do nothing
      }

      @Override
      public void fatal( final String message, final Object p0 ) {
         // do nothing
      }

      @Override
      public void fatal( final String message, final Object p0, final Object p1 ) {
         // do nothing
      }

      @Override
      public void fatal( final String message, final Object p0, final Object p1, final Object p2 ) {
         // do nothing
      }

      @Override
      public void fatal( final String message, final Object p0, final Object p1, final Object p2, final Object p3 ) {
         // do nothing
      }

      @Override
      public void fatal( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4 ) {
         // do nothing
      }

      @Override
      public void fatal( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5 ) {
         // do nothing
      }

      @Override
      public void fatal( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6 ) {
         // do nothing
      }

      @Override
      public void fatal( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7 ) {
         // do nothing
      }

      @Override
      public void fatal( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7, final Object p8 ) {
         // do nothing
      }

      @Override
      public void fatal( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9 ) {
         // do nothing
      }

      @Override
      public Level getLevel() {
         return null;
      }

      @SuppressWarnings( "NewClassNamingConvention" )
      @Override
      public <M extends MessageFactory> M getMessageFactory() {
         return null;
      }

      @Override
      public FlowMessageFactory getFlowMessageFactory() {
         return null;
      }

      @Override
      public String getName() {
         return null;
      }

      @Override
      public void info( final Marker marker, final Message message ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final Message message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final MessageSupplier messageSupplier ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final CharSequence message ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final CharSequence message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final Object message ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final Object message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final String message ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final String message, final Object... params ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final String message, final Supplier<?>... paramSuppliers ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final String message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final Supplier<?> messageSupplier ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void info( final Message message ) {
         // do nothing
      }

      @Override
      public void info( final Message message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void info( final MessageSupplier messageSupplier ) {
         // do nothing
      }

      @Override
      public void info( final MessageSupplier messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void info( final CharSequence message ) {
         // do nothing
      }

      @Override
      public void info( final CharSequence message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void info( final Object message ) {
         // do nothing
      }

      @Override
      public void info( final Object message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void info( final String message ) {
         // do nothing
      }

      @Override
      public void info( final String message, final Object... params ) {
         // do nothing
      }

      @Override
      public void info( final String message, final Supplier<?>... paramSuppliers ) {
         // do nothing
      }

      @Override
      public void info( final String message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void info( final Supplier<?> messageSupplier ) {
         // do nothing
      }

      @Override
      public void info( final Supplier<?> messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final String message, final Object p0 ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final String message, final Object p0, final Object p1 ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final String message, final Object p0, final Object p1, final Object p2 ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3 ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4 ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5 ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6 ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7 ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8 ) {
         // do nothing
      }

      @Override
      public void info( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8, final Object p9 ) {
         // do nothing
      }

      @Override
      public void info( final String message, final Object p0 ) {
         // do nothing
      }

      @Override
      public void info( final String message, final Object p0, final Object p1 ) {
         // do nothing
      }

      @Override
      public void info( final String message, final Object p0, final Object p1, final Object p2 ) {
         // do nothing
      }

      @Override
      public void info( final String message, final Object p0, final Object p1, final Object p2, final Object p3 ) {
         // do nothing
      }

      @Override
      public void info( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4 ) {
         // do nothing
      }

      @Override
      public void info( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5 ) {
         // do nothing
      }

      @Override
      public void info( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6 ) {
         // do nothing
      }

      @Override
      public void info( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7 ) {
         // do nothing
      }

      @Override
      public void info( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7, final Object p8 ) {
         // do nothing
      }

      @Override
      public void info( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9 ) {
         // do nothing
      }

      @Override
      public boolean isDebugEnabled() {
         return false;
      }

      @Override
      public boolean isDebugEnabled( final Marker marker ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level ) {
         return false;
      }

      @Override
      public boolean isEnabled( final Level level, final Marker marker ) {
         return false;
      }

      @Override
      public boolean isErrorEnabled() {
         return false;
      }

      @Override
      public boolean isErrorEnabled( final Marker marker ) {
         return false;
      }

      @Override
      public boolean isFatalEnabled() {
         return false;
      }

      @Override
      public boolean isFatalEnabled( final Marker marker ) {
         return false;
      }

      @Override
      public boolean isInfoEnabled() {
         return false;
      }

      @Override
      public boolean isInfoEnabled( final Marker marker ) {
         return false;
      }

      @Override
      public boolean isTraceEnabled() {
         return false;
      }

      @Override
      public boolean isTraceEnabled( final Marker marker ) {
         return false;
      }

      @Override
      public boolean isWarnEnabled() {
         return false;
      }

      @Override
      public boolean isWarnEnabled( final Marker marker ) {
         return false;
      }

      @Override
      public void log( final Level level, final Marker marker, final Message message ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final Message message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final MessageSupplier messageSupplier ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final CharSequence message ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final CharSequence message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final Object message ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final Object message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final String message ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final String message, final Object... params ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final String message, final Supplier<?>... paramSuppliers ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final String message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final Supplier<?> messageSupplier ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Message message ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Message message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final MessageSupplier messageSupplier ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final MessageSupplier messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final CharSequence message ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final CharSequence message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Object message ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Object message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final String message ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final String message, final Object... params ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final String message, final Supplier<?>... paramSuppliers ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final String message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Supplier<?> messageSupplier ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Supplier<?> messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final String message, final Object p0 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final String message, final Object p0, final Object p1 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5,
            final Object p6 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final Marker marker, final String message, final Object p0, final Object p1, final Object p2,
            final Object p3, final Object p4, final Object p5, final Object p6,
            final Object p7, final Object p8, final Object p9 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final String message, final Object p0 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final String message, final Object p0, final Object p1 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final String message, final Object p0, final Object p1, final Object p2 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8 ) {
         // do nothing
      }

      @Override
      public void log( final Level level, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8, final Object p9 ) {
         // do nothing
      }

      @Override
      public void printf( final Level level, final Marker marker, final String format, final Object... params ) {
         // do nothing
      }

      @Override
      public void printf( final Level level, final String format, final Object... params ) {
         // do nothing
      }

      @Override
      public <T extends Throwable> T throwing( final Level level, final T throwable ) {
         return null;
      }

      @Override
      public <T extends Throwable> T throwing( final T throwable ) {
         return null;
      }

      @Override
      public void trace( final Marker marker, final Message message ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final Message message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final MessageSupplier messageSupplier ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final CharSequence message ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final CharSequence message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final Object message ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final Object message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final String message ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final String message, final Object... params ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final String message, final Supplier<?>... paramSuppliers ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final String message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final Supplier<?> messageSupplier ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void trace( final Message message ) {
         // do nothing
      }

      @Override
      public void trace( final Message message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void trace( final MessageSupplier messageSupplier ) {
         // do nothing
      }

      @Override
      public void trace( final MessageSupplier messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void trace( final CharSequence message ) {
         // do nothing
      }

      @Override
      public void trace( final CharSequence message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void trace( final Object message ) {
         // do nothing
      }

      @Override
      public void trace( final Object message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void trace( final String message ) {
         // do nothing
      }

      @Override
      public void trace( final String message, final Object... params ) {
         // do nothing
      }

      @Override
      public void trace( final String message, final Supplier<?>... paramSuppliers ) {
         // do nothing
      }

      @Override
      public void trace( final String message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void trace( final Supplier<?> messageSupplier ) {
         // do nothing
      }

      @Override
      public void trace( final Supplier<?> messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final String message, final Object p0 ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final String message, final Object p0, final Object p1 ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final String message, final Object p0, final Object p1, final Object p2 ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3 ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4 ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5 ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6 ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7 ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8 ) {
         // do nothing
      }

      @Override
      public void trace( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8, final Object p9 ) {
         // do nothing
      }

      @Override
      public void trace( final String message, final Object p0 ) {
         // do nothing
      }

      @Override
      public void trace( final String message, final Object p0, final Object p1 ) {
         // do nothing
      }

      @Override
      public void trace( final String message, final Object p0, final Object p1, final Object p2 ) {
         // do nothing
      }

      @Override
      public void trace( final String message, final Object p0, final Object p1, final Object p2, final Object p3 ) {
         // do nothing
      }

      @Override
      public void trace( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4 ) {
         // do nothing
      }

      @Override
      public void trace( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5 ) {
         // do nothing
      }

      @Override
      public void trace( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6 ) {
         // do nothing
      }

      @Override
      public void trace( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7 ) {
         // do nothing
      }

      @Override
      public void trace( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7, final Object p8 ) {
         // do nothing
      }

      @Override
      public void trace( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9 ) {
         // do nothing
      }

      @Override
      public EntryMessage traceEntry() {
         return null;
      }

      @Override
      public EntryMessage traceEntry( final String format, final Object... params ) {
         return null;
      }

      @Override
      public EntryMessage traceEntry( final Supplier<?>... paramSuppliers ) {
         return null;
      }

      @Override
      public EntryMessage traceEntry( final String format, final Supplier<?>... paramSuppliers ) {
         return null;
      }

      @Override
      public EntryMessage traceEntry( final Message message ) {
         return null;
      }

      @Override
      public void traceExit() {
         // do nothing
      }

      @Override
      public <R> R traceExit( final R result ) {
         return null;
      }

      @Override
      public <R> R traceExit( final String format, final R result ) {
         return null;
      }

      @Override
      public void traceExit( final EntryMessage message ) {
         // do nothing
      }

      @Override
      public <R> R traceExit( final EntryMessage message, final R result ) {
         return null;
      }

      @Override
      public <R> R traceExit( final Message message, final R result ) {
         return null;
      }

      @Override
      public void warn( final Marker marker, final Message message ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final Message message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final MessageSupplier messageSupplier ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final MessageSupplier messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final CharSequence message ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final CharSequence message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final Object message ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final Object message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final String message ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final String message, final Object... params ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final String message, final Supplier<?>... paramSuppliers ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final String message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final Supplier<?> messageSupplier ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final Supplier<?> messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void warn( final Message message ) {
         // do nothing
      }

      @Override
      public void warn( final Message message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void warn( final MessageSupplier messageSupplier ) {
         // do nothing
      }

      @Override
      public void warn( final MessageSupplier messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void warn( final CharSequence message ) {
         // do nothing
      }

      @Override
      public void warn( final CharSequence message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void warn( final Object message ) {
         // do nothing
      }

      @Override
      public void warn( final Object message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void warn( final String message ) {
         // do nothing
      }

      @Override
      public void warn( final String message, final Object... params ) {
         // do nothing
      }

      @Override
      public void warn( final String message, final Supplier<?>... paramSuppliers ) {
         // do nothing
      }

      @Override
      public void warn( final String message, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void warn( final Supplier<?> messageSupplier ) {
         // do nothing
      }

      @Override
      public void warn( final Supplier<?> messageSupplier, final Throwable throwable ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final String message, final Object p0 ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final String message, final Object p0, final Object p1 ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final String message, final Object p0, final Object p1, final Object p2 ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3 ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4 ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5 ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6 ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6,
            final Object p7 ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8 ) {
         // do nothing
      }

      @Override
      public void warn( final Marker marker, final String message, final Object p0, final Object p1, final Object p2, final Object p3,
            final Object p4, final Object p5, final Object p6, final Object p7,
            final Object p8, final Object p9 ) {
         // do nothing
      }

      @Override
      public void warn( final String message, final Object p0 ) {
         // do nothing
      }

      @Override
      public void warn( final String message, final Object p0, final Object p1 ) {
         // do nothing
      }

      @Override
      public void warn( final String message, final Object p0, final Object p1, final Object p2 ) {
         // do nothing
      }

      @Override
      public void warn( final String message, final Object p0, final Object p1, final Object p2, final Object p3 ) {
         // do nothing
      }

      @Override
      public void warn( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4 ) {
         // do nothing
      }

      @Override
      public void warn( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5 ) {
         // do nothing
      }

      @Override
      public void warn( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6 ) {
         // do nothing
      }

      @Override
      public void warn( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7 ) {
         // do nothing
      }

      @Override
      public void warn( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7, final Object p8 ) {
         // do nothing
      }

      @Override
      public void warn( final String message, final Object p0, final Object p1, final Object p2, final Object p3, final Object p4,
            final Object p5, final Object p6, final Object p7, final Object p8,
            final Object p9 ) {
         // do nothing
      }
   }
}
