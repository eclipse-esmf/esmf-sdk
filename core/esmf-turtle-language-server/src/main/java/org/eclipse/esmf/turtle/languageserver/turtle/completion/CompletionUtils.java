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

package org.eclipse.esmf.turtle.languageserver.turtle.completion;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.lsp4j.Position;

/** Stateless helpers shared by the completion classes that are not tied to a single one of them. */
public final class CompletionUtils {

   private CompletionUtils() {}

   static Position positionAt( final String source, final int offset ) {
      int line = 0;
      int lastNewline = -1;
      for ( int i = 0; i < offset; i++ ) {
         if ( source.charAt( i ) == '\n' ) {
            line++;
            lastNewline = i;
         }
      }
      return new Position( line, offset - lastNewline - 1 );
   }

   static String expand( final String text, final Map<String, String> namespaces ) {
      if ( text.startsWith( "<" ) && text.endsWith( ">" ) ) {
         return text.substring( 1, text.length() - 1 );
      }
      final int colon = text.indexOf( ':' );
      if ( colon >= 0 ) {
         final String namespace = namespaces.get( text.substring( 0, colon + 1 ) );
         if ( namespace != null ) {
            return namespace + text.substring( colon + 1 );
         }
      }
      return "";
   }

   /**
    * IRI -> prefixed name using the well-known namespaces, e.g. samm:characteristic
    */
   public static Optional<String> prefixedName( final String iri ) {
      return prefixedName( TurtleCursor.withLatestSammNamespace( iri ),
            SammNs.wellKnownNamespaces().map( namespace -> Map.entry( namespace.getShortForm() + ":", namespace.getNamespace() ) ) );
   }

   /**
    * The prefixed name of an IRI using the declared namespaces, or {@code unmatched} applied to the
    * IRI if none matches
    */
   public static String toPrefixedName( final String iri, final Map<String, String> namespaces, final UnaryOperator<String> unmatched ) {
      return prefixedName( iri, namespaces.entrySet().stream() ).orElseGet( () -> unmatched.apply( iri ) );
   }

   /**
    * The prefixed name of an IRI, using the prefix with the longest matching namespace, or empty if
    * none matches
    */
   private static Optional<String> prefixedName( final String iri, final Stream<Map.Entry<String, String>> prefixes ) {
      return prefixes
            .filter( entry -> !entry.getValue().isEmpty() && iri.startsWith( entry.getValue() ) )
            .max( Comparator.comparingInt( entry -> entry.getValue().length() ) )
            .map( entry -> entry.getKey() + iri.substring( entry.getValue().length() ) );
   }
}
