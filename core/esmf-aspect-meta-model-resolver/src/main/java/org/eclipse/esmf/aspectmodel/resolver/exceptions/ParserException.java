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

public class ParserException extends Exception {
   private final String sourceDocument;

   public ParserException( final Throwable cause, final String sourceDocument ) {
      super( cause.getMessage(), cause );
      this.sourceDocument = sourceDocument;
   }

   public String getSourceDocument() {
      return sourceDocument;
   }
}
