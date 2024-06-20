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

package org.eclipse.esmf.aspectmodel.loader;

public class AspectLoadingException extends RuntimeException {
   private static final long serialVersionUID = 7687644022103150329L;

   public AspectLoadingException( final Throwable cause ) {
      super( cause );
   }

   public AspectLoadingException( final String message ) {
      super( message );
   }

   public AspectLoadingException( final String message, final Throwable cause ) {
      super( message, cause );
   }
}
