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

package org.eclipse.esmf.aspectmodel.generator;

public class DocumentGenerationException extends GenerationException {
   private static final long serialVersionUID = -3592685602580476530L;

   public DocumentGenerationException( final String message ) {
      super( message );
   }

   public DocumentGenerationException( final Throwable cause ) {
      super( cause );
   }
}
