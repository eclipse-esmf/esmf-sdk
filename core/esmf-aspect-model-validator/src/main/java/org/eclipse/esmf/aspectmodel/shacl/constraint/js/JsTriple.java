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

import org.apache.jena.graph.Triple;

/**
 * Represents a <a href="https://www.w3.org/TR/shacl-js/#js-api-triples">SHACL JS Triple</a>
 */
public class JsTriple {
   private final Triple triple;
   public final JsTerm subject;
   public final JsTerm predicate;
   public final JsTerm object;

   JsTriple( final Triple triple ) {
      this.triple = triple;
      subject = JsFactory.asJsTerm( triple.getObject() );
      predicate = JsFactory.asJsTerm( triple.getPredicate() );
      object = JsFactory.asJsTerm( triple.getObject() );
   }

   @Override
   public boolean equals( final Object obj ) {
      if ( obj instanceof JsTriple ) {
         return triple.equals( ((JsTriple) obj).triple );
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return triple.hashCode();
   }

   @Override
   public String toString() {
      return "Triple(" + triple.getSubject() + ", " + triple.getPredicate() + ", " + triple.getObject() + ")";
   }
}
