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

   @Override
   public String formatIri( final String iri ) {
      return coloredFragment( IRI_COLOR, iri );
   }

   @Override
   public String formatDirective( final String directive ) {
      return coloredFragment( DIRECTIVE_COLOR, directive );
   }

   @Override
   public String formatPrefix( final String prefix ) {
      return coloredFragment( PREFIX_COLOR, prefix );
   }

   @Override
   public String formatName( final String name ) {
      return ansi().fgBright( PREFIX_COLOR ).a( name ).reset().toString();
   }

   @Override
   public String formatString( final String string ) {
      return coloredFragment( STRING_COLOR, string );
   }

   @Override
   public String formatLangTag( final String langTag ) {
      return coloredFragment( LANG_COLOR, langTag );
   }

   @Override
   public String formatDefault( final String text ) {
      return ansi().a( text ).toString();
   }

   @Override
   public String formatPrimitive( final String primitive ) {
      return switch ( primitive ) {
         case "<", ">" -> coloredFragment( IRI_COLOR, primitive );
         case ":" -> coloredFragment( PREFIX_COLOR, primitive );
         case "@", "(", ")" -> coloredFragment( DIRECTIVE_COLOR, primitive );
         case "\"", "a" -> coloredFragment( STRING_COLOR, primitive );
         default -> ansi().a( primitive ).toString();
      };
   }

   @Override
   public String formatError( final String text ) {
      return coloredFragment( ERROR_COLOR, text );
   }

   private String coloredFragment( final Ansi.Color color, final String fragment ) {
      return ansi().fg( color ).a( fragment ).reset().toString();
   }
}
