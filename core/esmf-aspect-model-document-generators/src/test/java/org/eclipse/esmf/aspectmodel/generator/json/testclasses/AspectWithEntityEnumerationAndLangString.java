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

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class AspectWithEntityEnumerationAndLangString {

   TestEnumeration testProperty;

   @JsonCreator
   public AspectWithEntityEnumerationAndLangString(
         @JsonProperty( value = "testProperty" ) final TestEnumeration testProperty ) {
      this.testProperty = testProperty;
   }

   public TestEnumeration getTestProperty() {
      return testProperty;
   }

   public enum TestEnumeration {
      ENTITY_INSTANCE( new TestEntity( Map.of( "en", "This is a test." ) ) );

      private final TestEntity value;

      TestEnumeration( final TestEntity value ) {
         this.value = value;
      }

      @JsonCreator
      static TestEnumeration enumDeserializationConstructor( final TestEntity value ) {
         return fromValue( value )
               .orElseThrow( () -> new RuntimeException( "No enum present for json input" ) );
      }

      @JsonValue
      public TestEntity getValue() {
         return value;
      }

      public static Optional<TestEnumeration> fromValue( final TestEntity value ) {
         return Arrays.stream( TestEnumeration.values() )
               .filter( enumValue -> compareEnumValues( enumValue, value ) )
               .findAny();
      }

      private static boolean compareEnumValues( final TestEnumeration enumValue, final TestEntity value ) {
         return enumValue.getValue().getEntityProperty().equals( value.getEntityProperty() );
      }
   }

   public static class TestEntity {
      private final Map<String, String> entityProperty;

      @JsonCreator
      public TestEntity( @JsonProperty( value = "entityProperty" ) final Map<String, String> entityProperty ) {
         this.entityProperty = entityProperty;
      }

      public Map<String, String> getEntityProperty() {
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
         final TestEntity that = (TestEntity) o;
         return Objects.equals( entityProperty, that.entityProperty );
      }

      @Override
      public int hashCode() {
         return Objects.hash( entityProperty );
      }
   }
}
