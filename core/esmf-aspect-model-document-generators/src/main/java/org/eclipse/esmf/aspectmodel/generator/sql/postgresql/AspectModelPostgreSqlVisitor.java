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

package org.eclipse.esmf.aspectmodel.generator.sql.postgresql;

import java.util.Map;
import java.util.Optional;

import org.apache.jena.vocabulary.XSD;

import org.eclipse.esmf.aspectmodel.generator.sql.AspectModelSqlVisitor;
import org.eclipse.esmf.metamodel.datatype.RdfDatatypeUris;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import com.google.common.collect.ImmutableMap;

/**
 * Base class for AspectVisitors creating PostgreSQL statements.
 *
 * @param <C> the context class
 */
public abstract class AspectModelPostgreSqlVisitor<C> extends AspectModelSqlVisitor<C> {
   protected final Map<String, PostgreSqlType> postgreSqlTypeMap;
   protected final PostgreSqlGenerationConfig config;

   public AspectModelPostgreSqlVisitor( final PostgreSqlGenerationConfig config ) {
      this.config = config;
      postgreSqlTypeMap = ImmutableMap.<String, PostgreSqlType>builder()
            .put( XSD.xstring.getURI(), PostgreSqlType.TEXT )
            .put( XSD.xboolean.getURI(), PostgreSqlType.BOOLEAN )
            .put( XSD.decimal.getURI(), new PostgreSqlType.PostgreSqlNumeric( Optional.of( config.decimalPrecision() ),
                  Optional.of( config.decimalScale() ) ) )
            .put( XSD.integer.getURI(), new PostgreSqlType.PostgreSqlNumeric( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.xdouble.getURI(), PostgreSqlType.DOUBLE_PRECISION )
            .put( XSD.xfloat.getURI(), PostgreSqlType.REAL )
            .put( XSD.date.getURI(), PostgreSqlType.TEXT )
            .put( XSD.time.getURI(), PostgreSqlType.TEXT )
            .put( XSD.dateTime.getURI(), PostgreSqlType.TIMESTAMP )
            .put( XSD.dateTimeStamp.getURI(), PostgreSqlType.TIMESTAMP_WITH_TIME_ZONE )
            .put( XSD.gYear.getURI(), PostgreSqlType.TEXT )
            .put( XSD.gMonth.getURI(), PostgreSqlType.TEXT )
            .put( XSD.gDay.getURI(), PostgreSqlType.TEXT )
            .put( XSD.gYearMonth.getURI(), PostgreSqlType.TEXT )
            .put( XSD.gMonthDay.getURI(), PostgreSqlType.TEXT )
            .put( XSD.duration.getURI(), PostgreSqlType.TEXT )
            .put( XSD.yearMonthDuration.getURI(), PostgreSqlType.TEXT )
            .put( XSD.dayTimeDuration.getURI(), PostgreSqlType.TEXT )
            .put( XSD.xbyte.getURI(), PostgreSqlType.SMALLINT )
            .put( XSD.xshort.getURI(), PostgreSqlType.SMALLINT )
            .put( XSD.xint.getURI(), PostgreSqlType.INTEGER )
            .put( XSD.xlong.getURI(), PostgreSqlType.BIGINT )
            .put( XSD.unsignedByte.getURI(), PostgreSqlType.SMALLINT )
            .put( XSD.unsignedShort.getURI(), PostgreSqlType.INTEGER )
            .put( XSD.unsignedInt.getURI(), PostgreSqlType.BIGINT )
            .put( XSD.unsignedLong.getURI(), new PostgreSqlType.PostgreSqlNumeric( Optional.of( 20 ) ) )
            .put( XSD.positiveInteger.getURI(), new PostgreSqlType.PostgreSqlNumeric( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.nonNegativeInteger.getURI(), new PostgreSqlType.PostgreSqlNumeric( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.negativeInteger.getURI(), new PostgreSqlType.PostgreSqlNumeric( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.nonPositiveInteger.getURI(), new PostgreSqlType.PostgreSqlNumeric( Optional.of( config.decimalPrecision() ) ) )
            .put( XSD.hexBinary.getURI(), PostgreSqlType.BYTEA )
            .put( XSD.base64Binary.getURI(), PostgreSqlType.BYTEA )
            .put( XSD.anyURI.getURI(), PostgreSqlType.TEXT )
            .put( SammNs.SAMM.curie().getURI(), PostgreSqlType.TEXT )
            .put( RdfDatatypeUris.LANG_STRING, PostgreSqlType.TEXT )
            .build();
   }
}
