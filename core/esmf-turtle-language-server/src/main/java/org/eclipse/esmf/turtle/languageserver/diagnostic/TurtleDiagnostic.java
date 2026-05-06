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

public interface TurtleDiagnostic {
   Code code();

   String message();

   Severity severity();

   default boolean hasLocation() {
      return false;
   }

   interface Code {
      String code();

      String description();
   }

   enum TurtleCode implements Code {
      E0000( "No more info available" ),
      E0001( "Could not load document" ),
      E0002( "Document validation failed" ),
      E0003( "Syntax error" );

      private final String description;

      TurtleCode( final String description ) {
         this.description = description;
      }

      @Override
      public String code() {
         return name();
      }

      @Override
      public String description() {
         return description;
      }
   }

   enum Severity {
      ERROR,
      WARNING,
      INFO,
      HINT
   }
}
