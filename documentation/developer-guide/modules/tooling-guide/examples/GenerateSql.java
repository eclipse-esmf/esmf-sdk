/*
 * Copyright (c) 2023 Robert Bosch Manufacturing Solutions GmbH
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

package examples;

// tag::imports[]
import java.util.Locale;
import java.util.Optional;

import org.eclipse.esmf.aspectmodel.generator.sql.AspectModelSqlGenerator;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksColumnDefinitionBuilder;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksType;
import org.eclipse.esmf.aspectmodel.resolver.AspectModelResolver;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.metamodel.loader.AspectModelLoader;
// end::imports[]

import org.junit.jupiter.api.Test;

public class GenerateSql extends AbstractGenerator {
   @Test
   public void generate() throws IOException {
      // tag::generate[]
      // AspectModel as returned by the AspectModelLoader
      final AspectModel aspectModel = // ...
            // end::generate[]
            new AspectModelLoader().load(
                  new File( "aspect-models/org.eclipse.esmf.examples.movement/1.0.0/Movement.ttl" ) );
      // tag::generate[]

      final DatabricksSqlGenerationConfig databricksSqlGenerationConfig =
            DatabricksSqlGenerationConfigBuilder.builder()
                  .commentLanguage( Locale.ENGLISH ) // optional
                  .includeTableComment( true )       // optional
                  .includeColumnComments( true )     // optional
                  .decimalPrecision( 10 )            // optional
                  .customColumns( List.of(           // optional
                        DatabricksColumnDefinitionBuilder.builder()
                              .name( "custom_column" )
                              .type( new DatabricksType.DatabricksArray( DatabricksType.STRING ) )
                              .nullable( false )
                              .comment( Optional.of( "Custom column" ) )
                              .build() ) )
                  .build();
      final SqlGenerationConfig sqlGenerationConfig =
            SqlGenerationConfigBuilder.builder()
                  .dialect( SqlGenerationConfig.Dialect.DATABRICKS )
                  .mappingStrategy( SqlGenerationConfig.MappingStrategy.DENORMALIZED )
                  .dialectSpecificConfig( databricksSqlGenerationConfig )
                  .build();
      final String result = AspectModelSqlGenerator.INSTANCE.apply( aspectModel.aspect(), sqlGenerationConfig ).getContent();
      // end::generate[]
   }
}
