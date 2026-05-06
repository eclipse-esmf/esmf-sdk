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
               SemanticTokenTypes.Function
         ),
         List.of( SemanticTokenModifiers.DefaultLibrary )
   );

   private static final Map<String, String> PARSER_TOKEN_TO_SEMANTIC_TOKEN = ImmutableMap.<String, String>builder()
         .put( ParserTokenType.COMMENT, SemanticTokenTypes.Comment )
         .put( ParserTokenType.AT_BASE, SemanticTokenTypes.Keyword )
         .put( ParserTokenType.AT_PREFIX, SemanticTokenTypes.Keyword )
         .put( ParserTokenType.SPARQL_BASE, SemanticTokenTypes.Keyword )
         .put( ParserTokenType.SPARQL_PREFIX, SemanticTokenTypes.Keyword )
         .put( ParserTokenType.STRING, SemanticTokenTypes.String )
         .put( ParserTokenType.IRI_REFERENCE, SemanticTokenTypes.Class )
         .put( ParserTokenType.INTEGER, SemanticTokenTypes.Number )
         .put( ParserTokenType.DECIMAL, SemanticTokenTypes.Number )
         .put( ParserTokenType.DOUBLE, SemanticTokenTypes.Number )
         .put( ParserTokenType.RDF_LITERAL, SemanticTokenTypes.String )
         .put( ParserTokenType.BOOLEAN_LITERAL, SemanticTokenTypes.Keyword )
         .put( ParserTokenType.PREFIXED_NAME, SemanticTokenTypes.Class )
         .put( ParserTokenType.LANG_TAG, SemanticTokenTypes.Decorator )
         .put( ParserTokenType.PN_PREFIX, SemanticTokenTypes.Function )
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

   public SemanticTokens buildSemanticTokens( final Document document ) {
      final SemanticTokens semanticTokens = new SemanticTokens();
      final List<Integer> data = new ArrayList<>();
      semanticTokens.setData( data );

      final TSTree abstractSyntaxTree = parserService.getAbstractSyntaxTree( document );
      final Deque<TSNode> nodes = new ArrayDeque<>();
      TSNode node;
      nodes.push( abstractSyntaxTree.getRootNode() );
      int lastLine = -1;
      int lastColumn = -1;
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
         final int length = node.getEndByte() - node.getEndByte();
         if ( lastLine == -1 ) {
            data.add( line );
            data.add( column );
         } else {
            data.add( (int) ( line - lastLine ) );
            data.add( (int) ( lastLine == line ? column - lastColumn : column ) );
         }
         data.add( length );
         data.add( tokenId );
         data.add( tokenModifierBitSetForNode( node, document ) );
         lastLine = line;
         lastColumn = column;
      }

      return semanticTokens;
   }

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
