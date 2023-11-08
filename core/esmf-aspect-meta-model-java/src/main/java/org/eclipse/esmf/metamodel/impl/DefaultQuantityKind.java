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

package org.eclipse.esmf.metamodel.impl;

import java.util.Objects;
import java.util.StringJoiner;

import org.eclipse.esmf.metamodel.QuantityKind;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

public class DefaultQuantityKind extends ModelElementImpl implements QuantityKind {
   private final String label;

   public DefaultQuantityKind( final MetaModelBaseAttributes metaModelBaseAttributes, final String label ) {
      super( metaModelBaseAttributes );
      this.label = label;
   }

   @Override
   public String getLabel() {
      return label;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      if ( !super.equals( o ) ) {
         return false;
      }
      final DefaultQuantityKind that = (DefaultQuantityKind) o;
      return Objects.equals( label, that.label );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), label );
   }

   /**
    * Accepts an Aspect visitor
    *
    * @param visitor The visitor to accept
    * @param <T> The result type of the traversal operation
    * @param <C> The context of the visitor traversal
    */
   @Override
   public <T, C> T accept( final AspectVisitor<T, C> visitor, final C context ) {
      return visitor.visitQuantityKind( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultQuantityKind.class.getSimpleName() + "[", "]" )
            .toString();
   }
}
