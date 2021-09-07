/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */
package io.openmanufacturing.sds.aspectmodel.generator.json.testclasses;

import java.math.BigInteger;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo( use = JsonTypeInfo.Id.NAME )
@JsonSubTypes( {
      @JsonSubTypes.Type( value = ExtendingTestEntity.class, name = "ExtendingTestEntity" )
} )
public abstract class AbstractTestEntity {

   @NotNull
   private final BigInteger abstractTestProperty;

   @JsonCreator
   public AbstractTestEntity(
         @JsonProperty( value = "abstractTestProperty" ) final BigInteger abstractTestProperty ) {
      super();
      this.abstractTestProperty = abstractTestProperty;
   }

   public BigInteger getAbstractTestProperty() {
      return abstractTestProperty;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final AbstractTestEntity that = (AbstractTestEntity) o;
      return Objects.equals( abstractTestProperty, that.abstractTestProperty );
   }
}
