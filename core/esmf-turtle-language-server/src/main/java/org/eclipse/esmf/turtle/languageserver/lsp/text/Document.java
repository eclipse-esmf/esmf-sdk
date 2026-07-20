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

import org.eclipse.esmf.Location;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.jspecify.annotations.Nullable;

/**
 * Implementation of a document based on a {@link Rope}.
 */
public class Document implements TurtleSyntaxTree.TokenProvider {
   private final String uri;
   private Rope content;

   public Document( final String uri, final String initialContent ) {
      this.uri = uri;
      content = new Rope( initialContent );
   }

   public String uri() {
      return uri;
   }

   public String content() {
      return content.toString();
   }

   public Rope rope() {
      return content;
   }

   public InputStream inputStream() {
      return content.inputStream();
   }

   public int index( final int targetLine, final int targetColumn ) {
      return content.getIndex( targetLine, targetColumn );
   }

   public String subSequence( final int fromLine, final int fromColumn, final int toLine, final int toColumn ) {
      final int fromIndex = index( fromLine, fromColumn );
      final int toIndex = index( toLine, toColumn );
      return content.subSequence( fromIndex, toIndex ).toString();
   }

   public synchronized void update( final @Nullable Range range, final String newContent ) {
      if ( range == null ) {
         content = new Rope( newContent );
         return;
      }
      final Position start = range.getStart();
      final Position end = range.getEnd();
      content = content.update( start.getLine(), start.getCharacter(),
            end.getLine(), end.getCharacter(), newContent );
   }

   @Override
   public String apply( final Location location ) {
      return subSequence( location.fromLine(), location.fromColumn(), location.toLine(), location.toColumn() );
   }
}
