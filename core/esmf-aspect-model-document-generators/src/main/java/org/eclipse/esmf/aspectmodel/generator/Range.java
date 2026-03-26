/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.constraint.LengthConstraint;
import org.eclipse.esmf.metamodel.constraint.RangeConstraint;

/**
 * Represents a numeric range with optional min/max bounds.
 */
public record Range(
      BigDecimal min, BigDecimal max
) {
   static final Range OPEN = new Range( null, (BigDecimal) null );
   static final float EPSILON = .0001f;

   public Range( final Double min, final Double max ) {
      this( min == null ? null : BigDecimal.valueOf( min ), max == null ? null : BigDecimal.valueOf( max ) );
   }

   public Range( final Long min, final Long max ) {
      this( min == null ? null : BigDecimal.valueOf( min ), max == null ? null : BigDecimal.valueOf( max ) );
   }

   public Range( final Integer min, final Integer max ) {
      this( min == null ? null : BigDecimal.valueOf( min ), max == null ? null : BigDecimal.valueOf( max ) );
   }

   public Range( final BigInteger min, final BigInteger max ) {
      this( min == null ? null : new BigDecimal( min ), max == null ? null : new BigDecimal( max ) );
   }

   public Range merge( final Range other ) {
      final BigDecimal newMin;
      if ( min == null && other.min == null ) {
         newMin = null;
      } else if ( min == null ) {
         newMin = other.min;
      } else if ( other.min == null ) {
         newMin = min;
      } else {
         newMin = min.max( other.min );
      }

      final BigDecimal newMax;
      if ( max == null && other.max == null ) {
         newMax = null;
      } else if ( max == null ) {
         newMax = other.max;
      } else if ( other.max == null ) {
         newMax = max;
      } else {
         newMax = max.min( other.max );
      }
      if ( newMin != null && newMax != null && newMin.compareTo( newMax ) > 0 ) {
         return this;
      }
      return new Range( newMin, newMax );
   }

   public Range clamp( final Long min, final Long max ) {
      return merge( new Range( min, max ) );
   }

   public Range clamp( final Integer min, final Integer max ) {
      return merge( new Range( min, max ) );
   }

   public Range clamp( final Optional<BigInteger> min, final Optional<BigInteger> max ) {
      return merge( new Range( min.orElse( null ), max.orElse( null ) ) );
   }

   private static BigDecimal getScalarValue( final ScalarValue value ) {
      return value.getValue() instanceof final BigDecimal bigDecimal
            ? bigDecimal
            : new BigDecimal( value.getValue().toString() );
   }

   public static Range fromLengthConstraints( final List<Constraint> constraints ) {
      return constraints.stream()
            .filter( constraint -> constraint.is( LengthConstraint.class ) )
            .map( constraint -> constraint.as( LengthConstraint.class ) )
            .map( lc -> new Range( lc.getMinValue().orElse( null ), lc.getMaxValue().orElse( null ) ) )
            .reduce( OPEN, Range::merge );
   }

   public static Range fromRangeConstraints( final List<Constraint> constraints, final boolean floatingPoint ) {
      return constraints.stream()
            .<Optional<Range>>map( constraint -> {
               if ( constraint instanceof final RangeConstraint rc ) {
                  if ( floatingPoint ) {
                     final Optional<Double> rcMin = rc.getMinValue()
                           .map( v -> getScalarValue( v ).doubleValue() )
                           .map( v -> org.eclipse.esmf.metamodel.BoundDefinition.GREATER_THAN
                                 .equals( rc.getLowerBoundDefinition() ) ? v + EPSILON : v );
                     final Optional<Double> rcMax = rc.getMaxValue()
                           .map( v -> getScalarValue( v ).doubleValue() )
                           .map( v -> org.eclipse.esmf.metamodel.BoundDefinition.LESS_THAN
                                 .equals( rc.getUpperBoundDefinition() ) ? v - EPSILON : v );
                     return Optional.of( new Range( rcMin.orElse( null ), rcMax.orElse( null ) ) );
                  }
                  final Optional<BigDecimal> rcMin = rc.getMinValue()
                        .map( Range::getScalarValue )
                        .map( v -> org.eclipse.esmf.metamodel.BoundDefinition.GREATER_THAN
                              .equals( rc.getLowerBoundDefinition() ) ? v.add( BigDecimal.ONE ) : v );
                  final Optional<BigDecimal> rcMax = rc.getMaxValue()
                        .map( Range::getScalarValue )
                        .map( v -> org.eclipse.esmf.metamodel.BoundDefinition.LESS_THAN
                              .equals( rc.getUpperBoundDefinition() ) ? v.subtract( BigDecimal.ONE ) : v );
                  return Optional.of( new Range( rcMin.orElse( null ), rcMax.orElse( null ) ) );
               }
               return Optional.empty();
            } )
            .flatMap( Optional::stream )
            .reduce( OPEN, Range::merge );
   }
}
