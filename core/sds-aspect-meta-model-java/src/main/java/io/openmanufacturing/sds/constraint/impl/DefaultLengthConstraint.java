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
package io.openmanufacturing.sds.constraint.impl;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import io.openmanufacturing.sds.constraint.LengthConstraint;
import io.openmanufacturing.sds.metamodel.impl.DefaultConstraint;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

public class DefaultLengthConstraint extends DefaultConstraint implements LengthConstraint {
   private final Optional<BigInteger> minValue;
   private final Optional<BigInteger> maxValue;

   public DefaultLengthConstraint( final MetaModelBaseAttributes metaModelBaseAttributes,
         final Optional<BigInteger> minValue,
         final Optional<BigInteger> maxValue ) {
      super( metaModelBaseAttributes );
      this.minValue = minValue;
      this.maxValue = maxValue;
   }

   /**
    * The lower bound of the length constraint.
    *
    * @return the minValue.
    */
   @Override
   public Optional<BigInteger> getMinValue() {
      return minValue;
   }

   /**
    * The upper bound of the length constraint.
    *
    * @return the maxValue.
    */
   @Override
   public Optional<BigInteger> getMaxValue() {
      return maxValue;
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
      return visitor.visitLengthConstraint( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultLengthConstraint.class.getSimpleName() + "[", "]" )
            .add( "minValue=" + minValue )
            .add( "maxValue=" + maxValue )
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
      final DefaultLengthConstraint that = (DefaultLengthConstraint) o;
      return Objects.equals( minValue, that.minValue ) &&
            Objects.equals( maxValue, that.maxValue );
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), minValue, maxValue );
   }
}
