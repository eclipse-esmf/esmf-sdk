/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.TargetClass;

/**
 * This is a <a href="https://build-native-java-apps.cc/developer-guide/substitution/">GraalVM substitution class</a>
 * for {@link Thread}.
 * Reason: Backport of
 * <a
 * href="https://github.com/oracle/graal/commit/369f0ff8b0a1c2b1051065b8c5d96937d058c922#diff
 * -0aac6c381ea903b15210c9b8e29f378ef804565a21270977b0dd5bdf9e826982">a
 * fix</a>
 * which leads to issue <a href="https://github.com/oracle/graal/issues/9672">9672</a> but which is not released as of 21.0.6-graal
 */
@TargetClass( Thread.class )
@SuppressWarnings( {
      "unused",
      "squid:S00101", // Class name uses GraalVM substitution class naming schema, see
      // https://github.com/oracle/graal/tree/master/substratevm/src/com.oracle.svm.core/src/com/oracle/svm/core/jdk
      "checkstyle:TypeName"
      , "NewClassNamingConvention" } )
public final class Target_java_lang_Thread {
   @Delete
   @SuppressWarnings( { "static-method" } )
   private native Object getStackTrace0();

   @Delete
   static native long getNextThreadIdOffset();
}
