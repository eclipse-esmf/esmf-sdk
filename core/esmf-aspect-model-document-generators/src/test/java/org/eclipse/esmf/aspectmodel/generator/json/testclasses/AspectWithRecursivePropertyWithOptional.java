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

/** Generated class for AspectWithRecursivePropertyWithOptional. */
public class AspectWithRecursivePropertyWithOptional {

   @NotNull
   private TestEntity testProperty;

   @JsonCreator
   public AspectWithRecursivePropertyWithOptional(
         @JsonProperty( value = "testProperty" ) TestEntity testProperty ) {
      this.testProperty = testProperty;
   }

   /**
    * Returns testProperty
    *
    * @return {@link #testProperty}
    */
   public TestEntity getTestProperty() {
      return this.testProperty;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final AspectWithRecursivePropertyWithOptional that =
            (AspectWithRecursivePropertyWithOptional) o;
      return Objects.equals( testProperty, that.testProperty );
   }
}
