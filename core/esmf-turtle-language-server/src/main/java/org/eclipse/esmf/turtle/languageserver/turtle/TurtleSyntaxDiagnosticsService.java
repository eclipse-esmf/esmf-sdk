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

package org.eclipse.esmf.turtle.languageserver.turtle;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.esmf.Diagnostic;
import org.eclipse.esmf.Location;
import org.eclipse.esmf.treesitterturtle.TurtleDiagnosticCode;
import org.eclipse.esmf.treesitterturtle.TurtleDocumentDiagnostic;
import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

import org.treesitter.TSNode;

public class TurtleSyntaxDiagnosticsService {
   public DiagnosticReport validate( final ParsedDocument parsedDocument ) {
      return new DiagnosticReport( checkNode( parsedDocument.concreteSyntaxTree().getRootNode(),
            parsedDocument.sourceDocument().getUri() ).toList() );
   }

   private Stream<Diagnostic<?>> checkNode( final TSNode node, final String sourceLocation ) {
      return Stream.concat( node.isError() || node.isMissing() ? Stream.of( diagnosticForNode( node, sourceLocation ) ) : Stream.empty(),
            IntStream.range( 0, node.getChildCount() ).boxed().map( node::getChild )
                  .flatMap( child -> checkNode( child, sourceLocation ) ) );
   }

   private TurtleDocumentDiagnostic diagnosticForNode( final TSNode node, final String sourceLocation ) {
      final String message;
      if ( node.isMissing() ) {
         message = "Syntax error: Missing '" + node.getGrammarType() + "'";
      } else {
         message = "Syntax error";
      }
      final Location location = new Location( node.getStartPoint().getRow(), node.getStartPoint().getColumn(), node.getEndPoint().getRow(),
            node.getEndPoint().getColumn() );
      return new TurtleDocumentDiagnostic( message,
            TurtleDiagnosticCode.E0003, sourceLocation, location );
   }
}
