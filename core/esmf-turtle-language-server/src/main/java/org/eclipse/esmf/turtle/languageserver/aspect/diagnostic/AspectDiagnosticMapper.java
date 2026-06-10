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

import java.util.List;

import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDiagnostic;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDocumentDiagnostic;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public final class AspectDiagnosticMapper {
   public List<Diagnostic> toDiagnostics( final Document document, final DiagnosticReport result ) {
      return result.diagnostics().stream()
            .filter( violation -> appliesToDocument( document, violation ) )
            .map( this::toDiagnostic )
            .toList();
   }

   private boolean appliesToDocument( final Document document, final TurtleDiagnostic turtleDiagnostic ) {
      if ( turtleDiagnostic instanceof final TurtleDocumentDiagnostic turtleDocumentDiagnostic ) {
         return document.getUri().equals( turtleDocumentDiagnostic.sourceLocation() );
      }
      return true;
   }

   private Diagnostic toDiagnostic( final TurtleDiagnostic turtleDiagnostic ) {
      final Diagnostic diagnostic = new Diagnostic();
      diagnostic.setSeverity( DiagnosticSeverity.Error );
      diagnostic.setMessage( turtleDiagnostic.message() );
      diagnostic.setCode( turtleDiagnostic.code().code() );
      if ( turtleDiagnostic instanceof final TurtleDocumentDiagnostic turtleDocumentDiagnostic ) {
         diagnostic.setRange( toRange( turtleDocumentDiagnostic ) );
      }
      return diagnostic;
   }

   private Range toRange( final TurtleDocumentDiagnostic violation ) {
      return new Range( new Position( violation.fromLine(), violation.fromColumn() ),
            new Position( violation.toLine(), violation.toColumn() ) );
   }
}
