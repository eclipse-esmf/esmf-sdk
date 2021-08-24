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

package io.openmanufacturing.sds.aspectmodel.resolver.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.xml.datatype.DatatypeConstants;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.openmanufacturing.sds.metamodel.datatypes.Curie;
import lombok.Value;

public class DataTypeTest {
   @BeforeAll
   public static void setup() {
      DataType.setupTypeMapping();
   }

   @ParameterizedTest
   @MethodSource( value = "getValidTestConfigurations" )
   public <T> void testSerialization( final TestConfiguration<T> testConfiguration ) {
      testConfiguration.predicates.keySet().forEach( testValue -> {
         final Object parsedUntypedObject = testConfiguration.type.parse( testValue );
         assertThat( testConfiguration.predicates.get( testValue ).test( (T) parsedUntypedObject ) )
               .as( "For datatype %s check untyped value: %s", testConfiguration.type.getURI(), testValue ).isTrue();

         final Optional<T> parsedObject = testConfiguration.type.parseTyped( testValue );
         final T value = parsedObject.orElseThrow();
         assertThat( testConfiguration.predicates.get( testValue ).test( value ) )
               .as( "For datatype %s check value: %s", testConfiguration.type.getURI(), testValue ).isTrue();
         assertThat( testConfiguration.type.parseTyped( testConfiguration.type.unparse( value ) ).get() )
               .isEqualTo( value );
      } );
   }

   @ParameterizedTest
   @MethodSource( value = "getTestConfigurationsWithLexicalErrors" )
   public <T> void testConfigurableParserChecks( final TestConfiguration<T> testConfiguration ) {
      testConfiguration.predicates.keySet().forEach( testValue -> {
         ExtendedXsdDataType.setChecking( false );
         final Object parsedUntypedObject = testConfiguration.type.parse( testValue );
         assertThat( parsedUntypedObject ).isEqualTo( testValue );

         final Optional<T> parseTyped = testConfiguration.type.parseTyped( testValue );
         assertThat( parseTyped ).isEmpty().as( "For datatype %s check invalid lexical value: %s",
               testConfiguration.type.getURI(), testValue );

         ExtendedXsdDataType.setChecking( true );
         assertThatThrownBy( () -> testConfiguration.type.parse( testValue ) )
               .isNotNull()
               .as( "For datatype %s untyped invalid lexical value %s throws exception",
                     testConfiguration.type.getURI(), testValue );
         assertThatThrownBy( () -> testConfiguration.type.parseTyped( testValue ) )
               .isNotNull()
               .as( "For datatype %s invalid lexical value %s throws exception", testConfiguration.type.getURI(),
                     testValue );
      } );
   }

   @Value
   private static class TestConfiguration<T> {
      TypedRdfDatatype<T> type;
      Map<String, Predicate<T>> predicates;
   }

   static Stream<TestConfiguration<?>> getTestConfigurationsWithLexicalErrors() {
      return Stream.of(
            new TestConfiguration<>( ExtendedXsdDataType.DOUBLE, Map.of(
                  "foo", v -> false
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.FLOAT, Map.of(
                  "foo", v -> false
            ) )
      );
   }

   static Stream<TestConfiguration<?>> getValidTestConfigurations() {
      final Stream<TestConfiguration<?>> extendedXsdTypes = Stream.of(
            new TestConfiguration<>( ExtendedXsdDataType.BOOLEAN, Map.of(
                  "true", v -> v,
                  "false", v -> !v,
                  "TRUE", v -> v,
                  "FALSE", v -> !v,
                  "True", v -> v,
                  "False", v -> !v
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.DECIMAL, Map.of(
                  "-1.23", v -> v.equals( new BigDecimal( "-1.23" ) ),
                  "126789672374892739424.543233", v -> v.equals( new BigDecimal( "126789672374892739424.543233" ) ),
                  "+100000.00", v -> v.equals( new BigDecimal( "100000.00" ) )
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.INTEGER, Map.of(
                  "-1", v -> v.equals( new BigInteger( "-1" ) ),
                  "0", v -> v.equals( BigInteger.ZERO ),
                  "126789675432332938792837429837429837429",
                  v -> v.equals( new BigInteger( "126789675432332938792837429837429837429" ) ),
                  "+10000", v -> v.equals( new BigInteger( "10000" ) )
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.DOUBLE, Map.of(
                  "-1.0", v -> v.equals( -1.0d ),
                  "+0.0", v -> v.equals( 0.0d ),
                  "-0.0", v -> v.equals( -0.0d ),
                  "234.567e8", v -> v.equals( 234.567e8d ),
                  "INF", v -> v.equals( Double.POSITIVE_INFINITY ),
                  "-INF", v -> v.equals( Double.NEGATIVE_INFINITY ),
                  "NaN", v -> v.equals( Double.NaN )
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.FLOAT, Map.of(
                  "-1.0", v -> v.equals( -1.0f ),
                  "+0.0", v -> v.equals( 0.0f ),
                  "-0.0", v -> v.equals( -0.0f ),
                  "234.567e8", v -> v.equals( 234.567e8f ),
                  "INF", v -> v.equals( Float.POSITIVE_INFINITY ),
                  "-INF", v -> v.equals( Float.NEGATIVE_INFINITY ),
                  "NaN", v -> v.equals( Float.NaN )
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.DATE, Map.of(
                  "2000-01-01", v -> v.getYear() == 2000 && v.getMonth() == 1 && v.getDay() == 1,
                  "2000-01-01Z", v -> v.getYear() == 2000 && v.getMonth() == 1 && v.getDay() == 1
                        && v.getTimezone() != DatatypeConstants.FIELD_UNDEFINED,
                  "2000-01-01+12:05",
                  v -> v.getYear() == 2000 && v.getMonth() == 1 && v.getDay() == 1 && v.getTimezone() == 12 * 60 + 5
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.TIME, Map.of(
                  "14:23:00", v -> v.getHour() == 14 && v.getMinute() == 23 && v.getSecond() == 0,
                  "14:23:00.527634Z",
                  v -> v.getHour() == 14 && v.getMinute() == 23 && v.getSecond() == 0 && v.getFractionalSecond().equals(
                        new BigDecimal( "0.527634" ) ) && v.getTimezone() == 0,
                  "14:23:00+03:00", v -> v.getHour() == 14 && v.getMinute() == 23 && v.getSecond() == 0
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.DATE_TIME, Map.of(
                  "2000-01-01T14:23:00",
                  v -> v.getYear() == 2000 && v.getMonth() == 1 && v.getDay() == 1 && v.getHour() == 14
                        && v.getMinute() == 23 && v.getSecond() == 0,
                  "2000-01-01T14:23:00.66372+14:00",
                  v -> v.getYear() == 2000 && v.getMonth() == 1 && v.getDay() == 1 && v.getHour() == 14
                        && v.getMinute() == 23 && v.getSecond() == 0
                        && v.getFractionalSecond().equals( new BigDecimal( "0.66372" ) ) && v.getTimezone() == 14 * 60
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.DATE_TIME_STAMP, Map.of(
                  "2000-01-01T14:23:00.66372+14:00",
                  v -> v.getYear() == 2000 && v.getMonth() == 1 && v.getDay() == 1 && v.getHour() == 14
                        && v.getMinute() == 23 && v.getSecond() == 0
                        && v.getFractionalSecond().equals( new BigDecimal( "0.66372" ) ) && v.getTimezone() == 14 * 60
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.G_YEAR, Map.of(
                  "2000", v -> v.getYear() == 2000,
                  "2000+03:00", v -> v.getYear() == 2000 && v.getTimezone() == 3 * 60
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.G_MONTH, Map.of(
                  "--04", v -> v.getMonth() == 4,
                  "--04+03:00", v -> v.getMonth() == 4 && v.getTimezone() == 3 * 60
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.G_DAY, Map.of(
                  "---04", v -> v.getDay() == 4,
                  "---04+03:00", v -> v.getDay() == 4 && v.getTimezone() == 3 * 60
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.G_YEAR_MONTH, Map.of(
                  "2000-01", v -> v.getYear() == 2000 && v.getMonth() == 1,
                  "2000-01+03:00", v -> v.getYear() == 2000 && v.getMonth() == 1 && v.getTimezone() == 3 * 60
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.G_MONTH_DAY, Map.of(
                  "--01-01", v -> v.getMonth() == 1 && v.getDay() == 1,
                  "--01-01+03:00", v -> v.getMonth() == 1 && v.getDay() == 1 && v.getTimezone() == 3 * 60
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.DURATION, Map.of(
                  "P30D", v -> v.getDays() == 30,
                  "-P1Y2M3DT1H", v -> v.getYears() == 1 && v.getMonths() == 2 && v.getDays() == 3 && v.getHours() == 1
                        && v.getSign() == -1,
                  "PT1H5M0S", v -> v.getHours() == 1 && v.getMinutes() == 5 && v.getSeconds() == 0
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.YEAR_MONTH_DURATION, Map.of(
                  "P10M", v -> v.getMonths() == 10,
                  "P5Y2M", v -> v.getYears() == 5 && v.getMonths() == 2
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.DAY_TIME_DURATION, Map.of(
                  "P30D", v -> v.getDays() == 30,
                  "P1DT5H", v -> v.getDays() == 1 && v.getHours() == 5,
                  "PT1H5M0S", v -> v.getHours() == 1 && v.getMinutes() == 5 && v.getSeconds() == 0
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.BYTE, Map.of(
                  "-1", v -> v == -1,
                  "0", v -> v == 0,
                  "127", v -> v == 127
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.SHORT, Map.of(
                  "-1", v -> v == -1,
                  "0", v -> v == 0,
                  "32767", v -> v == 32767
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.INT, Map.of(
                  "-1", v -> v == -1,
                  "0", v -> v == 0,
                  "2147483647", v -> v == 2147483647
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.LONG, Map.of(
                  "-1", v -> v == -1,
                  "0", v -> v == 0,
                  "9223372036854775807", v -> v == 9223372036854775807L
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.UNSIGNED_BYTE, Map.of(
                  "0", v -> v == 0,
                  "1", v -> v == 1,
                  "255", v -> v == 255
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.UNSIGNED_SHORT, Map.of(
                  "0", v -> v == 0,
                  "1", v -> v == 1,
                  "65535", v -> v == 65535
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.UNSIGNED_INT, Map.of(
                  "0", v -> v == 0,
                  "1", v -> v == 1,
                  "4294967295", v -> v == 4294967295L
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.UNSIGNED_LONG, Map.of(
                  "0", v -> v.equals( BigInteger.ZERO ),
                  "1", v -> v.equals( BigInteger.ONE ),
                  "18446744073709551615", v -> v.equals( new BigInteger( "18446744073709551615" ) )
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.POSITIVE_INTEGER, Map.of(
                  "1", v -> v.equals( BigInteger.ONE ),
                  "7345683746578364857368475638745",
                  v -> v.equals( new BigInteger( "7345683746578364857368475638745" ) )
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.NON_NEGATIVE_INTEGER, Map.of(
                  "0", v -> v.equals( BigInteger.ZERO ),
                  "1", v -> v.equals( BigInteger.ONE ),
                  "7345683746578364857368475638745",
                  v -> v.equals( new BigInteger( "7345683746578364857368475638745" ) )
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.NEGATIVE_INTEGER, Map.of(
                  "-1", v -> v.equals( new BigInteger( "-1" ) ),
                  "-23487263847628376482736487263847",
                  v -> v.equals( new BigInteger( "-23487263847628376482736487263847" ) )
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.NON_POSITIVE_INTEGER, Map.of(
                  "-1", v -> v.equals( new BigInteger( "-1" ) ),
                  "0", v -> v.equals( BigInteger.ZERO ),
                  "-93845837498573987498798987394",
                  v -> v.equals( new BigInteger( "-93845837498573987498798987394" ) )
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.HEX_BINARY, Map.of(
                  "5468697320697320612074657374",
                  v -> new String( v, StandardCharsets.US_ASCII ).equals( "This is a test" )
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.BASE64_BINARY, Map.of(
                  "VGhpcyBpcyBhIHRlc3Q=", v -> new String( v, StandardCharsets.US_ASCII ).equals( "This is a test" )
            ) ),

            new TestConfiguration<>( ExtendedXsdDataType.ANY_URI, Map.of(
                  "http://example.org/", v -> v.toString().equals( "http://example.org/" ),
                  "urn:bamm:io.openmanufacturing:Errors:1.0.0#errorState",
                  v -> v.toString()
                        .equals( "urn:bamm:io.openmanufacturing:Errors:1.0.0#errorState" )
            ) )
      );

      final Stream<TestConfiguration<Curie>> curieTypes =
            DataType.getAllSupportedTypes().stream()
                    .filter( dataType -> dataType.getJavaClass() != null
                          && dataType.getJavaClass().equals( Curie.class ) )
                    .map( dataType -> (TypedRdfDatatype<Curie>) dataType )
                    .map( curieType -> new TestConfiguration<>( curieType, Map.of(
                          "xsd:string", v -> ((Curie) v).getValue().equals( "xsd:string" ),
                          "unit:hectopascal", v -> ((Curie) v).getValue().equals( "unit:hectopascal" )
                    ) ) );

      return Stream.concat( extendedXsdTypes, curieTypes );
   }
}
