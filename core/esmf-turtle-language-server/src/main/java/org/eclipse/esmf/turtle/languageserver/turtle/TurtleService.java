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

import static org.eclipse.esmf.aspectmodel.StreamUtil.asSortedMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;

/**
 * Base class for services that operate on a {@link TurtleSyntaxTree}
 */
public abstract class TurtleService {
   protected static final List<String> TYPE_DEFINITION_PREDICATES = List.of( "a", "rdf:type" );

   /**
    * Takes a token representing a triple and checks if it has a rdf:type assertion
    *
    * @param triple the triple token
    * @return true if 'rdf:type' or 'a' is used as predictate
    */
   protected boolean hasTypeDefinitionPredicate( final TurtleSyntaxTree.Token triple ) {
      final Map<TurtleSyntaxTree.Node, TurtleSyntaxTree.Node> predicateObjectMap = predicateObjectMapForTriple( triple );
      return predicateObjectMap.keySet().stream().map( TurtleSyntaxTree.Node::content )
            .anyMatch( TYPE_DEFINITION_PREDICATES::contains );
   }

   protected boolean isPrefixedBy( final TurtleSyntaxTree.Node prefixedName, final String prefix ) {
      return prefixedName.children().stream()
            .filter( TurtleSyntaxTree.Node::isToken )
            .filter( n -> ParserTokenType.NAMESPACE.equals( n.type() ) )
            .anyMatch( n -> n.content().equals( prefix ) );
   }

   protected Map<TurtleSyntaxTree.Node, TurtleSyntaxTree.Node> predicateObjectMapForTriple( final TurtleSyntaxTree.Token triple ) {
      return triple.childWithType( ParserTokenType.PROPERTY_LIST ).stream()
            .flatMap( propertyList -> propertyList.children().stream()
                  .filter( node -> ParserTokenType.PROPERTY.equals( node.type() ) ) )
            .flatMap( property -> property.childWithType( ParserTokenType.PREDICATE )
                  .filter( TurtleSyntaxTree.Node::isToken )
                  .stream()
                  .flatMap( predicate -> property.childWithType( ParserTokenType.OBJECT_LIST )
                        .filter( TurtleSyntaxTree.Node::isToken ).stream()
                        .map( objectToken -> Map.entry( predicate, objectToken ) ) ) )
            .collect( asSortedMap() );
   }

   protected Stream<TurtleSyntaxTree.Node> getPrefixDefinitionTokens( final TurtleSyntaxTree turtleSyntaxTree ) {
      return turtleSyntaxTree.nodes()
            .filter( node -> ParserTokenType.DIRECTIVE.equals( node.type() ) )
            .flatMap( node -> node.children().stream() )
            .filter( node -> ParserTokenType.PREFIX_ID.equals( node.type() ) )
            .flatMap( node -> node.children().stream() )
            .filter( node -> ParserTokenType.NAMESPACE.equals( node.type() ) )
            .flatMap( node -> node.children().stream() )
            .filter( TurtleSyntaxTree.Node::isToken );
   }
}
