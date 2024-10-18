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

package org.eclipse.esmf.nativefeatures;

import org.graalvm.nativeimage.hosted.Feature;

/**
 * Configuration of eagerly initialized SLF4J and Logback classes for native image builds
 */
public class LogbackFeature implements Feature {
   @Override
   public void beforeAnalysis( final Feature.BeforeAnalysisAccess access ) {
      Native.forClass( ch.qos.logback.classic.Level.class )
            .initializeAtBuildTime()
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.Logger.class )
            .initializeAtBuildTime()
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.PatternLayout.class )
            .initializeAtBuildTime()
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.CoreConstants.class )
            .initializeAtBuildTime()
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.parser.Parser.class )
            .initializeAtBuildTime()
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.spi.AppenderAttachableImpl.class )
            .initializeAtBuildTime()
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.status.InfoStatus.class )
            .initializeAtBuildTime()
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.status.StatusBase.class )
            .initializeAtBuildTime()
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.util.Loader.class )
            .initializeAtBuildTime()
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.util.StatusPrinter.class )
            .initializeAtBuildTime()
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.util.StatusPrinter2.class )
            .initializeAtBuildTime()
            .registerEverythingForReflection();

      Native.forClass( org.slf4j.LoggerFactory.class )
            .initializeAtBuildTime()
            .registerEverythingForReflection();
      Native.forClass( org.slf4j.helpers.Reporter.class )
            .initializeAtBuildTime()
            .registerEverythingForReflection();

      Native.forClass( ch.qos.logback.classic.pattern.ClassOfCallerConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.ClassOfCallerConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.ContextNameConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.DateConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.ExtendedThrowableProxyConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.FileOfCallerConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.LevelConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.LineOfCallerConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.LineSeparatorConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.LocalSequenceNumberConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.LoggerConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.MDCConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.MarkerConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.MessageConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.MethodOfCallerConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.NopThrowableInformationConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.PropertyConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.RelativeTimeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.RootCauseFirstThrowableProxyConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.ThreadConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.ThrowableProxyConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.classic.pattern.color.HighlightingCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.IdentityCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.ReplacingCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.color.BlackCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.color.BoldBlueCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.color.BoldCyanCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.color.BoldGreenCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.color.BoldMagentaCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.color.BoldRedCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.color.BoldWhiteCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.color.BoldYellowCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.color.CyanCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.color.GrayCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.color.GreenCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.color.MagentaCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.color.RedCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.color.WhiteCompositeConverter.class )
            .registerEverythingForReflection();
      Native.forClass( ch.qos.logback.core.pattern.color.YellowCompositeConverter.class )
            .registerEverythingForReflection();

      Native.addResource( "org/slf4j/impl/StaticLoggerBinder.class" );
   }
}
