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

package org.eclipse.esmf.turtle.languageserver.lsp.text;

import java.io.InputStream;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class Document {
   private final String uri;
   private Rope content;

   public Document( final String uri, final String initialContent ) {
      this.uri = uri;
      content = new Rope( initialContent );
   }

   public String getUri() {
      return uri;
   }

   public String getContent() {
      return content.toString();
   }

   public InputStream getInputStream() {
      return content.inputStream();
   }

   public int getIndex( final int targetLine, final int targetColumn ) {
      return content.getIndex( targetLine, targetColumn );
   }

   /**
    * Reads at most many bytes from the given offset into the buffer array, as the array provides, or
    * fewer, if
    * not as many are left at the offset. Returns 0 if the end of the source code was reached,
    * otherwise the number of bytes read.
    *
    * @param buffer the buffer to write to
    * @param offset offset to read from
    * @return the number of bytes read
    */
   public int read( final byte[] buffer, final int offset ) {
      return content.read( buffer, offset );
   }

   /**
    * Reads bytes from a specific position (row, column) in the document into the buffer.
    *
    * @param buffer the buffer to write to
    * @param offset offset in the buffer to write to
    * @param row the row/line to read from
    * @param column the column in the row to read from
    * @return the number of bytes read
    */
   public int read( final byte[] buffer, final int offset, final int row, final int column ) {
      return content.read( buffer, offset, row, column );
   }

   public void update( final Range range, final String newContent ) {
      final Position start = range.getStart();
      final Position end = range.getEnd();
      content = content.update( start.getLine(), start.getCharacter(),
            end.getLine(), end.getCharacter(), newContent );
   }
}
