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

package org.eclipse.esmf.test.shared.arbitraries;

import java.math.BigInteger;
import java.time.Month;
import java.time.YearMonth;
import java.util.stream.Stream;

import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;
import net.jqwik.api.Tuple;

/**
 * Provides {@link Arbitrary}s for values of XML Schema datatypes.
 */
public interface XsdArbitraries {

   DatatypeFactory getDatatypeFactory();

   @Provide
   default Arbitrary<Boolean> anyBoolean() {
      return Arbitraries.of( true, false );
   }

   @Provide
   default Arbitrary<Short> anyUnsignedByte() {
      return Arbitraries.shorts().between( (short) 0, (short) 255 );
   }

   @Provide
   default Arbitrary<Integer> anyUnsignedShort() {
      return Arbitraries.integers().between( 0, 65535 );
   }

   @Provide
   default Arbitrary<BigInteger> anyUnsignedLong() {
      return Arbitraries.bigIntegers().between( BigInteger.ZERO, new BigInteger( "18446744073709551615" ) );
   }

   @Provide
   default Arbitrary<BigInteger> anyPositiveInteger() {
      return Arbitraries.bigIntegers().filter( value -> value.compareTo( BigInteger.ZERO ) > 0 );
   }

   @Provide
   default Arbitrary<BigInteger> anyNonNegativeInteger() {
      return Arbitraries.bigIntegers().filter( value -> value.compareTo( BigInteger.ZERO ) >= 0 );
   }

   @Provide
   default Arbitrary<String> anyHexBinary() {
      return Arbitraries.strings().ofMinLength( 1 )
                        .map( string -> DatatypeConverter.printHexBinary( string.getBytes() ) );
   }

   @Provide
   default Arbitrary<String> anyBase64Binary() {
      return Arbitraries.strings().ofMinLength( 1 )
                        .map( string -> DatatypeConverter.printBase64Binary( string.getBytes() ) );
   }

   @Provide
   default Arbitrary<Integer> anyYear() {
      return Arbitraries.integers().between( 1970, 2999 );
   }

   @Provide
   default Arbitrary<Integer> anyMonth() {
      return Arbitraries.integers().between( 1, 12 );
   }

   @Provide
   default Arbitrary<Integer> anyDayInMonth() {
      return Arbitraries.integers().between( 1, 31 );
   }

   @Provide
   default Arbitrary<Integer> anyDay( final int year, final int month ) {
      return Arbitraries.integers().between( 1, YearMonth.of( year, month ).lengthOfMonth() );
   }

   @Provide
   default Arbitrary<Integer> anyTimezone() {
      return Arbitraries.integers().between( -500, 500 );
   }

   @Provide
   default Arbitrary<Integer> anyHour() {
      return Arbitraries.integers().between( 0, 23 );
   }

   @Provide
   default Arbitrary<Integer> anyMinute() {
      return Arbitraries.integers().between( 0, 59 );
   }

   @Provide
   default Arbitrary<Integer> anySecond() {
      return Arbitraries.integers().between( 0, 59 );
   }

   @Provide
   default Arbitrary<Integer> anyMillisecond() {
      return Arbitraries.integers().between( 0, 999 );
   }

   @Provide
   default Arbitrary<Tuple.Tuple3<Integer, Integer, Integer>> anyYearMonthDay() {
      return anyYear().flatMap( year ->
            anyMonth().flatMap( month ->
                  anyDay( year, month ).map( day ->
                        Tuple.of( year, month, day ) ) ) );
   }

   @Provide
   default Arbitrary<XMLGregorianCalendar> anyDate() {
      return Combinators.combine( anyYearMonthDay(), anyTimezone() )
                        .as( ( yearMonthDay, timezone ) ->
                              getDatatypeFactory().newXMLGregorianCalendarDate(
                                    yearMonthDay.get1(), yearMonthDay.get2(), yearMonthDay.get3(), timezone ) );
   }

   @Provide
   default Arbitrary<XMLGregorianCalendar> anyTime() {
      return Combinators.combine( anyHour(), anyMinute(), anySecond(), anyTimezone() )
                        .as( getDatatypeFactory()::newXMLGregorianCalendarTime );
   }

   @Provide
   default Arbitrary<XMLGregorianCalendar> anyDateTime() {
      return Combinators
            .combine( anyYearMonthDay(), anyHour(), anyMinute(), anySecond(), anyMillisecond(), anyTimezone() )
            .as( ( yearMonthDay, hour, minute, second, millisecond, timezone ) ->
                  getDatatypeFactory().newXMLGregorianCalendar( yearMonthDay.get1(), yearMonthDay.get2(),
                        yearMonthDay.get3(), hour, minute, second, millisecond, timezone ) );
   }

   @Provide
   default Arbitrary<XMLGregorianCalendar> anyDateTimeStamp() {
      return anyDateTime();
   }

   @Provide
   default Arbitrary<XMLGregorianCalendar> anyGYear() {
      return Combinators.combine( anyYear(), anyTimezone().optional() )
                        .as( ( year, optionalTimezone ) -> {
                           final int timezone = optionalTimezone.orElse( DatatypeConstants.FIELD_UNDEFINED );
                           return getDatatypeFactory().newXMLGregorianCalendar( year, DatatypeConstants.FIELD_UNDEFINED,
                                 DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
                                 DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
                                 DatatypeConstants.FIELD_UNDEFINED, timezone );
                        } );
   }

   @Provide
   default Arbitrary<XMLGregorianCalendar> anyGMonth() {
      return Combinators.combine( anyMonth(), anyTimezone().optional() )
                        .as( ( month, optionalTimezone ) -> {
                           final int timezone = optionalTimezone.orElse( DatatypeConstants.FIELD_UNDEFINED );
                           return getDatatypeFactory()
                                 .newXMLGregorianCalendar( DatatypeConstants.FIELD_UNDEFINED, month,
                                       DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
                                       DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
                                       DatatypeConstants.FIELD_UNDEFINED, timezone );
                        } );
   }

   @Provide
   default Arbitrary<XMLGregorianCalendar> anyGDay() {
      return Combinators.combine( anyDayInMonth(), anyTimezone().optional() )
                        .as( ( day, optionalTimezone ) -> {
                           final int timezone = optionalTimezone.orElse( DatatypeConstants.FIELD_UNDEFINED );
                           return getDatatypeFactory().newXMLGregorianCalendar( DatatypeConstants.FIELD_UNDEFINED,
                                 DatatypeConstants.FIELD_UNDEFINED, day, DatatypeConstants.FIELD_UNDEFINED,
                                 DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
                                 DatatypeConstants.FIELD_UNDEFINED, timezone );
                        } );
   }

   @Provide
   default Arbitrary<XMLGregorianCalendar> anyGYearMonth() {
      return Combinators.combine( anyYear(), anyMonth(), anyTimezone().optional() )
                        .as( ( year, month, optionalTimezone ) -> {
                           final int timezone = optionalTimezone.orElse( DatatypeConstants.FIELD_UNDEFINED );
                           return getDatatypeFactory().newXMLGregorianCalendar( year, month,
                                 DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
                                 DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
                                 DatatypeConstants.FIELD_UNDEFINED, timezone );
                        } );
   }

   @Provide
   default Arbitrary<Tuple.Tuple2<Integer, Integer>> anyMonthDay() {
      return Combinators.combine( anyMonth(), anyDayInMonth() ).as( Tuple::of ).filter( tuple -> {
         // See https://www.w3.org/TR/xmlschema11-2/#gMonthDay:
         // The ·day· value must be no more than 30 if ·month· is one of 4, 6, 9, or 11, and no more than 29 if ·month· is 2.
         final int month = tuple.get1();
         final int day = tuple.get2();
         if ( month == Month.FEBRUARY.getValue() ) {
            return day <= 29;
         }
         if ( Stream.of( Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER )
                    .anyMatch( theMonth -> theMonth.getValue() == month ) ) {
            return day <= 30;
         }
         return day <= 31;
      } );
   }

   @Provide
   default Arbitrary<XMLGregorianCalendar> anyGMonthDay() {
      return Combinators.combine( anyMonthDay(), anyTimezone().optional() )
                        .as( ( monthDay, optionalTimezone ) -> {
                           final int timezone = optionalTimezone.orElse( DatatypeConstants.FIELD_UNDEFINED );
                           return getDatatypeFactory().newXMLGregorianCalendar( DatatypeConstants.FIELD_UNDEFINED,
                                 monthDay.get1(), monthDay.get2(), DatatypeConstants.FIELD_UNDEFINED,
                                 DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
                                 DatatypeConstants.FIELD_UNDEFINED, timezone );
                        } );
   }

   @Provide
   default Arbitrary<Duration> anyDuration() {
      return Arbitraries.longs().between( 0, 10000000L ).map( getDatatypeFactory()::newDuration );
   }

   @Provide
   default Arbitrary<Duration> anyYearMonthDuration() {
      return Combinators.combine( anyBoolean(), anyYear(), anyMonth() )
                        .as( getDatatypeFactory()::newDurationYearMonth );
   }

   @Provide
   default Arbitrary<Duration> anyDayTimeDuration() {
      return Combinators.combine( anyBoolean(), anyDayInMonth(), anyHour(), anyMinute(), anySecond() )
                        .as( getDatatypeFactory()::newDurationDayTime );
   }
}
