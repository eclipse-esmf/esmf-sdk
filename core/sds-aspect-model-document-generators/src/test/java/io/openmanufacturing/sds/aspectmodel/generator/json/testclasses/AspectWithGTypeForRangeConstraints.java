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
package io.openmanufacturing.sds.aspectmodel.generator.json.testclasses;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.openmanufacturing.sds.aspectmodel.java.customconstraint.GregorianCalendarMax;
import io.openmanufacturing.sds.aspectmodel.java.customconstraint.GregorianCalendarMin;
import io.openmanufacturing.sds.metamodel.impl.BoundDefinition;

/** Generated class for AspectWithGTypeForRangeConstraints. */
public class AspectWithGTypeForRangeConstraints {

   @NotNull
   @GregorianCalendarMin( value = "2000", boundDefinition = BoundDefinition.AT_LEAST )
   @GregorianCalendarMax( value = "2001", boundDefinition = BoundDefinition.AT_MOST )
   private XMLGregorianCalendar TestPropertyWithGYear;

   @NotNull
   @GregorianCalendarMin( value = "--04", boundDefinition = BoundDefinition.AT_LEAST )
   @GregorianCalendarMax( value = "--05", boundDefinition = BoundDefinition.AT_MOST )
   private XMLGregorianCalendar TestPropertyWithGMonth;

   @NotNull
   @GregorianCalendarMin( value = "---04", boundDefinition = BoundDefinition.AT_LEAST )
   @GregorianCalendarMax( value = "---05", boundDefinition = BoundDefinition.AT_MOST )
   private XMLGregorianCalendar TestPropertyWithGDay;

   @NotNull
   @GregorianCalendarMin( value = "2000-01", boundDefinition = BoundDefinition.AT_LEAST )
   @GregorianCalendarMax( value = "2000-02", boundDefinition = BoundDefinition.AT_MOST )
   private XMLGregorianCalendar TestPropertyWithGYearMonth;

   @NotNull
   @GregorianCalendarMin( value = "--01-01", boundDefinition = BoundDefinition.AT_LEAST )
   @GregorianCalendarMax( value = "--01-02", boundDefinition = BoundDefinition.AT_MOST )
   private XMLGregorianCalendar TestPropertyWithGMonthYear;

   @JsonCreator
   public AspectWithGTypeForRangeConstraints(
         @JsonProperty( value = "TestPropertyWithGYear" ) XMLGregorianCalendar TestPropertyWithGYear,
         @JsonProperty( value = "TestPropertyWithGMonth" ) XMLGregorianCalendar TestPropertyWithGMonth,
         @JsonProperty( value = "TestPropertyWithGDay" ) XMLGregorianCalendar TestPropertyWithGDay,
         @JsonProperty( value = "TestPropertyWithGYearMonth" )
               XMLGregorianCalendar TestPropertyWithGYearMonth,
         @JsonProperty( value = "TestPropertyWithGMonthYear" )
               XMLGregorianCalendar TestPropertyWithGMonthYear ) {
      this.TestPropertyWithGYear = TestPropertyWithGYear;
      this.TestPropertyWithGMonth = TestPropertyWithGMonth;
      this.TestPropertyWithGDay = TestPropertyWithGDay;
      this.TestPropertyWithGYearMonth = TestPropertyWithGYearMonth;
      this.TestPropertyWithGMonthYear = TestPropertyWithGMonthYear;
   }

   /**
    * Returns TestPropertyWithGYear
    *
    * @return {@link #TestPropertyWithGYear}
    */
   public XMLGregorianCalendar getTestPropertyWithGYear() {
      return this.TestPropertyWithGYear;
   }

   /**
    * Returns TestPropertyWithGMonth
    *
    * @return {@link #TestPropertyWithGMonth}
    */
   public XMLGregorianCalendar getTestPropertyWithGMonth() {
      return this.TestPropertyWithGMonth;
   }

   /**
    * Returns TestPropertyWithGDay
    *
    * @return {@link #TestPropertyWithGDay}
    */
   public XMLGregorianCalendar getTestPropertyWithGDay() {
      return this.TestPropertyWithGDay;
   }

   /**
    * Returns TestPropertyWithGYearMonth
    *
    * @return {@link #TestPropertyWithGYearMonth}
    */
   public XMLGregorianCalendar getTestPropertyWithGYearMonth() {
      return this.TestPropertyWithGYearMonth;
   }

   /**
    * Returns TestPropertyWithGMonthYear
    *
    * @return {@link #TestPropertyWithGMonthYear}
    */
   public XMLGregorianCalendar getTestPropertyWithGMonthYear() {
      return this.TestPropertyWithGMonthYear;
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
      return Objects.equals( TestPropertyWithGYear, that.TestPropertyWithGYear )
            && Objects.equals( TestPropertyWithGMonth, that.TestPropertyWithGMonth )
            && Objects.equals( TestPropertyWithGDay, that.TestPropertyWithGDay )
            && Objects.equals( TestPropertyWithGYearMonth, that.TestPropertyWithGYearMonth )
            && Objects.equals( TestPropertyWithGMonthYear, that.TestPropertyWithGMonthYear );
   }
}
