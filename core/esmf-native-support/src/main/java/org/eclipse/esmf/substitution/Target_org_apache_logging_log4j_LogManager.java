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

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.graalvm.nativeimage.hosted.FieldValueTransformer;

/**
 * This is a <a href="https://build-native-java-apps.cc/developer-guide/substitution/">GraalVM substitution class</a>
 * for {@link LogManager}.
 * Reason: The original implementation uses reflection and services to find implementations, which does not work in the
 * GraalVM setup. The substitution class provides a custom implementation that always returns a dummy LoggerContextFactory.
 */
@TargetClass( LogManager.class )
@SuppressWarnings( {
      "unused",
      "squid:S00101", // Class name uses GraalVM substitution class naming schema, see
      // https://github.com/oracle/graal/tree/master/substratevm/src/com.oracle.svm.core/src/com/oracle/svm/core/jdk
      "checkstyle:TypeName",
      "NewClassNamingConvention" } )
public final class Target_org_apache_logging_log4j_LogManager {
   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.FromAlias )
   private static LoggerContextFactory factory = new DummyLoggerContextFactory();

   @Alias
   @RecomputeFieldValue( kind = RecomputeFieldValue.Kind.Custom, declClass = StatusLoggerInjector.class )
   private static Logger LOGGER;

   public static final class StatusLoggerInjector implements FieldValueTransformer {
      @Override
      public Object transform( final Object receiver, final Object originalValue ) {
         return new DummyLogger();
      }
   }
}
