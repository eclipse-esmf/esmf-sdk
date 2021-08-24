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

public class TestEntityEvaluationResult {
   private final short numericCode;
   private final String description;
   private final String enumerationKey;

   public TestEntityEvaluationResult( @JsonProperty( "numericCode" ) final short numericCode,
         @JsonProperty( "description" ) final String description,
         @JsonProperty( "enumerationKey" ) final String enumerationKey ) {
      this.numericCode = numericCode;
      this.description = description;
      this.enumerationKey = enumerationKey;
   }

   public short getNumericCode() {
      return numericCode;
   }

   public String getDescription() {
      return description;
   }

   public String getEnumerationKey() {
      return enumerationKey;
   }
}
