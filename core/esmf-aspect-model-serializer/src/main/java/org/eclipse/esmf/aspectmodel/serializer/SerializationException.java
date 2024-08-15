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

package org.eclipse.esmf.aspectmodel.serializer;

import java.io.Serial;

public class SerializationException extends RuntimeException {
   @Serial
   private static final long serialVersionUID = 8685799345891779111L;

   public SerializationException( final String message ) {
      super( message );
   }

   public SerializationException( final Throwable cause ) {
      super( cause );
   }
}
