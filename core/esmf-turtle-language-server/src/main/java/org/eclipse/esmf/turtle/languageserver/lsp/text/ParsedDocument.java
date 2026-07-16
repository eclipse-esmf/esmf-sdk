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

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;

import org.treesitter.TSTree;

public class ParsedDocument {
   private final Document sourceDocument;
   private final TSTree concreteSyntaxTree;
   private final TurtleSyntaxTree turtleSyntaxTree;

   public ParsedDocument( final Document document, final TSTree concreteSyntaxTree ) {
      sourceDocument = document;
      this.concreteSyntaxTree = concreteSyntaxTree;
      turtleSyntaxTree = TurtleSyntaxTree.fromConcreteSyntaxTree( concreteSyntaxTree, sourceDocument.getContent() );
   }

   public Document sourceDocument() {
      return sourceDocument;
   }

   public TSTree concreteSyntaxTree() {
      return concreteSyntaxTree;
   }

   public TurtleSyntaxTree turtleSyntaxTree() {
      return turtleSyntaxTree;
   }

   public String getUri() {
      return sourceDocument.getUri();
   }

   public boolean storedIn( final Path path ) {
      try {
         return Path.of( new URI( sourceDocument.getUri() ) ).getParent().toAbsolutePath().equals( path.toAbsolutePath() );
      } catch ( final URISyntaxException e ) {
         return false;
      }
   }
}
