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

import java.util.Objects;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.esmf.aspectmodel.java.customconstraint.GregorianCalendarMax;
import org.eclipse.esmf.aspectmodel.java.customconstraint.GregorianCalendarMin;
import org.eclipse.esmf.metamodel.BoundDefinition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

/** Generated class for AspectWithGTypeForRangeConstraints. */
public class AspectWithGTypeForRangeConstraints {

   @NotNull
   @GregorianCalendarMin( value = "2000", boundDefinition = BoundDefinition.AT_LEAST )
   @GregorianCalendarMax( value = "2001", boundDefinition = BoundDefinition.AT_MOST )
   private XMLGregorianCalendar testPropertyWithGYear;

   @NotNull
   @GregorianCalendarMin( value = "--04", boundDefinition = BoundDefinition.AT_LEAST )
   @GregorianCalendarMax( value = "--05", boundDefinition = BoundDefinition.AT_MOST )
   private XMLGregorianCalendar testPropertyWithGMonth;

   @NotNull
   @GregorianCalendarMin( value = "---04", boundDefinition = BoundDefinition.AT_LEAST )
   @GregorianCalendarMax( value = "---05", boundDefinition = BoundDefinition.AT_MOST )
   private XMLGregorianCalendar testPropertyWithGDay;

   @NotNull
   @GregorianCalendarMin( value = "2000-01", boundDefinition = BoundDefinition.AT_LEAST )
   @GregorianCalendarMax( value = "2000-02", boundDefinition = BoundDefinition.AT_MOST )
   private XMLGregorianCalendar testPropertyWithGYearMonth;

   @NotNull
   @GregorianCalendarMin( value = "--01-01", boundDefinition = BoundDefinition.AT_LEAST )
   @GregorianCalendarMax( value = "--01-02", boundDefinition = BoundDefinition.AT_MOST )
   private XMLGregorianCalendar testPropertyWithGMonthYear;

   @JsonCreator
   public AspectWithGTypeForRangeConstraints(
         @JsonProperty( value = "testPropertyWithGYear" ) XMLGregorianCalendar testPropertyWithGYear,
         @JsonProperty( value = "testPropertyWithGMonth" ) XMLGregorianCalendar testPropertyWithGMonth,
         @JsonProperty( value = "testPropertyWithGDay" ) XMLGregorianCalendar testPropertyWithGDay,
         @JsonProperty( value = "testPropertyWithGYearMonth" ) XMLGregorianCalendar testPropertyWithGYearMonth,
         @JsonProperty( value = "testPropertyWithGMonthYear" ) XMLGregorianCalendar testPropertyWithGMonthYear ) {
      this.testPropertyWithGYear = testPropertyWithGYear;
      this.testPropertyWithGMonth = testPropertyWithGMonth;
      this.testPropertyWithGDay = testPropertyWithGDay;
      this.testPropertyWithGYearMonth = testPropertyWithGYearMonth;
      this.testPropertyWithGMonthYear = testPropertyWithGMonthYear;
   }

   /**
    * Returns TestPropertyWithGYear
    *
    * @return {@link #testPropertyWithGYear}
    */
   public XMLGregorianCalendar getTestPropertyWithGYear() {
      return this.testPropertyWithGYear;
   }

   /**
    * Returns TestPropertyWithGMonth
    *
    * @return {@link #testPropertyWithGMonth}
    */
   public XMLGregorianCalendar getTestPropertyWithGMonth() {
      return this.testPropertyWithGMonth;
   }

   /**
    * Returns TestPropertyWithGDay
    *
    * @return {@link #testPropertyWithGDay}
    */
   public XMLGregorianCalendar getTestPropertyWithGDay() {
      return this.testPropertyWithGDay;
   }

   /**
    * Returns TestPropertyWithGYearMonth
    *
    * @return {@link #testPropertyWithGYearMonth}
    */
   public XMLGregorianCalendar getTestPropertyWithGYearMonth() {
      return this.testPropertyWithGYearMonth;
   }

   /**
    * Returns TestPropertyWithGMonthYear
    *
    * @return {@link #testPropertyWithGMonthYear}
    */
   public XMLGregorianCalendar getTestPropertyWithGMonthYear() {
      return this.testPropertyWithGMonthYear;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final AspectWithGTypeForRangeConstraints that = (AspectWithGTypeForRangeConstraints) o;
      return Objects.equals( testPropertyWithGYear, that.testPropertyWithGYear )
            && Objects.equals( testPropertyWithGMonth, that.testPropertyWithGMonth )
            && Objects.equals( testPropertyWithGDay, that.testPropertyWithGDay )
            && Objects.equals( testPropertyWithGYearMonth, that.testPropertyWithGYearMonth )
            && Objects.equals( testPropertyWithGMonthYear, that.testPropertyWithGMonthYear );
   }
}
