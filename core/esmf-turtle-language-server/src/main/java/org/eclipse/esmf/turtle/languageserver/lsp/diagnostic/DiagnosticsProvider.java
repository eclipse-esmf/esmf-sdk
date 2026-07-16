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

package org.eclipse.esmf.turtle.languageserver.lsp.diagnostic;

import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

/**
 * A diagnostics provider takes a (parsed) RDF/Turtle source document as input and creates a
 * diagnostics report for it.
 */
public interface DiagnosticsProvider {
   /**
    * Validate the input document
    *
    * @param document the document
    * @return the corresponding diagnostics report
    */
   DiagnosticReport validate( ParsedDocument document );

   /**
    * The diagnostics provider can either be "fast" where it can be executed immediately on every model
    * change, or "delayed" where we assume the checks can be expensive and are executed only manually
    * triggered or after a delay.
    *
    * @return the type of provider
    */
   default Type type() {
      return Type.FAST;
   }

   enum Type {
      FAST, DELAYED
   }
}
