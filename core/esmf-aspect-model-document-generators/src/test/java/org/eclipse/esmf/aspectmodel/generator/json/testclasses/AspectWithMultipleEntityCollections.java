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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AspectWithMultipleEntityCollections {

   private final List<TestEntityWithSimpleTypes> testListOne;
   private final List<TestEntityWithSimpleTypes> testListTwo;

   public AspectWithMultipleEntityCollections(
         @JsonProperty( "testListOne" ) final List<TestEntityWithSimpleTypes> testListOne,
         @JsonProperty( "testListTwo" ) final List<TestEntityWithSimpleTypes> testListTwo ) {
      this.testListOne = testListOne;
      this.testListTwo = testListTwo;
   }

   public List<TestEntityWithSimpleTypes> getTestListOne() {
      return testListOne;
   }

   public List<TestEntityWithSimpleTypes> getTestListTwo() {
      return testListTwo;
   }
}
