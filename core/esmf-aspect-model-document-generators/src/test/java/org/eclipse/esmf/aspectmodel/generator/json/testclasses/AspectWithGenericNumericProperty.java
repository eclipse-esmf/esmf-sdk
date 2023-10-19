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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

/**
 * Class for testing aspects with variously typed and constrained numeric properties.
 */
public class AspectWithGenericNumericProperty {

   @NotNull
   private final Number testNumber;

   @JsonCreator
   public AspectWithGenericNumericProperty(
         @JsonProperty(value = "testNumber") Number testNumber) {
      this.testNumber = testNumber;
   }

   /**
    * Returns Test number Property
    *
    * @return {@link #testNumber}
    */
   public Number getTestNumber() {
      return this.testNumber;
   }

   @Override
   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }
      final AspectWithGenericNumericProperty that = (AspectWithGenericNumericProperty) o;
      return Objects.equals(testNumber, that.testNumber);
   }
}


