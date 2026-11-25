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

package org.eclipse.esmf.aspectmodel.generator.sql;

import java.util.function.BiFunction;

import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.Property;
import org.eclipse.esmf.metamodel.StructureElement;

import com.google.common.base.CaseFormat;

/**
 * Base class for SQL artifact generators
 *
 * @param <U> the user-provided config
 * @param <D> the type of the column datatypes
 * @param <C> the column definition type
 * @param <T> the table definition type
 * @param <X> the constraint type
 */
public abstract class SqlArtifactGeneratorBase<
      U extends SqlGenerationConfig.DialectSpecificConfig<D, C, X>,
      D extends SqlType,
      C extends SqlColumnDefinition<D, C>,
      T extends SqlTableDefinition<D, C, X>,
      X extends SqlConstraint>
      implements BiFunction<Aspect, U, SqlArtifact> {
   protected static final int MAX_RECURSION_DEPTH = 20;
   protected static final String LEVEL_DELIMITER = "__";
   protected final U config;
   protected final SqlDialect<U, D, C, T, X> sqlDialect;

   public SqlArtifactGeneratorBase( final U config, final SqlDialect<U, D, C, T, X> sqlDialect ) {
      this.config = config;
      this.sqlDialect = sqlDialect;
   }

   protected String tableName( final StructureElement element ) {
      return CaseFormat.UPPER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, element.getName() );
   }

   protected String columnName( final Property property ) {
      return CaseFormat.LOWER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, property.getPayloadName() );
   }
}
