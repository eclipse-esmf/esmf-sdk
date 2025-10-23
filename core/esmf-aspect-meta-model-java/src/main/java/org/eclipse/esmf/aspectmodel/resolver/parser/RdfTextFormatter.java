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

/**
 * Interface to allow interested parties to do structured formatting of RDF documents (for example syntax highlighting).
 * Methods by default all return the original token.
 */
public interface RdfTextFormatter {
   default String formatIri( final String iri ) {
      return iri;
   }

   default String formatDirective( final String directive ) {
      return directive;
   }

   default String formatPrefix( final String prefix ) {
      return prefix;
   }

   default String formatName( final String name ) {
      return name;
   }

   default String formatString( final String string ) {
      return string;
   }

   default String formatLangTag( final String langTag ) {
      return langTag;
   }

   default String formatDefault( final String text ) {
      return text;
   }

   default String formatPrimitive( final String primitive ) {
      return primitive;
   }

   default String formatError( final String text ) {
      return text;
   }
}
