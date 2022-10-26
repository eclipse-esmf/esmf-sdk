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

import org.apache.jena.graph.Triple;

public class JsTriple {
   private final Triple triple;

   JsTriple( final Triple triple ) {
      this.triple = triple;
   }

   public JsTerm getObject() {
      return JsFactory.asJsTerm( triple.getObject() );
   }

   public JsTerm getPredicate() {
      return JsFactory.asJsTerm( triple.getPredicate() );
   }

   public JsTerm getSubject() {
      return JsFactory.asJsTerm( triple.getSubject() );
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
