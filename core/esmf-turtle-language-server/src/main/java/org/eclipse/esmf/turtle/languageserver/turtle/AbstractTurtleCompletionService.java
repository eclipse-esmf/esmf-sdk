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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.Location;
import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.aspectmodel.shacl.ShapeLoader;
import org.eclipse.esmf.aspectmodel.shacl.constraint.ClassConstraint;
import org.eclipse.esmf.aspectmodel.shacl.path.PredicatePath;
import org.eclipse.esmf.metamodel.datatype.SammXsdType;
import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.eclipse.lsp4j.Position;

/**
 * Base class for dropdown and inline completion services.
 */
public abstract class AbstractTurtleCompletionService extends TurtleService {
   private static final Model META_MODEL_DEFINITIONS = MetaModelFile.metaModelDefinitions();
   private static final Map<String, String> LATEST_SAMM_NAMESPACES = initLatestSammNamespaces();
   protected static final Map<String, Set<Resource>> EXPECTED_CLASSES = initExpectedClasses();
   protected static final List<String> DATA_TYPE_NAMES = initDataTypeNames();

   protected Optional<TurtleSyntaxTree.Node> namespaceAtCursor( final ParsedDocument parsedDocument, final Position position ) {
      final long targetLine = position.getLine();
      final long targetColumn = position.getCharacter() - 1L; // character one entry before cursor position is relevant

      // First try: cursor is right after the colon of a namespace prefix (e.g. user typed "ex:")
      // This is important for incomplete trees with error nodes, when PREFIXED_NAME is not parsed
      TurtleSyntaxTree.Node namespaceAtCursor = parsedDocument.turtleSyntaxTree()
            .findMatchingTreeSitterToken( List.of( ParserTokenType.NAMESPACE ), targetLine, targetColumn );

      if ( namespaceAtCursor == null ) {
         // Second try: cursor is inside the local-name part of a prefixed name (e.g. "ex:sub|ject").
         // Find the enclosing prefixed_name node and retrieve its namespace child.
         // Relevant when user re-triggers completion manually
         final TurtleSyntaxTree.Node prefixedNameAtCursor = parsedDocument.turtleSyntaxTree()
               .findMatchingTreeSitterToken( List.of( ParserTokenType.PREFIXED_NAME ), targetLine, targetColumn );
         if ( prefixedNameAtCursor == null ) {
            return Optional.empty();
         }
         namespaceAtCursor = prefixedNameAtCursor.children().stream()
               .filter( n -> ParserTokenType.NAMESPACE.equals( n.type() ) )
               .filter( TurtleSyntaxTree.Node::isToken )
               .findFirst()
               .orElse( null );
         if ( namespaceAtCursor == null ) {
            return Optional.empty();
         }
      }
      return Optional.of( namespaceAtCursor );
   }

   /**
    * The statement at the cursor, found with a single tree scan, with the predicate IRI and the
    * subject it
    * contains
    */
   protected TypedStatement typedStatement( final TurtleSyntaxTree tree, final Position cursor ) {
      final Optional<TurtleSyntaxTree.Token> statement = statementAt( tree, cursor );
      final Optional<String> predicateIri = statement
            .flatMap( token -> closestPredicate( token, cursor ) )
            .flatMap( node -> expandedIri( node, tree ) )
            .map( AbstractTurtleCompletionService::withLatestSammNamespace );
      final String subject = statement
            .flatMap( t -> t.childWithType( ParserTokenType.SUBJECT ) )
            .flatMap( s -> s.childWithType( ParserTokenType.PREFIXED_NAME ) )
            .map( TurtleSyntaxTree.Node::content )
            .orElse( "" );
      return new TypedStatement( predicateIri, subject );
   }

   /**
    * The predicate IRI of the statement, or empty if there is none before the cursor, and the subject
    * of the
    * statement, or an empty string
    */
   protected record TypedStatement(
         Optional<String> predicateIri, String subject
   ) {}

   protected Optional<TurtleSyntaxTree.Node> localNameAtCursor( final ParsedDocument parsedDocument, final Position position ) {
      return Optional.ofNullable( parsedDocument.turtleSyntaxTree()
            .findMatchingTreeSitterToken( List.of( ParserTokenType.PN_LOCAL ), position.getLine(), position.getCharacter() - 1L ) );
   }

   protected boolean isPrefixedBy( final TurtleSyntaxTree.Node prefixedName, final String prefix ) {
      return prefixedName.children().stream()
            .filter( TurtleSyntaxTree.Node::isToken )
            .filter( n -> ParserTokenType.NAMESPACE.equals( n.type() ) )
            .anyMatch( n -> n.content().equals( prefix ) );
   }

   /**
    * The full IRIs of the types a triple's subject declared with ({@code a} / {@code rdf:type})
    */
   protected Stream<String> declaredTypes( final TurtleSyntaxTree.Token triple, final TurtleSyntaxTree tree ) {
      return predicateObjectMapForTriple( triple ).entrySet().stream()
            .filter( entry -> TYPE_DEFINITION_PREDICATES.contains( entry.getKey().content() ) )
            .flatMap( entry -> entry.getValue().children().stream() )
            .flatMap( object -> expandedIri( object, tree ).stream() )
            .map( AbstractTurtleCompletionService::withLatestSammNamespace );
   }

   /**
    * The subjects of the document whose type is (a subclass of) one of the expected classes
    */
   protected Stream<TurtleSyntaxTree.Node> declaredSubjectNames( final TurtleSyntaxTree tree, final Set<Resource> expectedClasses ) {
      return tree.tokens()
            .filter( node -> ParserTokenType.TRIPLE.equals( node.type() ) )
            .filter( triple -> declaredTypes( triple, tree ).anyMatch( type -> isSubclassOfAny( type, expectedClasses ) ) )
            .flatMap( triple -> triple.childWithType( ParserTokenType.SUBJECT ).stream() )
            .flatMap( subject -> subject.childWithType( ParserTokenType.PREFIXED_NAME ).stream() );
   }

   /**
    * IRI -> prefixed name using the well-known namespaces, e.g. samm:characteristic
    */
   protected static Optional<String> prefixedName( final String iri ) {
      final String latestIri = withLatestSammNamespace( iri );
      return SammNs.wellKnownNamespaces()
            .filter( namespace -> latestIri.startsWith( namespace.getNamespace() ) )
            .findFirst()
            .map( namespace -> namespace.getShortForm() + ":" + latestIri.substring( namespace.getNamespace().length() ) );
   }

   /**
    * The statement (TRIPLE token) that contains the position
    */
   private Optional<TurtleSyntaxTree.Token> statementAt( final TurtleSyntaxTree tree, final Position position ) {
      return tree.tokens()
            .filter( t -> ParserTokenType.TRIPLE.equals( t.type() ) )
            .filter( t -> inside( t.location(), position ) )
            .findFirst();
   }

   /**
    * The last predicate of the statement that ends before the cursor. Predicates of other statements
    * are not used.
    */
   private static Optional<TurtleSyntaxTree.Node> closestPredicate( final TurtleSyntaxTree.Token statement, final Position cursor ) {
      return descendants( statement )
            .filter( node -> ParserTokenType.PREDICATE.equals( node.type() ) )
            .filter( node -> isBefore( node.location(), cursor ) )
            .reduce( ( _, second ) -> second );
   }

   private static Stream<TurtleSyntaxTree.Node> descendants( final TurtleSyntaxTree.Node node ) {
      return Stream.concat( Stream.of( node ), node.children().stream().flatMap( AbstractTurtleCompletionService::descendants ) );
   }

   private static Position start( final Location location ) {
      return new Position( location.fromLine(), location.fromColumn() );
   }

   private static Position end( final Location location ) {
      return new Position( location.toLine(), location.toColumn() );
   }

   private static int compare( final Position a, final Position b ) {
      return a.getLine() != b.getLine()
            ? Integer.compare( a.getLine(), b.getLine() )
            : Integer.compare( a.getCharacter(), b.getCharacter() );
   }

   private static boolean inside( final Location location, final Position position ) {
      return compare( start( location ), position ) <= 0 && compare( position, end( location ) ) <= 0;
   }

   private static boolean isBefore( final Location location, final Position position ) {
      return compare( end( location ), position ) <= 0;
   }

   private static boolean isSubclassOfAny( final String typeIri, final Set<Resource> classes ) {
      final Resource type = META_MODEL_DEFINITIONS.createResource( typeIri );
      return classes.stream().anyMatch( expected -> RdfUtil.isTypeOrSubtypeOf( type, expected ) );
   }

   /**
    * SAMM namespace of any known meta model version -> the corresponding namespace of {@link SammNs}
    */
   private static Map<String, String> initLatestSammNamespaces() {
      final Map<String, String> latestByShortForm = SammNs.wellKnownNamespaces()
            .collect( Collectors.toMap( RdfNamespace::getShortForm, RdfNamespace::getNamespace ) );
      return KnownVersion.getVersions().stream()
            .flatMap( version -> RdfNamespace.createPrefixMap( version ).entrySet().stream() )
            .filter( entry -> latestByShortForm.containsKey( entry.getKey() ) )
            .collect( Collectors.toMap( Map.Entry::getValue, entry -> latestByShortForm.get( entry.getKey() ), ( first, _ ) -> first ) );
   }

   /**
    * Replaces SAMM namespaces of older meta model versions with the ones of {@link SammNs}, i.e. of
    * the bundled meta model
    */
   private static String withLatestSammNamespace( final String iri ) {
      final int separator = iri.lastIndexOf( '#' );
      if ( separator < 0 ) {
         return iri;
      }
      final String namespace = iri.substring( 0, separator + 1 );
      return LATEST_SAMM_NAMESPACES.getOrDefault( namespace, namespace ) + iri.substring( separator + 1 );
   }

   /**
    * Full names ({@code xsd:int}, {@code rdf:langString}, {@code samm:curie}, ...) of all data types a
    * {@code samm:dataType} may reference
    */
   private static List<String> initDataTypeNames() {
      return SammXsdType.ALL_TYPES.stream()
            .flatMap( type -> prefixedName( type.getURI() ).stream() )
            .toList();
   }

   /**
    * Predicate IRI -> classes the SAMM shapes expect for its object. Uses {@code sh:path} +
    * {@code sh:class}, and adds
    * the classes of predicates whose shapes restrict the object only via SPARQL or
    * {@code mmm:listType}.
    */
   private static Map<String, Set<Resource>> initExpectedClasses() {
      final Map<String, Set<Resource>> expectedClasses = new HashMap<>( shaclExpectedClasses() );
      // samm:dataType accepts an XSD data type or an entity
      expectedClasses.put( SammNs.SAMM.dataType().getURI(),
            Set.of( META_MODEL_DEFINITIONS.createResource( SammNs.SAMM.Entity().getURI() ),
                  META_MODEL_DEFINITIONS.createResource( SammNs.SAMM.AbstractEntity().getURI() ) ) );
      // samm:properties is a list of samm:Property
      expectedClasses.put( SammNs.SAMM.properties().getURI(),
            Set.of( META_MODEL_DEFINITIONS.createResource( SammNs.SAMM.Property().getURI() ) ) );
      return expectedClasses;
   }

   private static Map<String, Set<Resource>> shaclExpectedClasses() {
      return new ShapeLoader().apply( MetaModelFile.metaModelShapes() ).stream()
            .flatMap( shape -> shape.properties().stream() ).<Map.Entry<String, Resource>>mapMulti( ( property, consumer ) -> {
               if ( property.path() instanceof final PredicatePath path ) {
                  property.attributes().constraints().stream()
                        .filter( ClassConstraint.class::isInstance )
                        .map( ClassConstraint.class::cast )
                        .forEach( constraint -> consumer.accept( Map.entry( path.predicate().getURI(), constraint.allowedClass() ) ) );
               }
            } )
            .collect( Collectors.groupingBy( Map.Entry::getKey, Collectors.mapping( Map.Entry::getValue, Collectors.toSet() ) ) );
   }
}
