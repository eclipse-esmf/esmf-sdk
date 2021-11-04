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

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AspectWithAbstractSingleEntity {

   @NotNull
   private final AbstractTestEntity testProperty;

   @JsonCreator
   public AspectWithAbstractSingleEntity( @JsonProperty( value = "testProperty" ) final AbstractTestEntity testProperty ) {
      super();
      this.testProperty = testProperty;
   }

   public AbstractTestEntity getTestProperty() {
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
      final AspectWithAbstractSingleEntity that = (AspectWithAbstractSingleEntity) o;
      return Objects.equals( testProperty, that.testProperty );
   }
}
