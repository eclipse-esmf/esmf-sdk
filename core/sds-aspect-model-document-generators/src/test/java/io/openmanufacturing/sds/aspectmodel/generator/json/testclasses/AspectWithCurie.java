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

public class AspectWithCurie {

   private final String testCurie;
   private final String testCurieWithoutExampleValue;

   public AspectWithCurie( @JsonProperty( "testCurie" ) final String testCurie,
         @JsonProperty( "testCurieWithoutExampleValue" ) final String testCurieWithoutExampleValue ) {
      this.testCurie = testCurie;
      this.testCurieWithoutExampleValue = testCurieWithoutExampleValue;
   }

   public String getTestCurie() {
      return testCurie;
   }

   public String getTestCurieWithoutExampleValue() {
      return testCurieWithoutExampleValue;
   }
}
