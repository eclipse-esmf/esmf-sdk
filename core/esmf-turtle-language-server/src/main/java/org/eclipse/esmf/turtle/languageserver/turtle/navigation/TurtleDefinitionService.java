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

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class TurtleDefinitionService {
   private static final List<String> TYPE_DEFINITION_PREDICATES = List.of( "a", "rdf:type" );

   public Optional<Location> findDefinition( final ParsedDocument parsedDocument, final TurtleSyntaxTree turtleSyntaxTree,
         final Position position ) {
      final TurtleSyntaxTree.Token highlightedToken = turtleSyntaxTree.findMatchingTreeSitterToken( position.getLine(),
            position.getCharacter() );
      if ( highlightedToken == null ) {
         return Optional.empty();
      }
      switch ( highlightedToken.type() ) {
         case ParserTokenType.PN_PREFIX -> {
            return findPrefixDefinition( highlightedToken.content(), parsedDocument, turtleSyntaxTree );
         }
         case ParserTokenType.PN_LOCAL -> {
            return findElementDefinition( position, parsedDocument, turtleSyntaxTree );
         }
         default -> {
            return Optional.empty();
         }
      }
   }

   private Optional<Location> findPrefixDefinition( final String prefixName, final ParsedDocument parsedDocument,
         final TurtleSyntaxTree turtleSyntaxTree ) {
      final Optional<TurtleSyntaxTree.Node> prefixDefinitionNode = turtleSyntaxTree.nodes()
            .filter( node -> ParserTokenType.DIRECTIVE.equals( node.type() ) )
            .flatMap( node -> node.children().stream() )
            .filter( node -> ParserTokenType.PREFIX_ID.equals( node.type() ) )
            .flatMap( node -> node.children().stream() )
            .filter( node -> ParserTokenType.NAMESPACE.equals( node.type() ) )
            .flatMap( node -> node.children().stream() )
            .filter( node -> {
               if ( node instanceof TurtleSyntaxTree.Token token ) {
                  return ParserTokenType.PN_PREFIX.equals( token.type() )
                        && token.content().equals( prefixName );
               }
               return false;
            } )
            .findFirst();
      return prefixDefinitionNode.map( definition -> getLocationForLsp( parsedDocument, definition ) );
   }

   private Optional<Location> findElementDefinition( final Position position, final ParsedDocument parsedDocument,
         final TurtleSyntaxTree turtleSyntaxTree ) {
      final TurtleSyntaxTree.Token prefixedName = turtleSyntaxTree
            .findMatchingTreeSitterToken( List.of( ParserTokenType.PREFIXED_NAME ), position.getLine(), position.getCharacter() );

      if ( prefixedName == null ) {
         return Optional.empty();
      }

      final Optional<TurtleSyntaxTree.Node> elementDefinition = turtleSyntaxTree.tokens()
            .filter( t -> ParserTokenType.TRIPLE.equals( t.type() ) )
            .filter( this::hasTypeDefinitionPredicate )
            .flatMap( t -> t.children().stream() )
            .filter( n -> ParserTokenType.SUBJECT.equals( n.type() ) )
            .flatMap( n -> n.children().stream() )
            .filter( n -> {
               if ( n instanceof TurtleSyntaxTree.Token t ) {
                  return ParserTokenType.PREFIXED_NAME.equals( t.type() ) && prefixedName.content().equals( t.content() );
               }
               return false;
            } )
            .flatMap( node -> node.children().stream() )
            .filter( node -> ParserTokenType.PN_LOCAL.equals( node.type() ) )
            .findFirst();
      return elementDefinition.map( definition -> getLocationForLsp( parsedDocument, definition ) );
   }

   private boolean hasTypeDefinitionPredicate( final TurtleSyntaxTree.Token token ) {
      return token.children().stream()
            .filter( node -> ParserTokenType.PROPERTY_LIST.equals( node.type() ) )
            .flatMap( node -> node.children().stream() )
            .filter( node -> ParserTokenType.PROPERTY.equals( node.type() ) )
            .flatMap( node -> node.children().stream() )
            .anyMatch( node -> {
               if ( node instanceof TurtleSyntaxTree.Token t ) {
                  return ParserTokenType.PREDICATE.equals( t.type() ) && TYPE_DEFINITION_PREDICATES.contains( t.content() );
               }
               return false;
            } );
   }

   private Location getLocationForLsp( final ParsedDocument parsedDocument, final TurtleSyntaxTree.Node node ) {
      final Range range = new Range(
            new Position( node.location().fromLine(), node.location().fromColumn() ),
            new Position( node.location().toLine(), node.location().toColumn() ) );
      return new Location( parsedDocument.getUri(), range );
   }
}
