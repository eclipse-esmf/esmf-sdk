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
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.urn.ElementType;
import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.metamodel.vocabulary.SAMMC;
import org.eclipse.esmf.metamodel.vocabulary.SAMME;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.metamodel.vocabulary.UNIT;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammCompletionContext.CursorEdit;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammCompletionContext.Namespaces;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammCompletionContext.SubjectFacts;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammCompletionContext.TargetPosition;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.TurtleCompletionTree.Frame;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.TurtleCompletionTree.Lexeme;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.TurtleCompletionTree.State;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.TurtleCompletionTree.TokenKind;

import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.eclipse.lsp4j.Range;

public final class SammSuggestionContextDetector {
   private static final String XSD_NAMESPACE = XSD.getURI();
   private static final String RDF_NAMESPACE = RDF.getURI();
   private static final String SAMM_URN_PREFIX = AspectModelUrn.PROTOCOL_AND_NAMESPACE_PREFIX;
   private static final String SAMM_SYSTEM_NAMESPACE_PREFIX = SammNs.SAMM.getBaseUri();
   private static final String SAMM_META_MODEL_NAMESPACE_PREFIX = SAMM_SYSTEM_NAMESPACE_PREFIX + ElementType.META_MODEL.getValue() + ":";
   private static final String SAMM_CHARACTERISTIC_NAMESPACE_PREFIX =
         SAMM_SYSTEM_NAMESPACE_PREFIX + ElementType.CHARACTERISTIC.getValue() + ":";
   private static final String SAMM_ENTITY_NAMESPACE_PREFIX = SAMM_SYSTEM_NAMESPACE_PREFIX + ElementType.ENTITY.getValue() + ":";
   private static final String SAMM_UNIT_NAMESPACE_PREFIX = SAMM_SYSTEM_NAMESPACE_PREFIX + ElementType.UNIT.getValue() + ":";
   private static final Pattern NON_INDENTATION = Pattern.compile( "[^\\t ].*$" );
   private static final Pattern LINE_BREAK = Pattern.compile( "\\R" );
   private static final Pattern ANON_TRAILING_ALLOWED = Pattern.compile( "[\\t \\]\\).;,]*+(?:#.*)?$" );

   public Optional<SammCompletionContext> detect( final TurtleCursor analysis ) {
      if ( analysis.offset() < 0 ) {
         return Optional.empty();
      }
      final int cursor = analysis.offset();
      final List<Lexeme> tokens = analysis.tokens();
      if ( tokens.stream().anyMatch( token -> token.protectedAt( cursor ) ) ) {
         return Optional.empty();
      }
      final Map<String, String> declarations = analysis.declarations();
      final List<String> sammNamespaces = declarations.values().stream()
            .filter( ns -> ns.startsWith( SAMM_META_MODEL_NAMESPACE_PREFIX ) )
            .distinct()
            .toList();
      final Lexeme current = analysis.typed();
      final State cursorState = analysis.before();
      final CursorEdit edit = cursorEdit( analysis );

      if ( sammNamespaces.isEmpty() ) {
         return headerContext( cursorState, declarations, edit );
      }
      if ( sammNamespaces.size() != 1 || cursorState.isInDirective() ) {
         return Optional.empty();
      }
      final Optional<Namespaces> resolvedNamespaces = resolveNamespaces( declarations, sammNamespaces.getFirst() );
      if ( resolvedNamespaces.isEmpty() ) {
         return Optional.empty();
      }
      final Namespaces namespaces = resolvedNamespaces.get();
      final Frame top = cursorState.top();
      if ( top.isEmptyAnonymousBracket() ) {
         return anonymousContext( analysis, namespaces );
      }
      if ( cursorState.isTopLevelStatement() && top.isSubjectSlot() ) {
         return Optional.of( SammCompletionContext.topLevel( namespaces, edit ) );
      }
      if ( !top.isInsideSupportedDelimiter() ) {
         return Optional.empty();
      }
      if ( top.isPredicateSlot() && top.hasSubject() ) {
         final SubjectFacts subject = analysis.facts().factsFor( top.subject() );
         return Optional.of( SammCompletionContext.predicate( namespaces, edit, subject,
               determinePredicateSeparator( tokens, current, declarations ) ) );
      }
      if ( top.isObjectSlot() && !top.statementPredicate().isEmpty() ) {
         final SubjectFacts subject = analysis.facts().factsFor( top.statementSubject() );
         return Optional.of( SammCompletionContext.objectValue( namespaces, edit, subject,
               top.statementPredicate(), top.enclosingPredicateIfAnonymous() ) );
      }
      return Optional.empty();
   }

   private static Optional<SammCompletionContext> headerContext(
         final State cursorState, final Map<String, String> declarations, final CursorEdit edit ) {
      if ( !cursorState.isTopLevelStatement() || !cursorState.top().isSubjectSlot()
            || cursorState.hasTripleStatements() || !edit.blankRestOfLine()
            || !isHeaderPrefixTrigger( declarations, edit.typedText() ) ) {
         return Optional.empty();
      }
      return Optional.of( SammCompletionContext.header( declarations, edit ) );
   }

   private static Optional<SammCompletionContext> anonymousContext( final TurtleCursor analysis, final Namespaces namespaces ) {
      final String source = analysis.source();
      final int cursor = analysis.offset();
      final Lexeme current = analysis.typed();
      final State cursorState = analysis.before();
      final Optional<TargetPosition> target = detectAnonymousTargetPosition(
            cursorState, namespaces.sammNamespace(), namespaces.characteristicNamespace() );
      if ( target.isEmpty() ) {
         return Optional.empty();
      }
      final Frame top = cursorState.top();
      final int nextNewline = source.indexOf( '\n', top.openOffset() );
      if ( nextNewline >= 0 && nextNewline < cursor ) {
         return Optional.empty();
      }
      final int replaceEnd = findAnonymousReplaceEnd( source, current.end() );
      final String restOfLine = LINE_BREAK.split( source.substring( replaceEnd ), 2 )[0];
      if ( !ANON_TRAILING_ALLOWED.matcher( restOfLine ).matches() ) {
         return Optional.empty();
      }
      final CursorEdit edit = cursorEdit( analysis, top.openOffset(), replaceEnd );
      final SubjectFacts subject = analysis.facts().factsFor( top.enclosingSubject() );
      return Optional.of( SammCompletionContext.anonymousObject( target.get(), namespaces, edit, subject ) );
   }

   private static Optional<Namespaces> resolveNamespaces( final Map<String, String> namespaces, final String sammNamespace ) {
      final Optional<KnownVersion> version = KnownVersion.getVersions().stream()
            .filter( candidate -> new SAMM( candidate ).getNamespace().equals( sammNamespace ) )
            .findFirst();
      if ( version.isEmpty() ) {
         return Optional.empty();
      }
      final String sammPrefix = namespaces.entrySet().stream()
            .filter( entry -> entry.getValue().equals( sammNamespace ) )
            .map( Map.Entry::getKey )
            .findFirst()
            .orElseThrow();

      final KnownVersion ver = version.get();
      final SAMM samm = new SAMM( ver );
      final String expectedCharNs = new SAMMC( ver ).getNamespace();
      final Optional<String> characteristicPrefix = resolveVersionedPrefix(
            namespaces, SAMM_CHARACTERISTIC_NAMESPACE_PREFIX, expectedCharNs );
      final Optional<String> characteristicNamespace = characteristicPrefix.map( ignored -> expectedCharNs );

      final String expectedUnitNs = new UNIT( ver, samm ).getNamespace();
      final Optional<String> unitPrefix = resolveVersionedPrefix(
            namespaces, SAMM_UNIT_NAMESPACE_PREFIX, expectedUnitNs );

      final String expectedEntityNs = new SAMME( ver, samm ).getNamespace();
      final Optional<String> entityPrefix = resolveVersionedPrefix(
            namespaces, SAMM_ENTITY_NAMESPACE_PREFIX, expectedEntityNs );

      return Optional.of( new Namespaces( sammNamespace, sammPrefix, characteristicNamespace, characteristicPrefix,
            prefixOf( namespaces, XSD_NAMESPACE ), prefixOf( namespaces, RDF_NAMESPACE ), unitPrefix, ver, Map.copyOf( namespaces ),
            modelPrefix( namespaces ), entityPrefix ) );
   }

   private static Optional<String> prefixOf( final Map<String, String> namespaces, final String namespace ) {
      return namespaces.entrySet().stream()
            .filter( entry -> entry.getValue().equals( namespace ) )
            .map( Map.Entry::getKey )
            .findFirst();
   }

   private static boolean isHeaderPrefixTrigger( final Map<String, String> namespaces, final String typed ) {
      if ( typed.isEmpty() ) {
         return namespaces.isEmpty();
      }
      final String lower = typed.toLowerCase( Locale.ROOT );
      return "@prefix".startsWith( lower ) || "prefix".startsWith( lower );
   }

   private static Optional<String> resolveVersionedPrefix(
         final Map<String, String> namespaces,
         final String systemPrefix,
         final String expectedNamespace ) {
      final List<String> matchingNamespaces = namespaces.values().stream()
            .filter( ns -> ns.startsWith( systemPrefix ) )
            .distinct()
            .toList();
      if ( matchingNamespaces.size() != 1 || !matchingNamespaces.getFirst().equals( expectedNamespace ) ) {
         return Optional.empty();
      }
      return namespaces.entrySet().stream()
            .filter( entry -> entry.getValue().equals( expectedNamespace ) )
            .map( Map.Entry::getKey )
            .findFirst();
   }

   private static Optional<TargetPosition> detectAnonymousTargetPosition(
         final State cursorState,
         final String sammNamespace,
         final Optional<String> characteristicNamespace ) {
      final Frame top = cursorState.top();
      final Frame parent = cursorState.parent();
      if ( parent != null && parent.isList() && top.enclosingPredicate().equals( sammNamespace + SAMM.PROPERTIES ) ) {
         return Optional.of( TargetPosition.OBJECT_PROPERTIES_LIST );
      }
      if ( parent != null && !parent.isList() ) {
         if ( characteristicNamespace.isPresent()
               && top.enclosingPredicate().equals( characteristicNamespace.get() + SAMMC.CONSTRAINT ) ) {
            return Optional.of( TargetPosition.OBJECT_CONSTRAINT );
         }
         if ( SammCompletionContext.isCharacteristicPredicate( top.enclosingPredicate(), sammNamespace, characteristicNamespace ) ) {
            return Optional.of( TargetPosition.OBJECT_CHARACTERISTIC );
         }
      }
      return Optional.empty();
   }

   private static int findAnonymousReplaceEnd( final String source, final int fromOffset ) {
      int idx = fromOffset;
      while ( idx < source.length() && ( source.charAt( idx ) == ' ' || source.charAt( idx ) == '\t' ) ) {
         idx++;
      }
      if ( idx < source.length() && source.charAt( idx ) == ']' ) {
         return idx + 1;
      }
      return fromOffset;
   }

   private static Optional<String> determinePredicateSeparator(
         final List<Lexeme> tokens,
         final Lexeme current,
         final Map<String, String> namespaces ) {
      final Optional<Lexeme> next = tokens.stream()
            .filter( token -> token.start() >= current.end() && token != current && token.kind() != TokenKind.COMMENT )
            .findFirst();
      if ( next.isEmpty() ) {
         return Optional.of( " ;" );
      }
      final Lexeme nextToken = next.get();
      if ( Set.of( ".", ";", "]" ).contains( nextToken.text() ) ) {
         return Optional.of( "" );
      }
      final boolean followedByAnotherPredicate = !CompletionUtils.expand( nextToken.text(), namespaces ).isEmpty()
            && tokens.stream()
                  .filter( token -> token.start() >= nextToken.end() && token.kind() != TokenKind.COMMENT )
                  .findFirst()
                  .filter( token -> token.kind() != TokenKind.PUNCTUATION || Set.of( "[", "(" ).contains( token.text() ) )
                  .isPresent();
      return followedByAnotherPredicate
            ? Optional.of( nextToken.start() == current.end() ? " ; " : " ;" )
            : Optional.empty();
   }

   private static Optional<String> modelPrefix( final Map<String, String> namespaces ) {
      if ( namespaces.containsKey( ":" ) && !namespaces.get( ":" ).startsWith( SAMM_SYSTEM_NAMESPACE_PREFIX ) ) {
         return Optional.of( ":" );
      }
      final List<String> candidates = namespaces.entrySet().stream()
            .filter( entry -> entry.getValue().startsWith( SAMM_URN_PREFIX )
                  && !entry.getValue().startsWith( SAMM_SYSTEM_NAMESPACE_PREFIX ) )
            .map( Map.Entry::getKey )
            .toList();
      return candidates.size() == 1 ? Optional.of( candidates.getFirst() ) : Optional.empty();
   }

   private static CursorEdit cursorEdit( final TurtleCursor analysis ) {
      return cursorEdit( analysis, analysis.typed().start(), analysis.typed().end() );
   }

   /**
    * The edit that replaces the text from start to end, with the text up to the cursor as the typed
    * text
    */
   private static CursorEdit cursorEdit( final TurtleCursor analysis, final int start, final int end ) {
      final String source = analysis.source();
      final String linePrefix = source.substring( source.lastIndexOf( '\n', start - 1 ) + 1, start );
      return new CursorEdit(
            new Range( CompletionUtils.positionAt( source, start ), CompletionUtils.positionAt( source, end ) ),
            source.substring( start, analysis.offset() ),
            NON_INDENTATION.matcher( linePrefix ).replaceAll( "" ),
            source.contains( "\r\n" ) ? "\r\n" : "\n",
            LINE_BREAK.split( source.substring( end ), 2 )[0].isBlank() );
   }

}
