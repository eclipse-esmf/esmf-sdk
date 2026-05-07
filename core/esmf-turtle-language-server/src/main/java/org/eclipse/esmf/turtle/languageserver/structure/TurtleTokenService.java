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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.turtle.languageserver.lsp.text.Document;
import org.eclipse.esmf.turtle.languageserver.lsp.text.TreeSitterTurtleParserService;

import org.eclipse.lsp4j.SemanticTokenModifiers;
import org.eclipse.lsp4j.SemanticTokenTypes;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensLegend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treesitter.TSNode;
import org.treesitter.TSTree;

import com.google.common.collect.ImmutableMap;

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

   private static final Map<String, String> PARSER_TOKEN_TO_SEMANTIC_TOKEN = ImmutableMap.<String, String>builder()
         .put( ParserTokenType.COMMENT, SemanticTokenTypes.Comment )
         .put( ParserTokenType.AT_BASE, SemanticTokenTypes.Keyword )
         .put( ParserTokenType.AT_PREFIX, SemanticTokenTypes.Keyword )
         .put( ParserTokenType.SPARQL_BASE, SemanticTokenTypes.Keyword )
         .put( ParserTokenType.SPARQL_PREFIX, SemanticTokenTypes.Keyword )
         .put( ParserTokenType.A, SemanticTokenTypes.Keyword )
         .put( ParserTokenType.STRING, SemanticTokenTypes.String )
         .put( ParserTokenType.INTEGER, SemanticTokenTypes.Number )
         .put( ParserTokenType.DECIMAL, SemanticTokenTypes.Number )
         .put( ParserTokenType.DOUBLE, SemanticTokenTypes.Number )
         .put( ParserTokenType.BOOLEAN_LITERAL, SemanticTokenTypes.Keyword )
         .put( ParserTokenType.LANG_TAG, SemanticTokenTypes.Decorator )
         .put( ParserTokenType.PN_PREFIX, SemanticTokenTypes.Function )
         .put( ParserTokenType.PN_LOCAL, SemanticTokenTypes.Property )
         .put( ParserTokenType.SYMBOL_DOUBLE_CARET, SemanticTokenTypes.Decorator )
         .put( ParserTokenType.SYMBOL_DOT, SemanticTokenTypes.Decorator )
         .put( ParserTokenType.SYMBOL_SEMICOLON, SemanticTokenTypes.Decorator )
         .build();
   private final TreeSitterTurtleParserService parserService;

   private final Map<String, Integer> tokenTypeIds = IntStream.range( 0, SUPPORTED_TOKEN_TYPES.getTokenTypes().size() )
         .boxed()
         .collect( Collectors.toMap( i -> SUPPORTED_TOKEN_TYPES.getTokenTypes().get( i ), Function.identity() ) );
   private final Map<String, Integer> tokenModifierTypeIds = IntStream.range( 0, SUPPORTED_TOKEN_TYPES.getTokenModifiers().size() )
         .boxed()
         .collect( Collectors.toMap( i -> SUPPORTED_TOKEN_TYPES.getTokenModifiers().get( i ), Function.identity() ) );

   public TurtleTokenService( final TreeSitterTurtleParserService parserService ) {
      this.parserService = parserService;
   }

   /**
    * Represents a single token over a given range
    *
    * @param line the line where the token appears
    * @param column the column in the line
    * @param length the length of the token in characters
    * @param tokenType the token type
    * @param tokenModifiers the token modifiers bit set
    */
   private record TokenRange(
         int line,
         int column,
         int length,
         int tokenType,
         int tokenModifiers
   ) {}

   /**
    * Builds the SemanticTokens for a Document
    *
    * @param document the document
    */
   public SemanticTokens buildSemanticTokens( final Document document ) {
      final List<TokenRange> tokenRanges = new ArrayList<>();
      final TSTree concreteSyntaxTree = parserService.apply( document ).concreteSyntaxTree();
      final Deque<TSNode> nodes = new ArrayDeque<>();
      TSNode node;
      nodes.push( concreteSyntaxTree.getRootNode() );
      while ( !nodes.isEmpty() ) {
         node = nodes.pop();
         for ( int i = 0; i < node.getChildCount(); i++ ) {
            nodes.push( node.getChild( i ) );
         }

         final int tokenId = tokenIdForNode( node );
         if ( tokenId == -1 ) {
            continue;
         }

         final int line = node.getStartPoint().getRow();
         final int column = node.getStartPoint().getColumn();
         final int length = node.getEndByte() - node.getStartByte();
         tokenRanges.add( new TokenRange( line, column, length, tokenId, tokenModifierBitSetForNode( node, document ) ) );
      }

      return buildSemanticTokens( tokenRanges );
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
   private SemanticTokens buildSemanticTokens( final List<TokenRange> tokenRanges ) {
      tokenRanges.sort( Comparator.comparingInt( TokenRange::line ).thenComparingInt( TokenRange::column ) );
      final List<Integer> data = new ArrayList<>();
      int lastLine = -1;
      int lastColumn = -1;
      for ( final TokenRange tokenRange : tokenRanges ) {
         final int line = tokenRange.line();
         final int column = tokenRange.column();
         if ( lastLine == -1 ) {
            data.add( line );
            data.add( column );
         } else {
            data.add( line - lastLine );
            data.add( lastLine == line ? column - lastColumn : column );
         }
         data.add( tokenRange.length() );
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
   private int tokenIdForNode( final TSNode node ) {
      final String semanticToken = PARSER_TOKEN_TO_SEMANTIC_TOKEN.get( node.getGrammarType() );
      if ( semanticToken == null ) {
         return -1;
      }

      final Integer semanticTokenId = tokenTypeIds.get( semanticToken );
      if ( semanticTokenId == null ) {
         LOG.error( "Trying to return unsupported token type for parser type {}", semanticToken );
         return -1;
      }
      return semanticTokenId;
   }

   private int tokenModifierBitSetForNode( final TSNode node, final Document document ) {
      int bitSet = 0;
      if ( node.getGrammarType().equals( ParserTokenType.PN_PREFIX ) ) {
         final String token = document.subSequence( node.getStartPoint().getRow(), node.getStartPoint().getColumn(),
               node.getEndPoint().getRow(), node.getEndPoint().getColumn() );
         if ( token.equals( SammNs.SAMM.getShortForm() ) || token.equals( SammNs.SAMMC.getShortForm() ) ) {
            bitSet = bitSet | ( 1 << tokenModifierTypeIds.get( SemanticTokenModifiers.DefaultLibrary ) );
         }
      }
      return bitSet;
   }
}
