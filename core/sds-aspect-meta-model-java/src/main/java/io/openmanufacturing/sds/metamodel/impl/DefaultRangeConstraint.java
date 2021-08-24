/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH
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
package io.openmanufacturing.sds.metamodel.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import io.openmanufacturing.sds.metamodel.RangeConstraint;
import io.openmanufacturing.sds.metamodel.loader.MetaModelBaseAttributes;
import io.openmanufacturing.sds.metamodel.visitor.AspectVisitor;

public class DefaultRangeConstraint extends DefaultConstraint implements RangeConstraint {
   private final Optional<Object> minValue;
   private final Optional<Object> maxValue;
   private final BoundDefinition lowerBoundDefinition;
   private final BoundDefinition upperBoundDefinition;

   public DefaultRangeConstraint( final MetaModelBaseAttributes metaModelBaseAttributes,
         final Optional<Object> minValue,
         final Optional<Object> maxValue,
         final BoundDefinition lowerBoundDefinition,
         final BoundDefinition upperBoundDefinition ) {
      super( metaModelBaseAttributes );
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.lowerBoundDefinition = lowerBoundDefinition;
      this.upperBoundDefinition = upperBoundDefinition;
   }

   /**
    * The lower bound of a range.
    *
    * @return the minValue.
    */
   @Override
   public Optional<Object> getMinValue() {
      return minValue;
   }

   /**
    * The upper bound of a range.
    *
    * @return the maxValue.
    */
   @Override
   public Optional<Object> getMaxValue() {
      return maxValue;
   }

   @Override
   public BoundDefinition getLowerBoundDefinition() {
      return lowerBoundDefinition;
   }

   @Override
   public BoundDefinition getUpperBoundDefinition() {
      return upperBoundDefinition;
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
      return visitor.visitRangeConstraint( this, context );
   }

   @Override
   public String toString() {
      return new StringJoiner( ", ", DefaultRangeConstraint.class.getSimpleName() + "[", "]" )
            .add( "minValue=" + minValue )
            .add( "maxValue=" + maxValue )
            .add( "lowerBoundDefinition=" + lowerBoundDefinition )
            .add( "upperBoundDefinition=" + upperBoundDefinition )
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
      final DefaultRangeConstraint that = (DefaultRangeConstraint) o;
      return Objects.equals( minValue, that.minValue ) &&
            Objects.equals( maxValue, that.maxValue ) &&
            lowerBoundDefinition == that.lowerBoundDefinition &&
            upperBoundDefinition == that.upperBoundDefinition;
   }

   @Override
   public int hashCode() {
      return Objects.hash( super.hashCode(), minValue, maxValue, lowerBoundDefinition, upperBoundDefinition );
   }
}
