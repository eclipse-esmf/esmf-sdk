/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.turtle.languageserver.turtle;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Position;

public class TurtleCompletionService extends TurtleService {

   public List<CompletionItem> complete( final ParsedDocument parsedDocument, final CompletionParams completionParams ) {
      final Position position = completionParams.getPosition();
      final long targetLine = position.getLine();
      final long targetColumn = position.getCharacter() - 1L; // character one entry before cursor position is relevant

      // First try: cursor is right after the colon of a namespace prefix (e.g. user typed "ex:")
      // This is important for incomplete trees with error nodes, when PREFIXED_NAME is not parsed
      TurtleSyntaxTree.Token namespaceAtCursor = parsedDocument.turtleSyntaxTree()
            .findMatchingTreeSitterToken( List.of( ParserTokenType.NAMESPACE ), targetLine, targetColumn );

      if ( namespaceAtCursor == null ) {
         // Second try: cursor is inside the local-name part of a prefixed name (e.g. "ex:sub|ject").
         // Find the enclosing prefixed_name node and retrieve its namespace child.
         // Relevant when user re-triggers completion manually
         final TurtleSyntaxTree.Token prefixedNameAtCursor = parsedDocument.turtleSyntaxTree()
               .findMatchingTreeSitterToken( List.of( ParserTokenType.PREFIXED_NAME ), targetLine, targetColumn );
         if ( prefixedNameAtCursor == null ) {
            return List.of();
         }
         namespaceAtCursor = prefixedNameAtCursor.children().stream()
               .filter( n -> ParserTokenType.NAMESPACE.equals( n.type() ) )
               .filter( TurtleSyntaxTree.Token.class::isInstance )
               .map( TurtleSyntaxTree.Token.class::cast )
               .findFirst()
               .orElse( null );
         if ( namespaceAtCursor == null ) {
            return List.of();
         }
      }

      final String prefix = namespaceAtCursor.content();
      return parsedDocument.turtleSyntaxTree().nodes()
            .filter( t -> ParserTokenType.PREFIXED_NAME.equals( t.type() ) )
            .filter( n -> isPrefixedBy( n, prefix ) )
            .flatMap( n -> n.children().stream() )
            .filter( t -> ParserTokenType.PN_LOCAL.equals( t.type() ) )
            .filter( TurtleSyntaxTree.Token.class::isInstance )
            .map( TurtleSyntaxTree.Token.class::cast )
            .map( TurtleSyntaxTree.Token::content )
            .map( CompletionItem::new )
            .collect( Collectors.toSet() ).stream().toList();
   }
}
