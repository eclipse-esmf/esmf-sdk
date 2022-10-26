/*
 * Copyright (c) 2022 Robert Bosch Manufacturing Solutions GmbH
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

package io.openmanufacturing.sds.aspectmodel.shacl.constraint.js;

import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.graph.NodeFactory;

/**
 * The TermFactory that is injected into the JavaScript evaluation context, as specified in
 * <a href="https://www.w3.org/TR/shacl-js/#js-api-termfactory">The TermFactory</a>.
 */
public class TermFactory {
   public JsNamedNode namedNode( final String value ) {
      return new JsNamedNode( NodeFactory.createURI( value ) );
   }

   public JsBlankNode blankNode() {
      return new JsBlankNode( NodeFactory.createBlankNode() );
   }

   public JsBlankNode blankNode( final String value ) {
      return new JsBlankNode( NodeFactory.createBlankNode( value ) );
   }

   public JsLiteral literal( final String value, final Object langOrDatatype ) {
      if ( langOrDatatype instanceof JsNamedNode ) {
         return new JsLiteral( NodeFactory.createLiteral( value, TypeMapper.getInstance().getTypeByName( ((JsNamedNode) langOrDatatype).uri ) ) );
      } else if ( langOrDatatype instanceof String ) {
         return new JsLiteral( NodeFactory.createLiteral( value, (String) langOrDatatype ) );
      } else {
         throw new IllegalArgumentException( "Invalid type of langOrDatatype argument" );
      }
   }
}
