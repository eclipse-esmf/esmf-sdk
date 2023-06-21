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
 * Represents a <a href="https://www.w3.org/TR/shacl-js/#js-api-bnode">SHACL JS BlankNode</a>.
 */
public class JsBlankNode extends JsTerm {
   public final String id;

   JsBlankNode( final Node node ) {
      super( node );
      id = node.getBlankNodeLabel();
   }
}

