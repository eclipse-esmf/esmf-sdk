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

import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

public interface TurtleDiagnosticsService {
   default DiagnosticReport defaultValidate( final ParsedDocument document ) {
      return DiagnosticReport.EMPTY;
   }

   default DiagnosticReport onOpen( final ParsedDocument document ) {
      return defaultValidate( document );
   }

   default DiagnosticReport onChange( final ParsedDocument document ) {
      return defaultValidate( document );
   }

   default DiagnosticReport onSave( final ParsedDocument document ) {
      return defaultValidate( document );
   }
}
