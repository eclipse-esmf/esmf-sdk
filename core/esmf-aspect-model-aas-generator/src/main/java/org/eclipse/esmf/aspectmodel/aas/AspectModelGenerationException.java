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

package org.eclipse.esmf.aspectmodel.aas;

import java.io.Serial;

public class AspectModelGenerationException extends RuntimeException {
   @Serial
   private static final long serialVersionUID = 4617898870718066956L;

   public AspectModelGenerationException( final String message, final Throwable cause ) {
      super( message, cause );
   }

   public AspectModelGenerationException( final Throwable cause ) {
      super( cause );
   }

   public AspectModelGenerationException( final String message ) {
      super( message );
   }
}
