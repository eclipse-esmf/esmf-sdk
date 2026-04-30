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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.RiotParseException;
import org.apache.jena.riot.system.ErrorHandlerFactory;
import org.apache.jena.riot.system.StreamRDFLib;

import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDiagnostic;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDiagnosticsService;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDocumentDiagnostic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JenaTurtleSyntaxValidationService implements TurtleDiagnosticsService {
   private static final Logger LOG = LoggerFactory.getLogger( JenaTurtleSyntaxValidationService.class );

   @Override
   public DiagnosticReport check( final Document document ) {
      final List<TurtleDiagnostic> diagnostics = new ArrayList<>();

      try {
         RDFParser.create()
               .source( new StringReader( document.getContent() ) )
               .lang( Lang.TTL )
               .errorHandler( ErrorHandlerFactory.errorHandlerStrictNoLogging )
               .parse( StreamRDFLib.sinkNull() );
         LOG.debug( "[validate] turtle parsing successful" );
      } catch ( final RiotParseException exception ) {
         LOG.warn( "[validate] parse error at line={}, col={}: {}", exception.getLine(), exception.getCol(), exception.getMessage() );
         diagnostics.add( new TurtleDocumentDiagnostic( exception.getMessage(), TurtleDiagnostic.TurtleCode.E0003, document.getUri(),
               (int) exception.getLine(), (int) exception.getCol(), (int) exception.getLine(), (int) exception.getCol() ) );
      } catch ( final RiotException exception ) {
         LOG.warn( "[validate] RDF error: {}", exception.getMessage() );
         diagnostics.add( new TurtleDocumentDiagnostic( exception.getMessage(), TurtleDiagnostic.TurtleCode.E0003, document.getUri(),
               1, 1, 1, 1 ) );
      }

      LOG.debug( "[validate] found {} diagnostic(s)", diagnostics.size() );
      return new DiagnosticReport( diagnostics );
   }
}
