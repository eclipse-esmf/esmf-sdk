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

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Copy of {@link AspectWithComplexSet}, but using a List instead of a Set; used specifically in
 * {@link AspectModelJsonPayloadGeneratorTest#testGenerateJsonForAspectWithComplexCollectionOfMinSize2AndExampleValue()}
 */
public class AspectWithComplexSetAsList {

   @NotNull
   @Size( min = 2 )

   private final List<Id> testProperty;

   @JsonCreator
   public AspectWithComplexSetAsList( @JsonProperty( value = "testProperty" ) final List<Id> testProperty ) {
      super(

      );
      this.testProperty = testProperty;
   }

   /**
    * Returns Test Property
    *
    * @return {@link #testProperty}
    */
   public List<Id> getTestProperty() {
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

      final AspectWithComplexSetAsList that = (AspectWithComplexSetAsList) o;
      return Objects.equals( testProperty, that.testProperty );
   }

   @Override
   public int hashCode() {
      return Objects.hash( testProperty );
   }
}
