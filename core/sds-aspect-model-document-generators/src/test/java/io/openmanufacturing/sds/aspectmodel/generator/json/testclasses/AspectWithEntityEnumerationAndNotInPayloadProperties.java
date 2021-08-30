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
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class AspectWithEntityEnumerationAndNotInPayloadProperties {

   private final SystemStates systemState;

   @JsonCreator
   public AspectWithEntityEnumerationAndNotInPayloadProperties(
         @JsonProperty( value = "systemState" ) final SystemStates systemState ) {
      this.systemState = systemState;
   }

   public SystemStates getSystemState() {
      return systemState;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final AspectWithEntityEnumerationAndNotInPayloadProperties that = (AspectWithEntityEnumerationAndNotInPayloadProperties) o;
      return Objects.equals( systemState, that.systemState );
   }

   @JsonFormat( shape = JsonFormat.Shape.OBJECT )
   public enum SystemStates {
      OFF( new SystemState( Short.valueOf( "0" ), "Off" ) ),
      COOL_DOWN( new SystemState( Short.valueOf( "3" ), "CoolDown" ) ),
      HEAT_UP( new SystemState( Short.valueOf( "4" ), "HeatUp" ) ),
      ON( new SystemState( Short.valueOf( "1" ), "On" ) );

      private final SystemState value;

      SystemStates( final SystemState value ) {
         this.value = value;
      }

      @JsonCreator
      static SystemStates enumDeserializationConstructor( final SystemState value ) {
         return fromValue( value )
               .orElseThrow( () -> new RuntimeException( "No enum present for json input" ) );
      }

      @JsonValue
      public SystemState getValue() {
         return value;
      }

      public static Optional<SystemStates> fromValue( final SystemState value ) {
         return Arrays.stream( SystemStates.values() )
                      .filter( enumValue -> compareEnumValues( enumValue, value ) )
                      .findAny();
      }

      private static boolean compareEnumValues( final SystemStates enumValue, final SystemState value ) {
         return enumValue.getValue().getState().equals( value.getState() );
      }
   }

   public static class SystemState {
      private final Short state;
      private String description;

      public SystemState( final Short state, final String description ) {
         this.state = state;
         this.description = description;
      }

      @JsonCreator
      public SystemState( @JsonProperty( value = "state" ) final Short state ) {
         this.state = state;
      }

      public Short getState() {
         return this.state;
      }

      @JsonIgnore
      public String getDescription() {
         return this.description;
      }

      @Override
      public boolean equals( final Object o ) {
         if ( this == o ) {
            return true;
         }
         if ( o == null || getClass() != o.getClass() ) {
            return false;
         }
         final SystemState that = (SystemState) o;
         return Objects.equals( state, that.state ) && Objects.equals( description, that.description );
      }
   }
}
