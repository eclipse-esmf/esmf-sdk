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

/**
 * Base class for SQL artifact generators
 * 
 * @param <U> the user-provided config
 * @param <D> the type of the column datatypes
 * @param <C> the column definition type
 * @param <T> the table definition type
 */
public abstract class SqlArtifactGeneratorBase<
      U extends SqlGenerationConfig.DialectSpecificConfig<D, C>,
      D extends SqlType,
      C extends SqlColumnDefinition<D, C>,
      T extends SqlTableDefinition<D, C>>
      implements BiFunction<Aspect, U, SqlArtifact> {
   protected final U config;

   public SqlArtifactGeneratorBase( final U config ) {
      this.config = config;
   }
}
