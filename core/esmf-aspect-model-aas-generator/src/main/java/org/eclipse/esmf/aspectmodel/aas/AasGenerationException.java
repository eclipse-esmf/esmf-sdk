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

import org.eclipse.esmf.aspectmodel.generator.GenerationException;

public class AasGenerationException extends GenerationException {
   @Serial
   private static final long serialVersionUID = 6578531471799897531L;

   public AasGenerationException( final Throwable cause ) {
      super( cause );
   }
}
