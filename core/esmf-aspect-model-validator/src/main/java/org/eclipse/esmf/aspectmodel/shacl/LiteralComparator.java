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

package org.eclipse.esmf.aspectmodel.shacl;

import java.util.Comparator;

import org.apache.jena.rdf.model.Literal;

/**
 * A {@link Comparator} for RDF {@link Literal}s that takes into account their type
 */
public class LiteralComparator implements Comparator<Literal> {
   @Override
   public int compare( final Literal literal1, final Literal literal2 ) {
      if ( !literal1.getDatatypeURI().equals( literal2.getDatatypeURI() ) ) {
         throw new ClassCastException();
      }

      final Object value = literal1.getValue();
      if ( value instanceof Integer ) {
         return Integer.compare( literal1.getInt(), literal2.getInt() );
      }
      if ( value instanceof Long ) {
         return Long.compare( literal1.getLong(), literal2.getLong() );
      }
      if ( value instanceof Short ) {
         return Short.compare( literal1.getShort(), literal2.getShort() );
      }
      if ( value instanceof Float ) {
         return Float.compare( literal1.getFloat(), literal2.getFloat() );
      }
      if ( value instanceof Double ) {
         return Double.compare( literal1.getDouble(), literal2.getDouble() );
      }
      if ( value instanceof Character ) {
         return Character.compare( literal1.getChar(), literal2.getChar() );
      }
      if ( value instanceof Byte ) {
         return Byte.compare( literal1.getByte(), literal2.getByte() );
      }
      if ( value instanceof Boolean ) {
         return Boolean.compare( literal1.getBoolean(), literal2.getBoolean() );
      }

      throw new ClassCastException();
   }
}
