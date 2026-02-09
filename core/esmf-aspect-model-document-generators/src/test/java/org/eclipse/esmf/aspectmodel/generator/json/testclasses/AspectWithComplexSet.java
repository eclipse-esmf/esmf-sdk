/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Generated class for Test Aspect. This is a test description
 */
public class AspectWithComplexSet {

   @NotNull
   @Size( min = 2 )

   private Set<Id> testProperty;

   @JsonCreator
   public AspectWithComplexSet( @JsonProperty( value = "testProperty" ) final Set<Id> testProperty ) {
      super(

      );
      this.testProperty = testProperty;
   }

   /**
    * Returns Test Property
    *
    * @return {@link #testProperty}
    */
   public Set<Id> getTestProperty() {
      return testProperty;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }

      final AspectWithComplexSet that = (AspectWithComplexSet) o;
      return Objects.equals( testProperty, that.testProperty );
   }

   @Override
   public int hashCode() {
      return Objects.hash( testProperty );
   }
}
