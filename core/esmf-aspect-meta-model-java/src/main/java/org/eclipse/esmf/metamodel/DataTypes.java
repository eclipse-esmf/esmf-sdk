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

package org.eclipse.esmf.metamodel;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.eclipse.esmf.metamodel.datatype.CurieType;
import org.eclipse.esmf.metamodel.impl.DefaultScalar;

import com.google.common.collect.ImmutableMap;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

/**
 * Instances for the scalar data types built in the meta model
 */
@SuppressWarnings( { "NewClassNamingConvention", "checkstyle:TypeName", "checkstyle:ConstantName" } )
public class DataTypes {
   private static final Map<String, Scalar> TYPES = ImmutableMap.<String, Scalar> builder()
         .put( XSD.xstring.getURI(), new DefaultScalar( XSD.xstring.getURI() ) )
         .put( XSD.xboolean.getURI(), new DefaultScalar( XSD.xboolean.getURI() ) )
         .put( XSD.decimal.getURI(), new DefaultScalar( XSD.decimal.getURI() ) )
         .put( XSD.integer.getURI(), new DefaultScalar( XSD.integer.getURI() ) )
         .put( XSD.xdouble.getURI(), new DefaultScalar( XSD.xdouble.getURI() ) )
         .put( XSD.xfloat.getURI(), new DefaultScalar( XSD.xfloat.getURI() ) )
         .put( XSD.date.getURI(), new DefaultScalar( XSD.date.getURI() ) )
         .put( XSD.time.getURI(), new DefaultScalar( XSD.time.getURI() ) )
         .put( XSD.dateTime.getURI(), new DefaultScalar( XSD.dateTime.getURI() ) )
         .put( XSD.dateTimeStamp.getURI(), new DefaultScalar( XSD.dateTimeStamp.getURI() ) )
         .put( XSD.gYear.getURI(), new DefaultScalar( XSD.gYear.getURI() ) )
         .put( XSD.gMonth.getURI(), new DefaultScalar( XSD.gMonth.getURI() ) )
         .put( XSD.gDay.getURI(), new DefaultScalar( XSD.gDay.getURI() ) )
         .put( XSD.gYearMonth.getURI(), new DefaultScalar( XSD.gYearMonth.getURI() ) )
         .put( XSD.gMonthDay.getURI(), new DefaultScalar( XSD.gMonthDay.getURI() ) )
         .put( XSD.duration.getURI(), new DefaultScalar( XSD.duration.getURI() ) )
         .put( XSD.yearMonthDuration.getURI(), new DefaultScalar( XSD.yearMonthDuration.getURI() ) )
         .put( XSD.dayTimeDuration.getURI(), new DefaultScalar( XSD.dayTimeDuration.getURI() ) )
         .put( XSD.xbyte.getURI(), new DefaultScalar( XSD.xbyte.getURI() ) )
         .put( XSD.xshort.getURI(), new DefaultScalar( XSD.xshort.getURI() ) )
         .put( XSD.xint.getURI(), new DefaultScalar( XSD.xint.getURI() ) )
         .put( XSD.xlong.getURI(), new DefaultScalar( XSD.xlong.getURI() ) )
         .put( XSD.unsignedByte.getURI(), new DefaultScalar( XSD.unsignedByte.getURI() ) )
         .put( XSD.unsignedShort.getURI(), new DefaultScalar( XSD.unsignedShort.getURI() ) )
         .put( XSD.unsignedInt.getURI(), new DefaultScalar( XSD.unsignedInt.getURI() ) )
         .put( XSD.unsignedLong.getURI(), new DefaultScalar( XSD.unsignedLong.getURI() ) )
         .put( XSD.positiveInteger.getURI(), new DefaultScalar( XSD.positiveInteger.getURI() ) )
         .put( XSD.nonNegativeInteger.getURI(), new DefaultScalar( XSD.nonNegativeInteger.getURI() ) )
         .put( XSD.negativeInteger.getURI(), new DefaultScalar( XSD.negativeInteger.getURI() ) )
         .put( XSD.nonPositiveInteger.getURI(), new DefaultScalar( XSD.nonPositiveInteger.getURI() ) )
         .put( XSD.hexBinary.getURI(), new DefaultScalar( XSD.hexBinary.getURI() ) )
         .put( XSD.base64Binary.getURI(), new DefaultScalar( XSD.base64Binary.getURI() ) )
         .put( XSD.anyURI.getURI(), new DefaultScalar( XSD.anyURI.getURI() ) )
         .put( CurieType.INSTANCE.getURI(), new DefaultScalar( CurieType.INSTANCE.getURI() ) )
         .put( RDF.langString.getURI(), new DefaultScalar( RDF.langString.getURI() ) )
         .build();

   public static Scalar dataTypeByUri( final String uri ) {
      return Optional.ofNullable( TYPES.get( uri ) ).orElseThrow( NoSuchElementException::new );
   }

   public static class xsd {
      public static final Scalar string = TYPES.get( XSD.xstring.getURI() );
      public static final Scalar boolean_ = TYPES.get( XSD.xboolean.getURI() );
      public static final Scalar decimal = TYPES.get( XSD.decimal.getURI() );
      public static final Scalar integer = TYPES.get( XSD.integer.getURI() );
      public static final Scalar double_ = TYPES.get( XSD.xdouble.getURI() );
      public static final Scalar float_ = TYPES.get( XSD.xfloat.getURI() );
      public static final Scalar date = TYPES.get( XSD.date.getURI() );
      public static final Scalar time = TYPES.get( XSD.time.getURI() );
      public static final Scalar dateTime = TYPES.get( XSD.dateTime.getURI() );
      public static final Scalar dateTimeStamp = TYPES.get( XSD.dateTimeStamp.getURI() );
      public static final Scalar gYear = TYPES.get( XSD.gYear.getURI() );
      public static final Scalar gMonth = TYPES.get( XSD.gMonth.getURI() );
      public static final Scalar gDay = TYPES.get( XSD.gDay.getURI() );
      public static final Scalar gYearMonth = TYPES.get( XSD.gYearMonth.getURI() );
      public static final Scalar gMonthDay = TYPES.get( XSD.gMonthDay.getURI() );
      public static final Scalar duration = TYPES.get( XSD.duration.getURI() );
      public static final Scalar yearMonthDuration = TYPES.get( XSD.yearMonthDuration.getURI() );
      public static final Scalar dayTimeDuration = TYPES.get( XSD.dayTimeDuration.getURI() );
      public static final Scalar byte_ = TYPES.get( XSD.xbyte.getURI() );
      public static final Scalar short_ = TYPES.get( XSD.xshort.getURI() );
      public static final Scalar int_ = TYPES.get( XSD.xint.getURI() );
      public static final Scalar long_ = TYPES.get( XSD.xlong.getURI() );
      public static final Scalar unsignedByte = TYPES.get( XSD.unsignedByte.getURI() );
      public static final Scalar unsignedShort = TYPES.get( XSD.unsignedShort.getURI() );
      public static final Scalar unsignedInt = TYPES.get( XSD.unsignedInt.getURI() );
      public static final Scalar unsignedLong = TYPES.get( XSD.unsignedLong.getURI() );
      public static final Scalar positiveInteger = TYPES.get( XSD.positiveInteger.getURI() );
      public static final Scalar nonNegativeInteger = TYPES.get( XSD.nonNegativeInteger.getURI() );
      public static final Scalar negativeInteger = TYPES.get( XSD.negativeInteger.getURI() );
      public static final Scalar nonPositiveInteger = TYPES.get( XSD.nonPositiveInteger.getURI() );
      public static final Scalar hexBinary = TYPES.get( XSD.hexBinary.getURI() );
      public static final Scalar base64Binary = TYPES.get( XSD.base64Binary.getURI() );
      public static final Scalar anyURI = TYPES.get( XSD.anyURI.getURI() );
   }

   public static class samm {
      public static final Scalar curie = TYPES.get( CurieType.INSTANCE.getURI() );
   }

   public static class rdf {
      public static final Scalar langString = TYPES.get( RDF.langString.getURI() );
   }
}
