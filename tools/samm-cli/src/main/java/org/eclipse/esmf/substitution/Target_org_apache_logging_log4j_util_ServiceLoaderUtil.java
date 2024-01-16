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

import java.lang.invoke.MethodHandles;
import java.util.List;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.apache.logging.log4j.util.ServiceLoaderUtil;

/**
 * This is a <a href="https://build-native-java-apps.cc/developer-guide/substitution/">GraalVM substitution class</a>
 * for {@link ServiceLoaderUtil}.
 * Reason: The original implementation uses reflection to instantiate classes, which is unsupported in GraalVM.
 */
@TargetClass( ServiceLoaderUtil.class )
@SuppressWarnings( {
      "unused",
      "squid:S00101" // Class name uses GraalVM substitution class naming schema, see
      // https://github.com/oracle/graal/tree/master/substratevm/src/com.oracle.svm.core/src/com/oracle/svm/core/jdk
} )
public final class Target_org_apache_logging_log4j_util_ServiceLoaderUtil {
   @Substitute
   static <T> Iterable<T> callServiceLoader( final MethodHandles.Lookup lookup, final Class<T> serviceType, final ClassLoader classLoader,
         final boolean verbose ) {
      return List.of();
   }
}
