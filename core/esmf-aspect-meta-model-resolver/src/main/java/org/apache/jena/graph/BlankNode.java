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

package org.apache.jena.graph;

import org.eclipse.esmf.aspectmodel.resolver.parser.SmartToken;

/**
 * Because of some internal implementation details in Jena library, it was not possible
 * to extend all node types via one wrapper node; as a workaround each node type must have its own extension.
 */
public class BlankNode extends Node_Blank {

   private final SmartToken token;

   public BlankNode( final Node_Blank original, final SmartToken token ) {
      super( original.getBlankNodeId() );
      this.token = token;
   }

   public SmartToken getToken() {
      return token;
   }
}
