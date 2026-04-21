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

import java.util.List;

import org.eclipse.lsp4j.Diagnostic;

import org.eclipse.esmf.turtle.languageserver.aspect.diagnostic.AspectDiagnosticMapper;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationResult;

public class DocumentDiagnosticsService {
   private final TurtleSyntaxValidationService syntaxValidationService;
   private final AspectDiagnosticMapper aspectDiagnosticMapper;
   private final DocumentDiagnosticsStore diagnosticsStore;

   public DocumentDiagnosticsService() {
      this( new TurtleSyntaxValidationService(), new AspectDiagnosticMapper(), new DocumentDiagnosticsStore() );
   }

   DocumentDiagnosticsService(
         final TurtleSyntaxValidationService syntaxValidationService,
         final AspectDiagnosticMapper aspectDiagnosticMapper,
         final DocumentDiagnosticsStore diagnosticsStore ) {
      this.syntaxValidationService = syntaxValidationService;
      this.aspectDiagnosticMapper = aspectDiagnosticMapper;
      this.diagnosticsStore = diagnosticsStore;
   }

   public void updateSyntax( final String uri, final String content ) {
      diagnosticsStore.putSyntax( uri, syntaxValidationService.validate( content ) );
   }

   public void updateAspect( final String uri, final AspectValidationResult result ) {
      diagnosticsStore.putAspect( uri, aspectDiagnosticMapper.toDiagnostics( uri, result ) );
   }

   public void clearAspect( final String uri ) {
      diagnosticsStore.clearAspect( uri );
   }

   public void clearAll( final String uri ) {
      diagnosticsStore.clear( uri );
   }

   public List<Diagnostic> getCombined( final String uri ) {
      return diagnosticsStore.getCombined( uri );
   }
}
