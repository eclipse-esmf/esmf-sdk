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

package org.eclipse.esmf.aspectmodel.java;

import java.math.BigInteger;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.java.rangeconstraint.AnnotationExpression;
import org.eclipse.esmf.aspectmodel.java.rangeconstraint.AnnotationFactory;
import org.eclipse.esmf.aspectmodel.java.rangeconstraint.AnnotationTypeMapping;
import org.eclipse.esmf.metamodel.BoundDefinition;
import org.eclipse.esmf.metamodel.Constraint;
import org.eclipse.esmf.metamodel.ScalarValue;
import org.eclipse.esmf.metamodel.constraint.FixedPointConstraint;
import org.eclipse.esmf.metamodel.constraint.LengthConstraint;
import org.eclipse.esmf.metamodel.constraint.RangeConstraint;
import org.eclipse.esmf.metamodel.constraint.RegularExpressionConstraint;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.apache.commons.text.StringEscapeUtils;

/**
 * Provides functionality to create jakarta.validation.constraints that are displayed in the generated java class
 */
public class ConstraintAnnotationBuilder {

   private static final String ANNOTATION_MARKING = "@";
   private static final String LEFT_BRACKET = "(";
   private static final String RIGHT_BRACKET = ")";

   private final StringBuilder constraintAnnotation = new StringBuilder( 110 );

   private Constraint constraintClass;
   private ImportTracker importTracker;

   public ConstraintAnnotationBuilder setConstraintClass( final Constraint constraintClass ) {
      this.constraintClass = constraintClass;
      return this;
   }

   public ConstraintAnnotationBuilder setImportTracker( final ImportTracker importTracker ) {
      this.importTracker = importTracker;
      return this;
   }

   public String build() {
      return createConstraintAnnotation();
   }

   private String createConstraintAnnotation() {
      createRegularExpressionConstraint();
      createRangeConstraint();
      createLengthConstraint();
      createFixedPointConstraint();
      return constraintAnnotation.toString();
   }

   private void createRegularExpressionConstraint() {
      if ( !(constraintClass instanceof RegularExpressionConstraint) ) {
         return;
      }
      final RegularExpressionConstraint regularExpressionConstraint = (RegularExpressionConstraint) constraintClass;
      final String value = regularExpressionConstraint.getValue();
      final String escapedValue = StringEscapeUtils.escapeJava( value );
      appendStringBuilder( Pattern.class, "regexp = \"" + escapedValue + "\"" );
   }

   private void createRangeConstraint() {
      if ( !(constraintClass instanceof RangeConstraint) ) {
         return;
      }
      final RangeConstraint rangeConstraint = (RangeConstraint) constraintClass;
      final Optional<Object> minValue = rangeConstraint.getMinValue().map( ScalarValue::getValue );
      final Optional<Object> maxValue = rangeConstraint.getMaxValue().map( ScalarValue::getValue );
      final BoundDefinition lowerBoundDefinition = rangeConstraint.getLowerBoundDefinition();
      final BoundDefinition upperBoundDefinition = rangeConstraint.getUpperBoundDefinition();

      if ( minValue.isPresent() || maxValue.isPresent() ) {
         importTracker.importExplicit( BoundDefinition.class );
      }
      minValue.ifPresent( value -> {
         final AnnotationExpression targetAnnotation = getAnnotationExpression( value, AnnotationTypeMapping.MINIMUM );
         importTracker.importExplicit( targetAnnotation.getTargetAnnotation() );
         constraintAnnotation.append( targetAnnotation.apply( value, lowerBoundDefinition ) );
      } );

      maxValue.ifPresent( value -> {
         final AnnotationExpression targetAnnotation = getAnnotationExpression( value, AnnotationTypeMapping.MAXIMUM );
         importTracker.importExplicit( targetAnnotation.getTargetAnnotation() );
         constraintAnnotation.append( targetAnnotation.apply( value, upperBoundDefinition ) );
      } );
   }

   private AnnotationExpression getAnnotationExpression( final Object value,
         final AnnotationTypeMapping annotationTypeMapping ) {
      return AnnotationFactory.getOperation( value.getClass(), annotationTypeMapping )
            .orElseThrow( () -> new IllegalArgumentException( "Invalid Annotation" ) );
   }

   private void createLengthConstraint() {
      if ( !(constraintClass instanceof LengthConstraint) ) {
         return;
      }
      final LengthConstraint lengthConstraint = (LengthConstraint) constraintClass;
      final Optional<BigInteger> minValue = lengthConstraint.getMinValue();
      final Optional<BigInteger> maxValue = lengthConstraint.getMaxValue();

      final StringBuilder sizeExpression = new StringBuilder( 30 );

      minValue.ifPresent( value -> sizeExpression.append( "min = " ).append( value ) );
      if ( minValue.isPresent() && maxValue.isPresent() ) {
         sizeExpression.append( "," );
      }
      maxValue.ifPresent( value -> sizeExpression.append( "max = " ).append( value ) );

      appendStringBuilder( Size.class, sizeExpression.toString() );
   }

   private void createFixedPointConstraint() {
      if ( !(constraintClass instanceof FixedPointConstraint) ) {
         return;
      }
      final FixedPointConstraint fixedPointConstraint = (FixedPointConstraint) constraintClass;
      final Integer scale = fixedPointConstraint.getScale();
      final Integer integer = fixedPointConstraint.getInteger();

      final StringBuilder fixedPointExpression = new StringBuilder( 30 );
      fixedPointExpression.append( "fraction = " ).append( scale );
      fixedPointExpression.append( ", integer = " ).append( integer );

      appendStringBuilder( Digits.class, fixedPointExpression );
   }

   private void appendStringBuilder( final Class<?> beanAnnotation, final Object expression ) {
      importTracker.importExplicit( beanAnnotation );
      constraintAnnotation.append( ANNOTATION_MARKING )
            .append( beanAnnotation.getSimpleName() )
            .append( LEFT_BRACKET )
            .append( expression )
            .append( RIGHT_BRACKET )
            .append( '\n' );
   }
}
