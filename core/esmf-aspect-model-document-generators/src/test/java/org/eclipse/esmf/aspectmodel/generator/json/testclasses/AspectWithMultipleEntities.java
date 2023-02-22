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

import com.fasterxml.jackson.annotation.JsonProperty;

public class AspectWithMultipleEntities {

   private final TestEntityWithSimpleTypes testEntityOne;
   private final TestEntityWithSimpleTypes testEntityTwo;

   public AspectWithMultipleEntities( @JsonProperty( "testEntityOne" ) final TestEntityWithSimpleTypes testEntityOne,
         @JsonProperty( "testEntityTwo" ) final TestEntityWithSimpleTypes testEntityTwo ) {
      this.testEntityOne = testEntityOne;
      this.testEntityTwo = testEntityTwo;
   }

   public TestEntityWithSimpleTypes getTestEntityOne() {
      return testEntityOne;
   }

   public TestEntityWithSimpleTypes getTestEntityTwo() {
      return testEntityTwo;
   }
}
