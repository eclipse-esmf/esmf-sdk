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

package org.eclipse.esmf.aspectmodel.generator.json;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generated class for Test Aspect.
 */
public class AspectWithRangeConstraintWithoutMinMaxDoubleValue {

   private final Double doubleProperty;

   @JsonCreator
   public AspectWithRangeConstraintWithoutMinMaxDoubleValue(
         @JsonProperty( value = "doubleProperty" ) final Double doubleProperty ) {
      this.doubleProperty = doubleProperty;
   }

   /**
    * Returns Test Double Property
    *
    * @return {@link #doubleProperty}
    */
   public Double getDoubleProperty() {
      return doubleProperty;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final AspectWithRangeConstraintWithoutMinMaxDoubleValue that =
            (AspectWithRangeConstraintWithoutMinMaxDoubleValue) o;
      return Objects.equals( doubleProperty, that.doubleProperty );
   }
}
