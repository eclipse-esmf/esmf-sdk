/*
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH, Germany. All rights reserved.
 */
package io.openmanufacturing.sds.aspectmodel.generator.json.testclasses;

import java.util.Collection;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.openmanufacturing.sds.aspectmodel.java.CollectionAspect;

public class AspectWithCollectionWithAbstractEntity
      implements CollectionAspect<Collection<AbstractTestEntity>, AbstractTestEntity> {

   @NotNull
   private final Collection<AbstractTestEntity> testProperty;

   @JsonCreator
   public AspectWithCollectionWithAbstractEntity(
         @JsonProperty( value = "testProperty" ) final Collection<AbstractTestEntity> testProperty ) {
      super();
      this.testProperty = testProperty;
   }

   public Collection<AbstractTestEntity> getTestProperty() {
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
      final AspectWithCollectionWithAbstractEntity that = (AspectWithCollectionWithAbstractEntity) o;
      return Objects.equals( testProperty, that.testProperty );
   }
}
