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

import java.util.List;

import org.eclipse.esmf.metamodel.vocabulary.RdfNamespace;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;
import org.eclipse.esmf.treesitterturtle.ParserTokenType;
import org.eclipse.esmf.treesitterturtle.TurtleSyntaxTree;

import org.treesitter.TSTree;

public record ParsedDocument(
      Document sourceDocument,
      TSTree concreteSyntaxTree
) {
   private static final List<String> SAMM_PREFIXES = SammNs.sammNamespaces().map( RdfNamespace::getShortForm ).toList();

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

   public boolean isAspectModel() {
      return this.turtleSyntaxTree().nodes().anyMatch(
            node -> node.isToken() && ParserTokenType.PN_PREFIX.equals( node.type() ) && SAMM_PREFIXES.contains( node.content() )
      );
   }
}
