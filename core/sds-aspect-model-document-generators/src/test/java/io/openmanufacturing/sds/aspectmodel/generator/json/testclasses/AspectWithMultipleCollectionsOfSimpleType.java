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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AspectWithMultipleCollectionsOfSimpleType {

   private final List<Integer> testListInt;
   private final List<String> testListString;

   public AspectWithMultipleCollectionsOfSimpleType( @JsonProperty( "testListInt" ) final List<Integer> testListInt,
         @JsonProperty( "testListString" ) final List<String> testListString ) {
      this.testListInt = testListInt;
      this.testListString = testListString;
   }

   public List<Integer> getTestListInt() {
      return testListInt;
   }

   public List<String> getTestListString() {
      return testListString;
   }
}
