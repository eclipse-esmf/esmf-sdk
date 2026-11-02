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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.Location;
import org.eclipse.esmf.aspectmodel.resolver.modelfile.MetaModelFile;
import org.eclipse.esmf.aspectmodel.shacl.ShapeLoader;
import org.eclipse.esmf.aspectmodel.shacl.path.PredicatePath;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

import org.apache.jena.rdf.model.Resource;
import org.eclipse.lsp4j.InlineCompletionItem;
import org.eclipse.lsp4j.InlineCompletionParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

public class TurtleInlineCompletionService extends AbstractTurtleCompletionService {
   private static final Set<String> PREDICATE_IRIS = initPredicateIris();

   /**
    * Inline completion while typing a name. Only a single, unambiguous candidate is suggested.
    * <ul>
    * <li>A predicate, e.g. {@code samm:chara|}, is suggested together with its single object, e.g.
    * {@code samm:characteristic :Single}. The object is the unique subject of the expected class.</li>
    * <li>An object, e.g. {@code samm:characteristic :Ex|}. Candidates are the document subjects of the
    * expected
    * class whose name starts with the typed text.</li>
    * </ul>
    */
   public List<InlineCompletionItem> inlineComplete( final ParsedDocument parsedDocument, final InlineCompletionParams params ) {
      final Position position = params.getPosition();
      final TurtleSyntaxTree tree = parsedDocument.turtleSyntaxTree();

      // Do not suggest in the middle of an existing name, e.g. "samm:characteristic :Exam|ple"
      if ( tree.findMatchingTreeSitterToken( List.of( ParserTokenType.NAMESPACE, ParserTokenType.PN_LOCAL ),
            position.getLine(), position.getCharacter() ) != null ) {
         return List.of();
      }

      // Predicate position, e.g. "samm:chara|": a single matching predicate, followed by its single
      // object
      final TurtleSyntaxTree.Node typedPredicate = tree.findMatchingTreeSitterToken( List.of( ParserTokenType.PREDICATE ),
            position.getLine(), position.getCharacter() - 1L );
      if ( typedPredicate != null ) {
         return predicateInlineComplete( parsedDocument, position, typedPredicate );
      }

      // The namespace of the name being typed, e.g. ":" of ":Ex"
      final Optional<TurtleSyntaxTree.Node> typedNamespaceOptional = namespaceAtCursor( parsedDocument, position );
      if ( typedNamespaceOptional.isEmpty() ) {
         return List.of();
      }

      final TurtleSyntaxTree.Node typedNamespace = typedNamespaceOptional.get();
      final String prefix = typedNamespace.content();
      final String typedLocalName = typedLocalName( parsedDocument, position );
      final TypedStatement statement = typedStatement( tree, position );
      final boolean isDataType = statement.predicateIri().filter( SammNs.SAMM.dataType().getURI()::equals ).isPresent();
      // The classes the SAMM shapes expect for the predicate's object, e.g. samm:Characteristic or
      // samm:Entity
      final Set<Resource> expectedClasses = closestPredicateExpectedClasses( statement.predicateIri() );
      final Stream<String> matchingTypes = matchingExpectedTypes( tree, prefix, typedLocalName, expectedClasses )
            // The subject of the statement being edited must not be suggested as its own object
            .filter( type -> !type.equals( statement.subject() ) );

      final Stream<String> matchesStream;
      if ( isDataType ) {
         // The data type name is already complete, e.g. xsd:int: do not suggest a longer one like
         // xsd:integer
         if ( isCompleteDataTypeName( prefix, typedLocalName ) ) {
            return List.of();
         }
         // Data types (samm:dataType) and subjects of the document whose type is (a subclass of) an expected
         // class
         matchesStream = Stream.concat( dataTypeNames( prefix, typedLocalName ), matchingTypes );
      } else {
         matchesStream = matchingTypes;
      }

      final List<String> matches = matchesStream.distinct().toList();
      // Only suggest if the choice is unambiguous
      if ( matches.size() != 1 ) {
         return List.of();
      }
      final InlineCompletionItem item = new InlineCompletionItem( Either.forLeft( matches.getFirst() ) );
      final Location location = typedNamespace.location();
      final Position start = new Position( location.fromLine(), location.fromColumn() );
      item.setRange( new Range( start, position ) );
      return List.of( item );
   }

   /**
    * The subjects of the document whose type is (a subclass of) one of the expected classes that
    * continue the typed name
    */
   private Stream<String> matchingExpectedTypes( final TurtleSyntaxTree tree, final String prefix, final String typedLocalName,
         final Set<Resource> expectedClasses ) {
      return declaredSubjectNames( tree, expectedClasses )
            .filter( subject -> continuesTypedName( subject, prefix, typedLocalName ) )
            .map( TurtleSyntaxTree.Node::content );
   }

   /**
    * The single subject of the document a predicate completes to, for predicates that
    * {@link #takesSingleObject}
    */
   protected Optional<String> singleObject( final TurtleSyntaxTree tree, final String predicateIri, final String excludedSubject ) {
      if ( !takesSingleObject( predicateIri ) ) {
         return Optional.empty();
      }
      final Set<Resource> expectedClasses = EXPECTED_CLASSES.get( predicateIri );
      final List<String> objects = declaredSubjectNames( tree, expectedClasses ).map( TurtleSyntaxTree.Node::content )
            .filter( subject -> !subject.equals( excludedSubject ) )
            .distinct().toList();
      return objects.size() == 1 ? Optional.of( objects.getFirst() ) : Optional.empty();
   }

   /**
    * Checks whether the candidate uses the given prefix and its local name starts with the typed local
    * name
    */
   private boolean continuesTypedName( final TurtleSyntaxTree.Node candidate, final String prefix, final String typedLocalName ) {
      if ( !isPrefixedBy( candidate, prefix ) ) {
         return false;
      }
      final String candidateLocalName = localName( candidate );
      return candidateLocalName.length() >= typedLocalName.length() && candidateLocalName.startsWith( typedLocalName );
   }

   private List<InlineCompletionItem> predicateInlineComplete( final ParsedDocument parsedDocument, final Position position,
         final TurtleSyntaxTree.Node typedPredicate ) {
      final Optional<TurtleSyntaxTree.Node> typedNamespace = namespaceAtCursor( parsedDocument, position );
      if ( typedNamespace.isEmpty() ) {
         return List.of();
      }
      final String typedLocalName = typedLocalName( parsedDocument, position );
      final Optional<String> predicateIri = singlePredicateIri( typedNamespace.get().content(), typedLocalName );
      final Optional<String> predicateName = predicateIri.flatMap( AbstractTurtleCompletionService::prefixedName );
      if ( predicateName.isEmpty() ) {
         return List.of();
      }
      final String statementSubject = typedStatement( parsedDocument.turtleSyntaxTree(), position ).subject();
      final Optional<String> object = predicateIri.flatMap(
            iri -> singleObject( parsedDocument.turtleSyntaxTree(), iri, statementSubject ) );
      // A predicate is only suggested together with its single object, so "samm:data" alone is not
      // completed
      if ( object.isEmpty() ) {
         return List.of();
      }
      final InlineCompletionItem item = new InlineCompletionItem( Either.forLeft( predicateName.get() + " " + object.get() ) );
      final Location location = typedPredicate.location();
      item.setRange( new Range( new Position( location.fromLine(), location.fromColumn() ), position ) );
      return List.of( item );
   }

   protected String typedLocalName( final ParsedDocument parsedDocument, final Position position ) {
      final TurtleSyntaxTree.Node localName = localNameAtCursor( parsedDocument, position ).orElse( null );
      if ( localName == null ) {
         return "";
      }
      final String content = localName.content();
      // Take only the text before the cursor, keeping the end index within the node's content.
      return content.substring( 0, Math.clamp( position.getCharacter() - localName.location().fromColumn(), 0, content.length() ) );
   }

   /**
    * The single predicate IRI (of all SAMM shape predicates) that the typed predicate name continues
    * to
    */
   protected static Optional<String> singlePredicateIri( final String prefix, final String typedLocalName ) {
      final String typedName = prefix + typedLocalName;
      final List<String> matches = PREDICATE_IRIS.stream()
            .filter( iri -> prefixedName( iri )
                  .filter( name -> name.length() >= typedName.length() && name.startsWith( typedName ) ).isPresent() )
            .toList();
      return matches.size() == 1 ? Optional.of( matches.getFirst() ) : Optional.empty();
   }

   /**
    * The classes the SAMM shapes expect for the object of the predicate, if any
    */
   private static Set<Resource> closestPredicateExpectedClasses( final Optional<String> closestPredicateIri ) {
      return closestPredicateIri.map( iri -> EXPECTED_CLASSES.getOrDefault( iri, Set.of() ) )
            .orElse( Set.of() );
   }

   /**
    * The complete data type names (e.g. {@code xsd:string}) that the typed text may continue with. A
    * name that is already complete is not returned, so that {@code xsd:int} does not suggest
    * {@code xsd:integer}.
    */
   private static Stream<String> dataTypeNames( final String prefix, final String typedLocalName ) {
      final String typedName = prefix + typedLocalName;
      return DATA_TYPE_NAMES.stream().filter( name -> name.length() > typedName.length() && name.startsWith( typedName ) );
   }

   private static boolean isCompleteDataTypeName( final String prefix, final String typedLocalName ) {
      return DATA_TYPE_NAMES.contains( prefix + typedLocalName );
   }

   /**
    * Whether a predicate completes to a single object: it has an expected class, and is neither
    * {@code samm:dataType} (39 data types) nor {@code samm:properties} (a list)
    */
   private static boolean takesSingleObject( final String predicateIri ) {
      return !EXPECTED_CLASSES.getOrDefault( predicateIri, Set.of() ).isEmpty()
            && !predicateIri.equals( SammNs.SAMM.properties().getURI() )
            && !predicateIri.equals( SammNs.SAMM.dataType().getURI() );
   }

   private static String localName( final TurtleSyntaxTree.Node prefixedName ) {
      return prefixedName.childWithType( ParserTokenType.PN_LOCAL ).map( TurtleSyntaxTree.Node::content ).orElse( "" );
   }

   /**
    * All predicates of the SAMM shapes
    */
   private static Set<String> initPredicateIris() {
      return new ShapeLoader().apply( MetaModelFile.metaModelShapes() ).stream()
            .flatMap( shape -> shape.properties().stream() )
            .filter( property -> property.path() instanceof PredicatePath )
            .map( property -> ( (PredicatePath) property.path() ).predicate().getURI() )
            .collect( Collectors.toSet() );
   }
}
