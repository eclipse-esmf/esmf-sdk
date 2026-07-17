/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.resolver.parser;

import java.util.Objects;
import java.util.Optional;

import org.apache.jena.riot.tokens.Token;
import org.apache.jena.riot.tokens.TokenType;

import org.eclipse.esmf.Location;
import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;

import org.jspecify.annotations.Nullable;

/**
 * Wrapper class for a {@link Token}. This provides access to the actual string representation of
 * the token, 1-based line and column information.
 */
public final class SmartToken {
   private final Token jenaToken;
   private final TurtleSyntaxTree.Token treesitterToken;
   private AspectModelFile originatingFile;

   /**
    * @param jenaToken the token
    */
   public SmartToken( final Token jenaToken ) {
      this.jenaToken = jenaToken;
      treesitterToken = null;
   }

   public SmartToken( final TurtleSyntaxTree.Token treesitterToken ) {
      jenaToken = null;
      this.treesitterToken = treesitterToken;
   }

   public @Nullable TokenType type() {
      return jenaToken.getType();
   }

   /**
    * The lexical representation, see also {@link Token#getImage()}.
    *
    * @return the lexical representation
    */
   public String image() {
      if ( jenaToken != null ) {
         return jenaToken.getImage();
      }
      return treesitterToken.content();
   }

   /**
    * The lexical representation, see also {@link Token#getImage2()}.
    *
    * @return the lexical representation
    */
   public String image2() {
      if ( jenaToken != null ) {
         return jenaToken.getImage2();
      }
      return "";
   }

   public @Nullable Token subToken1() {
      if ( jenaToken != null ) {
         return jenaToken.getSubToken1();
      }
      return null;
   }

   public @Nullable Token subToken2() {
      if ( jenaToken != null ) {
         return jenaToken.getSubToken2();
      }
      return null;
   }

   @SuppressWarnings( "DataFlowIssue" )
   public Location location() {
      if ( treesitterToken != null ) {
         return treesitterToken.location();
      }
      return new Location( (int) jenaToken.getLine() - 1, (int) jenaToken.getColumn() - 1, (int) jenaToken.getLine() - 1,
            (int) jenaToken.getColumn() - 1 + content().length() );
   }

   /**
    * The line of the token, 1-based.
    *
    * @return the line
    */
   public int line() {
      return location().fromLine() + 1;
   }

   /**
    * The column of the token, 1-based.
    *
    * @return the column
    */
   public int column() {
      return location().fromColumn() + 1;
   }

   /**
    * Determines the actual content of a token. Although tokens do store their content, this is often
    * times simplified, e.g. a token with type DIRECTIVE will have the content "prefix" even though in
    * the actual source document, it's "@prefix". So this method will return the actual string content
    * for a token.
    *
    * @return the token's effective content
    */
   public String content() {
      if ( treesitterToken != null ) {
         return treesitterToken.content();
      }
      // For Jena, only an approximation of the token can be provided
      return switch ( jenaToken.getType() ) {
         case IRI -> "<" + jenaToken.getImage() + ">";
         case DIRECTIVE -> "@" + jenaToken.getImage();
         case PREFIXED_NAME ->
            Optional.ofNullable( jenaToken.getImage() ).orElse( "" ) + ":" + Optional.ofNullable( jenaToken.getImage2() ).orElse( "" );
         case LT -> "<";
         case GT -> ">";
         case LE -> "<=";
         case GE -> ">=";
         case LOGICAL_AND -> "&&";
         case LOGICAL_OR -> "||";
         case LT2 -> "<<";
         case GT2 -> ">>";
         case DOT -> ".";
         case COMMA -> ",";
         case SEMICOLON -> ";";
         case LBRACE -> "{";
         case RBRACE -> "}";
         case LPAREN -> "(";
         case RPAREN -> ")";
         case LBRACKET -> "[";
         case RBRACKET -> "]";
         case EQUALS -> "=";
         case EQUIVALENT -> "==";
         case PLUS -> "+";
         case MINUS -> "-";
         case STAR -> "*";
         case SLASH -> "/";
         case RSLASH -> "\\";
         case STRING -> "\"" + jenaToken.getImage() + "\"";
         case LITERAL_LANG -> String.format( "\"%s\"@%s", jenaToken.getImage(), jenaToken.getImage2() );
         case LITERAL_DT ->
            String.format( "\"%s\"^^%s:%s", jenaToken.getImage(), jenaToken.getSubToken2().getImage(),
                  jenaToken.getSubToken2().getImage2() );
         default -> jenaToken.getImage();
      };
   }

   public Token token() {
      return jenaToken;
   }

   @Override
   public boolean equals( final Object obj ) {
      if ( obj == this ) {
         return true;
      }
      if ( obj == null || obj.getClass() != getClass() ) {
         return false;
      }
      final var that = (SmartToken) obj;
      return jenaToken != null
            ? Objects.equals( jenaToken, that.jenaToken )
            : Objects.equals( treesitterToken, that.treesitterToken );
   }

   @Override
   public int hashCode() {
      return Objects.hash( jenaToken );
   }

   @Override
   public String toString() {
      return jenaToken != null
            ? "SmartToken[jenaToken=" + jenaToken + ']'
            : "SmartToken[treesitterToken=" + treesitterToken + ']';
   }

   public Token getJenaToken() {
      return jenaToken;
   }

   public TurtleSyntaxTree.Token getTreesitterToken() {
      return treesitterToken;
   }

   public AspectModelFile getOriginatingFile() {
      return originatingFile;
   }

   public void setOriginatingFile( final AspectModelFile originatingFile ) {
      this.originatingFile = originatingFile;
   }
}
