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

package org.eclipse.esmf.turtle.languageserver.structure;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.esmf.aspectmodel.Location;
import org.eclipse.esmf.aspectmodel.RdfUtil;
import org.eclipse.esmf.metamodel.datatype.SammType;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.ParsedDocument;

import org.eclipse.lsp4j.SemanticTokenModifiers;
import org.eclipse.lsp4j.SemanticTokenTypes;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensLegend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service that maps parser tokens to LSP semantic tokens
 */
public class TurtleTokenService {
   private static final Logger LOG = LoggerFactory.getLogger( TurtleTokenService.class );
   public static final SemanticTokensLegend SUPPORTED_TOKEN_TYPES = new SemanticTokensLegend(
         List.of(
               SemanticTokenTypes.Type,
               SemanticTokenTypes.Comment,
               SemanticTokenTypes.Keyword,
               SemanticTokenTypes.String,
               SemanticTokenTypes.Class,
               SemanticTokenTypes.Number,
               SemanticTokenTypes.Decorator,
               SemanticTokenTypes.Function,
               SemanticTokenTypes.Property
         ),
         List.of(
               SemanticTokenModifiers.DefaultLibrary,
               SemanticTokenModifiers.Deprecated
         )
   );

   private final Map<String, Integer> tokenTypeIds = IntStream.range( 0, SUPPORTED_TOKEN_TYPES.getTokenTypes().size() )
         .boxed()
         .collect( Collectors.toMap( i -> SUPPORTED_TOKEN_TYPES.getTokenTypes().get( i ), Function.identity() ) );
   private final Map<String, Integer> tokenModifierTypeIds = IntStream.range( 0, SUPPORTED_TOKEN_TYPES.getTokenModifiers().size() )
         .boxed()
         .collect( Collectors.toMap( i -> SUPPORTED_TOKEN_TYPES.getTokenModifiers().get( i ), Function.identity() ) );

   public TurtleTokenService() {}

   /**
    * Represents a single token over a given range
    *
    * @param location the location of the token
    * @param tokenType the token type
    * @param tokenModifiers the token modifiers bit set
    */
   private record TokenRange(
         Location location,
         String content,
         int tokenType,
         int tokenModifiers
   ) {}

   /**
    * Builds the SemanticTokens for a Document
    *
    * @param document the document
    */
   public SemanticTokens buildSemanticTokens( final ParsedDocument document ) {
      final List<TokenRange> tokenRanges = new ArrayList<>();
      final Deque<TurtleSyntaxTree.Node> nodes = new ArrayDeque<>();
      TurtleSyntaxTree.Node node;
      nodes.push( document.turtleSyntaxTree().rootNode() );
      while ( !nodes.isEmpty() ) {
         node = nodes.pop();
         node.children().forEach( nodes::push );

         final int tokenId = tokenIdForNode( node );
         if ( tokenId == -1 ) {
            continue;
         }

         final Location location = node.location();
         final TokenRange tokenRange = new TokenRange( location,
               document.sourceDocument().subSequence( location.fromLine(), location.fromColumn(), location.toLine(), location.toColumn() ),
               tokenId, tokenModifierBitSetForNode( node ) );
         tokenRanges.addAll( splitIntoSingleLineTokens( tokenRange ) );
      }

      return buildSemanticTokens( tokenRanges, document.sourceDocument() );
   }

   private List<TokenRange> splitIntoSingleLineTokens( final TokenRange tokenRange ) {
      if ( !tokenRange.content().contains( "\n" ) ) {
         return List.of( tokenRange );
      }

      final List<TokenRange> result = new ArrayList<>();
      final String[] lines = tokenRange.content().split( "\n", -1 );
      int currentLine = tokenRange.location().fromLine();
      int currentColumn = tokenRange.location().fromColumn();

      for ( int i = 0; i < lines.length; i++ ) {
         final String lineContent = lines[i];
         if ( i == lines.length - 1 && lineContent.isEmpty() ) {
            // Skip the last empty line if content ends with \n
            break;
         }

         final Location lineLocation = new Location( currentLine, currentColumn, currentLine, currentColumn + lineContent.length() );
         result.add( new TokenRange( lineLocation, lineContent, tokenRange.tokenType(), tokenRange.tokenModifiers() ) );
         currentLine++;
         currentColumn = 0;
      }

      return result;
   }

   /**
    * Builds the SemanticTokens for the given list of token ranges. In LSP, this is described as a list
    * of integers.
    *
    * @param tokenRanges the input list of token ranges
    * @see <a href=
    *      "https://microsoft.github.io/language-server-protocol/specifications/lsp/3.17/specification/#textDocument_semanticTokens">Semantic
    *      Tokens at LSP specification</a>
    * @return the SemanticTokens representation
    */
   private SemanticTokens buildSemanticTokens( final List<TokenRange> tokenRanges, final Document document ) {
      final ToIntFunction<TokenRange> lineExtractor = tokenRange -> tokenRange.location().fromLine();
      final ToIntFunction<TokenRange> columnExtractor = tokenRange -> tokenRange.location().fromColumn();
      tokenRanges.sort( Comparator.comparingInt( lineExtractor ).thenComparingInt( columnExtractor ) );
      final List<Integer> data = new ArrayList<>();
      int lastLine = -1;
      int lastColumn = -1;
      for ( final TokenRange tokenRange : tokenRanges ) {
         final int line = tokenRange.location().fromLine();
         final int column = tokenRange.location().fromColumn();
         if ( lastLine == -1 ) {
            data.add( line );
            data.add( column );
         } else {
            data.add( line - lastLine );
            data.add( lastLine == line ? column - lastColumn : column );
         }
         final int length = document.subSequence( tokenRange.location().fromLine(), tokenRange.location().fromColumn(),
               tokenRange.location().toLine(), tokenRange.location().toColumn() ).length();
         data.add( length );
         data.add( tokenRange.tokenType() );
         data.add( tokenRange.tokenModifiers() );
         lastLine = line;
         lastColumn = column;
      }
      return new SemanticTokens( data );
   }

   /**
    * Returns the tokenId for a given parser node, i.e., the index of the type of token in the
    * SemanticTokenLegends.tokenTypes
    *
    * @param node the parser node
    * @see TurtleTokenService#SUPPORTED_TOKEN_TYPES
    * @return the corresponding tokenId
    */
   private int tokenIdForNode( final TurtleSyntaxTree.Node node ) {
      final String semanticToken = switch ( node.type() ) {
         case ParserTokenType.COMMENT -> SemanticTokenTypes.Comment;
         case ParserTokenType.AT_BASE -> SemanticTokenTypes.Keyword;
         case ParserTokenType.AT_PREFIX -> SemanticTokenTypes.Keyword;
         case ParserTokenType.SPARQL_BASE -> SemanticTokenTypes.Keyword;
         case ParserTokenType.SPARQL_PREFIX -> SemanticTokenTypes.Keyword;
         case ParserTokenType.A -> SemanticTokenTypes.Keyword;
         case ParserTokenType.STRING -> SemanticTokenTypes.String;
         case ParserTokenType.INTEGER -> SemanticTokenTypes.Number;
         case ParserTokenType.DECIMAL -> SemanticTokenTypes.Number;
         case ParserTokenType.DOUBLE -> SemanticTokenTypes.Number;
         case ParserTokenType.BOOLEAN_LITERAL -> SemanticTokenTypes.Keyword;
         case ParserTokenType.LANG_TAG -> SemanticTokenTypes.Decorator;
         case ParserTokenType.PN_PREFIX -> SemanticTokenTypes.Function;
         case ParserTokenType.PN_LOCAL -> SemanticTokenTypes.Property;
         case ParserTokenType.SYMBOL_DOUBLE_CARET -> SemanticTokenTypes.Decorator;
         case ParserTokenType.SYMBOL_FULL_STOP -> SemanticTokenTypes.Decorator;
         case ParserTokenType.SYMBOL_SEMICOLON -> SemanticTokenTypes.Decorator;
         case ParserTokenType.RDF_LITERAL -> {
            if ( node.childWithType( ParserTokenType.SYMBOL_DOUBLE_CARET ).isPresent() ) {
               // Typed literal
               yield node.childWithType( ParserTokenType.PREFIXED_NAME )
                     .map( TurtleSyntaxTree.Node::content )
                     .map( RdfUtil::fullUri )
                     .map( typeUri -> SammType.forUri( typeUri )
                           .filter( sammType -> sammType instanceof SammType.NumericType<?> )
                           .map( _ -> SemanticTokenTypes.Number )
                           .orElse( SemanticTokenTypes.String ) )
                     .orElse( "" );
            } else if ( node.childWithType( ParserTokenType.LANG_TAG ).isPresent() ) {
               // rdf:langString
               yield SemanticTokenTypes.String;
            } else {
               // plain string
               yield SemanticTokenTypes.String;
            }
         }
         default -> "";
      };
      if ( semanticToken.isEmpty() ) {
         return -1;
      }

      final Integer semanticTokenId = tokenTypeIds.get( semanticToken );
      if ( semanticTokenId == null ) {
         LOG.error( "Trying to return unsupported token type for parser type {}", semanticToken );
         return -1;
      }
      return semanticTokenId;
   }

   private int tokenModifierBitSetForNode( final TurtleSyntaxTree.Node node ) {
      int bitSet = 0;
      if ( node.type().equals( ParserTokenType.PN_PREFIX ) ) {
         final String token = node.content();
         if ( token.equals( SammNs.SAMM.getShortForm() ) || token.equals( SammNs.SAMMC.getShortForm() ) ) {
            bitSet = bitSet | ( 1 << tokenModifierTypeIds.get( SemanticTokenModifiers.DefaultLibrary ) );
         }
      }
      return bitSet;
   }
}
