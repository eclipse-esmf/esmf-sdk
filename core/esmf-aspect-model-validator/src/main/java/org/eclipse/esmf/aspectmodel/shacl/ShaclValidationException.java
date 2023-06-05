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

package org.eclipse.esmf.aspectmodel.shacl;

import java.io.Serial;

/**
 * This exception is thrown when the validation process fails
 */
public class ShaclValidationException extends RuntimeException {
   @Serial
   private static final long serialVersionUID = 7771592676699681415L;

   public ShaclValidationException( final String message ) {
      super( message );
   }
}
