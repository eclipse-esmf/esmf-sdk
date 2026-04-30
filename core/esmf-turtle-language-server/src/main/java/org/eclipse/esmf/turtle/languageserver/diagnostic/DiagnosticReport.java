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

import java.util.List;

public record DiagnosticReport(
      List<TurtleDiagnostic> diagnostics
) {
   public DiagnosticReport( final TurtleDiagnostic diagnostic ) {
      this( List.of( diagnostic ) );
   }

   /**
    * Convenience constructor to create are report for one {@link TurtleBaseDiagnostic}
    * 
    * @param message the message
    * @param code the code
    */
   public DiagnosticReport( final String message, final TurtleDiagnostic.TurtleCode code ) {
      this( new TurtleBaseDiagnostic( message, code ) );
   }
}
