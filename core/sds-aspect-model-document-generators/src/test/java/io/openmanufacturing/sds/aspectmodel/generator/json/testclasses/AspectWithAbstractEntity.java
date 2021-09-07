/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */
package io.openmanufacturing.sds.aspectmodel.generator.json.testclasses;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AspectWithAbstractEntity {

   @NotNull
   private final ExtendingTestEntity testProperty;

   @JsonCreator
   public AspectWithAbstractEntity(
         @JsonProperty( value = "testProperty" ) final ExtendingTestEntity testProperty ) {
      super();
      this.testProperty = testProperty;
   }

   public ExtendingTestEntity getTestProperty() {
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
      final AspectWithAbstractEntity that = (AspectWithAbstractEntity) o;
      return Objects.equals( testProperty, that.testProperty );
   }
}
