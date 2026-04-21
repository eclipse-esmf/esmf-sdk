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

package org.eclipse.esmf.turtle.languageserver.turtle.navigation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class TurtlePrefixDefinitionService {
   private static final Pattern PREFIX_DECLARATION_PATTERN = Pattern.compile(
         "^\\s*@prefix\\s+([A-Za-z][A-Za-z0-9_-]*)?:\\s*<[^>]*>\\s*\\.",
         Pattern.CASE_INSENSITIVE
   );

   public Location findPrefixDeclaration( final String uri, final String content, final Position position ) {
      final String prefix = findPrefixAtPosition( content, position );
      if ( prefix == null ) {
         return null;
      }

      final String[] lines = content.split( "\\R", -1 );
      for ( int line = 0; line < lines.length; line++ ) {
         final Matcher matcher = PREFIX_DECLARATION_PATTERN.matcher( lines[line] );
         if ( !matcher.find() ) {
            continue;
         }

         final String declaredPrefix = matcher.group( 1 );
         final String normalizedPrefix = declaredPrefix == null ? "" : declaredPrefix;
         if ( !normalizedPrefix.equals( prefix ) ) {
            continue;
         }

         return new Location( uri, new Range( new Position( line, 0 ), new Position( line, lines[line].length() ) ) );
      }

      return null;
   }

   public String findPrefixAtPosition( final String content, final Position position ) {
      int lineStart = 0;
      int currentLine = 0;
      while ( currentLine < position.getLine() && lineStart < content.length() ) {
         if ( content.charAt( lineStart++ ) == '\n' ) {
            currentLine++;
         }
      }
      if ( currentLine != position.getLine() ) {
         return null;
      }

      int lineEnd = lineStart;
      while ( lineEnd < content.length() && content.charAt( lineEnd ) != '\n' ) {
         lineEnd++;
      }

      final int character = Math.max( 0, Math.min( position.getCharacter(), lineEnd - lineStart ) );
      int offset = lineStart + character;
      if ( offset > lineStart && ( offset == lineEnd || !isPrefixedNameChar( content.charAt( offset ) ) ) ) {
         offset--;
      }
      if ( offset < lineStart || offset >= lineEnd || !isPrefixedNameChar( content.charAt( offset ) ) ) {
         return null;
      }

      int start = offset;
      while ( start > lineStart && isPrefixedNameChar( content.charAt( start - 1 ) ) ) {
         start--;
      }

      int end = offset + 1;
      while ( end < lineEnd && isPrefixedNameChar( content.charAt( end ) ) ) {
         end++;
      }

      final String token = content.substring( start, end );
      final int colonIndex = token.indexOf( ':' );
      if ( colonIndex < 0 || colonIndex == token.length() - 1 ) {
         return null;
      }

      final String prefix = token.substring( 0, colonIndex );
      final String localPart = token.substring( colonIndex + 1 );
      if ( localPart.isEmpty() ) {
         return null;
      }

      return prefix;
   }

   private boolean isPrefixedNameChar( final char ch ) {
      return Character.isLetterOrDigit( ch ) || ch == ':' || ch == '_' || ch == '-';
   }
}
