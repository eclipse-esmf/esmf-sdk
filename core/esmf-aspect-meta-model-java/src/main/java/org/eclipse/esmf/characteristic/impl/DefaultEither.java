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
package org.eclipse.esmf.characteristic.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import org.eclipse.esmf.characteristic.Either;
import org.eclipse.esmf.metamodel.Characteristic;
import org.eclipse.esmf.metamodel.Type;
import org.eclipse.esmf.metamodel.impl.DefaultCharacteristic;
import org.eclipse.esmf.metamodel.loader.MetaModelBaseAttributes;
import org.eclipse.esmf.metamodel.visitor.AspectVisitor;

public class DefaultEither extends DefaultCharacteristic implements Either {
   private final Characteristic left;
   private final Characteristic right;

   public DefaultEither( final MetaModelBaseAttributes metaModelBaseAttributes,
         final Optional<Type> dataType,
         final Characteristic left,
         final Characteristic right ) {
      super( metaModelBaseAttributes, dataType );
      this.left = left;
      this.right = right;
   }

   /**
    * The Characteristic for the left side value of a disjoint union.
    *
    * @return the left.
    */
   @Override
   public Characteristic getLeft() {
      return left;
   }

   /**
    * The Characteristic for the right side value of a disjoint union.
    *
    * @return the right.
    */
   @Override
   public Characteristic getRight() {
      return right;
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
      return visitor.visitEither( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultEither.class.getSimpleName() + "[", "]" )
            .add( "left=" + left )
            .add( "right=" + right )
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
      final DefaultEither that = (DefaultEither) o;
      return Objects.equals( left, that.left )
            && Objects.equals( right, that.right );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), left, right );
   }
}
