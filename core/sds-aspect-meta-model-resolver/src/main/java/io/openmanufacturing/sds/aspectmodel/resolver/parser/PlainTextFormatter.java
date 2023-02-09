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

package io.openmanufacturing.sds.aspectmodel.resolver.parser;

/**
 * Special implementation of the formatter, always returning plain text. Useful in unit tests and on systems which do not support coloring text.
 */
public class PlainTextFormatter implements IRdfTextFormatter {

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
   public IRdfTextFormatter formatIri( final String iri ) {
      builder.append( iri );
      return this;
   }

   @Override
   public IRdfTextFormatter formatDirective( final String directive ) {
      builder.append( directive );
      return this;
   }

   @Override
   public IRdfTextFormatter formatPrefix( final String prefix ) {
      builder.append( prefix );
      return this;
   }

   @Override
   public IRdfTextFormatter formatName( final String name ) {
      builder.append( name );
      return this;
   }

   @Override
   public IRdfTextFormatter formatString( final String string ) {
      builder.append( string );
      return this;
   }

   @Override
   public IRdfTextFormatter formatLangTag( final String langTag ) {
      builder.append( langTag );
      return this;
   }

   @Override
   public IRdfTextFormatter formatDefault( final String text ) {
      builder.append( text );
      return this;
   }

   @Override
   public IRdfTextFormatter formatPrimitive( final String primitive ) {
      builder.append( primitive );
      return this;
   }

   @Override
   public IRdfTextFormatter formatError( final String text ) {
      builder.append( text );
      return this;
   }
}
