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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringReader;
import javax.inject.Inject;

import org.apache.maven.api.plugin.testing.InjectMojo;
import org.apache.maven.api.plugin.testing.MojoTest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.junit.jupiter.api.Test;

@MojoTest
public class MojoConfigTest extends AspectModelMojoTest {
   @Inject
   private MavenSession session;

   @Test
   @InjectMojo(
         goal = Validate.MAVEN_GOAL,
         pom = "src/test/resources/test-pom-invalid-models-root/pom.xml"
   )
   public void testInvalidModelsRoot( final Validate validate ) {
      assertThatCode( validate::execute )
            .isInstanceOf( MojoExecutionException.class );
   }

   @Test
   @InjectMojo(
         goal = Validate.MAVEN_GOAL,
         pom = "src/test/resources/test-pom-missing-includes/pom.xml"
   )
   public void testMissingIncludes( final Validate validate ) {
      assertThatCode( validate::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Missing configuration. Please provide Aspect Models to be included." );
   }

   @Test
   @InjectMojo(
         goal = Validate.MAVEN_GOAL,
         pom = "src/test/resources/test-pom-missing-include/pom.xml"
   )
   public void testMissingInclude( final Validate validate ) {
      assertThatCode( validate::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessage( "Missing configuration. Please provide Aspect Models to be included." );
   }

   @Test
   @InjectMojo(
         goal = Validate.MAVEN_GOAL,
         pom = "src/test/resources/test-pom-github-server-config/pom.xml"
   )
   public void testGitHubServerConfig( final Validate validate ) throws Exception {
      // Inject <configuration> block into session's config
      final String serverConfigXml = """
            <configuration>
                <repository>test-org/test-repository</repository>
                <directory>src/main/resources/aspects</directory>
                <branch>main</branch>
                <token>THE_TOKEN</token>
            </configuration>
            """;
      final Xpp3Dom serverConfig = Xpp3DomBuilder.build( new StringReader( serverConfigXml ) );
      final Server server = new Server();
      server.setId( GITHUB_SERVER_CONFIG_ID );
      server.setConfiguration( serverConfig );
      final Settings settings = mock( Settings.class );
      when( session.getSettings() ).thenReturn( settings );
      when( settings.getServer( GITHUB_SERVER_CONFIG_ID ) ).thenReturn( server );

      assertThatCode( validate::execute ).doesNotThrowAnyException();
      assertThat( validate.gitHubConfigs ).isNotEmpty().allSatisfy( config -> {
         assertThat( config.repository().owner() ).isEqualTo( "test-org" );
         assertThat( config.repository().repository() ).isEqualTo( "test-repository" );
         assertThat( config.directory() ).isEqualTo( "src/main/resources/aspects" );
         assertThat( config.repository().branchOrTag().name() ).isEqualTo( "main" );
         assertThat( config.token() ).isEqualTo( "THE_TOKEN" );
      } );
   }
}
