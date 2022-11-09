/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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
package io.openmanufacturing.sds.aspectmodel.resolver.exceptions;

import java.io.Serial;

/**
 * An exception indicating that usage of the given namespace is invalid.
 */
public final class InvalidNamespaceException extends RuntimeException {

   @Serial
   private static final long serialVersionUID = -1075433954587137319L;

   /**
    * Creates an instance of the exception.
    * @param message The detailed message of the problem.
    * @param cause The cause of the problem.
    */
   public InvalidNamespaceException( final String message, final Throwable cause ) {
      super( message, cause );
   }

   /**
    * Creates an instance of the exception.
    * @param message The detailed message of the problem.
    */
   public InvalidNamespaceException( final String message ) {
      super( message );
   }

}
