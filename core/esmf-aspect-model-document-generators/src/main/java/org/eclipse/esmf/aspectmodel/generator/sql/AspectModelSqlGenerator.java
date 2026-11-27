/*
 * Copyright (c) 2024 Robert Bosch Manufacturing Solutions GmbH
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

import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.AspectGenerator;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlDialect;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.generator.sql.postgresql.PostgreSqlDialect;
import org.eclipse.esmf.aspectmodel.generator.sql.postgresql.PostgreSqlGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.sql.postgresql.PostgreSqlGenerationConfigBuilder;
import org.eclipse.esmf.metamodel.Aspect;

/**
 * Generates SQL scripts from an Aspect Model that set up tables to contain the data of the Aspect.
 */
public class AspectModelSqlGenerator extends AspectGenerator<String, String, SqlGenerationConfig, SqlArtifact> {
   public AspectModelSqlGenerator( final Aspect aspect, final SqlGenerationConfig config ) {
      super( aspect, config );
   }

   private <U extends SqlGenerationConfig.DialectSpecificConfig<D, C, X>,
         D extends SqlType,
         C extends SqlColumnDefinition<D, C>,
         T extends SqlTableDefinition<D, C, X>,
         X extends SqlConstraint> Function<Aspect, SqlArtifact> generator(
               final SqlDialect<U, D, C, T, X> dialect, final SqlGenerationConfig.MappingStrategy mappingStrategy, final U config ) {
      return switch ( mappingStrategy ) {
         case NORMALIZED -> new NormalizedSqlGenerator<>( config, dialect );
         case DENORMALIZED -> new DenormalizedSqlGenerator<>( config, dialect );
      };
   }

   @Override
   public Stream<SqlArtifact> generate() {
      return Stream.of( switch ( config.dialect() ) {
         case DATABRICKS -> {
            final DatabricksSqlGenerationConfig generationConfig =
                  config.dialectSpecificConfig() instanceof final DatabricksSqlGenerationConfig databricksConfig
                        ? databricksConfig
                        : DatabricksSqlGenerationConfigBuilder.builder().build();
            final DatabricksSqlDialect dialect = new DatabricksSqlDialect( generationConfig );
            yield generator( dialect, config.mappingStrategy(), generationConfig ).apply( aspect() );
         }
         case POSTGRESQL -> {
            final PostgreSqlGenerationConfig generationConfig =
                  config.dialectSpecificConfig() instanceof final PostgreSqlGenerationConfig postgresSpecificConfig
                        ? postgresSpecificConfig
                        : PostgreSqlGenerationConfigBuilder.builder().build();
            final PostgreSqlDialect dialect = new PostgreSqlDialect( generationConfig );
            yield generator( dialect, config.mappingStrategy(), generationConfig ).apply( aspect() );
         }
      } );
   }
}
