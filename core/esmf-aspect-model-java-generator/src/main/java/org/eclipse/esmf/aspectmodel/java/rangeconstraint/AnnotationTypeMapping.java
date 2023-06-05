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

package org.eclipse.esmf.aspectmodel.java.rangeconstraint;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.esmf.aspectmodel.java.customconstraint.DoubleMax;
import org.eclipse.esmf.aspectmodel.java.customconstraint.DurationMax;
import org.eclipse.esmf.aspectmodel.java.customconstraint.LongMax;

import com.google.common.collect.ImmutableMap;

import org.eclipse.esmf.aspectmodel.java.customconstraint.DoubleMin;
import org.eclipse.esmf.aspectmodel.java.customconstraint.DurationMin;
import org.eclipse.esmf.aspectmodel.java.customconstraint.FloatMax;
import org.eclipse.esmf.aspectmodel.java.customconstraint.FloatMin;
import org.eclipse.esmf.aspectmodel.java.customconstraint.GregorianCalendarMax;
import org.eclipse.esmf.aspectmodel.java.customconstraint.GregorianCalendarMin;
import org.eclipse.esmf.aspectmodel.java.customconstraint.IntegerMax;
import org.eclipse.esmf.aspectmodel.java.customconstraint.IntegerMin;
import org.eclipse.esmf.aspectmodel.java.customconstraint.LongMin;
import org.eclipse.esmf.aspectmodel.java.customconstraint.ShortMax;
import org.eclipse.esmf.aspectmodel.java.customconstraint.ShortMin;

/**
 * Serves to map used data types to the respective annotation class.
 */
public enum AnnotationTypeMapping {

   MINIMUM(
         ImmutableMap.<Class<?>, Class<?>> builder()
               .put( Integer.class, IntegerMin.class )
               .put( Short.class, ShortMin.class )
               .put( Long.class, LongMin.class )
               .put( Double.class, DoubleMin.class )
               .put( Float.class, FloatMin.class )
               .put( BigDecimal.class, DecimalMin.class )
               .put( Duration.class, DurationMin.class )
               .put( XMLGregorianCalendar.class, GregorianCalendarMin.class )
               .put( BigInteger.class, DecimalMin.class )
               .build() ),
   MAXIMUM(
         ImmutableMap.<Class<?>, Class<?>> builder()
               .put( Integer.class, IntegerMax.class )
               .put( Short.class, ShortMax.class )
               .put( Long.class, LongMax.class )
               .put( Double.class, DoubleMax.class )
               .put( Float.class, FloatMax.class )
               .put( BigDecimal.class, DecimalMax.class )
               .put( Duration.class, DurationMax.class )
               .put( XMLGregorianCalendar.class, GregorianCalendarMax.class )
               .put( BigInteger.class, DecimalMax.class )
               .build()
   );

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