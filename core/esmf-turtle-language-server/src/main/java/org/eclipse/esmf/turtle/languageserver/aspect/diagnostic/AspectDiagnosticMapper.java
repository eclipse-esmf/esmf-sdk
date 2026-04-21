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

import java.net.URI;
import java.util.List;

import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectValidationResult;
import org.eclipse.esmf.turtle.languageserver.aspect.model.AspectViolationInfo;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

public final class AspectDiagnosticMapper {
   public static final String SOURCE = "lsp-server.aspect";

   public List<Diagnostic> toDiagnostics( final String documentUri, final AspectValidationResult result ) {
      return result.violations().stream()
            .filter( violation -> appliesToDocument( documentUri, violation ) )
            .map( this::toDiagnostic )
            .toList();
   }

   private boolean appliesToDocument( final String documentUri, final AspectViolationInfo violation ) {
      if ( violation.sourceLocation() == null ) {
         return true;
      }

      return URI.create( documentUri ).equals( violation.sourceLocation() );
   }

   private Diagnostic toDiagnostic( final AspectViolationInfo violation ) {
      final Diagnostic diagnostic = new Diagnostic();
      diagnostic.setSource( SOURCE );
      diagnostic.setSeverity( DiagnosticSeverity.Error );
      diagnostic.setMessage( violation.message() );
      diagnostic.setCode( Either.forLeft( violation.code() ) );
      diagnostic.setRange( toRange( violation ) );
      return diagnostic;
   }

   private Range toRange( final AspectViolationInfo violation ) {
      final long line = violation.line() != null ? violation.line() : 1L;
      final long column = violation.column() != null ? violation.column() : 1L;
      final int safeLine = (int) Math.max( 0, line - 1 );
      final int safeColumn = (int) Math.max( 0, column - 1 );
      return new Range( new Position( safeLine, safeColumn ), new Position( safeLine, safeColumn + 1 ) );
   }
}
