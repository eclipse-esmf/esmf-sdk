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

package org.eclipse.esmf.aspectmodel;

/**
 * Zero-based section of a document
 *
 * @param fromLine starting line
 * @param fromColumn starting column
 * @param toLine ending line
 * @param toColumn ending column
 */
public record Location(
      int fromLine,
      int fromColumn,
      int toLine,
      int toColumn
) {
   public Location( final int fromLine, final int fromColumn ) {
      this( fromLine, fromColumn, fromLine, fromColumn );
   }

   /**
    * Returns the subpart of a given document that is covered by this location.
    *
    * @param sourceDocument the source document
    * @return the subpart of the document, or an empty string, if the location is invalid or the
    *         document is empty
    */
   public String forDocument( final String sourceDocument ) {
      final Location location = this;
      if ( sourceDocument == null || sourceDocument.isEmpty() ) {
         return "";
      }

      int startIndex = 0;
      int currentLine = 0;
      for ( int i = 0; i < sourceDocument.length() && currentLine < location.fromLine(); i++ ) {
         if ( sourceDocument.charAt( i ) == '\n' ) {
            currentLine++;
         }
         startIndex = i + 1;
      }
      startIndex += location.fromColumn();
      int endIndex = 0;
      currentLine = 0;

      for ( int i = 0; i < sourceDocument.length() && currentLine < location.toLine(); i++ ) {
         if ( sourceDocument.charAt( i ) == '\n' ) {
            currentLine++;
         }
         endIndex = i + 1;
      }
      endIndex += location.toColumn();
      startIndex = Math.clamp( startIndex, 0, sourceDocument.length() );
      endIndex = Math.clamp( endIndex, 0, sourceDocument.length() );

      if ( startIndex > endIndex ) {
         return "";
      }

      return sourceDocument.substring( startIndex, endIndex );
   }
}
