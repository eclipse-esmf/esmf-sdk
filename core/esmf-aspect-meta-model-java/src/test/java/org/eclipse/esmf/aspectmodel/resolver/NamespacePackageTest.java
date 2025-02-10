/*
 * Copyright (c) 2025 Robert Bosch Manufacturing Solutions GmbH
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

package org.eclipse.esmf.aspectmodel.resolver;

import static org.assertj.core.api.Assertions.as;
import static org.eclipse.esmf.test.shared.AspectModelAsserts.NAMESPACE;
import static org.eclipse.esmf.test.shared.AspectModelAsserts.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.esmf.aspectmodel.loader.AspectModelLoader;
import org.eclipse.esmf.aspectmodel.urn.AspectModelUrn;
import org.eclipse.esmf.metamodel.AspectModel;
import org.eclipse.esmf.metamodel.HasDescription;
import org.eclipse.esmf.samm.KnownVersion;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class NamespacePackageTest {
   /**
    * Returns the File object for a test model file
    */
   private Path getPackage( final String packageName ) {
      return Paths.get( String.format(
            "%s/../../core/esmf-test-aspect-models/src/main/resources/packages/%s",
            System.getProperty( "user.dir" ), packageName ) );
   }

   @Test
   void testLoadAspectModelFromZipArchiveFile() {
      final Path archivePath = getPackage( "namespaces.zip" );
      final AspectModel aspectModel = new AspectModelLoader().loadNamespacePackage( new File( archivePath.toString() ) );

      assertThat( aspectModel ).namespaces().hasSize( 2 );
      assertThat( aspectModel ).namespaces().map( HasDescription::getName ).contains( "urn:samm:org.eclipse.esmf.examples:1.1.0" );
      assertThat( aspectModel ).namespaces().map( HasDescription::getName ).contains( "urn:samm:org.eclipse.esmf.examples:1.0.0" );

      final List<String> aspectsNames = List.of( "Movement2", "Movement3", "Movement", "SimpleAspect" );

      assertThat( aspectModel ).files().hasSize( 4 );
      assertThat( aspectModel ).files().anySatisfy( aspectModelFile -> {
         Assertions.assertThat( aspectsNames ).contains( aspectModelFile.aspect().getName() );
      } );
   }

   @Test
   void testLoadAspectModelFromZipArchiveInputStream() throws FileNotFoundException {
      final Path archivePath = getPackage( "namespaces.zip" );
      final AspectModel aspectModel = new AspectModelLoader().loadNamespacePackage( new FileInputStream( archivePath.toString() ),
            archivePath.toUri() );

      assertThat( aspectModel ).namespaces().hasSize( 2 );
      assertThat( aspectModel ).namespaces().map( HasDescription::getName ).contains( "urn:samm:org.eclipse.esmf.examples:1.1.0" );
      assertThat( aspectModel ).namespaces().map( HasDescription::getName ).contains( "urn:samm:org.eclipse.esmf.examples:1.0.0" );

      final List<String> aspectsNames = List.of( "Movement2", "Movement3", "Movement", "SimpleAspect" );

      assertThat( aspectModel ).files().hasSize( 4 );
      assertThat( aspectModel ).files().anySatisfy( aspectModelFile -> {
         Assertions.assertThat( aspectsNames ).contains( aspectModelFile.aspect().getName() );
      } );
   }

   /**
    * Test migration to the latest version of Aspect Model in Archive
    */
   @Test
   void testLoadAspectModelFromZipArchive2_0_0() throws FileNotFoundException {
      final Path archivePath = getPackage( "namespaces-with-old-version.zip" );
      final AspectModel aspectModel = new AspectModelLoader().loadNamespacePackage( new FileInputStream( archivePath.toString() ),
            archivePath.toUri() );

      assertThat( aspectModel ).namespaces().hasSize( 2 );
      assertThat( aspectModel ).namespaces().map( HasDescription::getName ).contains( "urn:samm:org.eclipse.esmf.examples:1.1.0" );
      assertThat( aspectModel ).namespaces().map( HasDescription::getName ).contains( "urn:samm:org.eclipse.esmf.examples:1.0.0" );

      final List<String> aspectsNames = List.of( "Movement2", "Movement3", "Movement4", "Movement", "SimpleAspect" );

      assertThat( aspectModel ).files().hasSize( 5 );
      assertThat( aspectModel ).files().anySatisfy( aspectModelFile -> {
         Assertions.assertThat( aspectsNames ).contains( aspectModelFile.aspect().getName() );
      } );
   }

   @Test
   void testLoadAspectModelFromZipArchiveAspectModelsRoot() throws FileNotFoundException {
      final Path archivePath = getPackage( "namespaces-aspect-models-root.zip" );
      final AspectModel aspectModel = new AspectModelLoader().loadNamespacePackage( new FileInputStream( archivePath.toString() ),
            archivePath.toUri() );

      assertThat( aspectModel ).namespaces().hasSize( 2 );
      assertThat( aspectModel ).namespaces().map( HasDescription::getName ).contains( "urn:samm:org.eclipse.esmf.examples:1.1.0" );
      assertThat( aspectModel ).namespaces().map( HasDescription::getName ).contains( "urn:samm:org.eclipse.esmf.examples:1.0.0" );

      final List<String> aspectsNames = List.of( "Movement2", "Movement3", "Movement", "SimpleAspect" );

      assertThat( aspectModel ).files().hasSize( 4 );
      assertThat( aspectModel ).files().anySatisfy( aspectModelFile -> {
         Assertions.assertThat( aspectsNames ).contains( aspectModelFile.aspect().getName() );
      } );
   }

   @Test
   void testLoadAspectModelFromZipArchiveAspectModelsSubfolder() throws FileNotFoundException {
      final Path archivePath = getPackage( "namespaces-aspect-models-subfolder.zip" );
      final AspectModel aspectModel = new AspectModelLoader().loadNamespacePackage( new FileInputStream( archivePath.toString() ),
            archivePath.toUri() );

      assertThat( aspectModel ).namespaces().hasSize( 2 );
      assertThat( aspectModel ).namespaces().map( HasDescription::getName ).contains( "urn:samm:org.eclipse.esmf.examples:1.1.0" );
      assertThat( aspectModel ).namespaces().map( HasDescription::getName ).contains( "urn:samm:org.eclipse.esmf.examples:1.0.0" );

      final List<String> aspectsNames = List.of( "Movement2", "Movement3", "Movement", "SimpleAspect" );

      assertThat( aspectModel ).files().hasSize( 4 );
      assertThat( aspectModel ).files().anySatisfy( aspectModelFile -> {
         Assertions.assertThat( aspectsNames ).contains( aspectModelFile.aspect().getName() );
      } );
   }

   @Test
   void testLoadAspectModelFromZipArchiveWithSharedProperty() throws FileNotFoundException, URISyntaxException {
      final Path archivePath = getPackage( "namespace-with-shared-property.zip" );

      final File aspectModelsRootDirectory = new File(
            NamespacePackageTest.class.getClassLoader()
                  .getResource( KnownVersion.getLatest().toString().toLowerCase() )
                  .toURI().getPath() );

      final ResolutionStrategy urnStrategy = new FileSystemStrategy( aspectModelsRootDirectory.toPath() );

      final AspectModel aspectModel = new AspectModelLoader( urnStrategy ).loadNamespacePackage(
            new FileInputStream( archivePath.toString() ), archivePath.toUri() );

      assertThat( aspectModel ).namespaces().hasSize( 1 );
      assertThat( aspectModel ).namespaces().first( as( NAMESPACE ) ).hasName( "urn:samm:org.eclipse.esmf.test:1.0.0" );

      final List<String> aspectsNames = List.of( "Movement" );

      assertThat( aspectModel ).files().hasSize( 2 );
      assertThat( aspectModel ).files().anySatisfy( aspectModelFile -> {
         if ( !aspectModelFile.aspects().isEmpty() ) {
            Assertions.assertThat( aspectsNames ).contains( aspectModelFile.aspect().getName() );
         }
      } );
   }

   @Test
   void testSerializeNamespacePackage() throws IOException {
      final Consumer<AspectModel> assertions = aspectModel -> {
         assertThat( aspectModel ).namespaces().hasSize( 2 );
         assertThat( aspectModel ).namespaces().map( HasDescription::getName ).contains( "urn:samm:org.eclipse.esmf.examples:1.1.0" );
         assertThat( aspectModel ).namespaces().map( HasDescription::getName ).contains( "urn:samm:org.eclipse.esmf.examples:1.0.0" );

         final List<String> aspectsNames = List.of( "Movement2", "Movement3", "Movement", "SimpleAspect" );

         assertThat( aspectModel ).files().hasSize( 4 );
         assertThat( aspectModel ).files().anySatisfy( aspectModelFile -> {
            Assertions.assertThat( aspectsNames ).contains( aspectModelFile.aspect().getName() );
         } );
      };

      // Load the file
      final Path archivePath = getPackage( "namespaces.zip" );
      final AspectModel originalModel = new AspectModelLoader().loadNamespacePackage( new File( archivePath.toString() ) );

      // assert facts about model
      assertions.accept( originalModel );

      // Serialize the model into a .zip
      final NamespacePackage namespacePackage = new NamespacePackage( originalModel );
      final byte[] namespacePackageZipContent = namespacePackage.serialize();

      // Now load it again from the zip and assert the same facts
      try ( final ByteArrayInputStream input = new ByteArrayInputStream( namespacePackageZipContent ) ) {
         final AspectModel modelFromZip = new AspectModelLoader().loadNamespacePackage( input, URI.create( "testmodel:inmemory" ) );
         assertions.accept( modelFromZip );
      }
   }

   @Test
   void testNamespacePackageAsResolutionStrategy() throws IOException {
      final File archivePath = getPackage( "namespaces.zip" ).toFile();
      final FileInputStream input = new FileInputStream( archivePath );
      final byte[] content = IOUtils.toByteArray( input );
      final NamespacePackage namespacePackage = new NamespacePackage( content, archivePath.toURI() );

      // By using the NamespacePackage as an AspectModelLoader constructor argument, it is used as a ResolutionStrategy
      final AspectModelLoader aspectModelLoader = new AspectModelLoader( namespacePackage );
      final AspectModel aspectModel = aspectModelLoader.load(
            AspectModelUrn.fromUrn( "urn:samm:org.eclipse.esmf.examples:1.0.0#Movement" ) );
      assertThat( aspectModel ).hasSingleAspectThat().hasName( "Movement" );
   }
}
