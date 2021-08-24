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

public class AspectWithEnum {

   private TestEntityEvaluationResult result;
   private String simpleResult;

   public AspectWithEnum( @JsonProperty( "result" ) TestEntityEvaluationResult result,
         @JsonProperty( "simpleResult" ) String simpleResult ) {
      this.result = result;
      this.simpleResult = simpleResult;
   }

   public TestEntityEvaluationResult getResult() {
      return result;
   }

   public String getSimpleResult() {
      return simpleResult;
   }

}
