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

import org.eclipse.esmf.aspectmodel.AspectModelFile;

import org.apache.jena.riot.tokens.Token;
import org.apache.jena.riot.tokens.TokenType;

/**
 * Wrapper class for a {@link Token}. This provides access to the actual string representation of the token, 1-based line and
 * column information.
 */
public final class SmartToken {
   private final Token token;
   private AspectModelFile originatingFile;

   /**
    * @param token the token
    */
   public SmartToken( final Token token ) {
      this.token = token;
   }

   public TokenType type() {
      return token.getType();
   }

   /**
    * The lexical representation, see also {@link Token#getImage()}.
    *
    * @return the lexical representation
    */
   public String image() {
      return token.getImage();
   }

   /**
    * The lexical representation, see also {@link Token#getImage2()}.
    *
    * @return the lexical representation
    */
   public String image2() {
      return token.getImage2();
   }

   public Token subToken1() {
      return token.getSubToken1();
   }

   public Token subToken2() {
      return token.getSubToken2();
   }

   /**
    * The line of the token, 1-based.
    *
    * @return the line
    */
   public int line() {
      return (int) token.getLine();
   }

   /**
    * The column of the token, 1-based. *
    *
    * @return the column
    */
   public int column() {
      return (int) token.getColumn();
   }

   /**
    * Determines the actual content of a token. Although tokens do store their content, this is often times simplified, e.g. a
    * token with type DIRECTIVE will have the content "prefix" even though in the actual source document, it's "@prefix".
    * So this method will return the actual string content for a token.
    *
    * @return the token's effective content
    */
   public String content() {
      return switch ( token.getType() ) {
         case IRI -> "<" + token.getImage() + ">";
         case DIRECTIVE -> "@" + token.getImage();
         case PREFIXED_NAME ->
               Optional.ofNullable( token.getImage() ).orElse( "" ) + ":" + Optional.ofNullable( token.getImage2() ).orElse( "" );
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
         case STRING -> "\"" + token.getImage() + "\"";
         case LITERAL_LANG -> String.format( "\"%s\"@%s", token.getImage(), token.getImage2() );
         case LITERAL_DT ->
               String.format( "\"%s\"^^%s:%s", token.getImage(), token.getSubToken2().getImage(), token.getSubToken2().getImage2() );
         default -> token.getImage();
      };
   }

   public Token token() {
      return token;
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
      return Objects.equals( token, that.token );
   }

   @Override
   public int hashCode() {
      return Objects.hash( token );
   }

   @Override
   public String toString() {
      return "SmartToken[" +
            "token=" + token + ']';
   }

   public Token getToken() {
      return token;
   }

   public AspectModelFile getOriginatingFile() {
      return originatingFile;
   }

   public void setOriginatingFile( final AspectModelFile originatingFile ) {
      this.originatingFile = originatingFile;
   }
}
