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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generated class for AspectWithPropertyWithPayloadName.
 */
public class AspectWithPropertyWithPayloadName {

   private final String test;

   @JsonCreator
   public AspectWithPropertyWithPayloadName(
         @JsonProperty( value = "test" ) final String test ) {
      this.test = test;
   }

   /**
    * Returns test
    *
    * @return {@link #test}
    */
   public String getTest() {
      return test;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final AspectWithPropertyWithPayloadName that = (AspectWithPropertyWithPayloadName) o;
      return Objects.equals( test, that.test );
   }
}