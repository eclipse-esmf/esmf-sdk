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

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.ResolverExpressionEvaluatorStub;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.settings.Server;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.configurator.BasicComponentConfigurator;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

public abstract class AspectModelMojoTest extends AbstractMojoTestCase {
   // Due to the nature of how the maven plugin testing harness works, we use a temp directory relative to the project
   protected Path tempDir = Path.of( System.getProperty( "user.dir" ), "target", "test-artifacts" );
   protected Path localRepoDir = Path.of( System.getProperty( "user.home" ), "m2" );

   protected static final String GITHUB_SERVER_CONFIG_ID = "test-github-config";

   @Override
   @Before
   public void setUp() throws Exception {
      super.setUp();
      FileUtils.deleteDirectory( tempDir.toFile() );
      Files.createDirectories( tempDir );
   }

   @Override
   @After
   public void tearDown() throws Exception {
      super.tearDown();
      FileUtils.deleteDirectory( tempDir.toFile() );
   }

   protected Path generatedFilePath( final String... pathParts ) {
      return Path.of( tempDir.toString(), pathParts );
   }

   protected Mojo getMojo( final String pomName, final String mojoName ) throws Exception {
      return getMojo( pomName, mojoName, null );
   }

   protected Mojo getMojo( final String pomName, final String mojoName, final String serverConfig ) throws Exception {
      final MavenSession mavenSession = getMavenSession( new File( "src/test/resources/" + pomName ), serverConfig );
      final MojoExecution mojoExecution = newMojoExecution( mojoName );
      final PlexusConfiguration pluginConfiguration = extractPluginConfiguration( "esmf-aspect-model-maven-plugin",
            new File( "src/test/resources/" + pomName + "/pom.xml" ) );
      final Field configuratorField = getClass().getSuperclass().getSuperclass().getDeclaredField( "configurator" );
      configuratorField.setAccessible( true );
      final BasicComponentConfigurator configurator = (BasicComponentConfigurator) configuratorField.get( this );
      final Mojo mojo = lookupConfiguredMojo( mavenSession, mojoExecution );
      configurator.configureComponent( mojo, pluginConfiguration, new ResolverExpressionEvaluatorStub(),
            getContainer().getContainerRealm() );

      // Inject mavenSession and mavenProject
      for ( final Field field : mojo.getClass().getDeclaredFields() ) {
         if ( field.getName().equals( "mavenSession" ) ) {
            field.setAccessible( true );
            field.set( mojo, mavenSession );
            continue;
         }
         if ( field.getName().equals( "mavenProject" ) ) {
            field.setAccessible( true );
            field.set( mojo, mavenSession.getCurrentProject() );
         }
         if ( field.getName().equals( "pluginManager" ) ) {
            field.setAccessible( true );
            field.set( mojo, lookup( BuildPluginManager.class ) );
         }
      }
      return mojo;
   }

   private MavenSession getMavenSession( final File basedir, final String serverConfiguration ) {
      try {

         final MavenArtifactRepository localRepo = new MavenArtifactRepository( "local",
               localRepoDir.toUri().toString(),
               new DefaultRepositoryLayout(),
               new ArtifactRepositoryPolicy( true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS,
                     ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE ),
               new ArtifactRepositoryPolicy( true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS,
                     ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE )
         );

         final File pom = new File( basedir, "pom.xml" );
         final MavenExecutionRequest request = new DefaultMavenExecutionRequest();
         request.setLocalRepository( localRepo );
         request.setRemoteRepositories( List.of() );
         request.setBaseDirectory( basedir );
         request.setOffline( true );
         final ProjectBuildingRequest configuration = request.getProjectBuildingRequest();
         final DefaultRepositorySystemSession repositorySystemSession = new DefaultRepositorySystemSession();
         configuration.setRemoteRepositories( List.of() );
         configuration.setRepositorySession( repositorySystemSession );
         final MavenProject project = lookup( ProjectBuilder.class ).build( pom, configuration ).getProject();

         // Add the <server> entry in the Maven Settings of this execution
         if ( serverConfiguration != null ) {
            final Xpp3Dom serverConfig = Xpp3DomBuilder.build( new StringReader( serverConfiguration ) );
            final Server server = new Server();
            server.setId( GITHUB_SERVER_CONFIG_ID );
            server.setConfiguration( serverConfig );
            request.setServers( List.of( server ) );
         }

         project.setRemoteArtifactRepositories( List.of() );
         // Remove Maven central, we do _not_ want online resolution
         project.getModel().getRepositories().clear();
         project.setRemoteArtifactRepositories( List.of() );
         project.setPluginArtifactRepositories( List.of() );
         final MavenExecutionResult result = new DefaultMavenExecutionResult();
         final Field containerField = AbstractMojoTestCase.class.getDeclaredField( "container" );
         containerField.setAccessible( true );
         final PlexusContainer container = (PlexusContainer) containerField.get( this );

         @SuppressWarnings( "deprecation" ) // There is no non-deprecated constructor for MavenSession, but the class is
         // still required when executing the plugin
         final MavenSession session = new MavenSession( container, repositorySystemSession, request, result );
         session.setCurrentProject( project );
         session.setProjects( Collections.singletonList( project ) );

         return session;
      } catch ( final NoSuchFieldException | IllegalAccessException | XmlPullParserException | IOException | ComponentLookupException
                      | ProjectBuildingException exception ) {
         Assert.fail();
         throw new RuntimeException( exception );
      }
   }
}
