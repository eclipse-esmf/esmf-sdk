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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.lsp4j.Diagnostic;

public class DocumentDiagnosticsStore {
   private final Map<String, List<Diagnostic>> syntaxDiagnostics = new ConcurrentHashMap<>();
   private final Map<String, List<Diagnostic>> aspectDiagnostics = new ConcurrentHashMap<>();

   public void putSyntax( final String uri, final List<Diagnostic> diagnostics ) {
      syntaxDiagnostics.put( uri, List.copyOf( diagnostics ) );
   }

   public void putAspect( final String uri, final List<Diagnostic> diagnostics ) {
      aspectDiagnostics.put( uri, List.copyOf( diagnostics ) );
   }

   public void clearAspect( final String uri ) {
      aspectDiagnostics.remove( uri );
   }

   public void clear( final String uri ) {
      syntaxDiagnostics.remove( uri );
      aspectDiagnostics.remove( uri );
   }

   public List<Diagnostic> getCombined( final String uri ) {
      final List<Diagnostic> diagnostics = new ArrayList<>();
      diagnostics.addAll( syntaxDiagnostics.getOrDefault( uri, List.of() ) );
      diagnostics.addAll( aspectDiagnostics.getOrDefault( uri, List.of() ) );
      return diagnostics;
   }
}
