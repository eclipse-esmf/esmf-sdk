/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */

package org.eclipse.esmf.turtle.languageserver.turtle;

import static org.eclipse.esmf.aspectmodel.StreamUtil.asMap;
import static org.eclipse.esmf.metamodel.vocabulary.SammNs.UNIT;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.shacl.SHACL;
import org.eclipse.esmf.metamodel.Units;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

import org.apache.jena.rdf.model.Resource;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Position;

public class TurtleCompletionService extends AbstractTurtleCompletionService {
   static final Map<String, List<CompletionItem>> SAMM_PREFIXES_COMPLETION_MAP = initSammPrefixesCompletionMap();

   /**
    * Dropdown completion for the prefixed name at the cursor. Returns the full list of candidates for
    * the client to filter, so client features like fuzzy match are supported.
    * <ul>
    * <li>{@code :} prefix: the local names of the document subjects that fit the expected type of the
    * predicate</li>
    * <li>after {@code samm:dataType}: all XSD data types of the typed prefix</li>
    * <li>otherwise: all names of the typed SAMM, unit or SHACL namespace</li>
    * </ul>
    */
   public List<CompletionItem> complete( final ParsedDocument parsedDocument, final CompletionParams completionParams ) {
      final Position position = completionParams.getPosition();
      final Optional<TurtleSyntaxTree.Node> namespaceAtCursor = namespaceAtCursor( parsedDocument, position );
      if ( namespaceAtCursor.isEmpty() ) {
         return List.of();
      }
      final TurtleSyntaxTree.Node typedNamespace = namespaceAtCursor.get();
      final String prefix = typedNamespace.content();
      final TurtleSyntaxTree tree = parsedDocument.turtleSyntaxTree();
      final TypedStatement statement = typedStatement( tree, position );

      if ( ":".equals( prefix ) ) {
         final Optional<TurtleSyntaxTree.Node> typedNode = localNameAtCursor( parsedDocument, position );
         final Set<Resource> expectedClasses = closestPredicateExpectedClasses( statement.predicateIri() );
         final Set<String> expectedTypesSet = expectedTypes( tree, expectedClasses ).collect( Collectors.toSet() );
         return parsedDocument.turtleSyntaxTree().tokens()
               .filter( t -> ParserTokenType.PREFIXED_NAME.equals( t.type() ) )
               .filter( n -> isPrefixedBy( n, prefix ) )
               .flatMap( n -> n.children().stream() )
               .filter( t -> ParserTokenType.PN_LOCAL.equals( t.type() ) )
               // The name being typed is part of the tree, but must not suggest itself
               .filter( t -> typedNode.isEmpty() || !t.location().equals( typedNode.get().location() ) )
               .map( TurtleSyntaxTree.Node::content )
               .filter( content -> expectedClasses.isEmpty() || expectedTypesSet.contains( ":" + content ) )
               // The subject of the statement being edited must not be suggested as its own object
               .filter( content -> !( ":" + content ).equals( statement.subject() ) )
               .map( CompletionItem::new )
               .collect( Collectors.toSet() ).stream().toList();
      }

      if ( statement.predicateIri().filter( SammNs.SAMM.dataType().getURI()::equals ).isPresent() ) {
         return dataTypeLocalNames( prefix ).map( CompletionItem::new ).toList();
      }

      return SAMM_PREFIXES_COMPLETION_MAP.getOrDefault( prefix, List.of() );
   }

   /**
    * All subjects of the document whose type is (a subclass of) one of the expected classes; the
    * client filters them
    */
   private Stream<String> expectedTypes( final TurtleSyntaxTree tree, final Set<Resource> expectedClasses ) {
      return declaredSubjectNames( tree, expectedClasses ).map( TurtleSyntaxTree.Node::content );
   }

   private static Set<Resource> closestPredicateExpectedClasses( final Optional<String> closestPredicateIri ) {
      return closestPredicateIri.map( iri -> EXPECTED_CLASSES.getOrDefault( iri, Set.of() ) )
            .orElse( Set.of() );
   }

   /**
    * The local names of all data types with the given prefix, e.g. {@code int}. The client filters
    * them by the typed text.
    */
   private static Stream<String> dataTypeLocalNames( final String prefix ) {
      return DATA_TYPE_NAMES.stream().filter( name -> name.startsWith( prefix ) ).map( name -> name.substring( prefix.length() ) );
   }

   private static HashMap<String, List<CompletionItem>> initSammPrefixesCompletionMap() {
      final SHACL shacl = new SHACL();
      final HashMap<String, List<CompletionItem>> result = new HashMap<>( SammNs.sammNamespaces().map( n -> {
         final String prefix = n.getShortForm() + ":";
         final List<CompletionItem> elements = Stream.of( n.allResources(), n.allProperties() )
               .flatMap( Collection::stream )
               .map( r -> new CompletionItem( r.getLocalName() ) ).toList();
         return Map.entry( prefix, elements );
      } ).collect( asMap() ) );

      result.put( UNIT.getShortForm() + ":", Units.getUnits().stream()
            .map( u -> new CompletionItem( u.getName() ) ).toList() );

      result.put( shacl.getShortForm() + ":", Stream.of( shacl.allProperties(), shacl.allResources() )
            .flatMap( Collection::stream )
            .map( r -> new CompletionItem( r.getLocalName() ) ).toList() );

      return result;
   }
}
