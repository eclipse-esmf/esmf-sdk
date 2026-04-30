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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.esmf.treesitterturtle.TreeSitterTurtle;
import org.eclipse.esmf.turtle.languageserver.diagnostic.DiagnosticReport;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDiagnostic;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDiagnosticsService;
import org.eclipse.esmf.turtle.languageserver.diagnostic.TurtleDocumentDiagnostic;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.jspecify.annotations.Nullable;
import org.treesitter.TSInputEdit;
import org.treesitter.TSLanguage;
import org.treesitter.TSNode;
import org.treesitter.TSParser;
import org.treesitter.TSPoint;
import org.treesitter.TSTree;

/**
 * Service for parsing Turtle documents using Tree-sitter and maintaining their syntax trees.
 * Supports incremental parsing for efficient updates when documents change.
 */
public class TreeSitterTurtleSyntaxValidationService implements TurtleDiagnosticsService {
   private final TSParser parser;
   private final Map<Document, TSTree> syntaxTrees = new HashMap<>();
   private final Map<Document, Rope> previousDocumentStates = new WeakHashMap<>();

   public TreeSitterTurtleSyntaxValidationService() {
      parser = new TSParser();
      final TSLanguage turtle = new TreeSitterTurtle();
      parser.setLanguage( turtle );
   }

   public TSTree getAbstractSyntaxTree( final Document document ) {
      return syntaxTrees.computeIfAbsent( document, this::parseDocument );
   }

   private TSTree parseDocument( final Document document ) {
      previousDocumentStates.put( document, document.getRope() );
      return parser.parseString( null, document.getContent() );
   }

   /**
    * Converts an LSP text change event into a Tree-sitter input edit.
    *
    * @param oldRope the rope before the change was applied
    * @param changeEvent the LSP change event
    * @return a TSInputEdit, or null if this is a full document change
    */
   private @Nullable TSInputEdit treeChangeFromLspChange( final Rope oldRope, final TextDocumentContentChangeEvent changeEvent ) {
      final Range range = changeEvent.getRange();
      if ( range == null ) {
         return null;
      }

      final Position startPos = range.getStart();
      final Position endPos = range.getEnd();
      final String newText = changeEvent.getText() != null ? changeEvent.getText() : "";

      final TSPoint startPoint = new TSPoint( startPos.getLine(), startPos.getCharacter() );
      final TSPoint oldEndPoint = new TSPoint( endPos.getLine(), endPos.getCharacter() );
      final int startByte = oldRope.getIndex( startPos.getLine(), startPos.getCharacter() );
      final int oldEndByte = oldRope.getIndex( endPos.getLine(), endPos.getCharacter() );
      final TSPoint newEndPoint = calculateNewEndPoint( startPoint, newText );
      final int newEndByte = startByte + newText.getBytes( StandardCharsets.UTF_8 ).length;
      return new TSInputEdit( startByte, oldEndByte, newEndByte, startPoint, oldEndPoint, newEndPoint );
   }

   /**
    * Calculates the new end point after inserting text at the start point.
    *
    * @param startPoint the starting position
    * @param newText the text being inserted
    * @return the new end position after insertion
    */
   private TSPoint calculateNewEndPoint( final TSPoint startPoint, final String newText ) {
      if ( newText.isEmpty() ) {
         return startPoint;
      }

      // Count newlines in the inserted text
      final long newlineCount = newText.chars().filter( ch -> ch == '\n' ).count();

      if ( newlineCount == 0 ) {
         // Single line insertion - same row, column advances by text length
         final int newColumn = startPoint.getColumn() + newText.length();
         return new TSPoint( startPoint.getRow(), newColumn );
      } else {
         // Multi-line insertion
         final int lastNewlineIndex = newText.lastIndexOf( '\n' );
         final int newRow = startPoint.getRow() + (int) newlineCount;
         final int newColumn = newText.length() - lastNewlineIndex - 1;
         return new TSPoint( newRow, newColumn );
      }
   }

   public void onOpen( final Document document ) {
      syntaxTrees.put( document, parseDocument( document ) );
   }

   public void onChange( final Document document, final TextDocumentContentChangeEvent changeEvent ) {
      final TSTree oldTree = syntaxTrees.get( document );
      if ( oldTree == null ) {
         syntaxTrees.put( document, parseDocument( document ) );
         return;
      }

      final Rope oldRope = previousDocumentStates.get( document );
      if ( oldRope == null ) {
         syntaxTrees.put( document, parseDocument( document ) );
         return;
      }

      final TSInputEdit edit = treeChangeFromLspChange( oldRope, changeEvent );
      if ( edit == null ) {
         syntaxTrees.put( document, parseDocument( document ) );
         return;
      }

      oldTree.edit( edit );
      final TSTree newTree = parser.parseString( oldTree, document.getContent() );
      syntaxTrees.put( document, newTree );
      previousDocumentStates.put( document, document.getRope() );
   }

   @Override
   public DiagnosticReport check( final Document document ) {
      final TSTree abstractSyntaxTree = getAbstractSyntaxTree( document );
      return new DiagnosticReport( checkNode( abstractSyntaxTree.getRootNode(), document.getUri() ) );
   }

   private List<TurtleDiagnostic> checkNode( final TSNode node, final String sourceLocation ) {
      final List<TurtleDiagnostic> result = new ArrayList<>();
      if ( node.hasError() ) {
         final TurtleDiagnostic diagnostic = new TurtleDocumentDiagnostic( "Syntax error: unexpected token '" + node.getType() + "'",
               TurtleDiagnostic.TurtleCode.E0003, sourceLocation,
               node.getStartPoint().getRow(), node.getStartPoint().getColumn(),
               node.getEndPoint().getRow(), node.getEndPoint().getColumn() );
         result.add( diagnostic );
      }
      for ( int i = 0; i < node.getChildCount(); i++ ) {
         result.addAll( checkNode( node.getChild( i ), sourceLocation ) );
      }
      return result;
   }
}
