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

import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;

import org.treesitter.TSTree;

public record ParsedDocument(
      Document sourceDocument,
      TSTree concreteSyntaxTree
) {
   public String getUri() {
      return sourceDocument().getUri();
   }

   /**
    * Retrieve the {@link TurtleSyntaxTree} for this parsed document
    *
    * @return the turtle syntax tree
    */
   public TurtleSyntaxTree turtleSyntaxTree() {
      return TurtleSyntaxTree.fromConcreteSyntaxTree( concreteSyntaxTree(),
            () -> sourceDocument().getContent(),
            location -> sourceDocument().subSequence( location.fromLine(), location.fromColumn(),
                  location.toLine(), location.toColumn() ) );
   }
}
