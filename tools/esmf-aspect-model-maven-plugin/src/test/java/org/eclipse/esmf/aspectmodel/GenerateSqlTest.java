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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.junit.jupiter.api.Test;

@MojoTest
public class GenerateSqlTest extends AspectModelMojoTest {
   @Test
   @InjectMojo(
         goal = GenerateSql.MAVEN_GOAL,
         pom = "src/test/resources/generate-sql-pom-valid-aspect-model-default-settings/pom.xml"
   )
   public void testGenerateSqlWithDefaultSettings( final GenerateSql generateSql ) throws IOException {
      assertThatCode( generateSql::execute ).doesNotThrowAnyException();
      final Path generatedFile = generatedFilePath( "AspectWithSimpleTypes.sql" );
      assertThat( generatedFile ).exists();
      final String sqlContent = new String( Files.readAllBytes( generatedFile ) );

      assertThat( sqlContent ).contains( "CREATE TABLE IF NOT EXISTS aspect_with_simple_types" );
   }

   @Test
   @InjectMojo(
         goal = GenerateSql.MAVEN_GOAL,
         pom = "src/test/resources/generate-sql-pom-valid-aspect-model-adjusted-settings/pom.xml"
   )
   public void testGenerateSqlWithAdjustedSettings( final GenerateSql generateSql ) throws IOException {
      assertThatCode( generateSql::execute ).doesNotThrowAnyException();
      final Path generatedFile = generatedFilePath( "AspectWithSimpleTypes.sql" );
      assertThat( generatedFile ).exists();
      final String sqlContent = new String( Files.readAllBytes( generatedFile ) );

      assertThat( sqlContent ).contains( "CREATE TABLE aspect_with_simple_types" );
      assertThat( sqlContent ).contains( "custom_column ARRAY<STRING> NOT NULL COMMENT 'Custom column'" );
      assertThat( sqlContent ).contains( "DECIMAL(23)" );
   }
}
