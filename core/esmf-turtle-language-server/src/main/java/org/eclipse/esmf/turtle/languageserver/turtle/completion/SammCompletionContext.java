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

package org.eclipse.esmf.turtle.languageserver.turtle.completion;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.eclipse.esmf.samm.KnownVersion;

import org.eclipse.lsp4j.Range;

/** The document facts and edit location needed to produce a completion. */
public record SammCompletionContext(
      TargetPosition targetPosition,
      Namespaces namespaces,
      CursorEdit edit,
      SubjectFacts subject,
      Optional<String> predicateSeparator,
      Optional<String> activePredicate,
      Optional<String> enclosingPredicate
) {
   public enum TargetPosition {
      DOCUMENT_HEADER,
      TOP_LEVEL_SUBJECT,
      PREDICATE,
      OBJECT_PROPERTIES_LIST,
      OBJECT_CONSTRAINT,
      OBJECT_CHARACTERISTIC,
      OBJECT_VALUE;

      public boolean isAnonymousObject() {
         return switch ( this ) {
            case OBJECT_PROPERTIES_LIST, OBJECT_CONSTRAINT, OBJECT_CHARACTERISTIC -> true;
            default -> false;
         };
      }
   }

   public record Namespaces(
         String sammNamespace,
         String sammPrefix,
         Optional<String> characteristicNamespace,
         Optional<String> characteristicPrefix,
         Optional<String> xsdPrefix,
         Optional<String> unitPrefix,
         KnownVersion version,
         Map<String, String> declarations,
         Optional<String> modelPrefix,
         Optional<String> entityPrefix
   ) {}

   public record CursorEdit(
         Range range,
         String typedText,
         String indentation,
         String lineEnding,
         boolean blankRestOfLine
   ) {}

   public record SubjectFacts(
         Set<String> types,
         Set<String> predicates,
         Map<String, Set<String>> allSubjectTypes,
         Map<String, Set<String>> extendedBySubject
   ) {
      static final SubjectFacts EMPTY = new SubjectFacts( Set.of(), Set.of(), Map.of(), Map.of() );
   }

   public static SammCompletionContext header( final Map<String, String> declarations, final CursorEdit edit ) {
      final Namespaces namespaces = new Namespaces( "", "samm:", Optional.empty(), Optional.empty(),
            Optional.empty(), Optional.empty(), KnownVersion.getLatest(), Map.copyOf( declarations ),
            Optional.empty(), Optional.empty() );

      return new SammCompletionContext( TargetPosition.DOCUMENT_HEADER, namespaces, edit,
            SubjectFacts.EMPTY, Optional.empty(), Optional.empty(), Optional.empty() );
   }

   public static SammCompletionContext topLevel( final Namespaces namespaces, final CursorEdit edit ) {
      return new SammCompletionContext( TargetPosition.TOP_LEVEL_SUBJECT, namespaces, edit,
            SubjectFacts.EMPTY, Optional.empty(), Optional.empty(), Optional.empty() );
   }

   public static SammCompletionContext anonymousObject(
         final TargetPosition position,
         final Namespaces namespaces,
         final CursorEdit edit,
         final SubjectFacts subject ) {
      return new SammCompletionContext( position, namespaces, edit, subject,
            Optional.empty(), Optional.empty(), Optional.empty() );
   }

   public static SammCompletionContext predicate(
         final Namespaces namespaces,
         final CursorEdit edit,
         final SubjectFacts subject,
         final Optional<String> separator ) {
      return new SammCompletionContext( TargetPosition.PREDICATE, namespaces, edit, subject,
            separator, Optional.empty(), Optional.empty() );
   }

   public static SammCompletionContext objectValue(
         final Namespaces namespaces,
         final CursorEdit edit,
         final SubjectFacts subject,
         final String activePredicate,
         final Optional<String> enclosingPredicate ) {
      return new SammCompletionContext( TargetPosition.OBJECT_VALUE, namespaces, edit, subject,
            Optional.empty(), Optional.of( activePredicate ), enclosingPredicate );
   }

   public boolean hasSubjectType( final String localTypeName ) {
      return subject.types().contains( namespaces.sammNamespace() + localTypeName )
            || namespaces.characteristicNamespace().map( ns -> subject.types().contains( ns + localTypeName ) ).orElse( false );
   }

   public boolean hasAnySammSubjectType() {
      return subject.types().stream().anyMatch( type -> type.startsWith( namespaces.sammNamespace() )
            || namespaces.characteristicNamespace().map( type::startsWith ).orElse( false ) );
   }

   public boolean hasSubjectPredicate( final String localPredicateName ) {
      return subject.predicates().contains( namespaces.sammNamespace() + localPredicateName )
            || namespaces.characteristicNamespace().map( ns -> subject.predicates().contains( ns + localPredicateName ) ).orElse( false );
   }

   public String resolvedSammPrefix() {
      return edit.typedText().contains( ":" ) ? edit.typedText().substring( 0, edit.typedText().indexOf( ':' ) + 1 )
            : namespaces.sammPrefix();
   }

   public static final Set<String> CHARACTERISTIC_PREDICATES = Set.of(
         "characteristic", "baseCharacteristic", "elementCharacteristic", "left", "right"
   );

   public static boolean isCharacteristicPredicate(
         final String predicateUri,
         final String sammNamespace,
         final Optional<String> characteristicNamespace ) {
      if ( predicateUri.equals( sammNamespace + "characteristic" ) ) {
         return true;
      }
      return characteristicNamespace.map( charNs -> predicateUri.startsWith( charNs )
            && CHARACTERISTIC_PREDICATES.contains( predicateUri.substring( charNs.length() ) )
      ).orElse( false );
   }

   public static String localName( final String uriOrCurie ) {
      final int sep = Math.max( uriOrCurie.lastIndexOf( '#' ),
            Math.max( uriOrCurie.lastIndexOf( '/' ), uriOrCurie.lastIndexOf( ':' ) ) );
      return sep >= 0 ? uriOrCurie.substring( sep + 1 ) : uriOrCurie;
   }

   public List<String> localEntities() {
      return matchingSubjects( subject.allSubjectTypes(), types -> types.contains( namespaces.sammNamespace() + "Entity" )
            || types.contains( namespaces.sammNamespace() + "AbstractEntity" ) );
   }

   public List<String> localAbstractEntities() {
      return matchingSubjects( subject.allSubjectTypes(), types -> types.contains( namespaces.sammNamespace() + "AbstractEntity" ) );
   }

   public List<String> localCharacteristics() {
      return matchingSubjects( subject.allSubjectTypes(), types -> types.contains( namespaces.sammNamespace() + "Characteristic" )
            || namespaces.characteristicNamespace().map( ns -> types.stream().anyMatch( t -> t.startsWith( ns ) ) ).orElse( false ) );
   }

   public List<String> localEntitiesExtending( final String parentUri ) {
      return matchingSubjects( subject.extendedBySubject(), parents -> parents.contains( parentUri ) );
   }

   private List<String> matchingSubjects(
         final Map<String, Set<String>> subjects,
         final Predicate<Set<String>> predicate ) {
      return subjects.entrySet().stream()
            .filter( entry -> !entry.getKey().startsWith( "_:" ) && predicate.test( entry.getValue() ) )
            .map( entry -> toPrefixedName( entry.getKey() ) )
            .sorted()
            .toList();
   }

   public String toPrefixedName( final String uri ) {
      for ( final Map.Entry<String, String> entry : namespaces.declarations().entrySet() ) {
         final String nsUri = entry.getValue();
         if ( uri.startsWith( nsUri ) ) {
            return entry.getKey() + uri.substring( nsUri.length() );
         }
      }
      return uri;
   }
}
