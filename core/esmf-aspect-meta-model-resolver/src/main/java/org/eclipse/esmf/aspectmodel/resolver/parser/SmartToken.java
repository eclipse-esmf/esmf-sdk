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

import java.util.Optional;

import org.apache.jena.riot.tokens.Token;
import org.apache.jena.riot.tokens.TokenType;

/**
 * Wrapper class for a {@link Token}. This provides access to the actual string representation of the token, 1-based line and
 * column information.
 *
 * @param token the token
 */
public record SmartToken( Token token ) {
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

   /**
    * Format the content of the token in a structured manner.
    *
    * @param formatter formatter to use for text formatting
    * @return the length of the UNFORMATTED content
    */
   public int structureContent( final RdfTextFormatter formatter ) {
      switch ( token.getType() ) {
         case IRI -> formatter.formatPrimitive( "<" ).formatIri( token.getImage() ).formatPrimitive( ">" );
         case DIRECTIVE -> formatter.formatPrimitive( "@" ).formatDirective( token.getImage() );
         case PREFIXED_NAME -> formatter.formatPrefix( Optional.ofNullable( token.getImage() ).orElse( "" ) )
               .formatPrimitive( ":" )
               .formatName( Optional.ofNullable( token.getImage2() ).orElse( "" ) );
         case LT -> formatter.formatPrimitive( "<" );
         case GT -> formatter.formatPrimitive( ">" );
         case LE -> formatter.formatPrimitive( "<=" );
         case GE -> formatter.formatPrimitive( ">=" );
         case LOGICAL_AND -> formatter.formatPrimitive( "&&" );
         case LOGICAL_OR -> formatter.formatPrimitive( "||" );
         case LT2 -> formatter.formatPrimitive( "<<" );
         case GT2 -> formatter.formatPrimitive( ">>" );
         case DOT -> formatter.formatPrimitive( "." );
         case COMMA -> formatter.formatPrimitive( "," );
         case SEMICOLON -> formatter.formatPrimitive( ";" );
         case LBRACE -> formatter.formatPrimitive( "{" );
         case RBRACE -> formatter.formatPrimitive( "}" );
         case LPAREN -> formatter.formatPrimitive( "(" );
         case RPAREN -> formatter.formatPrimitive( ")" );
         case LBRACKET -> formatter.formatPrimitive( "[" );
         case RBRACKET -> formatter.formatPrimitive( "]" );
         case EQUALS -> formatter.formatPrimitive( "=" );
         case EQUIVALENT -> formatter.formatPrimitive( "==" );
         case PLUS -> formatter.formatPrimitive( "+" );
         case MINUS -> formatter.formatPrimitive( "-" );
         case STAR -> formatter.formatPrimitive( "*" );
         case SLASH -> formatter.formatPrimitive( "/" );
         case RSLASH -> formatter.formatPrimitive( "\\" );
         case STRING -> formatter.formatPrimitive( "\"" ).formatString( token.getImage() ).formatPrimitive( "\"" );
         case LITERAL_LANG -> formatter.formatPrimitive( "\"" ).formatString( token.getImage() ).formatPrimitive( "\"" )
               .formatPrimitive( "@" ).formatLangTag( token.getImage2() );
         case LITERAL_DT ->
               formatter.formatPrimitive( "\"" ).formatString( token.getImage() ).formatPrimitive( "\"" ).formatPrimitive( "^^" )
                     .formatPrefix( token.getSubToken2().getImage() ).formatPrimitive( ":" ).formatName( token.getSubToken2().getImage2() );
         default -> formatter.formatDefault( token.getImage() );
      }

      return content().length();
   }
}
