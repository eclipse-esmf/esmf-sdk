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

import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.generator.AspectGenerator;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.AspectModelDatabricksDenormalizedSqlVisitor;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.AspectModelDatabricksDenormalizedSqlVisitorContextBuilder;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlGenerationConfig;
import org.eclipse.esmf.metamodel.Aspect;

/**
 * Generates SQL scripts from an Aspect Model that set up tables to contain the data of the Aspect.
 */
public class AspectModelSqlGenerator extends AspectGenerator<String, String, SqlGenerationConfig, SqlArtifact> {
   @Deprecated( forRemoval = true )
   public static final AspectModelSqlGenerator INSTANCE = new AspectModelSqlGenerator( null );
   public static final SqlGenerationConfig DEFAULT_CONFIG = SqlGenerationConfigBuilder.builder().build();

   public AspectModelSqlGenerator( final Aspect aspect ) {
      this( aspect, DEFAULT_CONFIG );
   }

   public AspectModelSqlGenerator( final Aspect aspect, final SqlGenerationConfig config ) {
      super( aspect, config );
   }

   /**
    * @deprecated Use {@link #AspectModelSqlGenerator(Aspect, SqlGenerationConfig)} and {@link #singleResult()} instead
    */
   @Deprecated( forRemoval = true )
   public SqlArtifact apply( final Aspect aspect, final SqlGenerationConfig sqlGenerationConfig ) {
      return new AspectModelSqlGenerator( aspect, sqlGenerationConfig ).singleResult();
   }

   @Override
   public Stream<SqlArtifact> generate() {
      final String content = switch ( config.dialect() ) {
         case DATABRICKS -> switch ( config.mappingStrategy() ) {
            case DENORMALIZED -> {
               final DatabricksSqlGenerationConfig dataBricksConfig =
                     config.dialectSpecificConfig() instanceof final DatabricksSqlGenerationConfig databricksConfig
                           ? databricksConfig
                           : new DatabricksSqlGenerationConfig();
               yield aspect().accept( new AspectModelDatabricksDenormalizedSqlVisitor( dataBricksConfig ),
                     AspectModelDatabricksDenormalizedSqlVisitorContextBuilder.builder().build() );
            }
         };
      };

      return Stream.of( new SqlArtifact( aspect().getName() + ".sql", content ) );
   }
}
