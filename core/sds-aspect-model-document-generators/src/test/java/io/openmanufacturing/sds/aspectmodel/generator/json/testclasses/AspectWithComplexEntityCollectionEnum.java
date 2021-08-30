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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import io.openmanufacturing.sds.aspectmodel.java.exception.EnumAttributeNotFoundException;

/** Generated class for AspectWithComplexEntityCollectionEnum. */
public class AspectWithComplexEntityCollectionEnum {

   private MyEnumerationOne myPropertyOne;

   @JsonCreator
   public AspectWithComplexEntityCollectionEnum(
         @JsonProperty( value = "myPropertyOne" ) MyEnumerationOne myPropertyOne ) {
      this.myPropertyOne = myPropertyOne;
   }

   /**
    * Returns myPropertyOne
    *
    * @return {@link #myPropertyOne}
    */
   public MyEnumerationOne getMyPropertyOne() {
      return this.myPropertyOne;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final AspectWithComplexEntityCollectionEnum that = (AspectWithComplexEntityCollectionEnum) o;
      return Objects.equals( myPropertyOne, that.myPropertyOne );
   }

   /** Generated class for MyEntityOne. */
   public static class MyEntityOne {

      private List<MyEntityTwo> entityPropertyOne;

      @JsonCreator
      public MyEntityOne(
            @JsonProperty( value = "entityPropertyOne" ) List<MyEntityTwo> entityPropertyOne ) {
         this.entityPropertyOne = entityPropertyOne;
      }

      /**
       * Returns entityPropertyOne
       *
       * @return {@link #entityPropertyOne}
       */
      public List<MyEntityTwo> getEntityPropertyOne() {
         return this.entityPropertyOne;
      }

      @Override
      public boolean equals( final Object o ) {
         if ( this == o ) {
            return true;
         }
         if ( o == null || getClass() != o.getClass() ) {
            return false;
         }
         final MyEntityOne that = (MyEntityOne) o;
         return Objects.equals( entityPropertyOne, that.entityPropertyOne );
      }
   }

   /** Generated class for MyEntityTwo. */
   public static class MyEntityTwo {

      private String entityPropertyTwo;

      @JsonCreator
      public MyEntityTwo( @JsonProperty( value = "entityPropertyTwo" ) String entityPropertyTwo ) {
         this.entityPropertyTwo = entityPropertyTwo;
      }

      /**
       * Returns entityPropertyTwo
       *
       * @return {@link #entityPropertyTwo}
       */
      public String getEntityPropertyTwo() {
         return this.entityPropertyTwo;
      }

      @Override
      public boolean equals( final Object o ) {
         if ( this == o ) {
            return true;
         }
         if ( o == null || getClass() != o.getClass() ) {
            return false;
         }
         final MyEntityTwo that = (MyEntityTwo) o;
         return Objects.equals( entityPropertyTwo, that.entityPropertyTwo );
      }
   }

   /** Generated class {@link MyEnumerationOne}. */
   @JsonFormat( shape = JsonFormat.Shape.OBJECT )
   public enum MyEnumerationOne {
      ENTITY_INSTANCE_ONE( new MyEntityOne( List.of( new MyEntityTwo( "foo" ) ) ) );

      private MyEntityOne value;

      MyEnumerationOne( MyEntityOne value ) {
         this.value = value;
      }

      @JsonCreator
      static MyEnumerationOne enumDeserializationConstructor( MyEntityOne value ) {
         return fromValue( value )
               .orElseThrow(
                     () ->
                           new EnumAttributeNotFoundException(
                                 "Tried to parse value \""
                                       + value
                                       + "\", but there is no enum field like that in MyEnumerationOne" ) );
      }

      @JsonValue
      public MyEntityOne getValue() {
         return value;
      }

      public static Optional<MyEnumerationOne> fromValue( MyEntityOne value ) {
         return Arrays.stream( MyEnumerationOne.values() )
                      .filter( enumValue -> compareEnumValues( enumValue, value ) )
                      .findAny();
      }

      private static boolean compareEnumValues( MyEnumerationOne enumValue, MyEntityOne value ) {
         return enumValue.getValue().getEntityPropertyOne().equals( value.getEntityPropertyOne() );
      }
   }
}

