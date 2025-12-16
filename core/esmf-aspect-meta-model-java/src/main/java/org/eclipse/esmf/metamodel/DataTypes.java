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

import org.eclipse.esmf.metamodel.datatype.SammType;

/**
 * Instances for the scalar data types built in the meta model.
 * This class is for naming convenience, so that you can static import DataTypes.* and refer to "xsd.time" like "xsd:time" in RDF.
 */
@SuppressWarnings( { "NewClassNamingConvention", "checkstyle:TypeName", "checkstyle:ConstantName" } )
public class DataTypes {
   public static class xsd {
      public static final Scalar string = SammType.STRING;
      public static final Scalar boolean_ = SammType.BOOLEAN;
      public static final Scalar decimal = SammType.DECIMAL;
      public static final Scalar integer = SammType.INTEGER;
      public static final Scalar double_ = SammType.DOUBLE;
      public static final Scalar float_ = SammType.FLOAT;
      public static final Scalar date = SammType.DATE;
      public static final Scalar time = SammType.TIME;
      public static final Scalar dateTime = SammType.DATE_TIME;
      public static final Scalar dateTimeStamp = SammType.DATE_TIME_STAMP;
      public static final Scalar gYear = SammType.G_YEAR;
      public static final Scalar gMonth = SammType.G_MONTH;
      public static final Scalar gDay = SammType.G_DAY;
      public static final Scalar gYearMonth = SammType.G_YEAR_MONTH;
      public static final Scalar gMonthDay = SammType.G_MONTH_DAY;
      public static final Scalar duration = SammType.DURATION;
      public static final Scalar yearMonthDuration = SammType.YEAR_MONTH_DURATION;
      public static final Scalar dayTimeDuration = SammType.DAY_TIME_DURATION;
      public static final Scalar byte_ = SammType.BYTE;
      public static final Scalar short_ = SammType.SHORT;
      public static final Scalar int_ = SammType.INT;
      public static final Scalar long_ = SammType.LONG;
      public static final Scalar unsignedByte = SammType.UNSIGNED_BYTE;
      public static final Scalar unsignedShort = SammType.UNSIGNED_SHORT;
      public static final Scalar unsignedInt = SammType.UNSIGNED_INT;
      public static final Scalar unsignedLong = SammType.UNSIGNED_LONG;
      public static final Scalar positiveInteger = SammType.POSITIVE_INTEGER;
      public static final Scalar nonNegativeInteger = SammType.NON_NEGATIVE_INTEGER;
      public static final Scalar negativeInteger = SammType.NEGATIVE_INTEGER;
      public static final Scalar nonPositiveInteger = SammType.NON_POSITIVE_INTEGER;
      public static final Scalar hexBinary = SammType.HEX_BINARY;
      public static final Scalar base64Binary = SammType.BASE64_BINARY;
      public static final Scalar anyURI = SammType.ANY_URI;
   }

   public static class samm {
      public static final Scalar curie = SammType.CURIE;
   }

   public static class rdf {
      public static final Scalar langString = SammType.LANG_STRING;
   }
}
