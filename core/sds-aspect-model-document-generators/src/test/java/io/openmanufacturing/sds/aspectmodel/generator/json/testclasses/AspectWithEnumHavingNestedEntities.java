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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class AspectWithEnumHavingNestedEntities {

   private final EvaluationResults result;
   private final YesNo simpleResult;

   @JsonCreator
   public AspectWithEnumHavingNestedEntities(
         @JsonProperty( "result" ) final EvaluationResults result,
         @JsonProperty( "simpleResult" ) final YesNo simpleResult ) {
      this.result = result;
      this.simpleResult = simpleResult;
   }

   public EvaluationResults getResult() {
      return this.result;
   }

   public YesNo getSimpleResult() {
      return this.simpleResult;
   }

   @Override
   public boolean equals( final Object o ) {
      if ( this == o ) {
         return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
         return false;
      }
      final AspectWithEnumHavingNestedEntities that = (AspectWithEnumHavingNestedEntities) o;
      return Objects.equals( result, that.result ) && Objects.equals( simpleResult, that.simpleResult );
   }

   @JsonFormat( shape = JsonFormat.Shape.OBJECT )
   public enum EvaluationResults {
      RESULT_GOOD( new EvaluationResult(
            new DetailEntity( "Result succeeded", "Evaluation succeeded.", Short.valueOf( "10" ) ) ) ),
      RESULT_BAD(
            new EvaluationResult( new DetailEntity( "No status", "No status available", Short.valueOf( "-10" ) ) ) );

      private final EvaluationResult value;

      EvaluationResults( final EvaluationResult value ) {
         this.value = value;
      }

      @JsonCreator
      static EvaluationResults enumDeserializationConstructor( final EvaluationResult value ) {
         return fromValue( value )
               .orElseThrow( () -> new RuntimeException( "No enum present for json input" ) );
      }

      @JsonValue
      public EvaluationResult getValue() {
         return value;
      }

      public static Optional<EvaluationResults> fromValue( final EvaluationResult value ) {
         return Arrays.stream( EvaluationResults.values() )
                      .filter( enumValue -> compareEnumValues( enumValue, value ) )
                      .findAny();
      }

      private static boolean compareEnumValues( final EvaluationResults enumValue, final EvaluationResult value ) {
         return enumValue.getValue().getDetails().equals( value.getDetails() );
      }
   }

   public static class EvaluationResult {
      private final DetailEntity details;

      @JsonCreator
      public EvaluationResult( @JsonProperty( value = "details" ) final DetailEntity details ) {
         this.details = details;
      }

      public DetailEntity getDetails() {
         return this.details;
      }

      @Override
      public boolean equals( final Object o ) {
         if ( this == o ) {
            return true;
         }
         if ( o == null || getClass() != o.getClass() ) {
            return false;
         }
         final EvaluationResult that = (EvaluationResult) o;
         return Objects.equals( details, that.details );
      }
   }

   public static class DetailEntity {
      private final String description;
      private final String message;
      private final Short numericCode;

      @JsonCreator
      public DetailEntity( @JsonProperty( value = "description" ) final String description,
            @JsonProperty( value = "message" ) final String message,
            @JsonProperty( value = "numericCode" ) final Short numericCode ) {
         this.description = description;
         this.message = message;
         this.numericCode = numericCode;
      }

      public String getDescription() {
         return this.description;
      }

      public String getMessage() {
         return this.message;
      }

      public Short getNumericCode() {
         return this.numericCode;
      }

      @Override
      public boolean equals( final Object o ) {
         if ( this == o ) {
            return true;
         }
         if ( o == null || getClass() != o.getClass() ) {
            return false;
         }
         final DetailEntity that = (DetailEntity) o;
         return Objects.equals( description, that.description ) && Objects.equals( message, that.message ) && Objects
               .equals( numericCode, that.numericCode );
      }
   }

   @JsonFormat( shape = JsonFormat.Shape.OBJECT )
   public enum YesNo {
      YES( "Yes" ),
      NO( "No" );

      private final String value;

      YesNo( final String value ) {
         this.value = value;
      }

      @JsonCreator
      static YesNo enumDeserializationConstructor( final String value ) {
         return fromValue( value ).orElseThrow( () -> new RuntimeException( "No enum present for json input" ) );
      }

      @JsonValue
      public String getValue() {
         return value;
      }

      public static Optional<YesNo> fromValue( final String value ) {
         return Arrays.stream( YesNo.values() )
                      .filter( enumValue -> compareEnumValues( enumValue, value ) )
                      .findAny();
      }

      private static boolean compareEnumValues( final YesNo enumValue, final String value ) {
         return enumValue.getValue().equals( value );
      }
   }
}
