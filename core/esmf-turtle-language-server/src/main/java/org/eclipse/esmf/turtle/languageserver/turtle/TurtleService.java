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
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.aspect.navigation.ExternalModelFileCache;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

/**
 * Base class for services that operate on a {@link TurtleSyntaxTree}
 */
public abstract class TurtleService {
   protected static final List<String> SAMM_PREFIXES = SammNs.sammNamespaces().map( RdfNamespace::getShortForm ).toList();
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

   /**
    * Cached/materialized model files (e.g. SAMM meta model files opened via "go to definition") are
    * internal structures the user never wrote and must never be validated by any provider - regardless
    * of whether the provider is aspect-model-specific or a generic Turtle check.
    */
   protected boolean isExemptFromValidation( final ParsedDocument parsedDocument ) {
      return ExternalModelFileCache.isCachedModelUri( parsedDocument.sourceDocument().uri() );
   }

   protected boolean documentIsAspectModel( final ParsedDocument parsedDocument ) {
      final TurtleSyntaxTree syntaxTree = parsedDocument.turtleSyntaxTree();
      return syntaxTree.rootNode().children().stream()
            .filter( n -> ParserTokenType.DIRECTIVE.equals( n.type() ) ).flatMap( n -> n.children().stream() )
            .filter( n -> ParserTokenType.PREFIX_ID.equals( n.type() ) ).flatMap( n -> n.children().stream() )
            .filter( n -> ParserTokenType.NAMESPACE.equals( n.type() ) ).flatMap( n -> n.children().stream() )
            .filter( n -> ParserTokenType.PN_PREFIX.equals( n.type() ) )
            .anyMatch( n -> SAMM_PREFIXES.contains( n.content() ) );
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

   protected Stream<TurtleSyntaxTree.Node> typeDefinitionSubjectPrefixedNames( final TurtleSyntaxTree tree ) {
      return tree.tokens()
            .filter( t -> ParserTokenType.TRIPLE.equals( t.type() ) )
            .filter( this::hasTypeDefinitionPredicate )
            .flatMap( t -> t.children().stream() )
            .filter( n -> ParserTokenType.SUBJECT.equals( n.type() ) )
            .flatMap( n -> n.children().stream() )
            .filter( n -> ParserTokenType.PREFIXED_NAME.equals( n.type() ) );
   }

   protected Stream<TurtleSyntaxTree.Node> allSubjectPrefixedNames( final TurtleSyntaxTree tree ) {
      return tree.tokens()
            .filter( t -> ParserTokenType.TRIPLE.equals( t.type() ) )
            .flatMap( t -> t.children().stream() )
            .filter( n -> ParserTokenType.SUBJECT.equals( n.type() ) )
            .flatMap( n -> n.children().stream() )
            .filter( n -> ParserTokenType.PREFIXED_NAME.equals( n.type() ) );
   }

   /**
    * Expands an IRI reference or prefixed name found in the node, using the first matching
    * {@code @prefix} declaration.
    * Relative IRIs and Turtle escapes are not resolved.
    */
   protected Optional<String> expandedIri( final TurtleSyntaxTree.Node node, final TurtleSyntaxTree tree ) {
      final Optional<TurtleSyntaxTree.Node> iriReference = descendantWithType( node, ParserTokenType.IRI_REFERENCE );
      if ( iriReference.isPresent() ) {
         final String value = iriReference.get().content();
         return Optional.of( value.startsWith( "<" ) && value.endsWith( ">" ) ? value.substring( 1, value.length() - 1 ) : value );
      }
      return descendantWithType( node, ParserTokenType.PREFIXED_NAME )
            .flatMap( prefixedName -> expandedPrefixedName( prefixedName, tree ) );
   }

   protected Optional<String> expandedPrefixedName( final TurtleSyntaxTree.Node prefixedName, final TurtleSyntaxTree tree ) {
      final Optional<String> local = prefixedName.children().stream()
            .filter( child -> child.isToken() && ParserTokenType.PN_LOCAL.equals( child.type() ) )
            .map( TurtleSyntaxTree.Node::content ).findFirst();
      final String prefix = prefixedName.children().stream()
            .filter( child -> ParserTokenType.NAMESPACE.equals( child.type() ) )
            .map( TurtleSyntaxTree.Node::content ).findFirst().orElse( "" );
      final String prefixName = prefix.endsWith( ":" ) ? prefix.substring( 0, prefix.length() - 1 ) : prefix;
      return local.flatMap( name -> getPrefixIri( prefixName, tree ).map( iri -> iri + name ) );
   }

   /**
    * Retrieves the namespace IRI from the first matching {@code @prefix} declaration.
    */
   protected Optional<String> getPrefixIri( final String prefixName, final TurtleSyntaxTree tree ) {
      return tree.nodes().filter( node -> ParserTokenType.DIRECTIVE.equals( node.type() ) )
            .flatMap( node -> node.children().stream() )
            .filter( node -> ParserTokenType.PREFIX_ID.equals( node.type() ) )
            .filter( prefixId -> matchesPrefixName( prefixId, prefixName ) ).findFirst()
            .flatMap( prefixId -> prefixId.childWithType( ParserTokenType.IRI_REFERENCE ) )
            .map( TurtleSyntaxTree.Node::content )
            // IRI_REFERENCE content includes angle brackets: <urn:samm:...#>
            .map( TurtleService::stripAngleBrackets );
   }

   private boolean matchesPrefixName( final TurtleSyntaxTree.Node prefixIdNode, final String prefixName ) {
      final Optional<TurtleSyntaxTree.Node> namespaceNode = prefixIdNode.childWithType( ParserTokenType.NAMESPACE );
      if ( namespaceNode.isEmpty() ) {
         // Default prefix (:) has no pn_prefix child; empty prefixName matches it
         return prefixName.isEmpty();
      }
      // If there is no PN_PREFIX token, it's the default prefix (@prefix : <iri>)
      return namespaceNode.get().children().stream()
            .filter( node -> node.isToken() && ParserTokenType.PN_PREFIX.equals( node.type() ) )
            .map( TurtleSyntaxTree.Node::content ).findFirst()
            .map( prefixName::equals ).orElse( prefixName.isEmpty() );
   }

   protected Optional<TurtleSyntaxTree.Node> descendantWithType( final TurtleSyntaxTree.Node node, final String type ) {
      if ( type.equals( node.type() ) ) {
         return Optional.of( node );
      }
      return node.children().stream().map( child -> descendantWithType( child, type ) )
            .flatMap( Optional::stream ).findFirst();
   }

   protected Location getLocationForLsp( final ParsedDocument parsedDocument, final TurtleSyntaxTree.Node node ) {
      final Range range = new Range(
            new Position( node.location().fromLine(), node.location().fromColumn() ),
            new Position( node.location().toLine(), node.location().toColumn() ) );
      return new Location( parsedDocument.getUri().toString(), range );
   }

   private static String stripAngleBrackets( final String iri ) {
      return iri.startsWith( "<" ) && iri.endsWith( ">" ) ? iri.substring( 1, iri.length() - 1 ) : iri;
   }
}
