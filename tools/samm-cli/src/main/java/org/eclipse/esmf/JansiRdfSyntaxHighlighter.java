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

package org.eclipse.esmf;

import static org.fusesource.jansi.Ansi.ansi;

import org.eclipse.esmf.aspectmodel.resolver.parser.RdfTextFormatter;

import org.fusesource.jansi.Ansi;

/**
 * RDF syntax highlighting using JAnsi format, usable in JAnsi-enhanced environments.
 */
public class JansiRdfSyntaxHighlighter implements RdfTextFormatter {
   private static final Ansi.Color IRI_COLOR = Ansi.Color.GREEN;
   private static final Ansi.Color ERROR_COLOR = Ansi.Color.RED;
   private static final Ansi.Color PREFIX_COLOR = Ansi.Color.BLUE;
   private static final Ansi.Color DIRECTIVE_COLOR = Ansi.Color.YELLOW;
   private static final Ansi.Color STRING_COLOR = Ansi.Color.MAGENTA;
   private static final Ansi.Color LANG_COLOR = Ansi.Color.CYAN;

   private final StringBuilder builder = new StringBuilder();

   @Override
   public void reset() {
      builder.setLength( 0 );
   }

   @Override
   public String getResult() {
      return builder.toString();
   }

   @Override
   public RdfTextFormatter formatIri( final String iri ) {
      builder.append( coloredFragment( IRI_COLOR, iri ) );
      return this;
   }

   @Override
   public RdfTextFormatter formatDirective( final String directive ) {
      builder.append( coloredFragment( DIRECTIVE_COLOR, directive ) );
      return this;
   }

   @Override
   public RdfTextFormatter formatPrefix( final String prefix ) {
      builder.append( coloredFragment( PREFIX_COLOR, prefix ) );
      return this;
   }

   @Override
   public RdfTextFormatter formatName( final String name ) {
      builder.append( ansi().fgBright( PREFIX_COLOR ).a( name ).reset().toString() );
      return this;
   }

   @Override
   public RdfTextFormatter formatString( final String string ) {
      builder.append( coloredFragment( STRING_COLOR, string ) );
      return this;
   }

   @Override
   public RdfTextFormatter formatLangTag( final String langTag ) {
      builder.append( coloredFragment( LANG_COLOR, langTag ) );
      return this;
   }

   @Override
   public RdfTextFormatter formatDefault( final String text ) {
      builder.append( ansi().a( text ).toString() );
      return this;
   }

   @Override
   public RdfTextFormatter formatPrimitive( final String primitive ) {
      switch ( primitive ) {
         case "<" -> builder.append( coloredFragment( IRI_COLOR, primitive ) );
         case ">" -> builder.append( coloredFragment( IRI_COLOR, primitive ) );
         case ":" -> builder.append( coloredFragment( PREFIX_COLOR, primitive ) );
         case "@" -> builder.append( coloredFragment( DIRECTIVE_COLOR, primitive ) );
         case "\"" -> builder.append( coloredFragment( STRING_COLOR, primitive ) );
         case "(" -> builder.append( coloredFragment( DIRECTIVE_COLOR, primitive ) );
         case ")" -> builder.append( coloredFragment( DIRECTIVE_COLOR, primitive ) );
         case "a" -> builder.append( coloredFragment( STRING_COLOR, primitive ) );
         default -> builder.append( ansi().a( primitive ).toString() );
      }
      return this;
   }

   @Override
   public RdfTextFormatter formatError( final String text ) {
      builder.append( coloredFragment( ERROR_COLOR, text ) );
      return this;
   }

   private String coloredFragment( final Ansi.Color color, final String fragment ) {
      return ansi().fg( color ).a( fragment ).reset().toString();
   }
}
