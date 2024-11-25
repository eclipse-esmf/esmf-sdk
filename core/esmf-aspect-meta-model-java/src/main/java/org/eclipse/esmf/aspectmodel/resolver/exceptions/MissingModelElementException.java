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
public class MissingModelElementException extends RuntimeException {
   private static final long serialVersionUID = 3564348052165487489L;

   public MissingModelElementException( final String message ) {
      super( message );
   }
}
