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
package org.eclipse.esmf.aspectmodel.generator.json.testclasses;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

import org.eclipse.esmf.aspectmodel.java.customconstraint.DoubleMax;
import org.eclipse.esmf.aspectmodel.java.customconstraint.DoubleMin;
import org.eclipse.esmf.aspectmodel.java.customconstraint.FloatMax;
import org.eclipse.esmf.aspectmodel.java.customconstraint.FloatMin;
import org.eclipse.esmf.aspectmodel.java.customconstraint.IntegerMax;
import org.eclipse.esmf.aspectmodel.java.customconstraint.IntegerMin;
import org.eclipse.esmf.metamodel.impl.BoundDefinition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Generated class for AspectWithConstraints. */
public class AspectWithConstraints {

   @Pattern( regexp = "^[a-zA-Z]" )
   private String testPropertyWithRegularExpression;

   @DecimalMin( value = "2.3" )
   @DecimalMax( value = "10.5" )
   private BigDecimal testPropertyWithDecimalMinDecimalMaxRangeConstraint;

   @DecimalMax( value = "10.5" )
   private BigDecimal testPropertyWithDecimalMaxRangeConstraint;

   @IntegerMin( value = 1, boundDefinition = BoundDefinition.AT_LEAST )
   @IntegerMax( value = 10, boundDefinition = BoundDefinition.AT_MOST )
   private Integer testPropertyWithMinMaxRangeConstraint;

   @IntegerMin( value = 1, boundDefinition = BoundDefinition.AT_LEAST )
   private Integer testPropertyWithMinRangeConstraint;

   @FloatMin( value = "1.0", boundDefinition = BoundDefinition.AT_LEAST )
   @FloatMax( value = "10.0", boundDefinition = BoundDefinition.AT_MOST )
   private Float testPropertyRangeConstraintWithFloatType;

   @DoubleMin( value = "1.0", boundDefinition = BoundDefinition.AT_LEAST )
   @DoubleMax( value = "10.0", boundDefinition = BoundDefinition.AT_MOST )
   private Double testPropertyRangeConstraintWithDoubleType;

   @Size( min = 1, max = 10 )
   private String testPropertyWithMinMaxLengthConstraint;

   @Size( min = 1 )
   private BigInteger testPropertyWithMinLengthConstraint;

   @Size( min = 1, max = 10 )
   private List<BigInteger> testPropertyCollectionLengthConstraint;

   @JsonCreator
   public AspectWithConstraints(
         @JsonProperty( value = "testPropertyWithRegularExpression" )
         String testPropertyWithRegularExpression,
         @JsonProperty( value = "testPropertyWithDecimalMinDecimalMaxRangeConstraint" )
         BigDecimal testPropertyWithDecimalMinDecimalMaxRangeConstraint,
         @JsonProperty( value = "testPropertyWithDecimalMaxRangeConstraint" )
         BigDecimal testPropertyWithDecimalMaxRangeConstraint,
         @JsonProperty( value = "testPropertyWithMinMaxRangeConstraint" )
         Integer testPropertyWithMinMaxRangeConstraint,
         @JsonProperty( value = "testPropertyWithMinRangeConstraint" )
         Integer testPropertyWithMinRangeConstraint,
         @JsonProperty( value = "testPropertyRangeConstraintWithFloatType" )
         Float testPropertyRangeConstraintWithFloatType,
         @JsonProperty( value = "testPropertyRangeConstraintWithDoubleType" )
         Double testPropertyRangeConstraintWithDoubleType,
         @JsonProperty( value = "testPropertyWithMinMaxLengthConstraint" )
         String testPropertyWithMinMaxLengthConstraint,
         @JsonProperty( value = "testPropertyWithMinLengthConstraint" )
         BigInteger testPropertyWithMinLengthConstraint,
         @JsonProperty( value = "testPropertyCollectionLengthConstraint" )
         List<BigInteger> testPropertyCollectionLengthConstraint ) {
      this.testPropertyWithRegularExpression = testPropertyWithRegularExpression;
      this.testPropertyWithDecimalMinDecimalMaxRangeConstraint =
            testPropertyWithDecimalMinDecimalMaxRangeConstraint;
      this.testPropertyWithDecimalMaxRangeConstraint = testPropertyWithDecimalMaxRangeConstraint;
      this.testPropertyWithMinMaxRangeConstraint = testPropertyWithMinMaxRangeConstraint;
      this.testPropertyWithMinRangeConstraint = testPropertyWithMinRangeConstraint;
      this.testPropertyRangeConstraintWithFloatType = testPropertyRangeConstraintWithFloatType;
      this.testPropertyRangeConstraintWithDoubleType = testPropertyRangeConstraintWithDoubleType;
      this.testPropertyWithMinMaxLengthConstraint = testPropertyWithMinMaxLengthConstraint;
      this.testPropertyWithMinLengthConstraint = testPropertyWithMinLengthConstraint;
      this.testPropertyCollectionLengthConstraint = testPropertyCollectionLengthConstraint;
   }

   /**
    * Returns testPropertyWithRegularExpression
    *
    * @return {@link #testPropertyWithRegularExpression}
    */
   public String getTestPropertyWithRegularExpression() {
      return this.testPropertyWithRegularExpression;
   }

   /**
    * Returns testPropertyWithDecimalMinDecimalMaxRangeConstraint
    *
    * @return {@link #testPropertyWithDecimalMinDecimalMaxRangeConstraint}
    */
   public BigDecimal getTestPropertyWithDecimalMinDecimalMaxRangeConstraint() {
      return this.testPropertyWithDecimalMinDecimalMaxRangeConstraint;
   }

   /**
    * Returns testPropertyWithDecimalMaxRangeConstraint
    *
    * @return {@link #testPropertyWithDecimalMaxRangeConstraint}
    */
   public BigDecimal getTestPropertyWithDecimalMaxRangeConstraint() {
      return this.testPropertyWithDecimalMaxRangeConstraint;
   }

   /**
    * Returns testPropertyWithMinMaxRangeConstraint
    *
    * @return {@link #testPropertyWithMinMaxRangeConstraint}
    */
   public Integer getTestPropertyWithMinMaxRangeConstraint() {
      return this.testPropertyWithMinMaxRangeConstraint;
   }

   /**
    * Returns testPropertyWithMinRangeConstraint
    *
    * @return {@link #testPropertyWithMinRangeConstraint}
    */
   public Integer getTestPropertyWithMinRangeConstraint() {
      return this.testPropertyWithMinRangeConstraint;
   }

   /**
    * Returns testPropertyRangeConstraintWithFloatType
    *
    * @return {@link #testPropertyRangeConstraintWithFloatType}
    */
   public Float getTestPropertyRangeConstraintWithFloatType() {
      return this.testPropertyRangeConstraintWithFloatType;
   }

   /**
    * Returns testPropertyRangeConstraintWithDoubleType
    *
    * @return {@link #testPropertyRangeConstraintWithDoubleType}
    */
   public Double getTestPropertyRangeConstraintWithDoubleType() {
      return this.testPropertyRangeConstraintWithDoubleType;
   }

   /**
    * Returns testPropertyWithMinMaxLengthConstraint
    *
    * @return {@link #testPropertyWithMinMaxLengthConstraint}
    */
   public String getTestPropertyWithMinMaxLengthConstraint() {
      return this.testPropertyWithMinMaxLengthConstraint;
   }

   /**
    * Returns testPropertyWithMinLengthConstraint
    *
    * @return {@link #testPropertyWithMinLengthConstraint}
    */
   public BigInteger getTestPropertyWithMinLengthConstraint() {
      return this.testPropertyWithMinLengthConstraint;
   }

   /**
    * Returns testPropertyCollectionLengthConstraint
    *
    * @return {@link #testPropertyCollectionLengthConstraint}
    */
   public List<BigInteger> getTestPropertyCollectionLengthConstraint() {
      return this.testPropertyCollectionLengthConstraint;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final AspectWithConstraints that = (AspectWithConstraints) o;
      return Objects.equals( testPropertyWithRegularExpression, that.testPropertyWithRegularExpression )
            && Objects.equals(
            testPropertyWithDecimalMinDecimalMaxRangeConstraint,
            that.testPropertyWithDecimalMinDecimalMaxRangeConstraint )
            && Objects.equals(
            testPropertyWithDecimalMaxRangeConstraint,
            that.testPropertyWithDecimalMaxRangeConstraint )
            && Objects.equals(
            testPropertyWithMinMaxRangeConstraint, that.testPropertyWithMinMaxRangeConstraint )
            && Objects.equals(
            testPropertyWithMinRangeConstraint, that.testPropertyWithMinRangeConstraint )
            && Objects.equals(
            testPropertyRangeConstraintWithFloatType, that.testPropertyRangeConstraintWithFloatType )
            && Objects.equals(
            testPropertyRangeConstraintWithDoubleType,
            that.testPropertyRangeConstraintWithDoubleType )
            && Objects.equals(
            testPropertyWithMinMaxLengthConstraint, that.testPropertyWithMinMaxLengthConstraint )
            && Objects.equals(
            testPropertyWithMinLengthConstraint, that.testPropertyWithMinLengthConstraint )
            && Objects.equals(
            testPropertyCollectionLengthConstraint, that.testPropertyCollectionLengthConstraint );
   }
}
