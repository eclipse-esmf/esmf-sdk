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

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.java.exception.EnumAttributeNotFoundException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/** Generated class for AspectWithExtendedEnumsWithNotInPayloadProperty. */
public class AspectWithExtendedEnumsWithNotInPayloadProperty {

   private final EvaluationResults result;

   private final YesNo simpleResult;

   @JsonCreator
   public AspectWithExtendedEnumsWithNotInPayloadProperty(
         @JsonProperty( value = "result" ) EvaluationResults result,
         @JsonProperty( value = "simpleResult" ) YesNo simpleResult ) {
      this.result = result;
      this.simpleResult = simpleResult;
   }

   /**
    * Returns result
    *
    * @return {@link #result}
    */
   public EvaluationResults getResult() {
      return this.result;
   }

   /**
    * Returns simpleResult
    *
    * @return {@link #simpleResult}
    */
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
      final AspectWithExtendedEnumsWithNotInPayloadProperty that =
            (AspectWithExtendedEnumsWithNotInPayloadProperty) o;
      return Objects.equals( result, that.result ) && Objects.equals( simpleResult, that.simpleResult );
   }

   /** Generated class {@link EvaluationResults}. */
   @JsonFormat( shape = JsonFormat.Shape.OBJECT )
   public enum EvaluationResults {
      RESULT_NO_STATUS(
            new EvaluationResult(
                  Optional.of( BigInteger.valueOf( 3 ) ),
                  Short.valueOf( "-1" ),
                  "No status",
                  new NestedResult( BigInteger.valueOf( 1 ), "GOOD" ) ) ),
      RESULT_GOOD(
            new EvaluationResult(
                  Optional.of( BigInteger.valueOf( 4 ) ),
                  Short.valueOf( "1" ),
                  "Good",
                  new NestedResult( BigInteger.valueOf( 1 ), "GOOD" ) ) ),
      RESULT_BAD(
            new EvaluationResult(
                  Optional.of( BigInteger.valueOf( 13 ) ),
                  Short.valueOf( "2" ),
                  "Bad",
                  new NestedResult( BigInteger.valueOf( 1 ), "GOOD" ) ) );

      private final EvaluationResult value;

      EvaluationResults( EvaluationResult value ) {
         this.value = value;
      }

      @JsonCreator
      static EvaluationResults enumDeserializationConstructor( EvaluationResult value ) {
         return fromValue( value )
               .orElseThrow( () -> new EnumAttributeNotFoundException( "Tried to parse value \"" + value
                     + "\", but there is no enum field like that in EvaluationResults" ) );
      }

      @JsonValue
      public EvaluationResult getValue() {
         return value;
      }

      public static Optional<EvaluationResults> fromValue( EvaluationResult value ) {
         return Arrays.stream( EvaluationResults.values() )
                      .filter( enumValue -> compareEnumValues( enumValue, value ) )
                      .findAny();
      }

      private static boolean compareEnumValues( EvaluationResults enumValue, EvaluationResult value ) {
         return enumValue.getValue().getAverage().equals( value.getAverage() )
               && enumValue.getValue().getNumericCode().equals( value.getNumericCode() )
               && enumValue.getValue().getNestedResult().equals( value.getNestedResult() );
      }
   }

   /** Generated class for Nested Result. A nested result for testing */
   public static class NestedResult {

      private final BigInteger average;

      private final String description;

      @JsonCreator
      public NestedResult(
            @JsonProperty( value = "average" ) BigInteger average,
            @JsonProperty( value = "description" ) String description ) {
         this.average = average;
         this.description = description;
      }

      /**
       * Returns Average
       *
       * @return {@link #average}
       */
      public BigInteger getAverage() {
         return this.average;
      }

      /**
       * Returns Description
       *
       * @return {@link #description}
       */
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
         final NestedResult that = (NestedResult) o;
         return Objects.equals( average, that.average ) && Objects.equals( description, that.description );
      }
   }

   /** Generated class {@link YesNo}. */
   @JsonFormat( shape = JsonFormat.Shape.OBJECT )
   public enum YesNo {
      YES( "Yes" ),
      NO( "No" );

      private final String value;

      YesNo( String value ) {
         this.value = value;
      }

      @JsonCreator
      static YesNo enumDeserializationConstructor( String value ) {
         return fromValue( value )
               .orElseThrow( () -> new EnumAttributeNotFoundException(
                     "Tried to parse value \"" + value + "\", but there is no enum field like that in YesNo" ) );
      }

      @JsonValue
      public String getValue() {
         return value;
      }

      public static Optional<YesNo> fromValue( String value ) {
         return Arrays.stream( YesNo.values() )
                      .filter( enumValue -> compareEnumValues( enumValue, value ) )
                      .findAny();
      }

      private static boolean compareEnumValues( YesNo enumValue, String value ) {
         return enumValue.getValue().equals( value );
      }
   }

   /** Generated class for Evaluation Result. Possible values for the evaluation of a process */
   public static class EvaluationResult {
      private final Optional<BigInteger> average;

      private final Short numericCode;

      private String description;

      private final NestedResult nestedResult;

      public EvaluationResult(
            Optional<BigInteger> average,
            Short numericCode,
            String description,
            NestedResult nestedResult ) {
         this.average = average;
         this.numericCode = numericCode;
         this.description = description;
         this.nestedResult = nestedResult;
      }

      @JsonCreator
      public EvaluationResult(
            @JsonProperty( value = "average" ) Optional<BigInteger> average,
            @JsonProperty( value = "numericCode" ) Short numericCode,
            @JsonProperty( value = "nestedResult" ) NestedResult nestedResult ) {
         this.average = average;
         this.numericCode = numericCode;
         this.nestedResult = nestedResult;
      }

      /**
       * Returns Average
       *
       * @return {@link #average}
       */
      public Optional<BigInteger> getAverage() {
         return this.average;
      }

      /**
       * Returns Numeric Code
       *
       * @return {@link #numericCode}
       */
      public Short getNumericCode() {
         return this.numericCode;
      }

      /**
       * Returns Description
       *
       * @return {@link #description}
       */
      @JsonIgnore
      public String getDescription() {
         return this.description;
      }

      /**
       * Returns nested result
       *
       * @return {@link #nestedResult}
       */
      public NestedResult getNestedResult() {
         return this.nestedResult;
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
         return Objects.equals( average, that.average )
               && Objects.equals( numericCode, that.numericCode )
               && Objects.equals( description, that.description )
               && Objects.equals( nestedResult, that.nestedResult );
      }
   }
}

