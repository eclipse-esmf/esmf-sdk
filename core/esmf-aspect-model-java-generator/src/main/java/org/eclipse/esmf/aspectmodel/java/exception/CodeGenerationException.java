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

package org.eclipse.esmf.aspectmodel.java.exception;

public class CodeGenerationException extends RuntimeException {
   private static final long serialVersionUID = 4796494092053137301L;

   public CodeGenerationException( final Throwable cause ) {
      super( cause );
   }

   public CodeGenerationException( final String message ) {
      super( message );
   }

   public CodeGenerationException( final String message, final Throwable cause ) {
      super( message, cause );
   }
}
