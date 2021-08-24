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

import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.openmanufacturing.sds.aspectmodel.java.customconstraint.GregorianCalendarMax;
import io.openmanufacturing.sds.aspectmodel.java.customconstraint.GregorianCalendarMin;
import io.openmanufacturing.sds.metamodel.impl.BoundDefinition;

/** Generated class for AspectWithDateTimeTypeForRangeConstraints. */
public class AspectWithDateTimeTypeForRangeConstraints {

   @GregorianCalendarMin( value = "2000-01-01T14:23:00", boundDefinition = BoundDefinition.AT_LEAST )
   @GregorianCalendarMax( value = "2000-01-02T15:23:00", boundDefinition = BoundDefinition.AT_MOST )
   private XMLGregorianCalendar TestPropertyWithDateTime;

   @GregorianCalendarMin(
         value = "2000-01-01T14:23:00.66372+14:00",
         boundDefinition = BoundDefinition.AT_LEAST )
   @GregorianCalendarMax(
         value = "2000-01-01T15:23:00.66372+14:00",
         boundDefinition = BoundDefinition.AT_MOST )
   private XMLGregorianCalendar TestPropertyWithDateTimeStamp;

   @JsonCreator
   public AspectWithDateTimeTypeForRangeConstraints(
         @JsonProperty( value = "TestPropertyWithDateTime" )
               XMLGregorianCalendar TestPropertyWithDateTime,
         @JsonProperty( value = "TestPropertyWithDateTimeStamp" )
               XMLGregorianCalendar TestPropertyWithDateTimeStamp ) {
      this.TestPropertyWithDateTime = TestPropertyWithDateTime;
      this.TestPropertyWithDateTimeStamp = TestPropertyWithDateTimeStamp;
   }

   /**
    * Returns TestPropertyWithDateTime
    *
    * @return {@link #TestPropertyWithDateTime}
    */
   public XMLGregorianCalendar getTestPropertyWithDateTime() {
      return this.TestPropertyWithDateTime;
   }

   /**
    * Returns TestPropertyWithDateTimeStamp
    *
    * @return {@link #TestPropertyWithDateTimeStamp}
    */
   public XMLGregorianCalendar getTestPropertyWithDateTimeStamp() {
      return this.TestPropertyWithDateTimeStamp;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final AspectWithDateTimeTypeForRangeConstraints that =
            (AspectWithDateTimeTypeForRangeConstraints) o;
      return Objects.equals( TestPropertyWithDateTime, that.TestPropertyWithDateTime )
            && Objects.equals( TestPropertyWithDateTimeStamp, that.TestPropertyWithDateTimeStamp );
   }
}
