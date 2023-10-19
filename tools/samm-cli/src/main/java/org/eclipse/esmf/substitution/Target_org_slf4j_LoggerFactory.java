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

package org.eclipse.esmf.substitution;

import java.util.List;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.SLF4JServiceProvider;

@TargetClass( LoggerFactory.class )
public final class Target_org_slf4j_LoggerFactory {
   @Substitute
   private static void reportMultipleBindingAmbiguity( final List<SLF4JServiceProvider> providerList ) {
      // Do nothing
   }

   @Substitute
   private static void reportActualBinding( final List<SLF4JServiceProvider> providerList ) {
      // Do nothing
   }
}
