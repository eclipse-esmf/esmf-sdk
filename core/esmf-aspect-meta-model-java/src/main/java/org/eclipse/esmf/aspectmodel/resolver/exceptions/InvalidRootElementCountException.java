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

package org.eclipse.esmf.aspectmodel.resolver.exceptions;

@Deprecated( forRemoval = true )
public class InvalidRootElementCountException extends RuntimeException {
   private static final long serialVersionUID = 5199212935274840329L;

   public InvalidRootElementCountException( final String message ) {
      super( message );
   }
}
