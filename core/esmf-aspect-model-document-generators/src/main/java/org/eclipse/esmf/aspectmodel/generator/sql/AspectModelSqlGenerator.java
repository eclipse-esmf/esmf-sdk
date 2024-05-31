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

import org.eclipse.esmf.aspectmodel.generator.ArtifactGenerator;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.AspectModelDatabricksDenormalizedSqlVisitor;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.AspectModelDatabricksDenormalizedSqlVisitorContextBuilder;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlGenerationConfig;
import org.eclipse.esmf.metamodel.Aspect;

/**
 * Generates SQL scripts from an Aspect Model that set up tables to contain the data of the Aspect.
 */
public class AspectModelSqlGenerator implements ArtifactGenerator<String, String, Aspect, SqlGenerationConfig, SqlArtifact> {
   public static final AspectModelSqlGenerator INSTANCE = new AspectModelSqlGenerator();

   private AspectModelSqlGenerator() {
   }

   @Override
   public SqlArtifact apply( final Aspect aspect, final SqlGenerationConfig sqlGenerationConfig ) {
      final String content = switch ( sqlGenerationConfig.dialect() ) {
         case DATABRICKS -> switch ( sqlGenerationConfig.mappingStrategy() ) {
            case DENORMALIZED -> {
               final DatabricksSqlGenerationConfig config =
                     sqlGenerationConfig.dialectSpecificConfig() instanceof final DatabricksSqlGenerationConfig databricksConfig
                           ? databricksConfig
                           : new DatabricksSqlGenerationConfig();
               yield aspect.accept( new AspectModelDatabricksDenormalizedSqlVisitor( config ),
                     AspectModelDatabricksDenormalizedSqlVisitorContextBuilder.builder().build() );
            }
         };
      };

      return new SqlArtifact( aspect.getName() + ".sql", content );
   }
}
