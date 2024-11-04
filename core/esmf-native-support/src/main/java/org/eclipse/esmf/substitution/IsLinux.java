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

import java.util.function.BooleanSupplier;

/**
 * Conditional to execute substitution only on Linux. Use with com.oracle.svm.core.annotate.Substitute's onlyWith attribute.
 */
public class IsLinux implements BooleanSupplier {
   @Override
   public boolean getAsBoolean() {
      return "Linux".equals( System.getProperty( "os.name" ) );
   }
}
