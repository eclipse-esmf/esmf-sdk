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

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.esmf.aspectmodel.DocumentLocationViolation;
import org.eclipse.esmf.aspectmodel.Violation;
import org.eclipse.esmf.aspectmodel.Violation.Severity;
import org.eclipse.esmf.aspectmodel.ViolationReport;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticCodeDescription;
import org.eclipse.lsp4j.DiagnosticRelatedInformation;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

/**
 * Translates server internal violations into LSP conformant diagnostics
 */
public final class DiagnosticMapper {
   private static final Range FALLBACK = new Range( new Position( 0, 0 ), new Position( 0, 0 ) );

   public Map<URI, List<Diagnostic>> apply( final Document sourceDocument, final ViolationReport report ) {
      final Map<URI, List<Diagnostic>> result = new HashMap<>();
      // Always include empty diagnostics for sourceDocument, otherwise LSP client does not clear old but
      // fixed findings
      result.put( sourceDocument.uri(), new ArrayList<>() );
      for ( final Violation violation : report.violations() ) {
         final Diagnostic diagnostic = new Diagnostic();
         diagnostic.setSeverity( toDiagnosticSeverity( violation.severity() ) );
         diagnostic.setMessage( violation.message() );
         diagnostic.setCode( violation.code().code() );
         violation.code().href().ifPresent( href -> diagnostic.setCodeDescription( new DiagnosticCodeDescription( href ) ) );
         if ( violation instanceof final DocumentLocationViolation documentLocationViolation ) {
            if ( documentLocationViolation.sourceDocument().equals( sourceDocument.uri() ) ) {
               diagnostic.setRange( toRange( documentLocationViolation ) );
            } else {
               final DiagnosticRelatedInformation relatedInformation = new DiagnosticRelatedInformation();
               relatedInformation.setMessage( "Root cause of the problem is here" );
               final Location relatedLocation = new Location();
               relatedLocation.setUri( documentLocationViolation.sourceDocument().toString() );
               relatedLocation.setRange( toRange( documentLocationViolation ) );
               relatedInformation.setLocation( relatedLocation );
               diagnostic.setRelatedInformation( List.of( relatedInformation ) );
               diagnostic.setRange( FALLBACK );
            }
         } else {
            diagnostic.setRange( FALLBACK );
         }
         result.get( sourceDocument.uri() ).add( diagnostic );
      }
      return result;
   }

   private DiagnosticSeverity toDiagnosticSeverity( final Severity severity ) {
      return switch ( severity ) {
         case ERROR -> DiagnosticSeverity.Error;
         case WARNING -> DiagnosticSeverity.Warning;
         case INFO -> DiagnosticSeverity.Information;
         case HINT -> DiagnosticSeverity.Hint;
      };
   }

   private Range toRange( final DocumentLocationViolation diagnostic ) {
      return new Range( new Position( diagnostic.location().fromLine(), diagnostic.location().fromColumn() ),
            new Position( diagnostic.location().toLine(), diagnostic.location().toColumn() ) );
   }
}
