/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.java.exception;

public class EnumAttributeNotFoundException extends RuntimeException {
   private static final long serialVersionUID = 2785228190323275948L;

   public EnumAttributeNotFoundException( final String message ) {
      super( message );
   }

   public EnumAttributeNotFoundException( final Throwable cause ) {
      super( cause );
   }
}
