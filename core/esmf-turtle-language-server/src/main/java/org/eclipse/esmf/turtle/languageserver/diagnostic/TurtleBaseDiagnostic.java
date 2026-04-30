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

package org.eclipse.esmf.turtle.languageserver.diagnostic;

public class TurtleBaseDiagnostic implements TurtleDiagnostic {
   private final String message;
   private final Code code;

   public TurtleBaseDiagnostic( final String message, final Code code ) {
      this.message = message;
      this.code = code;
   }

   @Override
   public String message() {
      return message;
   }

   @Override
   public Code code() {
      return code;
   }
}
