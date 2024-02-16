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

package org.eclipse.esmf.constraint.impl;

import java.util.Objects;
import java.util.StringJoiner;

import org.eclipse.esmf.constraint.FixedPointConstraint;
import org.eclipse.esmf.metamodel.impl.DefaultConstraint;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

public class DefaultFixedPointConstraint extends DefaultConstraint implements FixedPointConstraint {

   private final Integer scale;
   private final Integer integer;

   public DefaultFixedPointConstraint( final MetaModelBaseAttributes metaModelBaseAttributes, final Integer scale,
         final Integer integer ) {
      super( metaModelBaseAttributes );
      this.scale = scale;
      this.integer = integer;
   }

   @Override
   public Integer getScale() {
      return scale;
   }

   @Override
   public Integer getInteger() {
      return integer;
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
      return visitor.visitFixedPointConstraint( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultFixedPointConstraint.class.getSimpleName() + "[", "]" )
            .add( "scale=" + scale )
            .add( "integer=" + integer )
            .toString();
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
      final DefaultFixedPointConstraint that = (DefaultFixedPointConstraint) o;
      return Objects.equals( scale, that.scale )
            && Objects.equals( integer, that.integer );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), scale, integer );
   }
}
