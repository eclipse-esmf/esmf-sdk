/*
 * Copyright (c) 2026 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.generator.parquet;

import java.math.BigInteger;

import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;
import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.Types;

/**
 * Maps XSD types from the Aspect Model to Apache Parquet schema types.
 *
 * <p>
 * This class encapsulates all XSD-to-Parquet type mapping logic, providing
 * methods to convert XSD type URIs to Parquet {@link PrimitiveType} definitions
 * including logical type annotations for dates, timestamps, and strings.
 * </p>
 */
final class ParquetSchemaMapper {

   private ParquetSchemaMapper() {
      // Utility class — no instantiation
   }

   /**
    * Maps an XSD type URI to a fully annotated Parquet {@link PrimitiveType} with logical type
    * annotations, field name, language tag, timezone information, and max length.
    *
    * @param xsdTypeUri the XSD type URI
    * @param fieldName the Parquet field name
    * @param language the language tag (for langString types), or {@code null}
    * @param isTimezoneAvailable whether timezone information is present (for dateTime types)
    * @param maxLength the maximum length constraint, or {@code null}
    * @return the fully annotated Parquet {@link PrimitiveType}
    */
   static PrimitiveType mapToAnnotatedPrimitiveType( final String xsdTypeUri, final String fieldName,
         final String language, final boolean isTimezoneAvailable, final BigInteger maxLength ) {
      final Resource xsdResource = ResourceFactory.createResource( xsdTypeUri );

      // Boolean type
      if ( XSD.xboolean.equals( xsdResource ) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.BOOLEAN, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .named( fieldName );
      } else if ( ( XSD.xstring.equals( xsdResource )
            || XSD.time.equals( xsdResource )
            || XSD.gYear.equals( xsdResource )
            || XSD.gMonth.equals( xsdResource )
            || XSD.gDay.equals( xsdResource )
            || XSD.gYearMonth.equals( xsdResource )
            || XSD.gMonthDay.equals( xsdResource )
            || XSD.duration.equals( xsdResource )
            || XSD.yearMonthDuration.equals( xsdResource )
            || XSD.dayTimeDuration.equals( xsdResource )
            || XSD.hexBinary.equals( xsdResource )
            || XSD.base64Binary.equals( xsdResource )
            || XSD.anyURI.equals( xsdResource )
            || RDF.langString.getURI().equals( xsdTypeUri ) ) && ( maxLength != null && maxLength.intValue() > 0 ) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .length( maxLength.intValue() )
               .named( fieldName );
      } else if ( RDF.langString.getURI().equals( xsdTypeUri ) ) {
         return Types.primitive( PrimitiveType.PrimitiveTypeName.BINARY, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.stringType() ).named( fieldName + "-" + language );
      } else if ( XSD.xfloat.equals( xsdResource ) ) { // Float type
         return Types.primitive( PrimitiveType.PrimitiveTypeName.FLOAT, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .named( fieldName );
      } else if ( XSD.xdouble.equals( xsdResource ) || XSD.decimal.equals( xsdResource ) ) { // Double and decimal types
         return Types.primitive( PrimitiveType.PrimitiveTypeName.DOUBLE, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .named( fieldName );
      } else if ( XSD.xint.equals( xsdResource ) || XSD.integer.equals( xsdResource )
            || XSD.xshort.equals( xsdResource ) || XSD.xbyte.equals( xsdResource )
            || XSD.unsignedShort.equals( xsdResource ) || XSD.unsignedByte.equals( xsdResource )
            || XSD.nonNegativeInteger.equals( xsdResource ) || XSD.positiveInteger.equals( xsdResource )
            || XSD.nonPositiveInteger.equals( xsdResource ) || XSD.negativeInteger.equals(
                  xsdResource ) ) { // Integer types that map to INT32
         return Types.primitive( PrimitiveType.PrimitiveTypeName.INT32, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .named( fieldName );
      } else if ( XSD.xlong.equals( xsdResource ) || XSD.unsignedInt.equals( xsdResource )
            || XSD.unsignedLong.equals( xsdResource ) ) { // Long types that map to INT64
         return Types.primitive( PrimitiveType.PrimitiveTypeName.INT64, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .named( fieldName );
      } else if ( XSD.date.equals( xsdResource ) ) { // Date type with logical annotation
         return Types.primitive( PrimitiveType.PrimitiveTypeName.INT32, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.dateType() )
               .named( fieldName );
      } else if ( XSD.time.equals( xsdResource ) || XSD.gDay.equals( xsdResource )
            || XSD.gMonth.equals( xsdResource ) || XSD.gYear.equals( xsdResource )
            || XSD.gYearMonth.equals( xsdResource ) || XSD.gMonthDay.equals( xsdResource ) ) { // Time types as string
         return Types.primitive( PrimitiveType.PrimitiveTypeName.BINARY, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.stringType() )
               .named( fieldName );
      } else if ( XSD.dateTime.equals( xsdResource ) || XSD.dateTimeStamp.equals(
            xsdResource ) ) { // DateTime types with timestamp annotation
         return Types.primitive( PrimitiveType.PrimitiveTypeName.INT64, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.timestampType( isTimezoneAvailable, LogicalTypeAnnotation.TimeUnit.MICROS ) )
               .named( fieldName );
      } else if ( XSD.duration.equals( xsdResource ) || XSD.yearMonthDuration.equals( xsdResource )
            || XSD.dayTimeDuration.equals( xsdResource ) ) { // Duration types as string
         return Types.primitive( PrimitiveType.PrimitiveTypeName.BINARY, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.stringType() )
               .named( fieldName );
      } else if ( XSD.hexBinary.equals( xsdResource ) || XSD.base64Binary.equals( xsdResource ) ) { // Binary types
         return Types.primitive( PrimitiveType.PrimitiveTypeName.BINARY, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .named( fieldName );
      } else if ( XSD.xstring.equals( xsdResource ) || XSD.anyURI.equals( xsdResource )
            || XSD.normalizedString.equals( xsdResource ) || XSD.token.equals( xsdResource )
            || XSD.Name.equals( xsdResource ) || XSD.QName.equals( xsdResource )
            || XSD.language.equals( xsdResource ) || XSD.NMTOKEN.equals( xsdResource )
            || XSD.NCName.equals( xsdResource ) || XSD.ID.equals( xsdResource )
            || XSD.IDREF.equals( xsdResource ) ) { // String types (including all string-derived types)
         return Types.primitive( PrimitiveType.PrimitiveTypeName.BINARY, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.stringType() )
               .named( fieldName );
      } else { // Default fallback for unknown types
         return Types.primitive( PrimitiveType.PrimitiveTypeName.BINARY, org.apache.parquet.schema.Type.Repetition.OPTIONAL )
               .as( LogicalTypeAnnotation.stringType() )
               .named( fieldName );
      }
   }
}
