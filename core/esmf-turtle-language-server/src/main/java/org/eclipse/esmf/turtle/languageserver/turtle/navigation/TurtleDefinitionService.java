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

package org.eclipse.esmf.turtle.languageserver.turtle.navigation;

import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.turtle.TurtleService;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;

public class TurtleDefinitionService extends TurtleService {
   public Optional<Location> findDefinition( final ParsedDocument parsedDocument, final Position position ) {
      final TurtleSyntaxTree turtleSyntaxTree = parsedDocument.turtleSyntaxTree();
      final TurtleSyntaxTree.Node highlightedToken = turtleSyntaxTree.findMatchingTreeSitterToken( position.getLine(),
            position.getCharacter() );
      if ( highlightedToken == null ) {
         return Optional.empty();
      }
      return switch ( highlightedToken.type() ) {
         case ParserTokenType.PN_PREFIX -> findPrefixDefinition( highlightedToken.content(), parsedDocument, turtleSyntaxTree );
         case ParserTokenType.PN_LOCAL -> findElementDefinition( position, parsedDocument, turtleSyntaxTree );
         default -> Optional.empty();
      };
   }

   private Optional<Location> findPrefixDefinition( final String prefixName, final ParsedDocument parsedDocument,
         final TurtleSyntaxTree turtleSyntaxTree ) {
      final Optional<TurtleSyntaxTree.Node> prefixDefinitionNode = this.getPrefixDefinitionTokens( turtleSyntaxTree )
            .filter( token -> ParserTokenType.PN_PREFIX.equals( token.type() )
                  && token.content().equals( prefixName ) )
            .findFirst()
            .map( TurtleSyntaxTree.Node.class::cast );
      return prefixDefinitionNode.map( definition -> getLocationForLsp( parsedDocument, definition ) );
   }

   private Optional<Location> findElementDefinition( final Position position, final ParsedDocument parsedDocument,
         final TurtleSyntaxTree turtleSyntaxTree ) {
      final TurtleSyntaxTree.Node prefixedName = turtleSyntaxTree
            .findMatchingTreeSitterToken( List.of( ParserTokenType.PREFIXED_NAME ), position.getLine(), position.getCharacter() );

      if ( prefixedName == null ) {
         return Optional.empty();
      }

      final Optional<TurtleSyntaxTree.Node> elementDefinition = typeDefinitionSubjectPrefixedNames( turtleSyntaxTree )
            .filter( n -> n.isToken() && prefixedName.content().equals( n.content() ) )
            .flatMap( node -> node.children().stream() )
            .filter( node -> ParserTokenType.PN_LOCAL.equals( node.type() ) )
            .findFirst();
      return elementDefinition.map( definition -> getLocationForLsp( parsedDocument, definition ) );
   }
}
