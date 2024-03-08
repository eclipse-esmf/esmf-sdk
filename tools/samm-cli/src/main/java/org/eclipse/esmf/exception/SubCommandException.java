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

package org.eclipse.esmf.exception;

public class SubCommandException extends RuntimeException {
   private final String subCommandName;

   public SubCommandException( final String subCommandName ) {
      this.subCommandName = subCommandName;
   }
   public SubCommandException( final String subCommandName , final Throwable cause ) {
      super( cause );
      this.subCommandName = subCommandName;
   }

   public String getSubCommandName() {
      return subCommandName;
   }
}