/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.ts;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.eclipse.esmf.metamodel.datatype.SammXsdType;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.XSD;

/**
 * Provides the code needed in code generation to create an instance of the Ts type corresponding to
 * a Property's effective scalar dataType and the respective serialized value.
 */
public class ValueInitializer {
   private static final Map<Resource, BiFunction<Class<?>, String, String>> INITIALIZERS;

   static {
      final BiFunction<Class<?>, String, String> literalExpression = ( type, valueExpression ) -> valueExpression;
      final BiFunction<Class<?>, String, String> date = ( type, valueExpression ) -> "new Date( '" + valueExpression + "' )";

      INITIALIZERS = new HashMap<>();
      INITIALIZERS.put( XSD.xstring, literalExpression );
      INITIALIZERS.put( XSD.xboolean, literalExpression );
      INITIALIZERS.put( XSD.decimal, literalExpression );
      INITIALIZERS.put( XSD.integer, literalExpression );
      INITIALIZERS.put( XSD.xdouble, literalExpression );
      INITIALIZERS.put( XSD.xfloat, literalExpression );
      INITIALIZERS.put( XSD.date, date );
      INITIALIZERS.put( XSD.dateTime, date );
      INITIALIZERS.put( XSD.dateTimeStamp, date );
      INITIALIZERS.put( XSD.gYear, literalExpression );
      INITIALIZERS.put( XSD.gMonth, literalExpression );
      INITIALIZERS.put( XSD.gYearMonth, literalExpression );
      INITIALIZERS.put( XSD.gMonthDay, literalExpression );
      INITIALIZERS.put( XSD.gDay, literalExpression );
      INITIALIZERS.put( XSD.duration, literalExpression );
      INITIALIZERS.put( XSD.yearMonthDuration, literalExpression );
      INITIALIZERS.put( XSD.dayTimeDuration, literalExpression );
      INITIALIZERS.put( XSD.xbyte, literalExpression );
      INITIALIZERS.put( XSD.xshort, literalExpression );
      INITIALIZERS.put( XSD.xint, literalExpression );
      INITIALIZERS.put( XSD.xlong, literalExpression );
      INITIALIZERS.put( XSD.unsignedByte, literalExpression );
      INITIALIZERS.put( XSD.unsignedShort, literalExpression );
      INITIALIZERS.put( XSD.unsignedInt, literalExpression );
      INITIALIZERS.put( XSD.unsignedLong, literalExpression );
      INITIALIZERS.put( XSD.positiveInteger, literalExpression );
      INITIALIZERS.put( XSD.nonNegativeInteger, literalExpression );
      INITIALIZERS.put( XSD.negativeInteger, literalExpression );
      INITIALIZERS.put( XSD.nonPositiveInteger, literalExpression );
      INITIALIZERS.put( XSD.hexBinary, null );
      INITIALIZERS.put( XSD.base64Binary, null );
      INITIALIZERS.put( XSD.anyURI, literalExpression );
   }

   /**
    * Creates and initializes an instance of the given type with the value from an expression as String. For example,
    * apply(XSD.xint, "\"3\"") returns "3", and apply(XSD.xstring, "\"foo\"") returns "\"foo\"".
    *
    * @param rdfType the type for which an instance should be created
    * @param valueExpression an expression that, when evaluated, will return the input value <b>as a string</b>.
    */
   public String apply( final Resource rdfType, final String valueExpression ) {
      return apply( rdfType, SammXsdType.getJavaTypeForMetaModelType( rdfType ), valueExpression );
   }

   /**
    * Creates and initializes an instance of the given type with the value from an expression as String. For example,
    * apply(XSD.xint, "\"3\"") returns "3", and apply(XSD.xstring, "\"foo\"") returns "\"foo\"".
    *
    * @param rdfType the type for which an instance should be created
    * @param javaType the corresponding Ts type
    * @param valueExpression an expression that, when evaluated, will return the input value <b>as a string</b>.
    */
   public String apply( final Resource rdfType, final Class<?> javaType, final String valueExpression ) {
      return INITIALIZERS.get( rdfType ).apply( javaType, valueExpression );
   }
}
