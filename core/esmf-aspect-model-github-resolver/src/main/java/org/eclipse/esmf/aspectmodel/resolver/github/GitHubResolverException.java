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

package org.eclipse.esmf.aspectmodel.resolver.github;

import java.io.Serial;

public class GitHubResolverException extends RuntimeException {
   @Serial
   private static final long serialVersionUID = 5167021398157587678L;

   public GitHubResolverException( final String message ) {
      super( message );
   }

   public GitHubResolverException( final Throwable cause ) {
      super( cause );
   }

   public GitHubResolverException( final String message, final Throwable cause ) {
      super( message, cause );
   }
}
