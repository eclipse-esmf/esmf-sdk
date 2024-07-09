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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Locale;

import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlGenerationConfigBuilder;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class AspectModelSqlGeneratorTest {
   @ParameterizedTest
   @EnumSource( value = TestAspect.class )
   void testDatabricksGeneration( final TestAspect testAspect ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      assertThatCode( () -> {
         final DatabricksSqlGenerationConfig dialectSpecificConfig = DatabricksSqlGenerationConfigBuilder.builder()
               .includeTableComment( true )
               .includeColumnComments( true )
               .commentLanguage( Locale.ENGLISH )
               .build();
         final SqlArtifact sqlArtifact = AspectModelSqlGenerator.INSTANCE.apply( aspect, SqlGenerationConfigBuilder.builder()
               .dialect( SqlGenerationConfig.Dialect.DATABRICKS )
               .dialectSpecificConfig( dialectSpecificConfig )
               .build() );
         final String result = sqlArtifact.getContent();

         assertThat( result ).contains( "TBLPROPERTIES ('x-samm-aspect-model-urn'='" );
         assertThat( result ).doesNotContain( "ARRAY<ARRAY" );
      } ).doesNotThrowAnyException();
   }
}
