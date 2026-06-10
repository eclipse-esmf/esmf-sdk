/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.turtle.languageserver.diagnostic;

public class TurtleDocumentDiagnostic extends TurtleBaseDiagnostic {
   private final String sourceLocation;
   private final int fromLine;
   private final int fromColumn;
   private final int toLine;
   private final int toColumn;

   public TurtleDocumentDiagnostic( final String message, final Code code, final String sourceLocation, final int fromLine,
         final int fromColumn, final int toLine, final int toColumn ) {
      super( message, code );
      this.sourceLocation = sourceLocation;
      this.fromLine = fromLine;
      this.fromColumn = fromColumn;
      this.toLine = toLine;
      this.toColumn = toColumn;
   }

   public String sourceLocation() {
      return sourceLocation;
   }

   public int fromLine() {
      return fromLine;
   }

   public int fromColumn() {
      return fromColumn;
   }

   public int toLine() {
      return toLine;
   }

   public int toColumn() {
      return toColumn;
   }

   @Override
   public boolean hasLocation() {
      return true;
   }
}
