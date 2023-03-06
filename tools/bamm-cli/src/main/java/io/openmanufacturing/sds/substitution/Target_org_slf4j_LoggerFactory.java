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

package io.openmanufacturing.sds.substitution;

import java.net.URL;
import java.util.Set;

import org.slf4j.LoggerFactory;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass( LoggerFactory.class )
public final class Target_org_slf4j_LoggerFactory {
   @Substitute
   private static void reportMultipleBindingAmbiguity( final Set<URL> binderPathSet ) {
      // Do nothing
   }

   @Substitute
   private static void reportActualBinding( final Set<URL> binderPathSet ) {
      // Do nothing
   }
}
