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

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.RiotParseException;
import org.apache.jena.riot.system.ErrorHandlerFactory;
import org.apache.jena.riot.system.StreamRDFLib;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurtleSyntaxValidationService {
   private static final Logger LOG = LoggerFactory.getLogger( TurtleSyntaxValidationService.class );
   private static final String SYNTAX_SOURCE = "lsp-server.syntax";

   public List<Diagnostic> validate( final String content ) {
      final List<Diagnostic> diagnostics = new ArrayList<>();

      try {
         RDFParser.create()
               .fromString( content )
               .lang( Lang.TTL )
               .errorHandler( ErrorHandlerFactory.errorHandlerStrictNoLogging )
               .parse( StreamRDFLib.sinkNull() );
         LOG.debug( "[validate] turtle parsing successful" );
      } catch ( final RiotParseException exception ) {
         LOG.warn( "[validate] parse error at line={}, col={}: {}", exception.getLine(), exception.getCol(), exception.getMessage() );
         diagnostics.add( toDiagnostic( exception.getMessage(), exception.getLine(), exception.getCol() ) );
      } catch ( final RiotException exception ) {
         LOG.warn( "[validate] rdf error: {}", exception.getMessage() );
         diagnostics.add( toDiagnostic( exception.getMessage(), 1, 1 ) );
      }

      LOG.debug( "[validate] found {} diagnostic(s)", diagnostics.size() );
      return diagnostics;
   }

   private Diagnostic toDiagnostic( final String message, final long line, final long column ) {
      final int safeLine = (int) Math.max( 0, line - 1 );
      final int safeColumn = (int) Math.max( 0, column - 1 );

      final Diagnostic diagnostic = new Diagnostic();
      diagnostic.setSource( SYNTAX_SOURCE );
      diagnostic.setSeverity( DiagnosticSeverity.Error );
      diagnostic.setMessage( message != null ? message : "Invalid Turtle syntax" );
      diagnostic.setRange( new Range( new Position( safeLine, safeColumn ), new Position( safeLine, safeColumn + 1 ) ) );
      return diagnostic;
   }
}
