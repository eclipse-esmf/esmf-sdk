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

package org.eclipse.esmf.aspectmodel.shacl.constraint.js;

import org.apache.jena.graph.Node;

/**
 * Base class for <a href="https://www.w3.org/TR/shacl-js/#js-api-terms">RDF Terms</a>
 */
public abstract class JsTerm {
   protected Node node;

   protected JsTerm( final Node node ) {
      this.node = node;
   }

   @Override
   public boolean equals( final Object other ) {
      return other instanceof JsTerm && node.equals( ((JsTerm) other).node );
   }

   public Node getNode() {
      return node;
   }

   @Override
   public int hashCode() {
      return node.hashCode();
   }

   public boolean isBlankNode() {
      return node.isBlank();
   }

   public boolean isLiteral() {
      return node.isLiteral();
   }

   public boolean isURI() {
      return node.isURI();
   }

   @Override
   public String toString() {
      return node.toString();
   }
}
