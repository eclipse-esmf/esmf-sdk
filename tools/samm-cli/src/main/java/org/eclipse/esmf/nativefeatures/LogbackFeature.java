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

import static org.graalvm.nativeimage.hosted.RuntimeClassInitialization.initializeAtBuildTime;

/**
 * Configuration of eagerly initialized SLF4J and Logback classes for native image builds
 */
public class LogbackFeature extends AbstractSammCliFeature {
   @Override
   public void beforeAnalysis( final BeforeAnalysisAccess access ) {
      initializeAtBuildTime( ch.qos.logback.classic.Level.class );
      initializeAtBuildTime( ch.qos.logback.classic.Logger.class );
      initializeAtBuildTime( ch.qos.logback.classic.PatternLayout.class );
      initializeAtBuildTime( ch.qos.logback.core.CoreConstants.class );
      initializeAtBuildTime( ch.qos.logback.core.pattern.parser.Parser.class );
      initializeAtBuildTime( ch.qos.logback.core.spi.AppenderAttachableImpl.class );
      initializeAtBuildTime( ch.qos.logback.core.status.InfoStatus.class );
      initializeAtBuildTime( ch.qos.logback.core.status.StatusBase.class );
      initializeAtBuildTime( ch.qos.logback.core.util.Loader.class );
      initializeAtBuildTime( ch.qos.logback.core.util.StatusPrinter.class );
      initializeAtBuildTime( ch.qos.logback.core.util.StatusPrinter2.class );

      initializeAtBuildTime( org.slf4j.LoggerFactory.class );
      initializeAtBuildTime( org.slf4j.helpers.Reporter.class );
   }
}
