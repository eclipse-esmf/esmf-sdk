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

package io.openmanufacturing.sds.aspectmodel.java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;

import io.openmanufacturing.sds.aspectmetamodel.KnownVersion;
import io.openmanufacturing.sds.aspectmodel.resolver.services.DataType;
import io.openmanufacturing.sds.aspectmodel.vocabulary.BAMM;

/**
 * Provides the code needed in code generation to create an instance of the Java type corresponding to
 * a Property's effective scalar dataType and the respective serialized value.
 */
public class ValueInitializer {
   private static final Map<Resource, BiFunction<Class<?>, String, String>> INITIALIZERS;

   static {
      final BiFunction<Class<?>, String, String> literalExpression = ( type, valueExpression ) -> valueExpression;
      final BiFunction<Class<?>, String, String> stringConstructor = ( type, valueExpression ) ->
            String.format( "new %s( %s )", type.getSimpleName(), valueExpression );
      final BiFunction<Class<?>, String, String> parseString = ( type, valueExpression ) ->
            String.format( "%s.parse( %s )", type.getSimpleName(), valueExpression );
      final BiFunction<Class<?>, String, String> create = ( type, valueExpression ) ->
            String.format( "%s.create( %s )", type.getSimpleName(), valueExpression );
      final BiFunction<Class<?>, String, String> parseTypeName = ( type, valueExpression ) ->
            String.format( "%s.parse%s( %s )", type.getSimpleName(), type.getSimpleName(), valueExpression );
      final BiFunction<Class<?>, String, String> valueOf = ( type, valueExpression ) ->
            String.format( "%s.valueOf( %s )", type.getSimpleName(), valueExpression );
      final BiFunction<Class<?>, String, String> gregorianCalendar = ( type, valueExpression ) ->
            "_datatypeFactory.newXMLGregorianCalendar( " + valueExpression + " )";
      final BiFunction<Class<?>, String, String> duration = ( type, valueExpression ) ->
            "_datatypeFactory.newDuration( " + valueExpression + " )";

      INITIALIZERS = new HashMap<>();
      INITIALIZERS.put( XSD.xstring, literalExpression );
      INITIALIZERS.put( XSD.xboolean, valueOf );
      INITIALIZERS.put( XSD.decimal, stringConstructor );
      INITIALIZERS.put( XSD.integer, stringConstructor );
      INITIALIZERS.put( XSD.xdouble, valueOf );
      INITIALIZERS.put( XSD.xfloat, valueOf );
      INITIALIZERS.put( XSD.date, gregorianCalendar );
      INITIALIZERS.put( XSD.dateTime, gregorianCalendar );
      INITIALIZERS.put( XSD.dateTimeStamp, gregorianCalendar );
      INITIALIZERS.put( XSD.gYear, ( type, valueExpression ) ->
            "_datatypeFactory.newXMLGregorianCalendarDate( Integer.valueOf( " + valueExpression + " )"
                  + ", DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED )" );
      INITIALIZERS.put( XSD.gMonth, ( type, valueExpression ) ->
            "_datatypeFactory.newXMLGregorianCalendarDate( DatatypeConstants.FIELD_UNDEFINED, Integer.valueOf( " + valueExpression + " )"
                  + ", DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED )" );
      INITIALIZERS.put( XSD.gYearMonth, gregorianCalendar );
      INITIALIZERS.put( XSD.gMonthDay, gregorianCalendar );
      INITIALIZERS.put( XSD.gDay, gregorianCalendar );
      INITIALIZERS.put( XSD.duration, duration );
      INITIALIZERS.put( XSD.yearMonthDuration, duration );
      INITIALIZERS.put( XSD.dayTimeDuration, duration );
      INITIALIZERS.put( XSD.xbyte, parseTypeName );
      INITIALIZERS.put( XSD.xshort, parseTypeName );
      INITIALIZERS.put( XSD.xint, valueOf );
      INITIALIZERS.put( XSD.xlong, valueOf );
      INITIALIZERS.put( XSD.unsignedByte, parseTypeName );
      INITIALIZERS.put( XSD.unsignedShort, parseTypeName );
      INITIALIZERS.put( XSD.unsignedInt, valueOf );
      INITIALIZERS.put( XSD.unsignedLong, stringConstructor );
      INITIALIZERS.put( XSD.positiveInteger, stringConstructor );
      INITIALIZERS.put( XSD.nonNegativeInteger, stringConstructor );
      INITIALIZERS.put( XSD.negativeInteger, stringConstructor );
      INITIALIZERS.put( XSD.nonPositiveInteger, stringConstructor );
      INITIALIZERS.put( XSD.hexBinary, null );
      INITIALIZERS.put( XSD.base64Binary, null );
      INITIALIZERS.put( XSD.anyURI, create );
   }

   public boolean needInitializationToConstructor( final List<DeconstructionSet> deconstructionSets ) {
      return deconstructionSets.stream().flatMap( deconstructionSet ->
                  deconstructionSet.getElementProperties().stream().map( property ->
                        property.getDataType().map( type ->
                              DataType.getJavaTypeForMetaModelType( ResourceFactory.createResource( type.getUrn() ), property.getMetaModelVersion() ) ) ) )
            .anyMatch( dataType ->
                  dataType.map( type -> type == XMLGregorianCalendar.class ).orElse( false ) );
   }

   /**
    * Creates and initializes an instance of the given type with the value from an expression as String. For example,
    * apply(XSD.xint, "\"3\"") returns "3", and apply(XSD.xstring, "\"foo\"") returns "\"foo\"".
    *
    * @param type the type for which an instance should be created
    * @param valueExpression an expression that, when evaluated, will return the input value <b>as a string</b>.
    * @param metaModelVersion the used meta model version
    */
   public String apply( final Resource type, final String valueExpression, final KnownVersion metaModelVersion ) {
      final BAMM bamm = new BAMM( metaModelVersion );
      if ( type.equals( bamm.curie() ) ) {
         return String.format( "new Curie( %s )", valueExpression );
      }
      return INITIALIZERS.get( type ).apply( DataType.getJavaTypeForMetaModelType( type, metaModelVersion ), valueExpression );
   }
}
