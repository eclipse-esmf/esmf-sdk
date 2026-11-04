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

import java.util.Map;
import java.util.Optional;

import org.apache.jena.vocabulary.XSD;

import org.eclipse.esmf.aspectmodel.generator.sql.AspectModelSqlVisitor;
import org.eclipse.esmf.metamodel.datatype.RdfDatatypeUris;
import org.eclipse.esmf.metamodel.vocabulary.SammNs;

import com.google.common.collect.ImmutableMap;

/**
 * Base class for AspectVisitors creating Databricks SQL statements.
 *
 * @param <C> the context class
 */
public abstract class AspectModelDatabricksVisitor<C> extends AspectModelSqlVisitor<C> {
   protected final Map<String, DatabricksType> databricksTypeMap;
   protected final DatabricksSqlGenerationConfig config;

   protected AspectModelDatabricksVisitor( final DatabricksSqlGenerationConfig config ) {
      this.config = config;
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
}
