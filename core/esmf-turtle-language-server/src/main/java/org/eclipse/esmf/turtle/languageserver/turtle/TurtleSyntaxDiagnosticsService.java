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

import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDiagnostic;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDiagnosticsService;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDocumentDiagnostic;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

import org.treesitter.TSNode;

public class TurtleSyntaxDiagnosticsService implements TurtleDiagnosticsService {
   @Override
   public DiagnosticReport defaultValidate( final ParsedDocument parsedDocument ) {
      return new DiagnosticReport( checkNode( parsedDocument.concreteSyntaxTree().getRootNode(),
            parsedDocument.sourceDocument().getUri() ).toList() );
   }

   private Stream<TurtleDiagnostic> checkNode( final TSNode node, final String sourceLocation ) {
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
      return new TurtleDocumentDiagnostic( message,
            TurtleDiagnostic.TurtleCode.E0003, sourceLocation,
            node.getStartPoint().getRow(), node.getStartPoint().getColumn(),
            node.getEndPoint().getRow(), node.getEndPoint().getColumn() );
   }
}
