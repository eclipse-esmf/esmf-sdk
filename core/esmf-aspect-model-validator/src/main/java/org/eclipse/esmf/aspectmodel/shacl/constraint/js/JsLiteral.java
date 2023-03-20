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
import org.apache.jena.graph.NodeFactory;

public class JsLiteral extends JsTerm {
   public final String lex;
   public final String language;
   public final JsNamedNode datatype;

   JsLiteral( final Node node ) {
      super( node );
      lex = node.getLiteralLexicalForm();
      language = node.getLiteralLanguage();
      datatype = new JsNamedNode( NodeFactory.createURI( node.getLiteralDatatypeURI() ) );
   }
}
