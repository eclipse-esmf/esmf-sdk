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

package org.eclipse.esmf.aspectmodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.eclipse.esmf.aspectmodel.resolver.github.GithubModelSourceConfig;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;

@SuppressWarnings( "JUnitMixedFramework" )
public class MojoConfigTest extends AspectModelMojoTest {
   @Test
   public void testInvalidModelsRoot() throws Exception {
      final Mojo validate = getMojo( "test-pom-invalid-models-root", "validate" );
      assertThatCode( validate::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessageContaining( "Validation failed" );
   }

   @Test
   public void testMissingIncludes() throws Exception {
      final Mojo validate = getMojo( "test-pom-missing-includes", "validate" );
      assertThatCode( validate::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Missing configuration. Please provide Aspect Models to be included." );
   }

   @Test
   public void testMissingInclude() throws Exception {
      final Mojo validate = getMojo( "test-pom-missing-include", "validate" );
      assertThatCode( validate::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Missing configuration. Please provide Aspect Models to be included." );
   }

   @Test
   public void testGitHubServerConfig() throws Exception {
      final String serverConfig = """
            <configuration>
                <repository>test-org/test-repository</repository>
                <directory>src/main/resources/aspects</directory>
                <branch>main</branch>
                <token>THE_TOKEN</token>
            </configuration>
            """;
      final AspectModelMojo validate = (AspectModelMojo) getMojo( "test-pom-github-server-config", "validate", serverConfig );
      validate.execute();
      final GithubModelSourceConfig config = validate.gitHubConfig;
      assertThat( config.repository().owner() ).isEqualTo( "test-org" );
      assertThat( config.repository().repository() ).isEqualTo( "test-repository" );
      assertThat( config.directory() ).isEqualTo( "src/main/resources/aspects" );
      assertThat( config.repository().branchOrTag().name() ).isEqualTo( "main" );
      assertThat( config.token() ).isEqualTo( "THE_TOKEN" );
   }
}
