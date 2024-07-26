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

package org.eclipse.esmf.aspectmodel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.esmf.aspectmodel.generator.sql.AspectModelSqlGenerator;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlArtifact;
import org.eclipse.esmf.aspectmodel.generator.sql.SqlGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksColumnDefinition;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksColumnDefinitionParser;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlGenerationConfig;
import org.eclipse.esmf.aspectmodel.generator.sql.databricks.DatabricksSqlGenerationConfigBuilder;
import org.eclipse.esmf.metamodel.Aspect;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo( name = "generateSql", defaultPhase = LifecyclePhase.GENERATE_RESOURCES )
public class GenerateSql extends AspectModelMojo {
   private static final Logger LOG = LoggerFactory.getLogger( GenerateSql.class );

   @Parameter( defaultValue = "" + DatabricksSqlGenerationConfig.DEFAULT_INCLUDE_TABLE_COMMENT )
   private boolean includeTableComment;

   @Parameter( defaultValue = "" + DatabricksSqlGenerationConfig.DEFAULT_INCLUDE_COLUMN_COMMENTS )
   private boolean includeColumnComments;

   @Parameter( defaultValue = DatabricksSqlGenerationConfig.DEFAULT_TABLE_COMMAND_PREFIX )
   private String tableCommandPrefix = DatabricksSqlGenerationConfig.DEFAULT_TABLE_COMMAND_PREFIX;

   @Parameter( defaultValue = "" + DatabricksSqlGenerationConfig.DECIMAL_DEFAULT_PRECISION )
   private int decimalPrecision = DatabricksSqlGenerationConfig.DECIMAL_DEFAULT_PRECISION;

   @Parameter( defaultValue = "en" )
   private String language = DatabricksSqlGenerationConfig.DEFAULT_COMMENT_LANGUAGE.getLanguage();

   @Parameter( defaultValue = "databricks" )
   private String dialect = SqlGenerationConfig.Dialect.DATABRICKS.toString().toLowerCase();

   @Parameter( defaultValue = "denormalized" )
   private String strategy = SqlGenerationConfig.MappingStrategy.DENORMALIZED.toString().toLowerCase();

   @Parameter( property = "column" )
   private List<String> customColumns = List.of();

   @Override
   public void executeGeneration() throws MojoExecutionException {
      validateParameters();

      final List<DatabricksColumnDefinition> customColumnDefinitions = customColumns.stream()
            .map( columnDefinition -> new DatabricksColumnDefinitionParser( columnDefinition ).get() )
            .toList();

      final Set<Aspect> aspectModels = loadAspects();
      for ( final Aspect aspect : aspectModels ) {
         final DatabricksSqlGenerationConfig generatorConfig =
               DatabricksSqlGenerationConfigBuilder.builder()
                     .commentLanguage( Locale.forLanguageTag( language ) )
                     .includeTableComment( includeTableComment )
                     .includeColumnComments( includeColumnComments )
                     .createTableCommandPrefix( tableCommandPrefix )
                     .decimalPrecision( decimalPrecision )
                     .customColumns( customColumnDefinitions )
                     .build();
         final SqlGenerationConfig sqlConfig = new SqlGenerationConfig( SqlGenerationConfig.Dialect.valueOf( dialect.toUpperCase() ),
               SqlGenerationConfig.MappingStrategy.valueOf( strategy.toUpperCase() ), generatorConfig );
         final SqlArtifact result = AspectModelSqlGenerator.INSTANCE.apply( aspect, sqlConfig );

         try ( final OutputStream out = getOutputStreamForFile( aspect.getName() + ".sql", outputDirectory ) ) {
            out.write( result.getContent().getBytes() );
         } catch ( final IOException exception ) {
            throw new MojoExecutionException( "Could not write SQL file.", exception );
         }
      }
      LOG.info( "Successfully generated SQL script." );
   }
}
