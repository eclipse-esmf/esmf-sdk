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

package org.eclipse.esmf.aspectmodel.resolver.exceptions;

public class ModelResolutionException extends RuntimeException {
   private static final long serialVersionUID = 1719805029020063645L;

   public ModelResolutionException( final String message ) {
      super( message );
   }

   public ModelResolutionException( final String message, final Throwable cause ) {
      super( message, cause );
   }
}
