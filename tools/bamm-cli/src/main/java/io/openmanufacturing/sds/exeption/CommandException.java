/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.exeption;

public class CommandException extends RuntimeException {

   public CommandException( final String reason ) {
      super( reason );
   }

   public CommandException( final Throwable throwable ) {
      super( throwable );
   }

   public CommandException( final String message, final Throwable cause ) {
      super( message, cause );
   }
}
