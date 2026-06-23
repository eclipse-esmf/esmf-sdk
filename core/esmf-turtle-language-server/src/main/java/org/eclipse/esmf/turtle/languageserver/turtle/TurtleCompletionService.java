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
      final TurtleSyntaxTree.Token namespaceAtCursor = parsedDocument.turtleSyntaxTree()
            .findMatchingTreeSitterToken( List.of( ParserTokenType.NAMESPACE ), position.getLine(),
                  position.getCharacter() - 1L ); // character one entry before cursor position is relevant to fetch previous token
      if ( namespaceAtCursor == null ) {
         return List.of();
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
