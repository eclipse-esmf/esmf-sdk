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

package org.eclipse.esmf.turtle.languageserver.lsp.text;

import org.eclipse.esmf.turtle.languageserver.aspect.service.AspectValidationCoordinator;
import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleBaseDiagnostic;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDiagnostic;

public class DocumentAspectValidationService {
   private final AspectValidationCoordinator aspectValidationCoordinator;

   public DocumentAspectValidationService( final AspectValidationCoordinator aspectValidationCoordinator ) {
      this.aspectValidationCoordinator = aspectValidationCoordinator;
   }

   public DiagnosticReport validateDocument( final String uri, final ParsedDocument document ) {
      if ( document == null ) {
         return new DiagnosticReport(
               new TurtleBaseDiagnostic( "Document is not available in memory: " + uri, TurtleDiagnostic.TurtleCode.E0001 ) );
      }
      return aspectValidationCoordinator.validateSync( document );
   }
}
