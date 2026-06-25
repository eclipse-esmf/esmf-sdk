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

package org.eclipse.esmf.treesitterturtle;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.esmf.Diagnostic;

import org.jspecify.annotations.Nullable;
import org.treesitter.TSNode;
import org.treesitter.TSTree;

/**
 * Represents the concrete syntax tree of a turtle document
 */
public class TurtleSyntaxTree {
   private final Supplier<String> sourceRepresentationSupplier;
   private final Node rootNode;

   public static class TurtleSyntaxTreeTraversalError extends RuntimeException {
   }

   public sealed interface Node {
      String type();

      Location location();

      default boolean isError() {
         return false;
      }

      default boolean isToken() {
         return !isError();
      }

      default String content() {
         throw new TurtleSyntaxTreeTraversalError();
      }

      default List<Node> children() {
         return List.of();
      }

      default Optional<Node> childWithType( final String type ) {
         return children().stream().filter( n -> n.type().equals( type ) ).findFirst();
      }

      default Token asToken() {
         throw new TurtleSyntaxTreeTraversalError();
      }

      default Error asError() {
         throw new TurtleSyntaxTreeTraversalError();
      }
   }

   /**
    * Represents one node in the concrete syntax tree in the source document
    *
    * @param type the type of token, expected to be one of the constants in {@link ParserTokenType}
    * @param content the actual content of the token
    * @param location the location in the source document
    * @param children children of the node
    */
   public record Token(
         String type,
         String content,
         Location location,
         List<Node> children
   ) implements Node {
      @Override
      public Token asToken() {
         return this;
      }

      @Override
      public String content() {
         return content;
      }
   }

   public record Error(
         String type,
         Diagnostic.Code errorType,
         Location location,
         List<Node> children
   ) implements Node {
      @Override
      public boolean isError() {
         return true;
      }

      @Override
      public List<Node> children() {
         return children;
      }

      @Override
      public Error asError() {
         return this;
      }

      @Override
      public String content() {
         return "ERROR";
      }
   }

   /**
    * Zero-based section of a document
    *
    * @param fromLine starting line
    * @param fromColumn starting column
    * @param toLine ending line
    * @param toColumn ending column
    */
   public record Location(
         int fromLine,
         int fromColumn,
         int toLine,
         int toColumn
   ) {}

   /**
    * Provides the token (substring) for a given location
    */
   public interface TokenProvider extends Function<Location, String> {
   }

   /**
    * TokenProvider implementation for an input document given as a string
    */
   public static class StringTokenProvider implements TokenProvider {
      private final String sourceDocument;

      public StringTokenProvider( final String sourceDocument ) {
         this.sourceDocument = sourceDocument;
      }

      @Override
      public String apply( final Location location ) {
         if ( sourceDocument == null || sourceDocument.isEmpty() ) {
            return "";
         }

         int startIndex = 0;
         int currentLine = 0;
         for ( int i = 0; i < sourceDocument.length() && currentLine < location.fromLine(); i++ ) {
            if ( sourceDocument.charAt( i ) == '\n' ) {
               currentLine++;
            }
            startIndex = i + 1;
         }
         startIndex += location.fromColumn();
         int endIndex = 0;
         currentLine = 0;

         for ( int i = 0; i < sourceDocument.length() && currentLine < location.toLine(); i++ ) {
            if ( sourceDocument.charAt( i ) == '\n' ) {
               currentLine++;
            }
            endIndex = i + 1;
         }
         endIndex += location.toColumn();
         startIndex = Math.clamp( startIndex, 0, sourceDocument.length() );
         endIndex = Math.clamp( endIndex, 0, sourceDocument.length() );

         if ( startIndex > endIndex ) {
            return "";
         }

         return sourceDocument.substring( startIndex, endIndex );
      }
   }

   private TurtleSyntaxTree( final Node rootNode, final Supplier<String> sourceRepresentationSupplier ) {
      this.rootNode = rootNode;
      this.sourceRepresentationSupplier = sourceRepresentationSupplier;
   }

   public Node rootNode() {
      return rootNode;
   }

   public Supplier<String> sourceRepresentationSupplier() {
      return sourceRepresentationSupplier;
   }

   public static TurtleSyntaxTree fromConcreteSyntaxTree( final TSTree syntaxTree, final Supplier<String> sourceRepresentationSupplier,
         final TokenProvider tokenProvider ) {
      return new TurtleSyntaxTree( nodeForTsNode( syntaxTree.getRootNode(), tokenProvider ), sourceRepresentationSupplier );
   }

   private static Node nodeForTsNode( final TSNode inputNode, final TokenProvider tokenProvider ) {
      final Location location = new Location(
            inputNode.getStartPoint().getRow(),
            inputNode.getStartPoint().getColumn(),
            inputNode.getEndPoint().getRow(),
            inputNode.getEndPoint().getColumn() );
      final List<Node> children = IntStream.range( 0, inputNode.getChildCount() )
            .mapToObj( inputNode::getChild )
            .filter( Objects::nonNull )
            .map( child -> nodeForTsNode( child, tokenProvider ) )
            .toList();
      if ( inputNode.isError() ) {
         return new Error( inputNode.getType(), TurtleDiagnostic.TurtleCode.E0003, location, children );
      } else if ( inputNode.isMissing() ) {
         return new Error( inputNode.getType(), TurtleDiagnostic.TurtleCode.E0004, location, children );
      }
      final String token = tokenProvider.apply( location );
      return new Token( inputNode.getType(), token, location, children );
   }

   private Stream<Node> nodes( final Node fromNode ) {
      final Stream<Node> children = fromNode.children().stream().flatMap( this::nodes );
      return Stream.concat( Stream.of( fromNode ), children )
            .sorted( Comparator.<Node>comparingInt( node -> node.location().fromLine() )
                  .thenComparingInt( node -> node.location().fromColumn() ) );
   }

   public Stream<Node> nodes() {
      return nodes( rootNode() );
   }

   public Stream<Token> tokens() {
      return nodes().filter( Token.class::isInstance ).map( Token.class::cast );
   }

   public TurtleSyntaxTree.@Nullable Token findMatchingTreeSitterToken( final long targetLine,
         final long targetColumn ) {
      return findMatchingTreeSitterToken( List.of(), targetLine, targetColumn );
   }

   /**
    * Finds the matching Tree-sitter token for a given Jena token based on a type filter, line and
    * column position.
    *
    * @param desiredTypes filter for this type of token
    * @param targetLine the line of the originating cursor position
    * @param targetColumn the column of the originating cursor position
    * @return the matching Tree-sitter token, or null if syntax tree is not available or no tokens
    *         found
    */
   public TurtleSyntaxTree.@Nullable Token findMatchingTreeSitterToken( final List<String> desiredTypes, final long targetLine,
         final long targetColumn ) {
      final List<Token> tokens = tokens().toList();
      if ( tokens.isEmpty() ) {
         return null;
      }

      TurtleSyntaxTree.Token shortestMatch = null;
      long minTokenLength = Long.MAX_VALUE;

      for ( final TurtleSyntaxTree.Token treeToken : tokens ) {
         if ( !desiredTypes.isEmpty() && !desiredTypes.contains( treeToken.type ) ) {
            continue;
         }

         final int tokenFromLine = treeToken.location().fromLine();
         final int tokenFromColumn = treeToken.location().fromColumn();
         final int tokenToLine = treeToken.location().toLine();
         final int tokenToColumn = treeToken.location().toColumn();

         if ( ( targetLine < tokenFromLine || targetLine > tokenToLine )
               || ( targetLine == tokenFromLine && targetColumn < tokenFromColumn )
               || ( targetLine == tokenToLine && targetColumn >= tokenToColumn ) ) {
            continue;
         }

         final long tokenLength = Math.abs( tokenFromLine - targetLine ) * 1000 + Math.abs( tokenFromColumn - targetColumn )
               + Math.abs( tokenToLine - targetLine ) * 1000 + Math.abs( tokenToColumn - targetColumn );

         if ( tokenLength < minTokenLength ) {
            minTokenLength = tokenLength;
            shortestMatch = treeToken;
         }
      }

      return shortestMatch;
   }
}
