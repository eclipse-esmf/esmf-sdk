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

package org.eclipse.esmf.aspectmodel.generator.sql.databricks;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.jena.vocabulary.XSD;

import org.eclipse.esmf.aspectmodel.generator.sql.SqlColumnDefinition;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlDialect;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlTableDefinition;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlType;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlValue;
import org.eclipse.esmf.metamodel.datatype.RdfDatatypeUris;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import com.google.common.collect.ImmutableMap;

public class DatabricksSqlDialect<
      U extends SqlGenerationConfig.DialectSpecificConfig<D, C>,
      D extends SqlType,
      C extends SqlColumnDefinition<D, C>,
      T extends SqlTableDefinition<D, C>>
      implements SqlDialect<DatabricksSqlGenerationConfig, DatabricksType, DatabricksColumnDefinition, DatabricksTableDefinition> {
   protected final Map<String, DatabricksType> databricksTypeMap;

   public DatabricksSqlDialect( final U config ) {
      databricksTypeMap = ImmutableMap.<String, DatabricksType>builder()
            .put( XSD.xstring.getURI(), DatabricksType.STRING )
            .put( XSD.xboolean.getURI(), DatabricksType.BOOLEAN )
            .put( XSD.decimal.getURI(), new DatabricksType.DatabricksDecimal( Optional.of( config.decimalPrecision() ),
                  Optional.of( config.decimalScale() ) ) )
            .put( XSD.integer.getURI(), new DatabricksType.DatabricksDecimal( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.xdouble.getURI(), DatabricksType.DOUBLE )
            .put( XSD.xfloat.getURI(), DatabricksType.FLOAT )
            .put( XSD.date.getURI(), DatabricksType.STRING )
            .put( XSD.time.getURI(), DatabricksType.STRING )
            .put( XSD.dateTime.getURI(), DatabricksType.TIMESTAMP )
            .put( XSD.dateTimeStamp.getURI(), DatabricksType.TIMESTAMP )
            .put( XSD.gYear.getURI(), DatabricksType.STRING )
            .put( XSD.gMonth.getURI(), DatabricksType.STRING )
            .put( XSD.gDay.getURI(), DatabricksType.STRING )
            .put( XSD.gYearMonth.getURI(), DatabricksType.STRING )
            .put( XSD.gMonthDay.getURI(), DatabricksType.STRING )
            .put( XSD.duration.getURI(), DatabricksType.STRING )
            .put( XSD.yearMonthDuration.getURI(), DatabricksType.STRING )
            .put( XSD.dayTimeDuration.getURI(), DatabricksType.STRING )
            .put( XSD.xbyte.getURI(), DatabricksType.TINYINT )
            .put( XSD.xshort.getURI(), DatabricksType.SMALLINT )
            .put( XSD.xint.getURI(), DatabricksType.INT )
            .put( XSD.xlong.getURI(), DatabricksType.BIGINT )
            .put( XSD.unsignedByte.getURI(), DatabricksType.SMALLINT )
            .put( XSD.unsignedShort.getURI(), DatabricksType.INT )
            .put( XSD.unsignedInt.getURI(), DatabricksType.BIGINT )
            .put( XSD.unsignedLong.getURI(), new DatabricksType.DatabricksDecimal( Optional.of( 20 ) ) )
            .put( XSD.positiveInteger.getURI(), new DatabricksType.DatabricksDecimal( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.nonNegativeInteger.getURI(), new DatabricksType.DatabricksDecimal( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.negativeInteger.getURI(), new DatabricksType.DatabricksDecimal( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.nonPositiveInteger.getURI(), new DatabricksType.DatabricksDecimal( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.hexBinary.getURI(), DatabricksType.BINARY )
            .put( XSD.base64Binary.getURI(), DatabricksType.BINARY )
            .put( XSD.anyURI.getURI(), DatabricksType.STRING )
            .put( SammNs.SAMM.curie().getURI(), DatabricksType.STRING )
            .put( RdfDatatypeUris.LANG_STRING, DatabricksType.STRING )
            .build();
   }

   @Override
   public SqlTableDefinition<DatabricksType, DatabricksColumnDefinition> tableDefinition( final String name,
         final List<DatabricksColumnDefinition> columns, final Optional<String> comment, final List<String> tableConstraints,
         final Map<String, SqlValue<? extends DatabricksType>> tableProperties ) {
      return new DatabricksTableDefinition( name, columns, comment, tableConstraints, tableProperties );
   }

   @Override
   public DatabricksColumnDefinition columnDefinition( final String name, final DatabricksType datatype, final boolean isNullable,
         final Optional<String> comment ) {
      return new DatabricksColumnDefinition( name, datatype, isNullable, comment );
   }

   @Override
   public <V extends DatabricksType> SqlValue<V> sqlStringValue( final String value ) {
      // noinspection unchecked
      return new DatabricksSqlValue<>( (Class<V>) DatabricksType.DatabricksString.class, value );
   }

   @Override
   public DatabricksType sqlType( final String sammTypeUri ) {
      return databricksTypeMap.get( sammTypeUri );
   }

   @SuppressWarnings( "unchecked" )
   @Override
   public <A extends DatabricksType> A sqlArrayType( final DatabricksType baseType ) {
      final DatabricksType.DatabricksArray databricksArray = new DatabricksType.DatabricksArray( baseType );
      return (A) databricksArray;
   }
}
