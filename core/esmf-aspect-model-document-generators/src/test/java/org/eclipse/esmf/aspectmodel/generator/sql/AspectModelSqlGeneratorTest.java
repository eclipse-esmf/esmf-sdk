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

import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlDialect;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlGenerationConfigBuilder;
import org.eclipse.esmf.aspectmodel.generator.sql.postgresql.PostgreSqlGenerationConfig;
import org.eclipse.esmf.metamodel.Aspect;
import org.eclipse.esmf.test.TestAspect;
import org.eclipse.esmf.test.TestResources;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class AspectModelSqlGeneratorTest {
   @ParameterizedTest
   @Execution( ExecutionMode.CONCURRENT )
   @EnumSource( value = TestAspect.class )
   void testDatabricksDenormalizedGeneration( final TestAspect testAspect ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      assertThatCode( () -> {
         final DatabricksSqlGenerationConfig dialectSpecificConfig = DatabricksSqlGenerationConfigBuilder.builder()
               .includeTableComment( true )
               .includeColumnComments( true )
               .commentLanguage( Locale.ENGLISH )
               .build();
         final String result = new DenormalizedSqlGenerator<>( dialectSpecificConfig, new DatabricksSqlDialect( dialectSpecificConfig ) )
               .apply( aspect ).getContent();
         assertThat( result ).contains( "TBLPROPERTIES ('x-samm-aspect-model-urn'='" );
         assertThat( result ).doesNotContain( "ARRAY<ARRAY" );
      } ).doesNotThrowAnyException();
   }

   @ParameterizedTest
   @Execution( ExecutionMode.CONCURRENT )
   @EnumSource( value = TestAspect.class )
   void testDatabricksNormalizedGeneration( final TestAspect testAspect ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      assertThatCode( () -> {
         final DatabricksSqlGenerationConfig dialectSpecificConfig = DatabricksSqlGenerationConfigBuilder.builder()
               .includeTableComment( true )
               .includeColumnComments( true )
               .commentLanguage( Locale.ENGLISH )
               .build();
         final String result = new NormalizedSqlGenerator<>( dialectSpecificConfig, new DatabricksSqlDialect( dialectSpecificConfig ) )
               .apply( aspect ).getContent();
         assertThat( result ).contains( "TBLPROPERTIES ('x-samm-aspect-model-urn'='" );
         assertThat( result ).doesNotContain( "ARRAY<ARRAY" );
      } ).doesNotThrowAnyException();
   }

   @ParameterizedTest
   @Execution( ExecutionMode.CONCURRENT )
   @EnumSource( value = TestAspect.class )
   void testDatabricksDenormalizedGenerationExcludeComments( final TestAspect testAspect ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      assertThatCode( () -> {
         final DatabricksSqlGenerationConfig dialectSpecificConfig = DatabricksSqlGenerationConfigBuilder.builder()
               .includeTableComment( false )
               .includeColumnComments( false )
               .commentLanguage( Locale.ENGLISH )
               .build();
         final SqlArtifact sqlArtifact = new AspectModelSqlGenerator( aspect, SqlGenerationConfigBuilder.builder()
               .dialect( SqlGenerationConfig.Dialect.DATABRICKS )
               .dialectSpecificConfig( dialectSpecificConfig )
               .build() ).singleResult();
         final String result = sqlArtifact.getContent();

         assertThat( result ).contains( "TBLPROPERTIES ('x-samm-aspect-model-urn'='" );
         assertThat( result ).doesNotContain( "ARRAY<ARRAY" );
         assertThat( result ).doesNotContain( "COMMENT" );
      } ).doesNotThrowAnyException();
   }

   @Test
   void testDatabricksGenerationDecimalPrecisionAndScale() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_SIMPLE_TYPES ).aspect();
      assertThatCode( () -> {
         final DatabricksSqlGenerationConfig dialectSpecificConfig = DatabricksSqlGenerationConfigBuilder.builder()
               .decimalPrecision( 38 )
               .decimalScale( 38 )
               .build();
         final SqlArtifact sqlArtifact = new AspectModelSqlGenerator( aspect, SqlGenerationConfigBuilder.builder()
               .dialect( SqlGenerationConfig.Dialect.DATABRICKS )
               .dialectSpecificConfig( dialectSpecificConfig )
               .build() ).singleResult();
         final String result = sqlArtifact.getContent();

         assertThat( result ).contains( "DECIMAL(38,38)" );
      } ).doesNotThrowAnyException();
   }

   @ParameterizedTest
   @Execution( ExecutionMode.CONCURRENT )
   @EnumSource( value = TestAspect.class )
   void testPostgreSqlDenormalizedGeneration( final TestAspect testAspect ) {
      final Aspect aspect = TestResources.load( testAspect ).aspect();
      assertThatCode( () -> {
         final SqlArtifact sqlArtifact = new AspectModelSqlGenerator( aspect, SqlGenerationConfigBuilder.builder()
               .mappingStrategy( SqlGenerationConfig.MappingStrategy.DENORMALIZED )
               .dialect( SqlGenerationConfig.Dialect.POSTGRESQL )
               .dialectSpecificConfig( new PostgreSqlGenerationConfig() )
               .build() ).singleResult();
         final String result = sqlArtifact.getContent();

         assertThat( result ).contains( "-- x-samm-aspect-model-urn: " + aspect.urn() );
         assertThat( result ).doesNotContain( "TBLPROPERTIES", "ARRAY<", " STRING" );
      } ).doesNotThrowAnyException();
   }

   @Test
   void testSqlGenerationConfigDefaults() {
      final SqlGenerationConfig config = SqlGenerationConfigBuilder.builder().build();
      assertThat( config.dialect() ).isEqualTo( SqlGenerationConfig.Dialect.DATABRICKS );
      assertThat( config.mappingStrategy() ).isEqualTo( SqlGenerationConfig.MappingStrategy.DENORMALIZED );
   }

   @Test
   void testPostgreSqlNormalizedGeneration() {
      final Aspect aspect = TestResources.load( TestAspect.ASPECT_WITH_COLLECTION ).aspect();
      final SqlArtifact artifact = new AspectModelSqlGenerator( aspect, SqlGenerationConfigBuilder.builder()
            .mappingStrategy( SqlGenerationConfig.MappingStrategy.NORMALIZED )
            .dialect( SqlGenerationConfig.Dialect.POSTGRESQL )
            .dialectSpecificConfig( new PostgreSqlGenerationConfig() )
            .build() ).singleResult();

      assertThat( artifact.language() ).isEqualTo( Locale.ENGLISH );
      assertThat( artifact.getContent() )
            .contains( "CREATE TABLE IF NOT EXISTS aspect_with_collection__test_property" )
            .contains( "value TEXT NOT NULL" )
            .contains( "REFERENCES aspect_with_collection (_id)" )
            .contains( "-- x-samm-aspect-model-urn: " + aspect.urn() );
   }
}
