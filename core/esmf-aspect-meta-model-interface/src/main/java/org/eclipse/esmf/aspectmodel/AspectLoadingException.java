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

package org.eclipse.esmf.aspectmodel;

import org.apache.jena.rdf.model.RDFNode;
import org.jspecify.annotations.Nullable;

public class AspectLoadingException extends RuntimeException {
   private static final long serialVersionUID = 7687644022103150329L;

   private final @Nullable RDFNode highlightElement;

   public AspectLoadingException( final Throwable cause ) {
      super( cause );
      highlightElement = null;
   }

   public AspectLoadingException( final String message ) {
      super( message );
      highlightElement = null;
   }

   public AspectLoadingException( final String message, final Throwable cause ) {
      super( message, cause );
      highlightElement = null;
   }

   public AspectLoadingException( final String message, final RDFNode highlightElement ) {
      super( message );
      this.highlightElement = highlightElement;
   }

   public @Nullable RDFNode highlightElement() {
      return highlightElement;
   }
}
