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
import java.util.stream.Stream;

import org.eclipse.esmf.metamodel.datatype.SammXsdType;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.CompletionUtils;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammCompletionContext;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammShapeUtils;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammSuggestionContextDetector;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.TurtleCompletionTree;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.TurtleCursor;

import org.eclipse.lsp4j.InlineCompletionItem;
import org.eclipse.lsp4j.InlineCompletionParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

public class TurtleInlineCompletionService extends TurtleService {
   /**
    * Full names ({@code xsd:int}, {@code rdf:langString}, {@code samm:curie}, ...) of all data types a
    * {@code samm:dataType} may reference
    */
   private static final List<String> DATA_TYPE_NAMES = initDataTypeNames();
   private final SammSuggestionContextDetector contextDetector = new SammSuggestionContextDetector();

   private static List<String> initDataTypeNames() {
      return SammXsdType.ALL_TYPES.stream()
            .flatMap( type -> CompletionUtils.prefixedName( type.getURI() ).stream() )
            .toList();
   }

   /**
    * Inline completion while typing a name. Only a single, unambiguous candidate is suggested.
    * <ul>
    * <li>A predicate, e.g. {@code samm:chara|}, is suggested together with its single object, e.g.
    * {@code samm:characteristic :Single}. The object is the unique subject of the expected class.</li>
    * <li>An object, e.g. {@code samm:characteristic :Ex|}. Candidates are the document subjects of the
    * expected class whose name starts with the typed text.</li>
    * </ul>
    */
   public List<InlineCompletionItem> inlineComplete( final ParsedDocument parsedDocument, final InlineCompletionParams params ) {
      final Position position = params.getPosition();
      final TurtleCursor analysis = new TurtleCompletionTree( parsedDocument ).cursorAt( position );

      // Do not suggest in the middle of an existing name, e.g. "samm:characteristic :Exam|ple"
      if ( analysis.insideName() ) {
         return List.of();
      }

      final Optional<SammCompletionContext> context = contextDetector.detect( analysis );
      if ( context.isEmpty() ) {
         return List.of();
      }
      return switch ( context.get().targetPosition() ) {
         case PREDICATE -> predicateInlineComplete( analysis, position );
         case OBJECT_VALUE -> objectInlineComplete( analysis, context.get().activePredicate().map( TurtleCursor::withLatestSammNamespace ),
               position );
         default -> List.of();
      };
   }

   private List<InlineCompletionItem> objectInlineComplete( final TurtleCursor analysis, final Optional<String> predicateIri,
         final Position position ) {
      final Optional<String> typedPrefix = analysis.prefix();
      if ( typedPrefix.isEmpty() ) {
         return List.of();
      }
      final String prefix = typedPrefix.get();
      final String typedLocalName = analysis.localName();
      final boolean isDataType = predicateIri.filter( SammNs.SAMM.dataType().getURI()::equals ).isPresent();
      // The classes the SAMM shapes expect for the predicate's object, e.g. samm:Characteristic or
      // samm:Entity
      final Stream<String> matchingSubjects = matchingExpectedSubjects( analysis, predicateIri, prefix, typedLocalName );

      final Stream<String> matchesStream;
      if ( isDataType ) {
         // The data type name is already complete, e.g. xsd:int: do not suggest a longer one like
         // xsd:integer
         if ( isCompleteDataTypeName( prefix, typedLocalName ) ) {
            return List.of();
         }
         // Data types (samm:dataType) and subjects of the document whose type is (a subclass of) an expected
         // class
         matchesStream = Stream.concat( dataTypeNames( prefix, typedLocalName ), matchingSubjects );
      } else {
         matchesStream = matchingSubjects;
      }

      final List<String> matches = matchesStream.distinct().toList();
      // Only suggest if the choice is unambiguous
      if ( matches.size() != 1 ) {
         return List.of();
      }
      final InlineCompletionItem item = new InlineCompletionItem( Either.forLeft( matches.getFirst() ) );
      item.setRange( new Range( analysis.typedStart(), position ) );
      return List.of( item );
   }

   /**
    * The prefixed names of the subjects of the document whose type is (a subclass of) one of the
    * expected
    * classes, that continue the typed name. The subject of the statement being edited is excluded, so
    * it is
    * not suggested as its own object.
    */
   private Stream<String> matchingExpectedSubjects( final TurtleCursor analysis, final Optional<String> predicateIri,
         final String prefix, final String typedLocalName ) {
      final Set<String> subjects = SammShapeUtils.expectedSubjects( analysis.subjectTypes(), predicateIri ).orElse( Set.of() );
      return analysis.otherLocalNamesOf( prefix ).stream()
            .filter( name -> name.length() >= typedLocalName.length() && name.startsWith( typedLocalName ) )
            .filter( name -> subjects.contains( analysis.iri( prefix + name ) ) )
            .filter( name -> !analysis.isStatementSubject( analysis.iri( prefix + name ) ) )
            .map( name -> prefix + name );
   }

   /**
    * The single subject of the document a predicate completes to, for predicates that
    * {@link #takesSingleObject}. Returned as a prefixed name.
    */
   protected Optional<String> singleObject( final TurtleCursor analysis, final String predicateIri, final String excludedSubjectIri ) {
      if ( !takesSingleObject( predicateIri ) ) {
         return Optional.empty();
      }
      final List<String> objects =
            SammShapeUtils.expectedSubjects( analysis.subjectTypes(), Optional.of( predicateIri ) ).orElse( Set.of() ).stream()
                  .filter( subject -> !subject.equals( excludedSubjectIri ) )
                  .map( analysis::toPrefixedName )
                  .distinct().toList();
      return objects.size() == 1 ? Optional.of( objects.getFirst() ) : Optional.empty();
   }

   private List<InlineCompletionItem> predicateInlineComplete( final TurtleCursor analysis, final Position position ) {
      final Optional<String> typedPrefix = analysis.prefix();
      if ( typedPrefix.isEmpty() ) {
         return List.of();
      }
      final Optional<String> predicateIri = singlePredicateIri( typedPrefix.get(), analysis.localName() );
      final Optional<String> predicateName = predicateIri.flatMap( CompletionUtils::prefixedName );
      if ( predicateName.isEmpty() ) {
         return List.of();
      }
      final Optional<String> object = predicateIri.flatMap( iri -> singleObject( analysis, iri, analysis.subjectIri() ) );
      // A predicate is only suggested together with its single object, so "samm:data" alone is not
      // completed
      if ( object.isEmpty() ) {
         return List.of();
      }
      final InlineCompletionItem item = new InlineCompletionItem( Either.forLeft( predicateName.get() + " " + object.get() ) );
      item.setRange( new Range( analysis.typedStart(), position ) );
      return List.of( item );
   }

   /**
    * The single predicate IRI (of all SAMM shape predicates) that the typed predicate name continues
    * to
    */
   protected static Optional<String> singlePredicateIri( final String prefix, final String typedLocalName ) {
      final String typedName = prefix + typedLocalName;
      final List<String> matches = SammShapeUtils.PREDICATES.stream()
            .filter( iri -> CompletionUtils.prefixedName( iri )
                  .filter( name -> name.length() >= typedName.length() && name.startsWith( typedName ) ).isPresent() )
            .toList();
      return matches.size() == 1 ? Optional.of( matches.getFirst() ) : Optional.empty();
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
      return !SammShapeUtils.EXPECTED_CLASSES.getOrDefault( predicateIri, Set.of() ).isEmpty()
            && !predicateIri.equals( SammNs.SAMM.properties().getURI() )
            && !predicateIri.equals( SammNs.SAMM.dataType().getURI() );
   }
}
