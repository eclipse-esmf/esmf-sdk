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

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.TurtleCompletionTree.Directive;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.TurtleCompletionTree.Lexeme;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.TurtleCompletionTree.State;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.TurtleCompletionTree.TokenKind;

import org.eclipse.lsp4j.Position;

/**
 * The name typed at the cursor, the statement it is in, and the subjects declared in the document.
 * Read from
 * the completion projection, so statements with syntax errors still count.
 */
public final class TurtleCursor {
   private static final Map<String, String> LATEST_SAMM_NAMESPACES = initLatestSammNamespaces();
   // Subjects are typed as SUBJECT, objects as PREFIXED_NAME, predicates as PREDICATE
   private static final Set<String> NAME_TYPES = Set.of( ParserTokenType.PREFIXED_NAME, ParserTokenType.SUBJECT,
         ParserTokenType.PREDICATE );

   private final Position position;
   private final int cursor;
   private final String source;
   private final List<Lexeme> tokens;
   private final Map<String, String> declarations;
   private final Lexeme typed;
   private final Position typedStart;
   private State before;
   private State facts;

   TurtleCursor( final Position position, final int cursor, final String source, final List<Lexeme> tokens,
         final List<Directive> directives ) {
      this.position = position;
      this.cursor = cursor;
      this.source = source;
      this.tokens = tokens;
      this.declarations = cursor < 0 ? Map.of() : declaredNamespacesBeforeCursor( directives );
      this.typed = cursor < 0 ? new Lexeme( "", 0, 0, TokenKind.TERM ) : typedAt( cursor );
      this.typedStart = cursor < 0 ? position : CompletionUtils.positionAt( source, typed.start() );
   }

   private Lexeme typedAt( final int offset ) {
      return tokens.stream()
            .filter( token -> ( token.kind() == TokenKind.TERM || token.kind() == TokenKind.STRING )
                  && token.start() < offset && offset <= token.end() )
            .findFirst()
            .orElseGet( () -> new Lexeme( "", offset, offset, TokenKind.TERM ) );
   }

   /**
    * Prefixes declared before the cursor. A prefix declared twice with different namespaces is
    * dropped.
    */
   private Map<String, String> declaredNamespacesBeforeCursor( final List<Directive> directives ) {
      final int byteColumn = source.substring( source.lastIndexOf( '\n', cursor - 1 ) + 1, cursor )
            .getBytes( StandardCharsets.UTF_8 ).length;
      final Map<String, String> result = new LinkedHashMap<>();
      final Set<String> conflicting = new HashSet<>();
      for ( final Directive directive : directives ) {
         if ( directive.line() < position.getLine()
               || directive.line() == position.getLine() && directive.byteColumn() <= byteColumn ) {
            final String previous = result.put( directive.prefix(), directive.namespace() );
            if ( previous != null && !previous.equals( directive.namespace() ) ) {
               conflicting.add( directive.prefix() );
            }
         }
      }
      conflicting.forEach( result::remove );
      return result;
   }

   String source() {
      return source;
   }

   int offset() {
      return cursor;
   }

   List<Lexeme> tokens() {
      return tokens;
   }

   Map<String, String> declarations() {
      return declarations;
   }

   Lexeme typed() {
      return typed;
   }

   /**
    * The statement at the cursor, read up to the cursor only
    */
   State before() {
      if ( before == null ) {
         before = TurtleCompletionTree.scanBefore( tokens, typed, declarations );
      }
      return before;
   }

   /**
    * The subjects of the whole document, including the statement at the cursor only in its other parts
    */
   State facts() {
      if ( facts == null ) {
         facts = TurtleCompletionTree.collectDocumentFacts( tokens, typed, declarations );
      }
      return facts;
   }

   /**
    * Whether the IRI is the subject of the statement at the cursor
    */
   public boolean isStatementSubject( final String iri ) {
      return !subjectIri().isEmpty() && subjectIri().equals( iri );
   }

   /**
    * The prefix typed before the cursor including the colon, e.g. {@code ex:} or {@code :}
    */
   public Optional<String> prefix() {
      if ( typed.kind() != TokenKind.TERM ) {
         return Optional.empty();
      }
      final int colon = typed.text().indexOf( ':' );
      if ( colon < 0 ) {
         return Optional.empty();
      }
      return Optional.of( typed.text().substring( 0, colon + 1 ) );
   }

   /**
    * The local name typed before the cursor, e.g. {@code Sp} for {@code :Sp|eed}
    */
   public String localName() {
      return prefix().map( prefix -> {
         final int end = Math.max( prefix.length(), cursor - typed.start() );
         return typed.text().substring( prefix.length(), end );
      } ).orElse( "" );
   }

   /**
    * Whether the cursor is inside a name, not at its end
    */
   public boolean insideName() {
      return typed.start() < cursor && cursor < typed.end();
   }

   public Position typedStart() {
      return typedStart;
   }

   /**
    * The IRI of the last predicate of the statement before the cursor
    */
   public Optional<String> predicateIri() {
      return Optional.of( before().top().statementPredicate() )
            .filter( predicate -> !predicate.isEmpty() )
            .map( TurtleCursor::withLatestSammNamespace );
   }

   /**
    * The IRI of the subject of the statement at the cursor, or an empty string
    */
   public String subjectIri() {
      return before().top().subject();
   }

   /**
    * Subject IRI -> types, including subjects of statements with syntax errors
    */
   public Map<String, Set<String>> subjectTypes() {
      return facts().typesBySubject().entrySet().stream()
            .collect( Collectors.toUnmodifiableMap( Map.Entry::getKey,
                  entry -> entry.getValue().stream().map( TurtleCursor::withLatestSammNamespace )
                        .collect( Collectors.toUnmodifiableSet() ) ) );
   }

   /**
    * The local names of all prefixed names with the given prefix in the document, including the typed
    * one
    */
   public List<String> localNamesOf( final String prefix ) {
      return localNames( prefix, false );
   }

   /**
    * The local names of all prefixed names with the given prefix in the document, except the typed one
    */
   public List<String> otherLocalNamesOf( final String prefix ) {
      return localNames( prefix, true );
   }

   private List<String> localNames( final String prefix, final boolean excludeTyped ) {
      return tokens.stream()
            .filter( token -> !( excludeTyped && token.equals( typed ) ) && NAME_TYPES.contains( token.type() ) )
            .map( Lexeme::text )
            .filter( name -> name.startsWith( prefix ) && name.length() > prefix.length() )
            .map( name -> name.substring( prefix.length() ) )
            .distinct()
            .toList();
   }

   /**
    * The IRI of a prefixed name, e.g. {@code :Speed}, or an empty string if its prefix is not declared
    */
   public String iri( final String prefixedName ) {
      return CompletionUtils.expand( prefixedName, facts().namespaces() );
   }

   /**
    * The prefixed name of an IRI, using the declared prefix with the longest matching namespace
    */
   public String toPrefixedName( final String iri ) {
      return CompletionUtils.toPrefixedName( iri, facts().namespaces(), unmatched -> "<" + unmatched + ">" );
   }

   /**
    * SAMM namespace of any known meta model version -> the corresponding namespace of {@link SammNs}
    */
   public static String withLatestSammNamespace( final String iri ) {
      final int separator = iri.lastIndexOf( '#' );
      if ( separator < 0 ) {
         return iri;
      }
      final String namespace = iri.substring( 0, separator + 1 );
      return LATEST_SAMM_NAMESPACES.getOrDefault( namespace, namespace ) + iri.substring( separator + 1 );
   }

   private static Map<String, String> initLatestSammNamespaces() {
      final Map<String, String> latestByShortForm = SammNs.wellKnownNamespaces()
            .collect( Collectors.toMap( RdfNamespace::getShortForm, RdfNamespace::getNamespace ) );
      return KnownVersion.getVersions().stream()
            .flatMap( version -> RdfNamespace.createPrefixMap( version ).entrySet().stream() )
            .filter( entry -> latestByShortForm.containsKey( entry.getKey() ) )
            .collect( Collectors.toMap( Map.Entry::getValue, entry -> latestByShortForm.get( entry.getKey() ), ( first, _ ) -> first ) );
   }
}
