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
import org.apache.jena.graph.Triple;

/**
 * Utility class that translates between SHACL JS objects and their RDF representation
 */
public class JsFactory {
   public static JsTerm asJsTerm( final Node node ) {
      if ( node.isURI() ) {
         return new JsNamedNode( node );
      } else if ( node.isBlank() ) {
         return new JsBlankNode( node );
      } else if ( node.isLiteral() ) {
         return new JsLiteral( node );
      } else {
         throw new IllegalArgumentException( "Unsupported node type " + node );
      }
   }

   public static JsTriple asJsTriple( final Triple triple ) {
      return new JsTriple( triple );
   }
}
