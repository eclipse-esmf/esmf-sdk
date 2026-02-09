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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@MojoTest
public class ValidateTest extends AspectModelMojoTest {
   @Inject
   private MavenSession session;

   @Test
   @InjectMojo(
         goal = Validate.MAVEN_GOAL,
         pom = "src/test/resources/validate-pom-valid-aspect-model/pom.xml"
   )
   public void testValidateValidAspectModel( final Validate validate ) {
      assertThatCode( validate::execute )
            .doesNotThrowAnyException();
   }

   @Test
   @InjectMojo(
         goal = Validate.MAVEN_GOAL,
         pom = "src/test/resources/validate-pom-invalid-aspect-model/pom.xml"
   )
   public void testValidateInvalidAspectModel( final Validate validate ) {
      assertThatCode( validate::execute )
            .isInstanceOf( MojoExecutionException.class )
            .hasMessageContaining( "Error at line 17 column 3" );
   }

   @Test
   @InjectMojo(
         goal = Validate.MAVEN_GOAL,
         pom = "src/test/resources/validate-pom-multiple-aspect-models/pom.xml"
   )
   public void testValidateMultipleAspectModels( final Validate validate ) {
      assertThatCode( validate::execute )
            .doesNotThrowAnyException();
   }

   @Test
   @InjectMojo(
         goal = Validate.MAVEN_GOAL,
         pom = "src/test/resources/validate-pom-resolve-from-github/pom.xml"
   )
   @Disabled( "Temporarily disabled due to an issue under investigation in CI" )
   public void testValidateWithResolutionFromGitHub( final Validate validate ) throws XmlPullParserException, IOException {
      // Inject <configuration> block into session's config
      final String serverConfigXml = """
            <configuration>
                <repository>eclipse-esmf/esmf-sdk</repository>
                <directory>core/esmf-test-aspect-models/src/main/resources/valid</directory>
                <branch>main</branch>
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
   }
}
