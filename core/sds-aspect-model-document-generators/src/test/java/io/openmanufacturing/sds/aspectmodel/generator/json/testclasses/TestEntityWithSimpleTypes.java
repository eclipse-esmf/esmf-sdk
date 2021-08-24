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

import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestEntityWithSimpleTypes {

   private final String testString;
   private final int testInt;
   private final Float testFloat;
   private final XMLGregorianCalendar testLocalDateTime;
   private final String randomValue;

   public TestEntityWithSimpleTypes( @JsonProperty( "testString" ) final String testString,
         @JsonProperty( "testInt" ) final int testInt,
         @JsonProperty( "testFloat" ) final Float testFloat,
         @JsonProperty( "testLocalDateTime" ) final XMLGregorianCalendar testLocalDateTime,
         @JsonProperty( "randomValue" ) final String randomValue ) {
      this.testString = testString;
      this.testInt = testInt;
      this.testFloat = testFloat;
      this.testLocalDateTime = testLocalDateTime;
      this.randomValue = randomValue;
   }

   public String getTestString() {
      return testString;
   }

   public int getTestInt() {
      return testInt;
   }

   public Float getTestFloat() {
      return testFloat;
   }

   public XMLGregorianCalendar getTestLocalDateTime() {
      return testLocalDateTime;
   }

   public String getRandomValue() {
      return randomValue;
   }
}
