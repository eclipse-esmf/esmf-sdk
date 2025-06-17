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

package org.eclipse.esmf.aspectmodel.resolver.github;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.esmf.test.shared.AspectModelAsserts.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.esmf.aspectmodel.AspectModelFile;
import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.resolver.GithubRepository;
import org.eclipse.esmf.aspectmodel.resolver.ResolutionStrategy;
import org.eclipse.esmf.aspectmodel.shacl.violation.SparqlConstraintViolation;
import org.eclipse.esmf.aspectmodel.shacl.violation.Violation;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.aspectmodel.validation.services.AspectModelValidator;
import org.eclipse.esmf.metamodel.AspectModel;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GitHubStrategyTest {
   Path outputDirectory = null;
   private final GithubRepository esmfSdk = new GithubRepository( "eclipse-esmf", "esmf-sdk", new GithubRepository.Branch( "main" ) );

   @BeforeEach
   void beforeEach() throws IOException {
      outputDirectory = Files.createTempDirectory( "junit" );
   }

   @AfterEach
   void afterEach() {
      if ( outputDirectory != null ) {
         final File outputDir = outputDirectory.toFile();
         if ( outputDir.exists() && outputDir.isDirectory() ) {
            // Recursively delete temporary directory
            try ( final Stream<Path> paths = Files.walk( outputDirectory ) ) {
               paths.sorted( Comparator.reverseOrder() )
                     .map( Path::toFile )
                     .forEach( file -> {
                        if ( !file.delete() ) {
                           System.err.println( "Could not delete file " + file );
                        }
                     } );
            } catch ( final IOException exception ) {
               throw new RuntimeException( exception );
            }
         }
      }
   }

   @Test
   void testParseGitHubZipExport() throws IOException {
      final GitHubModelSource modelSource = new GitHubModelSource( esmfSdk, "core/esmf-test-aspect-models/src/main/resources/valid/" );

      final File tempFile = outputDirectory.resolve( "temp.zip" ).toFile();
      final InputStream testZipFileInputStream = getClass().getClassLoader().getResourceAsStream( "github-export.zip" );
      final OutputStream tempFileOutputStream = new FileOutputStream( tempFile );
      assertThat( testZipFileInputStream ).isNotNull();
      IOUtils.copy( testZipFileInputStream, tempFileOutputStream );
      inject( modelSource, tempFile );

      final AspectModel aspectModel = new AspectModelLoader().loadAspectModelFiles( modelSource.loadContents().toList() );
      assertThat( aspectModel ).files().hasSize( 1 );
      assertThat( aspectModel ).hasSingleAspectThat().hasName( "Aspect" );
   }

   @Test
   void testDownloadAndLoadZip() {
      final GitHubModelSource modelSource = new GitHubModelSource( esmfSdk, "core/esmf-test-aspect-models/src/main/resources/valid/" );
      final List<AspectModelFile> files = modelSource.loadContents().toList();
      assertThat( files ).isNotEmpty();
      assertThat( files ).allMatch( file -> file.sourceLocation().isPresent()
            && file.sourceLocation().get().toString().startsWith( "https://github.com" ) );

      final AspectModelFile aspectModelFile = files.stream()
            .filter( file -> file.sourceLocation().map( URI::toString ).get().endsWith( "/Aspect.ttl" ) ).findFirst().get();
      final AspectModel aspectModel = new AspectModelLoader().loadAspectModelFiles( List.of( aspectModelFile ) );
      assertThat( aspectModel ).files().hasSize( 1 );
      assertThat( aspectModel ).hasSingleAspectThat().hasName( "Aspect" );
   }

   @Test
   void testResolveFromZipFile() throws IOException {
      final ResolutionStrategy gitHubStrategy = new GitHubStrategy( esmfSdk, "core/esmf-test-aspect-models/src/main/resources/valid" );

      final File tempFile = outputDirectory.resolve( "temp.zip" ).toFile();
      final InputStream testZipFileInputStream = getClass().getClassLoader().getResourceAsStream( "github-export.zip" );
      final OutputStream tempFileOutputStream = new FileOutputStream( tempFile );
      assertThat( testZipFileInputStream ).isNotNull();
      IOUtils.copy( testZipFileInputStream, tempFileOutputStream );
      inject( (GitHubModelSource) gitHubStrategy, tempFile );

      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.test:1.0.0#Aspect" );
      final AspectModel result = new AspectModelLoader( gitHubStrategy ).load( testUrn );
      assertThat( result ).files().hasSize( 1 );
      assertThat( result ).elements().hasSize( 1 );
      assertThat( result ).hasSingleAspectThat().hasName( "Aspect" );
      assertThat( result ).hasSingleAspectThat().hasUrn( testUrn );
   }

   @Test
   void testGithubStrategy() {
      final AspectModelUrn testUrn = AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.test:1.0.0#Aspect" );
      final ResolutionStrategy gitHubStrategy = new GitHubStrategy( esmfSdk,
            "core/esmf-test-aspect-models/src/main/resources/valid" );
      final AspectModel result = new AspectModelLoader( gitHubStrategy ).load( testUrn );
      assertThat( result ).files().hasSize( 1 );
      assertThat( result ).elements().hasSize( 1 );
      assertThat( result ).hasSingleAspectThat().hasName( "Aspect" );
      assertThat( result ).hasSingleAspectThat().hasUrn( testUrn );
   }

   private void inject( final GitHubModelSource gitHubModelSource, final File tempFile ) {
      gitHubModelSource.repositoryZipFile = tempFile;
      gitHubModelSource.loadFilesFromZip();
   }

   @Test
   void testLoadCatenaxBatteryPass() {
      final AspectModelUrn batteryPassUrn = AspectModelUrn.fromUrn( "urn:samm:io.catenax.battery.battery_pass:6.0.0#BatteryPass" );
      final GithubRepository sldt = new GithubRepository( "eclipse-tractusx", "sldt-semantic-models",
            new GithubRepository.Branch( "main" ) );
      final ResolutionStrategy gitHubStrategy = new GitHubStrategy( sldt, "/" );
      final AspectModel batteryPass = new AspectModelLoader( gitHubStrategy ).load( batteryPassUrn );

      final AspectModelValidator validator = new AspectModelValidator();
      final List<Violation> violations = validator.validateModel( batteryPass );
      assertThat( violations ).hasOnlyElementsOfType( SparqlConstraintViolation.class );
   }
}
