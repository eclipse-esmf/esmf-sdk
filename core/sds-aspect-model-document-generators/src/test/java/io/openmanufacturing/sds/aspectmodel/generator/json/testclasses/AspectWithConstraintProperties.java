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

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AspectWithConstraintProperties {

   private String stringLcProperty;
   private Double doubleRcProperty;
   private Integer intRcProperty;
   private BigInteger bigIntRcProperty;
   private Float floatRcProperty;
   private String stringRegexcProperty;

   public AspectWithConstraintProperties( @JsonProperty( "stringLcProperty" ) String stringLcProperty,
         @JsonProperty( "doubleRcProperty" ) Double doubleRcProperty,
         @JsonProperty( "intRcProperty" ) Integer intRcProperty,
         @JsonProperty( "bigIntRcProperty" ) BigInteger bigIntRcProperty,
         @JsonProperty( "floatRcProperty" ) Float floatRcProperty,
         @JsonProperty( "stringRegexcProperty" ) String stringRegexcProperty ) {
      this.stringLcProperty = stringLcProperty;
      this.doubleRcProperty = doubleRcProperty;
      this.intRcProperty = intRcProperty;
      this.bigIntRcProperty = bigIntRcProperty;
      this.floatRcProperty = floatRcProperty;
      this.stringRegexcProperty = stringRegexcProperty;
   }

   public String getStringLcProperty() {
      return stringLcProperty;
   }

   public Double getDoubleRcProperty() {
      return doubleRcProperty;
   }

   public Integer getIntRcProperty() {
      return intRcProperty;
   }

   public BigInteger getBigIntRcProperty() {
      return bigIntRcProperty;
   }

   public Float getFloatRcProperty() {
      return floatRcProperty;
   }

   public String getStringRegexcProperty() {
      return stringRegexcProperty;
   }
}
