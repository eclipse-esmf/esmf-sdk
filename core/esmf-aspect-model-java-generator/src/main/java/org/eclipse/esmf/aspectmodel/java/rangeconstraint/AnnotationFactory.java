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
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Contains the different annotation classes.
 */
public class AnnotationFactory {

   static final Map<OperationKeys, AnnotationExpression> operationMap = new EnumMap<>( OperationKeys.class );

   static {
      operationMap
            .put( OperationKeys.FLOAT_MINIMUM,
                  new FloatAnnotation( AnnotationTypeMapping.MINIMUM.getAnnotationType( Float.class ) ) );
      operationMap
            .put( OperationKeys.FLOAT_MAXIMUM,
                  new FloatAnnotation( AnnotationTypeMapping.MAXIMUM.getAnnotationType( Float.class ) ) );
      operationMap
            .put( OperationKeys.DOUBLE_MINIMUM,
                  new DoubleAnnotation( AnnotationTypeMapping.MINIMUM.getAnnotationType( Double.class ) ) );
      operationMap
            .put( OperationKeys.DOUBLE_MAXIMUM,
                  new DoubleAnnotation( AnnotationTypeMapping.MAXIMUM.getAnnotationType( Double.class ) ) );
      operationMap.put( OperationKeys.BIGINTEGER_MINIMUM,
            new BigIntegerAnnotation( AnnotationTypeMapping.MINIMUM.getAnnotationType( BigInteger.class ) ) );
      operationMap.put( OperationKeys.BIGINTEGER_MAXIMUM,
            new BigIntegerAnnotation( AnnotationTypeMapping.MAXIMUM.getAnnotationType( BigInteger.class ) ) );
      operationMap.put( OperationKeys.BIGDECIMAL_MINIMUM,
            new BigDecimalAnnotation( AnnotationTypeMapping.MINIMUM.getAnnotationType( BigDecimal.class ) ) );
      operationMap.put( OperationKeys.BIGDECIMAL_MAXIMUM,
            new BigDecimalAnnotation( AnnotationTypeMapping.MAXIMUM.getAnnotationType( BigDecimal.class ) ) );
      operationMap.put( OperationKeys.INTEGER_MINIMUM,
            new IntegerAnnotation( AnnotationTypeMapping.MINIMUM.getAnnotationType( Integer.class ) ) );
      operationMap.put( OperationKeys.INTEGER_MAXIMUM,
            new IntegerAnnotation( AnnotationTypeMapping.MAXIMUM.getAnnotationType( Integer.class ) ) );
      operationMap.put( OperationKeys.SHORT_MINIMUM,
            new ShortAnnotation( AnnotationTypeMapping.MINIMUM.getAnnotationType( Short.class ) ) );
      operationMap.put( OperationKeys.SHORT_MAXIMUM,
            new ShortAnnotation( AnnotationTypeMapping.MAXIMUM.getAnnotationType( Short.class ) ) );
      operationMap.put( OperationKeys.LONG_MINIMUM,
            new LongAnnotation( AnnotationTypeMapping.MINIMUM.getAnnotationType( Long.class ) ) );
      operationMap.put( OperationKeys.LONG_MAXIMUM,
            new LongAnnotation( AnnotationTypeMapping.MAXIMUM.getAnnotationType( Long.class ) ) );
      operationMap.put( OperationKeys.DURATIONIMPL_MINIMUM,
            new DurationAnnotation( AnnotationTypeMapping.MINIMUM.getAnnotationType( Duration.class ) ) );
      operationMap.put( OperationKeys.DURATIONIMPL_MAXIMUM,
            new DurationAnnotation( AnnotationTypeMapping.MAXIMUM.getAnnotationType( Duration.class ) ) );
      operationMap.put( OperationKeys.DURATIONYEARMONTHIMPL_MINIMUM,
            new DurationAnnotation( AnnotationTypeMapping.MINIMUM.getAnnotationType( Duration.class ) ) );
      operationMap.put( OperationKeys.DURATIONYEARMONTHIMPL_MAXIMUM,
            new DurationAnnotation( AnnotationTypeMapping.MAXIMUM.getAnnotationType( Duration.class ) ) );
      operationMap.put( OperationKeys.DURATIONDAYTIMEIMPL_MINIMUM,
            new DurationAnnotation( AnnotationTypeMapping.MINIMUM.getAnnotationType( Duration.class ) ) );
      operationMap.put( OperationKeys.DURATIONDAYTIMEIMPL_MAXIMUM,
            new DurationAnnotation( AnnotationTypeMapping.MAXIMUM.getAnnotationType( Duration.class ) ) );
      operationMap.put( OperationKeys.XMLGREGORIANCALENDARIMPL_MINIMUM,
            new DateTimeAnnotation( AnnotationTypeMapping.MINIMUM.getAnnotationType( XMLGregorianCalendar.class ) ) );
      operationMap.put( OperationKeys.XMLGREGORIANCALENDARIMPL_MAXIMUM,
            new DateTimeAnnotation( AnnotationTypeMapping.MAXIMUM.getAnnotationType( XMLGregorianCalendar.class ) ) );
   }

   public static Optional<AnnotationExpression> getOperation( final Class<?> clazz,
         final AnnotationTypeMapping annotationTypeMapping ) {
      return Optional
            .ofNullable( operationMap.get( OperationKeys.getByClassAndBound( clazz, annotationTypeMapping ) ) );
   }

   private enum OperationKeys {
      FLOAT_MINIMUM,
      FLOAT_MAXIMUM,
      DOUBLE_MINIMUM,
      DOUBLE_MAXIMUM,
      BIGINTEGER_MINIMUM,
      BIGINTEGER_MAXIMUM,
      BIGDECIMAL_MINIMUM,
      BIGDECIMAL_MAXIMUM,
      INTEGER_MINIMUM,
      INTEGER_MAXIMUM,
      SHORT_MINIMUM,
      SHORT_MAXIMUM,
      LONG_MINIMUM,
      LONG_MAXIMUM,
      DURATIONIMPL_MINIMUM,
      DURATIONIMPL_MAXIMUM,
      DURATIONYEARMONTHIMPL_MINIMUM,
      DURATIONYEARMONTHIMPL_MAXIMUM,
      DURATIONDAYTIMEIMPL_MINIMUM,
      DURATIONDAYTIMEIMPL_MAXIMUM,
      XMLGREGORIANCALENDARIMPL_MINIMUM,
      XMLGREGORIANCALENDARIMPL_MAXIMUM;

      static OperationKeys getByClassAndBound( final Class<?> clazz, final AnnotationTypeMapping extremum ) {
         return OperationKeys
               .valueOf( clazz.getSimpleName().toUpperCase() + "_" + extremum.name().toUpperCase() );
      }
   }
}