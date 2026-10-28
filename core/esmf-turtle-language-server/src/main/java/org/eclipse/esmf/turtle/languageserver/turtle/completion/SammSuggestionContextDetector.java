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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

import org.apache.jena.vocabulary.RDF;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public final class SammSuggestionContextDetector {
   private static final String RDF_TYPE = RDF.type.getURI();
   private static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema#";
   private static final String SAMM_URN_PREFIX = "urn:samm:";
   private static final String SAMM_SYSTEM_NAMESPACE_PREFIX = "urn:samm:org.eclipse.esmf.samm:";
   private static final String SAMM_META_MODEL_NAMESPACE_PREFIX = SAMM_SYSTEM_NAMESPACE_PREFIX + "meta-model:";
   private static final String SAMM_CHARACTERISTIC_NAMESPACE_PREFIX = SAMM_SYSTEM_NAMESPACE_PREFIX + "characteristic:";
   private static final String SAMM_ENTITY_NAMESPACE_PREFIX = SAMM_SYSTEM_NAMESPACE_PREFIX + "entity:";
   private static final String SAMM_UNIT_NAMESPACE_PREFIX = SAMM_SYSTEM_NAMESPACE_PREFIX + "unit:";
   private static final Pattern NON_INDENTATION = Pattern.compile( "[^\\t ].*$" );
   private static final Pattern LINE_BREAK = Pattern.compile( "\\R" );
   private static final Pattern NUMERIC_LITERAL =
         Pattern.compile( "[+-]?(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)(?:[eE][+-]?[0-9]+)?" );
   private static final Pattern LANG_TAG = Pattern.compile( "@[a-zA-Z]+(?:-[a-zA-Z0-9]+)*" );
   private static final Pattern ANON_TRAILING_ALLOWED = Pattern.compile( "[\\t \\]\\).;,]*+(?:#.*)?$" );

   public enum TargetPosition {
      DOCUMENT_HEADER,
      TOP_LEVEL_SUBJECT,
      PREDICATE,
      OBJECT_PROPERTIES_LIST,
      OBJECT_CONSTRAINT,
      OBJECT_CHARACTERISTIC,
      OBJECT_VALUE
   }

   public record CursorContext(
         TargetPosition targetPosition,
         String sammNamespace,
         String sammPrefix,
         Optional<String> characteristicNamespace,
         Optional<String> characteristicPrefix,
         Optional<String> xsdPrefix,
         Optional<String> unitPrefix,
         KnownVersion version,
         Map<String, String> namespaces,
         Optional<String> modelPrefix,
         Range range,
         String typedText,
         String indentation,
         String lineEnding,
         boolean blankRestOfLine,
         Set<String> subjectTypes,
         Set<String> subjectPredicates,
         Optional<String> predicateSeparator,
         Optional<String> activePredicate,
         Optional<String> entityPrefix,
         Optional<String> enclosingPredicate,
         Map<String, Set<String>> allSubjectTypes,
         Map<String, Set<String>> extendedBySubject
   ) {
      public CursorContext(
            final TargetPosition targetPosition,
            final String sammNamespace,
            final String sammPrefix,
            final Optional<String> characteristicNamespace,
            final Optional<String> characteristicPrefix,
            final Optional<String> xsdPrefix,
            final Optional<String> unitPrefix,
            final KnownVersion version,
            final Map<String, String> namespaces,
            final Optional<String> modelPrefix,
            final Range range,
            final String typedText,
            final String indentation,
            final String lineEnding,
            final boolean blankRestOfLine,
            final Set<String> subjectTypes,
            final Set<String> subjectPredicates,
            final Optional<String> predicateSeparator ) {
         this( targetPosition, sammNamespace, sammPrefix, characteristicNamespace, characteristicPrefix,
               xsdPrefix, unitPrefix, version, namespaces, modelPrefix, range, typedText, indentation,
               lineEnding, blankRestOfLine, subjectTypes, subjectPredicates, predicateSeparator,
               Optional.empty(), Optional.empty(), Optional.empty(), Map.of(), Map.of() );
      }

      public CursorContext(
            final TargetPosition targetPosition,
            final String sammNamespace,
            final String sammPrefix,
            final Optional<String> characteristicNamespace,
            final Optional<String> characteristicPrefix,
            final Optional<String> xsdPrefix,
            final Optional<String> unitPrefix,
            final KnownVersion version,
            final Map<String, String> namespaces,
            final Optional<String> modelPrefix,
            final Range range,
            final String typedText,
            final String indentation,
            final String lineEnding,
            final boolean blankRestOfLine,
            final Set<String> subjectTypes,
            final Set<String> subjectPredicates,
            final Optional<String> predicateSeparator,
            final Optional<String> activePredicate ) {
         this( targetPosition, sammNamespace, sammPrefix, characteristicNamespace, characteristicPrefix,
               xsdPrefix, unitPrefix, version, namespaces, modelPrefix, range, typedText, indentation,
               lineEnding, blankRestOfLine, subjectTypes, subjectPredicates, predicateSeparator,
               activePredicate, Optional.empty(), Optional.empty(), Map.of(), Map.of() );
      }

      public boolean hasSubjectType( final String localTypeName ) {
         return subjectTypes.contains( sammNamespace + localTypeName )
               || characteristicNamespace.map( ns -> subjectTypes.contains( ns + localTypeName ) ).orElse( false );
      }

      public boolean hasAnySammSubjectType() {
         return subjectTypes.stream().anyMatch( type -> type.startsWith( sammNamespace )
               || characteristicNamespace.map( type::startsWith ).orElse( false ) );
      }

      public boolean hasSubjectPredicate( final String localPredicateName ) {
         return subjectPredicates.contains( sammNamespace + localPredicateName )
               || characteristicNamespace.map( ns -> subjectPredicates.contains( ns + localPredicateName ) ).orElse( false );
      }

      public String resolvedSammPrefix() {
         return typedText.contains( ":" ) ? typedText.substring( 0, typedText.indexOf( ':' ) + 1 ) : sammPrefix;
      }

      public List<String> localEntities() {
         final List<String> result = new ArrayList<>();
         for ( final Map.Entry<String, Set<String>> entry : allSubjectTypes.entrySet() ) {
            final String subjectUri = entry.getKey();
            if ( subjectUri.startsWith( "_:" ) ) {
               continue;
            }
            final boolean isEntity = entry.getValue().stream().anyMatch( type ->
                  type.equals( sammNamespace + "Entity" ) || type.equals( sammNamespace + "AbstractEntity" ) );
            if ( isEntity ) {
               result.add( toPrefixedName( subjectUri ) );
            }
         }
         return result.stream().sorted().toList();
      }

      public List<String> localAbstractEntities() {
         final List<String> result = new ArrayList<>();
         for ( final Map.Entry<String, Set<String>> entry : allSubjectTypes.entrySet() ) {
            final String subjectUri = entry.getKey();
            if ( subjectUri.startsWith( "_:" ) ) {
               continue;
            }
            final boolean isAbstractEntity = entry.getValue().stream().anyMatch( type ->
                  type.equals( sammNamespace + "AbstractEntity" ) );
            if ( isAbstractEntity ) {
               result.add( toPrefixedName( subjectUri ) );
            }
         }
         return result.stream().sorted().toList();
      }

      public List<String> localCharacteristics() {
         final List<String> result = new ArrayList<>();
         for ( final Map.Entry<String, Set<String>> entry : allSubjectTypes.entrySet() ) {
            final String subjectUri = entry.getKey();
            if ( subjectUri.startsWith( "_:" ) ) {
               continue;
            }
            final boolean isCharacteristic = entry.getValue().stream().anyMatch( type ->
                  type.equals( sammNamespace + "Characteristic" )
                  || characteristicNamespace.map( type::startsWith ).orElse( false ) );
            if ( isCharacteristic ) {
               result.add( toPrefixedName( subjectUri ) );
            }
         }
         return result.stream().sorted().toList();
      }

      public List<String> localEntitiesExtending( final String parentUri ) {
         final List<String> result = new ArrayList<>();
         for ( final Map.Entry<String, Set<String>> entry : extendedBySubject.entrySet() ) {
            final String subjectUri = entry.getKey();
            if ( subjectUri.startsWith( "_:" ) ) {
               continue;
            }
            if ( entry.getValue().contains( parentUri ) ) {
               result.add( toPrefixedName( subjectUri ) );
            }
         }
         return result.stream().sorted().toList();
      }

      public String toPrefixedName( final String uri ) {
         for ( final Map.Entry<String, String> entry : namespaces.entrySet() ) {
            final String nsUri = entry.getValue();
            if ( uri.startsWith( nsUri ) ) {
               return entry.getKey() + uri.substring( nsUri.length() );
            }
         }
         return uri;
      }
   }

   public Optional<CursorContext> detect( final ParsedDocument document, final Position position ) {
      final String source = document.sourceDocument().content();
      final int cursor = offset( source, position );
      if ( cursor < 0 ) {
         return Optional.empty();
      }
      final List<Lexeme> tokens = tokenize( source );
      if ( tokens.stream().anyMatch( token -> token.protectedAt( cursor ) ) ) {
         return Optional.empty();
      }
      final Map<String, String> namespaces = declaredNamespacesBeforeCursor( document, position, source, cursor );
      final List<String> sammNamespaces = namespaces.values().stream()
            .filter( ns -> ns.startsWith( SAMM_META_MODEL_NAMESPACE_PREFIX ) )
            .distinct()
            .toList();

      final Lexeme current = tokens.stream()
            .filter( token -> ( token.kind == TokenKind.TERM || token.kind == TokenKind.STRING )
                  && token.start < cursor && cursor <= token.end )
            .findFirst()
            .orElseGet( () -> new Lexeme( "", cursor, cursor, TokenKind.TERM ) );
      final State cursorState = new State( namespaces, false );
      for ( final Lexeme token : tokens ) {
         if ( token.start >= current.start ) {
            break;
         }
         cursorState.accept( token );
      }

      final String typed = source.substring( current.start, cursor );
      final Range range = new Range( positionAt( source, current.start ), positionAt( source, current.end ) );
      final String linePrefix = source.substring( source.lastIndexOf( '\n', current.start - 1 ) + 1, current.start );
      final String indentation = NON_INDENTATION.matcher( linePrefix ).replaceAll( "" );
      final String lineEnding = source.contains( "\r\n" ) ? "\r\n" : "\n";
      final boolean blankRestOfLine = LINE_BREAK.split( source.substring( current.end ), 2 )[0].isBlank();

      if ( sammNamespaces.isEmpty() ) {
         if ( cursorState.isTopLevelStatement()
               && cursorState.top().stage == Stage.SUBJECT
               && !cursorState.hasTripleStatements
               && blankRestOfLine
               && isHeaderPrefixTrigger( namespaces, typed ) ) {
            return Optional.of( new CursorContext(
                  TargetPosition.DOCUMENT_HEADER,
                  "",
                  "samm:",
                  Optional.empty(),
                  Optional.empty(),
                  Optional.empty(),
                  Optional.empty(),
                  KnownVersion.getLatest(),
                  Map.copyOf( namespaces ),
                  Optional.empty(),
                  range,
                  typed,
                  indentation,
                  lineEnding,
                  true,
                  Set.of(),
                  Set.of(),
                  Optional.empty() ) );
         }
         return Optional.empty();
      }

      if ( sammNamespaces.size() != 1 ) {
         return Optional.empty();
      }
      final String sammNamespace = sammNamespaces.getFirst();
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

      final String expectedCharNs = SAMM_CHARACTERISTIC_NAMESPACE_PREFIX + version.get().toVersionString() + "#";
      final Optional<String> characteristicPrefix = resolveVersionedPrefix(
            namespaces, SAMM_CHARACTERISTIC_NAMESPACE_PREFIX, expectedCharNs );
      final Optional<String> characteristicNamespace = characteristicPrefix.map( ignored -> expectedCharNs );

      final String expectedUnitNs = SAMM_UNIT_NAMESPACE_PREFIX + version.get().toVersionString() + "#";
      final Optional<String> unitPrefix = resolveVersionedPrefix(
            namespaces, SAMM_UNIT_NAMESPACE_PREFIX, expectedUnitNs );

      final String expectedEntityNs = SAMM_ENTITY_NAMESPACE_PREFIX + version.get().toVersionString() + "#";
      final Optional<String> entityPrefix = resolveVersionedPrefix(
            namespaces, SAMM_ENTITY_NAMESPACE_PREFIX, expectedEntityNs );

      final Optional<String> xsdPrefix = namespaces.entrySet().stream()
            .filter( entry -> entry.getValue().equals( XSD_NAMESPACE ) )
            .map( Map.Entry::getKey )
            .findFirst();

      if ( cursorState.directiveMode != DirectiveMode.NONE ) {
         return Optional.empty();
      }

      final Frame top = cursorState.top();
      if ( top.delimiter.equals( "[" ) && !top.hasContent && top.stage != Stage.INVALID ) {
         final Optional<TargetPosition> anonPosition = detectAnonymousTargetPosition(
               cursorState, sammNamespace, characteristicNamespace );
         if ( anonPosition.isPresent() ) {
            final int openOffset = top.openOffset;
            final int nextNewline = source.indexOf( '\n', openOffset );
            if ( nextNewline < 0 || nextNewline >= cursor ) {
               final int replaceEnd = findAnonymousReplaceEnd( source, current.end );
               final String restOfLine = LINE_BREAK.split( source.substring( replaceEnd ), 2 )[0];
               if ( ANON_TRAILING_ALLOWED.matcher( restOfLine ).matches() ) {
                  final String anonLinePrefix = source.substring( source.lastIndexOf( '\n', openOffset - 1 ) + 1, openOffset );
                  final String anonIndentation = NON_INDENTATION.matcher( anonLinePrefix ).replaceAll( "" );
                  final Range anonRange = new Range( positionAt( source, openOffset ), positionAt( source, replaceEnd ) );
                  final String anonTyped = source.substring( openOffset, cursor );
                  final State documentFacts = collectDocumentFacts( tokens, current, namespaces );
                  final String enclosingSubject = top.enclosingSubject;
                  return Optional.of( new CursorContext(
                        anonPosition.get(),
                        sammNamespace,
                        sammPrefix,
                        characteristicNamespace,
                        characteristicPrefix,
                        xsdPrefix,
                        unitPrefix,
                        version.get(),
                        Map.copyOf( namespaces ),
                        modelPrefix( namespaces ),
                        anonRange,
                        anonTyped,
                        anonIndentation,
                        lineEnding,
                        true,
                        Set.copyOf( documentFacts.typesBySubject.getOrDefault( enclosingSubject, Set.of() ) ),
                        Set.copyOf( documentFacts.predicatesBySubject.getOrDefault( enclosingSubject, Set.of() ) ),
                        Optional.empty() ) );
               }
            }
         }
         return Optional.empty();
      }

      if ( cursorState.isTopLevelStatement() && top.stage == Stage.SUBJECT ) {
         return Optional.of( new CursorContext(
               TargetPosition.TOP_LEVEL_SUBJECT,
               sammNamespace,
               sammPrefix,
               characteristicNamespace,
               characteristicPrefix,
               xsdPrefix,
               unitPrefix,
               version.get(),
               Map.copyOf( namespaces ),
               modelPrefix( namespaces ),
               range,
               typed,
               indentation,
               lineEnding,
               blankRestOfLine,
               Set.of(),
               Set.of(),
               Optional.empty() ) );
      }

      if ( ( top.delimiter.isEmpty() || top.delimiter.equals( "[" ) )
            && top.stage == Stage.PREDICATE
            && !top.subject.isEmpty() ) {
         final State documentFacts = collectDocumentFacts( tokens, current, namespaces );
         final String subject = top.subject;
         return Optional.of( new CursorContext(
               TargetPosition.PREDICATE,
               sammNamespace,
               sammPrefix,
               characteristicNamespace,
               characteristicPrefix,
               xsdPrefix,
               unitPrefix,
               version.get(),
               Map.copyOf( namespaces ),
               modelPrefix( namespaces ),
               range,
               typed,
               indentation,
               lineEnding,
               blankRestOfLine,
               Set.copyOf( documentFacts.typesBySubject.getOrDefault( subject, Set.of() ) ),
               Set.copyOf( documentFacts.predicatesBySubject.getOrDefault( subject, Set.of() ) ),
               determinePredicateSeparator( tokens, current, namespaces ) ) );
      }

      if ( ( top.delimiter.isEmpty() || top.delimiter.equals( "[" ) )
            && top.stage == Stage.OBJECT
            && !top.predicate.isEmpty() ) {
         final State documentFacts = collectDocumentFacts( tokens, current, namespaces );
         final String subject = top.subject;
         final Optional<String> enclosingPredicate = top.delimiter.equals( "[" )
               ? Optional.ofNullable( top.enclosingPredicate ).filter( p -> !p.isEmpty() )
               : Optional.empty();
         return Optional.of( new CursorContext(
               TargetPosition.OBJECT_VALUE,
               sammNamespace,
               sammPrefix,
               characteristicNamespace,
               characteristicPrefix,
               xsdPrefix,
               unitPrefix,
               version.get(),
               Map.copyOf( namespaces ),
               modelPrefix( namespaces ),
               range,
               typed,
               indentation,
               lineEnding,
               blankRestOfLine,
               Set.copyOf( documentFacts.typesBySubject.getOrDefault( subject, Set.of() ) ),
               Set.copyOf( documentFacts.predicatesBySubject.getOrDefault( subject, Set.of() ) ),
               Optional.empty(),
               Optional.of( top.predicate ),
               entityPrefix,
               enclosingPredicate,
               Map.copyOf( documentFacts.typesBySubject ),
               Map.copyOf( documentFacts.extendedBySubject ) ) );
      }
      return Optional.empty();
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
      if ( parent != null && parent.delimiter.equals( "(" ) && top.enclosingPredicate.equals( sammNamespace + "properties" ) ) {
         return Optional.of( TargetPosition.OBJECT_PROPERTIES_LIST );
      }
      if ( parent != null && !parent.delimiter.equals( "(" ) ) {
         if ( characteristicNamespace.isPresent()
               && top.enclosingPredicate.equals( characteristicNamespace.get() + "constraint" ) ) {
            return Optional.of( TargetPosition.OBJECT_CONSTRAINT );
         }
         if ( isCharacteristicPredicate( top.enclosingPredicate, sammNamespace, characteristicNamespace ) ) {
            return Optional.of( TargetPosition.OBJECT_CHARACTERISTIC );
         }
      }
      return Optional.empty();
   }

   private static boolean isCharacteristicPredicate(
         final String predicateUri,
         final String sammNamespace,
         final Optional<String> characteristicNamespace ) {
      if ( predicateUri.equals( sammNamespace + "characteristic" ) ) {
         return true;
      }
      if ( characteristicNamespace.isEmpty() ) {
         return false;
      }
      final String charNs = characteristicNamespace.get();
      return predicateUri.equals( charNs + "baseCharacteristic" )
            || predicateUri.equals( charNs + "elementCharacteristic" )
            || predicateUri.equals( charNs + "left" )
            || predicateUri.equals( charNs + "right" );
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

   private static State collectDocumentFacts( final List<Lexeme> tokens, final Lexeme current, final Map<String, String> namespaces ) {
      final State documentFacts = new State( namespaces, true );
      for ( final Lexeme token : tokens ) {
         if ( token != current ) {
            documentFacts.accept( token );
         }
      }
      return documentFacts;
   }

   private static Optional<String> determinePredicateSeparator(
         final List<Lexeme> tokens,
         final Lexeme current,
         final Map<String, String> namespaces ) {
      final Optional<Lexeme> next = tokens.stream()
            .filter( token -> token.start >= current.end && token != current && token.kind != TokenKind.COMMENT )
            .findFirst();
      if ( next.isEmpty() ) {
         return Optional.of( " ;" );
      }
      final Lexeme nextToken = next.get();
      if ( Set.of( ".", ";", "]" ).contains( nextToken.text ) ) {
         return Optional.of( "" );
      }
      final boolean followedByAnotherPredicate = !expand( nextToken.text, namespaces ).isEmpty()
            && tokens.stream()
                  .filter( token -> token.start >= nextToken.end && token.kind != TokenKind.COMMENT )
                  .findFirst()
                  .filter( token -> token.kind != TokenKind.PUNCTUATION || Set.of( "[", "(" ).contains( token.text ) )
                  .isPresent();
      return followedByAnotherPredicate
            ? Optional.of( nextToken.start == current.end ? " ; " : " ;" )
            : Optional.empty();
   }

   private Map<String, String> declaredNamespacesBeforeCursor(
         final ParsedDocument document,
         final Position position,
         final String source,
         final int cursor ) {
      final Map<String, String> result = new LinkedHashMap<>();
      final Set<String> conflicting = new HashSet<>();
      final int byteColumn = source.substring( source.lastIndexOf( '\n', cursor - 1 ) + 1, cursor )
            .getBytes( StandardCharsets.UTF_8 ).length;
      document.turtleSyntaxTree().tokens()
            .filter( node -> Set.of( ParserTokenType.PREFIX_ID, ParserTokenType.SPARQL_PREFIX ).contains( node.type() ) )
            .filter( node -> node.location().toLine() < position.getLine()
                  || node.location().toLine() == position.getLine()
                        && node.location().toColumn() <= byteColumn )
            .forEach( node -> {
               final Optional<String> prefix = node.childWithType( ParserTokenType.NAMESPACE )
                     .filter( TurtleSyntaxTree.Node::isToken )
                     .map( TurtleSyntaxTree.Node::content );
               final Optional<String> iri = node.childWithType( ParserTokenType.IRI_REFERENCE )
                     .filter( TurtleSyntaxTree.Node::isToken )
                     .map( TurtleSyntaxTree.Node::content );
               if ( prefix.isPresent() && iri.isPresent() && iri.get().startsWith( "<" ) && iri.get().endsWith( ">" ) ) {
                  final String namespace = iri.get().substring( 1, iri.get().length() - 1 );
                  final String previous = result.put( prefix.get(), namespace );
                  if ( previous != null && !previous.equals( namespace ) ) {
                     conflicting.add( prefix.get() );
                  }
               }
            } );
      conflicting.forEach( result::remove );
      return result;
   }

   private Optional<String> modelPrefix( final Map<String, String> namespaces ) {
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

   private static String expand( final String text, final Map<String, String> namespaces ) {
      if ( text.startsWith( "<" ) && text.endsWith( ">" ) ) {
         return text.substring( 1, text.length() - 1 );
      }
      final int colon = text.indexOf( ':' );
      if ( colon >= 0 ) {
         final String namespace = namespaces.get( text.substring( 0, colon + 1 ) );
         if ( namespace != null ) {
            return namespace + text.substring( colon + 1 );
         }
      }
      return "";
   }

   private static int offset( final String source, final Position position ) {
      if ( position.getLine() < 0 || position.getCharacter() < 0 ) {
         return -1;
      }
      int lineStart = 0;
      for ( int line = 0; line < position.getLine(); line++ ) {
         final int newline = source.indexOf( '\n', lineStart );
         if ( newline < 0 ) {
            return -1;
         }
         lineStart = newline + 1;
      }
      final int newline = source.indexOf( '\n', lineStart );
      final int lineEnd = newline < 0 ? source.length() : newline > 0 && source.charAt( newline - 1 ) == '\r' ? newline - 1 : newline;
      return position.getCharacter() <= lineEnd - lineStart ? lineStart + position.getCharacter() : -1;
   }

   static Position positionAt( final String source, final int offset ) {
      final String preceding = source.substring( 0, offset );
      return new Position( (int) preceding.chars().filter( character -> character == '\n' ).count(),
            offset - preceding.lastIndexOf( '\n' ) - 1 );
   }

   private enum TokenKind {
      TERM, IRI, STRING, COMMENT, PUNCTUATION
   }

   private record Lexeme(
         String text, int start, int end, TokenKind kind
   ) {
      boolean protectedAt( final int cursor ) {
         if ( kind == TokenKind.TERM || kind == TokenKind.PUNCTUATION || cursor <= start || cursor > end ) {
            return false;
         }
         if ( cursor < end ) {
            return kind != TokenKind.STRING
                  || text.startsWith( "\"\"\"" ) || text.startsWith( "'''" ) || text.contains( "\n" );
         }
         return switch ( kind ) {
            case COMMENT -> true;
            case IRI -> !text.endsWith( ">" );
            case STRING -> ( text.startsWith( "\"\"\"" ) || text.startsWith( "'''" ) )
                  || text.contains( "\n" );
            default -> false;
         };
      }
   }

   private static List<Lexeme> tokenize( final String source ) {
      final List<Lexeme> result = new ArrayList<>();
      int index = 0;
      while ( index < source.length() ) {
         if ( Character.isWhitespace( source.charAt( index ) ) ) {
            index++;
            continue;
         }
         final int start = index;
         final char character = source.charAt( index++ );
         final TokenKind kind;
         if ( character == '#' ) {
            kind = TokenKind.COMMENT;
            while ( index < source.length() && source.charAt( index ) != '\n' && source.charAt( index ) != '\r' ) {
               index++;
            }
         } else if ( character == '<' ) {
            kind = TokenKind.IRI;
            while ( index < source.length() && source.charAt( index++ ) != '>' ) {
               // advance to end of IRI
            }
         } else if ( character == '\'' || character == '"' ) {
            kind = TokenKind.STRING;
            final String singleQuote = String.valueOf( character );
            final String tripleQuote = singleQuote.repeat( 3 );
            final String quote = source.startsWith( tripleQuote, start ) ? tripleQuote : singleQuote;
            index = start + quote.length();
            while ( index < source.length() ) {
               if ( quote.length() == 1 && ( source.charAt( index ) == '\n' || source.charAt( index ) == '\r' ) ) {
                  break;
               }
               if ( source.charAt( index ) == '\\' ) {
                  index = Math.min( index + 2, source.length() );
               } else if ( source.startsWith( quote, index ) ) {
                  index += quote.length();
                  break;
               } else {
                  index++;
               }
            }
         } else if ( ";,[](){}".indexOf( character ) >= 0
               || character == '.' && ( index == source.length() || Character.isWhitespace( source.charAt( index ) ) ) ) {
            kind = TokenKind.PUNCTUATION;
         } else {
            kind = TokenKind.TERM;
            while ( index < source.length()
                  && !Character.isWhitespace( source.charAt( index ) )
                  && ";,[](){}<>\"'#".indexOf( source.charAt( index ) ) < 0 ) {
               index++;
            }
            if ( source.charAt( index - 1 ) == '.' && index - start > 1 ) {
               index--;
            }
         }
         result.add( new Lexeme( source.substring( start, index ), start, index, kind ) );
      }
      return result;
   }

   private enum Stage {
      SUBJECT, PREDICATE, OBJECT, AFTER_OBJECT, DATATYPE, INVALID
   }

   private enum DirectiveMode {
      NONE, TURTLE_AT_DIRECTIVE, SPARQL_DIRECTIVE
   }

   private static final class Frame {
      private final String delimiter;
      private final int openOffset;
      private final String enclosingSubject;
      private final String enclosingPredicate;
      private String subject;
      private String predicate = "";
      private Stage stage;
      private boolean hasContent;

      Frame(
            final String delimiter,
            final int openOffset,
            final String enclosingSubject,
            final String enclosingPredicate,
            final String subject,
            final Stage stage ) {
         this.delimiter = delimiter;
         this.openOffset = openOffset;
         this.enclosingSubject = enclosingSubject;
         this.enclosingPredicate = enclosingPredicate;
         this.subject = subject;
         this.stage = stage;
      }
   }

   private static final class State {
      private final Map<String, String> namespaces;
      private final boolean recoverAtSeparator;
      private final Map<String, Set<String>> typesBySubject = new HashMap<>();
      private final Map<String, Set<String>> predicatesBySubject = new HashMap<>();
      private final Map<String, Set<String>> extendedBySubject = new HashMap<>();
      private final ArrayDeque<Frame> frames = new ArrayDeque<>();
      private DirectiveMode directiveMode = DirectiveMode.NONE;
      private String declaredPrefix = "";
      private boolean hasTripleStatements;

      State( final Map<String, String> namespaces, final boolean recoverAtSeparator ) {
         this.namespaces = new LinkedHashMap<>( namespaces );
         this.recoverAtSeparator = recoverAtSeparator;
         frames.push( new Frame( "", -1, "", "", "", Stage.SUBJECT ) );
      }

      Frame top() {
         return frames.peek();
      }

      Frame parent() {
         if ( frames.size() < 2 ) {
            return null;
         }
         final Iterator<Frame> iterator = frames.iterator();
         iterator.next();
         return iterator.next();
      }

      boolean isTopLevelStatement() {
         return frames.size() == 1 && directiveMode == DirectiveMode.NONE;
      }

      void accept( final Lexeme token ) {
         if ( token.kind == TokenKind.COMMENT ) {
            return;
         }
         final String text = token.text;
         if ( directiveMode != DirectiveMode.NONE ) {
            acceptDirectiveToken( token, text );
            return;
         }
         final Frame top = frames.peek();
         if ( frames.size() == 1 && top.stage == Stage.SUBJECT
               && ( text.equalsIgnoreCase( "@prefix" ) || text.equalsIgnoreCase( "@base" )
                     || text.equalsIgnoreCase( "PREFIX" ) || text.equalsIgnoreCase( "BASE" ) ) ) {
            directiveMode = text.startsWith( "@" ) ? DirectiveMode.TURTLE_AT_DIRECTIVE : DirectiveMode.SPARQL_DIRECTIVE;
            declaredPrefix = "";
            return;
         }
         hasTripleStatements = true;
         if ( Set.of( "[", "(", "{", "]", ")", "}" ).contains( text ) ) {
            acceptGroupDelimiter( token, text );
            return;
         }
         if ( top.delimiter.equals( "(" ) ) {
            acceptCollectionToken( top, token, text );
            return;
         }
         if ( top.delimiter.equals( "{" ) ) {
            return;
         }
         top.hasContent = true;
         switch ( text ) {
            case "." -> {
               if ( top.delimiter.isEmpty() ) {
                  top.stage = recoverAtSeparator || top.stage == Stage.AFTER_OBJECT
                        || top.stage == Stage.PREDICATE && !top.subject.isEmpty()
                              ? Stage.SUBJECT
                              : Stage.INVALID;
                  top.subject = "";
                  top.predicate = "";
               } else if ( recoverAtSeparator ) {
                  while ( frames.size() > 1 ) {
                     frames.pop();
                  }
                  final Frame root = frames.peek();
                  root.stage = Stage.SUBJECT;
                  root.subject = "";
                  root.predicate = "";
               } else {
                  top.stage = Stage.INVALID;
               }
            }
            case ";" -> top.stage = top.stage == Stage.AFTER_OBJECT || top.stage == Stage.PREDICATE
                  || recoverAtSeparator && !top.subject.isEmpty()
                        ? Stage.PREDICATE
                        : Stage.INVALID;
            case "," -> top.stage = top.stage == Stage.AFTER_OBJECT ? Stage.OBJECT : Stage.INVALID;
            default -> advanceStage( top, token, text );
         }
      }

      private void acceptDirectiveToken( final Lexeme token, final String text ) {
         if ( token.kind == TokenKind.TERM && text.endsWith( ":" ) ) {
            declaredPrefix = text;
         } else if ( !declaredPrefix.isEmpty() && token.kind == TokenKind.IRI && text.endsWith( ">" ) ) {
            namespaces.put( declaredPrefix, text.substring( 1, text.length() - 1 ) );
         }
         if ( text.equals( "." ) || directiveMode == DirectiveMode.SPARQL_DIRECTIVE && token.kind == TokenKind.IRI ) {
            directiveMode = DirectiveMode.NONE;
         }
      }

      private void acceptGroupDelimiter( final Lexeme token, final String text ) {
         final Frame top = frames.peek();
         if ( text.equals( "[" ) ) {
            final String bnodeId = "_:bnode@" + token.start;
            top.hasContent = true;
            if ( top.delimiter.equals( "(" ) ) {
               final Stage nextStage = top.stage == Stage.OBJECT ? Stage.PREDICATE : Stage.INVALID;
               frames.push( new Frame( "[", token.start, top.enclosingSubject, top.enclosingPredicate, bnodeId, nextStage ) );
            } else if ( top.stage == Stage.SUBJECT ) {
               top.subject = bnodeId;
               top.stage = Stage.PREDICATE;
               frames.push( new Frame( "[", token.start, "", "", bnodeId, Stage.PREDICATE ) );
            } else if ( top.stage == Stage.OBJECT ) {
               top.stage = Stage.AFTER_OBJECT;
               frames.push( new Frame( "[", token.start, top.subject, top.predicate, bnodeId, Stage.PREDICATE ) );
            } else {
               top.stage = Stage.INVALID;
               frames.push( new Frame( "[", token.start, "", "", "", Stage.INVALID ) );
            }
         } else if ( text.equals( "(" ) ) {
            top.hasContent = true;
            if ( top.delimiter.equals( "(" ) ) {
               frames.push( new Frame( "(", token.start, "", "", "", top.stage == Stage.OBJECT ? Stage.OBJECT : Stage.INVALID ) );
            } else if ( top.stage == Stage.SUBJECT ) {
               top.subject = "_:collection@" + token.start;
               top.stage = Stage.PREDICATE;
               frames.push( new Frame( "(", token.start, "", "", "", Stage.OBJECT ) );
            } else if ( top.stage == Stage.OBJECT ) {
               top.stage = Stage.AFTER_OBJECT;
               frames.push( new Frame( "(", token.start, top.subject, top.predicate, "", Stage.OBJECT ) );
            } else {
               top.stage = Stage.INVALID;
               frames.push( new Frame( "(", token.start, "", "", "", Stage.INVALID ) );
            }
         } else if ( text.equals( "{" ) ) {
            top.hasContent = true;
            top.stage = Stage.INVALID;
            frames.push( new Frame( "{", token.start, "", "", "", Stage.INVALID ) );
         } else if ( frames.size() > 1 && "[({".indexOf( top.delimiter ) == "])}".indexOf( text ) ) {
            final Frame closed = frames.pop();
            if ( closed.stage == Stage.INVALID && !recoverAtSeparator ) {
               frames.peek().stage = Stage.INVALID;
            }
         } else {
            top.stage = Stage.INVALID;
         }
      }

      private void acceptCollectionToken( final Frame top, final Lexeme token, final String text ) {
         top.hasContent = true;
         if ( top.stage == Stage.INVALID ) {
            return;
         }
         if ( text.equals( "^^" ) ) {
            top.stage = Stage.DATATYPE;
         } else if ( top.stage == Stage.DATATYPE ) {
            top.stage = expand( text, namespaces ).isEmpty() ? Stage.INVALID : Stage.OBJECT;
         } else if ( LANG_TAG.matcher( text ).matches()
               || text.startsWith( "^^" ) && !expand( text.substring( 2 ), namespaces ).isEmpty() ) {
            top.stage = Stage.OBJECT;
         } else if ( isValidObjectToken( token, text ) ) {
            top.stage = Stage.OBJECT;
         } else {
            top.stage = Stage.INVALID;
         }
      }

      private void advanceStage( final Frame top, final Lexeme token, final String text ) {
         switch ( top.stage ) {
            case SUBJECT -> {
               top.subject = text.startsWith( "_:" ) ? text : expand( text, namespaces );
               top.stage = top.subject.isEmpty() ? Stage.INVALID : Stage.PREDICATE;
            }
            case PREDICATE -> {
               top.predicate = text.equals( "a" ) ? RDF_TYPE : expand( text, namespaces );
               if ( !top.subject.isEmpty() && !top.predicate.isEmpty() ) {
                  predicatesBySubject.computeIfAbsent( top.subject, ignored -> new HashSet<>() ).add( top.predicate );
               }
               top.stage = top.predicate.isEmpty() ? Stage.INVALID : Stage.OBJECT;
            }
            case OBJECT -> {
               if ( top.predicate.equals( RDF_TYPE ) && !top.subject.isEmpty() ) {
                  final String expandedObject = expand( text, namespaces );
                  if ( !expandedObject.isEmpty() ) {
                     typesBySubject.computeIfAbsent( top.subject, ignored -> new HashSet<>() ).add( expandedObject );
                  }
               }
               if ( ( top.predicate.endsWith( "#extends" ) || top.predicate.endsWith( "/extends" ) ) && !top.subject.isEmpty() ) {
                  final String expandedObject = expand( text, namespaces );
                  if ( !expandedObject.isEmpty() ) {
                     extendedBySubject.computeIfAbsent( top.subject, ignored -> new HashSet<>() ).add( expandedObject );
                  }
               }
               top.stage = isValidObjectToken( token, text ) ? Stage.AFTER_OBJECT : Stage.INVALID;
            }
            case DATATYPE -> top.stage = expand( text, namespaces ).isEmpty() ? Stage.INVALID : Stage.AFTER_OBJECT;
            case AFTER_OBJECT -> {
               if ( text.equals( "^^" ) ) {
                  top.stage = Stage.DATATYPE;
               } else if ( !LANG_TAG.matcher( text ).matches()
                     && !( text.startsWith( "^^" ) && !expand( text.substring( 2 ), namespaces ).isEmpty() ) ) {
                  top.stage = Stage.INVALID;
               }
            }
            default -> {
               // stay in INVALID stage
            }
         }
      }

      private boolean isValidObjectToken( final Lexeme token, final String text ) {
         return token.kind == TokenKind.STRING
               || !expand( text, namespaces ).isEmpty()
               || text.startsWith( "_:" )
               || text.equals( "true" )
               || text.equals( "false" )
               || NUMERIC_LITERAL.matcher( text ).matches();
      }
   }
}
