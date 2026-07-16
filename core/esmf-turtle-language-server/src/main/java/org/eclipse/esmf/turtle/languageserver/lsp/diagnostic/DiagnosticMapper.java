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

import java.util.List;

import org.eclipse.esmf.Diagnostic.Severity;
import org.eclipse.esmf.DocumentDiagnostic;
import org.eclipse.esmf.treesitterturtle.TurtleDocumentDiagnostic;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

/**
 * Translates server internal diagnostics representations into LSP conformant objects
 */
public final class DiagnosticMapper {
   public List<Diagnostic> toDiagnostics( final Document document, final DiagnosticReport result ) {
      return result.diagnostics().stream()
            .filter( violation -> appliesToDocument( document, violation ) )
            .map( this::toDiagnostic )
            .toList();
   }

   private boolean appliesToDocument( final Document document, final org.eclipse.esmf.Diagnostic<?> diagnostic ) {
      if ( diagnostic instanceof final DocumentDiagnostic<?> documentDiagnostic ) {
         return document.getUri().equals( documentDiagnostic.sourceLocation() );
      }
      return true;
   }

   private Diagnostic toDiagnostic( final org.eclipse.esmf.Diagnostic<?> turtleDiagnostic ) {
      final Diagnostic diagnostic = new Diagnostic();
      diagnostic.setSeverity( toDiagnosticSeverity( turtleDiagnostic.severity() ) );
      diagnostic.setMessage( turtleDiagnostic.message() );
      diagnostic.setCode( turtleDiagnostic.code().code() );
      if ( turtleDiagnostic instanceof final TurtleDocumentDiagnostic turtleDocumentDiagnostic ) {
         diagnostic.setRange( toRange( turtleDocumentDiagnostic ) );
      } else {
         diagnostic.setRange( fallbackRange() );
      }
      return diagnostic;
   }

   private DiagnosticSeverity toDiagnosticSeverity( final Severity severity ) {
      return switch ( severity ) {
         case ERROR -> DiagnosticSeverity.Error;
         case WARNING -> DiagnosticSeverity.Warning;
         case INFO -> DiagnosticSeverity.Information;
         case HINT -> DiagnosticSeverity.Hint;
      };
   }

   private Range toRange( final DocumentDiagnostic<?> diagnostic ) {
      return new Range( new Position( diagnostic.location().fromLine(), diagnostic.location().fromColumn() ),
            new Position( diagnostic.location().toLine(), diagnostic.location().toColumn() ) );
   }

   private Range fallbackRange() {
      return new Range( new Position( 0, 0 ), new Position( 0, 1 ) );
   }
}
