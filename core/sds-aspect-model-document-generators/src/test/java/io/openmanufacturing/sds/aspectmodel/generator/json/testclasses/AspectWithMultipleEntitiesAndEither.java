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

import com.fasterxml.jackson.annotation.JsonProperty;

public class AspectWithMultipleEntitiesAndEither {

   private final TestEntityWithSimpleTypes testEntityOne;
   private final TestEntityWithSimpleTypes testEntityTwo;
   private final Either<TestEntityWithSimpleTypes, String> testEither;

   public AspectWithMultipleEntitiesAndEither(
         @JsonProperty( "testEntityOne" ) final TestEntityWithSimpleTypes testEntityOne,
         @JsonProperty( "testEntityTwo" ) final TestEntityWithSimpleTypes testEntityTwo,
         @JsonProperty( "testEither" ) final Either<TestEntityWithSimpleTypes, String> testEither
   ) {
      this.testEntityOne = testEntityOne;
      this.testEntityTwo = testEntityTwo;
      this.testEither = testEither;
   }

   public TestEntityWithSimpleTypes getTestEntityOne() {
      return testEntityOne;
   }

   public TestEntityWithSimpleTypes getTestEntityTwo() {
      return testEntityTwo;
   }

   public Either<TestEntityWithSimpleTypes, String> getTestEither() {
      return testEither;
   }
}
