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

package io.openmanufacturing.sds.aspectmodel.java.rangeconstraint;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import io.openmanufacturing.sds.aspectmodel.java.customconstraint.DoubleMax;
import io.openmanufacturing.sds.aspectmodel.java.customconstraint.DoubleMin;
import io.openmanufacturing.sds.aspectmodel.java.customconstraint.DurationMax;
import io.openmanufacturing.sds.aspectmodel.java.customconstraint.DurationMin;
import io.openmanufacturing.sds.aspectmodel.java.customconstraint.FloatMax;
import io.openmanufacturing.sds.aspectmodel.java.customconstraint.FloatMin;
import io.openmanufacturing.sds.aspectmodel.java.customconstraint.GregorianCalendarMax;
import io.openmanufacturing.sds.aspectmodel.java.customconstraint.GregorianCalendarMin;
import io.openmanufacturing.sds.aspectmodel.java.customconstraint.Max;
import io.openmanufacturing.sds.aspectmodel.java.customconstraint.Min;

/**
 * Serves to map used data types to the respective annotation class.
 */
public enum AnnotationTypeMapping {

   MINIMUM(
         Map.of( Integer.class, Min.class, Double.class, DoubleMin.class, Float.class, FloatMin.class, BigDecimal.class,
               DecimalMin.class, Duration.class, DurationMin.class, XMLGregorianCalendar.class,
               GregorianCalendarMin.class, BigInteger.class, DecimalMin.class ) ),
   MAXIMUM(
         Map.of( Integer.class, Max.class, Double.class, DoubleMax.class, Float.class, FloatMax.class, BigDecimal.class,
               DecimalMax.class, Duration.class, DurationMax.class, XMLGregorianCalendar.class,
               GregorianCalendarMax.class, BigInteger.class, DecimalMax.class ) );

   private final Map<Class<?>, Class<?>> typeMapping;

   //Enum constructor is used
   @SuppressWarnings( "squid:UnusedPrivateMethod " )
   AnnotationTypeMapping( final Map<Class<?>, Class<?>> typeMapping ) {
      this.typeMapping = typeMapping;
   }

   public Class<?> getAnnotationType( final Class<?> type ) {
      return typeMapping.get( type );
   }
}