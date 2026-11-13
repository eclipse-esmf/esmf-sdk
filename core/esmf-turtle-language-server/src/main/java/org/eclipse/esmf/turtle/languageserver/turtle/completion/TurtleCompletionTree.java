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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.samm.KnownVersion;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammCompletionContext.SubjectFacts;

import org.apache.jena.vocabulary.RDF;
import org.eclipse.lsp4j.Position;

/** Projects Tree-sitter's syntactic roles, including recovered nodes, into completion context. */
public final class TurtleCompletionTree {
   private static final String RDF_TYPE = RDF.type.getURI();
   /**
    * {@code samm:extends} in every known SAMM version. Predicates of other vocabularies that are also
    * named extends do not count.
    */
   private static final Set<String> SAMM_EXTENDS_PREDICATES = KnownVersion.getVersions().stream()
         .map( version -> new SAMM( version ).getNamespace() + SAMM.EXTENDS )
         .collect( Collectors.toUnmodifiableSet() );
   private static final Set<String> ATOMIC_TYPES = Set.of(
         ParserTokenType.SUBJECT, ParserTokenType.PREDICATE, ParserTokenType.PREFIXED_NAME, ParserTokenType.NAMESPACE,
         ParserTokenType.IRI_REFERENCE, ParserTokenType.BLANK_NODE_LABEL, ParserTokenType.STRING,
         ParserTokenType.BOOLEAN_LITERAL, ParserTokenType.INTEGER, ParserTokenType.DECIMAL,
         ParserTokenType.DOUBLE, ParserTokenType.LANG_TAG,
         ParserTokenType.PREFIX_ID, ParserTokenType.SPARQL_PREFIX, ParserTokenType.BASE, ParserTokenType.SPARQL_BASE );
   private static final Set<String> DIRECTIVE_TYPES = Set.of(
         ParserTokenType.PREFIX_ID, ParserTokenType.SPARQL_PREFIX, ParserTokenType.BASE, ParserTokenType.SPARQL_BASE );
   private static final Set<String> REFERENCE_TYPES = Set.of(
         ParserTokenType.PREFIXED_NAME, ParserTokenType.IRI_REFERENCE, ParserTokenType.BLANK_NODE_LABEL );
   private static final Set<String> OBJECT_TYPES = Set.of(
         ParserTokenType.PREFIXED_NAME, ParserTokenType.IRI_REFERENCE, ParserTokenType.BLANK_NODE_LABEL,
         ParserTokenType.STRING, ParserTokenType.BOOLEAN_LITERAL, ParserTokenType.INTEGER, ParserTokenType.DECIMAL,
         ParserTokenType.DOUBLE );
   private final TurtleSyntaxTree syntaxTree;
   private final String source;
   private final List<Integer> lineStarts = new ArrayList<>();
   private final List<byte[]> lines = new ArrayList<>();
   private final List<Lexeme> tokens = new ArrayList<>();
   private final List<Lexeme> documentTokens;

   /** A prefix declaration, located by the end of its node */
   record Directive(
         int line, int byteColumn, String prefix, String namespace
   ) {}

   /**
    * Projects one document. Create it once per request and take the cursor from it.
    */
   public TurtleCompletionTree( final ParsedDocument document ) {
      syntaxTree = document.turtleSyntaxTree();
      source = syntaxTree.content();
      int lineStart = 0;
      for ( final String line : source.split( "\n", -1 ) ) {
         lineStarts.add( lineStart );
         lines.add( line.getBytes( StandardCharsets.UTF_8 ) );
         lineStart += line.length() + 1;
      }
      visit( syntaxTree.rootNode(), false );
      documentTokens = List.copyOf( joinUnfinishedPrefixedNames( tokens ) );
   }

   /**
    * The cursor at a position of the document
    */
   public TurtleCursor cursorAt( final Position position ) {
      final int offset = offsetAt( position );
      return new TurtleCursor( position, offset, source, documentTokens, offset < 0 ? List.of() : directives() );
   }

   /**
    * The prefix declarations of the document. Read when a cursor is taken, not when the document is
    * projected.
    */
   private List<Directive> directives() {
      final List<Directive> directives = new ArrayList<>();
      syntaxTree.tokens()
            .filter( node -> Set.of( ParserTokenType.PREFIX_ID, ParserTokenType.SPARQL_PREFIX ).contains( node.type() ) )
            .forEach( node -> {
               final Optional<String> prefix = node.childWithType( ParserTokenType.NAMESPACE )
                     .filter( TurtleSyntaxTree.Node::isToken )
                     .map( TurtleSyntaxTree.Node::content );
               final Optional<String> iri = node.childWithType( ParserTokenType.IRI_REFERENCE )
                     .filter( TurtleSyntaxTree.Node::isToken )
                     .map( TurtleSyntaxTree.Node::content );
               if ( prefix.isPresent() && iri.isPresent() && iri.get().startsWith( "<" ) && iri.get().endsWith( ">" ) ) {
                  directives.add( new Directive( node.location().toLine(), node.location().toColumn(), prefix.get(),
                        iri.get().substring( 1, iri.get().length() - 1 ) ) );
               }
            } );
      return directives;
   }

   private int offsetAt( final Position position ) {
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

   /**
    * While the user types a prefixed name, the parser reports the prefix and the colon as two tokens
    * until the local name
    * starts, e.g. {@code samm} and {@code :} for {@code samm:}. This joins such neighbours into one
    * term, as the parser
    * does for {@code samm:p}.
    */
   private static List<Lexeme> joinUnfinishedPrefixedNames( final List<Lexeme> tokens ) {
      final List<Lexeme> joined = new ArrayList<>( tokens.size() );
      for ( final Lexeme token : tokens ) {
         final Lexeme previous = joined.isEmpty() ? null : joined.getLast();
         if ( previous != null && ParserTokenType.PN_PREFIX.equals( previous.type() ) && ParserTokenType.SYMBOL_COLON.equals( token.type() )
               && previous.end() == token.start() ) {
            joined.set( joined.size() - 1, new Lexeme( previous.text() + token.text(), previous.start(), token.end(),
                  TokenKind.TERM, ParserTokenType.PREFIXED_NAME, previous.valid() && token.valid(), Optional.empty() ) );
         } else {
            joined.add( token );
         }
      }
      return joined;
   }

   private int start( final TurtleSyntaxTree.Node node ) {
      return offset( node.location().fromLine(), node.location().fromColumn() );
   }

   private int end( final TurtleSyntaxTree.Node node ) {
      return offset( node.location().toLine(), node.location().toColumn() );
   }

   private int offset( final int line, final int byteColumn ) {
      return lineStarts.get( line ) + new String( lines.get( line ), 0, byteColumn, StandardCharsets.UTF_8 ).length();
   }

   private void visit( final TurtleSyntaxTree.Node node, final boolean annotation ) {
      final int start = start( node );
      final int end = end( node );
      if ( start == end ) {
         return;
      }
      final String type = node.type();
      if ( ParserTokenType.ANON.equals( type ) ) {
         tokens.add( new Lexeme( "[", start, start + 1, TokenKind.PUNCTUATION ) );
         tokens.add( new Lexeme( "]", end - 1, end, TokenKind.PUNCTUATION ) );
         return;
      }
      final boolean groupedSubject = ParserTokenType.SUBJECT.equals( type ) && node.children().stream()
            .anyMatch( child -> Set.of( ParserTokenType.COLLECTION, ParserTokenType.ANON ).contains( child.type() ) );
      if ( ATOMIC_TYPES.contains( type ) && !groupedSubject ) {
         final String text = source.substring( start, end );
         if ( isRecoveredTermSequence( type, text ) ) {
            // Error recovery can merge a half-typed name with the following statement, e.g. "prop\n:Aspect".
            splitAtWhitespace( start, text, type );
            return;
         }
         final TokenKind kind = annotation ? TokenKind.ANNOTATION
               : DIRECTIVE_TYPES.contains( type ) ? TokenKind.DIRECTIVE
                     : ParserTokenType.STRING.equals( type ) ? TokenKind.STRING
                           : text.startsWith( "<" ) ? TokenKind.IRI : TokenKind.TERM;
         final Optional<Map.Entry<String, String>> declaration = node.childWithType( ParserTokenType.NAMESPACE )
               .filter( prefix -> !hasErrors( prefix ) )
               .flatMap( prefix -> node.childWithType( ParserTokenType.IRI_REFERENCE )
                     .filter( iri -> !hasErrors( iri ) )
                     .map( iri -> Map.entry( prefix.content(), source.substring( start( iri ) + 1, end( iri ) - 1 ) ) ) );
         tokens.add( new Lexeme( text, start, end, kind, type, !hasErrors( node ), declaration ) );
         return;
      }
      if ( ParserTokenType.COMMENT.equals( type ) ) {
         tokens.add( new Lexeme( source.substring( start, end ), start, end, TokenKind.COMMENT ) );
         return;
      }
      if ( node.children().isEmpty() ) {
         tokens.add( new Lexeme( source.substring( start, end ), start, end,
               annotation ? TokenKind.ANNOTATION : ";,[](){}.".contains( type ) ? TokenKind.PUNCTUATION : TokenKind.TERM,
               type, !node.isError(), Optional.empty() ) );
         return;
      }

      int consumed = start;
      for ( final TurtleSyntaxTree.Node child : node.children() ) {
         final int childStart = start( child );
         if ( childStart < consumed ) {
            continue;
         }
         if ( node.isError() ) {
            addErrorGap( consumed, childStart );
         }
         // Unfinished strings can survive only as an opening quote inside an ERROR node.
         if ( node.isError() && Set.of( "\"", "'", "\"\"\"", "'''" ).contains( child.type() ) ) {
            final int newline = source.indexOf( '\n', childStart );
            consumed = child.type().length() == 1 && newline >= 0 ? Math.min( end, newline ) : end;
            if ( consumed > childStart && source.charAt( consumed - 1 ) == '\r' ) {
               consumed--;
            }
            tokens.add( new Lexeme( source.substring( childStart, consumed ), childStart, consumed, TokenKind.STRING,
                  ParserTokenType.STRING, false, Optional.empty() ) );
         } else {
            visit( child, annotation || ParserTokenType.RDF_LITERAL.equals( type )
                  && !ParserTokenType.STRING.equals( child.type() ) );
            consumed = end( child );
         }
      }
      if ( node.isError() ) {
         addErrorGap( consumed, end );
      }
   }

   private static boolean isRecoveredTermSequence( final String type, final String text ) {
      return Set.of( ParserTokenType.SUBJECT, ParserTokenType.PREDICATE, ParserTokenType.PREFIXED_NAME ).contains( type )
            && text.chars().anyMatch( Character::isWhitespace )
            && text.indexOf( '<' ) < 0 && text.indexOf( '"' ) < 0 && text.indexOf( '\'' ) < 0;
   }

   private void splitAtWhitespace( final int offset, final String text, final String type ) {
      int index = 0;
      while ( index < text.length() ) {
         while ( index < text.length() && Character.isWhitespace( text.charAt( index ) ) ) {
            index++;
         }
         final int wordStart = index;
         while ( index < text.length() && !Character.isWhitespace( text.charAt( index ) ) ) {
            index++;
         }
         if ( wordStart < index ) {
            tokens.add( new Lexeme( text.substring( wordStart, index ), offset + wordStart, offset + index, TokenKind.TERM,
                  type, true, Optional.empty() ) );
         }
      }
   }

   private void addErrorGap( final int from, final int to ) {
      int start = from;
      int end = to;
      while ( start < end && Character.isWhitespace( source.charAt( start ) ) ) {
         start++;
      }
      while ( end > start && Character.isWhitespace( source.charAt( end - 1 ) ) ) {
         end--;
      }
      if ( start < end ) {
         tokens.add( new Lexeme( source.substring( start, end ), start, end, TokenKind.TERM,
               "ERROR", false, Optional.empty() ) );
      }
   }

   private static boolean hasErrors( final TurtleSyntaxTree.Node node ) {
      return node.isError() || node.children().stream().anyMatch( TurtleCompletionTree::hasErrors );
   }

   static State scanBefore( final List<Lexeme> tokens, final Lexeme current, final Map<String, String> namespaces ) {
      final State state = new State( namespaces, false );
      for ( final Lexeme token : tokens ) {
         if ( token.start >= current.start ) {
            break;
         }
         state.accept( token );
         if ( token.kind == TokenKind.DIRECTIVE && token.end > current.start ) {
            state.inDirective = true;
         }
      }
      return state;
   }

   static State collectDocumentFacts( final List<Lexeme> tokens, final Lexeme current, final Map<String, String> namespaces ) {
      final State state = new State( namespaces, true );
      for ( int i = 0; i < tokens.size(); i++ ) {
         final Lexeme token = tokens.get( i );
         // An unfinished predicate can make the following statement lose its SUBJECT wrapper.
         // Recover at a resource followed by a predicate, before later semicolons reuse the old subject.
         if ( state.isTopLevelStatement() && i + 1 < tokens.size() && token.kind == TokenKind.TERM ) {
            final Lexeme next = tokens.get( i + 1 );
            final boolean declaresType = next.text.equals( "a" ) || CompletionUtils.expand( next.text, namespaces ).equals( RDF_TYPE );
            // "a" can never be an object, so the term before it must be a subject, even if the parser called it
            // a predicate.
            final boolean startsStatement = declaresType && ( REFERENCE_TYPES.contains( token.type )
                  || Set.of( ParserTokenType.SUBJECT, ParserTokenType.PREDICATE ).contains( token.type ) )
                  || REFERENCE_TYPES.contains( token.type ) && ( ParserTokenType.PREDICATE.equals( next.type )
                        || Set.of( ParserTokenType.PREFIXED_NAME, ParserTokenType.IRI_REFERENCE ).contains( next.type ) );
            if ( next.kind != TokenKind.ANNOTATION && startsStatement ) {
               state.top().slot = Slot.SUBJECT;
               state.top().subject = "";
               state.top().predicate = "";
            }
         }
         if ( token != current ) {
            state.accept( token );
         }
      }
      return state;
   }

   enum TokenKind {
      TERM, IRI, STRING, COMMENT, PUNCTUATION, DIRECTIVE, ANNOTATION
   }

   record Lexeme(
         String text, int start, int end, TokenKind kind, String type, boolean valid,
         Optional<Map.Entry<String, String>> prefixDeclaration
   ) {
      Lexeme( final String text, final int start, final int end, final TokenKind kind ) {
         this( text, start, end, kind, text, true, Optional.empty() );
      }

      boolean protectedAt( final int cursor ) {
         if ( cursor <= start || cursor > end ) {
            return false;
         }
         return switch ( kind ) {
            case COMMENT -> true;
            case IRI -> cursor < end || !text.endsWith( ">" );
            case STRING -> text.startsWith( "\"\"\"" ) || text.startsWith( "'''" ) || text.contains( "\n" );
            default -> false;
         };
      }
   }

   private enum Slot {
      SUBJECT, PREDICATE, OBJECT, NONE, INVALID
   }

   static final class Frame {
      private final String delimiter;
      private final int openOffset;
      private final String enclosingSubject;
      private final String enclosingPredicate;
      private String subject;
      private String predicate = "";
      private Slot slot;
      private boolean hasContent;

      private Frame( final String delimiter, final int openOffset, final String enclosingSubject,
            final String enclosingPredicate, final String subject, final Slot slot ) {
         this.delimiter = delimiter;
         this.openOffset = openOffset;
         this.enclosingSubject = enclosingSubject;
         this.enclosingPredicate = enclosingPredicate;
         this.subject = subject;
         this.slot = slot;
      }

      int openOffset() {
         return openOffset;
      }

      String subject() {
         return subject;
      }

      String predicate() {
         return predicate;
      }

      String enclosingSubject() {
         return enclosingSubject;
      }

      String enclosingPredicate() {
         return enclosingPredicate;
      }

      /** The subject of the statement the position is in, also for a list inside it */
      String statementSubject() {
         return isList() ? enclosingSubject : subject;
      }

      /** The predicate of the statement the position is in, also for a list inside it */
      String statementPredicate() {
         return isList() ? enclosingPredicate : predicate;
      }

      boolean isEmptyAnonymousBracket() {
         return "[".equals( delimiter ) && !hasContent && slot != Slot.INVALID;
      }

      boolean isSubjectSlot() {
         return slot == Slot.SUBJECT;
      }

      boolean isPredicateSlot() {
         return slot == Slot.PREDICATE;
      }

      boolean isObjectSlot() {
         return slot == Slot.OBJECT;
      }

      boolean hasSubject() {
         return !subject.isEmpty();
      }

      boolean hasPredicate() {
         return !predicate.isEmpty();
      }

      boolean isInsideSupportedDelimiter() {
         return delimiter.isEmpty() || "[".equals( delimiter ) || isList();
      }

      boolean isList() {
         return "(".equals( delimiter );
      }

      Optional<String> enclosingPredicateIfAnonymous() {
         return "[".equals( delimiter ) && !enclosingPredicate.isEmpty() ? Optional.of( enclosingPredicate ) : Optional.empty();
      }
   }

   static final class State {
      private final Map<String, String> namespaces;
      private final boolean recoverAtSeparator;
      private final Map<String, Set<String>> typesBySubject = new HashMap<>();
      private final Map<String, Set<String>> predicatesBySubject = new HashMap<>();
      private final Map<String, Set<String>> extendedBySubject = new HashMap<>();
      private final ArrayDeque<Frame> frames = new ArrayDeque<>();
      private boolean inDirective;
      private boolean hasTripleStatements;

      private State( final Map<String, String> namespaces, final boolean recoverAtSeparator ) {
         this.namespaces = new LinkedHashMap<>( namespaces );
         this.recoverAtSeparator = recoverAtSeparator;
         frames.push( new Frame( "", -1, "", "", "", Slot.SUBJECT ) );
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
         return frames.size() == 1 && !inDirective;
      }

      boolean isInDirective() {
         return inDirective;
      }

      boolean hasTripleStatements() {
         return hasTripleStatements;
      }

      /**
       * Namespaces declared before the cursor and anywhere in the document
       */
      Map<String, String> namespaces() {
         return Map.copyOf( namespaces );
      }

      Map<String, Set<String>> typesBySubject() {
         return Map.copyOf( typesBySubject );
      }

      SubjectFacts factsFor( final String subject ) {
         return new SubjectFacts( Set.copyOf( typesBySubject.getOrDefault( subject, Set.of() ) ),
               Set.copyOf( predicatesBySubject.getOrDefault( subject, Set.of() ) ),
               Map.copyOf( typesBySubject ), Map.copyOf( extendedBySubject ) );
      }

      private void accept( final Lexeme token ) {
         if ( token.kind == TokenKind.COMMENT || token.kind == TokenKind.ANNOTATION ) {
            return;
         }
         if ( token.kind == TokenKind.DIRECTIVE ) {
            token.prefixDeclaration.ifPresent( declaration -> namespaces.put( declaration.getKey(), declaration.getValue() ) );
            return;
         }
         if ( Set.of( ParserTokenType.AT_PREFIX, ParserTokenType.AT_BASE, "PREFIX", "BASE" ).contains( token.type ) ) {
            inDirective = true;
            return;
         }
         if ( inDirective ) {
            if ( token.text.equals( "." ) ) {
               inDirective = false;
            }
            return;
         }
         hasTripleStatements = true;
         final Frame top = top();
         if ( Set.of( "[", "(", "{", "]", ")", "}" ).contains( token.text ) ) {
            acceptDelimiter( token );
            return;
         }
         top.hasContent = true;
         switch ( token.text ) {
            case "." -> {
               if ( recoverAtSeparator ) {
                  while ( frames.size() > 1 ) {
                     frames.pop();
                  }
               }
               final Frame root = top();
               root.slot = root.delimiter.isEmpty() && ( recoverAtSeparator || root.slot == Slot.NONE
                     || root.slot == Slot.PREDICATE ) ? Slot.SUBJECT : Slot.INVALID;
               root.subject = "";
               root.predicate = "";
            }
            case ";" -> top.slot = top.hasSubject() && ( recoverAtSeparator || top.slot == Slot.NONE
                  || top.slot == Slot.PREDICATE ) ? Slot.PREDICATE : Slot.INVALID;
            case "," -> top.slot = top.slot == Slot.NONE ? Slot.OBJECT : Slot.INVALID;
            default -> acceptRole( top, token );
         }
      }

      private void acceptRole( final Frame frame, final Lexeme token ) {
         if ( !token.valid || frame.slot == Slot.INVALID ) {
            frame.slot = Slot.INVALID;
            return;
         }
         // A lone subject in an ERROR region can lack the SUBJECT wrapper.
         if ( frame.slot == Slot.SUBJECT
               && ( ParserTokenType.SUBJECT.equals( token.type ) || REFERENCE_TYPES.contains( token.type ) ) ) {
            frame.subject = token.text.startsWith( "_:" ) ? token.text : CompletionUtils.expand( token.text, namespaces );
            frame.slot = frame.hasSubject() ? Slot.PREDICATE : Slot.INVALID;
         } else if ( frame.slot == Slot.PREDICATE && ( ParserTokenType.PREDICATE.equals( token.type )
               || recoverAtSeparator && ( token.text.equals( "a" )
                     || CompletionUtils.expand( token.text, namespaces ).equals( RDF_TYPE )
                     || Set.of( ParserTokenType.PREFIXED_NAME, ParserTokenType.IRI_REFERENCE ).contains( token.type ) ) ) ) {
            frame.predicate = token.text.equals( "a" ) ? RDF_TYPE : CompletionUtils.expand( token.text, namespaces );
            if ( frame.hasPredicate() && frame.hasSubject() ) {
               predicatesBySubject.computeIfAbsent( frame.subject, ignored -> new HashSet<>() ).add( frame.predicate );
            }
            frame.slot = frame.hasPredicate() ? Slot.OBJECT : Slot.INVALID;
         } else if ( frame.slot == Slot.OBJECT && ( OBJECT_TYPES.contains( token.type )
               || recoverAtSeparator && ParserTokenType.PREDICATE.equals( token.type ) ) ) {
            final String object = CompletionUtils.expand( token.text, namespaces );
            if ( REFERENCE_TYPES.contains( token.type ) && object.isEmpty() && !token.text.startsWith( "_:" ) ) {
               frame.slot = Slot.INVALID;
               return;
            }
            if ( frame.predicate.equals( RDF_TYPE ) && !object.isEmpty() ) {
               typesBySubject.computeIfAbsent( frame.subject, ignored -> new HashSet<>() ).add( object );
            }
            if ( SAMM_EXTENDS_PREDICATES.contains( frame.predicate ) && !object.isEmpty() ) {
               extendedBySubject.computeIfAbsent( frame.subject, ignored -> new HashSet<>() ).add( object );
            }
            frame.slot = frame.isList() ? Slot.OBJECT : Slot.NONE;
         } else {
            frame.slot = Slot.INVALID;
         }
      }

      private void acceptDelimiter( final Lexeme token ) {
         final Frame top = top();
         final String text = token.text;
         if ( Set.of( "[", "(", "{" ).contains( text ) ) {
            final boolean valid = !text.equals( "{" ) && ( top.slot == Slot.SUBJECT || top.slot == Slot.OBJECT );
            final boolean subjectSlot = top.slot == Slot.SUBJECT;
            final String subject = "_:bnode@" + token.start;
            final boolean nestedList = top.isList() && text.equals( "(" );
            final String enclosingSubject = nestedList ? "" : top.isList() ? top.enclosingSubject : top.subject;
            final String enclosingPredicate = nestedList ? "" : top.isList() ? top.enclosingPredicate : top.predicate;
            top.hasContent = true;
            if ( subjectSlot ) {
               top.subject = subject;
            }
            top.slot = valid ? subjectSlot ? Slot.PREDICATE : top.isList() ? Slot.OBJECT : Slot.NONE : Slot.INVALID;
            frames.push( new Frame( text, token.start, enclosingSubject, enclosingPredicate,
                  text.equals( "[" ) ? subject : "", valid ? text.equals( "[" ) ? Slot.PREDICATE : Slot.OBJECT : Slot.INVALID ) );
         } else if ( frames.size() > 1 && "[({".indexOf( top.delimiter ) == "])}".indexOf( text ) ) {
            final Frame closed = frames.pop();
            if ( closed.slot == Slot.INVALID && !recoverAtSeparator ) {
               top().slot = Slot.INVALID;
            }
         } else {
            top.slot = Slot.INVALID;
         }
      }
   }
}
