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

package org.eclipse.esmf.turtle.languageserver.aspect.diagnostic;

import org.eclipse.esmf.Diagnostic;
import org.eclipse.esmf.DocumentDiagnostic;
import org.eclipse.esmf.Location;

public record AspectDocumentDiagnostic(
      String message,
      AspectDiagnosticCode code,
      String sourceLocation,
      Location location,
      Diagnostic.Severity severity
) implements DocumentDiagnostic<AspectDiagnosticCode> {
   public AspectDocumentDiagnostic( final String message, final AspectDiagnosticCode code,
         final String sourceLocation, final Location location ) {
      this( message, code, sourceLocation, location, Diagnostic.Severity.ERROR );
   }
}
