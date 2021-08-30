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

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generated class for AspectWithSimpleTypes.
 */
public class AspectWithSimpleProperties {

   private final String testString;

   private final Integer testInt;

   private final Float testFloat;

   private final XMLGregorianCalendar testLocalDateTime;

   private final XMLGregorianCalendar testLocalDateTimeWithoutExample;

   private final Duration testDurationWithoutExample;

   private final String randomValue;

   @JsonCreator
   public AspectWithSimpleProperties(
         @JsonProperty( value = "testString" ) final String testString,
         @JsonProperty( value = "testInt" ) final Integer testInt,
         @JsonProperty( value = "testFloat" ) final Float testFloat,
         @JsonProperty( value = "testLocalDateTime" ) final XMLGregorianCalendar testLocalDateTime,
         @JsonProperty( value = "testLocalDateTimeWithoutExample" ) final
         XMLGregorianCalendar testLocalDateTimeWithoutExample,
         @JsonProperty( value = "testDurationWithoutExample" ) final Duration testDurationWithoutExample,
         @JsonProperty( value = "randomValue" ) final String randomValue ) {
      this.testString = testString;
      this.testInt = testInt;
      this.testFloat = testFloat;
      this.testLocalDateTime = testLocalDateTime;
      this.testLocalDateTimeWithoutExample = testLocalDateTimeWithoutExample;
      this.testDurationWithoutExample = testDurationWithoutExample;
      this.randomValue = randomValue;
   }

   /**
    * Returns testString
    *
    * @return {@link #testString}
    */
   public String getTestString() {
      return testString;
   }

   /**
    * Returns testInt
    *
    * @return {@link #testInt}
    */
   public Integer getTestInt() {
      return testInt;
   }

   /**
    * Returns testFloat
    *
    * @return {@link #testFloat}
    */
   public Float getTestFloat() {
      return testFloat;
   }

   /**
    * Returns testLocalDateTime
    *
    * @return {@link #testLocalDateTime}
    */
   public XMLGregorianCalendar getTestLocalDateTime() {
      return testLocalDateTime;
   }

   /**
    * Returns testLocalDateTimeWithoutExample
    *
    * @return {@link #testLocalDateTimeWithoutExample}
    */
   public XMLGregorianCalendar getTestLocalDateTimeWithoutExample() {
      return testLocalDateTimeWithoutExample;
   }

   /**
    * Returns testDurationWithoutExample
    *
    * @return {@link #testDurationWithoutExample}
    */
   public Duration getTestDurationWithoutExample() {
      return testDurationWithoutExample;
   }

   /**
    * Returns randomValue
    *
    * @return {@link #randomValue}
    */
   public String getRandomValue() {
      return randomValue;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final AspectWithSimpleProperties that = (AspectWithSimpleProperties) o;
      return Objects.equals( testString, that.testString )
            && Objects.equals( testInt, that.testInt )
            && Objects.equals( testFloat, that.testFloat )
            && Objects.equals( testLocalDateTime, that.testLocalDateTime )
            && Objects.equals( testLocalDateTimeWithoutExample, that.testLocalDateTimeWithoutExample )
            && Objects.equals( testDurationWithoutExample, that.testDurationWithoutExample )
            && Objects.equals( randomValue, that.randomValue );
   }
}