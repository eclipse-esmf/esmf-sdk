/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */
package io.openmanufacturing.sds.aspectmodel.generator.json.testclasses;

import java.math.BigInteger;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtendingTestEntity extends AbstractTestEntity {

   @NotNull
   private final String entityProperty;

   @JsonCreator
   public ExtendingTestEntity(
         @JsonProperty( value = "entityProperty" ) final String entityProperty,
         @JsonProperty( value = "abstractTestProperty" ) final BigInteger abstractTestProperty ) {
      super( abstractTestProperty );
      this.entityProperty = entityProperty;
   }

   public String getEntityProperty() {
      return entityProperty;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final ExtendingTestEntity that = (ExtendingTestEntity) o;
      return Objects.equals( entityProperty, that.entityProperty );
   }
}
