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

package org.eclipse.esmf.aspectmodel.resolver.exceptions;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.riot.RiotException;

/**
 * Represents the context information of a parser (syntax) error: The location, source document and description
 */
public class ParserException extends RuntimeException {
   /**
    * The pattern to parse {@link RiotException}'s messages.
    * RiotException's message looks like this:
    * [line: 17, col: 2 ] Triples not terminated by DOT
    */
   private static final Pattern PATTERN = Pattern.compile( "\\[ *line: *(\\d+), *col: *(\\d+) *] *(.*)" );
   private final String sourceDocument;
   private final long line;
   private final long column;

   /**
    * Constructor: Parse a RiotException
    *
    * @param exception the cause
    * @param sourceDocument the source document
    */
   public ParserException( final RiotException exception, final String sourceDocument ) {
      super( getRiotMessage( exception.getMessage() ).orElse( "Syntax error: " + exception.getMessage() ), exception );
      this.sourceDocument = sourceDocument;
      final Matcher matcher = PATTERN.matcher( exception.getMessage() );
      if ( matcher.find() ) {
         line = Long.parseLong( matcher.group( 1 ) );
         column = Long.parseLong( matcher.group( 2 ) );
      } else {
         line = -1;
         column = -1;
      }
   }

   /**
    * Constructor: Create a parser exception when the problem location is already known
    *
    * @param line the line of the parser problem
    * @param column the column of the parser problem
    * @param message the message
    * @param sourceDocument the source document
    */
   public ParserException( final long line, final long column, final String message, final String sourceDocument ) {
      super( message );
      this.sourceDocument = sourceDocument;
      this.line = line;
      this.column = column;
   }

   private static Optional<String> getRiotMessage( final String riotMessage ) {
      final Matcher matcher = PATTERN.matcher( riotMessage );
      if ( matcher.find() ) {
         return Optional.of( matcher.group( 3 ) );
      }
      return Optional.empty();
   }

   public String getSourceDocument() {
      return sourceDocument;
   }

   public long getLine() {
      return line;
   }

   public long getColumn() {
      return column;
   }
}
