/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.turtle.languageserver.aspect.diagnostic;

import org.eclipse.esmf.Diagnostic;

final class AspectViolationCode implements Diagnostic.Code {
   private final String code;

   AspectViolationCode( final String code ) {
      this.code = code;
   }

   @Override
   public String code() {
      return code;
   }

   @Override
   public String description() {
      return code;
   }
}
