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
import java.util.regex.Pattern;

import org.eclipse.esmf.metamodel.vocabulary.SAMM;
import org.eclipse.esmf.turtle.languageserver.turtle.completion.SammCompletionContext.SubjectFacts;

import org.apache.jena.vocabulary.RDF;

/** Scans incomplete Turtle without requiring a valid RDF model. */
final class TurtleCompletionScanner {
   private static final String RDF_TYPE = RDF.type.getURI();
   private static final Pattern NUMERIC_LITERAL =
         Pattern.compile( "[+-]?(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)(?:[eE][+-]?[0-9]+)?" );
   private static final Pattern LANG_TAG = Pattern.compile( "@[a-zA-Z]+(?:-[a-zA-Z0-9]+)*" );

   static State scanBefore( final List<Lexeme> tokens, final Lexeme current, final Map<String, String> namespaces ) {
      final State state = new State( namespaces, false );
      for ( final Lexeme token : tokens ) {
         if ( token.start >= current.start ) {
            break;
         }
         state.accept( token );
      }
      return state;
   }

   static State collectDocumentFacts( final List<Lexeme> tokens, final Lexeme current, final Map<String, String> namespaces ) {
      final State documentFacts = new State( namespaces, true );
      for ( final Lexeme token : tokens ) {
         if ( token != current ) {
            documentFacts.accept( token );
         }
      }
      return documentFacts;
   }

   static String expand( final String text, final Map<String, String> namespaces ) {
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

   enum TokenKind {
      TERM, IRI, STRING, COMMENT, PUNCTUATION
   }

   record Lexeme(
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
            case TERM, PUNCTUATION -> false;
         };
      }
   }

   static List<Lexeme> tokenize( final String source ) {
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

   enum Stage {
      SUBJECT, PREDICATE, OBJECT, AFTER_OBJECT, DATATYPE, INVALID
   }

   enum DirectiveMode {
      NONE, TURTLE_AT_DIRECTIVE, SPARQL_DIRECTIVE
   }

   static final class Frame {
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

      boolean isEmptyAnonymousBracket() {
         return "[".equals( delimiter ) && !hasContent && stage != Stage.INVALID;
      }

      boolean isSubjectSlot() {
         return stage == Stage.SUBJECT;
      }

      boolean isPredicateSlot() {
         return stage == Stage.PREDICATE;
      }

      boolean isObjectSlot() {
         return stage == Stage.OBJECT;
      }

      boolean hasSubject() {
         return !subject.isEmpty();
      }

      boolean hasPredicate() {
         return !predicate.isEmpty();
      }

      boolean isInsideSupportedDelimiter() {
         return delimiter.isEmpty() || "[".equals( delimiter );
      }

      boolean isList() {
         return "(".equals( delimiter );
      }

      Optional<String> enclosingPredicateIfAnonymous() {
         return "[".equals( delimiter ) && !enclosingPredicate.isEmpty()
               ? Optional.of( enclosingPredicate )
               : Optional.empty();
      }
   }

   static final class State {
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

      boolean isInDirective() {
         return directiveMode != DirectiveMode.NONE;
      }

      boolean hasTripleStatements() {
         return hasTripleStatements;
      }

      SubjectFacts factsFor( final String subject ) {
         return new SubjectFacts(
               Set.copyOf( typesBySubject.getOrDefault( subject, Set.of() ) ),
               Set.copyOf( predicatesBySubject.getOrDefault( subject, Set.of() ) ),
               Map.copyOf( typesBySubject ), Map.copyOf( extendedBySubject ) );
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
               if ( ( top.predicate.endsWith( "#" + SAMM.EXTENDS ) || top.predicate.endsWith( "/" + SAMM.EXTENDS ) )
                     && !top.subject.isEmpty() ) {
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
